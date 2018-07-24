package controller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import model.Consenso;
import model.ISiteNoticia;
import model.Noticia;
import util.BaseDeDados;
import util.Configuracao;

public class Controller extends UnicastRemoteObject implements ISiteNoticia
{
	private static final long serialVersionUID = -6386698164507342395L;
	private static double MAIORIA = 3.34;
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

	private void serverRMI() throws IOException, AlreadyBoundException, InterruptedException // Inicia o serviço RMI
	{
		this.porta = this.configuracao.outrosServidores().get(0).getPorta();
		Registry registry = LocateRegistry.createRegistry(this.porta);
		registry.bind("SiteNoticia",this);
	}

	private void alteraTimeOut() throws IOException 
	{
		RMISocketFactory.setSocketFactory(new RMISocketFactory() {
			public Socket createSocket(String host, int port)
					throws IOException, SocketException {
				Socket socket = new Socket();
				socket.setSoTimeout(timeout);
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
	public double getMediaAvalicao(int idNoticia) throws RemoteException // Metodo que ira retornar o a avaliacao local de cada servidor sobre uma noticia
	{
		HashMap<Integer, Noticia> noticias = this.baseDados.getNoticias();
		Noticia n = (Noticia) noticias.get(idNoticia);
		return n.getMediaAvaliacoes();
	}

	@Override
	public void definirAvaliacao(int idNoticia, double mediaFinal) throws RemoteException // Metodo que ira dizer qual o resultado final de uma noticia
	{ 
		HashMap<Integer, Noticia> noticias = this.baseDados.getNoticias();
		Noticia n = (Noticia) noticias.get(idNoticia);
		n.setMediaAvaliacoes(mediaFinal);
		atualizarInformacoes();
	}


	@Override
	public void consenso(int idNoticia, List<ISiteNoticia> servidores) throws RemoteException
	{
		double somatorioMedias = 0;
		double avaliacaoMedia = 0;

		for (ISiteNoticia site : servidores)
			somatorioMedias += site.getMediaAvalicao(idNoticia);

		avaliacaoMedia = (double) somatorioMedias / servidores.size();

		if(avaliacaoMedia >= MAIORIA)
			for(ISiteNoticia site : servidores)
				site.definirAvaliacao(idNoticia, MAIORIA); // Foi decido que a notícia é verdadeira
		else
			for(ISiteNoticia site : servidores)
				site.definirAvaliacao(idNoticia, 5 - MAIORIA);
		
		System.out.println("A noticia " + baseDados.getNoticias().get(idNoticia).getTitulo() + " foi considerada " + baseDados.getNoticias().get(idNoticia).oldIsFake());
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

		if(noticia.oldIsFake() != noticia.isFake()) // So executa o processo de consenso se a avaliacao sobre a noticia mudou
			iniciarConsenso(noticia);
	}

	private void iniciarConsenso(Noticia noticia)
	{
		Consenso consenso = new Consenso(noticia, configuracao.outrosServidores());
		consenso.setEventoPosConsenso(() -> atualizarInformacoes());
		executor.execute(() -> {
			try 
			{
				consenso.iniciarConsenso();
			}
			catch (Exception e)
			{
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
			System.out.println("Aguardanto atualizacao");
			semaforoNoticias.acquire();
			System.out.println("As informacoes sobre a noticia foram atualizadas");
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}
}
