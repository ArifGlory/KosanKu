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
    CollectionReference ref;
    Intent intent;
    private String tipe;
    private Toolbar my_toolbar;
    TextView txtInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Firebase.setAndroidContext(getApplicationContext());
        FirebaseApp.initializeApp(getApplicationContext());
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("kosan");


        intent = getIntent();
        tipe = intent.getStringExtra("tipe");

        my_toolbar = findViewById(R.id.my_toolbar2);
        setSupportActionBar(my_toolbar);

        txtInfo = findViewById(R.id.txtInfo);
        txtInfo.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.rvFeed);

        kosanList = new ArrayList<>();
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
        }
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
}
