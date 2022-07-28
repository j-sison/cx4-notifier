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
public class UrlConfig
{
	//~ Instance fields --------------------------
	/**  */
	private Map<String, String> urls;
	//~ Methods ----------------------------------
	/**
	 * DOCUMENT ME!
	 *
	 * @param   url
	 * @return
	 */
	public String getUrl(String url)
	{
		String result = "";

		if (urls.get(url) != null)
		{
			result = urls.get(url);
		}

		return result;
	}
}
