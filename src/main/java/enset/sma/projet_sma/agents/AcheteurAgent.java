package enset.sma.projet_sma.agents;

import enset.sma.projet_sma.containers.AcheteurGui;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

public class AcheteurAgent extends GuiAgent {

    protected AcheteurGui acheteurGui;
    private AID[] vendeurs;

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }

    @Override
    protected void setup() {
        if(getArguments().length == 1){
            acheteurGui = (AcheteurGui) getArguments()[0];
            acheteurGui.setAcheteurAgent(this);
        }


        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);
        parallelBehaviour.addSubBehaviour(new TickerBehaviour(this, 5000) {
            @Override
            protected void onTick() {
                DFAgentDescription dfAgentDescription = new DFAgentDescription();
                ServiceDescription serviceDescription = new ServiceDescription();
                serviceDescription.setType("transaction");
                serviceDescription.setName("vente-livres");
                dfAgentDescription.addServices(serviceDescription);
                try {
                    DFAgentDescription[] results = DFService.search(myAgent, dfAgentDescription);
                    vendeurs = new AID[results.length];
                    for (int i = 0; i < vendeurs.length; i++) {
                        vendeurs[i] = results[i].getName();
                    }
                } catch (FIPAException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            private int counter = 0;
            private List<ACLMessage> replies = new ArrayList<>();
            @Override
            public void action() {
                ACLMessage aclMessage = receive();

                if(aclMessage != null ){

                    switch (aclMessage.getPerformative()){
                        case ACLMessage.REQUEST :
                            String livre = aclMessage.getContent();
                            ACLMessage aclMessage1 = new ACLMessage(ACLMessage.CFP);
                            aclMessage1.setContent(livre);
                            for (AID aid: vendeurs){
                                aclMessage1.addReceiver(aid);
                            }
                            send(aclMessage1);
                            break;

                        case ACLMessage.PROPOSE:
                            ++counter;
                            replies.add(aclMessage);
                            if(counter==vendeurs.length){
                                ACLMessage meilleurOffre = replies.get(0);
                                double min = Double.parseDouble(meilleurOffre.getContent());
                                for (ACLMessage offre:replies){
                                    double price = Double.parseDouble(offre.getContent());
                                    if (price<min){
                                        meilleurOffre = offre;
                                        min = price;
                                    }


                                }
                                counter = 0;
                                replies.clear();
                                ACLMessage aclMessageAccept = meilleurOffre.createReply();
                                aclMessageAccept.setContent(meilleurOffre.getContent());
                                aclMessageAccept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                                send(aclMessageAccept);
                            }

                            break;

                        case ACLMessage.AGREE:
                            ACLMessage aclMessage2 = new ACLMessage(ACLMessage.INFORM);
                            aclMessage2.addReceiver(new AID("Consumer", AID.ISLOCALNAME));
                            aclMessage2.setContent("purchased from " + aclMessage.getSender().getLocalName() + " for a price of "+aclMessage.getContent() + "dh");
                            send(aclMessage2);
                            break;
                        case ACLMessage.REFUSE:
                            ACLMessage aclMessage3 = new ACLMessage(ACLMessage.INFORM);
                            aclMessage3.addReceiver(new AID("Consumer", AID.ISLOCALNAME));
                            aclMessage3.setContent(" not found.");
                            send(aclMessage3);

                            break;
                        default:
                    }
                    acheteurGui.logMessage(aclMessage);
                    /*String livre = aclMessage.getContent();

                    ACLMessage reply = aclMessage.createReply();
                    reply.setContent("Request received!");
                    send(reply);*/

                    /*ACLMessage aclMessage1 = new ACLMessage(ACLMessage.CFP);
                    aclMessage1.setContent(livre);
                    aclMessage1.addReceiver(new AID("Vendeur", AID.ISLOCALNAME));
                    send(aclMessage1);*/


                }else block();
            }
        });


    }
}
