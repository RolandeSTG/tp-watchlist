package com.rolande.restws.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Defines an error body to include in a response to client.
 * 
 * @author Rolande
 */

public class ErrorBody {

	@JsonProperty(value = "message")
	private String message;

	public ErrorBody() {	}

	public ErrorBody(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return ".. ErrorBody = { message: "+ message + " }";
	}
	
}
