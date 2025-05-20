interface GameObserver {
    void onGameUpdated(GameState state);
    <G> void onGameOver(Player<G> winner);
    <G> void onMoveMade(int row, int col, Player<G> player);
}