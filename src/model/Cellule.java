package model;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Cellule {
	private int i;
	private int j;
	private int val;
	private List<Integer> possibilities = new LinkedList<Integer>();

	public Cellule() {
	}

	public Cellule(int line, int column, int value) {
		super();
		this.i = line;
		this.j = column;
		this.val = value;
	}

	public int getLine() {
		return i;
	}
	
	public int getColumn(){
		return this.j;
	}

	public int getValue(){
		return this.val;
	}
	
	public void setValue(int value)
	{
		this.val = value;
	}
	
	public List<Integer> getPossibilities(){
		return this.possibilities;
	}
	
	public void setPossibilities(List<Integer> list)
	{
		this.possibilities = list;
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
	
	public static Cellule read(String jsonString) {
		ObjectMapper mapper = new ObjectMapper();
		Cellule p = null;
		try {
			p = mapper.readValue(jsonString, Cellule.class);
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

