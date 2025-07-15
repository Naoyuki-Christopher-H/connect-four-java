package Solution;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameView extends JFrame
{
    private static final int CELL_SIZE = 80;
    private static final int BOARD_WIDTH = GameModel.COLS * CELL_SIZE;
    private static final int BOARD_HEIGHT = (GameModel.ROWS + 1) * CELL_SIZE;
    
    private final GameController controller;
    private BoardPanel boardPanel;
    private JLabel statusLabel;
    private final int animatedColumn = -1;
    private Timer animationTimer;
    
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
        getContentPane().setBackground(new Color(242, 242, 247));
        
        boardPanel = new BoardPanel();
        boardPanel.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        boardPanel.addMouseListener(new BoardMouseListener());
        
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 16));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(15, 5, 15, 5));
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(boardPanel, BorderLayout.CENTER);
        getContentPane().add(statusLabel, BorderLayout.SOUTH);
        
        updateStatus();
        pack();
        setLocationRelativeTo(null);
    }
    
    private class BoardPanel extends JPanel
    {
        private int hoverColumn = -1;
        
        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D)g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                               RenderingHints.VALUE_ANTIALIAS_ON);
            
            drawBoard(g2d);
            drawDiscs(g2d);
            drawHoverIndicator(g2d);
            drawAnimatedDisc(g2d);
        }
        
        private void drawBoard(Graphics2D g2d)
        {
            g2d.setColor(Color.WHITE);
            g2d.fillRoundRect(0, CELL_SIZE, 
                            BOARD_WIDTH, BOARD_HEIGHT - CELL_SIZE, 
                            20, 20);
            
            for (int col = 0; col < GameModel.COLS; col++)
            {
                for (int row = 0; row < GameModel.ROWS; row++)
                {
                    int x = col * CELL_SIZE + CELL_SIZE/2;
                    int y = (row + 1) * CELL_SIZE + CELL_SIZE/2;
                    
                    g2d.setColor(new Color(220, 220, 220));
                    g2d.fillOval(x - CELL_SIZE/3 + 2, y - CELL_SIZE/3 + 2, 
                                CELL_SIZE*2/3, CELL_SIZE*2/3);
                    
                    g2d.setColor(new Color(242, 242, 247));
                    g2d.fillOval(x - CELL_SIZE/3, y - CELL_SIZE/3, 
                                CELL_SIZE*2/3, CELL_SIZE*2/3);
                }
            }
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
                        drawDisc(g2d, row, col, board[row][col]);
                    }
                }
            }
        }
        
        private void drawDisc(Graphics2D g2d, int row, int col, char player)
        {
            int x = col * CELL_SIZE + CELL_SIZE/2;
            int y = (row + 1) * CELL_SIZE + CELL_SIZE/2;
            
            Color discColor = (player == GameModel.RED) ? 
                new Color(255, 59, 48) : new Color(0, 122, 255);
            
            g2d.setColor(new Color(0, 0, 0, 20));
            g2d.fillOval(x - CELL_SIZE/3 + 2, y - CELL_SIZE/3 + 2, 
                        CELL_SIZE*2/3, CELL_SIZE*2/3);
            
            g2d.setColor(discColor);
            g2d.fillOval(x - CELL_SIZE/3, y - CELL_SIZE/3, 
                        CELL_SIZE*2/3, CELL_SIZE*2/3);
            
            GradientPaint highlight = new GradientPaint(
                x, y - CELL_SIZE/6, new Color(255, 255, 255, 100),
                x, y, new Color(255, 255, 255, 0));
            
            g2d.setPaint(highlight);
            g2d.fillOval(x - CELL_SIZE/4, y - CELL_SIZE/4, 
                         CELL_SIZE/2, CELL_SIZE/3);
        }
        
        private void drawHoverIndicator(Graphics2D g2d)
        {
            if (hoverColumn >= 0 && hoverColumn < GameModel.COLS && 
                !controller.getModel().isColumnFull(hoverColumn))
            {
                int x = hoverColumn * CELL_SIZE + CELL_SIZE/2;
                int y = CELL_SIZE/2;
                
                g2d.setColor(new Color(0, 122, 255, 100));
                g2d.fillOval(x - CELL_SIZE/3, y - CELL_SIZE/3, 
                            CELL_SIZE*2/3, CELL_SIZE*2/3);
            }
        }
        
        private void drawAnimatedDisc(Graphics2D g2d)
        {
            if (animatedColumn >= 0 && animatedColumn < GameModel.COLS)
            {
                drawDisc(g2d, 0, animatedColumn, controller.getCurrentPlayer());
            }
        }
        
        public void setHoverColumn(int column)
        {
            hoverColumn = column;
            repaint();
        }
    }
    
    private class BoardMouseListener extends MouseAdapter
    {
        @Override
        public void mouseClicked(MouseEvent e)
        {
            int column = e.getX() / CELL_SIZE;
            if (column >= 0 && column < GameModel.COLS)
            {
                controller.makeMove(column);
            }
        }
        
        @Override
        public void mouseMoved(MouseEvent e)
        {
            int column = e.getX() / CELL_SIZE;
            ((BoardPanel)boardPanel).setHoverColumn(column);
        }
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
        
        Timer closeTimer = new Timer(1000, e -> 
        {
            dispose();
            new GameEndWindow(winnerName).setVisible(true);
        });
        closeTimer.setRepeats(false);
        closeTimer.start();
    }
    
    public void showTie()
    {
        statusLabel.setText("Game ended in a tie");
        
        Timer closeTimer = new Timer(1000, e -> 
        {
            dispose();
            new GameEndWindow("No one - It's a tie!").setVisible(true);
        });
        closeTimer.setRepeats(false);
        closeTimer.start();
    }
}