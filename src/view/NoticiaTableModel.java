package view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import model.Noticia;


/**
 * TableModel para uma tabela do consumo de uma resid�ncia.
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
	 * Retorna o nome da coluna no �ndice especificado.
	 * Este m�todo � usado pela JTable para saber o texto do cabe�alho.
	 *
	 */
	@Override
	public String getColumnName(int columnIndex) {
		// Retorna o conte�do do Array que possui o nome das colunas
		// no �ndice especificado.
		return colunas[columnIndex];
	};

	/**
	 *  Retorna a classe dos elementos da coluna especificada.
	 * Este m�todo � usado pela JTable na hora de definir o editor da c�lula.
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
	 * Retorna o valor da c�lula especificada
	 * pelos �ndices da linha e da coluna.
	 * 
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) 
	{      
		Noticia consumo = linhas.get(rowIndex);

		// Retorna o campo referente a coluna especificada.
		// Aqui � feito um switch para verificar qual � a coluna
		// e retornar o campo adequado. As colunas s�o as mesmas
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
	 * Retorna um valor booleano que define se a c�lula em quest�o
	 * pode ser editada ou n�o.
	 * Este m�todo � utilizado pela JTable na hora de definir o editor da c�lula.
	 * Neste caso, estar� sempre retornando false, n�o permitindo que nenhuma
	 * c�lula seja editada.
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) 
	{
		return false;
	}

	/**
	 * Adiciona uma lista de s�cios ao final dos registros.
	 */
	public void setListaDeNoticias(List<Noticia> noticia)
	{
		if(noticia == null)
			return;
		
		if(linhas != null)
			linhas.clear();
		
		linhas = noticia;

		// Reporta a mudan�a. O JTable recebe a notifica��o
		// e se redesenha permitindo que a atualiza��o seja visualizada.
		fireTableRowsInserted(noticia.size(), getRowCount() - 1);
	}
}
