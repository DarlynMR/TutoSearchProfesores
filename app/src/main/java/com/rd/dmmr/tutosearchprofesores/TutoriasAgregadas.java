package com.rd.dmmr.tutosearchprofesores;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class TutoriasAgregadas extends AppCompatActivity {


    FirebaseFirestore fdb;
    FirebaseUser FUser;

    RecyclerView RCAbajo;

    private TutoriasAdapter tutoriasAdapter;

    private List<ModelTutoriasProf> mListTutoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorias_agregadas);

        RCAbajo = (RecyclerView) findViewById(R.id.RCAbajo);


        fdb = FirebaseFirestore.getInstance();
        FUser = FirebaseAuth.getInstance().getCurrentUser();

        mListTutoria = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(TutoriasAgregadas.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);


        RCAbajo.setLayoutManager(layoutManager);

        //CrearTTutorias();

        tutoriasAdapter = new TutoriasAdapter(mListTutoria);

        RCAbajo.setAdapter(tutoriasAdapter);
        tutoriasAdapter.notifyDataSetChanged();

        upTutorias();

    }


    private void upTutorias() {


        CollectionReference ref = fdb.collection("Tutorias_institucionales");
        Query queryTuto = ref.whereEqualTo("UIDProfesor", FUser.getUid());
        Log.i("Probando", FUser.getUid());

        queryTuto.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i("Listen failed.", "" + e);
                    return;
                }


                for (DocumentChange dc : snapshot.getDocumentChanges()) {
                    DocumentSnapshot docS = dc.getDocument();

                    ModelTutoriasProf modelProf = docS.toObject(ModelTutoriasProf.class);

                    int index = -1;
                    switch (dc.getType()) {
                        case ADDED:
                            Log.i("Probando", "" + docS.getData());
                            Log.i("Probando", "" + modelProf.getTitulo());
                            if (modelProf.getTipo_tuto().equals("Live")) {
                                mListTutoria.add(new ModelTutoriasProf(docS.getId(), modelProf.getTitulo(), modelProf.getDescripcion(), modelProf.getBroadcastId(), modelProf.getFecha(), modelProf.getFecha_pub(), modelProf.getHora_inicial(), modelProf.getHora_pub(), modelProf.getMateria(), modelProf.getTipo_tuto(), modelProf.getUrl_image_portada(), modelProf.getUrl_thumb_image_portada(), ""));
                            } else {
                                mListTutoria.add(new ModelTutoriasProf(docS.getId(), modelProf.getTitulo(), modelProf.getDescripcion(), modelProf.getBroadcastId(), modelProf.getFecha(), modelProf.getFecha_pub(), modelProf.getHora_inicial() + " - " + modelProf.getHora_final(), modelProf.getHora_pub(), modelProf.getMateria(), modelProf.getTipo_tuto(), modelProf.getUrl_image_portada(), modelProf.getUrl_thumb_image_portada(), modelProf.getLugar()));
                            }
                            tutoriasAdapter.notifyDataSetChanged();

                            break;
                        case MODIFIED:

                            Log.i("Probando", "" + docS.getData());

                            index = getRCIndex(docS.getId());

                            if (modelProf.getTipo_tuto().equals("Live")) {
                                mListTutoria.set(index, new ModelTutoriasProf(docS.getId(), modelProf.getTitulo(), modelProf.getDescripcion(), modelProf.getBroadcastId(), modelProf.getFecha(), modelProf.getFecha_pub(), modelProf.getHora_inicial(), modelProf.getHora_pub(), modelProf.getMateria(), modelProf.getTipo_tuto(), modelProf.getUrl_image_portada(), modelProf.getUrl_thumb_image_portada(), ""));
                            } else {
                                mListTutoria.add(index, new ModelTutoriasProf(docS.getId(), modelProf.getTitulo(), modelProf.getDescripcion(), modelProf.getBroadcastId(), modelProf.getFecha(), modelProf.getFecha_pub(), modelProf.getHora_inicial() + " - " + modelProf.getHora_final(), modelProf.getHora_pub(), modelProf.getMateria(), modelProf.getTipo_tuto(), modelProf.getUrl_image_portada(), modelProf.getUrl_thumb_image_portada(), modelProf.getLugar()));
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

        switch (item.getItemId()){
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

}
