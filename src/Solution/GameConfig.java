package Solution;

public final class GameConfig
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
    
    public GameConfig(int gameType, int difficultyLevel) throws IllegalArgumentException
    {
        setGameType(gameType);
        setDifficulty(difficultyLevel);
    }
    
    public void setGameType(int gameType) throws IllegalArgumentException
    {
        if (gameType < HUMAN_HUMAN || gameType > COMPUTER_COMPUTER)
        {
            throw new IllegalArgumentException("Invalid game type: " + gameType);
        }
        this.gameType = gameType;
    }
    
    public void setDifficulty(int difficultyLevel) throws IllegalArgumentException
    {
        if (difficultyLevel < BEGINNER || difficultyLevel > EXPERT)
        {
            throw new IllegalArgumentException("Invalid difficulty level: " + difficultyLevel);
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