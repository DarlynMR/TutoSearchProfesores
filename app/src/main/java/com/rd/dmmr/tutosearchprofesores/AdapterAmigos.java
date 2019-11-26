package com.rd.dmmr.tutosearchprofesores;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdapterAmigos extends RecyclerView.Adapter<AdapterAmigos.ViewHolderAmigos> {

    List<ModelSolicitud> listaAmigos;
    private String emisor, estado, tipoUser, idUser;
    private FirebaseFirestore fdb;
    FirebaseUser FUser;
    private ProgressDialog progressDialog;

    public AdapterAmigos(List<ModelSolicitud> listaAmigos) {
        this.listaAmigos = listaAmigos;
    }

    @NonNull
    @Override
    public ViewHolderAmigos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_amigos, null, false);
        return new AdapterAmigos.ViewHolderAmigos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderAmigos holder, int position) {
        ModelSolicitud itemsSolicitud= listaAmigos.get(position);
        fdb = FirebaseFirestore.getInstance();
        FUser = FirebaseAuth.getInstance().getCurrentUser();
        idUser = FUser.getUid();
        progressDialog = new ProgressDialog(holder.itemView.getContext());
        progressDialog.setCancelable(false);

    }

    @Override
    public int getItemCount() {
        return listaAmigos.size();
    }

    public class ViewHolderAmigos extends RecyclerView.ViewHolder {
        ImageView imgCircularUser;
        TextView txtNombre, txtTipoUser;
        Button btnAceptar, btnRechazar;
        int idfila;
        private Context vcontext;

        public ViewHolderAmigos(@NonNull View itemView) {
            super(itemView);

            imgCircularUser = itemView.findViewById(R.id.imgCircularAmigos);
            txtNombre = itemView.findViewById(R.id.txtnamePersonFriend);
            txtTipoUser = itemView.findViewById(R.id.tipoUserFriend);
            vcontext = itemView.getContext();

        }
    }
}
