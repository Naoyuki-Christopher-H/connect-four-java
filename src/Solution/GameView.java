package Solution;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import javax.sound.sampled.*;
import java.io.*;

public class GameView extends JFrame
{
    // Constants for 2.5D perspective
    private static final int CELL_SIZE = 80;
    private static final int DEPTH_OFFSET = 15;
    private static final int BOARD_WIDTH = GameModel.COLS * CELL_SIZE;
    private static final int BOARD_HEIGHT = (GameModel.ROWS + 1) * CELL_SIZE + DEPTH_OFFSET;
    
    private final GameController controller;
    private BoardPanel boardPanel;
    private JLabel statusLabel;
    private int animatedColumn = -1;
    private int animatedY = 0;
    private Timer animationTimer;
    
    public GameView(GameController controller)
    {
        this.controller = controller;
        initializeUI();
    }
    
    private void initializeUI()
    {
        setTitle("Connect Four - 2.5D");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        boardPanel = new BoardPanel();
        boardPanel.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        boardPanel.addMouseListener(new BoardMouseListener());
        
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(boardPanel, BorderLayout.CENTER);
        getContentPane().add(statusLabel, BorderLayout.SOUTH);
        
        updateStatus();
        pack();
        setLocationRelativeTo(null);
    }
    
    private class BoardPanel extends JPanel
    {
        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D)g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                               RenderingHints.VALUE_ANTIALIAS_ON);
            
            drawBoard(g2d);
            drawDiscs(g2d);
            drawAnimatedDisc(g2d);
        }
        
        private void drawBoard(Graphics2D g2d)
        {
            // Draw board with 2.5D perspective
            int boardTop = CELL_SIZE/2;
            
            // Board face
            g2d.setColor(new Color(200, 160, 80));
            g2d.fillRoundRect(0, boardTop, 
                             BOARD_WIDTH, BOARD_HEIGHT - boardTop - DEPTH_OFFSET, 
                             20, 20);
            
            // Board side (for 3D effect)
            g2d.setColor(new Color(180, 140, 60));
            Polygon side = new Polygon();
            side.addPoint(BOARD_WIDTH, boardTop);
            side.addPoint(BOARD_WIDTH + DEPTH_OFFSET, boardTop + DEPTH_OFFSET);
            side.addPoint(BOARD_WIDTH + DEPTH_OFFSET, BOARD_HEIGHT);
            side.addPoint(BOARD_WIDTH, BOARD_HEIGHT - DEPTH_OFFSET);
            g2d.fill(side);
            
            // Draw cells
            for (int row = 0; row < GameModel.ROWS; row++)
            {
                for (int col = 0; col < GameModel.COLS; col++)
                {
                    drawCell(g2d, row, col);
                }
            }
        }
        
        private void drawCell(Graphics2D g2d, int row, int col)
        {
            int x = col * CELL_SIZE + CELL_SIZE/2;
            int y = row * CELL_SIZE + CELL_SIZE + CELL_SIZE/2;
            
            // Cell hole with depth
            Ellipse2D holeFront = new Ellipse2D.Float(
                x - CELL_SIZE/3, y - CELL_SIZE/3, 
                CELL_SIZE*2/3, CELL_SIZE*2/3);
            
            Ellipse2D holeBack = new Ellipse2D.Float(
                x - CELL_SIZE/3 + DEPTH_OFFSET/2, 
                y - CELL_SIZE/3 + DEPTH_OFFSET/2, 
                CELL_SIZE*2/3, CELL_SIZE*2/3);
                
            // Hole gradient for depth
            GradientPaint holeGradient = new GradientPaint(
                x, y, new Color(100, 100, 100),
                x + DEPTH_OFFSET, y + DEPTH_OFFSET, new Color(60, 60, 60));
            
            g2d.setPaint(holeGradient);
            g2d.fill(holeBack);
            
            g2d.setColor(new Color(80, 80, 80));
            g2d.fill(holeFront);
        }
        
        private void drawDiscs(Graphics2D g2d)
        {
            char[][] board = controller.getModel().getBoardState();
            
            for (int row = 0; row < GameModel.ROWS; row++)
            {
                for (int col = 0; col < GameModel.COLS; col++)
                {
                    if (board[row][col] != GameModel.EMPTY)
                    {
                        drawDisc(g2d, row, col, board[row][col], false);
                    }
                }
            }
        }
        
        private void drawDisc(Graphics2D g2d, int row, int col, char player, boolean isAnimated)
        {
            int x = col * CELL_SIZE + CELL_SIZE/2;
            int y = isAnimated ? animatedY : row * CELL_SIZE + CELL_SIZE + CELL_SIZE/2;
            
            Color discColor = getPlayerColor(player);
            
            // Disc shadow
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.fillOval(x - CELL_SIZE/3 + 3, y - CELL_SIZE/3 + 3, 
                        CELL_SIZE*2/3, CELL_SIZE*2/3);
            
            // Main disc
            g2d.setColor(discColor);
            g2d.fillOval(x - CELL_SIZE/3, y - CELL_SIZE/3, 
                        CELL_SIZE*2/3, CELL_SIZE*2/3);
            
            // Disc highlight for 3D effect
            if (!isAnimated)
            {
                GradientPaint highlight = new GradientPaint(
                    x, y - CELL_SIZE/6, new Color(255, 255, 255, 150),
                    x, y, new Color(255, 255, 255, 0));
                
                g2d.setPaint(highlight);
                g2d.fillOval(x - CELL_SIZE/4, y - CELL_SIZE/4, 
                             CELL_SIZE/2, CELL_SIZE/3);
            }
        }
        
        private void drawAnimatedDisc(Graphics2D g2d)
        {
            if (animatedColumn >= 0 && animatedColumn < GameModel.COLS)
            {
                drawDisc(g2d, 0, animatedColumn, controller.getCurrentPlayer(), true);
            }
        }
    }
    
    private Color getPlayerColor(char player)
    {
        String colorName = (player == GameModel.RED) ? 
            controller.getPlayer1Color() : controller.getPlayer2Color();
        
        switch (colorName)
        {
            case "Red": return Color.RED;
            case "Blue": return Color.BLUE;
            case "Green": return Color.GREEN;
            case "Yellow": return Color.YELLOW;
            default: return Color.RED;
        }
    }
    
    private class BoardMouseListener extends MouseAdapter
    {
        @Override
        public void mouseClicked(MouseEvent e)
        {
            int column = e.getX() / CELL_SIZE;
            if (column >= 0 && column < GameModel.COLS && 
                !controller.getModel().isColumnFull(column))
            {
                startDiscAnimation(column);
            }
        }
    }
    
    private void startDiscAnimation(int column)
    {
        if (animationTimer != null && animationTimer.isRunning())
        {
            return;
        }
        
        animatedColumn = column;
        animatedY = CELL_SIZE/2;
        
        int targetRow = controller.getModel().getFirstAvailableRow(column);
        int targetY = (targetRow + 1) * CELL_SIZE + CELL_SIZE/2;
        
        animationTimer = new Timer(10, e -> 
        {
            animatedY += 8;
            
            if (animatedY >= targetY)
            {
                animatedY = targetY;
                animationTimer.stop();
                controller.makeMove(animatedColumn);
                animatedColumn = -1;
            }
            
            boardPanel.repaint();
        });
        
        animationTimer.start();
    }
    
    public void updateBoard()
    {
        boardPanel.repaint();
        updateStatus();
    }
    
    private void updateStatus()
    {
        String playerName = (controller.getCurrentPlayer() == GameModel.RED) ? 
            controller.getPlayer1Name() : controller.getPlayer2Name();
        statusLabel.setText(playerName + "'s turn");
    }
    
    public void showWinner(char winner)
    {
        String winnerName = (winner == GameModel.RED) ? 
            controller.getPlayer1Name() : controller.getPlayer2Name();
        statusLabel.setText(winnerName + " wins!");
        
        // Celebration animation
        Timer celebrationTimer = new Timer(500, e -> 
        {
            boardPanel.setBackground(boardPanel.getBackground() == Color.WHITE ? 
                Color.YELLOW : Color.WHITE);
            boardPanel.repaint();
        });
        celebrationTimer.setRepeats(false);
        celebrationTimer.start();
    }
    
    public void showTie()
    {
        statusLabel.setText("Game ended in a tie!");
    }
}