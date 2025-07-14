package Solution;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameView extends JFrame
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
                try
                {
                    drawBoard(g);
                }
                catch (Exception e)
                {
                    showError("Failed to draw board: " + e.getMessage());
                }
            }
        };
        
        boardPanel.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        boardPanel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                try
                {
                    int column = e.getX() / CELL_SIZE;
                    controller.makeMove(column);
                }
                catch (Exception ex)
                {
                    showError("Failed to process mouse click: " + ex.getMessage());
                }
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
    
    public void showError(String message)
    {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}