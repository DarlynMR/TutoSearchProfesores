package com.rd.dmmr.tutosearchprofesores;

public class ModelTutoriasProf {

    String idTuto, titulo, descripcion, broadcastId, fecha, fecha_pub, hora, hora_pub, materia, tipo_tuto, url_image_portada, url_thumb_image_portada, lugar, hora_inicial, hora_final;


    public ModelTutoriasProf() {
    }



    public ModelTutoriasProf(String idTuto, String titulo, String descripcion, String broadcastId, String fecha, String fecha_pub, String hora, String hora_pub, String materia, String tipo_tuto, String url_image_portada, String url_thumb_image_portada, String lugar) {
        this.idTuto = idTuto;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.broadcastId = broadcastId;
        this.fecha = fecha;
        this.fecha_pub = fecha_pub;
        this.hora = hora;
        this.hora_pub = hora_pub;
        this.materia = materia;
        this.tipo_tuto = tipo_tuto;
        this.url_image_portada = url_image_portada;
        this.url_thumb_image_portada = url_thumb_image_portada;
        this.lugar = lugar;
    }


    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getBroadcastId() {
        return broadcastId;
    }

    public void setBroadcastId(String broadcastId) {
        this.broadcastId = broadcastId;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFecha_pub() {
        return fecha_pub;
    }

    public void setFecha_pub(String fecha_pub) {
        this.fecha_pub = fecha_pub;
    }

    public String getHora_pub() {
        return hora_pub;
    }

    public void setHora_pub(String hora_pub) {
        this.hora_pub = hora_pub;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public String getTipo_tuto() {
        return tipo_tuto;
    }

    public void setTipo_tuto(String tipo_tuto) {
        this.tipo_tuto = tipo_tuto;
    }

    public String getUrl_image_portada() {
        return url_image_portada;
    }

    public void setUrl_image_portada(String url_image_portada) {
        this.url_image_portada = url_image_portada;
    }

    public String getUrl_thumb_image_portada() {
        return url_thumb_image_portada;
    }

    public void setUrl_thumb_image_portada(String url_thumb_image_portada) {
        this.url_thumb_image_portada = url_thumb_image_portada;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getHora_inicial() {
        return hora_inicial;
    }

    public void setHora_inicial(String hora_inicial) {
        this.hora_inicial = hora_inicial;
    }

    public String getHora_final() {
        return hora_final;
    }

    public void setHora_final(String hora_final) {
        this.hora_final = hora_final;
    }
}