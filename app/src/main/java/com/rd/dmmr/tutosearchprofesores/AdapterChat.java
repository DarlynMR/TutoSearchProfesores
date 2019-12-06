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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.HolderChat> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    Context context;
    List<ModelChat> chatList;
    String imageURL;
    FirebaseUser FUser;

    public AdapterChat(Context context, List<ModelChat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }


    @NonNull
    @Override
    public HolderChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_chat_derecha, parent, false);
            return new HolderChat(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_chat_izquierda, parent, false);
            return new HolderChat(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull HolderChat holder, int position) {

        ModelChat itemModelChat =chatList.get(position);

        String mensaje = itemModelChat.getMensaje();
        String timestamp =itemModelChat.getTimestamp();

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        Log.i("ProbandoChatAdapter", "Probando en adapter: " + mensaje);

        Log.i("DatosAMadar", itemModelChat.emisor + " " + dateTime + " " + mensaje);

        holder.mensajeRC.setText(mensaje);
        holder.timeRC.setText(dateTime);

        if (position == chatList.size() - 1) {
            if (chatList.get(position).Isvisto) {
                holder.vistoRC.setText("Visto");
            } else {
                holder.vistoRC.setText("Enviado");
            }
        } else {
            holder.vistoRC.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemViewType(int position) {
        Log.i("ProbandoChatAdapter", "Probando en adapter type:" + position);
        FUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).emisor.equals(FUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }

    }

    @Override
    public int getItemCount() {
        Log.i("ProbandoChatAdapter", "" + chatList.size());

        return chatList.size();

    }


    class HolderChat extends RecyclerView.ViewHolder {

        ImageView imgUser;
        TextView mensajeRC, timeRC, vistoRC;
        String idmensaje;

        public HolderChat(@NonNull View itemView) {
            super(itemView);

            mensajeRC = itemView.findViewById(R.id.textMensaje);
            timeRC = itemView.findViewById(R.id.tiempo);
            vistoRC = itemView.findViewById(R.id.estado);

        }
    }


}
