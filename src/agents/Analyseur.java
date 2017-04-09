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
				ACLMessage reply = message.createReply();
				reply.setPerformative(ACLMessage.CONFIRM);
				
				Cellule[] listCel = ListCellule.read(message.getContent()).getList();
				ListCellule tmp = new ListCellule(listCel);
				
				FirstFilter(listCel);
				SecondFilter(listCel);
				ThirdFilter(listCel);			
				FourthFilter(listCel);
				
				tmp.setList(listCel);
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
						list[i].setPossibilities(new LinkedList<Integer>());
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
		
		//Cherche les valeurs des possibles uniques
		private void ThirdFilter(Cellule[] list){
			
			//on parcours les cellules
			for(int i = 0; i < 9; i++)
			{
				// on parcours les possibilités
				for(int j = 0; j< list[i].getPossibilities().size(); j++)
				{
					int value = list[i].getPossibilities().get(j);
					boolean isAlone = true;
					
					for(int k = 0; k < 9; k++){
						if(k!=i && list[k].containsPossibility(value)){
							isAlone = false;
							break;
						}
					}
					if(isAlone){
						//si la possibilité est unique, la valeur de la cellule prend pour valeur cette unique possibilit�
						list[i].setValue(value);
						// On vide la liste de cette cellule
						list[i].setPossibilities(new LinkedList<Integer>());
						break;
					}
				}
				
			}
		}
		//Cherche les listes de possibilités identiques
		private void FourthFilter(Cellule[] list){
			//on parcours les cellules
			for(int i = 0; i < 9; i++)
			{
				if(list[i].getPossibilities().size() == 2)
				{
					for(int j = 0; j<9; j++)
					{
						if(j!=i && list[i].containsSamePossibilities(list[j].getPossibilities()))
						{
							for(int delIndex = 0; delIndex < 9; delIndex++)
								if(delIndex != j && delIndex!=i)
									list[delIndex].getPossibilities().removeAll(list[i].getPossibilities());
						}					
					}
				}

			}
		}
	}

}
