package com.rd.dmmr.tutosearchprofesores;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;


public class AdapterSolicitud extends RecyclerView.Adapter<AdapterSolicitud.ViewHolderSolicitudes> {

    List<ModelSolicitud> listaSolicitudes;
    private String emisor, estado, tipoUser, idUser;
    private FirebaseFirestore fdb;
    FirebaseUser FUser;
    private ProgressDialog progressDialog;

    public AdapterSolicitud(List<ModelSolicitud> listaSolicitudes) {
        this.listaSolicitudes = listaSolicitudes;
    }

    @NonNull
    @Override
    public ViewHolderSolicitudes onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_solicitudes, null, false);
        return new ViewHolderSolicitudes(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderSolicitudes holder, int position) {
        ModelSolicitud itemsSolicitud= listaSolicitudes.get(position);
        fdb = FirebaseFirestore.getInstance();
        FUser = FirebaseAuth.getInstance().getCurrentUser();
        idUser = FUser.getUid();
        progressDialog = new ProgressDialog(holder.itemView.getContext());
        progressDialog.setCancelable(false);


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

       holder.setOnClickListener(position);

    }

    @Override
    public int getItemCount() {
        return listaSolicitudes.size();
    }

    public class ViewHolderSolicitudes extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imgCircularUser;
        TextView txtNombre, txtTipoUser;
        Button btnAceptar, btnRechazar;
        int idfila;
        private Context vcontext;

        public ViewHolderSolicitudes(@NonNull View itemView) {
            super(itemView);

            imgCircularUser = itemView.findViewById(R.id.imgCircularSolicitud);
            txtNombre = itemView.findViewById(R.id.txtnamePerson);
            txtTipoUser= itemView.findViewById(R.id.tipoUser);
            btnAceptar=   itemView.findViewById(R.id.btnAceptar);
            btnRechazar=   itemView.findViewById(R.id.btnRechazar);
            vcontext = itemView.getContext();

        }

        void setOnClickListener(Integer pos){
            idfila = pos;
            btnAceptar.setOnClickListener(this);
            btnRechazar.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btnAceptar:

                    final String emisor = listaSolicitudes.get(idfila).emisor;
                    final String idSoli = listaSolicitudes.get(idfila).idSolicitud;

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("tipoUser", listaSolicitudes.get(idfila).tipoUser);
                    progressDialog.setMessage("Completando solicitud");
                    progressDialog.show();

                    fdb.collection("Amigos").document(idUser).collection("Aceptados").document(emisor)
                            .set(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    HashMap<String, String> hashMap2 = new HashMap<>();
                                    hashMap2.put("tipoUser","Profesor");

                                    fdb.collection("Amigos").document(emisor).collection("Aceptados").document(idUser)
                                            .set(hashMap2)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    fdb.collection("Solicitudes").document(idUser).collection("Recibidas").document(idSoli)
                                                            .delete()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                    fdb.collection("Solicitudes").document(emisor).collection("Enviadas").document(idSoli)
                                                                            .delete()
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {


                                                                                    progressDialog.dismiss();
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    progressDialog.dismiss();
                                                                                }
                                                                            });

                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    progressDialog.dismiss();
                                                                }
                                                            });

                                                    progressDialog.dismiss();
                                                    Toast.makeText(vcontext, "Solicitud de amistad aceptada", Toast.LENGTH_SHORT).show();

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {



                                            progressDialog.dismiss();
                                        }
                                    });


                                    progressDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {


                            progressDialog.dismiss();
                        }
                    });

                    break;
                case R.id.btnRechazar:
                    progressDialog.setMessage("Rechanzado solicitud");
                    progressDialog.show();
                    fdb.collection("Solicitudes").document(idUser).collection("Recibidas").document(listaSolicitudes.get(idfila).idSolicitud)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    fdb.collection("Solicitudes").document(listaSolicitudes.get(idfila).emisor).collection("Enviadas").document(listaSolicitudes.get(idfila).idSolicitud)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    Toast.makeText(vcontext, "Se ha eliminado la solicitud", Toast.LENGTH_SHORT).show();
                                                    progressDialog.show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(vcontext, "Ocurrió un error al eliminar la solicitud, intente más tarde", Toast.LENGTH_SHORT).show();
                                                    progressDialog.show();
                                                }
                                            });

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(vcontext, "Ocurrió un error al eliminar la solicitud, intente más tarde", Toast.LENGTH_SHORT).show();
                                    progressDialog.show();
                                }
                            });

                    break;


            }
        }
    }
}
