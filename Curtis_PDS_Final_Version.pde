import processing.core.PApplet;

  int squareSize = 40;
  int spacing = 5;
  Game game;
  Tile[][] gameBoard;//a tile board with the same dimensions of game.grid, but this is mostly for drawing on the screen
  int clickedRow = -1;
  int clickedCol = -1;
  String clickCommand = "";
  boolean waitingForInput = false;

  public void settings() {
    size(400, 400);
  }

  public void setup() {
    background(152, 190, 100);
    game = new Game();
    gameBoard = game.board.grid;
    frameRate(30);
  }

  public void draw() {    
    background(152, 190, 100);
    game.update();//this update method will be able to process moves, update tiles, and allow them to be redrawn after each turn
    
    float startY = height/2 - (5 * squareSize + 4 * spacing)/2;
    
    drawPowerUpInstructions();
    
    // Draw first row (3 squares)
    drawRow(gameBoard[0], 3, startY);
    
    // Draw second row (4 squares)
    drawRow(gameBoard[1], 4, startY + squareSize + spacing);
    
    // Draw third row (5 squares)
    drawRow(gameBoard[2], 5, startY + 2 * (squareSize + spacing));
    
    // Draw fourth row (4 squares)
    drawRow(gameBoard[3], 4, startY + 3 * (squareSize + spacing));
    
    // Draw fifth row (3 squares)
    drawRow(gameBoard[4], 3, startY + 4 * (squareSize + spacing));
    
    //Draw score at bottom
    drawScore(game.score);
    
    drawNextTileValue(game.nextTileQueue.peek());
    
    if(waitingForInput){
      displayInputInstructions();//do this later 
    }
    
    if(game.gameOver){
      System.out.println("GAME OVER");
    }
    
  }
  
  public void keyPressed(){
    if(waitingForInput){
      if(key == '5'){//delete click command
        if(gameBoard[clickedRow][clickedCol].value != 0){
            clickCommand = "delete";
            executeClickCommand();//do this later
        }
      }
      else if(key=='6'){//insert click command
        if (gameBoard[clickedRow][clickedCol].value == 0) {
            clickCommand = "add";
            executeClickCommand();
        }
      }
      else if(key == 'x' || key == 'X'){
        waitingForInput = false;
      }
      return;
    }

    
    if(key == 'q' || key == 'Q'){//up left move
      game.moveDiagonalUpLeft();
      game.lastMove.push("left-up");
      game.moveMade = true;
    }
    if(key == 'e' || key == 'E'){//up right move
      game.moveDiagonalUpRight();
      game.lastMove.push("right-up");
      game.moveMade = true;
    }
    if(key == 'a' || key == 'A'){//down left move
      game.moveDiagonalDownLeft();
      game.lastMove.push("left-down");
      game.moveMade = true;
    }
    if(key == 'd' || key == 'D'){//down right move
      game.moveDiagonalDownRight();
      game.lastMove.push("right-down");
      game.moveMade = true;
    }
    
    //up and down don't exist because everything is on diagnols and rows
    if(key == CODED && keyCode == RIGHT){//right move
      game.horizontalMove("right");
      game.lastMove.push("right");
      game.moveMade = true;
    }
    if(key == CODED && keyCode == LEFT){//left move
      game.horizontalMove("left");
      game.lastMove.push("left");
      game.moveMade = true;
    }
    
    
    if(key == '1'){//powerup 1
      game.undoFunction();
      game.lastBoard.push((Tile[][]) game.board.grid);
      game.moveMade = true;
      game.doNotSpawn = true;
    }
    if(key == '2'){//powerup 2
      game.shuffleQueueFunction();
    }
    if(key == '3'){//powerup 3
      game.deleteFirstQueue();
    }
    if(key == '4'){//powerup 4
      game.addHighNumQueue();
    }
  }
  
  public void mousePressed(){
    //Calculate which tile was clicked
    float startY = height/2 - (5 * squareSize + 4 * spacing)/2;
    int row = -1;
    int col = -1;
    
    //Check first row (3 cols)
    if(isInRow(mouseX, mouseY, 3, startY)){
      row = 0;
      col = getColumnInRow(mouseX, 3);
    }
    //Check second row (4 cols)
    else if(isInRow(mouseX, mouseY, 4, startY + squareSize + spacing)){
      row = 1;
      col = getColumnInRow(mouseX, 4);
    }
    //Check third row (5 cols)
    else if(isInRow(mouseX, mouseY, 5, startY + 2 * (squareSize + spacing))){
      row = 2;
      col = getColumnInRow(mouseX, 5);
    }
    //Check fourth row (4 cols)
    else if(isInRow(mouseX, mouseY, 4, startY + 3 * (squareSize + spacing))){
      row = 3;
      col = getColumnInRow(mouseX, 4);
    }
    //Check fifth row (3 cols)
    else if(isInRow(mouseX, mouseY, 3, startY + 4 * (squareSize + spacing))){
      row = 4;
      col = getColumnInRow(mouseX, 3);
    }
    
    if (row != -1 && col != -1) {
      clickedRow = row;
      clickedCol = col;
      waitingForInput = true;
    }
    
  }
  public boolean isInRow(float x, float y, int squares, float rowY){//takes the x and y coord, the number of columns/squares in a row, and the row number
    float totalRowWidth = (squares*squareSize) + (squares-1)*spacing;
    float startX = (width/2) - (totalRowWidth/2);
    
    return x >= startX && x <= startX + totalRowWidth && y >= rowY && y <= rowY + squareSize;
  }

  public int getColumnInRow(float x, int squares) {//takes x coord and the number of squares in a row
    float totalRowWidth = (squares*squareSize) + (squares-1)*spacing;
    float startX = (width/2) - (totalRowWidth/2);
    
    for(int i = 0; i < squares; i++){
      float tileX = startX + i*(squareSize + spacing);
      if(x >= tileX && x <= tileX + squareSize){
        return i;
      }
    }
    return -1;
  }
  
  private void executeClickCommand(){
    if(clickCommand.equals("delete")){
      //display the delete
      game.score = game.score - game.board.grid[clickedRow][clickedCol].value;
      game.board.grid[clickedRow][clickedCol].value = 0;
    }
    else if(clickCommand.equals("add")){
      //add function
      game.board.grid[clickedRow][clickedCol].value = game.nextTileQueue.dequeue();
      game.score = game.score + game.board.grid[clickedRow][clickedCol].value;
    }
    
    // Reset clicked state
    waitingForInput = false;
    clickedRow = -1;
    clickedCol = -1;
    clickCommand = "";
  }
  
  public void displayInputInstructions() {
    fill(0, 0, 0, 180);//transparentish black background
    rect(0, height - 90, width, 40);//in rectangle
    
    fill(255); //white text bc black backgorund
    textAlign(CENTER, CENTER);
    textSize(14);
    
    String instructions;
    if(gameBoard[clickedRow][clickedCol].value == 0) {
      instructions = "Press 6 to add a tile, 'x' to cancel";
    }
    else{
      instructions = "Press 5 to delete tile, 'x' to cancel";
    }
    text(instructions, width/2, height - 70);
  }
  
  private void drawRow(Tile[] row, int squares, float y) {
    float totalRowWidth = squares * squareSize + (squares-1) * spacing;
    float startX = width/2 - totalRowWidth/2;
    
    for (int i = 0; i < squares; i++) {
      fill(255);
      rect(startX + i * (squareSize + spacing), y, squareSize, squareSize);
      
      fill(0);
      textAlign(CENTER, CENTER);
      textSize(16);
      text(row[i].value, startX + i * (squareSize + spacing) + squareSize / 2, y + squareSize / 2);
    }
  }
  
  private void drawPowerUpInstructions(){
    fill(0);
    textAlign(CENTER, TOP);
    textSize(12);
    text("UNDO [1]  SHUFFLE QUEUE [2]  DELETE NEXT [3]", width/2, height-370);
    text("HIGH NUMBER [4]  DELETE TILE [CLCK + 5]  INSERT NEXT TILE [CLCK + 6]",width/2, height-350);
  }
  
  private void drawScore(int newScore){
    fill(0); // Set text color (black)
    textAlign(CENTER, BOTTOM); //Center the text and align to bottom
    textSize(20); //Set text size
    text("Score: " + newScore, width / 2, height - 10); // Position near the bottom
  }
  
  private void drawNextTileValue(int tileValue){
    fill(0);
    textAlign(CENTER, TOP);
    textSize(20);
    text("Next Tile: " + tileValue, width / 2, height -60);
  }
