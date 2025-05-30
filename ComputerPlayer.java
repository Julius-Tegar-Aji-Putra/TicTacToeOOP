import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

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

        if (size == 3 && board.isValidMove(1, 1)) {
            return new int[]{1, 1};
        }

        List<int[]> cornerMoves = new ArrayList<>();
        if (size == 3) {
            if (board.isValidMove(0, 0)) cornerMoves.add(new int[]{0, 0});
            if (board.isValidMove(0, 2)) cornerMoves.add(new int[]{0, 2});
            if (board.isValidMove(2, 0)) cornerMoves.add(new int[]{2, 0});
            if (board.isValidMove(2, 2)) cornerMoves.add(new int[]{2, 2});
            
            if (!cornerMoves.isEmpty()) {
                Collections.shuffle(cornerMoves);
                return cornerMoves.get(0);
            }
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

    // Helper method untuk mencari langkah kemenangan untuk pemain tertentu
    private int[] findWinningMove(Board<G> board, Player<G> player) {
        int size = board.getSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board.isValidMove(i, j)) {
                    Cell<G> currentCell = board.getCell(i, j);
                    Player<G> originalPlayerInCell = currentCell.getPlayer(); // Simpan pemain asli

                    currentCell.setPlayer(player); // Coba tempatkan tanda pemain
                    
                    boolean wins = board.checkWin(); 
                    
                    currentCell.setPlayer(originalPlayerInCell); // Kembalikan sel ke kondisi semula (undo)
                                                              // Jika sebelumnya kosong, originalPlayerInCell akan null
                                                              // Jika clear() lebih sesuai, pastikan sel.clear() menangani ini.
                                                              // Atau, jika sel kosong, setPlayer(null) atau clear().
                                                              // Untuk konsistensi, jika originalPlayerInCell adalah null, kita bisa panggil clear().
                    if (originalPlayerInCell == null) {
                         currentCell.clear(); // Pastikan sel benar-benar kosong jika memang begitu
                    }


                    if (wins) {
                        // Verifikasi bahwa kemenangan adalah milik 'player' yang dites
                        // Ini penting karena checkWin() pada Board mungkin tidak spesifik pemain mana
                        currentCell.setPlayer(player); // Set lagi untuk verifikasi
                        boolean playerIsWinner = false;
                        if (board.checkWin()) { 
                           List<int[]> winningLine = board.getWinningLineCoordinates();
                           if (!winningLine.isEmpty()) {
                               Cell<G> firstCellInLine = board.getCell(winningLine.get(0)[0], winningLine.get(0)[1]);
                               if (firstCellInLine.getPlayer() == player) { // Periksa apakah pemain di garis kemenangan adalah 'player'
                                   playerIsWinner = true;
                               }
                           }
                        }
                        
                        // Undo lagi setelah verifikasi
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