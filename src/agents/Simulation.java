package agents;

import java.util.LinkedList;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import model.RequestStoE;

public class Simulation extends Agent{
	List<AID> listAnalyseur = new LinkedList<AID>();
	protected void setup(){
		addBehaviour(new Is_Ready_Behaviour());
		addBehaviour(new Is_Done_Behaviour());
	}
	
	//Ce behaviour recoit les souscriptions des 27 analyseurs
	//Une fois tous recut, il cree le behaviour MyTickerBehaviour
	//Et se termine
	public class Is_Ready_Behaviour extends Behaviour{
		boolean IsDone = false;
		int count = 27;
		@Override
		public void action() {
			ACLMessage message = receive(MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE));
			if(message!=null)
			{
				listAnalyseur.add(message.getSender());
				if(listAnalyseur.size() == count){
					IsDone = true;
					addBehaviour(new My_Ticker_Behaviour(this.getAgent(),500));
				}
			}
		}

		@Override
		public boolean done() {
			return IsDone;
		}
	
	}
	
	public class Is_Done_Behaviour extends CyclicBehaviour
	{

		@Override
		public void action() {
			ACLMessage message = receive(MessageTemplate.MatchPerformative(ACLMessage.CANCEL));
			if(message!=null)
			{
				System.out.println("Le Sudoku est résolu");
		        this.getAgent().doSuspend();
			}
			
		}
		
	}
	
	//Ce behaviour envoie tous les n ms 27 messages a l'environnement 
	//Avec les coordonnées de chaque analyseur
	public class My_Ticker_Behaviour extends TickerBehaviour{
		private static final long serialVersionUID = 1L;
		public My_Ticker_Behaviour(Agent a, long period) {
			super(a, period);
		}

		@Override
		protected void onTick() {
			System.out.println("onTick");
			for(int i = 0; i < listAnalyseur.size(); i++)
			{
				sendToEnv(listAnalyseur.get(i).getLocalName(), i);
			}
		}
		
		private void sendToEnv(String analyseur, int id) {
			ACLMessage messageToEnv = new ACLMessage(ACLMessage.INFORM);
			messageToEnv.addReceiver(new AID("Environnement",AID.ISLOCALNAME));
			messageToEnv.setContent(new RequestStoE(analyseur, id).toJSON());
			send(messageToEnv);
		}
		
	}
}
