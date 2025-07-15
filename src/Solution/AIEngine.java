package Solution;

public class AIEngine
{
    private static final int[] SCORE_TABLE = {0, 1, 4, 32, 128, 512};
    
    private final GameModel model;
    private final int maxDepth;
    private int nodesEvaluated;
    
    public AIEngine(GameModel model, int maxDepth)
    {
        this.model = model;
        this.maxDepth = maxDepth;
    }
    
    public int findBestMove(char player)
    {
        nodesEvaluated = 0;
        int bestColumn = 0;
        int bestScore = (player == GameModel.RED) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        
        for (int col = 0; col < GameModel.COLS; col++)
        {
            if (model.isColumnFull(col))
            {
                continue;
            }
            
            model.makeMove(col, player);
            int score = minimax(maxDepth - 1, 
                              (player == GameModel.RED) ? GameModel.BLACK : GameModel.RED,
                              Integer.MIN_VALUE, 
                              Integer.MAX_VALUE);
            model.undoMove(col);
            
            if ((player == GameModel.RED && score > bestScore) ||
                (player == GameModel.BLACK && score < bestScore))
            {
                bestScore = score;
                bestColumn = col;
            }
        }
        
        return bestColumn;
    }
    
    private int minimax(int depth, char player, int alpha, int beta)
    {
        nodesEvaluated++;
        
        char winner = model.checkWinner();
        if (winner != GameModel.EMPTY)
        {
            return (winner == GameModel.RED) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        }
        
        if (depth == 0 || model.isBoardFull())
        {
            return evaluateBoard();
        }
        
        if (player == GameModel.RED)
        {
            int maxScore = Integer.MIN_VALUE;
            for (int col = 0; col < GameModel.COLS; col++)
            {
                if (model.isColumnFull(col))
                {
                    continue;
                }
                
                model.makeMove(col, player);
                int score = minimax(depth - 1, GameModel.BLACK, alpha, beta);
                model.undoMove(col);
                
                maxScore = Math.max(maxScore, score);
                alpha = Math.max(alpha, score);
                if (beta <= alpha)
                {
                    break;
                }
            }
            return maxScore;
        }
        else
        {
            int minScore = Integer.MAX_VALUE;
            for (int col = 0; col < GameModel.COLS; col++)
            {
                if (model.isColumnFull(col))
                {
                    continue;
                }
                
                model.makeMove(col, player);
                int score = minimax(depth - 1, GameModel.RED, alpha, beta);
                model.undoMove(col);
                
                minScore = Math.min(minScore, score);
                beta = Math.min(beta, score);
                if (beta <= alpha)
                {
                    break;
                }
            }
            return minScore;
        }
    }
    
    private int evaluateBoard()
    {
        int score = 0;
        
        // Evaluate all possible 4-in-a-row segments
        score += evaluateLines();
        
        // Center column preference
        score += evaluateCenterControl();
        
        return score;
    }
    
    private int evaluateLines()
    {
        int score = 0;
        
        // Check all horizontal, vertical and diagonal lines
        score += evaluateHorizontal();
        score += evaluateVertical();
        score += evaluateDiagonals();
        
        return score;
    }
    
    private int evaluateHorizontal()
    {
        int score = 0;
        
        for (int row = 0; row < GameModel.ROWS; row++)
        {
            for (int col = 0; col <= GameModel.COLS - 4; col++)
            {
                score += evaluateSegment(row, col, 0, 1);
            }
        }
        
        return score;
    }
    
    private int evaluateVertical()
    {
        int score = 0;
        
        for (int col = 0; col < GameModel.COLS; col++)
        {
            for (int row = 0; row <= GameModel.ROWS - 4; row++)
            {
                score += evaluateSegment(row, col, 1, 0);
            }
        }
        
        return score;
    }
    
    private int evaluateDiagonals()
    {
        int score = 0;
        
        // Top-left to bottom-right
        for (int row = 0; row <= GameModel.ROWS - 4; row++)
        {
            for (int col = 0; col <= GameModel.COLS - 4; col++)
            {
                score += evaluateSegment(row, col, 1, 1);
            }
        }
        
        // Bottom-left to top-right
        for (int row = 3; row < GameModel.ROWS; row++)
        {
            for (int col = 0; col <= GameModel.COLS - 4; col++)
            {
                score += evaluateSegment(row, col, -1, 1);
            }
        }
        
        return score;
    }
    
    private int evaluateSegment(int row, int col, int rowStep, int colStep)
    {
        int redCount = 0;
        int blackCount = 0;
        char[][] board = model.getBoardState();
        
        for (int i = 0; i < 4; i++)
        {
            char cell = board[row + i * rowStep][col + i * colStep];
            if (cell == GameModel.RED)
            {
                redCount++;
            }
            else if (cell == GameModel.BLACK)
            {
                blackCount++;
            }
        }
        
        return evaluateCounts(redCount, blackCount);
    }
    
    private int evaluateCounts(int redCount, int blackCount)
    {
        if (redCount > 0 && blackCount > 0)
        {
            return 0;
        }
        
        if (redCount > 0)
        {
            return SCORE_TABLE[redCount];
        }
        
        if (blackCount > 0)
        {
            return -SCORE_TABLE[blackCount];
        }
        
        return 0;
    }
    
    private int evaluateCenterControl()
    {
        int centerCol = GameModel.COLS / 2;
        int score = 0;
        char[][] board = model.getBoardState();
        
        for (int row = 0; row < GameModel.ROWS; row++)
        {
            char cell = board[row][centerCol];
            if (cell == GameModel.RED)
            {
                score += 2;
            }
            else if (cell == GameModel.BLACK)
            {
                score -= 2;
            }
        }
        
        return score;
    }
    
    public int getNodesEvaluated()
    {
        return nodesEvaluated;
    }
}