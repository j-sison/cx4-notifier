package com.jay.cx4emailer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import java.util.Map;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
@RestController
public class DeployNotification
{
	//~ Static fields/initializers ---------------
	/**  */
	private static final Logger LOGGER = LogManager.getLogger(DeployNotification.class);
	//~ Methods ----------------------------------
	/**
	 * DOCUMENT ME!
	 *
	 * @param   allParams
	 * @param   model
	 * @throws  IOException
	 * @throws  NumberFormatException
	 * @throws  MQException
	 */
	@RequestMapping("/shutdownNotify")
	public void queryPage(@RequestParam
		Map<String, String> allParams, Model model)
	{
		String method = allParams.get("method");
		String bambooUser = allParams.get("user");

		if (bambooUser != null)
		{
			bambooUser = bambooUser.trim();
			bambooUser = bambooUser.toLowerCase();
		}
		else
		{
			bambooUser = "sisja";
			LOGGER.info("Defaulting to sisja");
		}

		LOGGER.info("method: " + method);
		LOGGER.info("user: " + bambooUser);

		Authenticator authenticator = new Authenticator()
			{
				private String user, pass;

				@Override
				public PasswordAuthentication getPasswordAuthentication()
				{
					return new PasswordAuthentication(null, null);
				}
			};

		Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.champ.aero");
		prop.put("mail.smtp.timeout", "0");
		prop.put("mail.smtp.port", "25");

		Session session = Session.getInstance(prop, authenticator);
		MimeMessage message = new MimeMessage(session);

		try
		{
			if ((bambooUser != null) && bambooUser.equals("sisja"))
			{
				message.setFrom(new InternetAddress("jaypee.sison@champ.aero"));
			}
			else if ((bambooUser != null) && bambooUser.equals("gonry"))
			{
				message.setFrom(new InternetAddress("Ryan.Gonzales@champ.aero"));
			}
			else if ((bambooUser != null) && bambooUser.equals("vilda"))
			{
				message.setFrom(new InternetAddress("DanAngelo.Villapando@champ.aero"));
			}
			else if ((bambooUser != null) && bambooUser.equals("enrch"))
			{
				message.setFrom(new InternetAddress("ChristopherOliver.ENRIQUEZ@champ.aero"));
			}
			else if ((bambooUser != null) && bambooUser.equals("carfr"))
			{
				message.setFrom(new InternetAddress("FrancisJulian.CAROLINO@champ.aero"));
			}
			else if ((bambooUser != null) && bambooUser.equals("mabch"))
			{
				message.setFrom(new InternetAddress("Chichie.Mabutas@champ.aero"));
			}
			else if ((bambooUser != null) && bambooUser.equals("araet"))
			{
				message.setFrom(new InternetAddress("EthelJean.Aramburo@champ.aero"));
			}
			else if ((bambooUser != null) && bambooUser.equals("samti"))
			{
				message.setFrom(new InternetAddress("TimothyPierce.SAMONTINA@champ.aero"));
			}
			else if ((bambooUser != null) && bambooUser.equals("cspsupport"))
			{
				message.setFrom(new InternetAddress("devcspsupport@champ.aero"));
			}
			else
			{
				message.setFrom(new InternetAddress("sisja@champ.aero"));
			}
			message.setRecipients(Message.RecipientType.TO,
				new Address[]
				{
					new InternetAddress("jaypee.sison@champ.aero"),
					new InternetAddress("f2139f7c.champcargosystems.onmicrosoft.com@emea.teams.ms")
				});
			message.addHeader("X-Auto-Response-Suppress", "OOF");
			if (method.equals("stop"))
			{
				message.setSubject("CX4 IE Deployment");
				message.setText("I will deploy in CX4 IE  in 2 minutes.\r\n"
					+ "Let me know if I should wait. :)\r\n"
					+ "");
			}
			else if (method.equals("start"))
			{
				message.setSubject("CX4 IE Deployment");
				message.setText("Deploy done via http://bamboo.champ.aero:8085/browse/CSPTOOLS-CX4IE.\r\n"
					+ "Thank you. :)");
			}
			else if (method.equals("restart"))
			{
				message.setSubject("CX4 IE Restart");
				message.setText("I will restart CX4 IE  in 2 minutes.\r\n"
					+ "Let me know if I should wait. :)\r\n"
					+ "");
			}
			else if (method.equals("restartDone"))
			{
				message.setSubject("CX4 IE Restart");
				message.setText("Restart done via http://bamboo.champ.aero:8085/browse/CSPTOOLS-CX4IE.\r\n"
					+ "Thank you. :)");
			}
			Transport.send(message);
		}
		catch (MessagingException e)
		{
			LOGGER.error("Failed", e);
		} // end try-catch
	} // end if
	
	/**
	 * DOCUMENT ME!
	 *
	 * @param  allParams
	 * @param  model
	 */
	@RequestMapping("/notify204Deploy")
	public void notify204Deploy(@RequestParam
		Map<String, String> allParams, Model model)
	{
		Authenticator authenticator = new Authenticator()
			{
				private String user, pass;

				@Override
				public PasswordAuthentication getPasswordAuthentication()
				{
					return new PasswordAuthentication(null, null);
				}
			};

		Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.champ.aero");
		prop.put("mail.smtp.timeout", "0");
		prop.put("mail.smtp.port", "25");

		Session session = Session.getInstance(prop, authenticator);
		MimeMessage message = new MimeMessage(session);

		try
		{
			message.setFrom(new InternetAddress("jaypee.sison@champ.aero"));

			message.setRecipients(Message.RecipientType.TO,
				new Address[]{ new InternetAddress("devcspcxhfdeploy@champ.aero") });
			message.addHeader("X-Auto-Response-Suppress", "OOF");

			message.setSubject("CSP-20.4.0-FINAL-CXDEVIE-HF deployment- Any objections?");
			message.setText("Hi All,\r\n"
				+ "\r\n"
				+ "I will deploy in 20.4.0-CX-DEV  in 2 minutes.\r\n"
				+ "Let me know if I should wait.\r\n"
				+ "\r\n"
				+ "\r\n"
				+ "Thanks and Regards,\r\n"
				+ "Jay\r\n"
				+ "");

			Transport.send(message);
		}
		catch (MessagingException e)
		{
			LOGGER.error("Failed", e);
		} // end try-catch
	} // end if
}
