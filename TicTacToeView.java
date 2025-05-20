class TicTacToeView extends javax.swing.JFrame implements GameObserver {
    private final int boardWidth = 600;
    private final int boardHeight = 650;
    private final GameController controller;
    
    private javax.swing.JLabel statusLabel;
    private javax.swing.JPanel boardPanel;
    private javax.swing.JButton[][] buttons;
    private javax.swing.JButton newGameButton;
    private javax.swing.JButton historyButton;
    
    public TicTacToeView(GameController controller) {
        this.controller = controller;
        controller.addObserver(this);
        
        initializeUI();
    }
    
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
        newGameButton.addActionListener(e -> controller.startNewGame());
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
            Player winner = controller.getWinner();
            if (winner != null) {
                statusLabel.setText(winner.getName() + " (" + winner.getSymbol() + ") wins!");
            } else {
                statusLabel.setText("Game ended in a tie!");
            }
        } else {
            Player currentPlayer = controller.getCurrentPlayer();
            statusLabel.setText(currentPlayer.getName() + " (" + currentPlayer.getSymbol() + ")'s turn");
        }
    }
    
    @Override
    public void onGameUpdated(GameState state) {
        updateStatus();
        updateBoard();
    }
    
    @Override
    public void onGameOver(Player winner) {
        updateStatus();
        
        String message;
        if (winner != null) {
            message = winner.getName() + " (" + winner.getSymbol() + ") wins!";
            highlightWinningCells();
        } else {
            message = "Game ended in a tie!";
        }
        
        // Show message after a short delay
        javax.swing.Timer timer = new javax.swing.Timer(500, event -> {
            javax.swing.JOptionPane.showMessageDialog(this, message);
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private void highlightWinningCells() {
        // For simplicity, highlight all cells for now
        // In a real implementation, you would only highlight the winning line
        Board board = controller.getBoard();
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                if (!board.getCell(i, j).isEmpty()) {
                    buttons[i][j].setBackground(java.awt.Color.lightGray);
                }
            }
        }
    }
    
    @Override
    public void onMoveMade(int row, int col, Player player) {
        buttons[row][col].setText(player.getSymbol());
    }
    
    private void updateBoard() {
        Board board = controller.getBoard();
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                buttons[i][j].setText(board.getCell(i, j).getSymbol());
                buttons[i][j].setBackground(null);
            }
        }
    }
}