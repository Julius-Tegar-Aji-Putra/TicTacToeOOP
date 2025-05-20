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
        board.placeMark(row, col, this);  // Set this player to the cell
    }

    // Find best move for computer - for now just returns a random valid move
    public int[] findBestMove(Board<G> board) {
        // For simplicity, just return a random valid move
        int size = board.getSize();
        
        // First check if we can win in one move
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board.isValidMove(i, j)) {
                    try {
                        // Temporarily place mark
                        board.placeMark(i, j, this);
                        
                        // Check if this is a winning move
                        if (board.checkWin()) {
                            // Undo the move
                            board.getCell(i, j).clear();
                            return new int[]{i, j};
                        }
                        
                        // Undo the move
                        board.getCell(i, j).clear();
                    } catch (Exception e) {
                        // If any error occurs, continue to next cell
                        continue;
                    }
                }
            }
        }
        
        // If we can't win, find any random valid move
        // Create a list of valid moves
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
        
        return null; // No valid moves
    }

    @Override
    public G getPlayerType() {
        return (G) "Computer"; // Untuk menandakan tipe pemain
    }
}