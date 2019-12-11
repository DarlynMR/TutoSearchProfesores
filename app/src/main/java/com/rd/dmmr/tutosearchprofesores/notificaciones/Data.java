package com.rd.dmmr.tutosearchprofesores.notificaciones;

public class Data {


    private String user, body, tittle, sent, tipoUser;
    private Integer icon;


    public Data() {

    }

    public Data(String user, String body, String tittle, String sent, Integer icon, String tipoUser) {
        this.user = user;
        this.body = body;
        this.tittle = tittle;
        this.sent = sent;
        this.icon = icon;
        this.tipoUser=tipoUser;
    }

    public String getUser() {
        return user;
    }

    public String getTipoUser() {
        return tipoUser;
    }

    public void setTipoUser(String tipoUser) {
        this.tipoUser = tipoUser;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getSent() {
        return sent;
    }

    public void setSent(String sent) {
        this.sent = sent;
    }

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }
}
