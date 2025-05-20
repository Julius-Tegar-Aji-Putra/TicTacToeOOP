import java.util.Random;

public class ComputerPlayer<G> implements Player<G> {
    private String name;
    private String symbol;

    public ComputerPlayer(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void makeMove(Board<G> board, int row, int col) throws InvalidMoveException {
        if (!board.isValidMove(row, col)) {
            throw new InvalidMoveException("Invalid move.");
        }
        board.placeMark(row, col, this);  // Set this player to the cell
    }

    // Logika untuk komputer memilih gerakan secara otomatis
    public void makeAutomaticMove(Board<G> board) {
        Random random = new Random();
        int row, col;
        boolean validMove = false;

        while (!validMove) {
            row = random.nextInt(board.getSize());
            col = random.nextInt(board.getSize());

            try {
                makeMove(board, row, col);  // Coba gerakan
                validMove = true;  // Jika berhasil, keluar dari loop
            } catch (InvalidMoveException e) {
                // Jika gerakan tidak valid, coba lagi
                continue;
            }
        }
    }

    @Override
    public G getPlayerType() {
        return (G) "Computer"; // Untuk menandakan tipe pemain
    }
}