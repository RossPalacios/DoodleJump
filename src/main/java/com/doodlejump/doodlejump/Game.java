//Ross Palacios, Adrianna Barcena, Alan Moore, Ashlyn Kasper
//11/13/2025
//CSCI 3331, Garcia
//
//Game class for doodle jump
//
//
//Run the games different classes together to make doodlejump.
// TODO:
//  platform overhaul/extra platforms
//  score
package com.doodlejump.doodlejump;

import javafx.scene.Scene;
import javafx.scene.control.Button;
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
    private Button quitBtn;
    private int score;

    /**
     * Create the game given a stage
     *
     * @param primaryStage the primary stage
     */
    public Game(Stage primaryStage) {
        double playerSpeedTemp = 5;
        this.score = 0;

        this.primaryStage = primaryStage;
        this.root = new Pane();
        this.player = new Player(playerSpeedTemp);
        Image bckImage = new Image(getClass().getResource("/Images/background.png").toExternalForm());
        this.backGround = new ImageView(bckImage);

        // create multiple platforms
        this.platforms = new ArrayList<>();

        initializeQuitButton(100, 50);
    }

    public void addToScore() {
        this.score++;
    }

    public Pane getRoot() {
        return this.root;
    }

    /**
     * Start the game and it's game loop.
     */
    public void startGame() {
        Scene scene = new Scene(root, 400, 700);

        primaryStage.setTitle("Doodle Jump");
        primaryStage.setScene(scene);
        primaryStage.show();

        root.getChildren().add(backGround); // adding background before player so it's behind

        root.getChildren().add(player); // add player

        initializePlatforms();


        // create handler for movement, collisions, and scrolling
        MovementPlatHandler handler = new MovementPlatHandler(scene, player, platforms, this);
        handler.update();

        root.getChildren().add(quitBtn); // add the quit button
        root.requestFocus(); // make sure keys still work

    }

    private void initializePlatforms() {
        int numOfPlatforms = 10;
        double spacing = 700.0 / numOfPlatforms;

        for (int i = 0; i < numOfPlatforms; i++) {
            double x = Math.random() * (400 - 100); // get random x, using screen and platform width
            double y = i * spacing;

            Platform p = new Platform(x, y);

            String[] types = {"normal", "bouncy", "breakable","moving"};
            String type = types[(int)(Math.random() * types.length)];
            p.setupImage(type);

            platforms.add(p);
            root.getChildren().add(p);
        }
    }

    /**
     * delete all platforms
     */
    private void deletePlatforms() {
        for (Platform p : platforms) {
            this.root.getChildren().remove(p);
        }
    }

    /**
     * End the game and make a game over screen with a quit button.
     */
    public void endGame() {
        //initializing bottom breaking of screen.
        ImageView bottomCrease = new ImageView(new Image(getClass().getResource("/Images/gameoverbottom.png").toExternalForm()));
        ImageView gameOverTitle = new ImageView(new Image(getClass().getResource("/Images/gameover.png").toExternalForm()));
        ImageView quit = new ImageView(new Image(getClass().getResource("/Images/done.png").toExternalForm()));
        //just manually set the position for the crinkling
        bottomCrease.setFitWidth(450);
        bottomCrease.setFitHeight(200);
        bottomCrease.setY(550);
        bottomCrease.setX(-10);
        //------------------------------

        // moving quit button to the center
        this.quitBtn.setPrefSize(250, 100);
        this.quitBtn.setMinSize(250, 100);
        this.quitBtn.setMaxSize(250, 100);


        this.quitBtn.setLayoutX(75);
        this.quitBtn.setLayoutY((this.root.getHeight() + quit.getFitHeight()) / 2);
        //---------------------------------

        this.root.getChildren().add(gameOverTitle);
        this.root.getChildren().add(bottomCrease);

        deletePlatforms();
    }

    /**
     * Create the initial quit button in the corner.
     */
    public void initializeQuitButton(int width, int height) {
        ImageView tempImg = new ImageView(new Image(getClass().getResource("/Images/done.png").toExternalForm()));
        tempImg.setPreserveRatio(true); // make sure there's no infinite scaling

        this.quitBtn = new Button();
        this.quitBtn.setGraphic(tempImg);
        this.quitBtn.setStyle("-fx-background-color: transparent;");
        this.quitBtn.setPrefSize(width, height);
        this.quitBtn.setMinSize(width, height);
        this.quitBtn.setMaxSize(width, height);

        tempImg.fitWidthProperty().bind(this.quitBtn.widthProperty()); // bind image to button
        tempImg.fitHeightProperty().bind(this.quitBtn.heightProperty());

        this.quitBtn.setOnAction(e -> {
            primaryStage.close();
        });
    }
}


