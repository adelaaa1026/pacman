package pacman;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


/**
 * This class models the ghost with a rectangle moving around the maze based on its current mode.
 * When the ghost is in Chase or Scatter mode, it moves in a way such that it can get to the target
 * as fast as possible; when it is in Frightened mode, it moves randomly around.
 */
public class Ghost implements Collidable{
    private Rectangle rect;
    private Game game;
    private Map map;
    private PacMan pacman;
    private Direction currDirect;
    private Color color;
    private int initialRow;
    private int initialCol;
    private int currentRow;
    private int currentCol;
    private BoardCoordinate scatterModeTarget;
    private BoardCoordinate chaseModeTarget;
    private ArrayList<Direction> allDirect;

    /**
     * Initializes the location, color, direction of the ghost.
     *
     * @param pane the gamePane where the ghost is added
     * @param row the initial row of the ghost inside the pen
     * @param col the initial col of the ghost inside the pen
     * @param color the color of the ghost
     * @param map the map of the maze
     * @param pacman Pacman
     * @param game game
     */
    public Ghost(Pane pane, int row, int col, Color color, Map map, PacMan pacman, Game game){
        this.rect = new Rectangle(col * Constants.SQUARE_LENGTH, row * Constants.SQUARE_LENGTH, Constants.SQUARE_LENGTH ,Constants.SQUARE_LENGTH );
        this.rect.setFill(color);
        pane.getChildren().add(this.rect);
        this.initialRow = row;
        this.initialCol = col;
        this.currentRow = row;
        this.currentCol = col;
        this.color = color;
        this.currDirect = Direction.LEFT;
        this.map = map;
        this.map.addToCollidables(this.currentRow, this.currentCol, this);
        this.pacman= pacman;
        this.game = game;
        this.setUpFourDirections();
    }

    /**
     * This method sets up the arrayList that contains all four directions.
     * The arrayList is used later in BFS method to check the validity of
     * all four directions.
     */
    private void setUpFourDirections(){
        this.allDirect = new ArrayList<>();
        allDirect.add(Direction.LEFT);
        allDirect.add(Direction.RIGHT);
        allDirect.add(Direction.DOWN);
        allDirect.add(Direction.UP);
    }

    /**
     * This method first updates the current direction of the ghost based on its mode,
     * using breath-first-search if in Chase or Scatter mode and using a random valid direction
     * if in Frightened mode. Note the target of bfs is different for the Chase and Scatter mode.
     * Then the method moves the ghost graphically and logically.
     */
    public void move(GhostMode mode, GhostName ghost){
        this.map.removeFromCollidables(this.currentRow, this.currentCol, this);

        //changes the current location depending on the current mode
        if(mode == GhostMode.CHASE){
            this.currDirect = this.bfs(this.chaseModeTarget(ghost).getRow(), this.chaseModeTarget(ghost).getColumn());}
        if(mode == GhostMode.SCATTER){
            this.currDirect = this.bfs(this.scatterModeTarget(ghost).getRow(), this.scatterModeTarget(ghost).getColumn());}
        if(mode == GhostMode.FRIGHTENED){
            this.currDirect = this.randomDirection();
        }

        //moves the Ghost
        if(this.moveValid(this.currentRow, this.currentCol, this.currDirect)){
            this.setCol(this.currDirect.newCol(this.currentCol));
            this.setRow(this.currDirect.newRow(this.currentRow));
            this.map.addToCollidables(this.currentRow, this.currentCol, this);
        }
    }

    /**
     * This method uses breath-first-search and returns a direction that will make ghost move to
     * the target as fast as possible. The method first uses a for-loop to iterate all four directions,
     * enqueues the valid board coordinates in a queue, and documents the corresponding directions in
     * a 2-D array. Then it dequeues the first coordinate as the currentCell and adds new
     * valid coordinates of the currentCell to the queue. The method continues this process of
     * enqueuing and dequeuing until the whole board is gone through and the square closest to the
     * target is found. The direction that was documented in the 2-D array in that square is
     * returned.
     */
    public Direction bfs(int targetRow, int targetCol){
        // initialize variables to track the closest square and directions
        Queue<BoardCoordinate> myQueue = new LinkedList<>();
        Direction[][] myArray = new Direction[Constants.TOTAL_ROW][Constants.TOTAL_COL];
        int minDisntance = 10000;

        // iterate through all four directions, enqueue valid initial turn options,
        // and document the directions in the 2-D array
        for(int i = 0 ; i < 4; i++){
            if(this.moveValid(this.currentRow, this.currentCol, this.allDirect.get(i))){
                int newRow = this.allDirect.get(i).newRow(this.currentRow);
                int newCol = this.allDirect.get(i).newCol(this.currentCol);
                myArray[newRow][newCol] = this.allDirect.get(i);
                myQueue.add(new BoardCoordinate(newRow, newCol, true));
            }}

        // dequeue and enqueue the board coordinates until the whole board is gone through;
        // meanwhile, populate the 2-D array of directions at the corresponding coordinate
        // and update the closest square to the target ;
        BoardCoordinate closestSquare = new BoardCoordinate(this.currentRow, this.currentCol, true);
        while(!myQueue.isEmpty()){
            BoardCoordinate currentCell = myQueue.remove();
            int distance = ((currentCell.getRow()-targetRow) * (currentCell.getRow()-targetRow) +
                    (currentCell.getColumn()-targetCol)*(currentCell.getColumn()-targetCol));
            if(distance < minDisntance){ minDisntance = distance;
                closestSquare = currentCell;
            }
            for(int i = 0 ; i < 4; i++) {
                int newRow = this.allDirect.get(i).newRow(currentCell.getRow());
                int newCol = this.allDirect.get(i).newCol(currentCell.getColumn());
                if(-1 < newCol && newCol < 23 && -1 < newRow && newRow < 23){if(!this.map.getMapSquare(newRow,newCol).isWall()){if(myArray[newRow][newCol] == null){
                    myQueue.add(new BoardCoordinate(newRow, newCol, true));
                    myArray[newRow][newCol] = myArray[currentCell.getRow()][currentCell.getColumn()];
                }}
                }
            }}
        return myArray[closestSquare.getRow()][closestSquare.getColumn()];}

    /**
     * This method takes in an argument of the ghost and returns the corresponding location of target
     * in the chase mode. Different ghosts will target different offsets of Pacman.
     */
    private BoardCoordinate chaseModeTarget(GhostName ghost){
        this.chaseModeTarget = new BoardCoordinate(this.pacman.getRow(), this.pacman.getCol()+2, true);
        GhostName myGhost = ghost;
        switch (myGhost) {
            case INKY: this.chaseModeTarget = new BoardCoordinate(this.pacman.getRow(), this.pacman.getCol()+2, true);
            case PINKY: this.chaseModeTarget = new BoardCoordinate(this.pacman.getRow()+1, this.pacman.getCol()+3, true); break;
            case BLINKY: this.chaseModeTarget = new BoardCoordinate(this.pacman.getRow(), this.pacman.getCol(), true);break;
            case CLYDE: this.chaseModeTarget = new BoardCoordinate(this.pacman.getRow()-4, this.pacman.getCol(), true); break;
        }
        return this.chaseModeTarget;
    }

    /**
     * This method takes in an argument of the ghost and returns the corresponding location of target
     * in the scatter mode. Different ghosts will target different corners of the maze.
     */
    private BoardCoordinate scatterModeTarget(GhostName ghost){
        this.scatterModeTarget = new BoardCoordinate(21,16, true);
        GhostName myGhost = ghost;
        switch (myGhost) {
            case INKY: this.scatterModeTarget = new BoardCoordinate(Constants.INKY_TARGET_ROW,
                    Constants.INKY_TARGET_COL, true); break;
            case PINKY: this.scatterModeTarget = new BoardCoordinate(Constants.PINKY_TARGET_ROW,
                    Constants.PINKY_TARGET_COL,true); break;
            case BLINKY: this.scatterModeTarget = new BoardCoordinate(Constants.BLINKY_TARGET_ROW,
                    Constants.INKY_TARGET_COL, true); break;
            case CLYDE: this.scatterModeTarget = new BoardCoordinate(21, 7, true);break;
        }
        return this.scatterModeTarget;
    }

    /**
     * This method iterates through all four directions and adds the valid directions to an arrayList.
     * Then a random index of the arrayList is generated and returns the direction on that index.
     */
    private Direction randomDirection(){
        ArrayList<Direction> validDirections = new ArrayList<>();

        for(int i = 0 ; i < 4; i++){
            if(this.moveValid(this.currentRow, this.currentCol, this.allDirect.get(i))){
                validDirections.add(this.allDirect.get(i));
            }}

        Direction dir = validDirections.get((int) (Math.random() * validDirections.size()));
        return dir;
    }

    /**
     * This method determines the validity of the next square Ghost is moving into based on the
     * current square and argument direction. The next square should be within the boundary
     * of the map, shouldn't be a wall, and the argument direction shouldn't be opposite to
     * the current direction. This method also deals with the wrapping of ghost.
     * If the next square is the either side of the tunnel, Ghost will enter the tunnel on one side
     * of the board and exit on the other side.
     */
    public boolean moveValid(int currentRow, int currentCol, Direction dir ){
        boolean moveValid = false;
        int newRow = dir.newRow(currentRow);
        int newCol = dir.newCol(currentCol);

        if(dir != this.currDirect.opposite()){
            if(-1< newRow && newRow < 23 && -1< newCol && newCol < 23){
                if(!this.map.getMapSquare(newRow, newCol).isWall()){
                    moveValid = true;
                } }

            //wrapping of the Ghost
            if( newRow == 11 & newCol == -1){this.setCol(22); this.currentCol = 22; moveValid = true;};
            if( newRow == 11 & newCol== 23){this.setCol(0); this.currentCol = 0;  moveValid = true;};
        }
        return moveValid;
    }

    /**
     * This method deals with the collision between Pacman and ghosts based on the current mode.
     * In scatter or chase mode, the ghost eats Pacman, and all ghosts are sent back to the pen;
     * in frightened mode, Pacman eats the ghost, and the ghost is sent back to the pen.
     */
    public void collide(){
        if(this.game.getMode() != GhostMode.FRIGHTENED ){ this.game.eatPacman();}
        if(this.game.getMode() == GhostMode.FRIGHTENED ){this.game.eatGhost(this); this.returnToPen();}
     };

    /**
     * This method removes the ghost from the current square and adds it to the pen.
     */
    public void returnToPen(){
        this.map.getMapSquare(this.currentRow, this.currentCol).removeFromCollidables(this);
        this.setCol(this.initialCol);
        this.setRow(this.initialRow);
        this.currDirect = Direction.LEFT;
    }

    /**
     * This method releases the ghost by setting its location to the exit of the pen.
     */
    public void leavePen(){
        this.currentRow = Constants.EXIT_ROW ; this.currentCol = Constants.EXIT_COL;
        this.setRow(this.currentRow); this.setCol(this.currentCol);
        this.map.getMapSquare(Constants.EXIT_ROW, Constants.EXIT_COL).addToCollidables(this);
    }

    /**
     * This method returns the incremental score when Pacman eats a ghost in the Frightened mode.
     */
    public int addingToScore(int score){
        if(this.game.getMode() == GhostMode.FRIGHTENED) { score = score + 200;}
        return score;
    }

    /**
     * The following methods are self-explanatory.
     */
    public void changeToBlue(){this.rect.setFill(Color.LIGHTBLUE);}

    public void setOriginalColor(){this.rect.setFill(this.color);}

    public void setRow(int row){this.rect.setY(row * Constants.SQUARE_LENGTH);
    this.currentRow = row;}

    public void setCol(int col){this.rect.setX(col * Constants.SQUARE_LENGTH);
    this.currentCol = col;}

    public int getRow(){return (int)Math.round(this.rect.getY()/Constants.SQUARE_LENGTH);}

    public int getCol(){return (int)Math.round(this.rect.getX()/Constants.SQUARE_LENGTH);}

}
