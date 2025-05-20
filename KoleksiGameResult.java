public class KoleksiGameResult<G> {
    private GameResult[] results;
    private int size;

    public KoleksiGameResult(int capacity) {
        this.results = (GameResult[]) new GameResult[capacity];
        this.size = 0;
    }

    public void addResult(GameResult result) {
        if (size < results.length) {
            results[size++] = result;
        } else {
            System.out.println("Koleksi hasil permainan penuh");
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
}