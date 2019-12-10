package com.rd.dmmr.tutosearchprofesores;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TutoriasAgregadas extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    FirebaseFirestore fdb;
    FirebaseUser FUser;

    RecyclerView RCAbajo;

    private TutoriasAdapter tutoriasAdapter;

    private List<ModelTutoriasProf> mListTutoria;
    private FloatingActionButton fBack;
    private Spinner spnMateria;
    private SearchView searchView;

    boolean create = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorias_agregadas);

        RCAbajo = (RecyclerView) findViewById(R.id.RCAbajo);


        fdb = FirebaseFirestore.getInstance();
        FUser = FirebaseAuth.getInstance().getCurrentUser();

        fBack = (FloatingActionButton) findViewById(R.id.fBackButton);
        spnMateria = (Spinner) findViewById(R.id.spnMateria);
        searchView = (SearchView) findViewById(R.id.txtBuscar);

        mListTutoria = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(TutoriasAgregadas.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        spnMateria.setOnItemSelectedListener(this);

        RCAbajo.setLayoutManager(layoutManager);

        //CrearTTutorias();

        tutoriasAdapter = new TutoriasAdapter(mListTutoria);

        RCAbajo.setAdapter(tutoriasAdapter);
        tutoriasAdapter.notifyDataSetChanged();

        fBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //tutoriasAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                tutoriasAdapter.getFilter().filter(s);
                return false;
            }
        });

        upTutorias();

    }


    private void upTutorias() {


        CollectionReference ref = fdb.collection("Tutorias_institucionales");
        Query queryTuto = ref.whereEqualTo("UIDProfesor", FUser.getUid());

        queryTuto.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i("Listen failed.", "" + e);
                    return;
                }

                final List<String> materias = new ArrayList<>();
                spnMateria = (Spinner) findViewById(R.id.spnMateria);
                ArrayAdapter<String> materiasAdapter = new ArrayAdapter<String>(TutoriasAgregadas.this, android.R.layout.simple_spinner_item, materias);
                materiasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnMateria.setAdapter(materiasAdapter);

                for (DocumentChange dc : snapshot.getDocumentChanges()) {
                    DocumentSnapshot docS = dc.getDocument();

                    ModelTutoriasProf modelProf = docS.toObject(ModelTutoriasProf.class);

                    int index = -1;
                    switch (dc.getType()) {
                        case ADDED:


                            if (!materias.contains(modelProf.getMateria())) {
                                materias.add(modelProf.getMateria());
                                materiasAdapter.notifyDataSetChanged();
                            }

                            if (modelProf.getTipo_tuto().equals("Live")) {
                                mListTutoria.add(new ModelTutoriasProf(docS.getId(), modelProf.getTitulo(), modelProf.getDescripcion(), modelProf.getBroadcastId(), docS.getString("timestamp_inicial"), docS.getString("timestamp_final"), docS.getString("timestamp_pub"), modelProf.getMateria(), modelProf.getTipo_tuto(), modelProf.getUrl_image_portada(), modelProf.getUrl_thumb_image_portada(), ""));
                            } else {
                                mListTutoria.add(new ModelTutoriasProf(docS.getId(), modelProf.getTitulo(), modelProf.getDescripcion(), modelProf.getBroadcastId(), docS.getString("timestamp_inicial"), docS.getString("timestamp_final"), docS.getString("timestamp_pub"), modelProf.getMateria(), modelProf.getTipo_tuto(), modelProf.getUrl_image_portada(), modelProf.getUrl_thumb_image_portada(), modelProf.getLugar()));
                            }
                            tutoriasAdapter.notifyDataSetChanged();

                            break;
                        case MODIFIED:

                            index = getRCIndex(docS.getId());

                            if (modelProf.getTipo_tuto().equals("Live")) {
                                mListTutoria.set(index, new ModelTutoriasProf(docS.getId(), modelProf.getTitulo(), modelProf.getDescripcion(), modelProf.getBroadcastId(), docS.getString("timestamp_inicial"), docS.getString("timestamp_final"), docS.getString("timestamp_pub"), modelProf.getMateria(), modelProf.getTipo_tuto(), modelProf.getUrl_image_portada(), modelProf.getUrl_thumb_image_portada(), ""));
                            } else {
                                mListTutoria.set(index, new ModelTutoriasProf(docS.getId(), modelProf.getTitulo(), modelProf.getDescripcion(), modelProf.getBroadcastId(), docS.getString("timestamp_inicial"), docS.getString("timestamp_final"), docS.getString("timestamp_pub"), modelProf.getMateria(), modelProf.getTipo_tuto(), modelProf.getUrl_image_portada(), modelProf.getUrl_thumb_image_portada(), modelProf.getLugar()));
                            }
                            tutoriasAdapter.notifyDataSetChanged();


                            tutoriasAdapter.notifyItemChanged(index);
                            break;
                        case REMOVED:


                            index = getRCIndex(docS.getId());

                            mListTutoria.remove(index);
                            tutoriasAdapter.notifyItemRemoved(index);

                            break;
                    }
                }

            }
        });


    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 0:
                return true;

            case 1:

                break;

        }
        return super.onContextItemSelected(item);
    }

    private int getRCIndex(String iTuto) {

        int index = -1;
        for (int i = 0; i < mListTutoria.size(); i++) {
            if (mListTutoria.get(i).idTuto.equals(iTuto)) {

                index = i;
                break;
            }
        }

        return index;

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (!create) {
            create = true;
            return;
        }

        if (adapterView.getId() == R.id.spnMateria) {
            if (spnMateria.getSelectedItem() != null) {
                tutoriasAdapter.getFilter().filter(spnMateria.getSelectedItem().toString());
            } else {
                tutoriasAdapter.getFilter().filter("");
            }
            Log.i("ProbandoSPN", "Entro a la condicion");
        }
    }


        @Override
        public void onNothingSelected (AdapterView < ? > adapterView){

        }
    }

