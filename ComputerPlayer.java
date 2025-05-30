import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class ComputerPlayer<G> extends AbstractPlayer<G> {
    private Random random = new Random();

    public ComputerPlayer(String name, String symbol) {
        super(name, symbol);
    }

    @Override
    protected void validateMove(Board<G> board, int row, int col) throws InvalidMoveException {
        if (!board.isValidMove(row, col)) {
            throw new InvalidMoveException("Invalid move.");
        }
    }

    private Player<G> getOpponentPlayer(GameController<G> controller) {
        if (controller != null) {
            List<Player<G>> players = controller.getPlayers(); 
            for (Player<G> p : players) {
                if (p != this) { 
                    return p;
                }
            }
        }
        return null; 
    }

    public int[] findBestMove(Board<G> board, GameController<G> controller) {
        int size = board.getSize();

        int[] winningMove = findWinningMove(board, this);
        if (winningMove != null) {
            return winningMove;
        }

        Player<G> opponentPlayer = getOpponentPlayer(controller);
        if (opponentPlayer != null) {
            int[] blockingMove = findWinningMove(board, opponentPlayer);
            if (blockingMove != null) {
                return blockingMove;
            }
        }

        if (size % 2 != 0) { 
            int middle = size / 2;
            if (board.isValidMove(middle, middle)) {
                return new int[]{middle, middle};
            }
        }

        List<int[]> cornerMoves = new ArrayList<>();
        if (board.isValidMove(0, 0)) cornerMoves.add(new int[]{0, 0});
        if (board.isValidMove(0, size - 1)) cornerMoves.add(new int[]{0, size - 1});
        if (board.isValidMove(size - 1, 0)) cornerMoves.add(new int[]{size - 1, 0});
        if (board.isValidMove(size - 1, size - 1)) cornerMoves.add(new int[]{size - 1, size - 1});
        
        if (!cornerMoves.isEmpty()) {
            Collections.shuffle(cornerMoves);
            return cornerMoves.get(0);
        }

        List<int[]> validMoves = new ArrayList<>();
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


    private int[] findWinningMove(Board<G> board, Player<G> player) {
        int size = board.getSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board.isValidMove(i, j)) {
                    Cell<G> currentCell = board.getCell(i, j);
                    Player<G> originalPlayerInCell = currentCell.getPlayer(); 

                    currentCell.setPlayer(player); 
                    
                    boolean wins = board.checkWin(); 
                    
                    currentCell.setPlayer(originalPlayerInCell); 
                    if (originalPlayerInCell == null) {
                         currentCell.clear(); 
                    }


                    if (wins) {
                        currentCell.setPlayer(player); 
                        boolean playerIsWinner = false;
                        if (board.checkWin()) { 
                           List<int[]> winningLine = board.getWinningLineCoordinates();
                           if (!winningLine.isEmpty()) {
                               Cell<G> firstCellInLine = board.getCell(winningLine.get(0)[0], winningLine.get(0)[1]);
                               if (firstCellInLine.getPlayer() == player) {
                                   playerIsWinner = true;
                               }
                           }
                        }
                        
                        currentCell.setPlayer(originalPlayerInCell);
                        if (originalPlayerInCell == null) {
                            currentCell.clear();
                        }

                        if (playerIsWinner) {
                           return new int[]{i, j};
                        }
                    }
                }
            }
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