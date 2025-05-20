public class KoleksiPlayer<G> {
    private Player<G>[] players;
    private int size;

    @SuppressWarnings("unchecked")
    public KoleksiPlayer(int capacity) {
        this.players = (Player<G>[]) new Player[capacity];
        this.size = 0;
    }

    public void addPlayer(Player<G> player) {
        if (size < players.length) {
            players[size++] = player;
        } else {
            System.out.println("Koleksi pemain penuh");
        }
    }

    public Player<G> getPlayer(int index) {
        if (index >= 0 && index < size) {
            return players[index];
        }
        return null;
    }

    public int getSize() {
        return size;
    }
}
