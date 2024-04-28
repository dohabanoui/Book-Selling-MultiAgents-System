package enset.sma.projet_sma.agents;

import enset.sma.projet_sma.containers.VendeurGui;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

import java.util.Random;

public class VendeurAgent extends GuiAgent {
    protected VendeurGui gui;

    @Override
    protected void setup() {
        if(getArguments().length == 1){
            gui = (VendeurGui)  getArguments()[0];
            gui.setVendeurAgent(this);
        }


        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);

        parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                DFAgentDescription agentDescription = new DFAgentDescription();
                agentDescription.setName(getAID());
                ServiceDescription serviceDescription = new ServiceDescription();
                serviceDescription.setType("transaction");
                serviceDescription.setName("vente-livres");
                agentDescription.addServices(serviceDescription);
                try {
                    DFService.register(myAgent, agentDescription);
                } catch (FIPAException e) {
                    throw new RuntimeException(e);
                }


            }
        });

        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage aclMessage = receive();
                if(aclMessage != null ){
                    gui.logMessage(aclMessage);
                    switch (aclMessage.getPerformative()){
                        case ACLMessage.CFP:
                            ACLMessage reply = aclMessage.createReply();
                            reply.setPerformative(ACLMessage.PROPOSE);
                            reply.setContent(String.valueOf(80 + new Random().nextInt(50)));
                            send(reply);
                            break;
                        case ACLMessage.ACCEPT_PROPOSAL:
                            ACLMessage reply2 = aclMessage.createReply();

                            // decide whether to accept proposal or not
                            Random random = new Random();
                            double randomNumber = random.nextDouble();
                            boolean accept = randomNumber < 0.7;

                            if(accept){
                                reply2.setContent(aclMessage.getContent());
                                reply2.setPerformative(ACLMessage.AGREE);
                            }else{
                                reply2.setPerformative(ACLMessage.REFUSE);
                            }

                            send(reply2);
                            break;
                        default:
                            break;
                    }
                    /*ACLMessage reply = aclMessage.createReply();
                    reply.setContent("Proposal received!");
                    send(reply);*/
                }else block();
            }
        });


    }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }
    }
}
