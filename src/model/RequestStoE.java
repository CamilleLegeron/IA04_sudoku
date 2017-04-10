package model;

import jade.core.AID;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RequestStoE {
	private String analyseur;
	private int id;

	public RequestStoE() {
	}

	public RequestStoE(String an, int value) {
		super();
		this.analyseur = an;
		this.id = value;
	}

	public String getAnalyseur() {
		return this.analyseur;
	}

	public void setAnalyseur(String an) {
		this.analyseur = an;
	}
	
	public int getId(){
		return this.id;
	}
	
	public void setId(int value)
	{
		this.id = value;
	}
	
	public String toJSON() {
		ObjectMapper mapper = new ObjectMapper();
		String s = "";
		try {
			s = mapper.writeValueAsString(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static RequestStoE read(String jsonString) {
		ObjectMapper mapper = new ObjectMapper();
		RequestStoE p = null;
		try {
			p = mapper.readValue(jsonString, RequestStoE.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return p;
	}
}
