package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import model.Cellule;
import model.ListCellule;
import model.RequestStoE;
import model.Sudoku;

public class Environnement extends Agent {
	Sudoku sudoku = new Sudoku() ;
	
	protected void setup(){
		addBehaviour(new Init_sudoku_behaviour());
		addBehaviour(new Reader_behaviour());
		addBehaviour(new Treatment_behaviour());
	}
	
	//Ce behaviour initialize un sudoku a partir d'un fichier texte
	public class Init_sudoku_behaviour extends OneShotBehaviour{

		@Override
		public void action() {
			sudoku.loadGridFromFile("Grid1");
			sudoku.display();
		}
	}
	
	//Ce behaviour recupere les messages envoyes par Simulation, lui informant que les analyseurs sont pres a travailler
	//Puis il decoupe en 27 groupes de 9 cellules le sudoku, et les envoient aux 27 analyseurs
	public class Reader_behaviour extends CyclicBehaviour{

		@Override
		public void action() {
			ACLMessage message = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			if (message != null) {
				ACLMessage messageToAnalyser = new ACLMessage(ACLMessage.REQUEST);
				messageToAnalyser.addReceiver(new AID(RequestStoE.read(message.getContent()).getAnalyseur(), AID.ISLOCALNAME));
				
				int id = RequestStoE.read(message.getContent()).getId();
				messageToAnalyser.setConversationId(Integer.toString(id));
				
				Cellule[] ListCellules = sudoku.getListCellulesById(id);
				ListCellule listcel = new ListCellule(ListCellules);
				messageToAnalyser.setContent(listcel.toJSON());
				
				send(messageToAnalyser);
			}
		}
		
		
	}
	
	//Ce behaviour recupere les reponses des analyseurs
	//Remplace dans le sudoku les nouvelles valeurs, ou possibilites, seulement si elles sont interessantes
	public class Treatment_behaviour extends CyclicBehaviour{
		int count = 0;
		boolean ticker_initiated = false;
		boolean isDone = false;
		@Override
		public void action() {
			ACLMessage message = receive(MessageTemplate.MatchPerformative(ACLMessage.CONFIRM));
			if (message != null) {
				int id = Integer.parseInt(message.getConversationId());
				
				if(id == 0){
					isDone = sudoku.isDone();
				}
				
				if(!isDone){
					Cellule[] listCel = ListCellule.read(message.getContent()).getList();				
					sudoku.replaceInSudoku(id, listCel);
					if(id == 26)
					{
						System.out.println("Step number " + count++ + "\n");
						sudoku.display();
					}
				}else{
					sendToSim();
				}
			}
		}
		private void sendToSim() {
			ACLMessage messageToEnv = new ACLMessage(ACLMessage.CANCEL);
			messageToEnv.addReceiver(new AID("Simulation",AID.ISLOCALNAME));
			messageToEnv.setContent("stop");
			send(messageToEnv);
		}
		
	}

}
