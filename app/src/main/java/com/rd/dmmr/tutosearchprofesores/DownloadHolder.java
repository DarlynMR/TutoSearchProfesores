package com.rd.dmmr.tutosearchprofesores;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DownloadHolder extends RecyclerView.ViewHolder {

    TextView filename, filesize;
    ImageView imgType;
    Button btnDescargar;
    ProgressBar progressBar;


    public DownloadHolder(@NonNull View itemView) {
        super(itemView);

        filename = itemView.findViewById(R.id.txtFilename);
        filesize = itemView.findViewById(R.id.txtFilesize);
        imgType = itemView.findViewById(R.id.imgType);
        btnDescargar = itemView.findViewById(R.id.btnDescargar);
        progressBar = itemView.findViewById(R.id.progresBar);


    }
}
