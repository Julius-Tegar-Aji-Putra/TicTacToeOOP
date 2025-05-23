import java.util.ArrayList;
import java.util.List;

class GameController<G> {
    public enum GameState {
        IN_PROGRESS, PLAYER_X_WIN, PLAYER_O_WIN, TIE
    }

    private final Board<G> board;
    private List<Player<G>> players;
    private TicTacToeView view;
    private int currentPlayerIndex;
    private boolean gameOver;
    private Player<G> winner;
    private final GameDataPersistence dataPersistence;
    private int lastPlayerChoice = 2; // Default to 2 players

    // Constructor GameController
    public GameController(int boardSize) {
        board = new Board<>(boardSize);
        players = new ArrayList<>(); // Koleksi untuk dua pemain (bisa lebih sesuai pilihan)
        currentPlayerIndex = 0;
        gameOver = false;
        winner = null;
        dataPersistence = new FileGameDataPersistence("game_data.txt");
    }

    // Metode untuk mengatur View
    public void setView(TicTacToeView view) {
        this.view = view;
    }

    // Menambahkan pemain ke dalam permainan
    public void addPlayer(Player<G> player) {
        // players.addPlayer(player); // Dihapus
        if (players.size() < 2) { // Maksimal 2 pemain
            players.add(player);
        } else {
            System.out.println("Koleksi pemain penuh");
        }
    }

    public void startNewGame() {
        board.reset();
        currentPlayerIndex = 0;
        gameOver = false;
        winner = null;
        if (view != null) {
            view.onGameUpdated(getCurrentGameState()); //
        }
    }
    
    // Method to start new game with player choice
    public void startNewGameWithPlayerChoice() {
        if (view != null) {
            view.promptPlayerChoice(); //
        }
    }
    
    // Setup players with choice
    public void setupPlayers(int playerChoice) {
        // Save the last choice
        this.lastPlayerChoice = playerChoice;
        
        // Reset player collection
        players.clear();
        
        Player<G> player1 = new HumanPlayer<>("Player X", "X");
        Player<G> player2;

        if (playerChoice == 0) { // 1 Player option (index 0)
            player2 = new ComputerPlayer<>("Player O", "O");  // Komputer sebagai pemain kedua
        } else {
            player2 = new HumanPlayer<>("Player O", "O");
        }

        addPlayer(player1);
        addPlayer(player2);
        
        board.reset(); //
        currentPlayerIndex = 0; //
        gameOver = false; //
        winner = null; //
        // notifyGameUpdated(); // Diganti dengan pemanggilan langsung ke view
        if (view != null) {
            view.onGameUpdated(getCurrentGameState()); //
        }
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
            if (view != null) {
                view.onMoveMade(row, col, currentPlayer); //
    }

            if (board.checkWin()) {
                gameOver = true;
                winner = currentPlayer;
                if (view != null) {
                    view.onGameOver(winner); //
                }
                saveGameResult();
            } else if (board.isFull()) {
                gameOver = true;
                if (view != null) {
                    view.onGameOver(null); //
        }                
                saveGameResult();
            } else {
                nextPlayer();  // Beralih ke pemain berikutnya
                if (view != null) {
                    view.onGameUpdated(getCurrentGameState()); //
                }

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
                makeMove(move[0], move[1]);
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
        if (players.isEmpty()) {
            return null;
        }
        return players.get(currentPlayerIndex);
    }

    // Beralih ke pemain berikutnya
    private void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    // Memeriksa apakah permainan sudah selesai
    public boolean isGameOver() {
        return gameOver;
    }

    // Mendapatkan pemenang permainan
    public Player<G> getWinner() {
        return winner;
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

    // Mendapatkan status permainan
    @SuppressWarnings("unused")
    private GameState getGameState() {
        if (gameOver) { //
            if (winner != null) { //
                if (winner.getSymbol().equals("X")) { //
                    return GameState.PLAYER_X_WIN; //
                } else if (winner.getSymbol().equals("O")) { //
                    return GameState.PLAYER_O_WIN; //
                }
            }
            return GameState.TIE; //
        }
        return GameState.IN_PROGRESS;//
    }

    public GameState getCurrentGameState() { // Tipe kembalian sekarang adalah GameController.GameState
        if (gameOver) {
            if (winner != null) {
                if (winner.getSymbol().equals("X")) {
                    return GameState.PLAYER_X_WIN;
                } else if (winner.getSymbol().equals("O")) {
                    return GameState.PLAYER_O_WIN;
                }
            }
            return GameState.TIE;
        }
        return GameState.IN_PROGRESS;
    }
}

