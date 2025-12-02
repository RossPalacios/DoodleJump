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
    private Game game; // needed this to actually end the game.
    private Scene scene;
    private List<Platform> platforms;  // multiple platforms
    private Timeline gLoop;

    private ArrayList<String> input;
    private double velocity;
    private boolean gameOver;
    //constants which have been tweaked to feel smooth
    private final double GRAVITY = 100;
    private final double DURATION = 0.05;
    private final double REBOUND_VELOCITY = -(GRAVITY * 2);

    /**
     * Instantiates a new Movement plat handler.
     *
     * @param scene     the scene for key usage
     * @param player    the player object
     * @param platforms the platforms the list of all platforms
     */
    public MovementPlatHandler(Scene scene, Player player, List<Platform> platforms, Game game) {
        this.player = player;
        this.game = game;
        this.scene = scene;
        this.velocity = 0;
        this.platforms = platforms;

        this.gameOver = false;
        input = new ArrayList<>();

        // handle key press/release
        scene.setOnKeyPressed(e -> {
            String code = e.getCode().toString();
            if (!input.contains(code) && !gameOver) input.add(code);
        });

        scene.setOnKeyReleased(e -> {
            String code = e.getCode().toString();
            input.remove(code);
        });
    }

    /**
     * Update the game using a timeline.
     */
    public void update() {
        KeyFrame loopKeyFrame = new KeyFrame(Duration.millis(16), e -> {
            updateMovement();
            checkBorders();
            checkPlatformCollision();
            scrollPlatforms(); // infinite scrolling
        });

        gLoop = new Timeline(loopKeyFrame);
        gLoop.setCycleCount(Timeline.INDEFINITE);
        gLoop.play();
    }

    /**
     * update movement using arrow keys and apply physics to the player.
     */
    private void updateMovement() {
        double prevY = player.getY();

        // move left/right
        if (input.contains("LEFT") && !gameOver) {
            player.setX(player.getX() - player.getSpeed());
            player.setImage("left");
        }
        if (input.contains("RIGHT") && !gameOver) {
            player.setX(player.getX() + player.getSpeed());
            player.setImage("right");
        }

        // gravity so set how fast you fall down
        velocity += GRAVITY * DURATION;
        player.setY(player.getY() + velocity * DURATION);

        player.setPreviousY(prevY);

    }

    /**
     * Let the player go between screen borders.
     */
    private void checkBorders() {

        // wrap horizontally
        if (player.getX() > this.scene.getWidth() && !gameOver) player.setX(0);
        if (player.getX() + player.getFitWidth() < 0) player.setX(this.scene.getWidth() - player.getFitWidth());

        //if the player hits the bottom, game over
        if (player.getY() + player.getFitHeight() > this.scene.getHeight()) {
            gLoop.stop();
            game.endGame();
        }
    }

    /**
     * check if the player is hitting a platform and should bounce.
     */
    private void checkPlatformCollision() {

        List<Platform> platformsToRemove = new ArrayList<>(); // going to remove platforms after loop.

        for (Platform platform : platforms) {
            // if the player is falling and is above the platform, rebound/bounce
            if (velocity > 0 && player.getPreviousY() + player.getFitHeight() <= platform.getY() && !gameOver) {
                if (isColliding(platform, player)) {
                    player.setY(platform.getY() - player.getFitHeight());
                    if (platform.getType().equals("bouncy"))
                        velocity = REBOUND_VELOCITY * 2;
                    else
                        velocity = REBOUND_VELOCITY;
                    if (platform.getType().equals("breakable")) {
                        platformsToRemove.add(platform);
                        platform.setImage(null);
                    }
                }
            }
        } // end of loop
        // get rid of the platforms.
        for (int i = 0; i < platformsToRemove.size(); i++) {
            this.game.getRoot().getChildren().remove(platformsToRemove.get(i));
            this.platforms.remove(platformsToRemove.get(i));
        }

    }

    /**
     * Check if the player is colliding, should be pixel perfect.
     *
     * @param platform the platform
     * @param player   the player
     * @return true if they are colliding perfectly
     */
    private boolean isColliding(ImageView platform, ImageView player) {

        if (platform.getImage() == null)
            return false;

        // create pixel reader to check collision
        PixelReader pixelReaderOne = platform.getImage().getPixelReader();
        PixelReader pixelReaderTwo = player.getImage().getPixelReader();

        // if there is no pixels then there's nothing to check.
        if (pixelReaderOne == null || pixelReaderTwo == null) {
            return false;
        }

        // calculating regions which overlap
        Bounds boundOne = platform.getBoundsInParent();
        Bounds boundTwo = player.getBoundsInParent();

        // much faster check, in case they don't intersect at all.
        if (!boundOne.intersects(boundTwo)) return false;


        int startX = (int) Math.max(boundOne.getMinX(), boundTwo.getMinX()); // leftmost x
        int startY = (int) (boundOne.getMaxY() - 5); // lowest y
        int endX = (int) Math.min(boundOne.getMaxX(), boundTwo.getMaxX()); // higher x
        int endY = (int) boundOne.getMaxY(); // higher y
        //--------------

        // really annoying but scaling is needed due to using getFitWidth/height
        double scaleXOne = platform.getImage().getWidth() / platform.getFitWidth();
        double scaleYOne = platform.getImage().getHeight() / platform.getFitHeight();

        double scaleXTwo = player.getImage().getWidth() / player.getFitWidth();
        double scaleYTwo = player.getImage().getHeight() / player.getFitHeight();

        //Looping through all pixels, very obnoxious
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {

                int x1 = (int) ((x - boundOne.getMinX()) * scaleXOne);
                int y1 = (int) ((y - boundOne.getMinY()) * scaleYOne);
                int x2 = (int) ((x - boundTwo.getMinX()) * scaleXTwo);
                int y2 = (int) ((x - boundTwo.getMinX()) * scaleYTwo);

                // make sure values are correctly in bounds.
                if (x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0 || x1 >= platform.getImage().getWidth() || y1 >= platform.getImage().getHeight() ||
                        x2 >= player.getImage().getWidth() || y2 >= player.getImage().getHeight())
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

    /**
     * simple infinite scrolling
     */
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

                    this.game.addToScore();

                    String[] types = {"normal", "bouncy", "breakable","moving"};
                    String type = types[(int)(Math.random() * types.length)];
                    p.setupImage(type);
                }
            }
        }
    }
}
