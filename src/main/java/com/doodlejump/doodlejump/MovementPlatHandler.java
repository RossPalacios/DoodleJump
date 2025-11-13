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

        double currentPlayerBottom = (player.getY() + player.getFitHeight()); // adding the top y with the height actually gets bottom
        double currentY = player.getY(); // this just gives the top y
        double nextY = currentY + (velocity * DURATION);
        double nextPlayerBottom = (nextY + player.getFitHeight());


        double playerLeft = player.getX();
        double playerRight = playerLeft + player.getFitWidth();

        for (Platform platform : platforms) {
            double platformTop = platform.getY();
            double platformLeft = platform.getX();
            double platformRight = platformLeft + platform.getFitWidth();

            // horizontal overlap
            boolean horizontalOverlap = playerRight > platformLeft && playerLeft < platformRight;


            boolean verticalCollision = player.getY() + player.getFitHeight() <= platformTop &&
                    nextPlayerBottom >= platformTop;

            //if (velocity > 0 && verticalCollision && horizontalOverlap) {
                if (isColliding(platform, player)) {
                    player.setY(platform.getY() - player.getFitHeight());
                    velocity = REBOUND_VELOCITY;
                    System.out.println("working");
                }
            //}
        }
    }
    private boolean isColliding(ImageView platform, ImageView player) {


        // create pixel reader to check collision
        PixelReader platformRead = platform.getImage().getPixelReader();
        PixelReader playerRead = player.getImage().getPixelReader();

        // if there is no pixels then there's nothing to check.
        if (platformRead == null || playerRead == null) return false;


        // calculating regions which overlap
        Bounds boundPlay = player.getBoundsInParent();
        Bounds boundPlat = platform.getBoundsInParent();

        // much faster check.
        if (!boundPlat.intersects(boundPlay)) return false;


        // really annoying but scaling is needed due to using getFitWidth/height
        double scaleXPlat = platform.getImage().getWidth() / platform.getFitWidth();
        double scaleYPlat = platform.getImage().getHeight() / platform.getFitHeight();
        double scaleXPlay = player.getImage().getWidth() / player.getFitWidth();
        double scaleYPlay = player.getImage().getHeight() / player.getFitHeight();

        int playerBottomY = (int)((boundPlay.getMaxY() - boundPlay.getMinY() - 1) * scaleYPlay);

        int startX = (int) Math.max(boundPlay.getMinX(), boundPlat.getMinX()); // leftmost x
        int endX = (int) Math.min(boundPlay.getMaxX(), boundPlat.getMaxX()); // higher x
        //--------------

        //Looping through all pixels, very obnoxious
        for (int x = startX; x < endX; x++) {

            int platX = (int) ((x - boundPlat.getMinX()) * scaleXPlat);
            //int platY = (int) ((boundPlay.getMaxY() - boundPlat.getMinY() - 1) * scaleYPlat);
            int playX = (int) ((x - boundPlay.getMinX()) * scaleXPlay);
            //int playY = playerBottomY;



            //get pixel at the correct coordinates
            int pixIntPlat = platformRead.getArgb(platX, 0);
            int pixIntPlay = playerRead.getArgb(playX, playerBottomY);

            // make sure values are correctly in bounds for player and platform.
            if (platX < 0 || platX >= platform.getImage().getWidth())
                continue;
            if (playX < 0 || playX >= player.getImage().getWidth())
                continue;

            //this gets alpha channels, whatever those are, I did have to look this up
            // since I have never made pixel perfect collision before in javaFX.
            int alphaPlat = (pixIntPlat >> 24) & 0xFF;
            int alphaPlay = (pixIntPlay >> 24) & 0xFF;

            //if they aren't transparent then they are colliding
            if (alphaPlay > 0 && alphaPlat > 0) {
                System.out.println("collision working");
                return true;
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
