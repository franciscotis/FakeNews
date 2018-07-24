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

public class Consenso 
{
	private Noticia noticia;
	private List<Servidor> servidores;
	private Evento eventoPosConsenso;
	private Semaphore semaforoServidores;

	public Consenso(Noticia noticia, List<Servidor> servidores)
	{
		this.noticia = noticia;
		this.servidores = servidores;
		semaforoServidores = new Semaphore(0);
	}

	public void setEventoPosConsenso(Evento evento)
	{
		this.eventoPosConsenso = evento;
	}

	public void iniciarConsenso() throws IOException, NotBoundException, InterruptedException 
	{
		System.out.println("Iniciou processo de consenso");
		
		ExecutorService executor = Executors.newCachedThreadPool();
		List<ISiteNoticia> outrosSites = new ArrayList<>();
		Semaphore semaforoSites = new Semaphore(0);

		semaforoSites.release(); // Libera a primeira permissao para adicao de elementos na lista de sites
		
		for(Servidor servidor : servidores) // Solicita conexao com os outros servidores
			executor.execute(() -> addSiteNoticia(outrosSites, servidor, semaforoSites));

		semaforoServidores.acquire(servidores.size()); // So executa a segunda parte do código após todos os servidores terem respondido
		
		if(outrosSites.size() > 2)
		{
			ISiteNoticia siteLider = elegerLider(outrosSites);
			executarAlgoritmo(siteLider, outrosSites);
			System.out.println("Processo de consenso finalizado com sucesso");
		}
		else
		{
			System.out.println("Não foram obtidas informações suficientes para executar o algoritmo");
		}
	}
	
	private ISiteNoticia elegerLider(List<ISiteNoticia> sites)
	{
		Random random = new Random();
		int indiceServidorSorteado = random.nextInt(sites.size());
		return sites.get(indiceServidorSorteado);
	}
	
	private void executarAlgoritmo(ISiteNoticia siteLider, List<ISiteNoticia> sites) throws RemoteException
	{
		siteLider.consenso(noticia.getId(), sites);
		eventoPosConsenso.disparar();
	}
	
	private void addSiteNoticia(List<ISiteNoticia> sites, Servidor servidor, Semaphore semaforoSites)
	{
		try
		{
			ISiteNoticia site = (ISiteNoticia) Naming.lookup(servidor.getUrlRmi() + "/SiteNoticia");
			semaforoSites.acquire();
			sites.add(site);
			semaforoSites.release();
		}
		catch(Exception e)
		{
			System.out.println("Não foi possível se conectar com o servidor: " + servidor.getUrlRmi());
		}
		semaforoServidores.release(1);
	}

}
