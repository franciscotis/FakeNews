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

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Envia emails a partir de contas do domínio Gmail.
 *
 *
 */
public class EmailSender
{
	private static Session session = null;
	private static String emailRemetente;

	private static void iniciarSessao()
	{
		emailRemetente = "inova.fsa@gmail.com";
		String senha = "inovafsa2018";
		
		Properties propriedades = new Properties();
		
		/** Parâmetros de conexão com servidor Gmail */
		propriedades.put("mail.smtp.host", "smtp.gmail.com");
		propriedades.put("mail.smtp.socketFactory.port", "465");
		propriedades.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		propriedades.put("mail.smtp.auth", "true");
		propriedades.put("mail.smtp.port", "465");

		session = Session.getDefaultInstance(propriedades, new javax.mail.Authenticator()
		{
			protected PasswordAuthentication getPasswordAuthentication()
			{
				return new PasswordAuthentication(emailRemetente, senha);
			}
		});

		/** Ativa Debug para sessão */
		session.setDebug(true);
	}
	
	/**
	 * Efetua o envio de um e-mail.
	 * @param assunto
	 * @param destinatario
	 * @param mensagem
	 */
	public static void send(String assunto, String destinatario, String mensagem)
	{
		if(session == null)
			iniciarSessao();
		
		try 
		{

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(emailRemetente)); //Remetente

			Address toUser = new InternetAddress(destinatario); // Destinat�rio

			message.setRecipient(Message.RecipientType.TO, toUser); // Adiciona o destinat�rio � mensagem
			message.setSubject(assunto); // Assunto
			message.setText(mensagem); // Mensagem
			
			Transport.send(message);
		} 
		catch (MessagingException e) 
		{
			throw new RuntimeException(e);
		}
	}
}

