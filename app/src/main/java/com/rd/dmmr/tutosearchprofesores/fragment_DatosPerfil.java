package com.rd.dmmr.tutosearchprofesores;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 * A simple {@link Fragment} subclass.
 */
public class fragment_DatosPerfil extends Fragment {


    public fragment_DatosPerfil() {
        // Required empty public constructor
    }


    private View view;
    private FirebaseFirestore fdb;
    private StorageReference urlStorage;


    private FirebaseUser mCurrentUser;
    private TextView txtNombreProf, txtApellidoProf, txtFechaNacimientoProf, txtCorreoProf, txtTelefonoProf, txtDireccionProf;

    PerfilProf perfilProf = new PerfilProf();

    //variables
    private String current_uid;


    private StorageReference mImageStorage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_datos_perfil, container, false);

            urlStorage = FirebaseStorage.getInstance().getReference();

            mImageStorage = FirebaseStorage.getInstance().getReference();
            //llamando la base de datos
            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
            //identificando el id del que tiene la sesion iniciada
            current_uid = mCurrentUser.getUid();
            //especificando donde se buscara


            fdb = FirebaseFirestore.getInstance();

            txtNombreProf = (TextView) view.findViewById(R.id.txtNombreProf);
            txtApellidoProf = (TextView) view.findViewById(R.id.txtApellidosProf);
            txtFechaNacimientoProf = (TextView) view.findViewById(R.id.txtFechaNacimientoProf);
            txtCorreoProf = (TextView) view.findViewById(R.id.txtCorreoProf);
            txtTelefonoProf = (TextView) view.findViewById(R.id.txtTelefonoProf);
            txtDireccionProf = (TextView) view.findViewById(R.id.txtDireccionProf);


            Log.i("ProbandoPerfil", "User id: "+current_uid);



        CargarDatosProf();

        } catch (Exception e) {
            Toast.makeText(view.getContext(), "Error al cargar la pantalla", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void CargarDatosProf() {

       // try {
            DocumentReference docRef = fdb.collection("Profesores").document(current_uid);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    //Log.i("Probando id", ""+dataSnapshot);

                    DocumentSnapshot docS = task.getResult();

                    String name = docS.getString("nombres");
                    String apellido = docS.getString("apellidos");
                    String fechaNacimiento = docS.getString("fecha_nacimiento");
                    String telefono = docS.getString("telefonos");
                    String correo = docS.getString("correo");
                    String img = docS.getString("url_pic");
                    String direccion = docS.getString("direccion");


                    //para separar el numero de telefono
                    String te = telefono.replace(" ", "");
                    String tress = te.substring(0, 3);
                    String seis = te.substring(3, 6);
                    String cuatro = te.substring(6, 10);
                    String telefonoo = tress + "-" + seis + "-" + cuatro;

                    txtNombreProf.setText(name);
                    txtApellidoProf.setText(apellido);
                    txtFechaNacimientoProf.setText(fechaNacimiento);
                    txtTelefonoProf.setText(telefonoo);
                    txtCorreoProf.setText(correo);



                    if (!direccion.equals("none")) {

                    }else{
                        txtDireccionProf.setText("Dirección no especificada");
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(view.getContext(), "Error de la base de datos", Toast.LENGTH_SHORT).show();
                }
            });


/*
        } catch (
                Exception e) {

            Toast.makeText(view.getContext(), "Error", Toast.LENGTH_LONG).show();
        }
*/
    }


}

