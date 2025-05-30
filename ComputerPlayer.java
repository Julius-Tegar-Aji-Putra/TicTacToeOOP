import java.util.Random;

public class ComputerPlayer<G> implements Player<G> {
    private String name;
    private String symbol;
    private Random random = new Random();

    public ComputerPlayer(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void makeMove(Board<G> board, int row, int col) throws InvalidMoveException {
        if (!board.isValidMove(row, col)) {
            throw new InvalidMoveException("Invalid move.");
        }
        board.placeMark(row, col, this);  
    }

    public int[] findBestMove(Board<G> board) {
        int size = board.getSize();
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board.isValidMove(i, j)) {
                    try {
                        board.placeMark(i, j, this);
                        
                        if (board.checkWin()) {
                            board.getCell(i, j).clear();
                            return new int[]{i, j};
                        }
                        
                        board.getCell(i, j).clear();
                    } catch (Exception e) {

                        continue;
                    }
                }
            }
        }
        
        java.util.List<int[]> validMoves = new java.util.ArrayList<>();
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board.isValidMove(i, j)) {
                    validMoves.add(new int[]{i, j});
                }
            }
        }
        
        if (!validMoves.isEmpty()) {
            return validMoves.get(random.nextInt(validMoves.size()));
        }
        
        return null; 
    }

    @Override
    public G getPlayerType() {
        @SuppressWarnings("unchecked")
        G type = (G) "Computer";
        return type; 
    }
}