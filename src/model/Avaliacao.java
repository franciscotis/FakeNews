package model;

public class Avaliacao 
{
	private Noticia noticia;
	private int nota;
	
	public Avaliacao()
	{
		
	}

	public Avaliacao(Noticia noticia, int nota) 
	{
		setNoticia(noticia);
		setNota(nota);
	}

	public Noticia getNoticia() 
	{
		return noticia;
	}

	public int getNota()
	{
		return nota;
	}

	public void setNoticia(Noticia noticia)
	{
		this.noticia = noticia;
	}

	public void setNota(int nota)
	{
		this.nota = nota;
	}
	
	
}
