package agents;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import model.RequestStoE;
import model.Cellule;
import model.ListCellule;

public class Environnement extends Agent {
	Cellule [ ] [ ] sudoku = new Cellule [ 9 ] [ 9 ] ;
	
	protected void setup(){
//		System.out.println(getLocalName() + "---> Installed");
		addBehaviour(new Init_sudoku_behaviour());
		addBehaviour(new Reader_behaviour());
		addBehaviour(new Treatment_behaviour());
	}
	
	public class Init_sudoku_behaviour extends OneShotBehaviour{

		@Override
		public void action() {
			try(BufferedReader br = new BufferedReader(new FileReader("Grid1"))) {
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
			    		sudoku[i][j] = cel;
//			    		System.out.println(cel.toJSON());
			    	}
			    }
			    print_sudoku();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	public class Reader_behaviour extends CyclicBehaviour{

		@Override
		public void action() {
			// TODO Auto-generated method stub
			ACLMessage message = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			if (message != null) {
				ACLMessage messageToAnalyser = new ACLMessage(ACLMessage.REQUEST);
				messageToAnalyser.addReceiver(new AID(RequestStoE.read(message.getContent()).getAnalyseur(), AID.ISLOCALNAME));
				int id = RequestStoE.read(message.getContent()).getId();
				messageToAnalyser.setConversationId(Integer.toString(id));
				Cellule[] ListCellules = getListCellulesById(id);
				
				ListCellule listcel = new ListCellule(ListCellules);
				
				messageToAnalyser.setContent(listcel.toJSON());
				send(messageToAnalyser);
				
				// RECUPERER LE MESSAGE RETOUR : C'est un INFORM
			}
		}
		
		private Cellule[] getListCellulesById(int id) {
			System.out.println(id);
			Cellule[] ListCellules = new Cellule[9];
			if (id>=0 && id < 9) {
				//ids correspondant aux lignes
				for (int j =0; j < 9; j++ ) {
					ListCellules[j] = sudoku[id][j];
				}
			} else if (id < 18){
				//ids correspondant aux colonnes
				for (int i =0; i < 9; i++ ) {
					ListCellules[i] = sudoku[i][id-9];
				}
			} else if (id < 27){
				//ids correspondant aux carrés
				int k = 0;
				for (int i=0; i<3; i++){
					for( int j=0; j<3; j++) {
						ListCellules[k++] = sudoku[3*(id%3)+i][3*((id/3)-6)+j];
					}
				}
				
			}
			return ListCellules;
		}
	}
	
	public class Treatment_behaviour extends CyclicBehaviour{

		@Override
		public void action() {
			// TODO Auto-generated method stub
			ACLMessage message = receive(MessageTemplate.MatchPerformative(ACLMessage.CONFIRM));
			if (message != null) {
				int id = Integer.parseInt(message.getConversationId());
				Cellule[] listCel = ListCellule.read(message.getContent()).getList();
				
//				ListCellule tmp = new ListCellule(listCel);
//				System.out.printf(" List annalysé " + id + "  : ");
//				System.out.println(tmp.toJSON());
				
				replaceInSudoku(id, listCel);
				System.out.println("Treatment with the id : " + id);
				print_sudoku();
			}
		}
		
		private void replaceInSudoku(int id, Cellule[] ListCellules) {
			if (id>=0 && id < 9) {
				//ids correspondant aux lignes
				for (int j =0; j < 9; j++ ) {
					if (sudoku[id][j].getValue() == 0){
						if (ListCellules[j].getValue() != 0 ){
							// si la valeur a été trouvée on la met dans le sudoku
							sudoku[id][j].setValue(ListCellules[j].getValue());
						} else if (sudoku[id][j].getPossibilities().size() == 0 || 
								sudoku[id][j].getPossibilities().size() > ListCellules[j].getPossibilities().size()){
							// si la liste des possibles est plus courte que précédemment on l'a remplace
							sudoku[id][j].setPossibilities(ListCellules[j].getPossibilities());
						}
					}
				}
			} else if (id < 18){
				//ids correspondant aux colonnes
				for (int i =0; i < 9; i++ ) {
					if (sudoku[i][id-9].getValue() == 0){
						if (ListCellules[i].getValue() != 0 ){
							// si la valeur a été trouvée on la met dans le sudoku
							sudoku[i][id-9].setValue(ListCellules[i].getValue());
						} else if (sudoku[i][id-9].getPossibilities().size() == 0 || 
								sudoku[i][id-9].getPossibilities().size() > ListCellules[i].getPossibilities().size()){
							// si la liste des possibles est plus courte que précédemment on l'a remplace
							sudoku[i][id-9].setPossibilities(ListCellules[i].getPossibilities());
						}
					}
				}
			} else if (id < 27){
				//ids correspondant aux carrés
				int k = 0;
				for (int i=0; i<3; i++){
					for( int j=0; j<3; j++) {
						int ligne = 3*(id%3)+i;
						int colonne = 3*((id/3)-6)+j;
						if (sudoku[ligne][colonne].getValue() == 0){
							if (ListCellules[k].getValue() != 0 ){
								// si la valeur a été trouvée on la met dans le sudoku
								sudoku[ligne][colonne].setValue(ListCellules[k].getValue());			
							} else if (sudoku[ligne][colonne].getPossibilities().size() == 0 || 
									sudoku[ligne][colonne].getPossibilities().size() > ListCellules[k].getPossibilities().size()){
								// si la liste des possibles est plus courte que précédemment on l'a remplace
								sudoku[ligne][colonne].setPossibilities(ListCellules[k].getPossibilities());
							}
						}
						k++;
					}
				}
			}
		}
	}	
	
	
	public void print_sudoku(){
		for (int i=0; i<9; i++){
			System.out.printf("| ");
			for (int j=0; j<9; j++){
				if (sudoku[i][j].getValue() != 0){
					System.out.printf(sudoku[i][j].getValue() + "  ");
				} else {
					System.out.printf(sudoku[i][j].getPossibilities() + " ");
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
}
