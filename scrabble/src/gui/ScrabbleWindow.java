
package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;

import board.Board;
import board.Tile;
import player.Player;
import dictionary.Dawg;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.image.ImageObserver;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

/**
 *
 * @author sean
 */
public class ScrabbleWindow extends JFrame implements ActionListener{

    private class TilePlacement {
        int row;
        int col;
        char c;
    }

    public static final int TILE_SIZE = 10;

    private ArrayList<TilePlacement> currentMove;
    private Board board;
    private JPanel scorePanel = new JPanel();
    private JLabel score1Label = new JLabel();
    private JLabel score2Label = new JLabel();
    private JPanel gridPanel = new JPanel();
    private JPanel buttonPanel = new JPanel();
    private JButton[][] tiles;
    private ArrayList<JButton> toDisable;

    Callback scrabbleGame;

    //Implement player
    private Player player1, player2, currentPlayer;
    private ArrayList<Character> playerTiles;

    JButton newGameButton;
    JButton endTurnButton;
    GridAction gridAction;

    public ScrabbleWindow(Callback scrabbleGame, Board board, Player player1, Player player2, Player activePlayer) {
        setLayout(new BorderLayout());
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        setLocation(screenSize.width / 4, screenSize.height / 4);
        this.toDisable=new ArrayList<JButton>();
        this.board = board;
        this.player1 = player1;
        this.currentPlayer = activePlayer;
        this.player2 = player2;
        this.scrabbleGame = scrabbleGame;
        this.playerTiles = new ArrayList(this.currentPlayer.getTiles());
        tiles = new JButton[board.getSize()][board.getSize()];
        currentMove = new ArrayList<>();
        initGUI();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
    }

    public interface Callback
    {
        public boolean validMove(String checkWord);
        public Player switchPlayer();
    }

    public void initGUI(){
        System.out.println("Printing int val of a: " + ( 'a'+1-1));
        initButtons();
        initGrid();
        initScorePanel();
        add(buttonPanel, BorderLayout.NORTH);
        add(gridPanel);
        add(scorePanel, BorderLayout.SOUTH);
    }

    public void initButtons(){
        buttonPanel.setLayout(new GridLayout(1,2));
        newGameButton = new JButton("New Game");
        endTurnButton = new JButton("End Turn");
        newGameButton.addActionListener(this);
        endTurnButton.addActionListener(this);
        buttonPanel.add(newGameButton);
        buttonPanel.add(endTurnButton);
    }

    private void initGrid() {
        gridAction = new GridAction(this, currentPlayer);
        gridPanel.setLayout(new GridLayout(board.getSize(), board.getSize()));
        for (int row = 0; row < board.getSize(); row++) {
            for (int column = 0; column < board.getSize(); column++) {
                Tile tile = board.getTile(row, column);


                JButton button = new JButton();
                button.addActionListener(this);
                button.setText("" + tile.getContent());
                button.setSize(50, 50);
                //button.setAction(gridAction);

                tiles[row][column] = button;
                gridPanel.add(button);
            }
        }
    }

    public void initScorePanel() {
        scorePanel.setLayout(new GridLayout(1, 2));
        //score1Label.setText(player1.getTilesAsString());
        score1Label.setText("Player1 Tiles:");
        //score2Label.setText(player2.getTilesAsString());
        score2Label.setText("Player2 Tiles:");
        scorePanel.add(score1Label);
        scorePanel.add(score2Label);
    }

    public void updateScores(Player player, int turn) {
        String text = (turn < 0) ? player1.getClass().getSimpleName() : player2
                .getClass().getSimpleName();

        text = text + ": " + " (" + player.getScore() + ") points!";
        if (turn < 0) {
            score1Label.setText(text);
        } else {
            score2Label.setText(text);
        }
    }

    public void updateBoard() {
        for (int row = 0; row < board.getSize(); row++) {
            for (int column = 0; column < board.getSize(); column++) {
                Tile tile = board.getTile(row, column);
                tile.setContent(tiles[row][column].getText());
            }
        }
        for (int i=0; i<toDisable.size(); i++){
            toDisable.get(i).removeActionListener(this);
        }
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println("Button pressed!");
        if (e.getSource() == newGameButton) {
            System.out.println("Start new game needs to be implemented!");
            reset();
        }
        if (e.getSource() == endTurnButton) {
            for (int i=0; i<tiles.length; i++){
                for (int j=0; j<tiles.length; j++){
                    if (board.getTile(i, j).containsLetter()){
                        String s = tiles[i][j].getText();
                        tiles[i][j].removeActionListener(this);
                        tiles[i][j].setText(s);
                    }
                }
            }

            this.currentPlayer = scrabbleGame.switchPlayer();

        }
        Point p = buttonInArray(e.getSource());
        if (p != null){
            System.out.println("Found button in array");
            gridButtonPressed((JButton)e.getSource(), p);
        }
    }

    public void gridButtonPressed(JButton button, Point p){
        if (this.currentPlayer != player1){
            return;
        }
        String tilesAsString = new String();
        for(int i=0; i<playerTiles.size(); i++){
            tilesAsString += playerTiles.get(i);
            if (i != playerTiles.size()-1){
                tilesAsString += ", ";
            }
        }
        String s = (String) JOptionPane.showInputDialog(
                this,
                "Available letters:\n"
                        + tilesAsString.toUpperCase(),
                "Place letter",
                JOptionPane.PLAIN_MESSAGE);

        s = s.toLowerCase();

        if (s != null && s.length()==1){
            if (!button.getText().equals(" ")){
                char tmp = button.getText().charAt(0);
                playerTiles.add(tmp);
                button.setText("");

            }
            if (playerTiles.contains((s.charAt(0)))){
                if (s.charAt(0) == ' ') button.setText("");
                else{
                button.setText(new String(("" + s.charAt(0)).toUpperCase()));
                playerTiles.remove((Character)s.charAt(0));
                TilePlacement tP = new TilePlacement();
                tP.c = s.charAt(0);
                tP.row = p.x;
                tP.col = p.y;
                currentMove.add(tP);
                }
            }
        }
    }

    public Point buttonInArray(Object o){
        System.out.println("Checking if button is in array");
        for (int i=0; i<board.getSize(); i++){
            for (int j=0; j<board.getSize(); j++){
                if(tiles[i][j].equals(o))
                    return new Point(i,j);
            }
        }
        return null;
    }

    private boolean Point(int i, int i0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    class GridAction extends AbstractAction {

        private Frame window;
        private Player currentPlayer;
        public GridAction(Frame frame, Player currentPlayer) {
            this.window = frame;
            this.currentPlayer = currentPlayer;
        }

        public void actionPerformed(ActionEvent e) {

        }
    }


    public void reset() {
        // Reset Game
    }

}
