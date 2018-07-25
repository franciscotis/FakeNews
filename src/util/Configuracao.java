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


package util;

import java.util.ArrayList;
import java.util.List;

import model.Servidor;

/**
 * Leitor dos arquivos de configuração da aplicação.
 * Por convenção, os arquivos de configuração do app (conf.yml)
 * e o arquivo contendo os IPs e portas dos outros servidores
 * (servers.yml) devem estar na pasta conf no diretório da aplicação
 *
 */
public class Configuracao 
{
	private ArrayList<Servidor> servidores;
	private LeitorConfiguracoes leitor;
	
	public Configuracao()
	{
		String[] configuracoes = { "conf/conf.yml", "conf/servers.yml" };
		leitor = new LeitorConfiguracoes(configuracoes);
		initServidores();
	}

	@SuppressWarnings("unchecked")
	private void initServidores() //Adiciona em uma lista de servidores, todos os servidores no arquivo. yml
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
	 * Retorna uma lista contendo os endere�os para todos os outros servidores
	 * que farão parte do processo de consenso
	 * 
	 * @return
	 */
	public List<Servidor> outrosServidores()
	{
		return servidores;
	}


}
