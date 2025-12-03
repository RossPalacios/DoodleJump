//Ross Palacios, Adrianna Barcena, Alan Moore, Ashlyn Kasper
//11/13/2025
//CSCI 3331, Garcia
//
//Game class for doodle jump
//
//
//Run the games different classes together to make doodlejump.
// TODO:
//  add ramping difficulty, not choosing though thats too much work
package com.doodlejump.doodlejump;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Game {

    // necessary objects for the game
    private Stage primaryStage;
    private Scene scene;
    private Pane root;
    private Player player;
    private ImageView backGround;
    private Button quitBtn, restartBtn;

    private List<Platform> platforms; // multiple platforms instead of just the test ones
    private Platform firstPlat; // initial platform

    // score related:
    private Label scoreLbl;
    private int score;

    // restart logic
    private boolean restarted;

    /**
     * Create the game given a stage
     *
     * @param primaryStage the primary stage
     */
    public Game(Stage primaryStage) {

        this.score = 0;

        this.player = new Player();
        this.restarted = false;

        // stage and background
        this.primaryStage = primaryStage;
        this.root = new Pane();
        this.scene = new Scene(root, 400, 700);

        Image bckImage = new Image(getClass().getResource("/Images/background.png").toExternalForm());
        this.backGround = new ImageView(bckImage);
        //-----------------------------

        // create multiple platforms
        this.platforms = new ArrayList<>();

        // create first platform for generation.
        this.firstPlat = new Platform(50, 400);
        this.platforms.add(this.firstPlat);

        initializeQuitButton(100, 50);

        // create and place score
        this.scoreLbl = new Label();
        this.scoreLbl.setText("Score: 0");
        this.scoreLbl.setLayoutX(335);
    }

    /**
     * get whether or not the game has been restarted
     * @return whether or not its restarted
     */
    public boolean getRestarted() {
        return this.restarted;
    }

    /**
     * set the gamestate back to normal once fully restarted
     * @param restarted the boolean to set
     */
    public void setRestarted(boolean restarted) {
        this.restarted = restarted;
    }

    public void addToScore() {
        this.score++;
        this.scoreLbl.setText("Score: " + this.score);
    }

    /**
     * Start the game and it's game loop.
     */
    public void startGame() {

        primaryStage.setTitle("Doodle Jump");
        primaryStage.setScene(scene);
        primaryStage.show();

        root.getChildren().add(backGround); // adding background before player so it's behind

        root.getChildren().add(player); // add player

        root.getChildren().add(firstPlat); // add the top platform

        root.getChildren().add(scoreLbl); // add the score

        generatePlatforms();


        // create handler for movement, collisions, and scrolling
        MovementPlatHandler handler = new MovementPlatHandler(scene, player, platforms, this);
        handler.update();

        root.getChildren().add(quitBtn); // add the quit button
        root.requestFocus(); // make sure keys still work

    }

    /**
     * most logic for restarting the game goes here.
     */
    private void restartGame() {

        this.restarted = true;

        // re-add platforms
        enablePlatforms();

        setupExtrasDependantOnGameState();

        // disable the restart button
        this.restartBtn.setVisible(false);
        this.restartBtn.setDisable(true);

        //fix the quit button back to its original state
        this.quitBtn.setPrefSize(100, 50);
        this.quitBtn.setMinSize(100, 50);
        this.quitBtn.setMaxSize(100, 50);

        this.quitBtn.setLayoutX(0);
        this.quitBtn.setLayoutY(0);
        //----------------------------------------------

        // set score to original state
        this.scoreLbl.setLayoutX(335);
        this.scoreLbl.setLayoutY(0);
        this.scoreLbl.setText("Score: 0");
        //----------------------------
    }

    /**
     * End the game and make a game over screen with a quit button.
     */
    public void endGame() {

        setupExtrasDependantOnGameState();

        ImageView quit = new ImageView(new Image(getClass().getResource("/Images/done.png").toExternalForm()));

        initializeRestartButton(200, 100); // restart button appears once game is over

        // moving quit button to the center, once again manually, along with score and restart button
        this.quitBtn.setPrefSize(200, 100);
        this.quitBtn.setMinSize(200, 100);
        this.quitBtn.setMaxSize(200, 100);


        this.quitBtn.setLayoutX(100);
        this.quitBtn.setLayoutY((this.root.getHeight() + quit.getFitHeight()) / 2); // just centers the y


        this.scoreLbl.setLayoutX(175); // centered manually mostly.
        this.scoreLbl.setLayoutY(this.root.getHeight() / 2 - 20);
        //---------------------------------

        // handling restart button
        this.restartBtn.setVisible(true);
        this.restartBtn.setDisable(false);

        this.restartBtn.setLayoutX(100);
        this.restartBtn.setLayoutY(this.quitBtn.getLayoutY() + this.restartBtn.getPrefHeight());
        //-----------------------

        this.root.getChildren().add(restartBtn);

        disablePlatforms();

    }

    private void setupExtrasDependantOnGameState(){
        //initializing bottom breaking of screen.
        ImageView bottomCrease = new ImageView(new Image(getClass().getResource("/Images/gameoverbottom.png").toExternalForm()));
        ImageView gameOverTitle = new ImageView(new Image(getClass().getResource("/Images/gameover.png").toExternalForm()));

        if(!this.restarted) { // changed logic for restarts.

            bottomCrease.setVisible(true);
            gameOverTitle.setVisible(true);

            //just manually set the position for the crinkling
            bottomCrease.setFitWidth(450);
            bottomCrease.setFitHeight(200);
            bottomCrease.setY(550);
            bottomCrease.setX(-10);
            //------------------------------

            this.root.getChildren().add(gameOverTitle);
            this.root.getChildren().add(bottomCrease);
        }else{
            bottomCrease.setVisible(false);
            gameOverTitle.setVisible(false);
        }
    }

    /**
     * Generate the platforms continuously and randomly.
     */
    private void generatePlatforms() {
        Platform topPlat = this.firstPlat;
        // offsets
        int xOffset = 175;
        int yOffsetMin = 80;
        int yOffsetMax = 110;
        //-----------------

        while (topPlat.getY() > -200) { // I just fiddled around to get -200, the rest was given in the assignment
            double lowX = Math.max(0, topPlat.getX() - xOffset);
            double highX = Math.min(this.root.getWidth() - topPlat.getPlatformWidth(), topPlat.getX() + xOffset);
            double randX = Math.random() * (highX - lowX) + lowX;

            double lowY = topPlat.getY() - yOffsetMin;
            double highY = topPlat.getY() - yOffsetMax;
            double randY = Math.random() * (highY - lowY) + lowY;

            Platform newPlat = new Platform(randX, randY);
            this.platforms.add(newPlat);
            this.root.getChildren().add(newPlat);
            topPlat = newPlat;

        }
    }

    /**
     * disable all platforms
     */
    private void disablePlatforms() {
        for (Platform p : platforms)
            p.setVisible(false);
    }

    /**
     * re-enable all platforms
     */
    private void enablePlatforms() {
        for (Platform p : platforms)
            p.setVisible(true);
    }

    /**
     * Create the initial quit button in the corner.
     */
    private void initializeQuitButton(int width, int height) {

        ImageView tempImg = new ImageView(new Image(getClass().getResource("/Images/done.png").toExternalForm()));
        tempImg.setPreserveRatio(true); // make sure there's no infinite scaling

        // lot of initialization things so I gave this its own method

        this.quitBtn = new Button();

        this.quitBtn.setGraphic(tempImg);
        this.quitBtn.setStyle("-fx-background-color: transparent;");

        this.quitBtn.setPrefSize(width, height);
        this.quitBtn.setMinSize(width, height); // min and max needed, had to force it pretty much
        this.quitBtn.setMaxSize(width, height);

        tempImg.fitWidthProperty().bind(this.quitBtn.widthProperty()); // bind image to button
        tempImg.fitHeightProperty().bind(this.quitBtn.heightProperty());

        this.quitBtn.setOnAction(e -> {
            primaryStage.close();
        });
    }

    /**
     * create the restart button
     *
     * @param width  the buttons width
     * @param height the buttons height
     */
    private void initializeRestartButton(int width, int height) {
        ImageView tempImg = new ImageView(new Image(getClass().getResource("/Images/restart.png").toExternalForm()));
        tempImg.setPreserveRatio(true);

        //initializing the same way as the quit button
        this.restartBtn = new Button();
        this.restartBtn.setGraphic(tempImg);
        this.restartBtn.setStyle("-fx-background-color: transparent;");

        this.restartBtn.setPrefSize(width, height);
        this.restartBtn.setMinSize(width, height);
        this.restartBtn.setMaxSize(width, height);

        tempImg.fitWidthProperty().bind(this.restartBtn.widthProperty());
        tempImg.fitHeightProperty().bind(this.restartBtn.heightProperty());

        this.restartBtn.setOnAction(e -> {
            restartGame();
        });
    }
}


