package Solution;

public class GameController
{
    private final GameModel model;
    private final GameConfig config;
    private GameView view;
    private char currentPlayer;
    private String player1Name = "Player 1";
    private String player2Name = "Player 2";
    private String player1Color = "Red";
    private String player2Color = "Blue";
    
    public GameController(GameConfig config)
    {
        this.config = config;
        this.model = new GameModel();
        this.currentPlayer = GameModel.RED;
    }
    
    public void setView(GameView view)
    {
        this.view = view;
    }
    
    public void setPlayerNames(String player1Name, String player2Name)
    {
        this.player1Name = player1Name;
        this.player2Name = player2Name;
    }
    
    public void setPlayerColors(String player1Color, String player2Color)
    {
        this.player1Color = player1Color;
        this.player2Color = player2Color;
    }
    
    public String getPlayer1Name()
    {
        return player1Name;
    }
    
    public String getPlayer2Name()
    {
        return player2Name;
    }
    
    public String getPlayer1Color()
    {
        return player1Color;
    }
    
    public String getPlayer2Color()
    {
        return player2Color;
    }
    
    public GameModel getModel()
    {
        return model;
    }
    
    public char getCurrentPlayer()
    {
        return currentPlayer;
    }
    
    public void startNewGame()
    {
        model.reset();
        currentPlayer = GameModel.RED;
        view.updateBoard();
    }
    
    public void makeMove(int column)
    {
        if (model.isColumnFull(column))
        {
            return;
        }
        
        model.makeMove(column, currentPlayer);
        view.updateBoard();
        
        char winner = model.checkWinner();
        if (winner != GameModel.EMPTY)
        {
            view.showWinner(winner);
            return;
        }
        
        if (model.isBoardFull())
        {
            view.showTie();
            return;
        }
        
        switchPlayer();
        
        if (shouldComputerMove())
        {
            makeComputerMove();
        }
    }
    
    private void switchPlayer()
    {
        currentPlayer = (currentPlayer == GameModel.RED) ? GameModel.BLACK : GameModel.RED;
    }
    
    private boolean shouldComputerMove()
    {
        int gameType = config.getGameType();
        return (gameType == GameConfig.HUMAN_COMPUTER && currentPlayer == GameModel.BLACK) ||
               (gameType == GameConfig.COMPUTER_HUMAN && currentPlayer == GameModel.RED) ||
               (gameType == GameConfig.COMPUTER_COMPUTER);
    }
    
    private void makeComputerMove()
    {
        int column = findBestMove();
        makeMove(column);
    }
    
    private int findBestMove()
    {
        AIEngine engine = new AIEngine(model, config.getMaxDepth());
        return engine.findBestMove(currentPlayer);
    }
}