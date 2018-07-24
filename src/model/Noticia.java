package model;

public class Noticia 
{
	private int id;
	private String titulo;
	private int numeroAvaliacoes;
	private double mediaAvaliacoes;
	private boolean fake;
	private double MAIORIA;
	private boolean reportado;
	
	
	public Noticia()
	{
		MAIORIA = 3.34;
		this.reportado = false;
	}
	
	public boolean oldIsFake()
	{
		return fake;
	}

	public void reportar(){
		this.reportado = true;
	}

	public boolean reportado(){
		return this.reportado;
	}

	public boolean isFake()
	{	
		if(mediaAvaliacoes >= MAIORIA)
			return false;
		
		return true;
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
		setFake(isFake());
		System.out.println("\nSetando valor. Media" + mediaAvaliacoes +" IsFake: " + isFake());
	}
	
	public void setFake(boolean fake)
	{
		this.fake = fake;
	}
	
	public String formatarParaBd()
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
	
	public String toString()
	{
		return id + " - " + titulo;
	}
}
