package com.rd.dmmr.tutosearchprofesores;

public class ModelListado {

    String idEstudiante, timestamp;

    public ModelListado() {
    }

    public ModelListado(String idEstudiante, String timestamp) {
        this.idEstudiante = idEstudiante;
        this.timestamp = timestamp;
    }

    public String getIdEstudiante() {
        return idEstudiante;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
