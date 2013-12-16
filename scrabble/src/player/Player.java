/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package player;

import board.Board;
import board.Tile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
/**
 *
 * @author sean
 */
public class Player {
    ArrayList <Character> playerTiles;
    Board board;
    int score;
    
    public Player(){
        this.board = null;
        this.score = 0;
        this.playerTiles = new ArrayList<Character>();
    }
    
    public int makeMove(Board board){
        int numTilesUsed = 0;
        // make moves and return tiles used
        return numTilesUsed;
    }
    
    public int getScore(){
        return this.score;
    }
    
    public int getNumTiles(){
        return this.playerTiles.size();
    }
    
    public ArrayList<Character> getTiles(){
        return playerTiles;
    }
    
    public Character getTileAt(int index){
        return playerTiles.get(index);
    }
    
    public void addAllTiles(Collection c){
        playerTiles.addAll(c);
    }

    public void addTile(Character c){
        playerTiles.add(c);
    }
    
    public void removeTile(char c){
        playerTiles.remove((Character)c);
    }
    
    public void makeMove(){
        
    }
}
