package com.rolande.restws.model;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A class/bean to define a security. A security (here) refers to a financial instrument 
 * that investors may buy/sell on trading markets. Stocks, bonds and options are common 
 * instances of it.
 * 
 * @author Rolande St-Gelais
 */

public class Security {

	@JsonProperty(value = "_id")
	private long id;

	@JsonProperty(value = "symbol")
	private String symbol;

	@JsonProperty(value = "name")
	private String name;

	@JsonProperty(value = "exchange")
	private String exchange;

	@JsonProperty(value = "quote")
	private Quote quote;

	// Default no-arg constructor
	public Security() {}

	public Security(long id, String symbol, String name, String exchange, Quote quote) {
		this.id = id;
		this.symbol = symbol;
		this.name = name;
		this.exchange = exchange;
		this.quote = quote;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public Quote getQuote() {
		return quote;
	}

	public void setQuote(Quote quote) {
		this.quote = quote;
	}

	@Override
	public String toString() {
		return "Security [id=" + id + ", symbol=" + symbol + ", name=" + name + ", exchange=" + exchange + ", quote="
				+ quote + "]";
	}

}
