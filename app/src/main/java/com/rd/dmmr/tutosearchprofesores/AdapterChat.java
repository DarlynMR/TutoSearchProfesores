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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.HolderChat> {

    private static  final int MSG_TYPE_LEFT= 0;
    private static  final int MSG_TYPE_RIGHT= 1;
    Context context;
    List<ModelChat> chatList;
    String imageURL;
    FirebaseUser FUser;

    public AdapterChat(Context context, List<ModelChat> chatList, String imageURL) {
        this.context = context;
        this.chatList = chatList;
        this.imageURL = imageURL;
    }


    @NonNull
    @Override
    public HolderChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i("ProbandoChat", "Probando en creacion "+ viewType);
        if (viewType==MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.fila_chat_derecha, parent, false);
            return new HolderChat(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.fila_chat_izquierda, parent, false);
            return new HolderChat(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull HolderChat holder, int position) {

        String mensaje = chatList.get(position).getMensaje();
        String timestamp = chatList.get(position).getTimestamp();

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        Log.i("ProbandoChat", "Probando en adapter: "+mensaje);


        holder.mensajeRC.setText(mensaje);
        holder.timeRC.setText(dateTime);
        if (!imageURL.equals("defaultPicUser") || !imageURL.equals("defaultPicProf")) {
            try {
                Glide.with(holder.itemView.getContext())
                        .load(imageURL)
                        .fitCenter()
                        .centerCrop()
                        .into(holder.imgUser);

            } catch (Exception e) {
                Log.i("ErrorImg", "" + e.getMessage());
            }
        }

        if (position==chatList.size()-1){
            if (chatList.get(position).Isvisto){
                holder.vistoRC.setText("Visto");
            }else {
                holder.vistoRC.setText("Enviado");
            }
        }else {
            holder.vistoRC.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemViewType(int position) {
        Log.i("ProbandoChat", "Probando en adapter:"+position);
        FUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).emisor.equals(FUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }



    class HolderChat extends RecyclerView.ViewHolder {

        ImageView imgUser;
        TextView mensajeRC, timeRC, vistoRC;
        String idmensaje;

        public HolderChat(@NonNull View itemView) {
            super(itemView);

            imgUser= itemView.findViewById(R.id.imgCircularUserProfile);
            mensajeRC= itemView.findViewById(R.id.textMensaje);
            timeRC= itemView.findViewById(R.id.tiempo);
            vistoRC= itemView.findViewById(R.id.estado);

        }
    }


}
