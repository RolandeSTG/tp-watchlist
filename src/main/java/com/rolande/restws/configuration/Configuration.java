package com.rolande.restws.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("watchlist-service")

public class Configuration {
	
	private String quoteBaseUrl;

	public String getQuoteBaseUrl() {
		return quoteBaseUrl;
	}

	public void setQuoteBaseUrl(String quoteBaseUrl) {
		this.quoteBaseUrl = quoteBaseUrl;
	}

}
