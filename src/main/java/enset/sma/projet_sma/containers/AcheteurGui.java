package enset.sma.projet_sma.containers;

import enset.sma.projet_sma.agents.AcheteurAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AcheteurGui extends Application {
    protected AcheteurAgent acheteurAgent;
    private ObservableList<String> observableList;

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        startContainer();

        BorderPane borderPane = new BorderPane();
        VBox vBox = new VBox();
        observableList = FXCollections.observableArrayList();
        ListView<String> listView = new ListView<String>(observableList);
        vBox.getChildren().add(listView);
        borderPane.setCenter(vBox);

        Scene scene = new Scene(borderPane, 400, 300);
        stage.setScene(scene);
        stage.setTitle("Acheteur");
        stage.show();




    }

    private void startContainer() throws Exception{
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        AgentContainer container = runtime.createAgentContainer(profile);
        AgentController controller = container.createNewAgent("Acheteur", "enset.sma.projet_sma.agents.AcheteurAgent", new Object[]{this});
        controller.start();
    }


    public void logMessage(ACLMessage aclMessage){
        Platform.runLater(()->{
            observableList.add(aclMessage.getPerformative()+": "+aclMessage.getContent());
        });


    }

    public void setAcheteurAgent(AcheteurAgent acheteurAgent) {
        this.acheteurAgent = acheteurAgent;
    }
}
