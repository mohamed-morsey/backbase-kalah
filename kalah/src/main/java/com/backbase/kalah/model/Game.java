package com.backbase.kalah.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

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
    private String uri;

    public Game() {
        id = 0L;
    }

    public Game(Board board, long player1Id, long player2Id, String uri) {
        this();
        this.board = board;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.uri = uri;
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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Game)) {
            return false;
        }

        Game game = (Game) o;

        return id == game.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", board=" + board +
                ", player1Id=" + player1Id +
                ", player2Id=" + player2Id +
                ", uri='" + uri + '\'' +
                '}';
    }
}