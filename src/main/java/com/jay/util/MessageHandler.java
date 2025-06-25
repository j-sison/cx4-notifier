package com.jay.util;

import java.util.concurrent.TimeUnit;


/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public final class MessageHandler
{
	//~ Constructors -----------------------------
	/** Creates a new MessageHandler object. */
	private MessageHandler( ) { }
	//~ Methods ----------------------------------
	/**
	 * DOCUMENT ME!
	 *
	 * @param   msg
	 * @param   url
	 * @return
	 */
	public static void main(String[] args)
	{
		System.out.println(convertTimeToMilliseconds("30m"));
	}
	
	/**
	 * DOCUMENT ME!
	 *
	 * @param   msg
	 * @param   url
	 * @return
	 */
	public static String generateDoneMsg(String msg, String url)
	{
		return msg.replaceAll("\\{}", url);
	}
	
	/**
	 * DOCUMENT ME!
	 * 
	 * @param msg
	 * @param url
	 * @param buildNum
	 * @return
	 */
	public static String generateDoneMsg(String msg, String url, String buildNum)
	{
		return msg.replaceFirst("\\{}", url).replaceFirst("\\{}", url).replaceAll("\\{}", buildNum);
	}
	
	/**
	 * DOCUMENT ME!
	 *
	 * @param   msg
	 * @param   sleep
	 * @return
	 */
	public static String generateSleepMsg(String msg, String sleep)
	{
		return msg.replaceAll("\\{}", sleep);
	}
	
	/**
	 * DOCUMENT ME!
	 *
	 * @param   msg
	 * @param   url
	 * @return
	 */
	public static String generateStopUrl(String msg, String url, String user, String buildNum)
	{
		return assignValues(msg.replaceAll("\\{}", url), user, buildNum);
	}
	
	/**
	 * DOCUMENT ME!
	 *
	 * @param   url
	 * @param   buildNum
	 * @return
	 */
	public static String generateStopRedirectUrl(String url, String buildNum)
	{
		return url.replaceAll("\\{}", buildNum);
	}
	
	/**
	 * DOCUMENT ME!
	 *
	 * @param   msg
	 * @param   user
	 * @param   buildNum
	 * @return
	 */
	private static String assignValues(String msg, String user, String buildNum)
	{
		return msg.replaceFirst("\\{}", user).replaceAll("\\{}", buildNum);
	}
	
	/**
	 * DOCUMENT ME!
	 *
	 * @param   time
	 * @return
	 */
	public static long convertTimeToMilliseconds(String time)
	{
		String timeUnit = getTimeUnit(time);
		String extractedTime = extractTime(time);
		Long seconds = 0L;

		if ("s".equals(timeUnit))
		{
			seconds = TimeUnit.MILLISECONDS.convert(Long.parseLong(extractedTime), TimeUnit.SECONDS);
		}
		else if ("m".equals(timeUnit))
		{
			seconds = TimeUnit.MILLISECONDS.convert(Long.parseLong(extractedTime), TimeUnit.MINUTES);
		}
		else if ("h".equals(timeUnit))
		{
			seconds = TimeUnit.MILLISECONDS.convert(Long.parseLong(extractedTime), TimeUnit.HOURS);
		}

		return seconds;
	}
	
	/**
	 * DOCUMENT ME!
	 *
	 * @param   time
	 * @return
	 */
	private static String getTimeUnit(String time)
	{
		return time.substring(time.length() - 1);
	}
	
	/**
	 * DOCUMENT ME!
	 *
	 * @param   time
	 * @return
	 */
	private static String extractTime(String time)
	{
		return time.replaceAll(getTimeUnit(time), "");
	}
	
	/**
	 * DOCUMENT ME!
	 *
	 * @param   time
	 * @return
	 */
	public static String getTimeMsg(String time)
	{
		String extractedTime = extractTime(time);
		String timeMsg = "";

		if ("1".equals(extractedTime))
		{
			timeMsg = extractedTime + ("s".equals(getTimeUnit(time)) ? " second" : " minute");
		}
		else
		{
			timeMsg = extractedTime + ("s".equals(getTimeUnit(time)) ? " seconds" : " minutes");
		}

		return timeMsg;
	}
}
