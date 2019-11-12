package com.rd.dmmr.tutosearchprofesores;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Owner on 10/3/2018.
 */

public class TutoriasAdapter extends RecyclerView.Adapter<TutoriasAdapter.ViewPos> {



    private List<ModelTutoriasProf> mList;


    public TutoriasAdapter(List<ModelTutoriasProf> mList) {
        this.mList = mList;
    }

    @Override
    public ViewPos onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewPos(LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_ver_tutorias,parent,false));

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final ViewPos holder, final int position) {

        ModelTutoriasProf itemTutoria= mList.get(position);
        Log.i("Probando lista",mList.get(position).toString()+" Otra: "+itemTutoria.hora);

        holder.item_txtMateriaPrev.setText(itemTutoria.materia);
        holder.item_txtTituloPrev.setText(itemTutoria.titulo);
        holder.item_txtDescripcionPrev.setText(itemTutoria.descripcion);
        holder.item_txtFechaPrev.setText("Fecha: "+itemTutoria.fecha);
        holder.item_txtHoraPrev.setText("Hora: "+itemTutoria.hora);
        if (!itemTutoria.lugar.equals("")) {
            holder.item_txtLugarPrev.setText("Lugar: " + itemTutoria.lugar);
        }else {
            holder.item_txtLugarPrev.setText("");
        }

        if (itemTutoria.url_image_portada.equals("defaultPresencial")){
            holder.item_imgFotoPrev.setImageResource(R.mipmap.presencial_background);
        } else if(itemTutoria.url_image_portada.equals("defaultLive")){
            holder.item_imgFotoPrev.setImageResource(R.drawable.video_camera_live);

        } else {
            try {
                Glide.with(holder.itemView.getContext())
                        .load(itemTutoria.url_image_portada)
                        .fitCenter()
                        .centerCrop()
                        .into(holder.item_imgFotoPrev);

            }catch (Exception e){
                Log.i("Error", ""+e.getMessage());
            }

        }

        if (itemTutoria.tipo_tuto.equals("Live")){
            holder.item_imgType.setImageResource(R.drawable.ic_iconfinder_facebook_live_icon_1083837);

        }else if (itemTutoria.tipo_tuto.equals("Presencial")){
            holder.item_imgType.setImageResource(R.drawable.ic_iconfinder_dicument_4115232);
        }



        holder.setOnClickListener(position);


        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(holder.getAdapterPosition(), 0, 0,"Ver más detalles");
                contextMenu.add(holder.getAdapterPosition(), 1, 0,"Asistir");
            }
        });




    }

    @Override
    public int getItemCount() {

        return mList.size();
    }

    public class  ViewPos extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView item_imgFotoPrev, item_imgType;
        TextView item_txtMateriaPrev, item_txtTituloPrev, item_txtDescripcionPrev, item_txtFechaPrev, item_txtHoraPrev, item_txtLugarPrev;
        CardView cardView;
        private Context vcontext;
        Integer idfila;


        public ViewPos(View itemView) {
            super(itemView);
            vcontext = itemView.getContext();
            item_imgFotoPrev= itemView.findViewById(R.id.imgFotoPrev);
            item_txtMateriaPrev=itemView.findViewById(R.id.txtMateria);
            item_txtTituloPrev= itemView.findViewById(R.id.txtTitulo);
            item_txtDescripcionPrev=itemView.findViewById(R.id.txtDescripcion);
            item_txtFechaPrev=itemView.findViewById(R.id.txtFechaTutorias);
            item_txtHoraPrev= itemView.findViewById(R.id.txtHoraTutorias);
            item_txtLugarPrev=itemView.findViewById(R.id.txtLugarTurorias);
            item_imgType=itemView.findViewById(R.id.imgType);
            cardView= itemView.findViewById(R.id.RCView);

        }
        void setOnClickListener(Integer pos){
            idfila = pos;
            cardView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.RCView:
                    Class clase;

                    if (mList.get(idfila).tipo_tuto.equals("Live")){
                        clase = TransmisionActivity2.class;
                    }else {
                        clase = DetallesTutorias.class;
                    }

                    Intent detalles = new Intent(vcontext, clase);
                    detalles.putExtra("idTuto",mList.get(idfila).idTuto);
                    Log.i("Prueba",mList.get(idfila).idTuto+" "+idfila);
                    detalles.putExtra("Materia",mList.get(idfila).materia);
                    detalles.putExtra("imgTuto",mList.get(idfila).url_image_portada);
                    detalles.putExtra("Titulo", mList.get(idfila).titulo);
                    detalles.putExtra("Descripcion",mList.get(idfila).descripcion);
                    detalles.putExtra("Fecha",mList.get(idfila).fecha);
                    detalles.putExtra("Hora",mList.get(idfila).hora);
                    detalles.putExtra("Lugar",mList.get(idfila).lugar);
                    detalles.putExtra("TipoEs",mList.get(idfila).tipo_tuto);



                    vcontext.startActivity(detalles);
                    break;
            }
        }
    }
}
