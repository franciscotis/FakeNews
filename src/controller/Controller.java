package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import model.Consenso;
import model.Noticia;
import util.BaseDeDados;
import util.Configuracao;

public class Controller 
{
	private ExecutorService executor;
	private Configuracao configuracao;
	private BaseDeDados baseDados;
	private Semaphore semaforoNoticias;
	private int timeout;

	public Controller(String pathDados)
	{
		executor = Executors.newCachedThreadPool();
		configuracao = new Configuracao();
		baseDados = new BaseDeDados(pathDados);
		timeout = Integer.parseInt(configuracao.getConfiguracao("timeout"));
		semaforoNoticias = new Semaphore(0);
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
		executor.execute(() -> consenso.iniciarConsenso()); // Inicia um thread para iniciar o processo de consenso
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
