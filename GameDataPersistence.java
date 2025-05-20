interface GameDataPersistence {
    void saveGameResult(GameResult result) throws PersistenceException;
    java.util.List<GameResult> loadGameResults() throws PersistenceException;
}