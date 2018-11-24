package com.backbase.kalah.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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

    public Game() {
        id = 0L;
    }

    public Game(long id, Board board, long player1Id, long player2Id) {
        this.id = id;
        this.board = board;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
    }

    @Id
    @GeneratedValue
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Board getBoard() {
        return board;
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
}