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

public class Servidor
{
	private int porta;
	private String ip;
	
	public Servidor()
	{
	}
	
	public Servidor(String ip, int porta)
	{
		setIp(ip);
		setPorta(porta);
	}

	public int getPorta()
	{
		return porta;
	}

	public String getIp()
	{
		return ip;
	}

	public void setPorta(int porta)
	{
		this.porta = porta;
	}

	public void setIp(String ip)
	{
		this.ip = ip;
	}
	
	public String getUrlRmi()
	{
		return "rmi://" + ip + ":" + porta;
	}
	
	public String toString()
	{
		return ip + ";" + porta;
	}
}
