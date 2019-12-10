package com.rd.dmmr.tutosearchprofesores;

public class ModelChatList {

    String id,tipoUser;

    public ModelChatList() {
    }

    public ModelChatList(String id, String tipoUser) {
        this.id = id;
        this.tipoUser = tipoUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipoUser() {
        return tipoUser;
    }

    public void setTipoUser(String tipoUser) {
        this.tipoUser = tipoUser;
    }
}
