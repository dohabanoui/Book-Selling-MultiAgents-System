package enset.sma.projet_sma.containers;

import enset.sma.projet_sma.agents.ConsumerAgent;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.ControllerException;

public class MainContainer {


    public static void main(String[] args) throws ControllerException {
        Runtime runtime = Runtime.instance();
        ProfileImpl profileImpl = new ProfileImpl();
        profileImpl.setParameter(ProfileImpl.GUI, "true");

        AgentContainer mainContainer = runtime.createMainContainer(profileImpl);
        mainContainer.start();
    }
}
