class PlayerFactory {
    public static <G> Player<G> createHumanPlayer(String name, String symbol) {
        return new HumanPlayer<>(name, symbol);
    }
    
    public static <G> Player<G> createComputerPlayer(String name, String symbol) {
        return new ComputerPlayer<>(name, symbol);
    }
}