package com.kalyzee.kontroller_services_api.dtos.video;

import com.google.gson.annotations.SerializedName;

public class StartWebrtcFeedbackSessionContent {

    @SerializedName("stuns")
    private String[] stuns;

    @SerializedName("turns")
    private String[] turns;

    public StartWebrtcFeedbackSessionContent(String[] stuns, String[] turns) {
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
