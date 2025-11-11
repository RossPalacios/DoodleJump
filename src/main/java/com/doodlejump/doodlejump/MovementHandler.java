package com.doodlejump.doodlejump;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.util.Duration;

import java.util.ArrayList;

public class MovementHandler {

    private Player player;
    private Scene scene;
    private Platform platform;

    private ArrayList<String> input;
    private double velocity;
    private final double GRAVITY = 100;
    //I tweaked constants just to make them feel smoother.
    private final double DURATION = .05;

    public MovementHandler(Scene scene, Player player, Platform platform) {
        this.player = player;
        this.scene = scene;
        this.velocity = 0;
        this.platform = platform;
        //create input to use in the movement updating
        input = new ArrayList<>();
        scene.setOnKeyPressed(e -> {
            String code = e.getCode().toString();
            if (!input.contains(code)) input.add(code);
        });

        scene.setOnKeyReleased(e -> {
            String code = e.getCode().toString();
            input.remove(code);
        });

    }

    public void update() {
        KeyFrame loopKeyFrame = new KeyFrame(Duration.millis(16), e -> {
            updateMovement();
            checkBorders();
            checkPlatformCollision();
        });
        Timeline gLoop = new Timeline(loopKeyFrame);
        gLoop.setCycleCount(Timeline.INDEFINITE);
        gLoop.play();
    }

    private void updateMovement() {
        if (input.contains("LEFT")) {
            player.setX(player.getX() - player.getSpeed());
            player.setImage("left");
        }
        if (input.contains("RIGHT")) {
            player.setX(player.getX() + player.getSpeed());
            player.setImage("right");
        }
        //he must jump when colliding
        velocity += GRAVITY * DURATION;
        player.setY(player.getY() + velocity * DURATION);
    }

    private void checkBorders() {
        //remove later, just usefeul for testing
        if (player.getY() + player.getFitHeight() > this.scene.getHeight()) {
            player.setY(this.scene.getHeight() - player.getFitHeight());
            velocity = -(GRAVITY * 2); // gravity * 2 felt the smoothest.
        }
        if (player.getX() - player.getFitWidth() > this.scene.getWidth()) {
            player.setX(0);
        }
        if (player.getX() + player.getFitWidth() < 0) {
            player.setX(this.scene.getWidth() - player.getFitWidth());
        }
    }

    private void checkPlatformCollision() {
        if (player.getBoundsInParent().intersects(platform.getBoundsInParent())) {
            if (velocity > 0 && player.getY() + player.getFitHeight() - velocity <= platform.getY()) {
                player.setY(platform.getY() - player.getFitHeight());
                velocity = 0;
            }
        }

    }
}

