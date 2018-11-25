package com.backbase.kalah.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

import static com.backbase.kalah.constant.Constants.COUNT_OF_ALL_PITS;

/**
 * Represents a Kalah game board
 *
 * @author Mohamed Morsey
 * Date: 2018-11-24
 **/
@Entity
public class Board {
    private long id;

    private List<Pit> pits;

    public Board() {
        id = 0L;
    }

    public Board(long id) {
        this.id = id;
        this.pits = new ArrayList<>(COUNT_OF_ALL_PITS);
    }

    public Board(long id, List<Pit> pits) {
        this.id = id;
        this.pits = pits;
    }

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Pit> getPits() {
        return pits;
    }

    public void setPits(List<Pit> pits) {
        this.pits = pits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Board)) {
            return false;
        }

        Board board = (Board) o;

        return id == board.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "Board{" +
                "id=" + id +
                ", pits=" + pits +
                '}';
    }
}