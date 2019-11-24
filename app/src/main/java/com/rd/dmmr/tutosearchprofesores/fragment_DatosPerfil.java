package com.rd.dmmr.tutosearchprofesores;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class fragment_DatosPerfil extends Fragment {


    public fragment_DatosPerfil() {
        // Required empty public constructor
    }

    ArrayList<String> listaMateriasProf;
    ArrayList<String> listaMaterias;
    RecyclerView rcMateria;
    AdapterMaterias adapterMaterias;

    //Checkbox materias
    String[] listItems;
    boolean[] checkItems;
    ArrayList<Integer> mUserItems = new ArrayList<>();


    private View view;
    private FirebaseFirestore fdb;
    private StorageReference urlStorage;
    private FirebaseFirestore fragment_fdb;

    Button btnAgregarMateria;

    private FirebaseUser mCurrentUser;
    private TextView txtNombreProf, txtApellidoProf, txtFechaNacimientoProf, txtCorreoProf, txtTelefonoProf, txtDireccionProf, empty;

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

            fragment_fdb = FirebaseFirestore.getInstance();


            listaMateriasProf = new ArrayList<String>();
            listaMaterias = new ArrayList<String>();


            rcMateria = (RecyclerView) view.findViewById(R.id.RCMaterias);
            rcMateria.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));


            fdb = FirebaseFirestore.getInstance();

            txtNombreProf = (TextView) view.findViewById(R.id.txtNombreProf);
            txtApellidoProf = (TextView) view.findViewById(R.id.txtApellidosProf);
            txtFechaNacimientoProf = (TextView) view.findViewById(R.id.txtFechaNacimientoProf);
            txtCorreoProf = (TextView) view.findViewById(R.id.txtCorreoProf);
            txtTelefonoProf = (TextView) view.findViewById(R.id.txtTelefonoProf);
            txtDireccionProf = (TextView) view.findViewById(R.id.txtDireccionProf);
            btnAgregarMateria = (Button) view.findViewById(R.id.btnAgregarMaterias);
            empty = (TextView) view.findViewById(R.id.textEmpty);

            btnAgregarMateria.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {

                    for (int i = 0; i < listaMaterias.size(); i++) {
                        listItems[i] = listaMaterias.get(i);
                    }

                    Log.i("ProbandoArray", "string list size: " + listItems.length + " ArrayList size;" + listaMaterias.size());

                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getContext());
                    mBuilder.setTitle("Materias disponibles a elegir");
                    mBuilder.setMultiChoiceItems(listItems, checkItems, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int pos, boolean isCheck) {
                            if (isCheck) {
                                if (!mUserItems.contains(pos)) {
                                    mUserItems.add(pos);
                                }
                            } else if (mUserItems.contains(pos)) {
                                mUserItems.remove(pos);
                            }
                        }
                    });
                    mBuilder.setCancelable(false);
                    mBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String item = "";
                            String materiasSave[] = new String[listItems.length];


                            ArrayList<String> list = new ArrayList<>();

                            for (int iPos = 0; iPos < mUserItems.size(); iPos++) {

                                item = item + listItems[mUserItems.get(iPos)];
                                list.add(listItems[mUserItems.get(iPos)]);
                                if (iPos != mUserItems.size() - 1) {
                                    item = item + ", ";
                                }
                            }
                            final ProgressDialog progressDialog = new ProgressDialog(view.getContext());
                            progressDialog.setTitle("Cargando datos...");
                            progressDialog.setMessage("Se está actualizando su información");
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();

                            final Map map = new HashMap<>();
                            map.put("Materias", list);

                            fdb.collection("Profesores").document(current_uid)
                                    .update(map)
                                    .addOnSuccessListener(new OnSuccessListener() {
                                        @Override
                                        public void onSuccess(Object o) {

                                            progressDialog.dismiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(view.getContext(), "Ha ocurrido un problema y no se pudo actualizar las materias", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            });

                            txtDireccionProf.setText(item);
                        }
                    });
                    mBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    AlertDialog mDialog = mBuilder.create();
                    mDialog.show();
                }
            });

            CargarDialogSpinner();
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
                Object objMateria = docS.get("Materias");


                //para separar el numero de telefono
                String te = telefono.replace(" ", "");
                String tress = te.substring(0, 3);
                String seis = te.substring(3, 6);
                String cuatro = te.substring(6, 10);
                String telefonoo = tress + "-" + seis + "-" + cuatro;

                if (objMateria != null) {
                    try {
                        empty.setVisibility(View.INVISIBLE);
                        listaMateriasProf = (ArrayList) docS.get("Materias");
                        adapterMaterias = new AdapterMaterias(listaMateriasProf);
                        rcMateria.setAdapter(adapterMaterias);

                    } catch (Exception e) {
                        Toast.makeText(perfilProf, "Error al cargar las materias " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    empty.setVisibility(View.VISIBLE);
                }

                txtNombreProf.setText(name);
                txtApellidoProf.setText(apellido);
                txtFechaNacimientoProf.setText(fechaNacimiento);
                txtTelefonoProf.setText(telefonoo);
                txtCorreoProf.setText(correo);


                if (!direccion.equals("none")) {

                } else {
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

    private void CargarDialogSpinner() {
        CollectionReference collectionReference = fdb.collection("Materias");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    DocumentSnapshot docS = dc.getDocument();

                    switch (dc.getType()) {
                        case ADDED:
                            listaMaterias.add(docS.getId());
                            Log.i("Materias", docS.getId());


                            break;
                        case MODIFIED:

                            break;
                        case REMOVED:


                            break;
                    }
                }


                if (listaMaterias.size() != 0) {
                    listItems = new String[listaMaterias.size()];
                    checkItems = new boolean[listaMaterias.size()];


                }
            }
        });


    }


}

