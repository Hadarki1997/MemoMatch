package com.example.matchmemo;

public class Card {
    private int imageId;
    private boolean flipped;
    private boolean matched;


    public Card(int id) {
        setImageId(id);
        setFlipped(false);
        setMatched(false);
    }


    public int getImageId() {
        return imageId;
    }

    public void setImageId(int id) {
        imageId = id;
    }

    public boolean isFlipped() {
        return flipped;
    }

    public void setFlipped(boolean state) {
        flipped = state;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean state) {
        matched = state;
    }
}
