package Solution;

import java.util.Arrays;

public final class GameModel
{
    public static final char 
        RED = 'R', 
        BLACK = 'B', 
        EMPTY = ' ', 
        RED_WIN = 'W', 
        BLACK_WIN = 'L';
    
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
    
    public void makeMove(int col, char player) throws IllegalArgumentException
    {
        if (col < 0 || col >= COLS)
        {
            throw new IllegalArgumentException("Column " + col + " is out of bounds");
        }
        if (isColumnFull(col))
        {
            throw new IllegalArgumentException("Column " + col + " is already full");
        }
        if (player != RED && player != BLACK)
        {
            throw new IllegalArgumentException("Invalid player: " + player);
        }
        
        int row = availableRows[col]--;
        board[row][col] = player;
        moveCount++;
    }
    
    public void undoMove(int col) throws IllegalArgumentException
    {
        if (col < 0 || col >= COLS)
        {
            throw new IllegalArgumentException("Column " + col + " is out of bounds");
        }
        if (availableRows[col] >= ROWS - 1)
        {
            throw new IllegalArgumentException("Column " + col + " is already empty");
        }
        
        int row = ++availableRows[col];
        board[row][col] = EMPTY;
        moveCount--;
    }
    
    public char checkWinner()
    {
        // Check horizontal
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
        
        // Check vertical
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
        
        // Check diagonal (top-left to bottom-right)
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
        
        // Check diagonal (bottom-left to top-right)
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
}