public interface Player<G> {
    String getSymbol();
    String getName();
    void makeMove(Board<G> board, int row, int col) throws InvalidMoveException;
    G getPlayerType(); 
}