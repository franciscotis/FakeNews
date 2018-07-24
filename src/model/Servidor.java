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
