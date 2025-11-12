package com.doodlejump.doodlejump;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {

        Game game = new Game(primaryStage);
        game.startGame();

    }

    public class Launcher {
        public static void main(String[] args) {
            launch();
        }
    }
}
