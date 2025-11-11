package com.doodlejump.doodlejump;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;

import java.util.ArrayList;

public class MovementHandler {

    private Player player;
    private Scene scene;
    private Platform platform;

    private ArrayList<String> input;
    private double velocity;
    private final double gravity = .35;

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
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateMovement();
                checkBorders();
                checkPlatformCollision();
            }
        };
        timer.start();
    }

    private void updateMovement() {
        if (input.contains("LEFT")) {
            player.setX(player.getX() - player.getSpeed());
        }
        if (input.contains("RIGHT")) {
            player.setX(player.getX() + player.getSpeed());
        }
        //he must jump when colliding
        velocity += gravity;
        player.setY(player.getY() + velocity);
    }

    private void checkBorders() {
        //remove later, just usefeul for testing
        if (player.getY() + player.getFitHeight() > this.scene.getHeight()) {
            player.setY(this.scene.getHeight() - player.getFitHeight());
            velocity = -15;
            //canJump = true;
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

