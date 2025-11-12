package com.doodlejump.doodlejump;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private Stage primaryStage;
    private Pane root;
    private Player player;
    private List<Platform> platforms; // multiple platforms instead of the just test ones

    public Game(Stage primaryStage) {
        double playerSpeedTemp = 5;

        this.primaryStage = primaryStage;
        this.root = new Pane();
        this.player = new Player(playerSpeedTemp);

        // create multiple platforms
        platforms = new ArrayList<>();
        int numberOfPlatforms = 10;
        for (int i = 0; i < numberOfPlatforms; i++) {
            Platform p = new Platform();
            p.setX(Math.random() * (400 - p.getFitWidth())); // random X for the placement of the platform
            p.setY(600 - i * 60); // spread platforms vertically
            platforms.add(p);
        }
    }

    public void startGame() {
        Scene scene = new Scene(root, 400, 700);

        primaryStage.setTitle("Doodle Jump");
        primaryStage.setScene(scene);
        primaryStage.show();

        // add player
        root.getChildren().add(player);

        // add all platforms
        for (Platform p : platforms) {
            root.getChildren().add(p);
        }

        // create handler for movement, collisions, and scrolling
        MovementHandler handler = new MovementHandler(scene, player, platforms);
        handler.update();
    }
}
