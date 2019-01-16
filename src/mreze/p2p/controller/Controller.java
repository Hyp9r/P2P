package mreze.p2p.controller;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import mreze.p2p.peer.Peer;
import mreze.p2p.shutdown.ShutdownHandler;
import mreze.p2p.util.NumberUtils;

public class Controller {

    private Peer peer;

    @FXML private Button connect;
    @FXML private Button disconnect;
    @FXML private Button find;
    @FXML private TextField port;
    @FXML private TextField findTextField;
    @FXML private TextArea info;
    @FXML private ListView<String> messages;

    public void onFindClick(){

    }

    public void onConnectClick() throws Exception {
        Integer port;

        if((port = NumberUtils.parseInt(this.port.getText())) == null) {
            messages.getItems().add("Port mora biti Integer!");
            return;
        }

        this.peer = new Peer(port, this);
        Runtime.getRuntime().addShutdownHook(new ShutdownHandler(peer));

        this.info.setText("Guid: " + this.peer.getGuid()
        + "\nAddress: " + this.peer.getAddressPair().getIpAddress().getHostAddress()
        + "\nPort: " + this.peer.getAddressPair().getPort());
    }

    public void onDisconnectClick() {
        System.exit(0);
    }

    public void sendMessageInfo(String message) {
        Platform.runLater(() -> this.messages.getItems().add(message));
    }

    public void initialize() {
        this.messages.getSelectionModel().selectFirst();
        this.messages.getItems().add("Message Info");
        this.messages.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        BooleanBinding connectBind = this.port.textProperty().isEmpty();
        this.connect.disableProperty().bind(connectBind);
    }
}
