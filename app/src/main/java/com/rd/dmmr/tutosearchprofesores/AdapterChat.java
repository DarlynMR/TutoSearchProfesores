package com.rd.dmmr.tutosearchprofesores;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.HolderChat> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    Context context;
    List<ModelChat> chatList;
    String imageURL;
    FirebaseUser FUser;
    FirebaseFirestore fdb;

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
    public void onBindViewHolder(@NonNull HolderChat holder, final int position) {

        ModelChat itemModelChat =chatList.get(position);
        fdb= FirebaseFirestore.getInstance();

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

        holder.msgLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("ProbandoChat", "Entra al click");
                if (chatList.get(position).getEmisor().equals(FUser.getUid())) {
                    Log.i("ProbandoChat", "Entra a la condicion del click");
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Borrar");
                    builder.setMessage("¿Está seguro de que desea eliminar este mensaje?");

                    builder.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            borrarMensaje(position);

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.create().show();
                }

            }
        });

    }

    private void borrarMensaje(int pos) {

        HashMap<String, Object> hashMap= new HashMap<>();

        hashMap.put("mensaje", "Este mensaje ha sido eliminado.");
        hashMap.put("msgEliminado", chatList.get(pos).getMensaje());


        fdb.collection("Mensajes").document(chatList.get(pos).idMensaje)
                .update(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context, "Se ha borrado el mensaje", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Ha ocurrido un error al tratar de borrar el mensaje", Toast.LENGTH_SHORT).show();
            }
        });

/*
        fdb.collection("Mensajes").document(chatList.get(pos).idMensaje)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Se ha borrado el mensaje", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Ha ocurrido un error al tratar de borrar el mensaje", Toast.LENGTH_SHORT).show();
            }
        });
*/
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
        LinearLayout msgLy;

        public HolderChat(@NonNull View itemView) {
            super(itemView);

            mensajeRC = itemView.findViewById(R.id.textMensaje);
            timeRC = itemView.findViewById(R.id.tiempo);
            vistoRC = itemView.findViewById(R.id.estado);
            msgLy = itemView.findViewById(R.id.msgLy);

        }
    }


}
