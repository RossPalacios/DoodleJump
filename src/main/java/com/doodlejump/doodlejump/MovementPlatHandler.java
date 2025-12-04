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

    // other objects needed for the loop to function
    private Player player;
    private Game game; // needed this to actually end the game.
    private Scene scene;
    private List<Platform> platforms;  // multiple platforms
    private Timeline gLoop;
    // ----------------------------------------------

    private ArrayList<String> input;
    private double velocity, difficultyScale;
    private long startTime;
    private final double GRAVITY = 100, DURATION = 0.05, REBOUND_VELOCITY = -(GRAVITY * 2); // constants which have been tweaked to feel smooth

    /**
     * Instantiates a new Movement plat handler.
     *
     * @param scene     the scene for key usage
     * @param player    the player object
     * @param platforms the list of all platforms
     */
    public MovementPlatHandler(Scene scene, Player player, List<Platform> platforms, Game game) {

        // setting up necessary objects
        this.player = player;
        this.game = game;
        this.scene = scene;
        this.platforms = platforms;
        //-----------------------------

        this.startTime = System.currentTimeMillis();
        this.difficultyScale = 0;
        this.velocity = 0;

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

        // set up loop forever (indefinitely)
        gLoop = new Timeline(loopKeyFrame);
        gLoop.setCycleCount(Timeline.INDEFINITE);
        gLoop.play();
        //-----------------------------------
    }

    /**
     * update movement using arrow keys and apply physics to the player.
     */
    private void updateMovement() {
        double prevY = player.getY();

        // scaling difficulty over time
        long currentTime = System.currentTimeMillis();
        difficultyScale = (currentTime - this.startTime) / 5000.0; // this/1000 is the seconds per difficulty ramp
        difficultyScale = Math.min(difficultyScale, 10); // this bounds the difficulty
        //-----------------------------

        // handling moving platforms here since it is for movement in general
        for (Platform p : platforms)
            if (p.getType().equals("moving"))
                handleMovingPlatform(p);
        //------------------------------

        // move left/right
        if (input.contains("LEFT")) {
            player.setX(player.getX() - player.getSpeed());
            player.setImage("left");
        }
        if (input.contains("RIGHT")) {
            player.setX(player.getX() + player.getSpeed());
            player.setImage("right");
        }

        // velocity and player height managed here:
        velocity += GRAVITY * DURATION;
        player.setY(player.getY() + velocity * DURATION);
        player.setPreviousY(prevY);
        //-----------------------------------------
    }

    /**
     * Let the player go between screen borders.
     */
    private void checkBorders() {

        // wrap horizontally
        if (player.getX() > this.scene.getWidth())
            player.setX(0);
        if (player.getX() + player.getFitWidth() < 0)
            player.setX(this.scene.getWidth() - player.getFitWidth());

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

        for (Platform platform : platforms) {
            // if the player is falling and is above the platform, rebound/bounce
            if (velocity > 0 && player.getPreviousY() + player.getFitHeight() <= platform.getY()) {
                if (isColliding(platform, player)) {
                    player.setY(platform.getY() - player.getFitHeight());
                    if (platform.getType().equals("bouncy"))
                        velocity = REBOUND_VELOCITY * 2;
                    else
                        velocity = REBOUND_VELOCITY;
                    if (platform.getType().equals("breakable")) {
                        platform.breakPlatform();
                    }
                }
            }
        } // end of loop
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
            diff *= (1 + difficultyScale * .08); // scale the scroll speed
            player.setY(threshold);

            // move all platforms down
            for (Platform p : platforms) {
                p.setY(p.getY() + diff);

                // if a platform goes to the bottom off the screen then it moves to top with random X
                if (p.getY() > 700) {
                    p.setY(0);
                    p.setX(Math.random() * (400 - p.getFitWidth()));

                    this.game.addToScore();
                    p.fixPlatform();

                    String type = pickPlatform();
                    p.setupImage(type);
                }
            }// end of for loop
        }
    }// end of scroll method

    /**
     * helper method to pick a random platform and scale it with the difficulty.
     *
     * @return
     */
    private String pickPlatform() {
        double random = Math.random();

        // base chances at difficulty scale of zero
        double movingPlatProb = .05; // 5%
        double breakingPlatProb = .10; // 10%
        double bouncyPlatProb = .10; // 10%, will get lower for difficulty

        // increasing chance with difficulty, although slowly
        movingPlatProb += difficultyScale * .02; // increases chance by up to 2 percent each time
        breakingPlatProb += difficultyScale * .025; // want these to appear more
        bouncyPlatProb -= difficultyScale * .0075; // will decrease very slowly


        // add in a cap for the probability
        movingPlatProb = Math.min(movingPlatProb, .45); // caps out at 45%
        breakingPlatProb = Math.min(breakingPlatProb, .35); // caps out at 35%
        bouncyPlatProb = Math.max(bouncyPlatProb, .05); // caps at 4 percent chance

        if (random < movingPlatProb) // based on moving probability
            return "moving";
        random -= movingPlatProb; // take this out of our probability range

        if (random < breakingPlatProb) // breaking probability
            return "breakable";
        random -= breakingPlatProb;

        if (random < bouncyPlatProb) // bouncy probability
            return "bouncy";

        return "normal"; // anything else is a regular platform, at peak difficulty normal platforms spawn 15% of the time
    }

    /**
     * Move moving platform and bounce it when it hits the side of the game
     *
     * @param p the platform to move.
     */
    private void handleMovingPlatform(Platform p) {
        if (!p.getType().equals("moving"))
            return;

        // created speed class variables so individual platforms could act differently
        double speed = p.getHorizontalSpeed();
        double speedMultiplier = Math.min(p.getSpeedMultiplier(),2.15); // had to cap out the multiplier at around 2 times

        //always move platform left or right
        p.setX(p.getX() + speed * speedMultiplier);

        // flip the speed for continuous rebounding
        if (p.getX() > this.scene.getWidth() - p.getFitWidth())
            speed = -(speed);
        else if (p.getX() < 0)
            speed = -(speed);

        p.setHorizontalSpeed(speed);
        speedMultiplier += .0015 * (1 + difficultyScale * .02); // similar scaling as the scrolling.

        p.setSpeedMultiplier(speedMultiplier);

    }
}
