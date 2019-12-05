package com.rd.dmmr.tutosearchprofesores;

public class ModelTutoriasProf {

    String idTuto, titulo, descripcion, broadcastId, timestampInicial,timestampFinal, timestampPub, materia, tipo_tuto, url_image_portada, url_thumb_image_portada, lugar;


    public ModelTutoriasProf() {
    }

    public ModelTutoriasProf(String idTuto, String titulo, String descripcion, String broadcastId, String timestampInicial, String timestampFinal, String timestampPub, String materia, String tipo_tuto, String url_image_portada, String url_thumb_image_portada, String lugar) {
        this.idTuto = idTuto;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.broadcastId = broadcastId;
        this.timestampInicial = timestampInicial;
        this.timestampFinal = timestampFinal;
        this.timestampPub = timestampPub;
        this.materia = materia;
        this.tipo_tuto = tipo_tuto;
        this.url_image_portada = url_image_portada;
        this.url_thumb_image_portada = url_thumb_image_portada;
        this.lugar = lugar;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getBroadcastId() {
        return broadcastId;
    }

    public String getTimestampInicial() {
        return timestampInicial;
    }

    public String getTimestampFinal() {
        return timestampFinal;
    }

    public String getTimestampPub() {
        return timestampPub;
    }

    public String getMateria() {
        return materia;
    }

    public String getTipo_tuto() {
        return tipo_tuto;
    }

    public String getUrl_image_portada() {
        return url_image_portada;
    }

    public String getUrl_thumb_image_portada() {
        return url_thumb_image_portada;
    }

    public String getLugar() {
        return lugar;
    }
}