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
    private String id;
    private String uri;

    public GameDto() {
        this.id = "0";
        this.uri = StringUtils.EMPTY;
    }

    public GameDto(String id, String uri) {
        this.id = id;
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
