package com.jay.cx4emailer.config;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
@Component
@ConfigurationProperties
@EnableConfigurationProperties
@Getter
@Setter
public class MessageConfig
{
	//~ Instance fields --------------------------
	/**  */
	private Map<String, Object> messages;
	//~ Methods ----------------------------------
	/**
	 * DOCUMENT ME!
	 *
	 * @param   msg
	 * @return
	 */
	public String getMsg(String msg)
	{
		String result = "";

		if (messages.get(msg) != null)
		{
			result = messages.get(msg).toString();
		}

		return result;
	}
}
