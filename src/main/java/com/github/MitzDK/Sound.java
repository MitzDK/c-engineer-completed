package com.github.MitzDK;

public enum Sound {
    ITEM_DROP_1("goodsound.wav");

    private final String resourceName;

    Sound(String resNam) {
        resourceName = resNam;
    }

    String getResourceName() {
        return resourceName;
    }
}
