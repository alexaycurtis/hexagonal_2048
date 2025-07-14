import java.util.*;
public class Game{
  public Board board;
  public QueueQ nextTileQueue;//integer queue with the next value of tiles
  public Stack lastBoard;
  public Stack lastMove;
  public int score;
  boolean moveMade;//tracks if move has been made so that the update method will know
  boolean gameOver;
  boolean doNotSpawn;
  
  public Game(){
    board = new Board();
    nextTileQueue = new QueueQ(10);//just include the next few items that are able to be accessed by powerups, so the queue doesn't take too much memory
    lastBoard = new Stack(5); //rule that can't use more than five undos in a row 
    lastMove = new Stack(5);
    score = 0;
    gameOver = false;
    
    //queue 10 random values to the queue
    for(int i = 0; i<10;i++){
      nextTileQueue.enqueue(generateRandomTileValue());
    }
  } 
  
  //GAMEPLAY
  public void update(){
    
    if(moveMade){
      lastBoard.push(deepCopyBoard(board.grid));
      if(lastBoard.isFull()){
        lastBoard.popLast();
      }
      if(lastMove.isFull()){
        lastMove.popLast();
      }
      
      ArrayList<int[]> emptySpots = this.getEmptySpotsList();
      if(emptySpots.size() != 0 && !doNotSpawn){
        this.spawnTile(nextTileQueue.peek(), emptySpots);
        score += nextTileQueue.peek();
        nextTileQueue.dequeue();
        nextTileQueue.enqueue(generateRandomTileValue());
      }
      
      moveMade = false;
      doNotSpawn = false;
      
      if(this.isGameOver()){
        gameOver = true;
      }
    }
  }
  
  public int generateRandomTileValue(){//returns either 2 or 4, with a higher likelihood to return 2
    double likelyValue = Math.random();
    int newValue = 0;
    if(likelyValue < 0.3){
      newValue = 4;
    }
    else if(likelyValue > 0.3){
      newValue = 2;
    }
    return newValue;
  }
  
  public void spawnTile(int value, ArrayList<int[]> emptySpotsList){//chooses a random empty location and inserts the tile
    //randomly choose location from empty spot
    //randomly pick from the empty spots
    int random = (int) Math.floor(Math.random()*emptySpotsList.size());
    
    //insert tile where it belongs
    int row = emptySpotsList.get(random)[0];
    int col = emptySpotsList.get(random)[1];
    board.grid[row][col].value = value;//set the empty location to added tile value
  }
  
  public ArrayList<int[]> getEmptySpotsList(){//helper method to get each index of an empty spot into an arraylist
    ArrayList<int[]> emptySpots = new ArrayList<>();
    for(int i = 0; i<board.grid.length; i++){
      for(int j = 0; j<board.grid[i].length;j++){
        if(board.grid[i][j].value == 0){
          int[] tempArr = {i,j};
          emptySpots.add(tempArr);
        }
      }
    }
    return emptySpots;
  }
  
  public boolean canMerge(Tile t1, Tile t2){//returns true if two tiles can merge (if they have the same value)
    if(t1.value == t2.value){
      return true;
    }
    else{
      return false;
    }
  }
  
  //POWERUPS
  public void undoFunction(){//takes the field variable stack that has all the last played tiles
    //1. remove just placed tile
    lastBoard.pop();
    Tile[][] previousBoard = (Tile[][]) lastBoard.pop();
    for(int i = 0; i<board.grid.length;i++){//reassign board values to the previous board
      for(int j = 0; j<board.grid[i].length;j++){
        board.grid[i][j].setValue(previousBoard[i][j].value);
      }
  }
  }
  
  private Tile[][] deepCopyBoard(Tile[][] original){
    int[] rowSizes = {3, 4, 5, 4, 3};
    Tile[][] copy = new Tile[5][]; 
    for(int row = 0; row < board.grid.length; row++){
      copy[row] = new Tile[rowSizes[row]]; //Initialize the row 
      for(int col = 0; col < board.grid[row].length; col++){
          copy[row][col] = new Tile(0); //Initialize each Tile object
      }
    }
    for(int i = 0; i < board.grid.length; i++){//copy the values from the original to the copy
      for(int j = 0; j < board.grid[i].length; j++){
          copy[i][j].value = original[i][j].value;
      }
    }
    return copy;
  }
  
  public void shuffleQueueFunction(){//takes the nextTileQueue, which stores ten of the next tiles, and mixes them around. This queue will always have ten next tiles which are randomly generated
    nextTileQueue.shuffle();//this is implemented in the QueueQ class
  }
  
  //NOTE: The two powerups: delete on click and add on click are managed in the main method.
  
  public void deleteFirstQueue(){//takes the next tiles queue and dequeues the top, and enqueues a random one on the end
    nextTileQueue.dequeue();
    nextTileQueue.enqueue(generateRandomTileValue());
  }
  
  public void addHighNumQueue(){//weighted random chance of adding higher number. 10% chance of adding 128, 40% of 64, 50% of 32.
    double rand = Math.random();
    nextTileQueue.dequeue();
    if(rand <0.1){
      nextTileQueue.enqueue(128);
    }
    else if(rand <0.5){
      nextTileQueue.enqueue(64);
    }
    else if(rand <= 1){
      nextTileQueue.enqueue(32);
    }
  }
  
  
  public boolean isGameOver(){//scans board to find any potential pairs in any direction. if there are no pairs AND NO SPACE, return true, the game is over
    if(board.isFull()){//the board being full is the only possible way it could be gameover
      for(int i = 0; i<board.grid.length; i++){
        for(int j = 0; j<board.grid[i].length;j++){
          
          //CHECK moves in all six directions
          
          //horizontal right
          if(j+1 < board.grid[i].length){//means there is a tile to the right of 
            if(canMerge(board.grid[i][j], board.grid[i][j+1])){
              return false;
            }
          }
          //horizontal left
          if(j-1 >= 0){//means there is a tile to the left of 
            if(canMerge(board.grid[i][j], board.grid[i][j-1])){
              return false;
            }
          }
          
          //DIAGONALS: Split the board between top two rows, middle row, and bottom two rows (like in the movement)
          
          //Diagonal Ups for First Two Rows:
          if(i < 2){//rows 1 and 2
          
            //diagonal ups don't apply to the first row
            if(i != 0){//row 2
            
              if(j!=0){//diagonal up left doesn't apply to index 0 in  either row so eliminate the option
                //test diagonal up right
                if(canMerge(board.grid[i][j], board.grid[i-1][j-1])){
                  return false;
                }
              }
              
              else if(j < board.grid[i].length-1){//diagonal up right doesn't apply to the last index in row 2
                //test diagonal up left
                if(canMerge(board.grid[i][j], board.grid[i-1][j])){
                  return false;
                }
              }
            }
            
          //Diagonal Downs for First Two Rows:
          
            //diagonal down left test just checks the same index in the next row, and no bounds checks are required
            if(canMerge(board.grid[i][j], board.grid[i+1][j])){//diagonal down left test
              return false;
            }
            
            //diagonal down right
            //this direction tests the index on the right down one row. no bounds checks are necessary at this point since we're not involving decreasing row lengths
            if(canMerge(board.grid[i][j], board.grid[i+1][j+1])){//diagonal down right test
              return false;
            }
          }
          
          //Diagonal Ups and Downs for the Middle Row
          
          else if(i==2){
            //diagonal up left will start at 2nd square and move to 1st square in row above
            //diagonal down left will start at 2nd square and move to 1st square in row below
            //diagonal up rights will go straight up for all squares except last column
            //diagonal down right will go directly down except last column
            
            //Left Diagonals:
            if(j!=0){//to go left, the column can't be 0
              //test if can merge up left or down left
              if(canMerge(board.grid[i][j], board.grid[i-1][j-1])){//up left
                return false;
              }
              
              if(canMerge(board.grid[i][j], board.grid[i+1][j-1])){//down left
                return false;
              }
              
            }
            //Right Diagonals:
            else if(j!=4){//to go right, column can't be the last square (which is 4)
              if(canMerge(board.grid[i][j], board.grid[i-1][j+1])){//up right
                return false;
              }
              if(canMerge(board.grid[i][j], board.grid[i+1][j])){//down right
                return false;
              }
            }
          }
          else{//rows 4,5
            //downs don't apply to the last row
            if(i!=4){//row 4
              if(j!=3){//diagonal up left (same col), but doesn't include last column
                if(canMerge(board.grid[i][j], board.grid[i-1][j])){//up left
                  return false;
                }
                if(canMerge(board.grid[i][j], board.grid[i+1][j])){//down right lines up so doesn't include last column
                  return false;
                }
              }
              if(j!=0){//diagonal up and down right don't include the first column
                if(canMerge(board.grid[i][j], board.grid[i-1][j+1])){//up right
                  return false;
                }
                //diagonal down left also doesn't include first column
                if(canMerge(board.grid[i][j], board.grid[i+1][j-1])){//down left
                  return false;
                }
              }  
            }
            else{//row 5
              //diagonal up left
              if(j!=2 && canMerge(board.grid[i][j], board.grid[i-1][j])){
                return false;
              }
              //diagonal up right
              if(j!=0 && canMerge(board.grid[i][j], board.grid[i-1][j+1])){
                return false;
              }
            }       
          }
        }
      }
      return true;//if the board is full and none of the previous checks return false, then it is actually game over
    }
    return false;//not game over because board is not full
  }
  
  //DIAGONAL MOVEMENT (up and down don't exist)
  //Several considerations occurred before I decided to implement a copy-pasted approach rather than an algorithmic one
  //The first consideration is that for each diagonal, the tiles had to be processed in a different order for the movement to happen on the whole board.
  //This was the primary factor in deciding to copy paste logic, just because an algorithm would not be more time efficient at all.


  //Direction constants for diagonal movements
  public static final int DIAGONAL_UP_LEFT = 0;
  public static final int DIAGONAL_UP_RIGHT = 1;
  public static final int DIAGONAL_DOWN_LEFT = 2;
  public static final int DIAGONAL_DOWN_RIGHT = 3;
  
  public void moveDiagonal(int direction){//Process the board in the correct order for the given direction
      processBoard(direction);
  }
  
  private void processBoard(int direction){
      int rows = board.grid.length;
      boolean movesMade;
      
      do{//need a do while loop to make sure the tiles continuously move in a row even if merging occurs
        movesMade = false;
        
        //initialize start, end, and step
        int rowStart, rowEnd, rowStep;
        int colStart, colEnd, colStep;
        
        //set order to process in based on direction
        if(direction == DIAGONAL_UP_LEFT || direction == DIAGONAL_UP_RIGHT) {
            //process from bottom to top for upward movements
            rowStart = rows - 1;
            rowEnd = -1;
            rowStep = -1;
        }
        else{
            //process from top to bottom for downward movements
            rowStart = 0;
            rowEnd = rows;
            rowStep = 1;
        }
        
        if(direction == DIAGONAL_UP_LEFT || direction == DIAGONAL_DOWN_LEFT){
            //process from right to left for leftward movements
            colStart = board.grid[0].length - 1;
            colEnd = -1;
            colStep = -1;
        }
        else{
            //process from left to right for rightward movements
            colStart = 0;
            colEnd = board.grid[0].length;
            colStep = 1;
        }
        
        //process rows
        for(int i = rowStart; i != rowEnd; i += rowStep) {
            // Adjust column limits based on current row
            colStart = (colStep > 0) ? 0 : board.grid[i].length - 1;
            colEnd = (colStep > 0) ? board.grid[i].length : -1;
            
            //process columns
            for(int j = colStart; j != colEnd; j += colStep){
                if(board.grid[i][j].value > 0){
                    //make sure to move tile as far as possible in the given direction
                    int originalValue = board.grid[i][j].value;
                    int originalRow = i;
                    int originalCol = j;
                    moveTile(i, j, direction);
                    
                    if(board.grid[originalRow][originalCol].value != originalValue){
                       movesMade = true;
                    }
                }
            }
        }
     } while(movesMade);
  }
  
  public void moveTile(int row, int col, int direction){//this method continues moving a tile so that it "slides" down a diagonal
      if(board.grid[row][col].value == 0) return;
      int currentValue = board.grid[row][col].value;
      int currentRow = row;
      int currentCol = col;
      int nextRow, nextCol;
      
      //keep moving until we hit a wall or another tile
      while(true){
          int[] next = getNextPosition(currentRow, currentCol, direction);
          nextRow = next[0];
          nextCol = next[1];
          
          //check if hit boundary
          if(!isValidPosition(nextRow, nextCol)){
              break;
          }
          
          //move to empty cell
          if(board.grid[nextRow][nextCol].value == 0){
              board.grid[nextRow][nextCol].value = currentValue;
              board.grid[currentRow][currentCol].value = 0;
              currentRow = nextRow;
              currentCol = nextCol;
              continue;
          }
          
          //find cell with the same value so merge
          if(board.grid[nextRow][nextCol].value == currentValue){
              board.grid[nextRow][nextCol].value *= 2;
              score += board.grid[nextRow][nextCol].value;
              board.grid[currentRow][currentCol].value = 0;
          }
          
          //stop when done
          break;
      }
  }
  
  private int[] getNextPosition(int row, int col, int direction){//this method gets the next position of a sliding tile based on row
      switch(direction){
          case DIAGONAL_UP_LEFT:
              if(row == 0) return new int[]{-1, -1};//Out of bounds
              
              if(row == 1 || row == 2){//rows 1 and 2(2nd and 3rd)
                  return new int[]{row - 1, col - 1};
                  
              }
              else{ //rows 4 and 5
                  return new int[]{row - 1, col};
              }
              
          case DIAGONAL_UP_RIGHT:
              if(row == 0) return new int[]{-1, -1};//Out of bounds
              
              if(row == 2 || row == 1){ //rows 1 and 2(2nd and 3rd)
                  return new int[]{row - 1, col};
              } 
              else{ // rows 4 and 5
                  return new int[]{row - 1, col + 1};
              }
              
          case DIAGONAL_DOWN_LEFT:
              if (row == 4) return new int[]{-1, -1};//Out of bounds
              
              if(row < 2){ //rows 0 and 1
                  return new int[]{row + 1, col};
              } 
              else{ // rows 2 and 3
                  return new int[]{row + 1, col - 1};
              }
              
          case DIAGONAL_DOWN_RIGHT:
              if (row == 4) return new int[]{-1, -1};//Out of bounds
              
              if(row < 2){ //rows 0 and 1
                  return new int[]{row + 1, col + 1};
              } 
              else{ //rows 2 and 3
                  return new int[]{row + 1, col};
              }
              
          default:
              return new int[]{row, col}; // No change
      }
  }
  
  private boolean isValidPosition(int row, int col) {
      return row >= 0 && row < board.grid.length && col >= 0 && col < board.grid[row].length;
  }
  
  //helpers for main method to call specific diagonals
  public void moveDiagonalUpLeft(){
      moveDiagonal(DIAGONAL_UP_LEFT);
  }
  
  public void moveDiagonalUpRight(){
      moveDiagonal(DIAGONAL_UP_RIGHT);
  }
  
  public void moveDiagonalDownLeft(){
      moveDiagonal(DIAGONAL_DOWN_LEFT);
  }
  
  public void moveDiagonalDownRight(){
      moveDiagonal(DIAGONAL_DOWN_RIGHT);
  }
  
  //Notes on checking for boundaries (horizontally): To properly handle a hexagonal or irregular board, you would need to:
    //1. Check if the column exists in each row being processed
    //2. Define column-specific borders rather than global row borders
    //3. Skip entirely or have special handling for columns that don't exist in certain rows
    
  public void horizontalMove(String direction){
    for(int row = 0; row<board.grid.length; row++){
      int rowLength = board.grid[row].length;//number of columns in each row
      int start = direction.equals("right") ? rowLength - 1 : 0;//choose starting index based on the direction of the movement
      int step = direction.equals("right") ? -1 : 1;
      
      int border = start;//keep track of where the board ends, and this also keeps track of where the shift should be going
      int counter = start;
      while(counter>= 0 && counter<rowLength){
        Tile current = board.grid[row][counter];
        Tile target = board.grid[row][border];//tile we're attemtping to slide into. if it matches then we're able to merge it
        
        if(current.value == 0){//skip empty tiles
          counter += step;
          continue;
        }
        
        if(counter == border){//skip if comparing tile to self (the board ended)
          counter += step;
          continue;
        }
        
        if(target.value == 0){//shifting tile into open space is allowed
          target.value = current.value;
          current.value = 0;
          counter += step;
        }
        else if(canMerge(current, target)){//check if can merge
          int newValue = target.value + current.value;//add tile value for merging
          target.value = newValue;
          current.value = 0; //the value is stored in target, and current will appear as an empty tile
          score += newValue;
          border += step;
          counter += step;
          
        }
        else{//no merge
          border += step;
          if(border == counter){
            counter += step;
          }
        }
      }
    }
  } 
}
