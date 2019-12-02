package com.rd.dmmr.tutosearchprofesores;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterListado extends RecyclerView.Adapter<AdapterListado.ViewHolderListado> {

    List<ModelListado> mListListado;
    private String idEstudiante, timestamp;
    private FirebaseFirestore fdb;
    private FirebaseUser fuser;

    public AdapterListado(List<ModelListado> mListListado) {
        this.mListListado = mListListado;
    }

    @NonNull
    @Override
    public ViewHolderListado onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_listado, null, false);
        return new ViewHolderListado(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderListado holder, int position) {
        final String idEstu, timestamp, nombre;

        idEstu= mListListado.get(position).getIdEstudiante();
        timestamp= mListListado.get(position).getTimestamp();

        DocumentReference docRef = fdb.collection("Estudiantes").document(idEstu);
                docRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot docS = task.getResult();
                            String urlpic;

                            holder.txtNombre.setText(docS.getString("nombres")+ " " +docS.getString("apellidos"));
                            urlpic = docS.getString("url_thumb_pic");
                            if (!urlpic.equals("defaultPicUser")) {
                                try {
                                    Glide.with(holder.itemView.getContext())
                                            .load(urlpic)
                                            .fitCenter()
                                            .centerCrop()
                                            .into(holder.imgEst);

                                } catch (Exception e) {
                                    Log.i("ErrorImg", "" + e.getMessage());
                                }
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });



    }

    @Override
    public int getItemCount() {
        return mListListado.size();
    }

    public class ViewHolderListado extends RecyclerView.ViewHolder {


        private ImageView imgEst;
        private TextView txtNombre, txtTimestamp;
        int idFila;
        private Context vcontext;

        public ViewHolderListado(@NonNull View itemView) {
            super(itemView);

            imgEst = itemView.findViewById(R.id.imgEstListado);
            txtNombre = itemView.findViewById(R.id.txtNombreEstudiante);
            txtTimestamp = itemView.findViewById(R.id.txtTiempo);




        }
    }
}
