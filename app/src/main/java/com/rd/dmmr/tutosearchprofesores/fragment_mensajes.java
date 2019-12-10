package com.rd.dmmr.tutosearchprofesores;


import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class fragment_mensajes extends Fragment {


    View view;
    FirebaseAuth FAuth;
    FirebaseUser FUser;
    RecyclerView recyclerView;
    FirebaseFirestore fdb;
    List<ModelChatList> modelChatLists;
    AdapterChatList adapterChatList;

    private String myUID;
    private List<ModelChat> mChatList;


    public fragment_mensajes() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mensajes, container, false);


        FAuth = FirebaseAuth.getInstance();
        FUser = FAuth.getCurrentUser();
        myUID = FUser.getUid();
        recyclerView = view.findViewById(R.id.RCMensajesList);
        modelChatLists = new ArrayList<>();

        mChatList = new ArrayList<>();

        fdb = FirebaseFirestore.getInstance();

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        adapterChatList = new AdapterChatList(getContext(),modelChatLists);
        recyclerView.setAdapter(adapterChatList);

        CollectionReference collectionReference = fdb.collection("ListChat").document(FUser.getUid()).collection("lista");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    DocumentSnapshot docS = dc.getDocument();
                    int index = -1;
                    switch (dc.getType()) {
                        case ADDED:

                            modelChatLists.add(new ModelChatList(docS.getId(), docS.getString("tipoUser")));
                            Log.i("ultimos", "Lista: "+modelChatLists.get(0).tipoUser);
                            adapterChatList.notifyDataSetChanged();
                            ObtenerUltimoMensaje(docS.getId());



                            break;
                        case MODIFIED:


                            break;
                        case REMOVED:


                            break;
                    }
                }

            }
        });


        return view;
    }

    private void ObtenerUltimoMensaje(String suUID) {

        Query ref = fdb.collection("Mensajes").orderBy("timestamp", Query.Direction.ASCENDING);
        ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i("Listen failed.", "" + e);
                    return;
                }

                final String[] ultimo = {""};

                for (DocumentChange dc : snapshot.getDocumentChanges()) {
                    final DocumentSnapshot docS = dc.getDocument();
                    Handler handler = new Handler();
                    final ModelChat modelChat = docS.toObject(ModelChat.class);
                    switch (dc.getType()) {
                        case ADDED:

                            Log.i("ultimosPrueb", ""+modelChat.getReceptor().equals(myUID));


                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (modelChat.getReceptor().equals(myUID) && modelChat.getEmisor().equals(suUID) ||
                                            modelChat.getReceptor().equals(suUID) && modelChat.getEmisor().equals(myUID)) {
                                        Log.i("ultimosPrueb", "Entra a la condicion, mensaje: "+modelChat.getMensaje());
                                        // mChatList.add(new ModelChat(docS.getId(), docS.getString("mensaje"), docS.getString("emisor"), docS.getString("receptor"), docS.getString("timestamp"), docS.getBoolean("visto")));
                                        ultimo[0] = modelChat.getMensaje();
                                        adapterChatList.setUltimoMensaje(suUID, ultimo[0]);
                                        adapterChatList.notifyDataSetChanged();
                                    }

                                }
                            },500);

                            break;
                        case MODIFIED:

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (modelChat.getReceptor().equals(myUID) && modelChat.getEmisor().equals(suUID) ||
                                            modelChat.getReceptor().equals(suUID) && modelChat.getEmisor().equals(myUID)) {
                                        Log.i("ultimosPrueb", "Entra a la condicion, mensaje: "+modelChat.getMensaje());
                                        // mChatList.add(new ModelChat(docS.getId(), docS.getString("mensaje"), docS.getString("emisor"), docS.getString("receptor"), docS.getString("timestamp"), docS.getBoolean("visto")));
                                        ultimo[0] = modelChat.getMensaje();
                                        adapterChatList.setUltimoMensaje(suUID, ultimo[0]);
                                        adapterChatList.notifyDataSetChanged();
                                    }

                                }
                            },500);


                            break;
                        case REMOVED:


                            break;
                    }
                }

                Log.i("ultimos", "" + ultimo[0]);





            }
        });

    }

}
