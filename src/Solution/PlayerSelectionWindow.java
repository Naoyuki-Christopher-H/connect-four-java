package Solution;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PlayerSelectionWindow extends JFrame
{
    private final JTextField player1NameField;
    private final JTextField player2NameField;
    private final JComboBox<String> player1ColorBox;
    private final JComboBox<String> player2ColorBox;
    private final JComboBox<String> player1TypeBox;
    private final JComboBox<String> player2TypeBox;
    private final JComboBox<String> difficultyBox;
    
    public PlayerSelectionWindow()
    {
        setTitle("Player Setup");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(7, 2, 10, 10));
        
        // Player 1 components
        add(new JLabel("Player 1 Name:"));
        player1NameField = new JTextField("Player 1");
        add(player1NameField);
        
        add(new JLabel("Player 1 Color:"));
        player1ColorBox = new JComboBox<>(new String[]{"Red", "Blue", "Green", "Yellow"});
        add(player1ColorBox);
        
        add(new JLabel("Player 1 Type:"));
        player1TypeBox = new JComboBox<>(new String[]{"Human", "Computer"});
        player1TypeBox.addActionListener(this::updateDifficultyVisibility);
        add(player1TypeBox);
        
        // Player 2 components
        add(new JLabel("Player 2 Name:"));
        player2NameField = new JTextField("Player 2");
        add(player2NameField);
        
        add(new JLabel("Player 2 Color:"));
        player2ColorBox = new JComboBox<>(new String[]{"Red", "Blue", "Green", "Yellow"});
        add(player2ColorBox);
        
        add(new JLabel("Player 2 Type:"));
        player2TypeBox = new JComboBox<>(new String[]{"Human", "Computer"});
        player2TypeBox.addActionListener(this::updateDifficultyVisibility);
        add(player2TypeBox);
        
        // Difficulty level
        add(new JLabel("Difficulty:"));
        difficultyBox = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        add(difficultyBox);
        difficultyBox.setVisible(false);
        
        // Start button
        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(this::startGame);
        add(startButton);
        
        setLocationRelativeTo(null);
    }
    
    private void updateDifficultyVisibility(ActionEvent e)
    {
        boolean showDifficulty = player1TypeBox.getSelectedItem().equals("Computer") || 
                               player2TypeBox.getSelectedItem().equals("Computer");
        difficultyBox.setVisible(showDifficulty);
    }
    
    private void startGame(ActionEvent e)
    {
        String player1Name = player1NameField.getText();
        String player2Name = player2NameField.getText();
        String player1Color = (String)player1ColorBox.getSelectedItem();
        String player2Color = (String)player2ColorBox.getSelectedItem();
        boolean player1IsComputer = player1TypeBox.getSelectedItem().equals("Computer");
        boolean player2IsComputer = player2TypeBox.getSelectedItem().equals("Computer");
        String difficulty = (String)difficultyBox.getSelectedItem();
        
        int gameType;
        if (!player1IsComputer && !player2IsComputer)
        {
            gameType = GameConfig.HUMAN_HUMAN;
        }
        else if (!player1IsComputer && player2IsComputer)
        {
            gameType = GameConfig.HUMAN_COMPUTER;
        }
        else if (player1IsComputer && !player2IsComputer)
        {
            gameType = GameConfig.COMPUTER_HUMAN;
        }
        else
        {
            gameType = GameConfig.COMPUTER_COMPUTER;
        }
        
        int difficultyLevel;
        switch (difficulty)
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
        
        GameConfig config = new GameConfig(gameType, difficultyLevel);
        GameController controller = new GameController(config);
        controller.setPlayerNames(player1Name, player2Name);
        controller.setPlayerColors(player1Color, player2Color);
        
        GameView view = new GameView(controller);
        controller.setView(view);
        view.setVisible(true);
        
        dispose();
    }
    
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> new PlayerSelectionWindow().setVisible(true));
    }
}