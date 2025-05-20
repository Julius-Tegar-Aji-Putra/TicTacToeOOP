class Cell<G> {
    private final int row;
    private final int col;
    private Player<G> player;
    
    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.player = null;
    }
    
    public boolean isEmpty() {
        return player == null;
    }
    
    public Player<G> getPlayer() {
        return player;
    }
    
    public void setPlayer(Player<G> player) {
        this.player = player;
    }
    
    public void clear() {
        player = null;
    }
    
    public int getRow() {
        return row;
    }
    
    public int getCol() {
        return col;
    }
    
    public String getSymbol() {
        return isEmpty() ? "" : player.getSymbol();
    }
}