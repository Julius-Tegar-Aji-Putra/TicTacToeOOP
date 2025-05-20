public class HumanPlayer<G> implements Player<G> {
    private String name;
    private String symbol;

    public HumanPlayer(String name, String symbol) {
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
        board.getCell(row, col).setPlayer(this);  // Set this player to the cell
    }

    @Override
    public G getPlayerType() {
        return (G) "Human"; // Untuk menandakan tipe pemain
    }
}