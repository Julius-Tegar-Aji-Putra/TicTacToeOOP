import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

class FileGameDataPersistence implements GameDataPersistence {
    private final String fileName;
    private static final int DEFAULT_HISTORY_CAPACITY = 100;
    
    public FileGameDataPersistence(String fileName) {
        this.fileName = fileName;
    }
    
    @Override
    public void saveGameResult(GameResult resultData) throws PersistenceException {
        int nextGameNumber = getLastGameNumber() + 1;
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(fileName, true))) {
            writer.write(nextGameNumber + "," + resultData.getResult());
            writer.newLine();
        } catch (IOException e) {
            throw new PersistenceException("Gagal menyimpan hasil permainan", e);
        }
    }
    
    @Override
    public KoleksiGameResult loadGameResults() throws PersistenceException {
        KoleksiGameResult gameResultsCollection = new KoleksiGameResult(DEFAULT_HISTORY_CAPACITY);
        
        File file = new File(fileName);
        if (!file.exists()) {
            return gameResultsCollection;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    GameResult result = new GameResult();
                    result.setGameNumber(parts[0].trim());
                    result.setResult(parts[1].trim());
                    gameResultsCollection.addResult(result); 
                } else {
                     System.err.println("Melewatkan entri cacat format di game_data.txt: " + line);
                }
            }
        } catch (IOException e) {
            throw new PersistenceException("Gagal memuat hasil permainan", e);
        }
        
        return gameResultsCollection;
    }

    private int getLastGameNumber() throws PersistenceException {
        int lastNumber = 0;
        File file = new File(fileName);
        if (!file.exists() || file.length() == 0) {
            return 0;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            String lastValidLine = null;
            while ((line = reader.readLine()) != null) {
                if (line.trim().matches("^\\d+,.*")) {
                    lastValidLine = line.trim();
                }
            }
            if (lastValidLine != null) {
                String[] parts = lastValidLine.split(",", 2);
                lastNumber = Integer.parseInt(parts[0]);
            }
        } catch (IOException e) {
            throw new PersistenceException("Gagal membaca nomor permainan terakhir", e);
        } catch (NumberFormatException e) {
            System.err.println("Peringatan: Tidak dapat mem-parse nomor permainan dari baris valid terakhir.");
        }
        return lastNumber;
    }
}