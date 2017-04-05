package agents;

import java.util.LinkedList;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import model.RequestStoE;

public class Simulation extends Agent{
	List<AID> listAnalyseur = new LinkedList<AID>();
	//Simulation that = this;
	protected void setup(){
		System.out.println(getLocalName() + "---> Installed");
		addBehaviour(new IsReadyBehaviour());
	}
	
	public class IsReadyBehaviour extends Behaviour{
		boolean IsDone = false;
		int count = 27;
		@Override
		public void action() {
			// TODO Auto-generated method stub
			ACLMessage message = receive(MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE));
			if(message!=null)
			{
				listAnalyseur.add(message.getSender());
				if(listAnalyseur.size() == count){
					IsDone = true;
					addBehaviour(new MyTickerBehaviour(this.getAgent(),500));
				}
			}
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return IsDone;
		}
	
	}
	
	public class MyTickerBehaviour extends TickerBehaviour{
		private static final long serialVersionUID = 1L;

		public MyTickerBehaviour(Agent a, long period) {
			super(a, period);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onTick() {
			// TODO Auto-generated method stub
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
