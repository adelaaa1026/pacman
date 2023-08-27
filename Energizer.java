package pacman;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Represents an energizer that can be eaten by Pacman
 */
public class Energizer implements Collidable{
    private Pane pane;
    private Circle energizer;
    private Map map;
    private int row;
    private int col;

    /**
     * Constructs an energizer.
     *
     * @param pane pane on which to add dot
     * @param row row index of the square on which to add dot
     * @param col column index of the square on which to add dot
     */
    public Energizer(Pane pane, int row, int col, Map map){
        this.pane = pane;
        this.energizer = new Circle(col*20+10, row*20+10, 6, Color.WHITE);
        this.pane.getChildren().add(energizer);
        this.map = map;
        this.row = row;
        this.col = col;
    }

    /**
     * after a pacMan collides with an energizer, this method is called to
     * set a boolean to indicate there's no longer energizer in the square
     * and graphically removes the energizer from the pane.
     */
    public void collide(){
        this.map.getMapSquare(this.row, this.col).setNoEnergizer();
        this.pane.getChildren().remove(this.energizer);
    }

    /**
     * This method adds the score when a dot is eaten.
     */
    public int addingToScore(int score){
        return (score + 100);
    }
}
