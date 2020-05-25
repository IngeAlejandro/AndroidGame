package com.example.final_juego;

public class Units {
    private int image;
    private int value;
    private int sound;

    public Units(int image, int value, int sound){
        this.image=image;
        this.value=value;
        this.sound=sound;
    }

    public int getImage() {
        return image;
    }
    public int getValue() {
        return value;
    }
    public int getSound() {
        return sound;
    }
}
