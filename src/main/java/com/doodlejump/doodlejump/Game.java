//Ross Palacios, Adrianna Barcena, Alan Moore, Ashlyn Kasper
//11/13/2025
//CSCI 3331, Garcia
//
//Game class for doodle jump
//
//
//Run the games different classes together to make doodlejump.
package com.doodlejump.doodlejump;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private Stage primaryStage;
    private Pane root;
    private Player player;
    private ImageView backGround;
    private List<Platform> platforms; // multiple platforms instead of just the test ones

    public Game(Stage primaryStage) {
        double playerSpeedTemp = 5;

        this.primaryStage = primaryStage;
        this.root = new Pane();
        this.player = new Player(playerSpeedTemp);
        Image bckImage = new Image(getClass().getResource("/Images/background.png").toExternalForm());
        this.backGround = new ImageView(bckImage);

        // create multiple platforms
        this.platforms = new ArrayList<>();
    }

    public void startGame() {
        Scene scene = new Scene(root, 400, 700);

        primaryStage.setTitle("Doodle Jump");
        primaryStage.setScene(scene);
        primaryStage.show();

        root.getChildren().add(backGround); // adding background before player so it's behind

        root.getChildren().add(player); // add player

        runPlatforms();
        // add all platforms
        for (Platform p : platforms) {
            root.getChildren().add(p);
        }

        // create handler for movement, collisions, and scrolling
        MovementPlatHandler handler = new MovementPlatHandler(scene, player, platforms, this);
        handler.update();

    }

    public void runPlatforms() {
        int numberOfPlatforms = 10;
        for (int i = 0; i < numberOfPlatforms; i++) {
            Platform p = new Platform();
            p.setX(Math.random() * (400 - p.getFitWidth())); // random X for the placement of the platform
            p.setY(600 - i * 60); // spread platforms vertically
            platforms.add(p);
        }
    }

    private void deletePlatforms(){
        for (Platform p : platforms) {
            this.root.getChildren().remove(p);
        }
    }

    public void endGame() {

        //initializing bottombreaking of screen.
        ImageView bottomCrease =  new ImageView(new Image(getClass().getResource("/Images/gameoverbottom.png").toExternalForm()));
        ImageView gameOverTitle = new ImageView( new Image(getClass().getResource("/Images/gameover.png").toExternalForm()));
        bottomCrease.setFitWidth(450);
        bottomCrease.setFitHeight(200);
        bottomCrease.setY(550);
        bottomCrease.setX(-10);
        this.root.getChildren().add(gameOverTitle);
        this.root.getChildren().add(bottomCrease);

        deletePlatforms();
    }
}


