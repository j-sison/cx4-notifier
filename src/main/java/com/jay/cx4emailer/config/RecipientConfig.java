package com.jay.cx4emailer.config;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
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
public class RecipientConfig
{
	//~ Instance fields --------------------------
	/**  */
	private Map<String, String> recipients;
	//~ Methods ----------------------------------
	/**
	 * DOCUMENT ME!
	 *
	 * @return
	 */
	public List<String> getEmailList()
	{
		return new ArrayList<String>(recipients.values());
	}
}
