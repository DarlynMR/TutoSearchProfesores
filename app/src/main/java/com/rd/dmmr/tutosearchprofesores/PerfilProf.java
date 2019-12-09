package com.rd.dmmr.tutosearchprofesores;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class PerfilProf extends AppCompatActivity {


    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private ProgressDialog mProgressDialog;
    private FirebaseUser mCurrentUser;
    private StorageReference urlStorage;
    private StorageReference mImageStorage;

    private FirebaseFirestore fdb;

    private String download_url, thumb_downloadUrl;

    private ImageView img_perfilProf;

    private String current_user_id;



    private static final int GALERRY_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_prof);

        setUpView();
        setUpViewPagerAdapter();
        urlStorage = FirebaseStorage.getInstance().getReference();
        mImageStorage  = FirebaseStorage.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        current_user_id=mCurrentUser.getUid();

        fdb = FirebaseFirestore.getInstance();

        img_perfilProf = (ImageView) findViewById(R.id.imgPerfil);

        //LlAMANDO EL TOOLBAR
        final Toolbar toolbar = (Toolbar) findViewById(R.id.MyToolbarPerfil);
        //  toolbar.setTitleTextColor(Color.parseColor("#00FF00"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Colapsando la barra
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbarPerfil);
        collapsingToolbarLayout.setTitle("Usuario");
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);

        //Color de la barra
        Context context = this;
        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(context, R.color.colorPrimary));




        //boton agregar imagen
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabImgChange);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "Seleccione una imagen"), GALERRY_PICK);
                     /* .setAction("Action", null).show();
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(Detalles_Usuario.this);*/


            }
        });

        DocumentReference docRef = fdb.collection("Profesores").document(current_user_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                //Log.i("Probando id", ""+dataSnapshot);

                DocumentSnapshot docS = task.getResult();

                String name = docS.getString("nombres");
                String apellido = docS.getString("apellidos");
                String img = docS.getString("url_pic");

                CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbarPerfil);
                collapsingToolbarLayout.setTitle(name + " " + apellido);

                if (!img.equals("defaultPicProf")) {
                    try {
                        Glide.with(PerfilProf.this)
                                .load(img)
                                .fitCenter()
                                .centerCrop()
                                .into(img_perfilProf);

                    } catch (Exception e) {
                        Log.i("ErrorImg", "" + e.getMessage());
                    }

                } else {
                    img_perfilProf.setImageResource(R.mipmap.default_profile_prof);
                }



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PerfilProf.this, "Error de la base de datos", Toast.LENGTH_SHORT).show();
            }
        });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == GALERRY_PICK && resultCode == RESULT_OK) {
                Uri imageurl = data.getData();

                CropImage.activity(imageurl)
                        .setAspectRatio(1, 1)
                        .start(this);

                //Toast.makeText(Detalles_Usuario.this, imageurl,Toast.LENGTH_LONG).show();
            }

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                final Uri resultUri = result.getUri();

                if (result==null){
                    return;
                }

                //CREANDO EL PROGREES BAR
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setTitle("Cargando imagen...");
                mProgressDialog.setMessage("Cargando imagen espere mientras se carga la imagen.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();


                File thumb_filePath = new File(resultUri.getPath());

                final Bitmap thumb_bitmap=new Compressor(this)
                        .setMaxWidth(100)
                        .setMaxHeight(100)
                        .setQuality(100)
                        .compressToBitmap(thumb_filePath);

                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                final byte[] thumb_byte=baos.toByteArray();

                StorageReference filepath=mImageStorage.child("img_profile").child(current_user_id + ".jpg");
                final StorageReference thumb_filepath =mImageStorage.child("img_profile").child("thumbs").child(current_user_id+".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){

                            final StorageReference url_filePath = urlStorage.child("img_profile").child(current_user_id + ".jpg");
                            url_filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    img_perfilProf.setImageURI(resultUri);

                                    download_url = uri.toString();
                                    Map update_hashmMap=new HashMap();
                                    update_hashmMap.put("url_pic",download_url);
                                    fdb.collection("Profesores").document(current_user_id)
                                            .update(update_hashmMap)
                                            .addOnSuccessListener(new OnSuccessListener() {
                                                @Override
                                                public void onSuccess(Object o) {

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(PerfilProf.this,"Error al subir la imagen.",Toast.LENGTH_LONG).show();
                                            mProgressDialog.dismiss();
                                        }
                                    });

                                }

                            });


                            UploadTask uploadTask=thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    if(thumb_task.isSuccessful()){

                                        final StorageReference url_thumbfilePath = urlStorage.child("img_profile").child("thumbs").child(current_user_id + ".jpg");
                                        url_thumbfilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {

                                                thumb_downloadUrl = uri.toString();

                                                Map update_hashmMap=new HashMap();
                                                update_hashmMap.put("url_thumb_pic",thumb_downloadUrl);

                                                fdb.collection("Profesores").document(current_user_id)
                                                        .update(update_hashmMap)
                                                        .addOnSuccessListener(new OnSuccessListener() {
                                                            @Override
                                                            public void onSuccess(Object o) {
                                                                mProgressDialog.dismiss();
                                                                Toast.makeText(PerfilProf.this,"Imagen Subida.",Toast.LENGTH_LONG).show();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(PerfilProf.this,"Error al subir la imagen.",Toast.LENGTH_LONG).show();
                                                        mProgressDialog.dismiss();
                                                    }
                                                });

                                            }

                                        });


                                    }else{
                                        Toast.makeText(PerfilProf.this,"Error al subir la imagen.",Toast.LENGTH_LONG).show();
                                        mProgressDialog.dismiss();
                                    }

                                }
                            });

                        }else{
                            Toast.makeText(PerfilProf.this,"Error al subir la imagen.",Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();
                        }
                    }
                });

            }
        }catch (Exception e){
            Toast.makeText(PerfilProf.this,"No se cambio la imagen!",Toast.LENGTH_LONG).show();
        }
    }



    private void setUpView() {

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        appBarLayout = (AppBarLayout) findViewById(R.id.MyAppbarPerfil);
        viewPager = (ViewPager) findViewById(R.id.viewPagerP);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
    }

    private void setUpViewPagerAdapter() {
        viewPagerAdapter.addFragment(new fragment_DatosPerfil(), "DATOS");
        viewPagerAdapter.addFragment(new fragment_Valoraciones(), "VALORACIONES");
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }

}
