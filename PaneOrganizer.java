package pacman;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.awt.*;

import static javafx.scene.input.KeyCode.F;

public class PaneOrganizer {
    private BorderPane root;
    private Pane gamePane;
    private HBox controlPane;

    public PaneOrganizer(){
        this.root = new BorderPane();
       this.setUpPanes();
        //this.root.getChildren().add(pane);
        new Game(this.gamePane, this.controlPane);
        this.setQuitButton();
    }

    private void setUpPanes(){
        this.gamePane = new Pane();
        this.controlPane = new HBox();
//        this.controlPane.setLayoutX(Constants.PANE_WIDTH);
//        this.controlPane.setLayoutX(20);
        this.root.setCenter(this.gamePane);
        this.root.setBottom(this.controlPane);
    }

    private void setQuitButton(){
        Button button = new Button("Quit");//add quit button
        this.controlPane.getChildren().add(button);
        this.controlPane.setSpacing(20);
        //this.controlPane.alignmentProperty();
        this.controlPane.setStyle("-fx-background-color: #0000FF");
        button.setOnAction((ActionEvent e) -> System.exit(0));// quit the game
        button.setFocusTraversable(false);//avoid the quit button being clicked when key is pressed
    }


    public BorderPane getRoot(){
        return this.root;
    }
}
