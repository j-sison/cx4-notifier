package com.jay.cx4emailer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

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
import javax.mail.internet.AddressException;
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
	//~ Instance fields --------------------------
	/**  */
	private Authenticator authenticator;

	/**  */
	private String bambooUser;

	/**  */
	private String buildNum;

	/**  */
	private String method;

	/**  */
	private Properties prop;

	/**  */
	private String sleepTime;
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
		method = allParams.get("method");
		bambooUser = allParams.get("user");
		buildNum = allParams.get("buildNum");
		sleepTime = allParams.get("sleepTime").replaceAll("[^0-9]", "");

		formatUserString();

		LOGGER.info("method: " + method);
		LOGGER.info("user: " + bambooUser);

		authenticator = getAuthenticator();
		prop = getProperties();

		Session session = Session.getInstance(prop, authenticator);
		MimeMessage message = new MimeMessage(session);

		try
		{
			assignMessageSetFrom(message);
			assignMessageRecipient(message);
			assignMessageHeader(message);
			setMessageText(method, sleepTime, message);

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
	 * @param   allParams
	 * @param   model
	 * @return
	 */
	@RequestMapping("/cancelNotify")
	public RedirectView cancelPage(@RequestParam
		Map<String, String> allParams, Model model)
	{
		method = allParams.get("method");
		bambooUser = allParams.get("user");
		buildNum = allParams.get("buildNum");

		formatUserString();

		LOGGER.info("method: " + method);
		LOGGER.info("user: " + bambooUser);

		authenticator = getAuthenticator();
		prop = getProperties();

		Session session = Session.getInstance(prop, authenticator);
		MimeMessage message = new MimeMessage(session);

		try
		{
			assignMessageSetFrom(message);
			assignMessageRecipient(message);
			assignMessageHeader(message);

			if (method.equals("deploy"))
			{
				message.setSubject("CX4 IE Deployment");
				message.setText("The deployment has been stopped. \r\n"
					+ "");
			}
			else
			{
				message.setSubject("CX4 IE Restart");
				message.setText("The restart has been stopped. \r\n"
					+ "");
			}
			Transport.send(message);
		}
		catch (MessagingException e)
		{
			LOGGER.error("Failed", e);
		} // end try-catch

		return new RedirectView("http://bamboo.champ.aero:8085/build/admin/stopPlan.action?planResultKey"
			+ "=CSPTOOLS-CX4IE-" + buildNum + "&returnUrl=%2Fbrowse%2FCSPTOOLS-CX4IE-" + buildNum);
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
		authenticator = getAuthenticator();
		prop = getProperties();

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
	
	/** DOCUMENT ME! */
	private void formatUserString()
	{
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
	}
	
	/**
	 * DOCUMENT ME!
	 *
	 * @return
	 */
	private Authenticator getAuthenticator()
	{
		return new Authenticator()
		{
			private String user, pass;

			@Override
			public PasswordAuthentication getPasswordAuthentication()
			{
				return new PasswordAuthentication(null, null);
			}
		};
	}
	
	/**
	 * DOCUMENT ME!
	 *
	 * @return
	 */
	private Properties getProperties()
	{
		Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.champ.aero");
		prop.put("mail.smtp.timeout", "0");
		prop.put("mail.smtp.port", "25");

		return prop;
	}
	
	/**
	 * DOCUMENT ME!
	 *
	 * @param   message
	 * @throws  AddressException
	 * @throws  MessagingException
	 */
	private void assignMessageSetFrom(MimeMessage message) throws AddressException, MessagingException
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
	}
	
	/**
	 * DOCUMENT ME!
	 *
	 * @param   message
	 * @throws  AddressException
	 * @throws  MessagingException
	 */
	private void assignMessageRecipient(MimeMessage message) throws AddressException, MessagingException
	{
		message.setRecipients(Message.RecipientType.TO,
			new Address[]
			{
				// --------------------------------- CHANGE THIS
				new InternetAddress("jaypee.sison@champ.aero"),
				new InternetAddress("f2139f7c.champcargosystems.onmicrosoft.com@emea.teams.ms")
			});
	}
	
	/**
	 * DOCUMENT ME!
	 *
	 * @param   message
	 * @throws  MessagingException
	 */
	private void assignMessageHeader(MimeMessage message) throws MessagingException
	{
		message.addHeader("X-Auto-Response-Suppress", "OOF");
	}
	
	/**
	 * DOCUMENT ME!
	 *
	 * @param   method
	 * @param   message
	 * @throws  MessagingException
	 */
	private void setMessageText(String method, String sleepTime, MimeMessage message) throws MessagingException
	{
		String msg = "";
		String stopLink = "";
		if (method.equals("deploy"))
		{
			msg = "I will deploy CX4 IE in " + sleepTime + " minutes.<br>"
				+ "Let me know if I should wait. :) <br>";
			stopLink =
				"<a href=http://cs-v-cspcxdev-03.champ.aero:8080/cx4-emailer-0.0.2-SNAPSHOT/cancelNotify?method=deploy&"
				+ "user=" + bambooUser + "&buildNum=" + buildNum
				+ ">If you wish to stop the deployment, click here<br>";

			message.setSubject("CX4 IE Deployment");
			message.setText(msg + stopLink, "UTF-8", "html");
		}
		else if (method.equals("deployDone"))
		{
			msg = "Deploy done via "
				+ "<a href=http://bamboo.champ.aero:8085/browse/CSPTOOLS-CX4IE>http://bamboo.champ.aero:8085/browse/CSPTOOLS-CX4IE</a><br>"
				+ "Thank you. :)";
			message.setSubject("CX4 IE Deployment");
			message.setText(msg, "UTF-8", "html");
		}
		else if (method.equals("restart"))
		{
			msg = "I will restart CX4 IE in " + sleepTime + " minutes.<br>"
				+ "Let me know if I should wait. :) <br>";
			stopLink =
				"<a href=http://cs-v-cspcxdev-03.champ.aero:8080/cx4-emailer-0.0.2-SNAPSHOT/cancelNotify?method=restart&"
				+ "user=" + bambooUser + "&buildNum=" + buildNum + ">If you wish to stop the restart, click here<br>";

			message.setSubject("CX4 IE Restart");
			message.setText(msg + stopLink, "UTF-8", "html");
		}
		else if (method.equals("restartDone"))
		{
			msg = "Restart done via "
				+ "<a href=http://bamboo.champ.aero:8085/browse/CSPTOOLS-CX4IE>http://bamboo.champ.aero:8085/browse/CSPTOOLS-CX4IE</a><br>"
				+ "Thank you. :)";
			message.setSubject("CX4 IE Restart");
			message.setText(msg, "UTF-8", "html");
		} // end if-else
	}
}
