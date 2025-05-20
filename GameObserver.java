interface GameObserver {
    void onGameUpdated(GameState state);
    void onGameOver(Player winner);
    void onMoveMade(int row, int col, Player player);
}