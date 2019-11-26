package com.rd.dmmr.tutosearchprofesores;

public class ModelAmigos {

    String idAmigo, tipoUser;

    public ModelAmigos() {
    }


    public ModelAmigos(String idAmigo, String tipoUser) {
        this.idAmigo = idAmigo;
        this.tipoUser = tipoUser;
    }

    public String getIdAmigo() {
        return idAmigo;
    }

    public String getTipoUser() {
        return tipoUser;
    }
}
