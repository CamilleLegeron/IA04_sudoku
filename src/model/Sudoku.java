package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Sudoku {
	Cellule [ ] [ ] _grid = new Cellule [ 9 ] [ 9 ] ;
	boolean initDone = false;
	public Sudoku()
	{
	}
	
	public boolean isDone()
	{
		if(!initDone)
			return false;
		
		for(int i = 0; i < 9 ; i++)
			for(int j = 0; j<9 ; j++)
			{
				if(_grid[i][j].getValue() == 0)
					return false;
			}
		return true;
	}
	
	public boolean loadGridFromFile(String file)
	{
		try(BufferedReader br = new BufferedReader(new FileReader(file)) )
		{
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    String everything = sb.toString();
		    String[] everythingParsed = everything.split("\\n"); 
		    for (int i = 0; i < 9; i++){
		    	String[] lineParsed = everythingParsed[i].split(" ");
		    	for (int j = 0; j < 9; j++){
		    		Cellule cel = new Cellule(i,j, Integer.valueOf(lineParsed[j].trim()));
		    		_grid[i][j] = cel;
//		    		System.out.println(cel.toJSON());
		    	}
		    }
		    initDone = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	//Cette fonction permet d'afficher un sudoku
	//Il affiche la valeur si elle existe, sinon il affiche la liste des possibilit�s
	public void display()
	{
		for (int i=0; i<9; i++){
			System.out.printf("| ");
			for (int j=0; j<9; j++){
				if (_grid[i][j].getValue() != 0){
					System.out.printf(_grid[i][j].getValue() + "  ");
				} else {
					System.out.printf(_grid[i][j].getPossibilities() + " ");
				}
				if (j == 2 || j == 5){
					System.out.printf("    ");
				}
			}
			System.out.println("|");
			if (i == 2 || i == 5){
				System.out.println();
			}
		}		
	}
	
	public Cellule[] getListCellulesById(int id) {
		//System.out.println(id);
		Cellule[] ListCellules = new Cellule[9];
		if (id>=0 && id < 9) {
			//ids correspondant aux lignes
			for (int j =0; j < 9; j++ ) {
				ListCellules[j] = _grid[id][j];
			}
		} else if (id < 18){
			//ids correspondant aux colonnes
			for (int i =0; i < 9; i++ ) {
				ListCellules[i] = _grid[i][id-9];
			}
		} else if (id < 27){
			//ids correspondant aux carr�s
			int k = 0;
			for (int i=0; i<3; i++){
				for( int j=0; j<3; j++) {
					ListCellules[k++] = _grid[3*(id%3)+i][3*((id/3)-6)+j];
				}
			}
			
		}
		return ListCellules;
	}
	
	public void replaceInSudoku(int id, Cellule[] ListCellules) {
		if (id>=0 && id < 9) {
			//ids correspondant aux lignes
			for (int j =0; j < 9; j++ ) {
				if (_grid[id][j].getValue() == 0){
					if (ListCellules[j].getValue() != 0 ){
						// si la valeur a �t� trouv�e on la met dans le soduko
						_grid[id][j].setValue(ListCellules[j].getValue());
					} else if (_grid[id][j].getPossibilities().size() == 0 || 
							_grid[id][j].getPossibilities().size() > ListCellules[j].getPossibilities().size()){
						// si la liste des possibles est plus courte que pr�c�demment on l'a remplace
						_grid[id][j].setPossibilities(ListCellules[j].getPossibilities());
					}
				}
			}
		} else if (id < 18){
			//ids correspondant aux colonnes
			for (int i =0; i < 9; i++ ) {
				if (_grid[i][id-9].getValue() == 0){
					if (ListCellules[i].getValue() != 0 ){
						// si la valeur a �t� trouv�e on la met dans le soduko
						_grid[i][id-9].setValue(ListCellules[i].getValue());
					} else if (_grid[i][id-9].getPossibilities().size() == 0 || 
							_grid[i][id-9].getPossibilities().size() > ListCellules[i].getPossibilities().size()){
						// si la liste des possibles est plus courte que pr�c�demment on l'a remplace
						_grid[i][id-9].setPossibilities(ListCellules[i].getPossibilities());
					}
				}
			}
		} else if (id < 27){
			//ids correspondant aux carr�s
			int k = 0;
			for (int i=0; i<3; i++){
				for( int j=0; j<3; j++) {
					int ligne = 3*(id%3)+i;
					int colonne = 3*((id/3)-6)+j;
					if (_grid[ligne][colonne].getValue() == 0){
						if (ListCellules[k].getValue() != 0 ){
							// si la valeur a �t� trouv�e on la met dans le soduko
							_grid[ligne][colonne].setValue(ListCellules[k].getValue());			
						} else if (_grid[ligne][colonne].getPossibilities().size() == 0 || 
								_grid[ligne][colonne].getPossibilities().size() > ListCellules[k].getPossibilities().size()){
							// si la liste des possibles est plus courte que pr�c�demment on l'a remplace
							_grid[ligne][colonne].setPossibilities(ListCellules[k].getPossibilities());
						}
					}
					k++;
				}
			}
		}
	}
}
