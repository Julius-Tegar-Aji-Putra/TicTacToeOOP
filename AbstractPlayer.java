abstract class AbstractPlayer<G> implements Player<G> {
    protected String symbol;
    protected String name;
    
    public AbstractPlayer(String name, String symbol) {
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
        validateMove(board, row, col);
        board.placeMark(row, col, this);
    }
    
    protected abstract void validateMove(Board<G> board, int row, int col) throws InvalidMoveException;
}