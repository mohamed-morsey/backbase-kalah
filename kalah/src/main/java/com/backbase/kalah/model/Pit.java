package com.backbase.kalah.model;

/**
 * A pit in the Kalah game that can be a normal pit or a house (Kalah)
 *
 * @author Mohamed Morsey
 * Date: 2018-11-24
 **/
public class Pit {
    private int index;
    private int nextPitIndex;
    private int oppositePitIndex;
    private int stoneCount;
    private boolean isKalah;

    public int getIndex() {
        return index;
    }

    public int getNextPitIndex() {
        return nextPitIndex;
    }

    public int getOppositePitIndex() {
        return oppositePitIndex;
    }

    public boolean isKalah() {
        return isKalah;
    }

    public int getStoneCount() {
        return stoneCount;
    }

    public void setStoneCount(int stoneCount) {
        this.stoneCount = stoneCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Pit)) {
            return false;
        }

        Pit pit = (Pit) o;

        return index == pit.index;
    }

    @Override
    public int hashCode() {
        return index;
    }

    @Override
    public String toString() {
        return "Pit{" +
                "index=" + index +
                ", nextPitIndex=" + nextPitIndex +
                ", oppositePitIndex=" + oppositePitIndex +
                ", stoneCount=" + stoneCount +
                ", isKalah=" + isKalah +
                '}';
    }
}