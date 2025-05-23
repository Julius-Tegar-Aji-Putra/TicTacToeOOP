import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

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
    private int gameMode; // 0 = 1 Player, 1 = 2 Players
    private String player1Name = "Player X"; // Default atau nama yang diinput
    private String player2Name = "Player O"; // Default atau nama yang diinput


    public String getPlayer1Name() { return player1Name; }
    public String getPlayer2Name() { return player2Name; }
    public int getGameMode() { return gameMode; }

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
        this.gameMode = playerChoice;
        this.lastPlayerChoice = playerChoice;

        players.clear();

        // Muat ikon kustom
        ImageIcon incognitoIcon = null;
        try {
            // Mengasumsikan icon.jpg ada di root classpath
            // Sesuaikan path "/icon.jpg" jika Anda meletakkannya di sub-folder dalam resources.
            java.net.URL imageUrl = getClass().getResource("/icon.jpg");
            if (imageUrl != null) {
                incognitoIcon = new ImageIcon(imageUrl);
            } else {
                // Fallback jika resource tidak ditemukan (misalnya, coba path langsung jika sesuai)
                // Ini kurang portabel jika aplikasi di-bundle dalam JAR.
                // incognitoIcon = new ImageIcon("icon.jpg");
                System.err.println("Peringatan: Ikon kustom 'icon.jpg' tidak ditemukan di classpath.");
            }
        } catch (Exception e) {
            System.err.println("Error saat memuat ikon kustom: " + e.getMessage());
            e.printStackTrace(); // Cetak stack trace untuk debugging
        }

        if (playerChoice == 0) { // 1 Player
            this.player1Name = "Player"; 
            this.player2Name = "Computer";
            addPlayer(new HumanPlayer<>(this.player1Name, "X"));
            addPlayer(new ComputerPlayer<>(this.player2Name, "O"));
        } else { // 2 Players
            // Meminta nama untuk Player X dengan ikon kustom
            Object p1InputNameResult = JOptionPane.showInputDialog(
                view, // parentComponent (gunakan 'view' jika tersedia, atau 'null')
                "Enter name for Player X:",
                "Input Name - Player X",      // Judul dialog
                JOptionPane.PLAIN_MESSAGE,    // messageType (PLAIN_MESSAGE agar ikon default tidak tampil)
                incognitoIcon,                // Ikon kustom Anda
                null,                         // Pilihan (tidak digunakan untuk input teks bebas)
                this.player1Name              // Nilai awal di field input
            );
            // Periksa apakah pengguna menekan Cancel atau menutup dialog
            if (p1InputNameResult == null) { // Pengguna menekan cancel atau menutup dialog
                // Pertahankan nama sebelumnya atau default, atau handle sesuai kebutuhan
                // Untuk saat ini, kita pertahankan nama sebelumnya jika cancel.
            } else {
                String p1InputName = (String) p1InputNameResult;
                 this.player1Name = (p1InputName.trim().isEmpty()) ? "Player X" : p1InputName.trim();
            }


            // Meminta nama untuk Player O dengan ikon kustom
            Object p2InputNameResult = JOptionPane.showInputDialog(
                view, // parentComponent
                "Enter name for Player O:",
                "Input Name - Player O",      // Judul dialog
                JOptionPane.PLAIN_MESSAGE,    // messageType
                incognitoIcon,                // Ikon kustom Anda
                null,                         // Pilihan
                this.player2Name              // Nilai awal
            );
            if (p2InputNameResult == null) {
                 // Pertahankan nama sebelumnya atau default
            } else {
                String p2InputName = (String) p2InputNameResult;
                this.player2Name = (p2InputName.trim().isEmpty()) ? "Player O" : p2InputName.trim();
            }

            addPlayer(new HumanPlayer<>(this.player1Name, "X"));
            addPlayer(new HumanPlayer<>(this.player2Name, "O"));
        }

        board.reset();
        currentPlayerIndex = 0;
        gameOver = false;
        winner = null;
        if (view != null) {
            view.onGameUpdated(getCurrentGameState());
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
        GameResult gameResult = new GameResult(); // Objek GameResult tidak lagi perlu Date atau gameNumber dari sini
        String resultString;

        Player<G> gameWinner = getWinner();

        if (gameWinner != null) { // Ada pemenang
            if (this.gameMode == 0) { // 1 Pemain
                if (gameWinner.getName().equals(this.player1Name)) { // Manusia (Player) menang
                    resultString = this.player1Name + " won";
                } else { // Komputer menang
                    resultString = this.player2Name + " won";
                }
            } else { // 2 Pemain
                resultString = gameWinner.getName() + " won";
            }
        } else { // Seri
            if (this.gameMode == 0) { // 1 Pemain
                resultString = this.player1Name + " and " + this.player2Name + " Tie";
            } else { // 2 Pemain
                resultString = this.player1Name + " and " + this.player2Name + " Tie";
            }
        }
        
        gameResult.setResult(resultString);
        
        try {
            dataPersistence.saveGameResult(gameResult);
        } catch (PersistenceException e) {
            System.err.println("Failed to save game result: " + e.getMessage());
            if (view != null) { // Beri tahu pengguna melalui UI jika gagal simpan
                JOptionPane.showMessageDialog(view, "Error saving game history: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            }
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

