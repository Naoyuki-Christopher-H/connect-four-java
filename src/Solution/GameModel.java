package Solution;

import java.util.Arrays;

public final class GameModel
{
    public static final char 
        RED = 'R', 
        BLACK = 'B', 
        EMPTY = ' ';
    
    public static final int ROWS = 6;
    public static final int COLS = 7;
    
    private final char[][] board;
    private final int[] availableRows;
    private int moveCount;
    
    public GameModel()
    {
        board = new char[ROWS][COLS];
        availableRows = new int[COLS];
        reset();
    }
    
    public void reset()
    {
        for (int row = 0; row < ROWS; row++)
        {
            Arrays.fill(board[row], EMPTY);
        }
        Arrays.fill(availableRows, ROWS - 1);
        moveCount = 0;
    }
    
    public boolean isColumnFull(int col)
    {
        return availableRows[col] < 0;
    }
    
    public boolean isBoardFull()
    {
        return moveCount == ROWS * COLS;
    }
    
    public void makeMove(int col, char player)
    {
        if (col < 0 || col >= COLS || isColumnFull(col))
        {
            throw new IllegalArgumentException("Invalid move");
        }
        if (player != RED && player != BLACK)
        {
            throw new IllegalArgumentException("Invalid player");
        }
        
        int row = availableRows[col]--;
        board[row][col] = player;
        moveCount++;
    }
    
    public void undoMove(int col)
    {
        if (col < 0 || col >= COLS || availableRows[col] >= ROWS - 1)
        {
            throw new IllegalArgumentException("Invalid undo");
        }
        
        int row = ++availableRows[col];
        board[row][col] = EMPTY;
        moveCount--;
    }
    
    public char checkWinner()
    {
        // Horizontal check
        for (int row = 0; row < ROWS; row++)
        {
            for (int col = 0; col <= COLS - 4; col++)
            {
                char player = board[row][col];
                if (player != EMPTY &&
                    player == board[row][col+1] &&
                    player == board[row][col+2] &&
                    player == board[row][col+3])
                {
                    return player;
                }
            }
        }
        
        // Vertical check
        for (int col = 0; col < COLS; col++)
        {
            for (int row = 0; row <= ROWS - 4; row++)
            {
                char player = board[row][col];
                if (player != EMPTY &&
                    player == board[row+1][col] &&
                    player == board[row+2][col] &&
                    player == board[row+3][col])
                {
                    return player;
                }
            }
        }
        
        // Diagonal (top-left to bottom-right)
        for (int row = 0; row <= ROWS - 4; row++)
        {
            for (int col = 0; col <= COLS - 4; col++)
            {
                char player = board[row][col];
                if (player != EMPTY &&
                    player == board[row+1][col+1] &&
                    player == board[row+2][col+2] &&
                    player == board[row+3][col+3])
                {
                    return player;
                }
            }
        }
        
        // Diagonal (bottom-left to top-right)
        for (int row = 3; row < ROWS; row++)
        {
            for (int col = 0; col <= COLS - 4; col++)
            {
                char player = board[row][col];
                if (player != EMPTY &&
                    player == board[row-1][col+1] &&
                    player == board[row-2][col+2] &&
                    player == board[row-3][col+3])
                {
                    return player;
                }
            }
        }
        
        return EMPTY;
    }
    
    public char[][] getBoardState()
    {
        char[][] copy = new char[ROWS][COLS];
        for (int row = 0; row < ROWS; row++)
        {
            System.arraycopy(board[row], 0, copy[row], 0, COLS);
        }
        return copy;
    }
    
    public int getFirstAvailableRow(int col)
    {
        return availableRows[col];
    }
}