package com.backbase.kalah.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * A pit in the Kalah game that can be a normal pit or a house (Kalah)
 *
 * @author Mohamed Morsey
 * Date: 2018-11-24
 **/
@Entity
public class Pit {
    private long id;

    private int index;
    private int nextPitIndex;
    private int oppositePitIndex;
    private int stoneCount;
    private boolean isKalah;

    private Board board;

    public Pit(){
        this.id = 0L;
    }

    public Pit(int index, int nextPitIndex, int oppositePitIndex, int stoneCount, boolean isKalah) {
        this();
        this.index = index;
        this.nextPitIndex = nextPitIndex;
        this.oppositePitIndex = oppositePitIndex;
        this.stoneCount = stoneCount;
        this.isKalah = isKalah;
    }

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getNextPitIndex() {
        return nextPitIndex;
    }

    public void setNextPitIndex(int nextPitIndex) {
        this.nextPitIndex = nextPitIndex;
    }

    public int getOppositePitIndex() {
        return oppositePitIndex;
    }

    public void setOppositePitIndex(int oppositePitIndex) {
        this.oppositePitIndex = oppositePitIndex;
    }

    public boolean isKalah() {
        return isKalah;
    }

    public void setKalah(boolean kalah) {
        isKalah = kalah;
    }

    public int getStoneCount() {
        return stoneCount;
    }

    public void setStoneCount(int stoneCount) {
        this.stoneCount = stoneCount;
    }

    // Increments the number of stones by one
    public int incrementStones(){
        return ++stoneCount;
    }

    @ManyToOne
    @JoinColumn(name = "pit_board_id")
    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
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
