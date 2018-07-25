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


package model;



import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class Consenso  //Classe do consenso
{
	//Declaração de variáveis
	private Noticia noticia;
	private List<Servidor> servidores;
	private Evento eventoPosConsenso;
	private Semaphore semaforoServidores;

	//Construtor
	public Consenso(Noticia noticia, List<Servidor> servidores)
	{
		this.noticia = noticia;
		this.servidores = servidores;
		semaforoServidores = new Semaphore(0);
	}

	public void setEventoPosConsenso(Evento evento) //Metodo que define o evento após o consenso
	{
		this.eventoPosConsenso = evento;
	}

	public void iniciarConsenso() throws IOException, NotBoundException, InterruptedException  //Método que inicia o consenso
	{
		System.out.println("Iniciou processo de consenso");
		
		ExecutorService executor = Executors.newCachedThreadPool();
		List<ISiteNoticia> outrosSites = new ArrayList<>();
		Semaphore semaforoSites = new Semaphore(0);

		semaforoSites.release(); // Libera a primeira permissao para adicao de elementos na lista de sites
		
		for(Servidor servidor : servidores) // Solicita conexao com os outros servidores
			executor.execute(() -> addSiteNoticia(outrosSites, servidor, semaforoSites));

		semaforoServidores.acquire(servidores.size()); // So executa a segunda parte do código após todos os servidores terem respondido
		
		if(outrosSites.size() > 2)//Caso tenha mais de 2 servidores, incluindo o servidor que iniciou o processo, disponíveis
		{
			ISiteNoticia siteLider = elegerLider(outrosSites);  //Define um lider para o consenso (o servidor que irá realizar o consenso)
			executarAlgoritmo(siteLider, outrosSites); //Executa o algoritmo
			System.out.println("Processo de consenso finalizado com sucesso");
		}
		else
		{
			System.out.println("N�o foram obtidas informa��es suficientes para executar o algoritmo");
		}
	}
	
	private ISiteNoticia elegerLider(List<ISiteNoticia> sites) //Método que irá eleger um lider a partir da lista de servidores
	{
		Random random = new Random();
		int indiceServidorSorteado = random.nextInt(sites.size()); //Sorteia um servidor a partir da lista de servidores
		return sites.get(indiceServidorSorteado); //Retorna o lider
	}
	
	private void executarAlgoritmo(ISiteNoticia siteLider, List<ISiteNoticia> sites) throws RemoteException
	{ //Executa o algoritmo
		siteLider.consenso(noticia.getId(), sites); // Via RMI é acessado o método do servidor, o qual executa o consenso
		eventoPosConsenso.disparar();
	}
	
	private void addSiteNoticia(List<ISiteNoticia> sites, Servidor servidor, Semaphore semaforoSites) //Método que irá adicionar os servidores na lista
	{
		try
		{
			ISiteNoticia site = (ISiteNoticia) Naming.lookup(servidor.getUrlRmi() + "/SiteNoticia"); //Conecta-se com o servidor via RMI
			semaforoSites.acquire();
			sites.add(site); //Adiciona na lista
			semaforoSites.release();
		}
		catch(Exception e)
		{
			System.out.println("Não foi possível se conectar com o servidor: " + servidor.getUrlRmi());
		}
		semaforoServidores.release(1);
	}

}
