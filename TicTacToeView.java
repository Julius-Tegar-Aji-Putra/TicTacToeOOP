class TicTacToeView extends javax.swing.JFrame {
    private final int boardWidth = 600;
    private final int boardHeight = 650;
    private final GameController<String> controller;
    
    private javax.swing.JLabel statusLabel;
    private javax.swing.JPanel boardPanel;
    private javax.swing.JButton[][] buttons;
    private javax.swing.JButton newGameButton;
    private javax.swing.JButton historyButton;
    
    public TicTacToeView(GameController<String> controller) {
        this.controller = controller;
        
        initializeUI();
    }
    
    @SuppressWarnings("unused")
    private void initializeUI() {
        setTitle("Tic-Tac-Toe Game");
        setSize(boardWidth, boardHeight);
        setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new java.awt.BorderLayout());
        
        // Top panel with status label
        javax.swing.JPanel topPanel = new javax.swing.JPanel(new java.awt.BorderLayout());
        topPanel.setBackground(java.awt.Color.darkGray);
        
        statusLabel = new javax.swing.JLabel("Tic-Tac-Toe");
        statusLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        statusLabel.setForeground(java.awt.Color.WHITE);
        statusLabel.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        statusLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(statusLabel, java.awt.BorderLayout.CENTER);
        
        // Button panel
        javax.swing.JPanel buttonPanel = new javax.swing.JPanel(new java.awt.FlowLayout());
        buttonPanel.setBackground(java.awt.Color.darkGray);
        
        newGameButton = new javax.swing.JButton("New Game");
        newGameButton.addActionListener(e -> controller.startNewGameWithPlayerChoice()); // Changed to prompt player choice
        buttonPanel.add(newGameButton);
        
        historyButton = new javax.swing.JButton("Game History");
        historyButton.addActionListener(e -> showGameHistory());
        buttonPanel.add(historyButton);
        
        topPanel.add(buttonPanel, java.awt.BorderLayout.SOUTH);
        add(topPanel, java.awt.BorderLayout.NORTH);
        
        // Board panel
        boardPanel = new javax.swing.JPanel();
        int size = controller.getBoard().getSize();
        boardPanel.setLayout(new java.awt.GridLayout(size, size));
        buttons = new javax.swing.JButton[size][size];
        
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                final int row = i;
                final int col = j;
                buttons[i][j] = new javax.swing.JButton("");
                buttons[i][j].setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 60));
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].addActionListener(e -> controller.makeMove(row, col));
                boardPanel.add(buttons[i][j]);
            }
        }
        
        add(boardPanel, java.awt.BorderLayout.CENTER);
        
        updateStatus();
    }
    
    // Method to prompt player choice
    public void promptPlayerChoice() {
        String[] options = {"1 Player", "2 Players"};
        int choice = javax.swing.JOptionPane.showOptionDialog(this, 
                                                          "Select the number of players:", 
                                                          "Choose Players", 
                                                          javax.swing.JOptionPane.DEFAULT_OPTION, 
                                                          javax.swing.JOptionPane.INFORMATION_MESSAGE, 
                                                          null, 
                                                          options, 
                                                          options[controller.getLastPlayerChoice() == 0 ? 0 : 1]);
        
        if (choice != -1) { // User didn't cancel
            controller.setupPlayers(choice);
        } else {
            // User canceled, use the previous setting
            controller.setupPlayers(controller.getLastPlayerChoice());
        }
    }
    
    private void showGameHistory() {
        try {
            java.util.List<GameResult> history = controller.loadGameHistory();
            
            if (history.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "No game history available.", 
                    "Game History", 
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            StringBuilder historyText = new StringBuilder("Game History:\n\n");
            
            for (int i = 0; i < history.size(); i++) {
                GameResult result = history.get(i);
                historyText.append(i + 1).append(". ")
                          .append(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(result.getDate()))
                          .append(" - ")
                          .append(result.getResult())
                          .append("\n");
            }
            
            javax.swing.JTextArea textArea = new javax.swing.JTextArea(historyText.toString());
            textArea.setEditable(false);
            textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 14));
            
            javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(textArea);
            scrollPane.setPreferredSize(new java.awt.Dimension(400, 300));
            
            javax.swing.JOptionPane.showMessageDialog(this, scrollPane, 
                "Game History", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            
        } catch (PersistenceException e) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Error loading game history: " + e.getMessage(), 
                "Error", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateStatus() {
        if (controller.isGameOver()) {
            Player<String> winner = controller.getWinner();
            if (winner != null) {
                statusLabel.setText(winner.getName() + " (" + winner.getSymbol() + ") wins!");
            } else {
                statusLabel.setText("Game ended in a tie!");
            }
        } else {
            Player<String> currentPlayer = controller.getCurrentPlayer();
            if (currentPlayer != null) {
                statusLabel.setText(currentPlayer.getName() + " (" + currentPlayer.getSymbol() + ")'s turn");
            } else {
                statusLabel.setText("Welcome to Tic-Tac-Toe");
            }
        }
    }
    

    public void onGameUpdated(GameController.GameState state) {
        updateStatus();
        updateBoard();
    }
    
    // METODE onGameOver YANG DIMODIFIKASI
    @SuppressWarnings("unused")
    public <G> void onGameOver(Player<G> winner) {
        updateStatus();
        
        String message;
        if (winner != null) {
            message = winner.getName() + " (" + winner.getSymbol() + ") wins!";
            highlightWinningLine(); // Panggil metode baru untuk highlight garis kemenangan
        } else {
            message = "Game ended in a tie!";
            highlightAllCellsForTie(); // Panggil metode baru untuk highlight semua sel saat seri
        }
        
        javax.swing.Timer timer = new javax.swing.Timer(500, event -> {
            javax.swing.JOptionPane.showMessageDialog(this, message);
        });
        timer.setRepeats(false);
        timer.start();
    }

    // METODE BARU: Untuk highlight garis kemenangan
    private void highlightWinningLine() {
        Board<String> board = controller.getBoard();
        java.util.List<int[]> winningCoords = board.getWinningLineCoordinates();

        if (winningCoords != null && !winningCoords.isEmpty()) {
            for (int[] coord : winningCoords) {
                buttons[coord[0]][coord[1]].setBackground(java.awt.Color.GREEN); // Warna highlight untuk kemenangan
            }
        }
    }

    // METODE BARU: Untuk highlight semua sel saat seri
    private void highlightAllCellsForTie() {
        int size = controller.getBoard().getSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                 // Anda bisa memilih untuk mewarnai semua sel atau hanya yang terisi
                 // Untuk "semua kotak berubah warna", kita warnai semua tombol
                buttons[i][j].setBackground(java.awt.Color.LIGHT_GRAY); // Warna highlight untuk seri
            }
        }
    }
    
    public <G> void onMoveMade(int row, int col, Player<G> player) {
        buttons[row][col].setText(player.getSymbol());
    }
    
    private void updateBoard() {
        Board<String> board = controller.getBoard();
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                buttons[i][j].setText(board.getCell(i, j).getSymbol());
                buttons[i][j].setBackground(null);
            }
        }
    }
}