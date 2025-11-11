package com.doodlejump.doodlejump;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Game {
    private Stage primaryStage;
    private Pane root;
    private Player player;
    private Platform testPlatform;

    public Game(Stage primaryStage) {
        double playerSpeedTemp = 5;

        this.primaryStage = primaryStage;
        this.root = new Pane();
        this.player = new Player(playerSpeedTemp);
        this.testPlatform = new Platform();


    }

    public void startGame() {
        Scene scene = new Scene(root, 400, 700);

        primaryStage.setTitle("Doodle Jump");
        primaryStage.setScene(scene);
        primaryStage.show();


        this.root.getChildren().add(player);
        this.root.getChildren().add(testPlatform);

        //handler will deal with collision and movement
        MovementHandler handler = new MovementHandler(scene, this.player, this.testPlatform);
        handler.update();
    }
}