package agents;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;
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
	// il faudra initialiser la matrice du sudoku
	
	protected void setup(){
		System.out.println(getLocalName() + "---> Installed");
		addBehaviour(new Init_sudoku_behaviour());
		addBehaviour(new Reader_behaviour());
	}
	
	public class Init_sudoku_behaviour extends OneShotBehaviour{

		@Override
		public void action() {
			try(BufferedReader br = new BufferedReader(new FileReader("Grid"))) {
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
			    		System.out.println(cel.toJSON());
			    	}
			    }
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
				System.out.println(message.getContent());
				messageToAnalyser.addReceiver(new AID(RequestStoE.read(message.getContent()).getAnalyseur(), AID.ISLOCALNAME));
				int id = RequestStoE.read(message.getContent()).getId();
				Cellule[] ListCellules = getListCellulesById(id);
				
				ListCellule listcel = new ListCellule(ListCellules);
				
				messageToAnalyser.setContent(listcel.toJSON());
				send(messageToAnalyser);
				
				// il faudra rajouter au message le liste des cellules
				// faire une fonction qui donne une série de 9 cellules correspondant à l'id du message reçu
			}
		}
		
		private Cellule[] getListCellulesById(int id) {
			System.out.println(id);
			Cellule[] ListCellules = new Cellule[9];
			if (id>=0 && id < 9) {
				for (int j =0; j < 9; j++ ) {
					ListCellules[j] = sudoku[id][j];
				}
			} else if (id < 18){
				for (int i =0; i < 9; i++ ) {
					ListCellules[i] = sudoku[i][id-9];
				}
			} else if (id < 27){
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
}
