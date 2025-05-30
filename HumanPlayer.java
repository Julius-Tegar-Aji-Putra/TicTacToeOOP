public class HumanPlayer<G> extends AbstractPlayer<G> {

    public HumanPlayer(String name, String symbol) {
        super(name, symbol);
    }

    @Override
    protected void validateMove(Board<G> board, int row, int col) throws InvalidMoveException {
        if (!board.isValidMove(row, col)) {
            throw new InvalidMoveException("Invalid move.");
        }
    }

    @Override
    public G getPlayerType() {
        @SuppressWarnings("unchecked")
        G type = (G) "Human";
        return type; 
    }
}