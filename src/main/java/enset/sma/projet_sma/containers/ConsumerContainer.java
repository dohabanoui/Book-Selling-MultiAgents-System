package enset.sma.projet_sma.containers;

import enset.sma.projet_sma.agents.ConsumerAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ConsumerContainer extends Application {

    private ConsumerAgent consumerAgent;
    private ObservableList<String> observableList;
    private String livre;


    public static void main(String[] args) throws StaleProxyException {
        launch(args);
    }


    public void startContainer() throws Exception{
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN, "localhost");
        AgentContainer container = runtime.createAgentContainer(profile);
        AgentController controller = container.createNewAgent("Consumer", "enset.sma.projet_sma.agents.ConsumerAgent", new Object[]{this});
        controller.start();

    }

    @Override
    public void start(Stage stage) throws Exception {
        startContainer();
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10));
        hBox.setSpacing(10);
        Label label = new Label("Livre:");
        TextField textField = new TextField();
        Button button = new Button("Acheter");
        hBox.getChildren().addAll(label, textField, button);
        BorderPane borderPane = new BorderPane();


        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));

        observableList = FXCollections.observableArrayList();
        ListView<String> listViewMessages = new ListView<>(observableList);
        vBox.getChildren().add(listViewMessages);

        borderPane.setTop(hBox);
        borderPane.setCenter(vBox);
        Scene scene = new Scene(borderPane, 600,400);
        stage.setScene(scene);
        stage.setTitle("Consumer");
        stage.show();


        button.setOnAction(actionEvent -> {
            livre = textField.getText();
            GuiEvent event = new GuiEvent(this, 1);
            event.addParameter(livre);
            consumerAgent.onGuiEvent(event);
        });

    }

    public void setConsumerAgent(ConsumerAgent consumerAgent) {
        this.consumerAgent = consumerAgent;
    }


    public void logMessage(ACLMessage aclMessage){
        Platform.runLater(()->{
            observableList.add(livre + " was " + aclMessage.getContent());
        });


    }
}
