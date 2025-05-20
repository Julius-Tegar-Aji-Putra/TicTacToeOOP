import java.util.ArrayList;
import java.util.List;

class GameController<G> {
    private final Board<G> board;
    private KoleksiPlayer<G> players;  // Changed to non-final to allow resetting
    private final List<GameObserver> observers;
    private int currentPlayerIndex;
    private boolean gameOver;
    private Player<G> winner;
    private final GameDataPersistence dataPersistence;
    private int lastPlayerChoice = 2; // Default to 2 players

    // Constructor GameController
    public GameController(int boardSize) {
        board = new Board<>(boardSize);
        players = new KoleksiPlayer<>(2); // Koleksi untuk dua pemain (bisa lebih sesuai pilihan)
        observers = new ArrayList<>();
        currentPlayerIndex = 0;
        gameOver = false;
        winner = null;
        dataPersistence = new FileGameDataPersistence("game_data.txt");
    }

    // Menambahkan pemain ke dalam permainan
    public void addPlayer(Player<G> player) {
        players.addPlayer(player);
    }

    // Menambahkan observer (untuk memperbarui tampilan UI)
    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    public void startNewGame() {
        board.reset();
        currentPlayerIndex = 0;
        gameOver = false;
        winner = null;
        notifyGameUpdated();
    }
    
    // Method to start new game with player choice
    public void startNewGameWithPlayerChoice() {
        // Notify observers to get player choice
        for (GameObserver observer : observers) {
            if (observer instanceof TicTacToeView) {
                ((TicTacToeView) observer).promptPlayerChoice();
            }
        }
    }
    
    // Setup players with choice
    public void setupPlayers(int playerChoice) {
        // Save the last choice
        this.lastPlayerChoice = playerChoice;
        
        // Reset player collection
        players = new KoleksiPlayer<>(2);
        
        Player<G> player1 = new HumanPlayer<>("Player X", "X");
        Player<G> player2;

        if (playerChoice == 0) { // 1 Player option (index 0)
            player2 = new ComputerPlayer<>("Player O", "O");  // Komputer sebagai pemain kedua
        } else {
            player2 = new HumanPlayer<>("Player O", "O");
        }

        addPlayer(player1);
        addPlayer(player2);
        
        // Reset the game state
        board.reset();
        currentPlayerIndex = 0;
        gameOver = false;
        winner = null;
        notifyGameUpdated();
    }

    // Menjalankan langkah pemain
    public void makeMove(int row, int col) {
        if (gameOver) {
            return;
        }

        try {
            Player<G> currentPlayer = getCurrentPlayer();  // Mendapatkan pemain saat ini
            
            // Only make move if it's valid
            if (!board.isValidMove(row, col)) {
                return; // Silently return without throwing exception
            }
            
            currentPlayer.makeMove(board, row, col);  // Pemain melakukan gerakan
            notifyMoveMade(row, col, currentPlayer);

            if (board.checkWin()) {
                gameOver = true;
                winner = currentPlayer;
                notifyGameOver(winner);
                saveGameResult();
            } else if (board.isFull()) {
                gameOver = true;
                notifyGameOver(null);  // Seri
                saveGameResult();
            } else {
                nextPlayer();  // Beralih ke pemain berikutnya
                notifyGameUpdated();

                // Jika giliran komputer, panggil metode untuk gerakan otomatis
                if (getCurrentPlayer() instanceof ComputerPlayer) {
                    computeNextMove();
                }
            }
        } catch (InvalidMoveException e) {
            System.err.println(e.getMessage());
        }
    }
    
    // Added method to compute next move for computer player
    private void computeNextMove() {
        if (getCurrentPlayer() instanceof ComputerPlayer) {
            ComputerPlayer<G> computerPlayer = (ComputerPlayer<G>) getCurrentPlayer();
            int[] move = computerPlayer.findBestMove(board);
            
            if (move != null) {
                try {
                    makeMove(move[0], move[1]);
                } catch (Exception e) {
                    System.err.println("Computer move error: " + e.getMessage());
                }
            }
        }
    }

    private void saveGameResult() {
        GameResult result = new GameResult();
        result.setDate(new java.util.Date());
        if (winner != null) {
            result.setResult(winner.getName() + " won");
        } else {
            result.setResult("Tie");
        }
        try {
            dataPersistence.saveGameResult(result);
        } catch (PersistenceException e) {
            System.err.println("Failed to save game result: " + e.getMessage());
        }
    }

    // Mendapatkan pemain yang sedang bermain
    public Player<G> getCurrentPlayer() {
        if (players.getSize() == 0) {
            return null;
        }
        return players.getPlayer(currentPlayerIndex);
    }

    // Beralih ke pemain berikutnya
    private void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.getSize();
    }

    // Memeriksa apakah permainan sudah selesai
    public boolean isGameOver() {
        return gameOver;
    }

    // Mendapatkan pemenang permainan
    public Player<G> getWinner() {
        return winner;
    }

    private void notifyGameUpdated() {
        GameState state = GameState.IN_PROGRESS;
        for (GameObserver observer : observers) {
            observer.onGameUpdated(state);
        }
    }

    private void notifyGameOver(Player<G> winner) {
        for (GameObserver observer : observers) {
            observer.onGameOver(winner);
        }
    }

    private void notifyMoveMade(int row, int col, Player<G> player) {
        for (GameObserver observer : observers) {
            observer.onMoveMade(row, col, player);
        }
    }

    // Mendapatkan papan permainan
    public Board<G> getBoard() {
        return board;
    }

    // Memuat riwayat permainan
    public List<GameResult> loadGameHistory() throws PersistenceException {
        return dataPersistence.loadGameResults();
    }
    
    // Get last player choice
    public int getLastPlayerChoice() {
        return lastPlayerChoice;
    }
}