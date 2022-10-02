package com.kalyzee.panel_connection_manager.mappers.video;


import com.fasterxml.jackson.annotation.JsonProperty;

public class StartWebrtcFeedbackSessionContent {

    @JsonProperty("stuns")
    private String[] stuns;

    @JsonProperty("turns")
    private String[] turns;

    public StartWebrtcFeedbackSessionContent(@JsonProperty("stuns") String[] stuns,
                                             @JsonProperty("turns") String[] turns) {
        this.stuns = stuns;
        this.turns = turns;
    }

    public String[] getStuns() {
        return stuns;
    }

    public void setStuns(String[] stuns) {
        this.stuns = stuns;
    }

    public String[] getTurns() {
        return turns;
    }

    public void setTurns(String[] turns) {
        this.turns = turns;
    }
}
