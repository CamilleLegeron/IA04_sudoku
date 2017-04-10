package model;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Cellule {
	private int line;
	private int column;
	private int value;
	private List<Integer> possibilities = new LinkedList<Integer>();

	public Cellule() {
	}

	public Cellule(int line, int column, int value) {
		super();
		this.line = line;
		this.column = column;
		this.value = value;
	}

	public int getLine() {
		return this.line;
	}
	
	public int getColumn(){
		return this.column;
	}

	public int getValue(){
		return this.value;
	}
	
	public void setValue(int value)
	{
		this.value = value;
	}
	
	public List<Integer> getPossibilities(){
		return this.possibilities;
	}
	
	public void setPossibilities(List<Integer> possibilities)
	{
		this.possibilities = possibilities;
	}
	
	public boolean containsPossibility(int value){
		if(this.possibilities.contains(value))
			return true;
		return false;
	}
	
	public boolean containsSamePossibilities(List<Integer> poss){
		if(this.possibilities.size() != poss.size())
			return false;
		for(int i = 0; i<this.possibilities.size(); i++){
			if(!poss.contains(possibilities.get(i)))
				return false;
		}
		return true;
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

