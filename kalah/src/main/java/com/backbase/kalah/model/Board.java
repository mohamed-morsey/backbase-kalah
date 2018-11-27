package com.backbase.kalah.model;

import com.backbase.kalah.model.enums.PlayerTurn;
import com.backbase.kalah.model.enums.Status;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static com.backbase.kalah.model.enums.PlayerTurn.FIRST_PLAYER;
import static com.backbase.kalah.model.enums.Status.RUNNING;

/**
 * Represents a Kalah game board
 *
 * @author Mohamed Morsey
 * Date: 2018-11-24
 **/
@Entity
public class Board {
    private long id;

    @NotNull
    private PlayerTurn playerTurn;

    @NotNull
    private Status status;

    @NotNull
    private List<Pit> pits;

    public Board() {
        id = 0L;
        playerTurn = FIRST_PLAYER;
        pits = new ArrayList<>();
        status = RUNNING;
    }

    public Board(List<Pit> pits) {
        this();
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

    public PlayerTurn getPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(PlayerTurn playerTurn) {
        this.playerTurn = playerTurn;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
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
                ", playerTurn=" + playerTurn +
                ", status=" + status +
                ", pits=" + pits +
                '}';
    }
}