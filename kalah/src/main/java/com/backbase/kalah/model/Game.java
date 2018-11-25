package com.backbase.kalah.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import static com.backbase.kalah.model.Status.RUNNING;

/**
 * Represents a Kalah game
 *
 * @author Mohamed Morsey
 * Date: 2018-11-24
 **/
@Entity
public class Game {
    private long id;
    private Board board;
    private long player1Id;
    private long player2Id;
    private Status status;

    public Game() {
        id = 0L;
        status = RUNNING;
    }

    public Game(long id, Board board, long player1Id, long player2Id) {
        this.id = id;
        this.board = board;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.status = RUNNING;
    }

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @OneToOne(cascade = CascadeType.ALL)
    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public long getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(long player1Id) {
        this.player1Id = player1Id;
    }

    public long getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(long player2Id) {
        this.player2Id = player2Id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}