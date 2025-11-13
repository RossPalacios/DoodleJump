package com.doodlejump.doodlejump;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class MovementPlatHandler {

    private Player player;
    private Scene scene;
    private List<Platform> platforms;  // multiple platforms

    private ArrayList<String> input;
    private double velocity;
    private final double GRAVITY = 100;
    private final double DURATION = 0.05;
    private final double REBOUND_VELOCITY = -(GRAVITY * 2);

    public MovementPlatHandler(Scene scene, Player player, List<Platform> platforms) {
        this.player = player;
        this.scene = scene;
        this.velocity = 0;
        this.platforms = platforms;
        input = new ArrayList<>();

        // handle key press/release
        scene.setOnKeyPressed(e -> {
            String code = e.getCode().toString();
            if (!input.contains(code)) input.add(code);
        });

        scene.setOnKeyReleased(e -> {
            String code = e.getCode().toString();
            input.remove(code);
        });
    }

    // start the game loop
    public void update() {
        KeyFrame loopKeyFrame = new KeyFrame(Duration.millis(16), e -> {
            updateMovement();
            checkBorders();
            checkPlatformCollision();
            scrollPlatforms(); // infinite scrolling
        });

        Timeline gLoop = new Timeline(loopKeyFrame);
        gLoop.setCycleCount(Timeline.INDEFINITE);
        gLoop.play();
    }

    private void updateMovement() {
        // move left/right
        if (input.contains("LEFT")) {
            player.setX(player.getX() - player.getSpeed());
            player.setImage("left");
        }
        if (input.contains("RIGHT")) {
            player.setX(player.getX() + player.getSpeed());
            player.setImage("right");
        }

        // gravity so set how fast you fall down
        velocity += GRAVITY * DURATION;
        player.setY(player.getY() + velocity * DURATION);
    }

    private void checkBorders() {
        // wrap horizontally
        if (player.getX() > this.scene.getWidth()) player.setX(0);
        if (player.getX() + player.getFitWidth() < 0) player.setX(this.scene.getWidth() - player.getFitWidth());

        // bottom boundary, will remove once platforms are 100% implemented since doodle
        // shouldn't be allowed to go past the bottom.
        if (player.getY() + player.getFitHeight() > this.scene.getHeight()) {
            player.setY(this.scene.getHeight() - player.getFitHeight());
            velocity = REBOUND_VELOCITY;
        }
    }

    private void checkPlatformCollision() {

        for (Platform platform : platforms) {
            if (isColliding(platform, player)) {
                if (velocity > 0 && player.getY() + player.getFitHeight() - velocity <= platform.getY()) {
                    player.setY(platform.getY() - player.getFitHeight());
                    velocity = REBOUND_VELOCITY;
                }
            }
        }
    }

    private boolean isColliding(ImageView imgOne, ImageView imgTwo) {


        // create pixel reader to check collision
        PixelReader pixelReaderOne = imgOne.getImage().getPixelReader();
        PixelReader pixelReaderTwo = imgTwo.getImage().getPixelReader();

        // if there is no pixels then there's nothing to check.
        if (pixelReaderOne == null || pixelReaderTwo == null) {
            return false;
        }

        // calculating regions which overlap
        Bounds boundOne = imgOne.getBoundsInParent();
        Bounds boundTwo = imgTwo.getBoundsInParent();

        // much faster check.
        if (!boundOne.intersects(boundTwo)) return false;


        int startX = (int) Math.max(boundOne.getMinX(), boundTwo.getMinX()); // leftmost x
        int startY = (int) Math.max(boundOne.getMinY(), boundTwo.getMinY()); // lowest y
        int endX = (int) Math.min(boundOne.getMaxX(), boundTwo.getMaxX()); // higher x
        int endY = (int) Math.min(boundOne.getMaxY(), boundTwo.getMaxY()); // higher y
        //--------------

        // really annoying but scaling is needed due to using getFitWidth/height
        double scaleXOne = imgOne.getImage().getWidth() / imgOne.getFitWidth();
        double scaleYOne = imgOne.getImage().getHeight() / imgOne.getFitHeight();
        double scaleXTwo = imgTwo.getImage().getWidth() / imgTwo.getFitWidth();
        double scaleYTwo = imgTwo.getImage().getHeight() / imgTwo.getFitHeight();

        //Looping through all pixels, very obnoxious
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {

                int x1 = (int) ((x - boundOne.getMinX()) * scaleXOne);
                int y1 = (int) ((y - boundOne.getMinY()) * scaleYOne);
                int x2 = (int) ((x - boundTwo.getMinX()) * scaleXTwo);
                int y2 = (int) ((x - boundTwo.getMinX()) * scaleYTwo);

                // make sure values are correctly in bounds.
                if (x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0)
                    continue;

                //get pixel at the correct coordinates
                int pixIntOne = pixelReaderOne.getArgb(x1, y1);
                int pixIntTwo = pixelReaderTwo.getArgb(x2, y2);

                //this gets alpha channels, whatever those are, I did have to look this up
                // since I have never made pixel perfect collision before in javaFX.
                int alphaOne = pixIntOne >> 24 & 0xFF;
                int alphaTwo = pixIntTwo >> 24 & 0xFF;

                //if they aren't transparent then they are colliding
                if (alphaOne > 0 && alphaTwo > 0) {
                    return true;
                }
            }
        }//end of for loops
        return false;
    }

    // simple infinite scrolling
    private void scrollPlatforms() {
        double threshold = 300; // Y coordinate above which player triggers scrolling
        if (player.getY() < threshold) {
            double diff = threshold - player.getY();
            player.setY(threshold);

            // move all platforms down
            for (Platform p : platforms) {
                p.setY(p.getY() + diff);

                // if a platform goes to the bottom off the screen then it moves to top with random X
                if (p.getY() > 700) {
                    p.setY(0);
                    p.setX(Math.random() * (400 - p.getFitWidth()));
                }
            }
        }
    }
}
