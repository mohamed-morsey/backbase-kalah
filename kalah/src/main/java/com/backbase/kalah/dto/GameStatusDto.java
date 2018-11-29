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
    private String url;
    private Map<String, String> status;

    public GameStatusDto() {
        id = 0L;
    }

    public GameStatusDto(long id, String url, Map<String, String> status) {
        this.id = id;
        this.url = url;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUri(String url) {
        this.url = url;
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
                ", url='" + url + '\'' +
                ", status=" + status +
                '}';
    }
}
