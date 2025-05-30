public class KoleksiGameResult {
    private GameResult[] results;
    private int size;
    private int capacity;

    public KoleksiGameResult(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Kapasitas harus lebih besar dari 0");
        }
        this.capacity = capacity; 
        this.results = new GameResult[capacity]; 
        this.size = 0;
    }

    public void addResult(GameResult result) {
        if (result == null) { 
            System.out.println("Tidak dapat menambahkan hasil null ke koleksi.");
            return;
        }
        if (size < capacity) { 
            results[size++] = result;
        } else {
            System.out.println("Koleksi hasil permainan penuh. Kapasitas maksimal: " + capacity);
        }
    }

    public GameResult getResult(int index) {
        if (index >= 0 && index < size) {
            return results[index];
        }
        return null;
    }

    public int getSize() {
        return size;
    }

    public int getCapacity() { 
        return capacity;
    }

    public boolean isEmpty() { 
        return size == 0;
    }
}