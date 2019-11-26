package com.rd.dmmr.tutosearchprofesores;

public class ModelSolicitud {


    String idSolicitud, emisor, estado, tipoUser;

    public ModelSolicitud() {
    }

    public ModelSolicitud(String idSolicitud, String emisor, String estado, String tipoUser) {
        this.idSolicitud = idSolicitud;
        this.emisor = emisor;
        this.estado = estado;
        this.tipoUser = tipoUser;
    }

    public String getIdSolicitud() {
        return idSolicitud;
    }

    public String getEmisor() {
        return emisor;
    }

    public String getEstado() {
        return estado;
    }

    public String getTipouser() {
        return tipoUser;
    }
}
