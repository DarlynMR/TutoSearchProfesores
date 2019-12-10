package com.rd.dmmr.tutosearchprofesores;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.List;

public class AdapterChatList extends  RecyclerView.Adapter<AdapterChatList.HolderChatList>{

    Context context;
    List<ModelChatList> modelChatLists;
    private HashMap<String, String> ultimoMensaje;

    private FirebaseFirestore fdb;
    FirebaseUser FUser;
    private ProgressDialog progressDialog;

    public AdapterChatList() {
    }

    public AdapterChatList(Context context, List<ModelChatList> modelChatLists) {
        this.context = context;
        this.modelChatLists = modelChatLists;
        ultimoMensaje = new HashMap<>();
    }

    @NonNull
    @Override
    public HolderChatList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.rc_chat_list, parent, false);


        return new HolderChatList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderChatList holder, int position) {
        String id, tipoU, ultimoMsg;
        fdb = FirebaseFirestore.getInstance();
        FUser = FirebaseAuth.getInstance().getCurrentUser();


        id = modelChatLists.get(position).id;
        tipoU= modelChatLists.get(position).getTipoUser();
        ultimoMsg = ultimoMensaje.get(id);

        if (ultimoMsg!=null) {
            holder.txtUltMsg.setText(ultimoMsg);
        }

        DocumentReference docRef = fdb.collection(tipoU).document(id);
        /*
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot docS = task.getResult();

                String estad = docS.getString("estadoOnline");
                holder.txtNombre.setText(docS.getString("nombres")+" "+docS.getString("apellidos"));

                if (estad.equals("En linea")){
                    holder.imgEstado.setImageResource(R.drawable.circulo_online);
                }else {
                    holder.imgEstado.setImageResource(R.drawable.circulo_offline);
                }

                String img = docS.getString("url_thumb_pic");
                if (!img.equals("defaultPicUser") || !img.equals("defaultPicProf")) {
                    try {
                        Glide.with(holder.itemView.getContext())
                                .load(img)
                                .fitCenter()
                                .centerCrop()
                                .into(holder.imgProfile);

                    } catch (Exception e) {
                        Log.i("ErrorImg", "" + e.getMessage());
                    }
                }
            }
        });
        */
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException Fe) {
                String estad = documentSnapshot.getString("estadoOnline");
                String nom = documentSnapshot.getString("nombres")+" "+documentSnapshot.getString("apellidos");

                if (!holder.txtNombre.getText().toString().equals(nom)) {
                    holder.txtNombre.setText(nom);
                }

                if (estad.equals("En linea")){
                    holder.imgEstado.setImageResource(R.drawable.circulo_online);
                }else {
                    holder.imgEstado.setImageResource(R.drawable.circulo_offline);
                }

                String img = documentSnapshot.getString("url_thumb_pic");
               // if (!documentSnapshot.getString("url_thumb_pic").equals(img)){
                    if (!img.equals("defaultPicUser") || !img.equals("defaultPicProf")) {
                        try {
                            Glide.with(holder.itemView.getContext())
                                    .load(img)
                                    .fitCenter()
                                    .centerCrop()
                                    .into(holder.imgProfile);

                        } catch (Exception e) {
                            Log.i("ErrorImg", "" + e.getMessage());
                        }
                   // }
                }



            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent detalles = new Intent(context, ChatPriv.class);
                detalles.putExtra("idAmigo",modelChatLists.get(position).id);
                detalles.putExtra("tipoUser",modelChatLists.get(position).tipoUser);
                context.startActivity(detalles);
            }
        });


    }

    public void setUltimoMensaje(String UID, String ultimoMsg){
        ultimoMensaje.put(UID, ultimoMsg);
    }

    @Override
    public int getItemCount() {
        return modelChatLists.size();
    }

    class HolderChatList extends RecyclerView.ViewHolder{

        ImageView imgProfile, imgEstado;
        TextView txtNombre, txtUltMsg;

        public HolderChatList(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.imgCircularAmigos);
            imgEstado = itemView.findViewById(R.id.imgEstado);
            txtNombre = itemView.findViewById(R.id.txtnamePersonFriend);
            txtUltMsg = itemView.findViewById(R.id.ultMensaje);
        }
    }


}
