package com.rd.dmmr.tutosearchprofesores;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class fragment_solicitudes extends Fragment {


    public fragment_solicitudes() {
        // Required empty public constructor
    }

    View view;

    RecyclerView RCSolicitud;
    private AdapterSolicitud adapterSolicitud;

    FirebaseFirestore fdb;
    FirebaseUser FUser;

    private List<ModelSolicitud> mListSolicitud;

    private TextView textAviso;

    private String idProf;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_solicitudes, container, false);

        RCSolicitud = (RecyclerView) view.findViewById(R.id.RCSolicitud);
        textAviso = (TextView) view.findViewById(R.id.textAviso);

        fdb = FirebaseFirestore.getInstance();
        FUser = FirebaseAuth.getInstance().getCurrentUser();
        idProf = FUser.getUid();


        mListSolicitud = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);


        RCSolicitud.setLayoutManager(layoutManager);

        //CrearTTutorias();


        adapterSolicitud = new AdapterSolicitud(mListSolicitud);

        RCSolicitud.setAdapter(adapterSolicitud);
        upSolicitudes();
        adapterSolicitud.notifyDataSetChanged();

        return view;
    }


    private void upSolicitudes() {
        checkSize();
        CollectionReference ref = fdb.collection("Solicitudes").document(idProf).collection("Recibidas");
        ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i("Listen failed.", "" + e);
                    return;
                }

                for (DocumentChange dc : snapshot.getDocumentChanges()) {
                    DocumentSnapshot docS = dc.getDocument();

                    ModelSolicitud modelSoli = docS.toObject(ModelSolicitud.class);
                    int index = -1;
                    switch (dc.getType()) {
                        case ADDED:


                            mListSolicitud.add(new ModelSolicitud(docS.getId(), modelSoli.getEmisor(), modelSoli.getEstado(), modelSoli.getTipouser()));
                            adapterSolicitud.notifyDataSetChanged();
                            checkSize();

                            break;
                        case MODIFIED:
                            index = getRCIndex(docS.getId());

                            mListSolicitud.set(index, new ModelSolicitud(docS.getId(), modelSoli.getEmisor(), modelSoli.getEstado(), modelSoli.getTipouser()));
                            adapterSolicitud.notifyItemChanged(index);
                            checkSize();
                            break;
                        case REMOVED:


                            index = getRCIndex(docS.getId());

                            mListSolicitud.remove(index);
                            adapterSolicitud.notifyItemRemoved(index);
                            checkSize();

                            break;
                    }
                }

            }
        });


    }

    private void checkSize() {
        if (mListSolicitud.isEmpty()) {
            textAviso.setVisibility(View.VISIBLE);
        } else {
            textAviso.setVisibility(View.INVISIBLE);
        }
    }

    private int getRCIndex(String idSolicitud) {

        int index = -1;
        for (int i = 0; i < mListSolicitud.size(); i++) {
            if (mListSolicitud.get(i).idSolicitud.equals(idSolicitud)) {

                index = i;
                break;
            }
        }

        return index;

    }

}
