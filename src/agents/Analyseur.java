package agents;

import java.util.LinkedList;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import model.Cellule;
import model.ListCellule;

public class Analyseur extends Agent{
	
	protected void setup(){
//		System.out.println(getLocalName() + "---> Installed");
		
		ACLMessage sub = new ACLMessage(ACLMessage.SUBSCRIBE);
		sub.addReceiver(new AID("Simulation",AID.ISLOCALNAME));
		send(sub);
		
		addBehaviour(new Analyze_Behaviour());
	}
	
	public class Analyze_Behaviour extends CyclicBehaviour{

		@Override
		public void action() {
			// TODO Auto-generated method stub
			ACLMessage message = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
			if (message != null) {
				System.out.println("Je suis l'analyseur :" + getLocalName());
				ACLMessage reply = message.createReply();
				System.out.println("MESSAGE : " + message.getContent());
				Cellule[] listCel = ListCellule.read(message.getContent()).getList();
				ListCellule tmp = new ListCellule(listCel);
				FirstFilter(listCel);
				tmp.setList(listCel);
//				print_List(listCel);
				System.out.println("TEST 1 : " + tmp.toJSON());
				SecondFilter(listCel);
//				print_List(listCel);
				tmp.setList(listCel);
				System.out.println("TEST 2 : " + tmp.toJSON());
				reply.setContent(tmp.toJSON());
				send(reply);
				
			}
		}
		
		private void FirstFilter(Cellule[] list){
			for(int i = 0; i < 9; i++){
				if(list[i].getValue() == 0){
					if(list[i].getPossibilities().size() == 0){
						LinkedList<Integer> temp = new LinkedList<Integer>();
						for(int k = 1 ; k < 10 ; k++)
						{
							temp.add(k-1, k);
						}
							
						list[i].setPossibilities(temp);
					}else if(list[i].getPossibilities().size() == 1)
					{
						list[i].setValue(list[i].getPossibilities().get(0));
						list[i].setPossibilities(new LinkedList<Integer>());
					}
				}
			}
		}
		
		private void SecondFilter(Cellule[] list){
			List<Integer> listValue = new LinkedList<Integer>();
			for(int count = 0; count < 9; count++){
				if(list[count].getValue() != 0)
					listValue.add(list[count].getValue());
			}
			
			for(int i = 0; i < 9; i++){
				if(list[i].getValue() == 0 && list[i].getPossibilities().size() != 0)
				{
					List<Integer> deletedPoss = new LinkedList<Integer>();
					list[i].getPossibilities().forEach(p -> {
						if(listValue.contains(p))
						{
							deletedPoss.add(p);
						}
					});
					list[i].getPossibilities().removeAll(deletedPoss);
				}
			}
		}
		
		private void print_List(Cellule[] list){
			System.out.printf("| ");
			for (int i=0; i<9; i++){
				if (list[i].getValue() != 0){
					System.out.printf(list[i].getValue() + "  ");
				} else {
					System.out.printf(list[i].getPossibilities() + " ");
				}
			}
			System.out.println("|");		
		}
	}
	

}
