package com.doodlejump.doodlejump;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
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
            if (player.getBoundsInParent().intersects(platform.getBoundsInParent())) {
                if (velocity > 0 && player.getY() + player.getFitHeight() - velocity <= platform.getY()) {
                    player.setY(platform.getY() - player.getFitHeight());
                    velocity = REBOUND_VELOCITY;
                }
            }
        }
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
