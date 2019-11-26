package com.rd.dmmr.tutosearchprofesores;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterChat {


    class HolderChat extends RecyclerView.ViewHolder {

        ImageView imgUser;
        TextView mensajeRC, timeRC, vistoRC;
        String idmensaje;

        public HolderChat(@NonNull View itemView) {
            super(itemView);

            imgUser= itemView.findViewById(R.id.imgCircularChat);
            mensajeRC= itemView.findViewById(R.id.imgCircularChat);
            timeRC= itemView.findViewById(R.id.imgCircularChat);
            vistoRC= itemView.findViewById(R.id.imgCircularChat);

        }
    }


}
