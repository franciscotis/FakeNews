package controller;

import model.Noticia;
import util.BaseDeDados;
import util.EmailSender;

public class Controller 
{
	private BaseDeDados baseDados;
	private EmailSender emailSender;
	
	public Controller(String pathDados)
	{
		baseDados = new BaseDeDados(pathDados);
		emailSender = new EmailSender("inova.fsa@gmail.com", "inovafsa2018");
		
		for(Noticia n : baseDados.getNoticias())
			System.out.println(n.toString());
		
		baseDados.persistirDados();
	}
	
}
