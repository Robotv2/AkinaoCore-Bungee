package fr.robotv2.akinaocore.impl;

public enum Currency {
    COINS("coins"),
    AKINAOPOINTS("akinao-points");

    String path;
    Currency(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String toLowerCase() {
        return this.toString().toLowerCase();
    }
}
