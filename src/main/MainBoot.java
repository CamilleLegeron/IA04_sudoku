package main;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;

/**
 * @author ia04p005
 *
 */
public class MainBoot {
	public static String MAIN_PROPERTIES = "main_properties";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Runtime rt = Runtime.instance();
		Profile p = null;
		try{
			p = new ProfileImpl(MAIN_PROPERTIES);
			AgentContainer mc = rt.createMainContainer(p);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

}
