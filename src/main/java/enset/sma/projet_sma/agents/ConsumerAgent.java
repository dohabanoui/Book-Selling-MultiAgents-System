package enset.sma.projet_sma.agents;

import enset.sma.projet_sma.containers.ConsumerContainer;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;


public class ConsumerAgent extends GuiAgent {

    private transient ConsumerContainer gui; // transient insures that the attribute will not be serialized
    @Override
    protected void setup() {

        if(getArguments().length == 1){
            gui = (ConsumerContainer) getArguments()[0];
            gui.setConsumerAgent(this);
        }


        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);

        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage aclMessage = receive();
                if(aclMessage != null ){
                    switch (aclMessage.getPerformative()){
                        case ACLMessage.INFORM:
                            gui.logMessage(aclMessage);
                            break;

                        default:
                            break;

                    }

                }else block();
            }
        });


    }


    @Override
    public void onGuiEvent(GuiEvent guiEvent) {
        if(guiEvent.getType()==1){
            String livre = (String) guiEvent.getParameter(0);
            ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
            aclMessage.setContent(livre);
            aclMessage.addReceiver(new AID("Acheteur", AID.ISLOCALNAME));
            send(aclMessage);
        }

    }
}
