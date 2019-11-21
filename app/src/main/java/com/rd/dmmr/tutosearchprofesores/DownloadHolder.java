package com.rd.dmmr.tutosearchprofesores;

import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class DownloadHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{

    TextView filename, filesize;
    ImageView imgType;
    Button btnDescargar;
    ProgressBar progressBar;
    CardView cardView;


    public DownloadHolder(@NonNull View itemView) {
        super(itemView);

        filename = itemView.findViewById(R.id.txtFilename);
        filesize = itemView.findViewById(R.id.txtFilesize);
        imgType = itemView.findViewById(R.id.imgType);
        btnDescargar = itemView.findViewById(R.id.btnDescargar);
        progressBar = itemView.findViewById(R.id.progresBar);
        cardView = itemView.findViewById(R.id.card__Archivo);
        cardView.setOnCreateContextMenuListener(this);


    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Acción");
        contextMenu.add(this.getAdapterPosition(), 0, 0, "Borrar archivo");
    }
}
