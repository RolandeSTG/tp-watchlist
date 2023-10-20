package com.rolande.restws.model;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A class/bean to define a watchlist.  In its simpliest form, a watchlist is really just a name 
 * associated with a list of security symbols...
 * 
 * @author Rolande
 */
public class Watchlist {
	@JsonProperty(value = "_id")
	Long id;

	@JsonProperty(value = "name")
	private String name;

	@JsonProperty(value = "date_created")
	private Date dateCreated;

	@JsonProperty(value = "number_of_securities")
	private int numberOfSecurities;


	public Watchlist() {
		super();
	}

	public Watchlist(Long id, String name, Date dateCreated, int numberOfSecurities) {
		super();
		this.id = id;
		this.name = name;
		this.dateCreated = dateCreated;
		this.numberOfSecurities = numberOfSecurities;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	public int getNumberOfSecurities() {
		return numberOfSecurities;
	}

	public void setNumberOfSecurities(int numberOfSecurities) {
		this.numberOfSecurities = numberOfSecurities;
	}

	@Override
	public String toString() {
		return "Watchlist [id=" + id + ", name=" + name + ", dateCreated=" + dateCreated + ", numberOfSecurities=" + numberOfSecurities + "]";
	}

}
