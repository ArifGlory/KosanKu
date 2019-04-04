package myproject.kosanku.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.kosanku.Kelas.Fasilitas;
import myproject.kosanku.Kelas.Kosan;
import myproject.kosanku.R;
import myproject.kosanku.adapter.AdapterFasilitas;
import myproject.kosanku.adapter.AdapterKosan;

public class DetailKosActivity extends AppCompatActivity {

    TextView txtNamaKos,txtHarga,txtSisaKamar;
    ImageView imgKos;
    Button btnHubungi,btnLokasi;
    RecyclerView recycler_view;
    Intent intent;
    FirebaseFirestore firestore;

    private SweetAlertDialog pDialogLoading,pDialodInfo;
    CollectionReference ref,refPemilik;
    Kosan kosan;
    AdapterFasilitas adapter;
    private List<Fasilitas> fasilitasList;
    private String nomorPemilik = "no";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_kos);
        Firebase.setAndroidContext(DetailKosActivity.this);
        FirebaseApp.initializeApp(DetailKosActivity.this);
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("kosan");
        refPemilik = firestore.collection("users");


        intent = getIntent();
        kosan = (Kosan) intent.getSerializableExtra("kosan");

        fasilitasList = new ArrayList<>();
        adapter = new AdapterFasilitas(DetailKosActivity.this,fasilitasList);

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.ENGLISH);
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        pDialogLoading = new SweetAlertDialog(DetailKosActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);
        pDialogLoading.show();


        imgKos = findViewById(R.id.backdrop);
        txtNamaKos = findViewById(R.id.txtNamaKos);
        txtHarga = findViewById(R.id.txtHarga);
        txtSisaKamar = findViewById(R.id.txtSisaKamar);
        btnHubungi = findViewById(R.id.btnHubungi);
        btnLokasi = findViewById(R.id.btnLokasi);
        recycler_view = findViewById(R.id.recycler_view);

        recycler_view.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recycler_view.setHasFixedSize(true);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setAdapter(adapter);


        txtNamaKos.setText(kosan.getNamaKos());
        txtHarga.setText(formatRupiah.format((double) kosan.getHarga()));
        txtSisaKamar.setText("Sisa Kamar : "+kosan.getSisaKamar());

        Glide.with(this)
                .load(kosan.getGambarUtama())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgKos);

        btnLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("latlon",kosan.getLatlon());
                startActivity(intent);
            }
        });
        btnHubungi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nomorPemilik.equals("no")){
                    getDataPemilik();
                }else {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", nomorPemilik, null));
                    startActivity(intent);
                }

            }
        });

        getDataFasilitas();
        getDataPemilik();

    }

    private void getDataFasilitas(){

        ref.document(kosan.getIdKos()).collection("listFasilitas").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                fasilitasList.clear();
                adapter.notifyDataSetChanged();

                QuerySnapshot dc = task.getResult();
                pDialogLoading.dismiss();
                if (dc.isEmpty()){
                    Log.d("fasilitas:","data fasilitas kosong");
                }else {

                    if (task.isSuccessful()){
                        for (DocumentSnapshot doc : task.getResult()){
                            Fasilitas fasilitas = doc.toObject(Fasilitas.class);

                            fasilitasList.add(fasilitas);
                            adapter.notifyDataSetChanged();
                        }
                    }else {
                        pDialogLoading.dismiss();
                        new SweetAlertDialog(DetailKosActivity.this,SweetAlertDialog.ERROR_TYPE)
                                .setContentText("Pengambilan data gagal")
                                .show();
                        Log.d("gagalGetData:",task.getException().toString());
                    }

                }
            }
        });

    }

    private void getDataPemilik(){
        refPemilik.document(kosan.getUidPemilik()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot dc = task.getResult();
                nomorPemilik = dc.get("nope").toString();
            }
        });
    }
}
