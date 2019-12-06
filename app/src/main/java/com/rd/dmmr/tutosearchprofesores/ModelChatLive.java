package com.rd.dmmr.tutosearchprofesores;

public class ModelChatLive {

    String idMensaje, mensaje, emisor,timestamp, tipo_user;

    public ModelChatLive() {
    }


    public ModelChatLive(String idMensaje, String mensaje, String emisor, String timestamp, String tipo_user) {
        this.idMensaje = idMensaje;
        this.mensaje = mensaje;
        this.emisor = emisor;
        this.timestamp = timestamp;
        this.tipo_user = tipo_user;
    }

    public String getIdMensaje() {
        return idMensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getEmisor() {
        return emisor;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTipo_user() {
        return tipo_user;
    }
}
