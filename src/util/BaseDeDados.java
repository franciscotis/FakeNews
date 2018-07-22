package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import model.Noticia;

public class BaseDeDados
{
	private HashMap<Integer, Noticia> noticias;
	private String comentarios = "";
	private String pathDados;

	/**
	 * Construtor da classe
	 * 
	 * @param nomeLoja
	 */
	public BaseDeDados(String pathDados)
	{
		this.pathDados = pathDados;
		noticias = new HashMap<>();
		lerArquivo(pathDados);
	}

	/**
	 * Efetua a leitura do arquivo de contendo os produtos, salvando-o na lista de produtos
	 * @param pathDados
	 */
	public void lerArquivo(String pathDados)
	{
		BufferedReader br;

		try
		{
			br = new BufferedReader(new FileReader(pathDados));

			while(br.ready())
			{
				String linha = br.readLine();
				if(linha.charAt(0) != '#' && linha.charAt(0) != '\n') // Linhas começadas pelo caractere '#' é uma linha comentário
				{
					String[] atributos = linha.split(";");

					Noticia noticia = new Noticia();
					
					noticia.setId(Integer.parseInt(atributos[0])); // O primeiro parametro é o id
					noticia.setTitulo(atributos[1]); // O segundo é o nome
					noticia.setNumeroAvaliacoes(Integer.parseInt(atributos[2])); // O terceiro é a quantidade 
					noticia.setMediaAvaliacoes(Double.parseDouble(atributos[3])); // O quanto o preço 
					noticia.setFake(Boolean.parseBoolean(atributos[4]));
					
					noticias.put(new Integer(noticia.getId()), noticia);
				}
				else
					comentarios += linha + '\n';
			}

			br.close();
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Persiste os dados no arquivo usado na leitura inicial
	 * 
	 */
	public void persistirDados()
	{
		BufferedWriter bw = null;
		FileWriter fw = null;

		try 
		{
			String conteudo = getDadosString(); // Converte os dados em String

			fw = new FileWriter(pathDados);
			bw = new BufferedWriter(fw);
			bw.write(conteudo); // Escreve os dados

			System.out.println("Base de dados atualizada");

		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try 
			{
				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();
			} 
			catch (IOException ex) 
			{
				ex.printStackTrace();
			}

		}
	}

	/**
	 * Retorna a lista de produtos
	 * 
	 * @return
	 */
	public HashMap<Integer, Noticia> getNoticias()
	{
		return noticias;
	}

	/**
	 * Retorna os dados como String
	 * @return
	 */
	private String getDadosString()
	{
		StringBuilder builder = new StringBuilder();

		builder.append(comentarios);

		for(Noticia noticia : noticias.values())
		{
			builder.append(noticia.toString());
		}

		return builder.toString();
	}

}
