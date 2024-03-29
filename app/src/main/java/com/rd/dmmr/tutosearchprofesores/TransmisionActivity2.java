package com.rd.dmmr.tutosearchprofesores;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bambuser.broadcaster.BroadcastStatus;
import com.bambuser.broadcaster.Broadcaster;
import com.bambuser.broadcaster.CameraError;
import com.bambuser.broadcaster.ConnectionError;
import com.github.zagum.switchicon.SwitchIconView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import org.apache.commons.io.FilenameUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;


public class TransmisionActivity2 extends AppCompatActivity implements View.OnClickListener {

    private static Long Mdia = Long.parseLong("86400000");
    private static Long Mhora = Long.parseLong("3600000");
    private static Long Mminuto = Long.parseLong("60000");
    private static Long Msegundo = Long.parseLong("1000");

    private SurfaceView mPreviewSurface;
    private Button btnIniciarTransmision;
    private LinearLayout btnAbrirLY;

    private StorageReference imgStorage;
    private StorageReference urlStorage;
    private StorageReference refDoc;
    private DatabaseReference DBRefGetid;
    private StorageReference archivoReference;

    private Bundle datosTuto;
    private TextView titulo;
    private String idTuto, download_url, thumb_downloadUrl, cualMethod, nameF, tiempoI;

    private List<DownloadModel> mListDown;
    private DownloadAdapter downloadAdapter;

    private FirebaseFirestore fdb;
    private FirebaseUser user;

    private ProgressDialog progressDialog;

    private Dialog mDialog;

    private static final int PICK_IMAGE = 1, PICK_DOC = 2, WRITE_EXTERNAL_STORAGE_CODE = 3;

    private Uri imgUri = null, docUri = null, nameImage, nameDoc;


    private static final String APPLICATION_ID = "bZFXuz2AitY0oFLf2UA36g";

    //Variables para el chat en vivo
    private RecyclerView rcChatLive;
    private EditText txtMensaje;
    private ImageButton btnEnviar;

    //
    private LinearLayout btnOpenChatlive, btnSilencio;
    private SwitchIconView swichtOpenChat, switchSilencio, swicthAbrirLY;

    List<ModelChatLive> mChatList;
    AdapterChatLive adapterChatLive;
    //---------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transmision2);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mDialog = new Dialog(this);

        titulo = (TextView) findViewById(R.id.txtTituloLive);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Iniciando transmisión...");
        progressDialog.setMessage("Preparando para transmitir, por favor espere.");
        progressDialog.setCanceledOnTouchOutside(false);

        //Variables para el chat en vivo
        rcChatLive = (RecyclerView) findViewById(R.id.RCChatLive);
        txtMensaje = (EditText) findViewById(R.id.txtMensajeEnviarLive);
        btnEnviar = (ImageButton) findViewById(R.id.btnEnviarLive);

        btnOpenChatlive = (LinearLayout) findViewById(R.id.btnOpenChatLive);
        btnSilencio = (LinearLayout) findViewById(R.id.btnSilencio);

        swichtOpenChat = (SwitchIconView) findViewById(R.id.swithiconChat);
        switchSilencio = (SwitchIconView) findViewById(R.id.swicthSilencio);
        swicthAbrirLY = (SwitchIconView) findViewById(R.id.swicthAbrirLY);


        mChatList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        rcChatLive.setHasFixedSize(true);
        rcChatLive.setLayoutManager(linearLayoutManager);


        //------------------------------------


        imgStorage = FirebaseStorage.getInstance().getReference();
        urlStorage = FirebaseStorage.getInstance().getReference();
        refDoc = FirebaseStorage.getInstance().getReference();

        DBRefGetid = FirebaseDatabase.getInstance().getReference();

        btnIniciarTransmision = (Button) findViewById(R.id.BroadcastButton);
        btnAbrirLY = (LinearLayout) findViewById(R.id.btnAbrirLY);

        mListDown = new ArrayList<>();
        downloadAdapter = new DownloadAdapter(mListDown);


        mPreviewSurface = (SurfaceView) findViewById(R.id.PreviewSurfaceView);
        mBroadcaster = new Broadcaster(this, APPLICATION_ID, mBroadcasterObserver);
        mBroadcaster.setRotation(getWindowManager().getDefaultDisplay().getRotation());


        fdb = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();

        datosTuto = intent.getExtras();

        if (datosTuto != null) {
            idTuto = datosTuto.getString("idTuto");
            tiempoI = datosTuto.getString("timestampI");
            titulo.setText(datosTuto.getString("Titulo"));


            titulo.setText(datosTuto.getString("Titulo"));
            Log.i("ProbandoLive", "" + datosTuto.getString(idTuto));
        }


        archivoReference = FirebaseStorage.getInstance().getReference();

        btnIniciarTransmision.setOnClickListener(this);
        btnAbrirLY.setOnClickListener(this);
        btnEnviar.setOnClickListener(this);
        btnOpenChatlive.setOnClickListener(this);
        btnSilencio.setOnClickListener(this);

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
            Log.w("", "Infodisponible2: " + i);
        }

        @Override
        public void onConnectionError(ConnectionError connectionError, String s) {
            Log.w("Mybroadcastingapp", "Received connection error: " + connectionError + ", " + s);
            progressDialog.dismiss();
            Toast.makeText(TransmisionActivity2.this, "Ha ocurrido un error al tratar de transmitir", Toast.LENGTH_SHORT).show();
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
            Log.w("", "Infodisponible: " + s + "Otra: " + s1);
        }

        @Override
        public void onBroadcastIdAvailable(String s) {
            Log.w("Mybroadcastingapp", "ID Transmision: " + s);
            Map update_hashmMap = new HashMap();
            update_hashmMap.put("broadcastId", s);
            update_hashmMap.put("timestamp_inicial", String.valueOf(System.currentTimeMillis()));

            fdb.collection("Tutorias_institucionales").document(idTuto)
                    .update(update_hashmMap)
                    .addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {

                            progressDialog.dismiss();
                            LeerMensajeLive();
                            findViewById(R.id.lyControls).setVisibility(View.VISIBLE);
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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO}, 1);
        else if (!hasPermission(Manifest.permission.RECORD_AUDIO))
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        else if (!hasPermission(Manifest.permission.CAMERA))
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        else if (!hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        else if (!hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE))
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        mBroadcaster.setCameraSurface(mPreviewSurface);
        mBroadcaster.onActivityResume();
    }

    private boolean hasPermission(String permission) {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }


    private void EnviarMensajeLive(String mensaje) {


        String timestamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("emisor", user.getUid());
        hashMap.put("mensaje", mensaje);
        hashMap.put("timestamp", timestamp);
        hashMap.put("tipo_user", "Profesores");

        String keyid = fdb.collection("Tutorias_institucionales").document(idTuto).collection("Mensajes_live").document().getId();

        fdb.collection("Tutorias_institucionales").document(idTuto).collection("Mensajes_live").document(keyid)
                .set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TransmisionActivity2.this, "No se pudo enviar el mensaje", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void LeerMensajeLive() {

        Query ref = fdb.collection("Tutorias_institucionales").document(idTuto).collection("Mensajes_live").orderBy("timestamp", Query.Direction.ASCENDING);
        ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i("Listen failed.", "" + e);
                    return;
                }

                for (DocumentChange dc : snapshot.getDocumentChanges()) {
                    final DocumentSnapshot docS = dc.getDocument();

                    switch (dc.getType()) {
                        case ADDED:


                            Handler handler = new Handler();

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {


                                    mChatList.add(new ModelChatLive(docS.getId(), docS.getString("mensaje"), docS.getString("emisor"), docS.getString("timestamp"), docS.getString("tipo_user")));
                                    Log.i("ProbandoPrincipal", "Tamaño: " + mChatList.size());


                                    adapterChatLive = new AdapterChatLive(TransmisionActivity2.this, mChatList);
                                    rcChatLive.setAdapter(adapterChatLive);
                                    adapterChatLive.notifyDataSetChanged();


                                }
                            }, 500);


                            break;
                        case MODIFIED:

                            break;
                        case REMOVED:

                            break;
                    }
                }
            }
        });

    }

    private void abrirLYPopup() {
        TextView txtCerrar;
        RecyclerView rvArchivos;
        Button btnImagen, btnDocs;


        mDialog.setContentView(R.layout.ly_archivos_tuto);
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (swicthAbrirLY.isIconEnabled()) {
                    swicthAbrirLY.switchState(false);
                }
            }
        });
        txtCerrar = (TextView) mDialog.findViewById(R.id.txtCerrar);
        rvArchivos = (RecyclerView) mDialog.findViewById(R.id.RC_Archivo);
        btnImagen = (Button) mDialog.findViewById(R.id.btnImagen);
        btnDocs = (Button) mDialog.findViewById(R.id.btnDoc);

        //Necesario para mostrar los datos el RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(TransmisionActivity2.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvArchivos.setLayoutManager(layoutManager);
        downloadAdapter = new DownloadAdapter(mListDown);
        rvArchivos.setAdapter(downloadAdapter);
        upFilestoRC();
        downloadAdapter.notifyDataSetChanged();
        //

        txtCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        btnImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Seleccione una imagen"), PICK_IMAGE);
            }
        });

        btnDocs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("application/pdf text/plain text/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Seleccione un archivo"), PICK_DOC);
            }
        });

        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        AlertDialog.Builder builder = new AlertDialog.Builder(TransmisionActivity2.this);
        builder.setMessage("¿Está seguro de que quiere subir este archivo?").setPositiveButton("Si", dialogArchivoClickListener)
                .setNegativeButton("No", dialogArchivoClickListener)
                .setCancelable(false);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            cualMethod = "Image";
            Uri imageurl = data.getData();

            nameImage = imageurl;

            CropImage.activity(imageurl)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result == null) {
                return;
            }
            cualMethod = "Image";
            imgUri = result.getUri();
            builder.show();


        }

        if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            docUri = data.getData();
            cualMethod = "Doc";
            builder.show();


        }

    }


    private String ObtenerNombreFile(Uri uri) {


        String fileName = null;


        if (uri.getScheme().equals("file")) {
            fileName = uri.getLastPathSegment();
        } else {
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DISPLAY_NAME}, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
                    Log.i("ProbandoTexto", fileName + " Extencion: " + FilenameUtils.getExtension(fileName));

                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        nameF = fileName;
        return fileName;
    }


    private void SubirImagen() {

        if (imgUri != null) {

            final String filename, keyid, extension;

            keyid = DBRefGetid.child("Archivos").push().getKey();

            filename = ObtenerNombreFile(nameImage);


            extension = FilenameUtils.getExtension(filename);
            StorageReference filePath = imgStorage.child("tuto_files").child(idTuto).child(keyid + "." + extension);
            final StorageReference thumb_filePath = imgStorage.child("tuto_files").child(idTuto).child("thumbs").child(keyid + "." + extension);

            progressDialog = new ProgressDialog(this);


            File thumb_filepath = new File(imgUri.getPath());

            final Bitmap thumb_bitmap = new Compressor(this)
                    .setMaxWidth(200)
                    .setMaxHeight(200)
                    .setQuality(75)
                    .compressToBitmap(thumb_filepath);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            final byte[] thumb_byte = baos.toByteArray();

            try {

                filePath.putFile(imgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            final StorageReference url_filePath = urlStorage.child("tuto_files").child(idTuto).child(keyid + "." + extension);
                            url_filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    download_url = uri.toString();
                                    Map hashmMap = new HashMap();
                                    hashmMap.put("file_name", filename);
                                    hashmMap.put("url_file", download_url);

                                    fdb.collection("tuto_files").document(idTuto).collection("files").document(keyid)
                                            .set(hashmMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressDialog.dismiss();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });


                                }

                            });

                            UploadTask uploadTask = thumb_filePath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    final StorageReference url_thumb_filePath = urlStorage.child("tuto_files").child(idTuto).child("thumbs").child(keyid + "." + extension);
                                    url_thumb_filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            thumb_downloadUrl = uri.toString();
                                            Map update_hashmMap_thumb = new HashMap();
                                            update_hashmMap_thumb.put("url_thumn_file", thumb_downloadUrl);

                                            fdb.collection("tuto_files").document(idTuto).collection("files").document(keyid)
                                                    .update(update_hashmMap_thumb)
                                                    .addOnSuccessListener(new OnSuccessListener() {
                                                        @Override
                                                        public void onSuccess(Object o) {
                                                            progressDialog.dismiss();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });

                                        }

                                    });

                                    if (thumb_task.isSuccessful()) {
                                        Toast.makeText(TransmisionActivity2.this, "Se ha subido el archivo con éxito", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();

                                    } else {
                                        Toast.makeText(TransmisionActivity2.this, "Error al subir la imagen.", Toast.LENGTH_LONG).show();
                                        Log.i("PruebaError", "Excepcion: " + task.getResult() + " Result: " + task.getResult());
                                        progressDialog.dismiss();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(TransmisionActivity2.this, "Error al subir la imagen.", Toast.LENGTH_LONG).show();
                            Log.i("PruebaError", "Excepcion: " + task.getResult() + " Result: " + task.getResult());
                            progressDialog.dismiss();
                        }
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setTitle("Cargando imagen...");
                        progressDialog.setMessage("Cargando imagen espere mientras se carga la imagen. \nSubiendo: " + (int) progress + "%");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                    }
                });

            } catch (Exception e) {
                Log.i("PruebaError", "Causa: " + e.getCause() + " Mensaje: " + e.getMessage());

            }

        }
    }


    private void SubirArchivo() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Subiendo archivo...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        final String keyid, filename, extension;

        filename = ObtenerNombreFile(docUri);
        nameF = filename;
        extension = FilenameUtils.getExtension(filename);

        keyid = DBRefGetid.child("Archivos").push().getKey();
        StorageReference reference = refDoc.child("tuto_files/" + idTuto + "/" + keyid + "." + extension);

        reference.putFile(docUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                        Toast.makeText(TransmisionActivity2.this, "Se ha subido el archivo", Toast.LENGTH_SHORT).show();

                        final StorageReference url_filePath = urlStorage.child("tuto_files").child(idTuto).child(keyid + "." + extension);
                        url_filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                download_url = uri.toString();
                                Map hashmMap = new HashMap();
                                hashmMap.put("file_name", filename);
                                hashmMap.put("url_file", download_url);

                                fdb.collection("tuto_files").document(idTuto).collection("files").document(keyid)
                                        .set(hashmMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.dismiss();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });


                            }

                        });


                        ///

                        download_url = uriTask.toString();


                        progressDialog.dismiss();


                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Subiendo: " + (int) progress + "%");


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }


    private void EliminarArchivos(final Integer pos) {

        String ruta = Environment.getExternalStorageDirectory() + "/Tutosearch/Documentos/" + idTuto;

        Log.i("Borrar", "Entro");

        File compFile = new File(ruta + mListDown.get(pos).name);

        if (compFile.exists()) {
            compFile.delete();

        }

        String storageUrl = mListDown.get(pos).linkFile;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(storageUrl);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully

                mListDown.remove(pos);
                downloadAdapter.notifyItemRemoved(pos);
                Log.i("Borrar", "onSuccess: deleted file");
                Toast.makeText(TransmisionActivity2.this, "Se ha borrado el archivo exitosamente", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.i("BorrarFail", "onFailure: did not delete file");
            }
        });

        fdb.collection("tuto_files").document(idTuto).collection("files").document(mListDown.get(pos).idDoc)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TransmisionActivity2.this, "Ocurrió un error al tratar de borrar el archivo", Toast.LENGTH_SHORT).show();
                    }
                });


    }


    void savefile(Uri sourceuri) {
        String sourceFilename = sourceuri.getPath();
        File destinationFilename = new File(Environment.getExternalStorageDirectory() + "/Tutosearch/Documentos/" + idTuto + "/" + nameF);


        Log.i("ProbandoFile", "El archivo que se lee: " + sourceFilename);

        String ruta = Environment.getExternalStorageDirectory() + "/Tutosearch/Documentos/" + idTuto;


        File compFile = new File(ruta);

        if (!compFile.exists()) {
            compFile.mkdirs();
        }

        File file_ruth = new File(ruta, nameF);

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            if (!destinationFilename.exists()) {

                file_ruth.createNewFile();

            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("ProbandoFileError", "No se creo el archivo vacio: " + e.getMessage());
        }


        try {
            bis = new BufferedInputStream(new FileInputStream(sourceFilename));
            bos = new BufferedOutputStream(new FileOutputStream(destinationFilename, false));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while (bis.read(buf) != -1);
            Log.i("ProbandoFile", "Se gualdo en: " + destinationFilename);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("ProbandoFileError", "Excepcion al escribir el archivo: " + e.getMessage());
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("ProbandoFileError", "El mensaje de error es: " + e.getMessage());
            }
        }
    }

    private void upFilestoRC() {


        CollectionReference ref = fdb.collection("tuto_files").document(idTuto).collection("files");
        ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i("ProbandoDBFailed", "Error de base de datos: " + e);
                    return;
                }
                String name, url, idDoc;


                for (DocumentChange dc : snapshot.getDocumentChanges()) {
                    DocumentSnapshot docS = dc.getDocument();
                    int index = -1;
                    switch (dc.getType()) {
                        case ADDED:


                            Log.i("ProbandoDBDocs", "" + docS.getData());


                            name = docS.getString("file_name");
                            url = docS.getString("url_file");
                            idDoc = docS.getId();

                            Log.i("ProbandoLlegada", "" + docS);


                            mListDown.add(new DownloadModel(name, url, idDoc, idTuto));

                            downloadAdapter.notifyDataSetChanged();

                            break;
                        case MODIFIED:

                            Log.i("Probando", "" + docS.getData());


                            name = docS.getString("file_name");
                            url = docS.getString("url_file");
                            idDoc = docS.getId();

                            Log.i("Probando", "" + docS);

                            index = getRCIndex(docS.getId());


                            mListDown.set(index, new DownloadModel(name, url, idDoc, idTuto));


                            downloadAdapter.notifyItemChanged(index);
                            break;
                        case REMOVED:


                            index = getRCIndex(docS.getId());

                            mListDown.remove(index);
                            downloadAdapter.notifyItemRemoved(index);

                            break;
                    }
                }

            }
        });

    }


    private int getRCIndex(String iDoc) {

        int index = -1;
        for (int i = 0; i < mListDown.size(); i++) {
            if (mListDown.get(i).idDoc.equals(iDoc)) {

                index = i;
                break;
            }
        }

        return index;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CualMethod();
                } else {
                    Toast.makeText(TransmisionActivity2.this, "Ha negado el permiso al almacenamiento.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.i("Borrar", "Entroo");
        switch (item.getItemId()) {
            case 0:

                EliminarArchivos(item.getGroupId());
                return true;

            case 1:

                break;

        }


        return super.onContextItemSelected(item);
    }


    private void CualMethod() {
        if (cualMethod.equals("Image")) {
            SubirImagen();
            savefile(imgUri);

        }
        if (cualMethod.equals("Doc")) {
            SubirArchivo();
            savefile(docUri);
        }
    }

    private String obtenerTiempoRestante(Long milisRestantes) {

        Long di, hor, min, seg;
        String textRestante = "";

        if (milisRestantes >= Mdia) {
            di = milisRestantes / Mdia;
            milisRestantes -= Mdia * di;
            textRestante += (milisRestantes >= Mhora ? di + " días, " : di + " días,");
        }
        if (milisRestantes >= Mhora) {
            hor = milisRestantes / Mhora;
            milisRestantes -= Mhora * hor;
            textRestante += (milisRestantes >= Mminuto ? hor + " horas, " : hor + " horas");
        }
        if (milisRestantes >= Mminuto) {
            min = milisRestantes / Mminuto;
            milisRestantes -= Mminuto * min;
            textRestante += (milisRestantes >= Msegundo ? min + " minutos y " : min + " minutos");
        }

        if (milisRestantes >= Msegundo) {
            seg = milisRestantes / Msegundo;
            textRestante += seg + " segundos";
        }
        return textRestante;
    }


    final DialogInterface.OnClickListener dialogArchivoClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface
                        .BUTTON_POSITIVE:
                    CualMethod();

                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        }
    };

    final DialogInterface.OnClickListener dialogIniciarClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface
                        .BUTTON_POSITIVE:
                    if (mBroadcaster.canStartBroadcasting()) {
                        mBroadcaster.setAuthor(idTuto);
                        mBroadcaster.startBroadcast();
                        progressDialog.show();
                        tiempoI = String.valueOf(System.currentTimeMillis());
                    } else {
                        mBroadcaster.stopBroadcast();
                    }

                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        }
    };


    @Override
    public void onClick(View view) {
        if (view == btnIniciarTransmision) {
            Long ini, actual;

            ini = Long.parseLong(tiempoI);
            actual = System.currentTimeMillis();
            String strTiempo;

            if (ini > actual) {
                strTiempo = obtenerTiempoRestante((ini - actual));
                AlertDialog.Builder builder = new AlertDialog.Builder(TransmisionActivity2.this);
                builder.setMessage("Aún quedan " + strTiempo + " para iniciar la transmisión.\n\n¿Está seguro que desea iniciar la transmisión?, sí presiona que 'Si' la transmisión iniciará y " +
                        "la fecha será modificada a la actual.").setPositiveButton("Si", dialogIniciarClickListener)
                        .setNegativeButton("No", dialogIniciarClickListener)
                        .setCancelable(false);
                builder.show();


            } else {
                if (mBroadcaster.canStartBroadcasting()) {
                    mBroadcaster.setAuthor(idTuto);
                    mBroadcaster.startBroadcast();
                    progressDialog.show();
                } else {
                    mBroadcaster.stopBroadcast();
                }
            }


        }
        if (view == btnAbrirLY) {
            abrirLYPopup();
            swicthAbrirLY.switchState();
        }
        if (view == btnEnviar) {
            String mensaje = txtMensaje.getText().toString().trim();
            txtMensaje.setText("");

            if (TextUtils.isEmpty(mensaje)) {
                Toast.makeText(this, "No puede enviar un mensaje vacío", Toast.LENGTH_SHORT).show();
            } else {
                EnviarMensajeLive(mensaje);
            }
        }
        if (view == btnOpenChatlive) {
            swichtOpenChat.switchState();

            if (swichtOpenChat.isIconEnabled()) {
                findViewById(R.id.rlChatLive).setVisibility(View.VISIBLE);
                btnIniciarTransmision.setVisibility(View.INVISIBLE);
            } else {
                findViewById(R.id.rlChatLive).setVisibility(View.INVISIBLE);
                btnIniciarTransmision.setVisibility(View.VISIBLE);
            }
        }
    }


}
