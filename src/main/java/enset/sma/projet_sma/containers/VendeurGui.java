package enset.sma.projet_sma.containers;

import enset.sma.projet_sma.agents.AcheteurAgent;
import enset.sma.projet_sma.agents.VendeurAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
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

public class VendeurGui extends Application {
    protected VendeurAgent vendeurAgent;
    private ObservableList<String> observableList;
    private AgentContainer container;

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        startContainer();


        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10));
        Label label = new Label();
        TextField textField = new TextField();
        Button button = new Button("Deploy");
        hBox.getChildren().addAll(label, textField, button);
        BorderPane borderPane = new BorderPane();

        borderPane.setTop(hBox);
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        observableList = FXCollections.observableArrayList();
        ListView<String> listView = new ListView<String>(observableList);
        vBox.getChildren().add(listView);
        borderPane.setCenter(vBox);

        Scene scene = new Scene(borderPane, 400, 300);
        stage.setScene(scene);
        stage.setTitle("Vendeur");
        stage.show();


        button.setOnAction(actionEvent -> {
            String vendeurNom = textField.getText();
            try {
                AgentController controller = container.createNewAgent(vendeurNom, "enset.sma.projet_sma.agents.VendeurAgent", new Object[]{this});
                controller.start();
            } catch (StaleProxyException e) {
                throw new RuntimeException(e);
            }

        });




    }

    private void startContainer() throws Exception{
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        container = runtime.createAgentContainer(profile);
    }


    public void logMessage(ACLMessage aclMessage){
        Platform.runLater(()->{
            observableList.add(aclMessage.getPerformative()+": "+aclMessage.getContent());
        });


    }

    public void setVendeurAgent(VendeurAgent vendeurAgent) {
        this.vendeurAgent = vendeurAgent;
    }
}
