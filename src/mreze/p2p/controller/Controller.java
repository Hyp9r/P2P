package mreze.p2p.controller;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mreze.p2p.peer.Peer;
import mreze.p2p.shutdown.ShutdownHandler;
import mreze.p2p.util.NumberUtils;

import java.io.File;

public class Controller {

    private Peer peer;

    @FXML private Button connect;
    @FXML private Button disconnect;
    @FXML private Button find;
    @FXML private TextField port;
    @FXML private TextField findTextField;
    @FXML private TextArea info;
    @FXML private ListView<String> messages;
    @FXML private Label fileName;

    public void onFindClick(){
        Integer guid;

        if((guid = NumberUtils.parseInt(this.findTextField.getText())) == null){
            messages.getItems().add("Find varijabla mora biti Integer!");
            return;
        }
        this.peer.getPeerDHT().find((byte) guid.intValue());
    }

    public void onConnectClick() throws Exception {
        Integer port;

        if((port = NumberUtils.parseInt(this.port.getText())) == null) {
            Platform.runLater(() -> this.messages.getItems().add("Port mora biti Integer!"));
            return;
        }

        this.peer = new Peer(port, this, fileName.getText());
        Runtime.getRuntime().addShutdownHook(new ShutdownHandler(peer));

        updatePeerInfo();

    }

    @FXML protected void locateFile(ActionEvent event) {
        //Node node = (Node) event.getSource();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select file to be shared");
        File file = chooser.showOpenDialog(new Stage());
        Platform.runLater(() -> fileName.setText(file.getName()));
        //chooser.showOpenDialog(node.getScene().getWindow());
    }

    public void onDisconnectClick() {
        System.exit(0);
    }

    public void sendMessageInfo(String message) {
        Platform.runLater(() -> this.messages.getItems().add(message));
    }

    private void updatePeerInfo(){
        Platform.runLater(() -> this.info.setText(("Guid: " + this.peer.getGuid()
                + "\nFile: " + this.peer.getFileName()
                + "\nAddress: " + this.peer.getAddressPair().getIpAddress().getHostAddress()
                + "\nPort: " + this.peer.getAddressPair().getPort())));
    }

    public void initialize() {
        this.messages.getSelectionModel().selectFirst();
        this.messages.getItems().add("Message Info");
        this.messages.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        BooleanBinding connectBind = this.port.textProperty().isEmpty();
        this.connect.disableProperty().bind(connectBind);
    }
}
