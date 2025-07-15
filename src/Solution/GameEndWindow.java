package Solution;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameEndWindow extends JFrame
{
    public GameEndWindow(String winnerName)
    {
        setTitle("Game Over");
        setSize(350, 200);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(242, 242, 247));
        
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(3, 1, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        contentPanel.setBackground(new Color(242, 242, 247));
        
        // Winner label
        JLabel winnerLabel = new JLabel("WINNER: " + winnerName, SwingConstants.CENTER);
        winnerLabel.setFont(new Font("SF Pro Display", Font.BOLD, 20));
        contentPanel.add(winnerLabel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(new Color(242, 242, 247));
        
        // Main Menu button
        JButton menuButton = createButton("MAIN MENU");
        menuButton.addActionListener(e -> 
        {
            new PlayerSelectionWindow().setVisible(true);
            dispose();
        });
        buttonPanel.add(menuButton);
        
        // Exit button
        JButton exitButton = createButton("EXIT");
        exitButton.addActionListener(e -> System.exit(0));
        buttonPanel.add(exitButton);
        
        contentPanel.add(new JPanel()); // Spacer
        contentPanel.add(buttonPanel);
        
        add(contentPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
    }
    
    private JButton createButton(String text)
    {
        JButton button = new JButton(text);
        button.setFont(new Font("SF Pro Text", Font.BOLD, 14));
        button.setBackground(new Color(0, 122, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent e)
            {
                button.setBackground(new Color(10, 132, 255));
            }
            
            @Override
            public void mouseExited(MouseEvent e)
            {
                button.setBackground(new Color(0, 122, 255));
            }
        });
        
        return button;
    }
}