package com.rd.dmmr.tutosearchprofesores;

public class DownloadModel {

    String name, linkFile, idDoc;


    public DownloadModel(String name, String linkFile, String idDoc) {
        this.name = name;
        this.linkFile = linkFile;
        this.idDoc = idDoc;
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
