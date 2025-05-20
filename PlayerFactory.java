class PlayerFactory {
    public static Player createHumanPlayer(String name, String symbol) {
        return new HumanPlayer(name, symbol);
    }
    
    public static Player createComputerPlayer(String name, String symbol) {
        return new ComputerPlayer(name, symbol);
    }
}