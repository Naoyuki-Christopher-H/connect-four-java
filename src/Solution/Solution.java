package Solution;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class Solution
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> 
        {
            GameConfig config = new GameConfig(GameConfig.HUMAN_COMPUTER, GameConfig.EXPERT);
            GameController controller = new GameController(config);
            GameView view = new GameView(controller);
            controller.setView(view);
            view.setVisible(true);
        });
    }
}

final class GameConfig
{
    public static final int
        HUMAN_HUMAN = 1,
        HUMAN_COMPUTER = 2,
        COMPUTER_HUMAN = 3,
        COMPUTER_COMPUTER = 4,
        
        BEGINNER = 1,
        INTERMEDIATE = 2,
        ADVANCED = 3,
        EXPERT = 4;
    
    private int gameType;
    private int difficultyLevel;
    
    public GameConfig(int gameType, int difficultyLevel)
    {
        setGameType(gameType);
        setDifficulty(difficultyLevel);
    }
    
    public void setGameType(int gameType)
    {
        if (gameType < HUMAN_HUMAN || gameType > COMPUTER_COMPUTER)
        {
            throw new IllegalArgumentException("Invalid game type");
        }
        this.gameType = gameType;
    }
    
    public void setDifficulty(int difficultyLevel)
    {
        if (difficultyLevel < BEGINNER || difficultyLevel > EXPERT)
        {
            throw new IllegalArgumentException("Invalid difficulty level");
        }
        this.difficultyLevel = difficultyLevel;
    }
    
    public int getGameType()
    {
        return gameType;
    }
    
    public int getDifficultyLevel()
    {
        return difficultyLevel;
    }
    
    public int getMaxDepth()
    {
        return difficultyLevel << 1;
    }
}

final class GameModel
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
    
    public void makeMove(int col, char player)
    {
        if (col < 0 || col >= COLS || isColumnFull(col))
        {
            throw new IllegalArgumentException("Invalid move");
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

class GameController
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
    
    public void startNewGame()
    {
        model.reset();
        currentPlayer = GameModel.RED;
        view.updateBoard();
    }
    
    public void makeMove(int column)
    {
        if (model.isColumnFull(column)) return;
        
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

    Object getModel()
    {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    char getCurrentPlayer()
    {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

class GameView extends JFrame
{
    private static final int CELL_SIZE = 60;
    private static final int BOARD_WIDTH = GameModel.COLS * CELL_SIZE;
    private static final int BOARD_HEIGHT = (GameModel.ROWS + 1) * CELL_SIZE;
    
    private final GameController controller;
    private JPanel boardPanel;
    private JLabel statusLabel;
    
    public GameView(GameController controller)
    {
        this.controller = controller;
        initializeUI();
    }
    
    private void initializeUI()
    {
        setTitle("Connect Four");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        boardPanel = new JPanel()
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                drawBoard(g);
            }
        };
        
        boardPanel.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        boardPanel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                int column = e.getX() / CELL_SIZE;
                controller.makeMove(column);
            }
        });
        
        statusLabel = new JLabel("Red's turn", SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        JMenuItem newGameItem = new JMenuItem("New Game");
        newGameItem.addActionListener(e -> controller.startNewGame());
        gameMenu.add(newGameItem);
        menuBar.add(gameMenu);
        setJMenuBar(menuBar);
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(boardPanel, BorderLayout.CENTER);
        getContentPane().add(statusLabel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void drawBoard(Graphics g)
    {
        char[][] board = controller.getModel().getBoardState();
        
        // Draw cells
        for (int row = 0; row < GameModel.ROWS; row++)
        {
            for (int col = 0; col < GameModel.COLS; col++)
            {
                drawCell(g, row, col, board[row][col]);
            }
        }
        
        // Draw column indicators
        g.setColor(Color.BLUE);
        for (int col = 0; col < GameModel.COLS; col++)
        {
            g.drawRect(col * CELL_SIZE, 0, CELL_SIZE, CELL_SIZE);
        }
    }
    
    private void drawCell(Graphics g, int row, int col, char value)
    {
        int x = col * CELL_SIZE;
        int y = (row + 1) * CELL_SIZE;
        
        // Draw cell background
        g.setColor(Color.BLUE);
        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
        
        // Draw checker
        switch (value)
        {
            case GameModel.RED:
                g.setColor(Color.RED);
                break;
            case GameModel.BLACK:
                g.setColor(Color.BLACK);
                break;
            case GameModel.RED_WIN:
                g.setColor(Color.PINK);
                break;
            case GameModel.BLACK_WIN:
                g.setColor(Color.GRAY);
                break;
            default:
                g.setColor(Color.WHITE);
        }
        
        g.fillOval(x + 5, y + 5, CELL_SIZE - 10, CELL_SIZE - 10);
    }
    
    public void updateBoard()
    {
        boardPanel.repaint();
        updateStatus();
    }
    
    private void updateStatus()
    {
        char currentPlayer = controller.getCurrentPlayer();
        statusLabel.setText((currentPlayer == GameModel.RED ? "Red" : "Black") + "'s turn");
    }
    
    public void showWinner(char winner)
    {
        boardPanel.repaint();
        statusLabel.setText((winner == GameModel.RED ? "Red" : "Black") + " wins!");
    }
    
    public void showTie()
    {
        statusLabel.setText("Game ended in a tie!");
    }
}

class AIEngine
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
            if (model.isColumnFull(col)) continue;
            
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
                if (model.isColumnFull(col)) continue;
                
                model.makeMove(col, player);
                int score = minimax(depth - 1, GameModel.BLACK, alpha, beta);
                model.undoMove(col);
                
                maxScore = Math.max(maxScore, score);
                alpha = Math.max(alpha, score);
                if (beta <= alpha) break;
            }
            return maxScore;
        }
        else
        {
            int minScore = Integer.MAX_VALUE;
            for (int col = 0; col < GameModel.COLS; col++)
            {
                if (model.isColumnFull(col)) continue;
                
                model.makeMove(col, player);
                int score = minimax(depth - 1, GameModel.RED, alpha, beta);
                model.undoMove(col);
                
                minScore = Math.min(minScore, score);
                beta = Math.min(beta, score);
                if (beta <= alpha) break;
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
        
        for (int i = 0; i < 4; i++)
        {
            char cell = model.getBoardState()[row + i * rowStep][col + i * colStep];
            if (cell == GameModel.RED) redCount++;
            else if (cell == GameModel.BLACK) blackCount++;
        }
        
        return evaluateCounts(redCount, blackCount);
    }
    
    private int evaluateCounts(int redCount, int blackCount)
    {
        if (redCount > 0 && blackCount > 0) return 0;
        
        if (redCount > 0) return SCORE_TABLE[redCount];
        if (blackCount > 0) return -SCORE_TABLE[blackCount];
        
        return 0;
    }
    
    private int evaluateCenterControl()
    {
        int centerCol = GameModel.COLS / 2;
        int score = 0;
        
        for (int row = 0; row < GameModel.ROWS; row++)
        {
            char cell = model.getBoardState()[row][centerCol];
            if (cell == GameModel.RED) score += 2;
            else if (cell == GameModel.BLACK) score -= 2;
        }
        
        return score;
    }
    
    public int getNodesEvaluated()
    {
        return nodesEvaluated;
    }
}