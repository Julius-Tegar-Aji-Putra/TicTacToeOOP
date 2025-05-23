import javax.swing.*;
public class TicTacToe {
    public static void main(String[] args) {
        // Create GameController
        GameController<String> controller = new GameController<>(3);
        
        // Setup and display UI first
        TicTacToeView view = new TicTacToeView(controller);
        controller.setView(view);
        
        view.setVisible(true);

        // Use SwingUtilities.invokeLater to ensure UI is fully loaded before showing dialog
        SwingUtilities.invokeLater(() -> {
            // Prompt for player choice
            view.promptPlayerChoice();
        });
    }
}