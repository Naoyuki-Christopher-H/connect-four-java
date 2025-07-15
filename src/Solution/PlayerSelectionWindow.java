package Solution;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PlayerSelectionWindow extends JFrame
{
    private final JTextField player1Field;
    private final JTextField player2Field;
    private final JComboBox<String> difficultyBox;
    
    public PlayerSelectionWindow()
    {
        setTitle("Connect Four Setup");
        setSize(400, 295);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(242, 242, 247));
        
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(5, 1, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        contentPanel.setBackground(new Color(242, 242, 247));
        
        // Title label
        JLabel titleLabel = new JLabel("Player Setup", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 20));
        contentPanel.add(titleLabel);
        
        // Player 1 field
        player1Field = createTextField("Player 1");
        contentPanel.add(createInputPanel("Player 1:", player1Field));
        
        // Player 2 field
        player2Field = createTextField("Player 2");
        contentPanel.add(createInputPanel("Player 2:", player2Field));
        
        // Difficulty dropdown
        difficultyBox = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        difficultyBox.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        contentPanel.add(createInputPanel("Difficulty:", difficultyBox));
        
        // Start button
        JButton startButton = createButton("Start Game");
        startButton.addActionListener(this::startGame);
        contentPanel.add(startButton);
        
        add(contentPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
    }
    
    private JTextField createTextField(String placeholder)
    {
        JTextField field = new JTextField(placeholder);
        field.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 209, 214), 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        return field;
    }
    
    private JPanel createInputPanel(String labelText, JComponent field)
    {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(new Color(242, 242, 247));
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        panel.add(label, BorderLayout.WEST);
        
        panel.add(field, BorderLayout.CENTER);
        return panel;
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
        
        // Add subtle hover effect
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
    
    private void startGame(ActionEvent e)
    {
        String player1Name = player1Field.getText().trim();
        String player2Name = player2Field.getText().trim();
        
        if (player1Name.isEmpty()) player1Name = "Player 1";
        if (player2Name.isEmpty()) player2Name = "Player 2";
        
        int difficultyLevel;
        switch ((String)difficultyBox.getSelectedItem())
        {
            case "Easy":
                difficultyLevel = GameConfig.BEGINNER;
                break;
            case "Medium":
                difficultyLevel = GameConfig.INTERMEDIATE;
                break;
            case "Hard":
                difficultyLevel = GameConfig.EXPERT;
                break;
            default:
                difficultyLevel = GameConfig.INTERMEDIATE;
        }
        
        GameConfig config = new GameConfig(GameConfig.HUMAN_HUMAN, difficultyLevel);
        GameController controller = new GameController(config);
        controller.setPlayerNames(player1Name, player2Name);
        
        GameView view = new GameView(controller);
        controller.setView(view);
        view.setVisible(true);
        
        dispose();
    }
}