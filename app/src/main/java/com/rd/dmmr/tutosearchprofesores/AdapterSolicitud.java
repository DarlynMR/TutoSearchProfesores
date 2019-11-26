package com.rd.dmmr.tutosearchprofesores;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class AdapterSolicitud extends RecyclerView.Adapter<AdapterSolicitud.ViewHolderSolicitudes> {

    List<ModelSolicitud> listaSolicitudes;
    private String emisor, estado, tipoUser;
    private FirebaseFirestore fdb;

    public AdapterSolicitud(List<ModelSolicitud> listaSolicitudes) {
        this.listaSolicitudes = listaSolicitudes;
    }

    @NonNull
    @Override
    public ViewHolderSolicitudes onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_solicitudes, null, false);
        Log.i("ProbandoAdapterSoli", "esto es creando");
        return new ViewHolderSolicitudes(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderSolicitudes holder, int position) {
        ModelSolicitud itemsSolicitud= listaSolicitudes.get(position);
        fdb = FirebaseFirestore.getInstance();


        emisor = itemsSolicitud.getEmisor();
        estado = itemsSolicitud.getEstado();
        tipoUser = itemsSolicitud.getTipouser();

        Log.i("ProbandoAdapterSoli", "esto es: "+emisor+ " "+estado+ " "+tipoUser);

        if (tipoUser.equals("Estudiante")){
            final DocumentReference docRef = fdb.collection("Estudiantes").document(emisor);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot docS = task.getResult();
                    String nombre,img;
                    img = docS.getString("url_thumb_pic");
                    nombre= docS.getString("nombres")+" "+docS.getString("apellidos");
                    holder.txtNombre.setText(nombre);
                    holder.txtTipoUser.setText(tipoUser);

                    if (!img.equals("defaultPicUser")) {
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
        }

    }

    @Override
    public int getItemCount() {
        return listaSolicitudes.size();
    }

    public class ViewHolderSolicitudes extends RecyclerView.ViewHolder {

        ImageView imgCircularUser;
        TextView txtNombre, txtTipoUser;

        public ViewHolderSolicitudes(@NonNull View itemView) {
            super(itemView);

            imgCircularUser = itemView.findViewById(R.id.imgCircularSolicitud);
            txtNombre = itemView.findViewById(R.id.txtnamePerson);
            txtTipoUser= itemView.findViewById(R.id.tipoUser);
        }
    }
}
