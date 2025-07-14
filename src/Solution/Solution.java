package Solution;

import javax.swing.SwingUtilities;

public class Solution
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> 
        {
            try
            {
                GameConfig config = new GameConfig(GameConfig.HUMAN_COMPUTER, GameConfig.EXPERT);
                GameController controller = new GameController(config);
                GameView view = new GameView(controller);
                controller.setView(view);
                view.setVisible(true);
            }
            catch (Exception e)
            {
                System.err.println("Failed to initialize game: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}