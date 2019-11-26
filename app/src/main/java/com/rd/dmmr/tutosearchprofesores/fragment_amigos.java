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
public class fragment_amigos extends Fragment {


    public fragment_amigos() {
        // Required empty public constructor
    }

    View view;

    RecyclerView RCAmigos;
    private AdapterAmigos adapterAmigos;

    FirebaseFirestore fdb;
    FirebaseUser FUser;

    private List<ModelAmigos> mListAmigos;

    private TextView textAviso;

    private String idProf;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_amigos, container, false);

        RCAmigos = (RecyclerView) view.findViewById(R.id.RCAmigos);
        textAviso = (TextView) view.findViewById(R.id.textAvisoFriend);

        fdb = FirebaseFirestore.getInstance();
        FUser = FirebaseAuth.getInstance().getCurrentUser();
        idProf = FUser.getUid();


        mListAmigos = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);


        RCAmigos.setLayoutManager(layoutManager);

        //CrearTTutorias();


        adapterAmigos = new AdapterAmigos(mListAmigos);

        RCAmigos.setAdapter(adapterAmigos);
        upAmigos();
        adapterAmigos.notifyDataSetChanged();


        return view;
    }

    private void upAmigos() {
        checkSize();

        CollectionReference ref = fdb.collection("Amigos").document(idProf).collection("Aceptados");
        ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i("Listen failed.", "" + e);
                    return;
                }

                for (DocumentChange dc : snapshot.getDocumentChanges()) {
                    DocumentSnapshot docS = dc.getDocument();

                    ModelAmigos modelAmi= docS.toObject(ModelAmigos.class);
                    int index = -1;
                    switch (dc.getType()) {
                        case ADDED:


                            mListAmigos.add(new ModelAmigos(docS.getId(),modelAmi.getTipoUser()));
                            adapterAmigos.notifyDataSetChanged();
                            checkSize();

                            break;
                        case MODIFIED:
                            index = getRCIndex(docS.getId());

                            mListAmigos.set(index, new ModelAmigos(docS.getId(),modelAmi.getTipoUser()));
                            adapterAmigos.notifyItemChanged(index);
                            checkSize();
                            break;
                        case REMOVED:


                            index = getRCIndex(docS.getId());

                            mListAmigos.remove(index);
                            adapterAmigos.notifyItemRemoved(index);
                            checkSize();

                            break;
                    }
                }

            }
        });



    }

    private int getRCIndex(String idAmi) {

        int index = -1;
        for (int i = 0; i < mListAmigos.size(); i++) {
            if (mListAmigos.get(i).idAmigo.equals(idAmi)) {

                index = i;
                break;
            }
        }

        return index;

    }

    private void checkSize() {
        if (mListAmigos.isEmpty()) {
            textAviso.setVisibility(View.VISIBLE);
        } else {
            textAviso.setVisibility(View.INVISIBLE);
        }
    }

}
