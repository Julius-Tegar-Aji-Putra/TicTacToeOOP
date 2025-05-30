import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

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
    private int lastPlayerChoice = 1; // Default 1 Player
    private int gameMode; // 0 = 1 Player, 1 = 2 Players
    private String player1Name = "Player X"; 
    private String player2Name = "Player O"; 

    public GameController (int boardSize) {
        board = new Board<>(boardSize);
        players = new ArrayList<>(); 
        currentPlayerIndex = 0;
        gameOver = false;
        winner = null;
        dataPersistence = new FileGameDataPersistence("game_data.txt");
        this.view = new TicTacToeView(this);
        this.view.setVisible(true); 
        SwingUtilities.invokeLater(() -> {
            this.view.promptPlayerChoice(); 
        });
    }

    public String getPlayer1Name() { return player1Name; }
    public String getPlayer2Name() { return player2Name; }
    public int getGameMode() { return gameMode; }

    public void addPlayer(Player<G> player) {
        if (players.size() < 2) { 
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
            view.onGameUpdated(getCurrentGameState()); 
        }
    }
    
    public void startNewGameWithPlayerChoice() {
        if (view != null) {
            view.promptPlayerChoice(); 
        }
    }
    
    public void setupPlayers(int playerChoice) {
        this.gameMode = playerChoice;
        this.lastPlayerChoice = playerChoice;

        players.clear();

        ImageIcon incognitoIcon = null;
        try {
            java.net.URL imageUrl = getClass().getResource("/icon.jpg");
            if (imageUrl != null) {
                incognitoIcon = new ImageIcon(imageUrl);
            } else {
                System.err.println("Peringatan: Ikon kustom 'icon.jpg' tidak ditemukan di classpath.");
            }
        } catch (Exception e) {
            System.err.println("Error saat memuat ikon kustom: " + e.getMessage());
            e.printStackTrace(); 
        }

        if (playerChoice == 0) { // 1 Player
            this.player1Name = "Player"; 
            this.player2Name = "Computer";
            addPlayer(new HumanPlayer<>(this.player1Name, "X"));
            addPlayer(new ComputerPlayer<>(this.player2Name, "O"));
        } else { // 2 Players
            Object p1InputNameResult = JOptionPane.showInputDialog(
                view,
                "Enter name for Player X:",
                "Input Name - Player X",     
                JOptionPane.PLAIN_MESSAGE,    
                incognitoIcon,                
                null,                        
                this.player1Name             
            );
   
            if (p1InputNameResult == null) { 

            } else {
                String p1InputName = (String) p1InputNameResult;
                 this.player1Name = (p1InputName.trim().isEmpty()) ? "Player X" : p1InputName.trim();
            }

            Object p2InputNameResult = JOptionPane.showInputDialog(
                view, 
                "Enter name for Player O:",
                "Input Name - Player O",      
                JOptionPane.PLAIN_MESSAGE,   
                incognitoIcon,             
                null,                         
                this.player2Name             
            );
            if (p2InputNameResult == null) {
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

    public void makeMove(int row, int col) {
        if (gameOver) {
            return;
        }

        try {
            Player<G> currentPlayer = getCurrentPlayer();  
            currentPlayer.makeMove(board, row, col);  
            if (view != null) {
                view.onMoveMade(row, col, currentPlayer); 
            }

            if (board.checkWin()) {
                gameOver = true;
                winner = currentPlayer;
                if (view != null) {
                    view.onGameOver(winner); 
                }
                saveGameResult();
            } else if (board.isFull()) {
                gameOver = true;
                winner = null;
                if (view != null) {
                    view.onGameOver(null); 
                }                
                saveGameResult();
            } else {
                nextPlayer();  
                if (view != null) {
                    view.onGameUpdated(getCurrentGameState()); 
                }

                if (getCurrentPlayer() instanceof ComputerPlayer) {
                    computeNextMove();
                }
            }
        } catch (InvalidMoveException e) {
            System.err.println(e.getMessage());
        }
    }
    
    private void computeNextMove() {
        if (getCurrentPlayer() instanceof ComputerPlayer) {
            ComputerPlayer<G> computerPlayer = (ComputerPlayer<G>) getCurrentPlayer();
            int[] move = computerPlayer.findBestMove(board,this);
            
            if (move != null) {
                makeMove(move[0], move[1]);
            }
        }
    }

    private void saveGameResult() {
        GameResult gameResult = new GameResult(); 
        String resultString;

        Player<G> gameWinner = getWinner();

        if (gameWinner != null) { 
            if (this.gameMode == 0) { // 1 Pemain
                if (gameWinner.getName().equals(this.player1Name)) { 
                    resultString = this.player1Name + " won";
                } else {
                    resultString = this.player2Name + " won";
                }
            } else { // 2 Pemain
                resultString = gameWinner.getName() + " won";
            }
        } else { 
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
            if (view != null) { 
                JOptionPane.showMessageDialog(view, "Error saving game history: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public List<Player<G>> getPlayers() {
        return this.players;
    }

    public Player<G> getCurrentPlayer() {
        if (players.isEmpty()) {
            return null;
        }
        return players.get(currentPlayerIndex);
    }

    private void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Player<G> getWinner() {
        return winner;
    }

    public Board<G> getBoard() {
        return board;
    }

    public KoleksiGameResult loadGameHistory() throws PersistenceException {
        return dataPersistence.loadGameResults();
    }
    
    public int getLastPlayerChoice() {
        return lastPlayerChoice;
    }

    public GameState getCurrentGameState() { 
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