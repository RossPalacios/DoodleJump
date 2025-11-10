package com.doodlejump.doodlejump;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;

import java.util.ArrayList;

public class MovementHandler {

    private Player player;
    private Scene scene;
    private ArrayList<String> input;

    public MovementHandler(Scene scene, Player player) {
        this.player = player;
        this.scene = scene;
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
    }

    private void checkBorders() {
        if (player.getY() + player.getFitHeight() > this.scene.getHeight()) {
            player.setY(this.scene.getHeight() - player.getFitHeight());
            //velocityY = 0;
            //canJump = true;
        }
        if(player.getX() - player.getFitWidth() > this.scene.getWidth()) {
            player.setX(0);
        }
        if(player.getX() + player.getFitWidth() < 0) {
            player.setX(this.scene.getWidth() - player.getFitWidth());
        }
    }
}

