class TicTacToeView extends javax.swing.JFrame {
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

        javax.swing.JPanel topPanel = new javax.swing.JPanel(new java.awt.BorderLayout());
        topPanel.setBackground(new java.awt.Color(70, 90, 120));

        statusLabel = new javax.swing.JLabel("Tic-Tac-Toe");
        statusLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        statusLabel.setForeground(java.awt.Color.WHITE);
        statusLabel.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        statusLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(statusLabel, java.awt.BorderLayout.CENTER);

        javax.swing.JPanel buttonPanel = new javax.swing.JPanel(new java.awt.FlowLayout());
        buttonPanel.setBackground(new java.awt.Color(70, 90, 120));

        newGameButton = new javax.swing.JButton("New Game");
        newGameButton.setBackground(java.awt.Color.WHITE);
        newGameButton.setForeground(new java.awt.Color(70, 90, 120));
        newGameButton.setFocusPainted(false);
        newGameButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(100, 130, 160), 2),
            javax.swing.BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        addHoverEffect(newGameButton);
        newGameButton.addActionListener(e -> controller.startNewGameWithPlayerChoice());
        buttonPanel.add(newGameButton);

        historyButton = new javax.swing.JButton("Game History");
        historyButton.setBackground(java.awt.Color.WHITE);
        historyButton.setForeground(new java.awt.Color(70, 90, 120));
        historyButton.setFocusPainted(false);
        historyButton.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(100, 130, 160), 2),
            javax.swing.BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        addHoverEffect(historyButton);
        historyButton.addActionListener(e -> showGameHistory());
        buttonPanel.add(historyButton);

        topPanel.add(buttonPanel, java.awt.BorderLayout.SOUTH);
        add(topPanel, java.awt.BorderLayout.NORTH);

        createStyledBoard();
        updateStatus();
    }

    private void createStyledBoard() {
        javax.swing.JPanel boardContainer = new javax.swing.JPanel();
        boardContainer.setLayout(new java.awt.BorderLayout());
        boardContainer.setBackground(new java.awt.Color(70, 90, 120));
        boardContainer.setBorder(javax.swing.BorderFactory.createEmptyBorder(30, 30, 30, 30));

        boardPanel = new javax.swing.JPanel();
        boardPanel.setLayout(new java.awt.GridLayout(3, 3, 0, 0));
        boardPanel.setBackground(java.awt.Color.WHITE);
        boardPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));

        int size = controller.getBoard().getSize();
        buttons = new javax.swing.JButton[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                final int row = i;
                final int col = j;

                buttons[i][j] = new javax.swing.JButton("");
                buttons[i][j].setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 60));
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].setBackground(java.awt.Color.WHITE);
                buttons[i][j].setOpaque(true);
                buttons[i][j].setContentAreaFilled(true);
                javax.swing.border.Border border = createGridBorder(i, j, size);
                buttons[i][j].setBorder(border);
                addBoardCellHoverEffect(buttons[i][j]);
                buttons[i][j].addActionListener(e -> controller.makeMove(row, col));
                boardPanel.add(buttons[i][j]);
            }
        }

        boardContainer.add(boardPanel, java.awt.BorderLayout.CENTER);
        add(boardContainer, java.awt.BorderLayout.CENTER);
    }

    private javax.swing.border.Border createGridBorder(int row, int col, int size) {
        int lineThickness = 3;
        java.awt.Color lineColor = new java.awt.Color(100, 130, 160);
        int top = 0, left = 0, bottom = 0, right = 0;

        if (row == 0) {
            bottom = lineThickness;
        } else if (row == size - 1) {
            top = lineThickness;
        } else {
            top = lineThickness;
            bottom = lineThickness;
        }

        if (col == 0) {
            right = lineThickness;
        } else if (col == size - 1) {
            left = lineThickness;
        } else {
            left = lineThickness;
            right = lineThickness;
        }

        return javax.swing.BorderFactory.createMatteBorder(top, left, bottom, right, lineColor);
    }

    private void addHoverEffect(javax.swing.JButton button) {
        java.awt.Color originalBg = button.getBackground();
        java.awt.Color hoverBg = new java.awt.Color(240, 245, 250);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            javax.swing.Timer hoverTimer;
            boolean isHovering = false;

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                isHovering = true;
                animateColor(button, originalBg, hoverBg, 150);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                isHovering = false;
                animateColor(button, button.getBackground(), originalBg, 150);
            }
        });
    }

    private void addBoardCellHoverEffect(javax.swing.JButton button) {
        java.awt.Color originalBg = java.awt.Color.WHITE;
        java.awt.Color hoverBg = new java.awt.Color(230, 240, 250);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.getText().isEmpty()) {
                    animateColor(button, originalBg, hoverBg, 100);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (button.getText().isEmpty()) {
                    animateColor(button, button.getBackground(), originalBg, 100);
                }
            }
        });
    }

    private void animateColor(javax.swing.JButton button, java.awt.Color fromColor, java.awt.Color toColor, int duration) {
        javax.swing.Timer timer = new javax.swing.Timer(10, null);
        long startTime = System.currentTimeMillis();

        timer.addActionListener(e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float progress = Math.min(1.0f, (float) elapsed / duration);
            int r = (int) (fromColor.getRed() + progress * (toColor.getRed() - fromColor.getRed()));
            int g = (int) (fromColor.getGreen() + progress * (toColor.getGreen() - fromColor.getGreen()));
            int b = (int) (fromColor.getBlue() + progress * (toColor.getBlue() - fromColor.getBlue()));
            button.setBackground(new java.awt.Color(r, g, b));
            if (progress >= 1.0f) {
                timer.stop();
            }
        });

        timer.start();
    }

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

        if (choice != -1) {
            controller.setupPlayers(choice);
        } else {
            controller.setupPlayers(controller.getLastPlayerChoice());
        }
    }

    private void showGameHistory() {
        try {
            KoleksiGameResult history = controller.loadGameHistory();

            if (history.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "No game history available.",
                        "Game History",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder historyText = new StringBuilder("Game History:\n");

            for (int i = 0; i < history.getSize(); i++) {
                GameResult result = history.getResult(i);
                if (result != null) {
                    historyText.append(result.getGameNumber()).append(". ")
                            .append(result.getResult())
                            .append("\n");
                }
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

    @SuppressWarnings("unused")
    public <G> void onGameOver(Player<G> winnerParam) {
        updateStatus();

        String message;
        int gameMode = controller.getGameMode();
        String player1Name = controller.getPlayer1Name();

        if (winnerParam != null) {
            if (gameMode == 0) {
                if (winnerParam.getName().equals(player1Name)) {
                    message = "You win!";
                } else {
                    message = winnerParam.getName() + " wins!";
                }
            } else {
                message = winnerParam.getName() + " wins!";
            }
            highlightWinningLine();
        } else {
            message = "Game ended in a tie!";
            highlightAllCellsForTie();
        }

        javax.swing.Timer timer = new javax.swing.Timer(500, event -> {
            javax.swing.JOptionPane.showMessageDialog(this, message);
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void highlightWinningLine() {
        Board<String> board = controller.getBoard();
        java.util.List<int[]> winningCoords = board.getWinningLineCoordinates();

        if (winningCoords != null && !winningCoords.isEmpty()) {
            for (int[] coord : winningCoords) {
                buttons[coord[0]][coord[1]].setBackground(new java.awt.Color(144, 238, 144));
            }
        }
    }

    private void highlightAllCellsForTie() {
        int size = controller.getBoard().getSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                buttons[i][j].setBackground(new java.awt.Color(200, 200, 200));
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
                buttons[i][j].setBackground(java.awt.Color.WHITE);
            }
        }
    }
}
