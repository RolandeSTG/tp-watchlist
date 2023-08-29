package com.rolande.restws.model;

/**
 * A class (and bean) to define a security. A security (here) refers to a financial instrument that 
 * investors may buy/sell on trading markets.  Stocks, bonds and options are common instances of it. 
 * @author Rolande St-Gelais
 * 
 */

//@JsonPropertyOrder({ "id", "symbol", "name", "exchange", "previousClose", "openPrice", "lastPrice" })

// {RSTG}: No need of an XML root, we'll use only JSON for now...

//@XmlRootElement(name="Security")
public class Security {	
	private long   id;
	private String symbol;
	private String name;
	private String exchange;
	private double previousClose;
	private double openPrice;
	private double lastPrice;

	// Default no-arg constructor	
	public Security() {}
	
	public Security(long id, String symbol, String name, String exchange, double previousClose, 
			        double openPrice, double lastPrice) {	
		this.id = id;
		this.symbol = symbol;
		this.name = name;
		this.exchange = exchange;
		this.previousClose = previousClose;
		this.openPrice = openPrice;
		this.lastPrice = lastPrice;
	}
	
	// Getters and Setters

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
	
	public double getPreviousClose() {
		return previousClose;
	}
	
	public void setPreviousClose(double previousClose) {
		this.previousClose = previousClose;
	}
	
	public double getOpenPrice() {
		return openPrice;
	}
	
	public void setOpenPrice(double openPrice) {
		this.openPrice = openPrice;
	}
	
	public double getLastPrice() {
		return lastPrice;
	}
	
	public void setLastPrice(double lastPrice) {
		this.lastPrice = lastPrice;
	}
	
	/**
	 * Convert a security object to a string 
	 * @return a printable string representation of a security object
	 */
	@Override
	public String toString() {
		return "Security [id=" + id + ", symbol=" + symbol + ", name=" + name + ", exchange=" + exchange +
				", previousClose=" + previousClose + ", openPrice=" + openPrice + ", lastPrice=" + lastPrice + "]";
	}
	
   
}
