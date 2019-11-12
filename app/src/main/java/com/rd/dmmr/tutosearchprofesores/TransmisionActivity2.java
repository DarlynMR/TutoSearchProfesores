package com.rd.dmmr.tutosearchprofesores;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bambuser.broadcaster.BroadcastStatus;
import com.bambuser.broadcaster.Broadcaster;
import com.bambuser.broadcaster.CameraError;
import com.bambuser.broadcaster.ConnectionError;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class TransmisionActivity2 extends AppCompatActivity implements View.OnClickListener {

    private SurfaceView mPreviewSurface;
    private Button btnIniciarTransmision;

    private Bundle datosTuto;
    private TextView titulo;
    private String idTuto;

    FirebaseFirestore fdb;

    ProgressDialog progressDialog;

    private static final String APPLICATION_ID = "gn3xWy0C556si80X9aPkTA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transmision2);

        titulo = (TextView) findViewById(R.id.txtTituloLive);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Iniciando transmisión...");
        progressDialog.setMessage("Preparando para transmitir, por favor espere.");
        progressDialog.setCanceledOnTouchOutside(false);


        btnIniciarTransmision = (Button)findViewById(R.id.BroadcastButton);

        mPreviewSurface = (SurfaceView) findViewById(R.id.PreviewSurfaceView);
        mBroadcaster = new Broadcaster(this, APPLICATION_ID, mBroadcasterObserver);
        mBroadcaster.setRotation(getWindowManager().getDefaultDisplay().getRotation());


        fdb = FirebaseFirestore.getInstance();
        Intent intent= getIntent();

        datosTuto=intent.getExtras();

        if (datosTuto!=null) {
            idTuto = datosTuto.getString("idTuto");
            titulo.setText(datosTuto.getString("Titulo"));


            titulo.setText(datosTuto.getString("Titulo"));
            Log.i("ProbandoLive",""+datosTuto.getString(idTuto));
        }



        btnIniciarTransmision.setOnClickListener(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBroadcaster.onActivityDestroy();
    }
    @Override
    public void onPause() {
        super.onPause();
        mBroadcaster.onActivityPause();
    }

    Broadcaster mBroadcaster;

    private Broadcaster.Observer mBroadcasterObserver = new Broadcaster.Observer() {
        @Override
        public void onConnectionStatusChange(BroadcastStatus broadcastStatus) {
            if (broadcastStatus == BroadcastStatus.STARTING)
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            if (broadcastStatus == BroadcastStatus.IDLE)
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            btnIniciarTransmision.setText(broadcastStatus == BroadcastStatus.IDLE ? "Iniciar transmisión" : "Desconectar");
            Log.i("Mybroadcastingapp", "Received status change: " + broadcastStatus);
        }
        @Override
        public void onStreamHealthUpdate(int i) {
            Log.w("", "Infodisponible2: " +i);
        }
        @Override
        public void onConnectionError(ConnectionError connectionError, String s) {
            Log.w("Mybroadcastingapp", "Received connection error: " + connectionError + ", " + s);
        }
        @Override
        public void onCameraError(CameraError cameraError) {
        }
        @Override
        public void onChatMessage(String s) {
        }
        @Override
        public void onResolutionsScanned() {
        }
        @Override
        public void onCameraPreviewStateChanged() {

        }
        @Override
        public void onBroadcastInfoAvailable(String s, String s1) {
            Log.w("", "Infodisponible: " +s +"Otra: "+s1);
        }
        @Override
        public void onBroadcastIdAvailable(String s) {
            Log.w("Mybroadcastingapp", "ID Transmision: " +s);
            Map update_hashmMap=new HashMap();
            update_hashmMap.put("broadcastId", s);

            fdb.collection("Tutorias_institucionales").document(idTuto)
                    .update(update_hashmMap)
                    .addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {

                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(TransmisionActivity2.this, "Ha ocurrido un problema y no se pudo iniciar la transmisión", Toast.LENGTH_LONG).show();
                    mBroadcaster.stopBroadcast();
                    progressDialog.dismiss();
                }
            });

        }
    };




    @Override
    public void onResume() {
        super.onResume();
        if (!hasPermission(Manifest.permission.CAMERA)
                && !hasPermission(Manifest.permission.RECORD_AUDIO))
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO}, 1);
        else if (!hasPermission(Manifest.permission.RECORD_AUDIO))
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, 1);
        else if (!hasPermission(Manifest.permission.CAMERA))
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 1);

        mBroadcaster.setCameraSurface(mPreviewSurface);
        mBroadcaster.onActivityResume();
    }

    private boolean hasPermission(String permission) {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onClick(View view) {
        if(view==btnIniciarTransmision){
            if (mBroadcaster.canStartBroadcasting()) {
                mBroadcaster.startBroadcast();
                progressDialog.show();
            }
            else{
                mBroadcaster.stopBroadcast();}
        }
    }
}
