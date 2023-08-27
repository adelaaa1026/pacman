package pacman;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Represents a dot that can be eaten by Pacman
 */
public class Dot implements Collidable{
    private Pane pane;
    private Circle dot;
    private Map map;
    private int row;
    private int col;

    /**
     * Constructs a dot.
     *
     * @param pane pane on which to add dot
     * @param row row index of the square on which to add dot
     * @param col column index of the square on which to add dot
     */
    public Dot(Pane pane, int row, int col, Map map){
        this.pane = pane;
        this.dot = new Circle(col * Constants.SQUARE_LENGTH + 10, row*Constants.SQUARE_LENGTH + 10,
                2, Color.WHITE);
        this.pane.getChildren().add(dot);
        this.row = row;
        this.col = col;
        this.map = map;
    }

    /**
     * This method is called when Pacman collides with a dot. The method
     * logically removes the dot by setting the boolean that indicates
     * the dot to false, and graphically removes the dot from the pane.
     */
    public void collide(){
        this.map.getMapSquare(this.row, this.col).setNoDot();
        this.pane.getChildren().remove(this.dot);
    }

    /**
     * This method adds the score when a dot is eaten.
     */
    public int addingToScore(int score){
        return (score + 10);
    }

}
