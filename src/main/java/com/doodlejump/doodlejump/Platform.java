package com.doodlejump.doodlejump;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Platform extends ImageView{

    public Platform() {
        int PLATFORM_HEIGHT = 20;
        int PLATFORM_WIDTH = 100;

        Image platForm = new Image(getClass().getResource("/Images/simple-platform.png").toExternalForm());
        this.setImage(platForm);
        this.setFitHeight(PLATFORM_HEIGHT);
        this.setFitWidth(PLATFORM_WIDTH);
        this.setX(50);
        this.setY(100);
    }
}
