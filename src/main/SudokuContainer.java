package main;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class SudokuContainer {
	public static String SECONDARY_PROPERTIES = "properties_1";
	public static ContainerController cc = null;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Runtime rt = Runtime.instance();
		Profile p = null;
		try{
			p = new ProfileImpl(SECONDARY_PROPERTIES);
			cc = rt.createAgentContainer(p);
			AgentController ac = cc.createNewAgent("Simulation", "agents.Simulation", null);
			ac.start();
			ac = cc.createNewAgent("Environnement", "agents.Environnement", null);
			ac.start();
			for(int i = 1; i<=27; i++)
			{
				ac = cc.createNewAgent("An" + i, "agents.Analyseur", null);
				ac.start();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();	
		}
	}

}
