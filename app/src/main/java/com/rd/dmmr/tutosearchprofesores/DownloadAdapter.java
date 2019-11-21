package com.rd.dmmr.tutosearchprofesores;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadHolder> {

    TransmisionActivity2 transmisionActivity2;
    List<DownloadModel> mListDownload;
    String linkDown,idTuto;


    public DownloadAdapter(TransmisionActivity2 transmisionActivity2, ArrayList<DownloadModel> downloadModels) {
        this.transmisionActivity2 = transmisionActivity2;
        this.mListDownload = downloadModels;
    }

    public DownloadAdapter(List<DownloadModel> mListDown) {
        this.mListDownload = mListDown;
    }

    @NonNull
    @Override
    public DownloadHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        return new DownloadHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_archivo, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final DownloadHolder holder, final int position) {


        String extension;
        holder.filename.setText(mListDownload.get(position).getName());
        linkDown = mListDownload.get(position).getLinkFile();
        idTuto = mListDownload.get(position).idTuto;
        final String ruta =  Environment.getExternalStorageDirectory() +  "/Tutosearch/Documentos/" + idTuto;

        Log.i("ProbandoDown", ""+mListDownload.get(position).getLinkFile());

        final File file = new File(ruta);
        final File fileC = new File(ruta+"/"+mListDownload.get(position).getName());
        extension = FilenameUtils.getExtension(mListDownload.get(position).getName());

        if (extension.equals("png")){
            holder.imgType.setImageResource(R.drawable.ic_png);
        } else if (extension.equals("jpg")){
            holder.imgType.setImageResource(R.drawable.ic_jpg);
        } else if (extension.equals("jpeg")){
            holder.imgType.setImageResource(R.drawable.ic_jpeg);
        } else if (extension.equals("txt")){
            holder.imgType.setImageResource(R.drawable.ic_txt);
        } else if (extension.equals("doc")){
            holder.imgType.setImageResource(R.drawable.ic_doc);
        } else if (extension.equals("zip")){
            holder.imgType.setImageResource(R.drawable.ic_zip);
        } else if (extension.equals("pdf")){
            holder.imgType.setImageResource(R.drawable.ic_pdf);
        } else  {
            holder.imgType.setImageResource(R.drawable.ic_none);
        }


        if (fileC.exists()){
            holder.btnDescargar.setVisibility(View.INVISIBLE);
            Log.i("ProbandoRuta", "Existe");
        }else {
            holder.btnDescargar.setVisibility(View.VISIBLE);
        }

        Log.i("ProbandoRuta", ruta+"/"+mListDownload.get(position).getName());

        holder.btnDescargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!file.mkdirs()) {
                    file.mkdirs();
                }
                if (fileC.exists()){
                    Toast.makeText(transmisionActivity2, "Este archivo ya se ha descargado", Toast.LENGTH_SHORT).show();
                }else{
                    downloadFile(holder.filename.getContext(),mListDownload.get(position).getName(),"/Tutosearch/Documentos/"+idTuto,linkDown);
                }

            }
        });



    }


    public void downloadFile(Context context, String fileName, String destinationDirectory, String url) {

        DownloadManager downloadmanager = (DownloadManager) context.
                getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(destinationDirectory, fileName);

        downloadmanager.enqueue(request);
        downloadmanager.addCompletedDownload("Tutosearch", "Se esta descargando el archivo"+fileName,false,
                "nada","nada 2",1,true);
    }

    @Override
    public int getItemCount() {
        return mListDownload.size();


    }

}
