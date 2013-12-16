package board;

import board.Tile;

/**
 *
 * @author sean
 */
public class Board {

    private final int boardSize;
    private Tile[][] board;
    private boolean[][] validMoves;

    public Board(int size) {
        boardSize = size;
        initBoard();
    }

    public void initBoard() {
        validMoves = new boolean[boardSize][boardSize];
        board = new Tile[boardSize][boardSize];
        for (int row = 0; row < boardSize; row++) {
            for (int column = 0; column < boardSize; column++) {
                board[row][column] = new Tile(' ', row, column);
                Tile tile = board[row][column];
                if (row == 7 || column == 7)
                    validMoves[row][column] = true;
                else
                    validMoves[row][column] = false;

                if (row == 7 && column == 7) {
                    tile.setCenterTile();
                    tile.setAnchor(true);

                    //tile.initCrossCheck();
                }
            }
        }
    }

    public void markValidTiles() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                validMoves[i][j] = false;
            }
        }

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j].containsLetter()) {
                    markNeighbors(i, j);
                }
            }
        }
    }

    public boolean validTile(int row, int col) {
        return validMoves[row][col];
    }

    private void markNeighbors(int row, int col) {
        if (row > 0 && col > 0 && !board[row-1][col-1].containsLetter()) {
            validMoves[row-1][col-1] = true;
        }
        if (row > 0 && col < boardSize-1 && !board[row-1][col+1].containsLetter()) {
            validMoves[row-1][col+1] = true;
        }
        if (row < boardSize-1 && col > 0 && !board[row+1][col-1].containsLetter()) {
            validMoves[row+1][col-1] = true;
        }
        if (row < boardSize-1 && col < boardSize-1 && !board[row+1][col+1].containsLetter()) {
            validMoves[row+1][col+1] = true;
        }
    }

    public void placeTile(char letter, Tile tile, boolean transposed) {

        tile.setAnchor(false);
        tile.setContent(letter, transposed);

    }

    public Tile[][] getTransposedBoard() {
        Tile[][] transposed = new Tile[this.boardSize][this.boardSize];
        for (int row = 0; row < this.boardSize; row++) {
            for (int column = 0; column < this.boardSize; column++) {
                transposed[column][row] = board[row][column];
            }
        }
        return transposed;
    }
    
    public int getSize() {
        return boardSize;
    }

    public Tile getTile(int row, int col) {

        return board[row][col];
    }
}
