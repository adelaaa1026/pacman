package pacman;

/**
 * Enum to represent direction of movement, with values UP, DOWN, LEFT, and
 * RIGHT.
 */
public enum Direction {
    UP, DOWN, LEFT, RIGHT;

    /**
     * Gets the opposite direction.
     *
     * @return the direction 180º away from direction
     */
    public Direction opposite() {
        switch (this) {
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case LEFT:
                return RIGHT;
            default:
                return LEFT;
        }
    }

    /**
     * This method calculates the new row index after one movement, given the current row index
     * and the current direction of movement.
     *
     * @param currRow the current row index
     * @return row index after moving one square in current direction
     */
    public int newRow(int currRow) {
        switch (this) {
            case UP:
                return currRow - 1;
            case DOWN:
                return currRow + 1;
            default:
                return currRow;
        }
    }

    /**
     * This method calculates the new column index after one movement, given the current column index
     * and the current direction of movement.
     *
     * @param currCol the current column index
     * @return column index after moving one square in current direction
     */
    public int newCol(int currCol) {
        switch (this) {
            case LEFT:
                return currCol - 1;
            case RIGHT:
                return currCol + 1;
            default:
                return currCol;
        }
    }
}
