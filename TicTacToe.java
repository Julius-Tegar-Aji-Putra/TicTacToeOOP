import javax.swing.*;
public class TicTacToe {
    public static void main(String[] args) {
        GameController<String> controller = new GameController<>(3);
        
        TicTacToeView view = new TicTacToeView(controller);
        controller.setView(view);
        
        view.setVisible(true);

        SwingUtilities.invokeLater(() -> {
            view.promptPlayerChoice();
        });
    }
}