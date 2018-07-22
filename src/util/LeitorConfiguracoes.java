package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class LeitorConfiguracoes
{
	private final HashMap<String, Object> conf;

	/**
	 * Método construtor da classe.
	 * 
	 * @param configuracoes
	 */
	public LeitorConfiguracoes(String[] configuracoes)
	{
		conf = new HashMap<>();

		for(String path : configuracoes)
			lerArquivo(path, conf);	
	}

	/**
	 * Efetua a leitura do arquivo, guardando os parâmetros na hash de configurações.
	 * 
	 * @param configuracoes
	 * @param conf
	 */
	private void lerArquivo(String configuracoes, HashMap<String, Object> conf)
	{
		BufferedReader br;
		try
		{
			br = new BufferedReader(new FileReader(configuracoes)); // Cria um buffer pro arquivo
			while(br.ready())
			{
				String linha = br.readLine(); // Ler a linha
				if(linha.charAt(0) != '#')
				{
					String[] atributos = linha.split(":"); // Separa os parâmetros

					addElemento(atributos[0], atributos[1]); // Adiciona na hash
				}
			}

			br.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	/**
	 * Adiciona um elemento na hash
	 * 
	 * @param chave
	 * @param valor
	 */
	@SuppressWarnings("unchecked")
	private void addElemento(String chave, String valor)
	{
		if(chave.equals("server"))
		{
			if(conf.get(chave) instanceof ArrayList)
				((ArrayList<String>)conf.get(chave)).add(valor);
			else
			{
				ArrayList<String> elementos = new ArrayList<>();

				elementos.add(valor);

				conf.put(chave, elementos);
			}
		}
		else
			conf.put(chave, valor);
	}

	/**
	 * Retorna o valor de um parâmetro de configuração.
	 * 
	 * @param configuracao
	 * @return
	 */
	public Object get(String configuracao)
	{
		return conf.get(configuracao);
	}
}
