package com.backbase.kalah.dto;

import com.backbase.kalah.model.Game;

import java.util.Map;

/**
 * View object for {@link Game} that represents a full game with board status
 *
 * @author Mohamed Morsey
 * Date: 2018-11-25
 **/
public class GameStatusDto {
    private long id;
    private String uri;
    private Map<String, String> status;

    public GameStatusDto() {
        id = 0L;
    }

    public GameStatusDto(long id, String uri, Map<String, String> status) {
        this.id = id;
        this.uri = uri;
        this.status = status;
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

    public Map<String, String> getStatus() {
        return status;
    }

    public void setStatus(Map<String, String> status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "GameStatusDto{" +
                "id=" + id +
                ", uri='" + uri + '\'' +
                ", status=" + status +
                '}';
    }
}
