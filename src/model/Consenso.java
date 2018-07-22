package model;

import java.util.List;

public class Consenso 
{
	private Noticia noticia;
	private List<Servidor> servidores;
	private int timeout;
	private Evento eventoPosConsenso;

	public Consenso(Noticia noticia, List<Servidor> servidores, int timeout)
	{
		this.noticia = noticia;
		this.servidores = servidores;
		this.timeout = timeout;
	}

	public void setEventoPosConsenso(Evento evento)
	{
		this.eventoPosConsenso = evento;
	}

	public void iniciarConsenso()
	{
		System.out.println("Iniciou processo de consenso");

		//TODO Após o consenso setar se a noticia foi considerada fake 
		//TODO noticia.setFake(resultadoConsenso);
		noticia.setFake(noticia.isFake()); //Retirar essa avaliação


		eventoPosConsenso.disparar();
	}

}
