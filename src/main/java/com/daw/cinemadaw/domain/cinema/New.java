package com.daw.cinemadaw.domain.cinema;

public class New {
    private String headline; // Titular de la notícia
    private String body; // Contingut de la notícia

    public New(String headline, String body) {
        this.headline = headline;
        this.body = body;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }
    
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return headline + ": " + body;
    }
}
