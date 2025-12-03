package com.doodlejump.doodlejump;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Platform extends ImageView {

    // creating constants for different platforms.
    private final Image normalPlat = new Image(getClass().getResource("/Images/simple-platform.png").toExternalForm()),
            breakablePlat = new Image(getClass().getResource("/Images/breaking-platform.png").toExternalForm()),
            bouncyPlat = new Image(getClass().getResource("/Images/bouncy-platform.png").toExternalForm());

    private final int PLATFORM_HEIGHT = 20, PLATFORM_WIDTH = 100;

    private String type;
    private double horizontalSpeed, speedMultiplier; // both used for moving platforms

    /**
     * parameterized constructor with a placement for the platform
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public Platform(double x, double y) {
        this.type = "normal";
        this.setupImage();
        this.horizontalSpeed = 0;
        this.speedMultiplier = 1;

        this.setFitHeight(PLATFORM_HEIGHT);
        this.setFitWidth(PLATFORM_WIDTH);
        this.setX(x);
        this.setY(y);
    }

    /**
     * parameterized constructor with a type and placement
     *
     * @param type the type of platform
     * @param x    the x coordinate
     * @param y    the y coordinate
     */
    public Platform(String type, double x, double y) {
        this.type = type;

        this.setupImage();
        this.horizontalSpeed = 0;
        this.speedMultiplier = 1;
        this.setFitHeight(PLATFORM_HEIGHT);
        this.setFitWidth(PLATFORM_WIDTH);
        this.setX(x);
        this.setY(y);
    }

    /**
     * get the horizontal speed
     *
     * @return the horizontal speed
     */
    public double getHorizontalSpeed() {
        return this.horizontalSpeed;
    }

    /**
     * set the speed of moving platforms, will not work on other types
     *
     * @param horizontalSpeed the speed inputted
     */
    public void setHorizontalSpeed(double horizontalSpeed) {
        if (!this.getType().equals("moving"))
            return;
        this.horizontalSpeed = horizontalSpeed;
    }

    /**
     * get the speed multiplier of moving platforms
     * @return the multiplier
     */
    public double getSpeedMultiplier() {
        return this.speedMultiplier;
    }

    /**
     * set the speed multiplier of moving platforms.
     * @param speedMultiplier the multiplier
     */
    public void setSpeedMultiplier(double speedMultiplier) {
        if (!this.getType().equals("moving"))
            return;
        this.speedMultiplier = speedMultiplier;
    }

    /**
     * get the type of platform
     *
     * @return the type of platform
     */
    public String getType() {
        return this.type;
    }

    /**
     * set up the image using a switch case based on it's inherent type.
     */
    public void setupImage() {
        switch (this.type) {
            case "normal":
                this.setImage(this.normalPlat);
                break;
            case "breakable":
                this.setImage(this.breakablePlat);
                break;
            case "bouncy":
                this.setImage(this.bouncyPlat);
                break;
            case "moving":
                this.horizontalSpeed = (int) (Math.random() * (8 - 4 + 1)) + 4; // max speed of 8, min of 4
                this.setImage(this.normalPlat);
                break;
        }
    }

    /**
     * Set up the image based on the type of platform inputted
     *
     * @param type the given type
     */
    public void setupImage(String type) {
        this.type = type;
        setupImage();
    }

    /**
     * simply get the width when needed
     *
     * @return the width
     */
    public int getPlatformWidth() {
        return this.PLATFORM_WIDTH;
    }

    /**
     * simply get the height when needed
     *
     * @return the height
     */
    public int getPlatformHeight() {
        return this.PLATFORM_HEIGHT;
    }

    /**
     * "break" a platform so it will not collide or be visible
     */
    public void breakPlatform() {
        if (!this.type.equals("breakable"))
            return;
        this.setImage(null);
    }

    /**
     * re-enable the previously broken platform
     */
    public void fixPlatform() {
        if (!this.type.equals("breakable"))
            return;
        this.setupImage();
    }
}
