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
 * @author matth.sobral
 *
 */
public class EmailSender
{
	private Session session;
	private String emailRemetente;

	/**
	 * Método construtor da classe.
	 * @param email
	 * @param senha
	 */
	public EmailSender(String email, String senha)
	{
		this.emailRemetente = email;
		
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
				return new PasswordAuthentication(email, senha);
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
	public void send(String assunto, String destinatario, String mensagem)
	{
		try 
		{

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(emailRemetente)); //Remetente

			Address toUser = new InternetAddress(destinatario); // Destinatário

			message.setRecipient(Message.RecipientType.TO, toUser); // Adiciona o destinatário à mensagem
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

