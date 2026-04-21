package com.tdt4240Grp04.clashofclaws.states;

public class ResultsData {
    public boolean isWinner;
    public String playerName;
    public int catIndex;
    public int score;
    public int kills;
    public int kibbleCount;
    public float survivalSeconds;

    public ResultsData(boolean isWinner, String playerName, int catIndex,
                       int score, int kills, int kibbleCount, float survivalSeconds) {
        this.isWinner       = isWinner;
        this.playerName     = playerName;
        this.catIndex       = catIndex;
        this.score          = score;
        this.kills          = kills;
        this.kibbleCount    = kibbleCount;
        this.survivalSeconds = survivalSeconds;
    }
}
