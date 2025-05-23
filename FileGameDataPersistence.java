import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

class FileGameDataPersistence implements GameDataPersistence {
    private final String fileName;
    
    public FileGameDataPersistence(String fileName) {
        this.fileName = fileName;
    }
    
    @Override
    public void saveGameResult(GameResult resultData) throws PersistenceException { // Ubah nama parameter agar jelas
        int nextGameNumber = getLastGameNumber() + 1;
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(fileName, true))) { // true untuk append
            writer.write(nextGameNumber + "," + resultData.getResult());
            writer.newLine();
        } catch (IOException e) {
            throw new PersistenceException("Failed to save game result", e);
        }
    }
    
    @Override
    public java.util.List<GameResult> loadGameResults() throws PersistenceException {
        java.util.List<GameResult> results = new java.util.ArrayList<>();
        java.io.File file = new java.io.File(fileName);
        if (!file.exists()) {
            return results; // Kembalikan list kosong jika file tidak ada
        }
        
        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // Lewati baris kosong
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    try {
                        GameResult result = new GameResult();
                        result.setGameNumber(parts[0].trim()); // Simpan nomor sebagai String
                        result.setResult(parts[1].trim());
                        results.add(result);
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping invalid entry in game_data.txt: " + line + " - " + e.getMessage());
                    }
                } else {
                     System.err.println("Skipping malformed entry in game_data.txt: " + line);
                }
            }
        } catch (java.io.IOException e) {
            throw new PersistenceException("Failed to load game results", e);
        }
        return results;
    }

    private int getLastGameNumber() throws PersistenceException {
        int lastNumber = 0;
        File file = new File(fileName);
        if (!file.exists() || file.length() == 0) { // Periksa juga jika file kosong
            return 0;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            String lastValidLine = null;
            while ((line = reader.readLine()) != null) {
                if (line.trim().matches("^\\d+,.*")) { // Pastikan baris dimulai dengan angka diikuti koma
                    lastValidLine = line.trim();
                }
            }
            if (lastValidLine != null) {
                String[] parts = lastValidLine.split(",", 2);
                lastNumber = Integer.parseInt(parts[0]);
            }
        } catch (IOException e) {
            throw new PersistenceException("Failed to read last game number", e);
        } catch (NumberFormatException e) {
            // Jika baris terakhir tidak memiliki format nomor yang valid, anggap 0 atau log error
            System.err.println("Warning: Could not parse game number from last valid line.");
        }
        return lastNumber;
    }
}