import javax.swing.*;
public class TicTacToe {
    public static void main(String[] args) {
        // Pilih jumlah pemain menggunakan JOptionPane
        String[] options = {"1 Player", "2 Players"};
        int choice = JOptionPane.showOptionDialog(null, "Select the number of players:", 
                                                  "Choose Players", JOptionPane.DEFAULT_OPTION, 
                                                  JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        // Setup GameController dan Pemain
        GameController<String> controller = new GameController<>(3);
        Player<String> player1 = new HumanPlayer<>("Player X", "X");

        Player<String> player2;
        if (choice == 1) {
            player2 = new ComputerPlayer<>("Player O", "O"); // Jika 1 pemain, komputer akan bermain
        } else {
            player2 = new HumanPlayer<>("Player O", "O");
        }

        controller.addPlayer(player1);
        controller.addPlayer(player2);

        // Setup dan tampilkan UI
        TicTacToeView view = new TicTacToeView(controller);
        view.setVisible(true);

        // Mulai permainan
        controller.startNewGame();
    }
}
