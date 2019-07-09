package myproject.kosanku.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.kosanku.Kelas.Kosan;
import myproject.kosanku.Kelas.SharedVariable;
import myproject.kosanku.R;
import myproject.kosanku.activity.TambahKosActivity;
import myproject.kosanku.adapter.AdapterKosan;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHomePemilik extends Fragment {


    public FragmentHomePemilik() {
        // Required empty public constructor
    }


    RecyclerView recyclerView;
    FirebaseFirestore firestore;
    FloatingActionButton btnCreate;
    private SweetAlertDialog pDialogLoading,pDialodInfo;
    AdapterKosan adapter;
    private List<Kosan> kosanList;

    CollectionReference ref;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_home_pemilik, container, false);
        Firebase.setAndroidContext(this.getActivity());
        FirebaseApp.initializeApp(this.getActivity());
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("kosan");

        recyclerView = view.findViewById(R.id.rvHomePemilik);
        btnCreate = view.findViewById(R.id.btnCreate);

        kosanList = new ArrayList<>();
        adapter = new AdapterKosan(getActivity(),kosanList);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        pDialogLoading = new SweetAlertDialog(this.getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);
        pDialogLoading.show();

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),TambahKosActivity.class);
                startActivity(intent);
            }
        });

        getDataKosan();

        return view;
    }

    public void getDataKosan(){


        ref.whereEqualTo("uidPemilik",SharedVariable.userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                kosanList.clear();
                adapter.notifyDataSetChanged();

                if (task.isSuccessful()){
                    pDialogLoading.dismiss();
                    for (DocumentSnapshot doc : task.getResult()){
                        String tipeBayar = "";
                        if (doc.get("tipeBayar") != null ){
                            tipeBayar = doc.get("tipeBayar").toString();

                        }else {
                            tipeBayar ="-";
                        }
                        Log.d("tipeBayar:",tipeBayar);
                        Kosan kosan = doc.toObject(Kosan.class);
                        kosan.setTipeBayar(tipeBayar);

                        kosanList.add(kosan);
                        adapter.notifyDataSetChanged();
                    }
                }else {
                    pDialogLoading.dismiss();
                    new SweetAlertDialog(getActivity(),SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Pengambilan data gagal")
                            .show();
                    Log.d("gagalGetData:",task.getException().toString());
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pDialogLoading.dismiss();
                new SweetAlertDialog(getActivity(),SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Pengambilan data gagal")
                        .show();
                Log.d("gagalGetData:",e.toString());
            }
        });
    }





}
