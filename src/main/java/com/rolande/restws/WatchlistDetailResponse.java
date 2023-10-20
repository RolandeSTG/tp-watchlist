package com.rolande.restws;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.rolande.restws.model.Security;
import com.rolande.restws.model.Watchlist;

/**
 * Class to return the details of a watchlist, which include its security list and there latest quote.
 * 
 * @author Rolande St-Gelais
 */
public class WatchlistDetailResponse {
	@JsonUnwrapped
	Watchlist list;

	@JsonProperty(value = "securities")
	private List<Security> securities;

	@JsonProperty(value = "environment")
	private String environment;

	public WatchlistDetailResponse() {
		super();
	}

	public WatchlistDetailResponse(Watchlist list, List<Security> securities, String environment) {
		super();
		this.list = list;
		this.securities = securities;
		this.environment = environment;
	}

	public Watchlist getList() {
		return list;
	}

	public void setList(Watchlist list) {
		this.list = list;
	}

	public List<Security> getSecurities() {
		return securities;
	}

	public void setSecurities(List<Security> securities) {
		this.securities = securities;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	@Override
	public String toString() {
		return "WatchlistResponse [list=" + list + ", securities=" + securities + ", environment=" + environment + "]";
	}
	
}
