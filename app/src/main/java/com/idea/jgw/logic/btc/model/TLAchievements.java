package com.idea.jgw.logic.btc.model;

class TLAchievements {
    private static TLAchievements instance = null;

    private TLAchievements() {}

    public static TLAchievements instance() {
        if (instance == null) {
            instance = new TLAchievements();
        }
        return instance;
    }
}