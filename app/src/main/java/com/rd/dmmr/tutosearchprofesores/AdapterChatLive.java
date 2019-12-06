package com.rd.dmmr.tutosearchprofesores;

import android.content.Context;
import android.text.format.DateFormat;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class AdapterChatLive extends RecyclerView.Adapter<AdapterChatLive.HolderChatLive> {

    private static  final int MSG_TYPE_EST= 0;
    private static  final int MSG_TYPE_PROF= 1;

    Context context;
    List<ModelChatLive> chatList;
    FirebaseUser FUser;
    private FirebaseFirestore fdb;

    public AdapterChatLive(Context context, List<ModelChatLive> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public HolderChatLive onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==MSG_TYPE_EST){
            View view = LayoutInflater.from(context).inflate(R.layout.fila_chat_live_estudiante, parent, false);
            return new AdapterChatLive.HolderChatLive(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.fila_chat_live_profesor, parent, false);
            return new AdapterChatLive.HolderChatLive(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final HolderChatLive holder, int position) {

        fdb = FirebaseFirestore.getInstance();
        String mensaje = chatList.get(position).getMensaje();
        String timestamp = chatList.get(position).getTimestamp();


        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        Log.i("ProbandoChat", "Probando en adapter: "+mensaje);


        final DocumentReference docRef = fdb.collection(chatList.get(position).tipo_user).document(chatList.get(position).emisor);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot docS = task.getResult();
                String nombre,img;
                img = docS.getString("url_thumb_pic");
                nombre= docS.getString("nombres")+" "+docS.getString("apellidos");
                holder.txtnombre.setText(nombre);

                if (!img.equals("defaultPicUser") || !img.equals("defaultPicProf")) {
                    try {
                        Glide.with(holder.itemView.getContext())
                                .load(img)
                                .fitCenter()
                                .centerCrop()
                                .into(holder.imgUser);

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

        holder.txtmensajeRC.setText(mensaje);
        holder.timeRC.setText(dateTime);



    }


    @Override
    public int getItemViewType(int position) {

        FUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).tipo_user.equals("Estudiantes")){
            return MSG_TYPE_EST;
        }
        else {
            return MSG_TYPE_PROF;
        }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class HolderChatLive extends RecyclerView.ViewHolder {

        ImageView imgUser;
        TextView txtnombre, txtmensajeRC, timeRC;
        String idmensaje;

        public HolderChatLive(@NonNull View itemView) {
            super(itemView);

            imgUser= itemView.findViewById(R.id.imgCircularUserProfileLive);
            txtnombre= itemView.findViewById(R.id.textNombreUser);
            txtmensajeRC= itemView.findViewById(R.id.textMensajeLive);
            timeRC= itemView.findViewById(R.id.tiempoLive);

        }
    }


}
