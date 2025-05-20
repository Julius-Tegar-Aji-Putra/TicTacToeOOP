class Board<G> {
    private final int size;
    private final Cell<G>[][] cells;
    
    @SuppressWarnings("unchecked")
    public Board(int size) {
        this.size = size;
        cells = (Cell<G>[][]) new Cell[size][size];
        initializeBoard();
    }
    
    private void initializeBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cells[i][j] = new Cell<>(i, j);  // Initialize Cell with Player<G>
            }
        }
    }
    
    public int getSize() {
        return size;
    }
    
    public boolean isValidMove(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size && cells[row][col].isEmpty();
    }
    
    public void placeMark(int row, int col, Player<G> player) {
        cells[row][col].setPlayer(player);  // Place player on the cell
    }
    
    public Cell<G> getCell(int row, int col) {
        return cells[row][col];
    }
    
    public void reset() {
        initializeBoard();
    }
    
    // Check for win conditions
    public boolean checkWin() {
        // Check rows
        for (int i = 0; i < size; i++) {
            if (!cells[i][0].isEmpty() && checkRowWin(i)) {
                return true;
            }
        }
        
        // Check columns
        for (int i = 0; i < size; i++) {
            if (!cells[0][i].isEmpty() && checkColumnWin(i)) {
                return true;
            }
        }
        
        // Check diagonals
        if (!cells[0][0].isEmpty() && checkDiagonalWin()) {
            return true;
        }
        
        return !cells[0][size - 1].isEmpty() && checkAntiDiagonalWin();
    }
    
    private boolean checkRowWin(int row) {
        Player<G> player = cells[row][0].getPlayer();
        for (int i = 1; i < size; i++) {
            if (cells[row][i].isEmpty() || cells[row][i].getPlayer() != player) {
                return false;
            }
        }
        return true;
    }
    
    private boolean checkColumnWin(int col) {
        Player<G> player = cells[0][col].getPlayer();
        for (int i = 1; i < size; i++) {
            if (cells[i][col].isEmpty() || cells[i][col].getPlayer() != player) {
                return false;
            }
        }
        return true;
    }
    
    private boolean checkDiagonalWin() {
        Player<G> player = cells[0][0].getPlayer();
        for (int i = 1; i < size; i++) {
            if (cells[i][i].isEmpty() || cells[i][i].getPlayer() != player) {
                return false;
            }
        }
        return true;
    }
    
    private boolean checkAntiDiagonalWin() {
        Player<G> player = cells[0][size - 1].getPlayer();
        for (int i = 1; i < size; i++) {
            if (cells[i][size - 1 - i].isEmpty() || cells[i][size - 1 - i].getPlayer() != player) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isFull() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (cells[i][j].isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
}