package myproject.kosanku.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
import myproject.kosanku.Kelas.FilterFasilitas;
import myproject.kosanku.Kelas.Kosan;
import myproject.kosanku.Kelas.SharedVariable;
import myproject.kosanku.R;
import myproject.kosanku.adapter.AdapterKosan;

public class ResultActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseFirestore firestore;

    private SweetAlertDialog pDialogLoading,pDialodInfo;
    AdapterKosan adapter;
    private List<Kosan> kosanList;
    private List<String> listKos;
    CollectionReference ref,refFasilitas;
    Intent intent;
    private String tipe;
    private Toolbar my_toolbar;
    TextView txtInfo;
    private List<String> listIdKosan = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Firebase.setAndroidContext(getApplicationContext());
        FirebaseApp.initializeApp(getApplicationContext());
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("kosan");
        refFasilitas = firestore.collection("filterFasilitas");

        intent = getIntent();
        tipe = intent.getStringExtra("tipe");

        my_toolbar = findViewById(R.id.my_toolbar2);
        setSupportActionBar(my_toolbar);

        txtInfo = findViewById(R.id.txtInfo);
        txtInfo.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.rvFeed);

        kosanList = new ArrayList<>();
        listKos = new ArrayList<>();
        adapter = new AdapterKosan(getApplicationContext(),kosanList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        pDialogLoading = new SweetAlertDialog(ResultActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);
        pDialogLoading.show();

        if (tipe.equals("sort")){
            if (SharedVariable.sortType.equals("desc")){
                getSupportActionBar().setTitle("Kosan terbaru");
                getDataSortDesc();
            }else if (SharedVariable.sortType.equals("asc")){
                getSupportActionBar().setTitle("Kosan terlama");
                getDataSortAsc();
            }
        } else if (tipe.equals("harga")){
            getSupportActionBar().setTitle("Filter Harga");
            getDataByHarga();
        }else if (tipe.equals("fasilitas")){
            getSupportActionBar().setTitle("Filter Fasilitas");
            getDataByFasilitas();

        }else if (tipe.equals("search")){
            getSupportActionBar().setTitle("Filter Alamat");
            getFilteredSearch();
        }
    }

    public void getFilteredSearch(){
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){

                    for (DocumentSnapshot doc : task.getResult()){

                        String alamat = doc.get("alamat").toString();
                        alamat = alamat.toLowerCase();
                        String keyword = SharedVariable.keyword;
                        keyword = keyword.toLowerCase();

                        if (alamat.contains(keyword)){
                            Log.d("filterSearch:","data ditemukan!, alamat :"+alamat);
                            Kosan kosan = doc.toObject(Kosan.class);
                            if ( kosan.getTipeBayar() == null ||kosan.getTipeBayar().equals("") || kosan.getTipeBayar().length() == 0 ){
                                kosan.setTipeBayar("-");
                            }
                            kosanList.add(kosan);
                        }

                    }
                    adapter.notifyDataSetChanged();
                    pDialogLoading.dismiss();
                }else {
                    pDialogLoading.dismiss();
                    new SweetAlertDialog(ResultActivity.this,SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Terjadi kesalahan,coba lagi nanti")
                            .show();
                    Log.d("gagalGetData:",task.getException().toString());
                }
            }
        });
    }

    public void getDataSortDesc(){
        ref.orderBy("idKos",Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                kosanList.clear();
                adapter.notifyDataSetChanged();

                if (task.isSuccessful()){
                    pDialogLoading.dismiss();
                    for (DocumentSnapshot doc : task.getResult()){
                        Kosan kosan = doc.toObject(Kosan.class);
                        if ( kosan.getTipeBayar() == null ||kosan.getTipeBayar().equals("") || kosan.getTipeBayar().length() == 0 ){
                            kosan.setTipeBayar("-");
                        }
                        kosanList.add(kosan);
                        adapter.notifyDataSetChanged();
                    }
                }else {
                    pDialogLoading.dismiss();
                    new SweetAlertDialog(ResultActivity.this,SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Pengambilan data gagal")
                            .show();
                    Log.d("gagalGetData:",task.getException().toString());
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pDialogLoading.dismiss();
                new SweetAlertDialog(ResultActivity.this,SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Pengambilan data gagal")
                        .show();
                Log.d("gagalGetData:",e.toString());
            }
        });
    }

    public void getDataSortAsc(){
        ref.orderBy("idKos",Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                kosanList.clear();
                adapter.notifyDataSetChanged();

                if (task.isSuccessful()){
                    pDialogLoading.dismiss();
                    for (DocumentSnapshot doc : task.getResult()){
                        Kosan kosan = doc.toObject(Kosan.class);
                        if ( kosan.getTipeBayar() == null ||kosan.getTipeBayar().equals("") || kosan.getTipeBayar().length() == 0 ){
                            kosan.setTipeBayar("-");
                        }
                        kosanList.add(kosan);
                        adapter.notifyDataSetChanged();
                    }
                }else {
                    pDialogLoading.dismiss();
                    new SweetAlertDialog(ResultActivity.this,SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Pengambilan data gagal")
                            .show();
                    Log.d("gagalGetData:",task.getException().toString());
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pDialogLoading.dismiss();
                new SweetAlertDialog(ResultActivity.this,SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Pengambilan data gagal")
                        .show();
                Log.d("gagalGetData:",e.toString());
            }
        });
    }

    public void getDataByHarga(){
        ref.whereLessThan("harga",SharedVariable.maxHarga).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                kosanList.clear();
                adapter.notifyDataSetChanged();

                QuerySnapshot dc = task.getResult();
                if (dc.isEmpty()){
                    txtInfo.setVisibility(View.VISIBLE);
                }

                if (task.isSuccessful()){
                    pDialogLoading.dismiss();
                    for (DocumentSnapshot doc : task.getResult()){
                        Kosan kosan = doc.toObject(Kosan.class);
                        if ( kosan.getTipeBayar() == null ||kosan.getTipeBayar().equals("") || kosan.getTipeBayar().length() == 0 ){
                            kosan.setTipeBayar("-");
                        }
                        kosanList.add(kosan);
                        adapter.notifyDataSetChanged();
                    }
                }else {
                    pDialogLoading.dismiss();
                    new SweetAlertDialog(ResultActivity.this,SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Pengambilan data gagal")
                            .show();
                    Log.d("gagalGetData:",task.getException().toString());
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pDialogLoading.dismiss();
                new SweetAlertDialog(ResultActivity.this,SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Pengambilan data gagal")
                        .show();
                Log.d("gagalGetData:",e.toString());
            }
        });
    }

    public void getDataByFasilitas(){
        listIdKosan.clear();
        refFasilitas.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){

                    for (DocumentSnapshot doc : task.getResult()){
                        FilterFasilitas filterFasilitas = doc.toObject(FilterFasilitas.class);

                        if (SharedVariable.listFiltered.contains(filterFasilitas.idFasilitas)){

                            if (!listIdKosan.contains(filterFasilitas.idKos)){
                                listIdKosan.add(filterFasilitas.idKos);
                            }
                        }
                    }

                    if (listIdKosan.isEmpty()){
                        pDialogLoading.dismiss();
                        new SweetAlertDialog(ResultActivity.this,SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText("Kosong")
                                .setContentText("Belum ada kosan dengan kriteria tersebut")
                                .show();
                    }else {
                        for (int c = 0;c<listIdKosan.size();c++){
                            Log.d("filteredByFasilitas:","idKos : "+listIdKosan.get(c).toString());
                        }
                        getDataFilteredKosanByFasilitas();
                    }


                }else {
                    pDialogLoading.dismiss();
                    new SweetAlertDialog(ResultActivity.this,SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("terjadi kesalahan")
                            .setContentText("coba lagi nanti / periksa koneksi anda")
                            .show();
                    Log.d("erorGetKosan:",""+task.getException().getMessage());
                }
            }
        });
    }

    public void getDataFilteredKosanByFasilitas(){


        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                kosanList.clear();
                adapter.notifyDataSetChanged();

                if (task.isSuccessful()){
                    for (DocumentSnapshot doc : task.getResult()){
                        String idKos = doc.getId();


                        if (listIdKosan.contains(idKos)){
                            Kosan kosan = doc.toObject(Kosan.class);
                            if ( kosan.getTipeBayar() == null ||kosan.getTipeBayar().equals("") || kosan.getTipeBayar().length() == 0 ){
                                kosan.setTipeBayar("-");
                            }
                            kosanList.add(kosan);
                            adapter.notifyDataSetChanged();
                        }

                    }
                    pDialogLoading.dismiss();
                }
            }
        });

    }

    public void getDataKosByString(){
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                kosanList.clear();
                adapter.notifyDataSetChanged();

                if (task.isSuccessful()){

                    QuerySnapshot dc = task.getResult();
                    if (dc.isEmpty()){
                        txtInfo.setVisibility(View.VISIBLE);
                    }else {

                        for (DocumentSnapshot doc : task.getResult()){
                            String idKos = doc.get("idKos").toString();

                            //membandingkan dengan isi array kosan yang punya fasilits tsb
                            for (int c=0;c<listKos.size();c++){
                                if (idKos.equals(listKos.get(c).toString())){
                                    Kosan kosan = doc.toObject(Kosan.class);

                                    kosanList.add(kosan);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }

                }else {
                    new SweetAlertDialog(ResultActivity.this,SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("terjadi kesalahan")
                            .setContentText("coba lagi nanti / periksa koneksi anda")
                            .show();
                    Log.d("erorGetKosan:",""+task.getException().getMessage());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                new SweetAlertDialog(ResultActivity.this,SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("terjadi kesalahan")
                        .setContentText("coba lagi nanti / periksa koneksi anda")
                        .show();
                Log.d("erorGetKosan:",""+e.getMessage().toString());
            }
        });
    }


}
