package pacman;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * This class models Pacman with a circle. Pacman continuously move around the maze until
 * it runs into a wall. Its direction can be controlled by pressing keys.
 */
public class PacMan  {
    private Direction currDirect;
    private Circle pacman;
    private Map map;


    /**
     * Constructs the Pacman and adds it to the pane. Initializes variables.
     *
     * @param pane the pane Pacman is added to
     * @param map the map of the game
     *
     */
    public PacMan(Pane pane, Map map){
        this.pacman = new Circle(Constants.PACMAN_INITIAL_COL * Constants.SQUARE_LENGTH + 10,
                Constants.PACMAN_INITIAL_ROW * Constants.SQUARE_LENGTH + 10, 8, Color.YELLOW);
        pane.getChildren().add(pacman);
        this.map = map;
        this.currDirect = Direction.RIGHT;
    }

    /**
     * This method changes the current direction of the pacman to a new direction
     * if this new direction is not the opposite of the current direction
     * and if the new square pacman will be in is a valid square.
     */
    public void changeDirect(Direction dir){
        if (!(dir == this.currDirect.opposite()) && moveValid(dir)) {
            this.currDirect = dir;
        }
    }

    /**
     * This method makes the pacman move to a new square depending on
     * the current direction if the next square pacman will be in
     * is a valid square. If the square is not valid, the pacman
     * does not move.
     */
    public void move(){
        if(moveValid(this.currDirect))
        {this.setCol(this.currDirect.newCol(this.getCol()));
        this.setRow(this.currDirect.newRow(this.getRow()));}
        }

    /**
     * This method checks whether a new square pacman will move into is
     * within the boundary and is not wall. This method also models the
     * wrapping of Pacman. If the new square is the either side of the tunnel,
     * Pacman will enter the tunnel on one side of the board and exit on
     * the other side.
     */
    public boolean moveValid(Direction dir){
        int newCol = dir.newCol(this.getCol());
        int newRow = dir.newRow(this.getRow());
        boolean moveValid = false;

        //models the wrapping of Pacman
        if(-1 < newRow && newRow < 23 && -1 < newCol && newCol < 23){if(!this.map.getMapSquare(newRow, newCol).isWall()){moveValid = true;}}
        if( newRow == 11 & newCol == -1){this.setCol(22); moveValid = true;};
        if( newRow == 11 & newCol== 23){this.setCol(0); moveValid = true;};
        return moveValid;
    }

    /**
     * Returns the current row of Pacman based on its current Y location.
     */
    public int getRow(){return (int)Math.round((this.pacman.getCenterY()-10)/Constants.SQUARE_LENGTH);}

    /**
     * Returns the current column of Pacman based on its current X.
     */
    public int getCol(){return (int)Math.round((this.pacman.getCenterX()-10)/Constants.SQUARE_LENGTH);}

    /**
     * Sets the row of Pacman.
     */
    public void setRow(int row){this.pacman.setCenterY(row * Constants.SQUARE_LENGTH + 10);}

    /**
     * Sets the column of Pacman.
     */
    public void setCol(int col){this.pacman.setCenterX(col * Constants.SQUARE_LENGTH + 10);}

}
