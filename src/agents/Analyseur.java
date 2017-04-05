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
		ACLMessage sub = new ACLMessage(ACLMessage.SUBSCRIBE);
		sub.addReceiver(new AID("Simulation",AID.ISLOCALNAME));
		send(sub);
		
		addBehaviour(new Analyze_Behaviour());
	}
	
	public class Analyze_Behaviour extends CyclicBehaviour{

		@Override
		public void action() {
			ACLMessage message = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
			if (message != null) {
//				int id = Integer.parseInt(message.getConversationId());
//				System.out.println("Je suis l'analyseur :" + getLocalName());
//				System.out.println("MESSAGE : " + message.getContent());
				
				ACLMessage reply = message.createReply();
				reply.setPerformative(ACLMessage.CONFIRM);
				
				Cellule[] listCel = ListCellule.read(message.getContent()).getList();
				ListCellule tmp = new ListCellule(listCel);
				FirstFilter(listCel);
				tmp.setList(listCel);
//				System.out.println("TEST 1 : " + tmp.toJSON());
				SecondFilter(listCel);
				tmp.setList(listCel);
//				System.out.printf(" List annalys� " + id + "  : ");
//				System.out.println(tmp.toJSON());
				
				reply.setContent(tmp.toJSON());
				send(reply);
				
			}
		}
		
		//Premier traitement de la liste
		private void FirstFilter(Cellule[] list){
			for(int i = 0; i < 9; i++){
				if(list[i].getValue() == 0){
					if(list[i].getPossibilities().size() == 0){
						//si pas de valeur et pas de possibilit� d�fini, on inilialize les possibilit�s � [1,2,3,4,5,6,7,8,9]
						LinkedList<Integer> temp = new LinkedList<Integer>();
						for(int k = 1 ; k < 10 ; k++)
						{
							temp.add(k-1, k);
						}
						list[i].setPossibilities(temp);
					}else if(list[i].getPossibilities().size() == 1){
						//si il y a une seule possibilit�s, la valeur de la cellule prend pour valeur cette unique possibilit�
						list[i].setValue(list[i].getPossibilities().get(0));
					}
				}
			}
		}
		
		//Deuxi�me traitement de la liste
		private void SecondFilter(Cellule[] list){
			List<Integer> listValue = new LinkedList<Integer>();
			//on met dans la variable listValue la liste des valeurs d�j� d�finies
			for(int count = 0; count < 9; count++){
				if(list[count].getValue() != 0)
					listValue.add(list[count].getValue());
			}
			//Pour chaque cellules de la liste, n'ayant pas de valuer,
			//on supprime les possibilit�s qui sont d�j� pr�sente dans cette liste 
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
	}

}
