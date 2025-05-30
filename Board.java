import java.util.ArrayList;
import java.util.List;

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
                cells[i][j] = new Cell<>(i, j);  
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
        cells[row][col].setPlayer(player);  
    }
    
    public Cell<G> getCell(int row, int col) {
        return cells[row][col];
    }
    
    public void reset() {
        initializeBoard();
    }
    

    public boolean checkWin() {
        for (int i = 0; i < size; i++) {
            if (!cells[i][0].isEmpty() && checkRowWin(i)) {
                return true;
            }
        }
        

        for (int i = 0; i < size; i++) {
            if (!cells[0][i].isEmpty() && checkColumnWin(i)) {
                return true;
            }
        }
        
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

    public List<int[]> getWinningLineCoordinates() {
        List<int[]> winningLine = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            if (!cells[i][0].isEmpty()) {
                Player<G> firstPlayer = cells[i][0].getPlayer();
                boolean rowWin = true;
                List<int[]> potentialLine = new ArrayList<>();
                potentialLine.add(new int[]{i, 0});
                for (int j = 1; j < size; j++) {
                    if (cells[i][j].isEmpty() || cells[i][j].getPlayer() != firstPlayer) {
                        rowWin = false;
                        break;
                    }
                    potentialLine.add(new int[]{i, j});
                }
                if (rowWin) {
                    return potentialLine;
                }
            }
        }

        for (int j = 0; j < size; j++) {
            if (!cells[0][j].isEmpty()) {
                Player<G> firstPlayer = cells[0][j].getPlayer();
                boolean colWin = true;
                List<int[]> potentialLine = new ArrayList<>();
                potentialLine.add(new int[]{0, j});
                for (int i = 1; i < size; i++) {
                    if (cells[i][j].isEmpty() || cells[i][j].getPlayer() != firstPlayer) {
                        colWin = false;
                        break;
                    }
                    potentialLine.add(new int[]{i, j});
                }
                if (colWin) {
                    return potentialLine;
                }
            }
        }

        if (!cells[0][0].isEmpty()) {
            Player<G> firstPlayer = cells[0][0].getPlayer();
            boolean diagWin = true;
            List<int[]> potentialLine = new ArrayList<>();
            potentialLine.add(new int[]{0, 0});
            for (int i = 1; i < size; i++) {
                if (cells[i][i].isEmpty() || cells[i][i].getPlayer() != firstPlayer) {
                    diagWin = false;
                    break;
                }
                potentialLine.add(new int[]{i, i});
            }
            if (diagWin) {
                return potentialLine;
            }
        }

        if (!cells[0][size - 1].isEmpty()) {
            Player<G> firstPlayer = cells[0][size - 1].getPlayer();
            boolean antiDiagWin = true;
            List<int[]> potentialLine = new ArrayList<>();
            potentialLine.add(new int[]{0, size - 1});
            for (int i = 1; i < size; i++) {
                if (cells[i][size - 1 - i].isEmpty() || cells[i][size - 1 - i].getPlayer() != firstPlayer) {
                    antiDiagWin = false;
                    break;
                }
                potentialLine.add(new int[]{i, size - 1 - i});
            }
            if (antiDiagWin) {
                return potentialLine;
            }
        }
        
        return winningLine; 
    }
}