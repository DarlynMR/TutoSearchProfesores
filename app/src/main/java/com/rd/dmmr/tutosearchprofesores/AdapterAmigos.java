package com.rd.dmmr.tutosearchprofesores;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdapterAmigos extends RecyclerView.Adapter<AdapterAmigos.ViewHolderAmigos> {

    List<ModelAmigos> listaAmigos;
    private String emisor, estado, tipoUser, idUser;
    private FirebaseFirestore fdb;
    FirebaseUser FUser;
    private ProgressDialog progressDialog;

    public AdapterAmigos(List<ModelAmigos> listaAmigos) {
        this.listaAmigos = listaAmigos;
    }

    @NonNull
    @Override
    public ViewHolderAmigos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_amigos, null, false);
        return new AdapterAmigos.ViewHolderAmigos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderAmigos holder, int position) {
        ModelAmigos itemsAmigos =listaAmigos.get(position);
        fdb = FirebaseFirestore.getInstance();
        FUser = FirebaseAuth.getInstance().getCurrentUser();
        idUser = FUser.getUid();
        progressDialog = new ProgressDialog(holder.itemView.getContext());
        progressDialog.setCancelable(false);
        String userRuta="";
        tipoUser = itemsAmigos.getTipoUser();

        if (itemsAmigos.getTipoUser().equals("Profesor")){
            userRuta = "Profesores";
        }else if (itemsAmigos.getTipoUser().equals("Estudiante")){
            userRuta= "Estudiantes";
        }

        final DocumentReference docRef = fdb.collection(userRuta).document(itemsAmigos.idAmigo);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot docS = task.getResult();
                String nombre,img;
                img = docS.getString("url_thumb_pic");
                nombre= docS.getString("nombres")+" "+docS.getString("apellidos");
                holder.txtNombre.setText(nombre);
                holder.txtTipoUser.setText(tipoUser);

                if (!img.equals("defaultPicUser") && !img.equals("defaultPicProf")) {
                    try {
                        Glide.with(holder.itemView.getContext())
                                .load(img)
                                .fitCenter()
                                .centerCrop()
                                .into(holder.imgCircularUser);

                    } catch (Exception e) {
                        Log.i("ErrorImg", "" + e.getMessage());
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        holder.setOnClickListener(position);

    }

    @Override
    public int getItemCount() {
        return listaAmigos.size();
    }

    public class ViewHolderAmigos extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgCircularUser;
        TextView txtNombre, txtTipoUser;
        Button btnAceptar, btnRechazar;
        int idfila;
        private Context vcontext;
        CardView cardView;

        public ViewHolderAmigos(@NonNull View itemView) {
            super(itemView);

            imgCircularUser = itemView.findViewById(R.id.imgCircularAmigos);
            txtNombre = itemView.findViewById(R.id.txtnamePersonFriend);
            txtTipoUser = itemView.findViewById(R.id.tipoUserFriend);
            cardView = itemView.findViewById(R.id.card_ContenidoAmigos);
            vcontext = itemView.getContext();

        }

        void setOnClickListener(Integer pos){
            idfila = pos;
            cardView.setOnClickListener(this);

        }
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.card_ContenidoAmigos:

                    Intent detalles = new Intent(vcontext, ChatPriv.class);
                    detalles.putExtra("idAmigo",listaAmigos.get(idfila).idAmigo);
                    detalles.putExtra("tipoUser",listaAmigos.get(idfila).tipoUser);
                    vcontext.startActivity(detalles);


                break;
            }

        }
    }
}
