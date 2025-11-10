package com.doodlejump.doodlejump;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Player extends ImageView {

    private double speed;

    public Player(double speed) {
        int PLAYER_WIDTH_HEIGHT = 100;
        this.speed = speed;

        Image doodleGuy = new Image(getClass().getResource("/Images/leftDood.png").toExternalForm());
        this.setImage(doodleGuy);
        this.setFitWidth(PLAYER_WIDTH_HEIGHT);
        this.setFitHeight(PLAYER_WIDTH_HEIGHT);
        this.setX(50);
        this.setY(50);
    }

    public double getSpeed() {
        return this.speed;
    }
    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
