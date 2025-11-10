package com.doodlejump.doodlejump;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Game {
    private Stage primaryStage;
    private Pane root;
    private Player player;

    public Game(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.root = new Pane();
        this.player = new Player();
    }

    public void startGame() {
        Scene scene = new Scene(root, 400, 700);

        primaryStage.setTitle("Doodle Jump");
        primaryStage.setScene(scene);
        primaryStage.show();


        this.root.getChildren().add(player);
    }
}
