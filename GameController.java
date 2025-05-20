import java.util.ArrayList;
import java.util.List;

class GameController<G> {
    private final Board<G> board;
    private final KoleksiPlayer<G> players;  // Menggunakan KoleksiPlayer dengan Generic
    private final List<GameObserver> observers;
    private int currentPlayerIndex;
    private boolean gameOver;
    private Player<G> winner;
    private final GameDataPersistence dataPersistence;

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

    // Menjalankan langkah pemain
    public void makeMove(int row, int col) {
        if (gameOver) {
            return;
        }

        try {
            Player<G> currentPlayer = getCurrentPlayer();  // Mendapatkan pemain saat ini
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
                    ((ComputerPlayer<G>) getCurrentPlayer()).makeAutomaticMove(board);
                    makeMove(row, col);  // Ulangi untuk memeriksa kondisi kemenangan setelah komputer bergerak
                }
            }
        } catch (InvalidMoveException e) {
            System.err.println(e.getMessage());
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

    // Fungsi untuk memilih antara satu atau dua pemain
    public void setupPlayers(int playerChoice) {
        Player<G> player1 = new HumanPlayer<>("Player X", "X");
        Player<G> player2;

        if (playerChoice == 1) {
            player2 = new ComputerPlayer<>("Player O", "O");  // Komputer sebagai pemain kedua
        } else {
            player2 = new HumanPlayer<>("Player O", "O");
        }

        addPlayer(player1);
        addPlayer(player2);
    }
}
