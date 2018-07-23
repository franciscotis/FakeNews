package model;

import jdk.nashorn.internal.runtime.ECMAException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMISocketFactory;
import java.util.List;
import java.util.Random;

public class Consenso 
{
	private Noticia noticia;
	private List<Servidor> servidores;
	private List<ISiteNoticia> sites;
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

	public void iniciarConsenso() throws IOException, NotBoundException {
		System.out.println("Iniciou processo de consenso");


		for(int i=0;i<this.servidores.size();i++){ //Método que adiciona em uma lista os servidores em um determinado timeout
			ISiteNoticia site = (ISiteNoticia) Naming.lookup("rmi://"+this.servidores.get(i).getIp()+":"+String.valueOf(this.servidores.get(i).getPorta())+"/SiteNoticia");
			try{
				this.sites.add(site);
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		int maximum = this.sites.size();
		int minimum = 0;
		int randomNum;
		Random rand = new Random();
		randomNum = minimum + rand.nextInt((maximum - minimum) + 1); //Escolha de quem vai realizar o algoritmo
		ISiteNoticia site = this.sites.get(randomNum);
		site.consenso(noticia.getId(),this.sites);















		//TODO Ap�s o consenso setar se a noticia foi considerada fake
		//TODO noticia.setFake(resultadoConsenso);


		noticia.setFake(noticia.isFake()); //Retirar essa avalia��o


		eventoPosConsenso.disparar();
	}

}
