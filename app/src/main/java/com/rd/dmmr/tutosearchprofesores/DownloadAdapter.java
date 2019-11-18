package com.rd.dmmr.tutosearchprofesores;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.List;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadHolder> {

    TransmisionActivity2 transmisionActivity2;
    List<DownloadModel> downloadModels;
    String linkDown;

    public DownloadAdapter(TransmisionActivity2 transmisionActivity2, ArrayList<DownloadModel> downloadModels) {
        this.transmisionActivity2 = transmisionActivity2;
        this.downloadModels = downloadModels;
    }

    public DownloadAdapter(List<DownloadModel> mListDown) {
        this.downloadModels = mListDown;
    }

    @NonNull
    @Override
    public DownloadHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        return new DownloadHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_archivo, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadHolder holder, int position) {

        String extension;
        holder.filename.setText(downloadModels.get(position).getName());
        linkDown = downloadModels.get(position).getLinkFile();

        extension = FilenameUtils.getExtension(downloadModels.get(position).getName());

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



    }




    public void downloadFile(Context context, String fileName, String destinationDirectory, String url) {

        DownloadManager downloadmanager = (DownloadManager) context.
                getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName);

        downloadmanager.enqueue(request);
    }

    @Override
    public int getItemCount() {
        return downloadModels.size();
    }
}
