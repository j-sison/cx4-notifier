package com.jay.cx4emailer.config;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.context.annotation.Configuration;

import java.util.Map;


/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
@Configuration
@ConfigurationProperties
@EnableConfigurationProperties
@Getter
@Setter
public class UserConfig
{
	//~ Instance fields --------------------------
	/**  */
	private Map<String, String> users;
	//~ Methods ----------------------------------
	/**
	 * DOCUMENT ME!
	 *
	 * @param   user
	 * @return
	 */
	public String getEmail(String user)
	{
		String result = "";

		if (users.get(user) != null)
		{
			result = users.get(user);
		}

		return result;
	}
}
