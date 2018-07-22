package view;

import util.Inicializador;
import util.Servidor;

public class Console {

	public static void main(String[] args)
	{
		Inicializador inicializador = new Inicializador();
		
		for(Servidor s : inicializador.outrosServidores())
			System.out.println(s);
		

	}

}
