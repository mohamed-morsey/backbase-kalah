package com.backbase.kalah.model;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

/**
 * Represents a Kalah game
 *
 * @author Mohamed Morsey
 * Date: 2018-11-24
 **/
@Entity
public class Game {
    private long id;

    @NotNull
    private Board board;

    @NotNull
    private String uri;

    public Game() {
        id = 0L;
        board = new Board();
        uri = StringUtils.EMPTY;
    }

    public Game(Board board, String uri) {
        id = 0L;
        this.board = board;
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
                ", uri='" + uri + '\'' +
                '}';
    }
}