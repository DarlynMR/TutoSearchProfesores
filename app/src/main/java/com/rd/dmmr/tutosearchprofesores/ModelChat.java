package com.rd.dmmr.tutosearchprofesores;

public class ModelChat {

    String idMensaje, mensaje, emisor, receptor, timestamp;
    boolean visto;

    public ModelChat() {
    }

    public ModelChat(String idMensaje, String mensaje, String emisor, String receptor, String timestamp, boolean visto) {
        this.idMensaje = idMensaje;
        this.mensaje = mensaje;
        this.emisor = emisor;
        this.receptor = receptor;
        this.timestamp = timestamp;
        this.visto = visto;
    }

    public String getIdMensaje() {
        return idMensaje;
    }

    public void setIdMensaje(String idMensaje) {
        this.idMensaje = idMensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    public String getReceptor() {
        return receptor;
    }

    public void setReceptor(String receptor) {
        this.receptor = receptor;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isVisto() {
        return visto;
    }

    public void setVisto(boolean visto) {
        this.visto = visto;
    }
}
