package fr.robotv2.akinaocore.impl;

public enum MiniGame {
    PRACTICE("Practice"),
    DE_A_COUDRE("Dé à Coudre"),
    DEMON_SLAYER_ADVENTURE("Demon Slayer Adventure"),
    DEMON_SLAYER_UHC("Demon Slayer UHC"),
    PVP_SWAP("PVP swap");

    String formattedName;
    MiniGame(String formattedName) {
        this.formattedName = formattedName;
    }

    public String getFormattedName() {
        return formattedName;
    }

    public String toLowerCase() {
        return this.toString().toLowerCase();
    }
}


