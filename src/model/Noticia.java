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
		MAIORIA = 3.34; //Para uma noticia ser verdadeira, a média de todas as noticias dos outros servidores tem que ser maior que 3.34
		this.reportado = false; // A noticia não foi reportada
	}
	
	public boolean oldIsFake()
	{
		return fake;
	} //Retorna se a noticia antes do consenso é fake

	public void reportar(){
		this.reportado = true;
	} // Faz que a noticia seja reportada

	public boolean reportado(){
		return this.reportado;
	} //Retorna se a noticia foi reportada

	public boolean isFake() //Verifica se a noticia é fake a partir das medias da avaliação e a maioria
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
	
	public void addAvaliacao(int nota) //Método que adiciona uma avaliação à nota
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
	
	public void setMediaAvaliacoes(double mediaAvaliacoes) //Método que modifica a média das avaliações
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
