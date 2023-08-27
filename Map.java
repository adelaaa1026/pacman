package pacman;

import cs15.fnl.pacmanSupport.CS15SquareType;
import cs15.fnl.pacmanSupport.CS15SupportMap;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Sets up the maze as well as the energizers and dots. Keeps track of what collidable objects are in
 * each square.
 */
public class Map {
    private MazeSquare[][] squares;
    private Pane myPane;

    public Map(Pane pane){
        this.squares = new MazeSquare[Constants.TOTAL_ROW][Constants.TOTAL_COL];
        this.myPane = pane;
        this.setUpSquares();
        this.setUpMaze();
    }

    /**
     * Populates the 2-D array with "smart" MazeSquares.
     */
    public void setUpSquares(){
        for( int row = 0 ; row < 23 ; row++ ){
            for( int col = 0 ; col < 23 ; col++ ){
                this.squares[row][col] = new MazeSquare(this.myPane, row, col);
            }
    }}

    /**
     * Uses the CS15 Support Map to set up the walls, dots, and energizers.
     */
    private void setUpMaze(){
        for( int row = 0 ; row < 23 ; row++ ){
            for( int col = 0 ; col < 23 ; col++ ){
                if(CS15SupportMap.getSupportMap()[row][col]!= CS15SquareType.WALL)
                {this.squares[row][col].setColor(Color.BLACK);}

                if(CS15SupportMap.getSupportMap()[row][col] == CS15SquareType.DOT)
                {Dot dot = new Dot(this.myPane, row, col, this);
                    this.squares[row][col].addToCollidables(dot);
                    this.squares[row][col].setHasDot();}

                if(CS15SupportMap.getSupportMap()[row][col] == CS15SquareType.ENERGIZER)
                {Energizer energizer = new Energizer(this.myPane, row, col, this);
                    this.squares[row][col].addToCollidables(energizer);
                    this.squares[row][col].setHasEnergizer();}}
        }
    }

    /**
     * Makes all elements in a MazeSquare collide.
     */
    public void collide(int row, int col){
        this.squares[row][col].collide();
    }

    /**
     * Checks whether a square at a certain coordinate has energizer in it.
     */
    public boolean checkEnergizer(int row, int col){
        if(this.squares[row][col].checkEnergizer()){return true;}
        else return false;
    }

    /**
     * Checks whether there are energizers or dots left on the maze.
     * If there is any energizer or dot, the method returns true and
     * the game is not over.
     */
    public boolean checkEnergizersAndDots(){
        boolean notGameOver = false;
        for(int row = 0; row < 23; row ++){
            for(int col = 0; col < 23; col ++){
                if(this.squares[row][col].checkEnergizer() ||this.squares[row][col].checkDot())
                    notGameOver = true;
            }
        }
        return notGameOver;
    }

    /**
     * Called when Pacman enters a MazeSquare and returns the incremental score in
     * the MazeSquare.
     */
    public int checkScoreUpdate(int row, int col){
        return this.squares[row][col].updateScore();
    }

    /**
     * Adds an object to the Collidable arrayList in a specific MazeSquare.
     */
    public void addToCollidables(int row, int col, Collidable c){
        this.squares[row][col].addToCollidables(c);}

    /**
     * Removes an object from the Collidable arrayList in a specific MazeSquare.
     */
    public void removeFromCollidables(int row, int col, Collidable c){
        this.squares[row][col].removeFromCollidables(c);}

    /**
     * A getter method that returns the MazeSquare at a specific coordinate.
     */
    public MazeSquare getMapSquare(int row, int col){
        return this.squares[row][col];
    }}

