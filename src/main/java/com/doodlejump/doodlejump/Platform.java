package com.doodlejump.doodlejump;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Platform extends ImageView{

    Image platform;

    public Platform() {
        int PLATFORM_HEIGHT = 20;
        int PLATFORM_WIDTH = 100;

        this.platform = new Image(getClass().getResource("/Images/simple-platform.png").toExternalForm());
        this.setImage(platform);
        this.setFitHeight(PLATFORM_HEIGHT);
        this.setFitWidth(PLATFORM_WIDTH);
        this.setX(100);
        this.setY(500);
    }

    public Image getPlatformImage(){
        return platform;
    }
}
