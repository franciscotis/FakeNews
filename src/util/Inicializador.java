package util;

import java.util.ArrayList;
import java.util.List;

/**
 * Leitor dos arquivos de configuração da aplicação.
 * Por convenção, os arquivos de configuração do app (conf.yml)
 * e o arquivo contendo os IPs e portas dos outros servidores
 * (servers.yml) devem estar na pasta conf no diretório da aplicação
 * @author matth.sobral
 *
 */
public class Inicializador 
{
	private ArrayList<Servidor> servidores;
	private LeitorConfiguracoes leitor;
	
	public Inicializador()
	{
		String[] configuracoes = { "conf/conf.yml", "conf/servers.yml" };
		leitor = new LeitorConfiguracoes(configuracoes);
		initServidores();
	}

	@SuppressWarnings("unchecked")
	private void initServidores()
	{
		servidores = new ArrayList<>();
		List<String> servers = (List<String>) leitor.get("server");
		
		for(String s : servers)
		{
			String[] campos = s.split(";");
			Servidor servidor = new Servidor(campos[0], Integer.parseInt(campos[1]));
			servidores.add(servidor);
		}
	}

	/**
	 * Retorna um valor de uma configuração carregada dos arquivos de configuração
	 * 
	 * @param conf
	 * @return
	 */
	public String getConfiguracao(String conf) // conf = ['entidade', 'timeout']
	{
		return (String) leitor.get(conf);
	}
	
	/**
	 * Retorna uma lista contendo os endereços para todos os outros servidores
	 * que farão parte do processo de consenso
	 * 
	 * @return
	 */
	public List<Servidor> outrosServidores()
	{
		return servidores;
	}


}
