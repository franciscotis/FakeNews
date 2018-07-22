package controller;

import model.Noticia;
import util.BaseDeDados;
import util.EmailSender;

public class Controller 
{
	private EmailSender emailSender;
	private BaseDeDados baseDados;
	
	public Controller(String pathDados)
	{
		baseDados = new BaseDeDados(pathDados);
		emailSender = new EmailSender("inova.fsa@gmail.com", "inovafsa2018");
		
		for(Noticia n : baseDados.getNoticias().values())
			System.out.println(n.toString());
		
		baseDados.persistirDados();
	}
	
}
