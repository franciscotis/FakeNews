package controller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import model.Consenso;
import model.ISiteNoticia;
import model.Noticia;
import util.BaseDeDados;
import util.Configuracao;

public class Controller extends UnicastRemoteObject implements ISiteNoticia
{
	private ExecutorService executor;
	private Configuracao configuracao;
	private BaseDeDados baseDados;
	private Semaphore semaforoNoticias;
	private int timeout;
	private int porta;


	public Controller(String pathDados) throws AlreadyBoundException, IOException, InterruptedException {
		executor = Executors.newCachedThreadPool();
		configuracao = new Configuracao();
		baseDados = new BaseDeDados(pathDados);
		timeout = Integer.parseInt(configuracao.getConfiguracao("timeout"));
		semaforoNoticias = new Semaphore(0);
		this.alteraTimeOut();
		this.serverRMI();

	}

	private void serverRMI() throws IOException, AlreadyBoundException, InterruptedException { // Método que irá iniciar a conexão rmi
		this.porta = this.configuracao.outrosServidores().get(0).getPorta();
		Registry registry = LocateRegistry.createRegistry(this.porta);
		registry.bind("SiteNoticia",this);
    }

    private void alteraTimeOut() throws IOException {
        RMISocketFactory.setSocketFactory(new RMISocketFactory() {
            public Socket createSocket(String host, int port)
                    throws IOException, SocketException {
                Socket socket = new Socket();
                socket.setSoTimeout(5000);
                socket.setSoLinger(false, 0);
                socket.connect(new InetSocketAddress(host, port), 5000);
                return socket;
            }

            public ServerSocket createServerSocket(int port)
                    throws IOException {
                return new ServerSocket(port);
            }

        });
    }

	@Override
	public boolean getParcial(int idNoticia) throws RemoteException { // Método que irá retornar o resultado parcial de uma noticia
        Map noticias = this.baseDados.getNoticias();
		Noticia n = (Noticia) noticias.get(idNoticia);
		return n.isFake();
	}

	@Override
	public void noticiaFinal(int idNoticia, boolean resultadoFinal) throws RemoteException { // Método que irá dizer qual o resultado final de uma noticia
		Map noticias = this.baseDados.getNoticias();
		Noticia n = (Noticia) noticias.get(idNoticia);
		n.setFake(resultadoFinal);
	}


    @Override
    public void consenso(int idNoticia, List<ISiteNoticia> servidores) throws RemoteException {
	    double verdade = 0 , falso = 0 ;
	    for (ISiteNoticia site : servidores){
	        if(site.getParcial(idNoticia)){
	            verdade+=1;
            }
            else{
	            falso+=1;
            }
        }

        if(verdade>2/3){
	        for(ISiteNoticia site : servidores){
	            site.noticiaFinal(idNoticia,true);
            }
        }
        else
            for(ISiteNoticia site : servidores){
                site.noticiaFinal(idNoticia,false);
            }
    }


    public Noticia[] listarNoticias()
	{
		Noticia[] noticias = new Noticia[baseDados.getNoticias().size()];

		int i  = 0;

		for(Noticia n : baseDados.getNoticias().values())
			noticias[i++] = n;

		return noticias;
	}

	public void addAvaliacao(Noticia noticia, int avaliacao)
	{
		noticia.addAvaliacao(avaliacao);
		atualizarInformacoes();
		
		if(noticia.oldIsFake() != noticia.isFake()) // S� executa o processo de consenso se a avalia��o sobre a not�cia mudou
			iniciarConsenso(noticia);
	}

	private void iniciarConsenso(Noticia noticia)
	{

		Consenso consenso = new Consenso(noticia, configuracao.outrosServidores(), timeout);
		consenso.setEventoPosConsenso(() -> atualizarInformacoes());
		executor.execute(() -> {
			try {
				consenso.iniciarConsenso();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NotBoundException e) {
                e.printStackTrace();
            }
        }); // Inicia um thread para iniciar o processo de consenso
	}

	private void atualizarInformacoes()
	{
		baseDados.persistirDados();
		semaforoNoticias.release();
	}

	public List<Noticia> getListaNoticias()
	{
		ArrayList<Noticia> noticias = new ArrayList<>();

		for(Noticia n : baseDados.getNoticias().values())
			noticias.add(n);

		return noticias;
	}

	public void aguardarAtualizacao()
	{
		try
		{
			System.out.println("Aguardanto atualiza��o");
			semaforoNoticias.acquire();
			System.out.println("As informa��es sobre a noticia foram atualizadas");
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}


}
