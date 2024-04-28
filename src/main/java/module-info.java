module enset.sma.projet_sma {
    requires javafx.controls;
    requires javafx.fxml;
    requires jade;


    opens enset.sma.projet_sma to javafx.fxml;
    exports enset.sma.projet_sma.containers;
    exports enset.sma.projet_sma.agents;
}