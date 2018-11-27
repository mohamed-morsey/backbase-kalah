package com.backbase.kalah.dto;

import com.backbase.kalah.model.Game;
import org.apache.commons.lang3.StringUtils;

/**
 * View object for {@link Game} that represents a just-created game
 *
 * @author Mohamed Morsey
 * Date: 2018-11-25
 **/
public class GameDto {
    private long id;
    private String uri;

    public GameDto() {
        this.id = 0L;
        this.uri = StringUtils.EMPTY;
    }

    public GameDto(long id, String uri) {
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
        return "GameDto{" +
                "id=" + id +
                ", uri='" + uri + '\'' +
                '}';
    }
}
