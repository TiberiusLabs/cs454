package board;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.HashSet;
import javax.swing.JFrame;

/**
 *
 * @author sean
 */
public class Tile {

    private char content;
    private int row;
    private int column;
    private boolean anchor;
    Tile nextLeft;
    Tile nextRight;
    Tile nextUp;
    Tile nextDown;
    private HashSet<Character> crosschecks;
    boolean transposed;

    public Tile(char content, int row, int column) {
        this.content = content;
        this.row = row;
        this.column = column;
        this.anchor = false;
    }

    public void setNeighborTiles(Tile left, Tile right, Tile up, Tile down) {
        this.nextLeft = left;
        this.nextRight = right;
        this.nextUp = up;
        this.nextDown = down;

    }

    public String verticalWord(Tile tile, boolean wordDown,
            boolean transposed) {
        if (tile == null || !tile.containsLetter()) {
            return "";
        }

        char letter = tile.getContent();

        if (!wordDown) {
            return verticalWord(tile.getNextUp(transposed), wordDown,
                    transposed) + letter;
        } else {
            return letter
                    + verticalWord(tile.getNextDown(transposed),
                    wordDown, transposed);
        }
    }

    public void setContent(char content, boolean transposed) {
        this.content = content;
        anchor = false;

        if (this.getContent() != ' ') {
            Tile neighbor;
            if (!(neighbor = this.getNextDown(transposed)).containsLetter()) {
                neighbor.setAnchor(true);
            }
            if (!(neighbor = this.getNextUp(transposed)).containsLetter()) {
                neighbor.setAnchor(true);
            }
            if (!(neighbor = this.getNextLeft(transposed)).containsLetter()) {
                neighbor.setAnchor(true);
            }
            if (!(neighbor = this.getNextRight(transposed)).containsLetter()) {
                neighbor.setAnchor(true);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Tile)) {
            return false;
        }

        Tile checkTile = (Tile) obj;

        return this.row == checkTile.getRow() && this.column == checkTile.getColumn();
    }

    public boolean containsLetter() {
        if (content != ' ') {
            return true;
        }
        return false;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public void setAnchor(boolean flag) {
        this.anchor = flag;
    }

    public void setContent(String s) {
        content = s.charAt(0);
    }

    public char getContent() {
        return content;
    }

    public void setCenterTile() {
    }

    public boolean isAnchor() {
        return anchor;
    }

    public Tile getNextRight(boolean transposed) {
        return (transposed) ? nextDown : nextRight;
    }

    public Tile getNextLeft(boolean transposed) {
        return (transposed) ? nextUp : nextLeft;
    }

    public Tile getNextDown(boolean transposed) {
        return (transposed) ? nextRight : nextDown;
    }

    public Tile getNextUp(boolean transposed) {
        return (transposed) ? nextLeft : nextUp;
    }
}
