class FileGameDataPersistence implements GameDataPersistence {
    private final String fileName;
    
    public FileGameDataPersistence(String fileName) {
        this.fileName = fileName;
    }
    
    @Override
    public void saveGameResult(GameResult result) throws PersistenceException {
        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(
                new java.io.FileWriter(fileName, true))) {
            writer.write(result.getDate().getTime() + "," + result.getResult());
            writer.newLine();
        } catch (java.io.IOException e) {
            throw new PersistenceException("Failed to save game result", e);
        }
    }
    
    @Override
    public java.util.List<GameResult> loadGameResults() throws PersistenceException {
        java.util.List<GameResult> results = new java.util.ArrayList<>();
        
        java.io.File file = new java.io.File(fileName);
        if (!file.exists()) {
            return results;
        }
        
        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    try {
                        GameResult result = new GameResult();
                        result.setDate(new java.util.Date(Long.parseLong(parts[0])));
                        result.setResult(parts[1]);
                        results.add(result);
                    } catch (NumberFormatException e) {
                        // Skip invalid entries
                    }
                }
            }
        } catch (java.io.IOException e) {
            throw new PersistenceException("Failed to load game results", e);
        }
        
        return results;
    }
}