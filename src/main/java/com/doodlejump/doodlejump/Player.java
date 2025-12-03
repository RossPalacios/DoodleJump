package com.doodlejump.doodlejump;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Player extends ImageView {

    private double speed, previousY;

    /**
     * the default constructor for the player
     */
    public Player() {
        int PLAYER_WIDTH_HEIGHT = 100;
        int PLAYER_SPEED = 5; // the manually found speed, I never needed to change it so it's not changeable outside of player for now
        this.speed = PLAYER_SPEED;

        Image doodleGuy = new Image(getClass().getResource("/Images/leftDood.png").toExternalForm());
        this.setImage(doodleGuy);
        this.setFitWidth(PLAYER_WIDTH_HEIGHT);
        this.setFitHeight(PLAYER_WIDTH_HEIGHT);
        this.setX(50);
        this.setY(200);
    }

    /**
     * get the player speed, which doesn't change
     * @return the player speed
     */
    public double getSpeed() {
        return this.speed;
    }

    /**
     * get the previous y of the player for landing
     * @return the previous y
     */
    public double getPreviousY() {
        return this.previousY;
    }

    /**
     * set the previous y of the player
     * @param previousY the previous sy value to set
     */
    public void setPreviousY(double previousY) {
        this.previousY = previousY;
    }

    /**
     * set the image of the player
     * @param image the image to set, given a string value
     */
    public void setImage(String image){
        if(image.equals("left")){
            this.setImage(new Image(getClass().getResource("/Images/leftDood.png").toExternalForm()));
        }
        if(image.equals("right")){
            this.setImage(new Image(getClass().getResource("/Images/rightDood.png").toExternalForm()));
        }
    }
}
