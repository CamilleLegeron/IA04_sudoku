package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ListCellule {

	private Cellule[] listCel = new Cellule[9];
	
	public ListCellule() {
	}

	public ListCellule(Cellule[] list) {
		super();
		listCel = list;
	}
	
	public Cellule[] getList(){
		return this.listCel;
	}
	
	public void setList(Cellule[] list)
	{
		this.listCel = list;
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
	
	public static ListCellule read(String jsonString) {
		ObjectMapper mapper = new ObjectMapper();
		ListCellule p = null;
		try {
			p = mapper.readValue(jsonString, ListCellule.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return p;
	}
}
