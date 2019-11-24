package com.rd.dmmr.tutosearchprofesores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterMaterias extends RecyclerView.Adapter<AdapterMaterias.ViewHolderMaterias> {

    ArrayList<String> listaMaterias;

    public AdapterMaterias(ArrayList<String> listaMaterias) {
        this.listaMaterias = listaMaterias;
    }

    @NonNull
    @Override
    public ViewHolderMaterias onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_materias, null, false);

        return new ViewHolderMaterias(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderMaterias holder, int position) {
        holder.traerMaterias(listaMaterias.get(position));

    }

    @Override
    public int getItemCount() {
        return listaMaterias.size();
    }

    public class ViewHolderMaterias extends RecyclerView.ViewHolder {

        TextView materia;

        public ViewHolderMaterias(@NonNull View itemView) {
            super(itemView);

            materia = itemView.findViewById(R.id.txtMateriaRC);
        }

        public void traerMaterias(String s) {
            materia.setText(s);

        }
    }
}
