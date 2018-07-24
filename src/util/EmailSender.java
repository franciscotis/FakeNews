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
 * Envia emails a partir de contas do dom�nio Gmail.
 * 
 * @author matth.sobral
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
		
		/** Par�metros de conex�o com servidor Gmail */
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

		/** Ativa Debug para sess�o */
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

