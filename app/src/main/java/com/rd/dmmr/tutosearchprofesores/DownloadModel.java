package com.rd.dmmr.tutosearchprofesores;

public class DownloadModel {

    public String name, linkFile, idDoc, idTuto;


    public DownloadModel(String name, String linkFile, String idDoc, String idTuto) {
        this.name = name;
        this.linkFile = linkFile;
        this.idDoc = idDoc;
        this.idTuto= idTuto;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLinkFile() {
        return linkFile;
    }

    public void setLinkFile(String linkFile) {
        this.linkFile = linkFile;
    }
}
