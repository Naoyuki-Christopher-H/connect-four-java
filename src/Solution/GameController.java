package Solution;

public class GameController
{
    private final GameModel model;
    private final GameConfig config;
    private GameView view;
    private char currentPlayer;
    
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
        try
        {
            model.reset();
            currentPlayer = GameModel.RED;
            view.updateBoard();
        }
        catch (Exception e)
        {
            view.showError("Failed to start new game: " + e.getMessage());
        }
    }
    
    public void makeMove(int column)
    {
        try
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
        catch (IllegalArgumentException e)
        {
            view.showError("Failed to make move: " + e.getMessage());
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
        try
        {
            int column = findBestMove();
            makeMove(column);
        }
        catch (Exception e)
        {
            view.showError("Computer failed to make move: " + e.getMessage());
        }
    }
    
    private int findBestMove()
    {
        AIEngine engine = new AIEngine(model, config.getMaxDepth());
        return engine.findBestMove(currentPlayer);
    }
}