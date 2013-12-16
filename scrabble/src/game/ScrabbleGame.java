
package game;

import player.Player;
import board.Board;
import board.Tile;
import gui.ScrabbleWindow;
import dictionary.Dawg;
import gui.ScrabbleWindow.Callback;
import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;

/**
 *
 * @author sean
 */
public class ScrabbleGame implements Callback {

    static boolean debug = true;
    private String fileName;
    private List <Character> tilesLeft;
    private Dawg dD;
    final private Player player1, player2;
    private Player activePlayer;
    final private Board board;
    private ScrabbleWindow scrabbleWindow;

    ScrabbleGame(Board board, Player player1, Player player2, Dawg d) {
        this.dD = d;
        this.player1 = player1;
        this.activePlayer = player1;
        this.player2 = player2;

        this.board = board;
        this.tilesLeft = new ArrayList<Character>();
    }


    // Returns a pointer to the player that wins
    public GameResult play() {
        initializeGame();

        setGameWindow(new ScrabbleWindow(this, board, player1, player2, activePlayer));

        while (!gameOver()) {


            int numTilesToAdd=activePlayer.getNumTiles();
            addTiles(activePlayer, 7-numTilesToAdd);


        }


        GameResult result = new GameResult(player1, player2, true);


        //                
        // if (score1 > score2){
        // result = new GameResult(player1String, player2String, score1,
        // score2);
        // // System.out.println("Player 1 wins! " + score1 + " - " + score2);
        // else if (score2 == score1)
        // new GameResult(player1)
        // // System.out.println("Draw! " + score1 + " - " + score2);
        // else
        // new Game
        // // System.out.println("Player 2 wins! " + score2 + " - " + score1);

        return result;
    }

    public void addTiles(Player player, int numTiles){
        if (tilesLeft.size() > numTiles-1)
            player.addAllTiles(tilesLeft.subList(0, numTiles-1));
        else player.addAllTiles(tilesLeft);
        tilesLeft.removeAll(tilesLeft.subList(0, numTiles-1));
    }

    public void setGameWindow(ScrabbleWindow sW) {
        this.scrabbleWindow = sW;

        // dont know why... but i cant set this in ScrabbleWindow.java
        Dimension d = new Dimension(450, 500);
        this.scrabbleWindow.setSize(d);
    }

    public void switchTurn(){
        if (activePlayer == player1)
            activePlayer = player2;
        else
            activePlayer = player1;
    }
    public boolean isActive(Player player){
        if (player == activePlayer)
            return true;
        return false;
    }

    public boolean gameOver() {
        if (player1.getNumTiles()==0 && tilesLeft.isEmpty()) {
            return true;
        }
        if (player2.getNumTiles()==0 && tilesLeft.isEmpty()) {
            return true;
        }

        return false;
    }

    public void initializeGame(){

        Board boardObj = new Board(15);
        Player firstPlayer;
        Player secondPlayer;
        //GameResult gameResult = new ScrabbleGame(boardObj, player1, player2).play();
        generateTiles();

    }

    public void generateTiles(){
        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());

        for (int i = 0; i < 30+14; i++){
            int charVal = rand.nextInt(26);
            char newChar = (char)('a'+charVal);
            tilesLeft.add(newChar);
            //System.out.println("Added new char: " + newChar);

        }

        addTiles(player1, 7);
        addTiles(player2, 7);

        /*
        ArrayList <Character> player1Tiles = new ArrayList<Character>();
        ArrayList <Character> player2Tiles = new ArrayList<Character>();
        for (int i = 0; i < 7; i++){
            Character c = tilesLeft.get(i);
            player1Tiles.add(c);
            tilesLeft.remove(i);
            c = tilesLeft.get(i);
            player2Tiles.add(c);
            tilesLeft.remove(i);
            //System.out.println("Added new char: " + newChar);

        }
        */

    }

    @Override
    public boolean validMove(String checkWord){
        return dD.isAccepted(checkWord);
    }

    @Override
    public Player switchPlayer(){
        if (activePlayer == player1)
            activePlayer = player2;
        else activePlayer = player1;

        return activePlayer;
    }


    public static void main(String[] args) throws IOException {
        String fileName = new String("dictionary.txt");
        Dawg d = new Dawg();



        if (debug) System.out.print("Loading program... ");

        if (debug) System.out.println("done!");

        if (debug) System.out.println("Loading dictionary from " + fileName + ", this may take a minute... ");
        try{
            d.importDawg("dawg.txt");
        }
        catch(IOException ioE){
            System.out.println("Error: Could not read contents of file!");
        }
        if (debug) System.out.println("The dictionary is loaded, begin program.");

        if (debug){

            System.out.println("\nList of words loaded in the dawg: ");
            d.printDawg("dawgPrintout.txt");
        }

        Board boardObj = new Board(15);
        Player firstPlayer = new Player();
        Player secondPlayer = new Player();
        GameResult gameResult = new ScrabbleGame(boardObj, firstPlayer, secondPlayer, d).play();
        // Test a word for acceptance
        d.isAccepted("dog");
    }
}
