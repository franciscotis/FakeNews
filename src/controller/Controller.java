/*
Autores: Francisco Tito Silva Santos Pereira - 16111203 e Matheus Sobral Oliveira - 16111189
Componente Curricular: MI - Conectividade e Concorrência
Concluido em: 24/07/2018
Declaramos que este código foi elaborado por nós de forma "individual" e não contém nenhum
trecho de código de outro colega ou de outro autor, tais como provindos de livros e
apostilas, e páginas ou documentos eletrônicos da Internet. Qualquer trecho de código
de outra autoria que não a nossa está destacado com uma citação para o autor e a fonte
do código, e estamos ciente que estes trechos não serão considerados para fins de avaliação.
 */

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
import util.EmailSender;

public class Controller extends UnicastRemoteObject implements ISiteNoticia
{
	//Declaração de variáveis
	private static final long serialVersionUID = -6386698164507342395L; //versão do serial
	private static double MAIORIA = 3.33; // A maioria
	private ExecutorService executor;
	private Configuracao configuracao;
	private BaseDeDados baseDados;
	private Semaphore semaforoNoticias;
	private int timeout;
	private int porta;
	private String destinatario;


	public Controller(String pathDados) throws AlreadyBoundException, IOException, InterruptedException {
		executor = Executors.newCachedThreadPool(); //Criação de um objeto thread
		configuracao = new Configuracao(); //objeto configuração
		baseDados = new BaseDeDados(pathDados); //Base de dados é onde os dados estão armazenados
		timeout = Integer.parseInt(configuracao.getConfiguracao("timeout")); // Pega o timeout a partir do arquivo
		this.destinatario = String.valueOf(configuracao.getConfiguracao("entidade")); //Pega o destinatário a partir do arquivo
		semaforoNoticias = new Semaphore(0); //Objeto semáforo
		this.alteraTimeOut(); // Altera o timeout
		this.serverRMI(); //Servidor RMI
	}


	private void serverRMI() throws IOException, AlreadyBoundException, InterruptedException // Inicia o servidor RMI
	{
		this.porta = this.configuracao.outrosServidores().get(0).getPorta(); // Pega a porta rmi a partir do banco de dados
		Registry registry = LocateRegistry.createRegistry(this.porta); //Cria um novo registro
		registry.bind("SiteNoticia",this); //"DNS" do RMI
	}

	private void alteraTimeOut() throws IOException  //Método que irá alterar o timeout do rmi para simular um sistema síncrono.
	{
		RMISocketFactory.setSocketFactory(new RMISocketFactory() {
			public Socket createSocket(String host, int port)
					throws IOException, SocketException {
				Socket socket = new Socket();
				socket.setSoTimeout(timeout);
				socket.setSoLinger(false, 0);
				socket.connect(new InetSocketAddress(host, port), timeout); //Muda o timeout da conexão
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
		HashMap<Integer, Noticia> noticias = this.baseDados.getNoticias(); //Estrutura de dados que armazena as noticias
		Noticia n = (Noticia) noticias.get(idNoticia);
		return n.getMediaAvaliacoes(); //Retorna a avaliação local
	}

	@Override
	public void definirAvaliacao(int idNoticia, double mediaFinal) throws RemoteException // Metodo que ira dizer qual o resultado final de uma noticia
	{ 
		HashMap<Integer, Noticia> noticias = this.baseDados.getNoticias(); //Estrutura de dados que armazena as noticias
		Noticia n = (Noticia) noticias.get(idNoticia);
		n.setMediaAvaliacoes(mediaFinal); //modifica a média local pela media final definida pelo consenso
		atualizarInformacoes(); //Atualiza as informações
	}


	@Override
	public void consenso(int idNoticia, List<ISiteNoticia> servidores) throws RemoteException //Método que realiza o consenso
	{
		//Variáveis
		double somatorioMedias = 0;
		double avaliacaoMedia = 0;
		boolean fake= false;

		for (ISiteNoticia site : servidores) //Para cada servidor, ele irá pegar a média local
			somatorioMedias += site.getMediaAvalicao(idNoticia);

		avaliacaoMedia = (double) somatorioMedias / servidores.size(); // Calcula a média das avaliações

		if(avaliacaoMedia >= MAIORIA) //Caso a média das avaliações supere 3,33 a notícia é considerada verdadeira.
			// Pelo algoritmo é definido que caso 2/3 das avaliações sejam positivas, a noticia é considerada verdadeira
			for(ISiteNoticia site : servidores)
				site.definirAvaliacao(idNoticia, 5); // Foi decido que a notícia é verdadeira, e substitui a média da avaliação para 5
		else { //Caso contrário
			for (ISiteNoticia site : servidores) {
				site.definirAvaliacao(idNoticia, 1); //Define que a noticia é falsa, e substitui a média da avaliação para 1
				fake = true;

			}

		}
		if(fake && !baseDados.getNoticias().get(idNoticia).reportado()) { //Caso a noticia seja falsa e ainda não tenha sido reportada
			System.out.println("A noticia é fake, reportando as autoridades agora...");
			//Envia um email para as autoridades
			EmailSender.send("Foi Reportada uma noticia FAKE", destinatario, "A noticia " + baseDados.getNoticias().get(idNoticia).getTitulo() + "foi considerada fake");
			for(ISiteNoticia site : servidores){
				site.emailEnviado(idNoticia); //Diz para os outros servidores que aquela noticia foi reportada
			}
		}
	}

	@Override
	public void emailEnviado(int idNoticia) throws RemoteException {
		baseDados.getNoticias().get(idNoticia).reportar(); //Verifica se uma noticia foi enviada
	}



	public Noticia[] listarNoticias() //Método que lista todas as noticias do banco de dados na tela
	{
		Noticia[] noticias = new Noticia[baseDados.getNoticias().size()];

		int i  = 0;

		for(Noticia n : baseDados.getNoticias().values())
			noticias[i++] = n;

		return noticias;
	}

	public void addAvaliacao(Noticia noticia, int avaliacao) //Método que adiciona uma avaliação para uma determinada noticia
	{
		noticia.addAvaliacao(avaliacao); //Adiciona uma avaliação
		atualizarInformacoes(); //Atualiza as informações

		if(noticia.oldIsFake() != noticia.isFake()) // So executa o processo de consenso se a avaliacao sobre a noticia mudou
			iniciarConsenso(noticia); //Inicia o processo de consenso
	}

	private void iniciarConsenso(Noticia noticia) // Método que realiza o processo de consenso
	{
		Consenso consenso = new Consenso(noticia, configuracao.outrosServidores()); //Cria um objeto de consenso que contem a noticia e os servidores envolvidos
		consenso.setEventoPosConsenso(() -> atualizarInformacoes()); //Seta o evento após o consenso
		executor.execute(() -> { // Inicia uma nova thread para aquele consenso
			try 
			{
				consenso.iniciarConsenso(); //Inicia o consenso
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		});
	}

	private void atualizarInformacoes() //Atualiza as informações
	{
		baseDados.persistirDados(); //Salva os dados
		semaforoNoticias.release();
	}

	public List<Noticia> getListaNoticias() //Método que retorna todas as noticias
	{
		ArrayList<Noticia> noticias = new ArrayList<>();

		for(Noticia n : baseDados.getNoticias().values())
			noticias.add(n);

		return noticias;
	}

	public void aguardarAtualizacao() //Método que informa o usuário que está aguardando atualizações
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
