package model;

public class Noticia 
{
	private int id;
	private String titulo;
	private int numeroAvaliacoes;
	private double mediaAvaliacoes;
	private boolean fake;
	
	
	public Noticia()
	{
	}
	
	public boolean isFake()
	{
		double MAIORIA = (double) 5 * 2/3;

		fake = true;
		
		if(mediaAvaliacoes < MAIORIA)
			return true;
		
		fake = false;
		
		return false;
	}

	public int getId() 
	{
		return id;
	}

	public String getTitulo() 
	{
		return titulo;
	}

	public int getNumeroAvaliacoes()
	{
		return numeroAvaliacoes;
	}
	
	public double getMediaAvaliacoes()
	{
		return mediaAvaliacoes;
	}
	
	public void addAvaliacao(int nota)
	{
		mediaAvaliacoes = (double) mediaAvaliacoes * numeroAvaliacoes;
		numeroAvaliacoes++;
		mediaAvaliacoes = (double) mediaAvaliacoes + nota;
		mediaAvaliacoes = (double) mediaAvaliacoes / numeroAvaliacoes;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public void setTitulo(String titulo)
	{
		this.titulo = titulo;
	}

	public void setNumeroAvaliacoes(int numeroAvaliacoes)
	{
		this.numeroAvaliacoes = numeroAvaliacoes;
	}
	
	public void setMediaAvaliacoes(double mediaAvaliacoes)
	{
		this.mediaAvaliacoes = mediaAvaliacoes;
	}
	
	public void setFake(boolean fake)
	{
		this.fake = fake;
	}
	
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append(id);
		builder.append(';');
		builder.append(titulo);
		builder.append(';');
		builder.append(numeroAvaliacoes);
		builder.append(';');
		builder.append(mediaAvaliacoes);
		builder.append(';');
		builder.append(fake);
		builder.append('\n');
		
		return builder.toString();
	}
}
