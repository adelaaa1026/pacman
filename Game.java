package pacman;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import java.util.LinkedList;
import java.util.Queue;


/**
 * This class handles the high-level logic of the program, by running a timeline that updates
 * the mode of the ghosts and makes ghosts and Pacman move accordingly, by making Pacman
 * collide with collidable objects (ghosts, dots, energizers) and by updating the
 * Pacman direction of key input.
 */
public class Game {
    private Pane gamePane;
    private HBox scorePane;
    private PacMan pacman;
    private Map map;
    private Ghost blinky;
    private Ghost inky;
    private Ghost pinky;
    private Ghost clyde;
    private GhostMode mode;
    private Timeline timeline;
    private Label scoreLabel;
    private Label livesLabel;
    private int score;
    private int lives;
    private int scatterCounter;
    private int chaseCounter;
    private int frightenedCounter;
    private int releaseGhostCounter;
    private Queue<Ghost> ghostPen;

    /**
     * Sets up the game by initializing the map, Pacman, ghosts, and ghostMode,
     * and by registering the KeyEvent handler on the gamePane and starting
     * the timeline that triggers the game update.
     *
     * @param pane the pane on which to add game elements
     * @param scorePane the pane on which the current score and lives are shown
     */
    public Game(Pane pane, HBox scorePane){
        this.gamePane = pane;
        this.scorePane = scorePane;
        this.map = new Map(this.gamePane);
        this.pacman = new PacMan(this.gamePane, this.map);
        this.mode = GhostMode.CHASE;
        this.setUpGhosts();
        this.setUpCountersAndLabels();
        this.startGame();
        this.gamePane.setOnKeyPressed((KeyEvent event) -> this.handleKeyPressed(event.getCode()));
        this.gamePane.setFocusTraversable(true);
        this.gamePane.requestFocus();
    }

    /**
     * This method sets up the ghosts by initializing the ghosts and adding them to the ghost pen.
     * The pen is modeled using a queue so that later when ghosts are released, the first one in
     * will be the first one out. In our case, the order is Pinky, Inky, and Clyde.
     */
    private void setUpGhosts(){
        this.blinky = new Ghost(this.gamePane, Constants.BLINKY_INITIAL_ROW, Constants.BLINKY_INITIAL_COL,
                Color.RED, this.map, this.pacman,this);
        this.pinky = new Ghost(this.gamePane, Constants.PINKY_INITIAL_ROW, Constants.PINKY_INITIAL_COL,
                Color.PINK, this.map, this.pacman, this);
        this.inky = new Ghost(this.gamePane, Constants.INKY_INITIAL_ROW, Constants.INKY_INITIAL_COL,
                Color.LIGHTBLUE, this.map, this.pacman, this);
        this.clyde = new Ghost(this.gamePane, Constants.CLYDE_INITIAL_ROW, Constants.CLYDE_INITIAL_COL,
                Color.ORANGE, this.map, this.pacman, this);
        this.ghostPen = new LinkedList<>();
        this.ghostPen.add(this.pinky);
        this.ghostPen.add(this.inky);
        this.ghostPen.add(this.clyde);

    }

    /**
     * This method initializes the counters that record the time of each mode.
     * It also sets up the labels that show the current score and lives.
     */
    private void setUpCountersAndLabels(){
        this.scatterCounter = 0;
        this.chaseCounter = 0;
        this.frightenedCounter = 0;
        this.releaseGhostCounter = 0;
        this.score = 0;
        this.lives = 3;
        this.scoreLabel = new Label("Score: " + this.score);
        this.scoreLabel.setTextFill(Color.WHITE);
        this.livesLabel = new Label("Lives: " + this.lives);
        this.livesLabel.setTextFill(Color.WHITE);
        this.scorePane.getChildren().addAll(livesLabel, scoreLabel);
        this.scorePane.setAlignment(Pos.CENTER);
        this.scorePane.setSpacing(20);
    }

    /**
     * Starts the game by starting the timeline, which is updated every 0.5 second.
     */
    private void startGame() {
        KeyFrame kf = new KeyFrame(Duration.seconds(0.5), (ActionEvent e) ->
                this.updateTimeline());
        this.timeline = new Timeline(kf);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * Updates the game by 1) checking the counters, updating the ghost mode and ghost colors
     * accordingly, and incrementing the counters, 2)making ghost and Pacman move, 3) checking
     * collision between pacman and other elements, updating the score, and checking gameOver.
     */
    private void updateTimeline(){
        this.checkCounters();
        this.pacman.move();

        // The checkScoreUpdate method is intentionally called for three times to make sure the score
        // is updated accordingly before the checkCollision method removes all collidable objects
        // in the current square and to make sure the lives shown on the label is 0 when the game is over.
        this.checkScoreUpdate();
        this.checkCollision();
        this.blinky.move(this.mode, GhostName.BLINKY);
        this.pinky.move(this.mode, GhostName.PINKY);
        this.inky.move(this.mode, GhostName.INKY);
        this.clyde.move(this.mode, GhostName.CLYDE);
        this.checkScoreUpdate();
        this.checkCollision();
        this.checkScoreUpdate();
        this.checkGameOver();
    };

    /**
     * This method is called each time the timeline is updated. It checks the counters,
     * updates the ghost mode and ghost colors accordingly, and increments the counters.
     * The Chase mode lasts 20s, the Scatter mode 7s, and the Frightened mode 7s.
     * Ghosts is released every 3 seconds, if there is any ghost left in the pen.
     * Since the timeline is updated every 0.5 seconds, the counters are set accordingly.
     */
    private void checkCounters(){
        //the Chase mode lasts 20s
        if(this.chaseCounter == 40){
            this.chaseCounter = 0;
            this.mode = GhostMode.SCATTER;
            System.out.println("Scatter Mode");}

        //the Scatter mode lasts 7s
        if(this.scatterCounter == 14){
            this.scatterCounter = 0;
            this.mode = GhostMode.CHASE;
            System.out.println("Chase Mode");
        }

        //the Chase mode lasts 7s
        if(this.frightenedCounter == 14){
            this.frightenedCounter = 0;
            this.mode = GhostMode.CHASE;
            System.out.println("Chase Mode");
        }

        //ghosts are released from the pen every 3s
        if(this.releaseGhostCounter == 6 && !this.ghostPen.isEmpty()){
            this.releaseGhostCounter = 0;
            this.ghostPen.remove().leavePen();
            System.out.println("release a ghost");
        }

        if(this.mode == GhostMode.CHASE){this.changeToOriginalColor(); this.chaseCounter ++;}
        if(this.mode == GhostMode.SCATTER){this.changeToOriginalColor(); this.scatterCounter ++;}
        if(this.mode == GhostMode.FRIGHTENED){this.changeToBlue(); this.frightenedCounter ++; }
        this.releaseGhostCounter ++;
    }

    /**
     * Checks whether Pacman collides with an energizer and updates the ghost mode according.
     * Makes Pacman collide with other elements in the current location.
     */
    private void checkCollision(){
        if(this.map.checkEnergizer(this.pacman.getRow(), this.pacman.getCol())){this.mode = GhostMode.FRIGHTENED;
            System.out.println("FRIGHTENED MODE");}
        this.map.collide(this.pacman.getRow(), this.pacman.getCol());
    }

    /**
     * Updates the current score and lives after calling the map's checkScoreUpdate method to check
     * the amount of incremental scores in the current square Pacman is in.
     */
    private void checkScoreUpdate(){
        this.score = this.score + this.map.checkScoreUpdate(this.pacman.getRow(),this.pacman.getCol());
        this.scoreLabel.setText("Score: " + this.score);
        this.livesLabel.setText("Lives: " + this.lives);
    }

    /**
     * Sets the color of all ghosts to blue.
     */
    private void changeToBlue(){
        this.blinky.changeToBlue();
        this.pinky.changeToBlue();
        this.inky.changeToBlue();
        this.clyde.changeToBlue();
    }

    /**
     * Sets the colors of ghosts to their original colors.
     */
    private void changeToOriginalColor(){
        this.blinky.setOriginalColor();
        this.pinky.setOriginalColor();
        this.inky.setOriginalColor();
        this.clyde.setOriginalColor();
    }

    /**
     * Handles key input by changing direction of Pacman on up, down, left, and
     * right arrow keys.
     *
     * @param code code of the key pressed
     */
    private void handleKeyPressed(KeyCode code) {
        switch (code) {
            case UP:
                this.pacman.changeDirect(Direction.UP);
                break;
            case DOWN:
                this.pacman.changeDirect(Direction.DOWN);
                break;
            case LEFT:
                this.pacman.changeDirect(Direction.LEFT);
                break;
            case RIGHT:
                this.pacman.changeDirect(Direction.RIGHT);
                break;
        }
    }

    /**
     * Resets the game by making ghosts return to pen both graphically and logically
     * and by setting all the counters to zero.
     */
    private void reset(){
        this.inky.returnToPen();
        this.blinky.returnToPen();
        this.pinky.returnToPen();
        this.clyde.returnToPen();

        this.ghostPen.add(this.pinky);
        this.ghostPen.add(this.inky);
        this.ghostPen.add(this.clyde);
        this.chaseCounter = 0;
        this.scatterCounter = 0;
        this.frightenedCounter = 0;
        this.releaseGhostCounter = 0;
    }

    /**
     * This method is called in Ghost class when a ghost collides with Pacman in Chase or Scatter mode.
     */
    public void eatPacman(){
        this.lives--;
        this.reset();
    }

    /**
     * This method is called in Ghost class when a ghost collides with Pacman in Frightened mode.
     * It adds the ghost to pen and updates the score and the counter.
     */
    public void eatGhost(Ghost ghost){
        this.ghostPen.add(ghost);
        this.score = this.score + 200;
        this.releaseGhostCounter = 0;
    }

    /**
     * This method checks game over. If Pacman loses all his lives,
     * a "GAME OVER!" label will be displayed; if Pacman eats all
     * the dots and energizers, a "WINNER" label will be displayed.
     * In any case, Pacman and the ghosts are no longer be able
     * to move and Pacman are not be able to be manipulated with
     * the keyboard.
     */
    private void checkGameOver(){
        if(this.lives == 0 ){
            this.timeline.stop();
            Label gameOver = new Label();
            gameOver.setTextFill(Color.ORANGE);
            gameOver.setLayoutY(Constants.GAMEOVER_Y);
            gameOver.setLayoutX(Constants.GAMEOVER_X);
            gameOver.setFont(new Font("Serif", 40));
            this.gamePane.getChildren().add(gameOver);
         gameOver.setText("GAME OVER!");
       }

        if(!this.map.checkEnergizersAndDots()){
            this.timeline.stop();
            Label gameOver = new Label();
            gameOver.setTextFill(Color.ORANGE);
            gameOver.setLayoutY(Constants.GAMEOVER_Y);
            gameOver.setLayoutX(Constants.GAMEOVER_X);
            gameOver.setFont(new Font("Serif", 40));
            this.gamePane.getChildren().add(gameOver);
            gameOver.setText("WINNER!");
        }
    }

    /**
     * This method is called in Ghost class to return the current mode of ghost so that the ghost
     * knows how to collide with Pacman accordingly.
     */
    public GhostMode getMode(){
        return this.mode;
    }


}
