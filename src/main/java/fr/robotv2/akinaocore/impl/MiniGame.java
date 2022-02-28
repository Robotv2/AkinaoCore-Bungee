package fr.robotv2.akinaocore.impl;

public enum MiniGame {
    PRACTICE("Practice", "practice"),
    DE_A_COUDRE("Dé à Coudre", "de-a-coudre"),
    DEMON_SLAYER_ADVENTURE("Demon Slayer Adventure", "demon-slayer-adventure"),
    DEMON_SLAYER_UHC("Demon Slayer UHC", "demon-slayer-uhc"),
    PVP_SWAP("PVP swap", "pvp-swap");

    String formattedName;
    String serverPrefix;
    MiniGame(String formattedName, String serverPrefix) {
        this.formattedName = formattedName;
        this.serverPrefix = serverPrefix;
    }

    public String getFormattedName() {
        return formattedName;
    }

    public String getServerPrefix() {
        return serverPrefix;
    }

    public String toLowerCase() {
        return this.toString().toLowerCase();
    }
}


