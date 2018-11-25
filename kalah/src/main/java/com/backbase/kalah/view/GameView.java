package com.backbase.kalah.view;

import com.backbase.kalah.model.Game;

/**
 * View object for {@link Game}
 *
 * @author Mohamed Morsey
 * Date: 2018-11-25
 **/
public class GameView {
    private long id;
    private String uri;

    public GameView(long id, String uri) {
        this.id = id;
        this.uri = uri;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        return "GameView{" +
                "id=" + id +
                ", uri='" + uri + '\'' +
                '}';
    }
}
