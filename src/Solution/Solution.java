package Solution;

import javax.swing.*;

public class Solution
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> 
        {
            PlayerSelectionWindow selectionWindow = new PlayerSelectionWindow();
            selectionWindow.setVisible(true);
        });
    }
}