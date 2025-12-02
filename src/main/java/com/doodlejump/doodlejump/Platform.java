package com.doodlejump.doodlejump;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Platform extends ImageView {

    // creating constants for different platforms.
    private final Image normalPlat = new Image(getClass().getResource("/Images/simple-platform.png").toExternalForm());
    private final Image breakablePlat = new Image(getClass().getResource("/Images/breaking-platform.png").toExternalForm());
    private final Image bouncyPlat = new Image(getClass().getResource("/Images/bouncy-platform.png").toExternalForm());
    private final int PLATFORM_HEIGHT = 20;
    private final int PLATFORM_WIDTH = 100;

    private Image platImg;
    private String type;

    public Platform(double x, double y) {
        this.type = "normal";
        this.setupImage();

        this.setFitHeight(PLATFORM_HEIGHT);
        this.setFitWidth(PLATFORM_WIDTH);
        this.setX(x);
        this.setY(y);
    }

    public Platform(String type, double x, double y) {
        this.type = type;

        this.setupImage();

        this.setFitHeight(PLATFORM_HEIGHT);
        this.setFitWidth(PLATFORM_WIDTH);
        this.setX(x);
        this.setY(y);
    }

    public String getType() {
        return this.type;
    }

    public Image getPlatformImage() {
        return platImg;

    }

    public void setPlatformImage(Image platImg) {
        this.platImg = platImg;
    }

    public void setupImage() {
        switch (this.type) {
            case "normal", "moving":
                this.setImage(this.normalPlat);
                break;
            case "breakable":
                this.setImage(this.breakablePlat);
                break;
            case "bouncy":
                this.setImage(this.bouncyPlat);
                break;
        }
    }

    public void setupImage(String type){
        this.type = type;
        setupImage();
    }

    public int getPlatformWidth(){
        return this.PLATFORM_WIDTH;
    }
    public int getPlatformHeight(){
        return this.PLATFORM_HEIGHT;
    }

    public void breakPlatform(){
        if(!this.type.equals("breakable"))
            return;

        this.setImage(null);
    }
    public void fixPlatform(){
        if(!this.type.equals("breakable"))
            return;
        this.setupImage();
    }
}
