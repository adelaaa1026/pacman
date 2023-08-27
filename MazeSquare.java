package pacman;


import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

/**
 * Wraps one square of the maze and tracks its contents (ghosts, energizers, dots, or empty).
 */
public class MazeSquare {
    private Rectangle rect;
    private ArrayList<Collidable> collidables;
    private boolean hasEnergizer;
    private boolean hasDot;

    /**
     * Constructs the empty MazeSquare square.
     *
     * @param pane the pane on which to add the square
     * @param row row index of the square
     * @param col column index of the square
     */
    public MazeSquare(Pane pane, int row, int col){
        this.rect = new Rectangle(col*20, row*20, 20, 20);
        this.rect.setFill(Color.BLUE);
        this.collidables = new ArrayList<>();
        pane.getChildren().add(this.rect);
        this.hasEnergizer = false;
        this.hasDot = false;
    }

    /**
     * Iterate through all objects in the colliables arrayList in this square,
     * makes them collide, and removes them from the arrayList.
     */
    public void collide(){
        for(int i = 0; i < this.collidables.size(); i ++){
            this.collidables.get(i).collide();
        }
        for(int i = 0; i < this.collidables.size(); i ++){
            this.collidables.remove(i); i--; }
    }

    /**
     * Iterates through all objects in the colliables arrayList in this square,
     * and updates the score accordingly.
     */
    public int updateScore(){
        int addScore = 0;
        for(int i = 0; i < this.collidables.size(); i ++){
            addScore = this.collidables.get(i).addingToScore(addScore);
            //System.out.println(this.collidables.get(i) + "adding to score" + this.collidables.get(i).addingToScore(addScore) );
        }
        return addScore;
    }

    /**
     * Sets the color of the mazeSquare based on its type (wall or valid square)
     */
    public void setColor(Color color){
        this.rect.setFill(color);
    }

    /**
     * Adds an object to the colliables arrayList.
     */
    public void addToCollidables(Collidable c){
        this.collidables.add(c);
    }

    /**
     * Removes an object to the colliables arrayList.
     */
    public void removeFromCollidables(Collidable c){
        this.collidables.remove(c);
    }

    /**
     * Checks whether a square an energizer in it.
     */
    public boolean checkEnergizer(){
        return this.hasEnergizer;
    }

    /**
     * Called when an energizer is initiated.
     */
    public void setHasEnergizer(){
        this.hasEnergizer = true;
    }

    /**
     * Called after Pacman eats an energizer to update the boolean
     * so that Pacman won't collide with the energizer when it passes the square again.
     */
    public void setNoEnergizer(){this.hasEnergizer = false;}

    /**
     * Checks whether a square a dot in it.
     */
    public boolean checkDot(){
        return this.hasDot;
    }

    /**
     * Called when a dot is initiated.
     */
    public void setHasDot(){
        this.hasDot = true;
    }

    /**
     * Called after Pacman eats a dot to update the boolean
     * so that Pacman won't collide with the dot when it passes the square again.
     */
    public void setNoDot(){this.hasDot = false;}

    /**
     * Checks whether a square is a wall.
     */
    public boolean isWall(){
        if(this.rect.getFill() == Color.BLUE) return true;
        else return false;
    }

}






