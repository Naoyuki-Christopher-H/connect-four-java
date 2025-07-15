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
    
    private final int gameType;
    private final int difficultyLevel;
    
    public GameConfig(int gameType, int difficultyLevel)
    {
        if (gameType < HUMAN_HUMAN || gameType > COMPUTER_COMPUTER)
        {
            throw new IllegalArgumentException("Invalid game type");
        }
        if (difficultyLevel < BEGINNER || difficultyLevel > EXPERT)
        {
            throw new IllegalArgumentException("Invalid difficulty level");
        }
        
        this.gameType = gameType;
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
        switch (difficultyLevel)
        {
            case BEGINNER: return 2;
            case INTERMEDIATE: return 4;
            case ADVANCED: return 6;
            case EXPERT: return 8;
            default: return 4;
        }
    }
}