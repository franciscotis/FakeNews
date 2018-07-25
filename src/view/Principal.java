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

package view;

import java.awt.EventQueue;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import controller.Controller;
import model.Noticia;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JLabel;

public class Principal extends JFrame 
{
	private Controller controller;
	private ExecutorService executor;
	private static final long serialVersionUID = 6375324782041802229L;
	private JPanel contentPane;
	private JSpinner spinner;
	private JComboBox<Noticia> noticias;
	private JTextPane saida;
	private JTable tabela;
	private NoticiaTableModel modelTabela;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) 
	{
		try 
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} 
		catch(Exception e)
		{
			// LookAndFell n�o encontrada
		}
		
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try
				{
					Principal frame = new Principal();
					frame.setVisible(true);
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Principal() throws AlreadyBoundException, IOException, InterruptedException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		executor = Executors.newCachedThreadPool();
		controller = new Controller("data/data.dat");
		
		JButton btnAvaliar = new JButton("Avaliar");
		btnAvaliar.addActionListener(e -> addAvaliacao());
		btnAvaliar.setBounds(255, 81, 89, 23);
		contentPane.add(btnAvaliar);
		
		noticias = new JComboBox<>(new DefaultComboBoxModel(controller.listarNoticias()));
		noticias.setBounds(10, 48, 300, 23);
		contentPane.add(noticias);
		
		spinner = new JSpinner();
		spinner.addChangeListener(e -> limitarSpinner());
		spinner.setBounds(350, 49, 50, 22);
		spinner.setValue(new Integer(1));
		contentPane.add(spinner);
		
		saida = new JTextPane();
		saida.setBounds(10, 289, 339, 61);
		contentPane.add(saida);
		

		modelTabela = new NoticiaTableModel();
		
		modelTabela.setListaDeNoticias(controller.getListaNoticias());
		tabela = new JTable(modelTabela);
		
		JScrollPane scrollPane = new JScrollPane(tabela);
		scrollPane.setBounds(10, 132, 339, 128);
		contentPane.add(scrollPane);
		
		JLabel lblNoticia = new JLabel("Noticia:");
		lblNoticia.setBounds(10, 31, 46, 14);
		contentPane.add(lblNoticia);
		
		JLabel lblSaida = new JLabel("Saida:");
		lblSaida.setBounds(10, 273, 46, 14);
		contentPane.add(lblSaida);
		
		executor.execute(() -> atualizarTabela());
	}
	
	private void addAvaliacao() //Método que adiciona uma avaliação
	{
		Noticia noticiaSelecionada = (Noticia) noticias.getSelectedItem();
		int avaliacao = new Integer((int) spinner.getValue()).intValue();
		controller.addAvaliacao(noticiaSelecionada, avaliacao);
	}
	
	private void limitarSpinner()
	{
		if(new Integer(1).compareTo((Integer) spinner.getValue()) > 0)
			spinner.setValue(new Integer(1));
		
		if(new Integer(5).compareTo((Integer) spinner.getValue()) < 0)
			spinner.setValue(new Integer(5));
	}
	
	private void atualizarTabela()
	{
		while(true)
		{
			controller.aguardarAtualizacao();
			modelTabela.fireTableDataChanged();
		}
	}
}
