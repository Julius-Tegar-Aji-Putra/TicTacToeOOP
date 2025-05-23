interface GameDataPersistence {
    void saveGameResult(GameResult result) throws PersistenceException;
    KoleksiGameResult loadGameResults() throws PersistenceException;
}