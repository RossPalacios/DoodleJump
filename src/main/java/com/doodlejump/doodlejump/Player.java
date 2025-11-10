package com.doodlejump.doodlejump;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Player extends ImageView {


    public Player() {
        int PLAYER_WIDTH_HEIGHT = 100;

        Image doodleGuy = new Image(getClass().getResource("/Images/leftDood.png").toExternalForm());
        this.setImage(doodleGuy);
        this.setFitWidth(PLAYER_WIDTH_HEIGHT);
        this.setFitHeight(PLAYER_WIDTH_HEIGHT);
        this.setX(50);
        this.setY(50);
    }
}
