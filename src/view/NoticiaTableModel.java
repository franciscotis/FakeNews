package view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import model.Noticia;


/**
 * TableModel para uma tabela do consumo de uma residência.
 * 
 * @author matth.sobral
 *
 */
public class NoticiaTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 5021916616462142844L;
	private List<Noticia> linhas;

	/**
	 *  Array de Strings com o nome das colunas. 
	 *  
	 */
	private String[] colunas = new String[] { "ID", "Título", "Média", "Fake?"};


	/**
	 * Construtor da classe.
	 */
	public NoticiaTableModel() 
	{
		linhas = new ArrayList<>();

	}

	/**
	 *  Retorna a quantidade de colunas.
	 */
	@Override
	public int getColumnCount() 
	{
		return colunas.length;
	}

	/**
	 *  Retorna a quantidade de linhas.
	 */
	@Override
	public int getRowCount() 
	{
		return linhas.size();
	}

	/**
	 * Retorna o nome da coluna no índice especificado.
	 * Este método é usado pela JTable para saber o texto do cabeçalho.
	 *
	 */
	@Override
	public String getColumnName(int columnIndex) {
		// Retorna o conteúdo do Array que possui o nome das colunas
		// no índice especificado.
		return colunas[columnIndex];
	};

	/**
	 *  Retorna a classe dos elementos da coluna especificada.
	 * Este método é usado pela JTable na hora de definir o editor da célula.
	 *
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) 
	{
		switch (columnIndex) {

		case 0: 
			return Integer.class;
		case 1: 
			return String.class;
		case 2:
			return Double.class;
		case 3:
			return Boolean.class;
		default:
			return null;
		}
	}
	
	/**
	 * Retorna o valor da célula especificada
	 * pelos índices da linha e da coluna.
	 * 
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) 
	{      
		Noticia consumo = linhas.get(rowIndex);

		// Retorna o campo referente a coluna especificada.
		// Aqui é feito um switch para verificar qual é a coluna
		// e retornar o campo adequado. As colunas são as mesmas
		// que foram especificadas no array "colunas".
		switch (columnIndex) 
		{
			case 0:
				return consumo.getId();
			case 1: 
				return consumo.getTitulo();
			case 2: 
				return consumo.getMediaAvaliacoes();
			case 3:
				return consumo.isFake();
			default:
				return null;
		}
	}

	/**
	 * Retorna um valor booleano que define se a célula em questão
	 * pode ser editada ou não.
	 * Este método é utilizado pela JTable na hora de definir o editor da célula.
	 * Neste caso, estará sempre retornando false, não permitindo que nenhuma
	 * célula seja editada.
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) 
	{
		return false;
	}

	/**
	 * Adiciona uma lista de sócios ao final dos registros.
	 */
	public void setListaDeNoticias(List<Noticia> noticia)
	{
		if(noticia == null)
			return;
		
		if(linhas != null)
			linhas.clear();
		
		linhas = noticia;

		// Reporta a mudança. O JTable recebe a notificação
		// e se redesenha permitindo que a atualização seja visualizada.
		fireTableRowsInserted(noticia.size(), getRowCount() - 1);
	}
}
