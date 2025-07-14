import java.util.*;

public class Board{
  public Tile[][] grid;
  
    @SuppressWarnings("unchecked") //Stop generic array warning (if decide to use array of Lists)
    public Board(){
        int[] rowSizes = {3, 4, 5, 4, 3}; //Define the number of columns for each row

        grid = new Tile[5][]; //Array of arrays of different sizes

        for (int row = 0; row < grid.length; row++) {
          grid[row] = new Tile[rowSizes[row]]; // Initialize the row
          
          for (int col = 0; col < grid[row].length; col++) {
              grid[row][col] = new Tile(0); // ✅ Initialize each Tile object
          }
        }
    }
    
    public boolean isFull(){//method to check if each part of the board is filled with a tile
      for(int i =0;i<grid.length;i++){
        for(int j =0; j<grid[i].length;j++){
          if(grid[i][j].value == 0){
            return false;
        }
      }
    }
    return true;
   } 
}
