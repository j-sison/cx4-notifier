package com.jay.cx4emailer;

import static com.jay.util.MessageHandler.convertTimeToMilliseconds;
import static com.jay.util.MessageHandler.generateDoneMsg;
import static com.jay.util.MessageHandler.generateSleepMsg;
import static com.jay.util.MessageHandler.generateStopRedirectUrl;
import static com.jay.util.MessageHandler.generateStopUrl;

import com.jay.cx4emailer.config.MessageConfig;
import com.jay.cx4emailer.config.RecipientConfig;
import com.jay.cx4emailer.config.UrlConfig;
import com.jay.cx4emailer.config.UserConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.ComponentScan;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

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
@ComponentScan
@RestController
public class DeployNotification
{
	//~ Static fields/initializers ---------------
	/**  */
	private static final Logger LOGGER = LogManager.getLogger(DeployNotification.class);
	//~ Instance fields --------------------------
	/**  */
	Timer timer = new Timer();

	/**  */

	/**  */
	private Authenticator authenticator;

	/**  */
	private String bambooUser;

	/**  */
	private String buildNum;

	/**  */
	private boolean isBuildStopEnabled = true;

	/**  */
	private String method;

	/**  */
	@Autowired
	private MessageConfig msgConfig;

	/**  */
	private Properties prop;

	/**  */
	@Autowired
	private RecipientConfig recipientConfig;

	/**  */
	private RedirectView redirect;

	/**  */
	private String sleepTime;

	/**  */
	@Autowired
	private UrlConfig urlConfig;

	/**  */
	@Autowired
	private UserConfig userConfig;
	//~ Methods ----------------------------------
	/** DOCUMENT ME! */

	/**
	 * /** DOCUMENT ME!
	 *
	 * @param  allParams
	 * @param  model
	 */
	@RequestMapping("/shutdownNotify")
	public void queryPage(@RequestParam
		Map<String, String> allParams, Model model)
	{
		method = allParams.get("method");
		bambooUser = allParams.get("user");
		buildNum = allParams.get("buildNum");
		sleepTime = allParams.get("sleepTime");

		formatUserString();
		LOGGER.info("isBuildStopEnabled " + isBuildStopEnabled);
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
		timer.cancel();

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
				message.setSubject(msgConfig.getMsg(Const.DEPLOY_SUBJECT));
				message.setText(isBuildStopEnabled ? msgConfig.getMsg(Const.DEPLOY_STOP_DONE_MSG)
												   : msgConfig.getMsg(Const.DEPLOY_STOP_NOT_DONE_MSG));
			}
			else
			{
				message.setSubject(msgConfig.getMsg(Const.RESTART_SUBJECT));
				message.setText(isBuildStopEnabled ? msgConfig.getMsg(Const.RESTART_STOP_DONE_MSG)
												   : msgConfig.getMsg(Const.RESTART_STOP_NOT_DONE_MSG));
			}
			Transport.send(message);
		}
		catch (MessagingException e)
		{
			LOGGER.error("Failed", e);
		} // end try-catch

		if (isBuildStopEnabled)
		{
			redirect = new RedirectView(generateStopRedirectUrl(urlConfig.getUrl(Const.STOP_REDIRECT_URL), buildNum));
		}
		else
		{
			redirect = new RedirectView(urlConfig.getUrl(Const.BAMBOO_URL));
		}

		return redirect;
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
		if ((bambooUser != null) && !userConfig.getEmail(bambooUser).isEmpty())
		{
			message.setFrom(new InternetAddress(userConfig.getEmail(bambooUser)));
		}
		else
		{
			message.setFrom(new InternetAddress(userConfig.getEmail("default")));
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
		message.setRecipients(Message.RecipientType.TO, getRecipientAddresses());
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
	 * @return
	 * @throws  AddressException
	 */
	private Address[] getRecipientAddresses() throws AddressException
	{
		List<Address> emailList = new ArrayList<>();
		for (String email : recipientConfig.getEmailList())
		{
			emailList.add(new InternetAddress(email));
		}

		Address[] result = new Address[emailList.size()];

		return emailList.toArray(result);
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
		String stopUrl = "";
		isBuildStopEnabled = true;

		if (method.equals("deploy"))
		{
			timer = new Timer();
			timer.schedule(new DisableBuildStop(), convertTimeToMilliseconds(sleepTime));
			msg = generateSleepMsg(msgConfig.getMsg(Const.DEPLOY_MSG), sleepTime);
			stopUrl = generateStopUrl(msgConfig.getMsg(Const.DEPLOY_STOP_MSG),
					urlConfig.getUrl(Const.DEPLOY_STOP_URL),
					bambooUser, buildNum);

			message.setSubject(msgConfig.getMsg(Const.DEPLOY_SUBJECT));
			message.setText(msg + stopUrl, Const.UTF, Const.HTML);
		}
		else if (method.equals("deployDone"))
		{
			msg = generateDoneMsg(msgConfig.getMsg(Const.DEPLOY_DONE_MSG), urlConfig.getUrl(Const.BAMBOO_URL));
			message.setSubject(msgConfig.getMsg(Const.DEPLOY_SUBJECT));
			message.setText(msg, Const.UTF, Const.HTML);
		}
		else if (method.equals("restart"))
		{
			timer = new Timer();
			timer.schedule(new DisableBuildStop(), convertTimeToMilliseconds(sleepTime));
			msg = generateSleepMsg(msgConfig.getMsg(Const.RESTART_MSG), sleepTime);
			stopUrl = generateStopUrl(msgConfig.getMsg(Const.RESTART_STOP_MSG),
					urlConfig.getUrl(Const.RESTART_STOP_URL),
					bambooUser, buildNum);

			message.setSubject(msgConfig.getMsg(Const.RESTART_SUBJECT));
			message.setText(msg + stopUrl, Const.UTF, Const.HTML);
		}
		else if (method.equals("restartDone"))
		{
			msg = generateDoneMsg(msgConfig.getMsg(Const.RESTART_DONE_MSG), urlConfig.getUrl(Const.BAMBOO_URL));
			message.setSubject(msgConfig.getMsg(Const.RESTART_SUBJECT));
			message.setText(msg, Const.UTF, Const.HTML);
		} // end if-else
	}
	//~ Inner Classes ----------------------------
	/**
	 * DOCUMENT ME!
	 *
	 * @version  $Revision$, $Date$
	 */
	private class DisableBuildStop extends TimerTask
	{
		/** @see  java.util.TimerTask#run() */
		@Override
		public void run()
		{
			isBuildStopEnabled = false;
		}
	}
}
