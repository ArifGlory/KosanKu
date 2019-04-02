package myproject.kosanku.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.kosanku.Kelas.Kosan;
import myproject.kosanku.R;
import myproject.kosanku.adapter.AdapterKosan;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHome extends Fragment {


    public FragmentHome() {
        // Required empty public constructor
    }


    RecyclerView recyclerView;
    FirebaseFirestore firestore;

    private SweetAlertDialog pDialogLoading,pDialodInfo;
    AdapterKosan adapter;
    private List<Kosan> kosanList;
    CollectionReference ref;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_beranda, container, false);
        Firebase.setAndroidContext(this.getActivity());
        FirebaseApp.initializeApp(this.getActivity());
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("kosan");

        recyclerView = view.findViewById(R.id.rvFeed);
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

        getDataKosan();

        return view;
    }

    private void getDataKosan(){


        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                kosanList.clear();
                adapter.notifyDataSetChanged();

                if (task.isSuccessful()){
                    pDialogLoading.dismiss();
                    for (DocumentSnapshot doc : task.getResult()){
                        Kosan kosan = doc.toObject(Kosan.class);

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
