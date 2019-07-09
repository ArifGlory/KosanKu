package myproject.kosanku.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.kosanku.Kelas.Fasilitas;
import myproject.kosanku.Kelas.FilterFasilitas;
import myproject.kosanku.Kelas.SharedVariable;
import myproject.kosanku.R;

public class PilihFasilitasActivity extends AppCompatActivity {

    Button btnSelesai;
    CheckBox checkAc,checkKmrMandi,checkWifi,checkKasur,checkLemari,checkMeja;
    FirebaseFirestore firestore;
    private SweetAlertDialog pDialogLoading,pDialodInfo;
    CollectionReference ref,refFasilitas,refFilter;
    private String idAC,idKmrMandi,idLemari,idKasur,idWifi,idMeja;
   
   private String filtered = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_fasilitas);
        Firebase.setAndroidContext(PilihFasilitasActivity.this);
        FirebaseApp.initializeApp(PilihFasilitasActivity.this);
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("kosan");
        refFasilitas = firestore.collection("fasilitas");
        refFilter = firestore.collection("filterFasilitas");

        idAC = "AC";
        idKasur = "Kasur";
        idKmrMandi = "KmrMandi";
        idLemari = "Lemari";
        idWifi = "Wifi";
        idMeja = "Meja";

        checkAc = findViewById(R.id.checkAC);
        checkKmrMandi = findViewById(R.id.checkKmrMandi);
        checkKmrMandi = findViewById(R.id.checkKmrMandi);
        checkWifi = findViewById(R.id.checkWifi);
        checkKasur = findViewById(R.id.checkKasur);
        checkLemari = findViewById(R.id.checkLemari);
        checkMeja = findViewById(R.id.checkMeja);
        btnSelesai = findViewById(R.id.btnSelesai);

        btnSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SharedVariable.akses.equals("Pemilik")){
                    checkValidation();
                }else {
                    getFilteredKosan();
                }


            }
        });


    }

    private void getFilteredKosan(){
        SharedVariable.listFiltered.clear();
        if (checkAc.isChecked()){
            SharedVariable.listFiltered.add("AC");
        }
        if (checkKasur.isChecked()){
            SharedVariable.listFiltered.add("Kasur");
        }
        if (checkKmrMandi.isChecked()){
            SharedVariable.listFiltered.add("KmrMandi");
        }
        if (checkLemari.isChecked()){
            SharedVariable.listFiltered.add("Lemari");
        }
        if (checkWifi.isChecked()){
            SharedVariable.listFiltered.add("Wifi");
        }
        if (checkMeja.isChecked()){
            SharedVariable.listFiltered.add("Meja");
        }

        Intent intent = new Intent(getApplicationContext(),ResultActivity.class);
        intent.putExtra("tipe","fasilitas");
        startActivity(intent);
    }

    private void checkValidation(){
        simpanFasilitas();
       /* if (!checkKasur.isChecked() || !checkWifi.isChecked() || !checkWifi.isChecked() ||
                !checkLemari.isChecked() || !checkKmrMandi.isChecked() ){
            new SweetAlertDialog(PilihFasilitasActivity.this,SweetAlertDialog.ERROR_TYPE)
                    .setContentText("Fasilitas harus diisi minimal 1")
                    .setTitleText("Oops..")
                    .setConfirmText("OK")
                    .show();
        }else{
            simpanFasilitas();
        }*/
    }

    private void simpanFasilitas(){
        if (checkAc.isChecked()){
            Fasilitas fasilitasAc = new Fasilitas(idAC,"Fasilitas AC");

            ref.document(SharedVariable.idKos).collection("listFasilitas").document(idAC).set(fasilitasAc).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("fasilitasKos:","Fasilitas Ac disimpan");
                }
            });

            refFasilitas.document(idAC).collection("listKos").document(SharedVariable.idKos).set(fasilitasAc)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("fasilitas:","Fasilitas Ac disimpan");
                }
            });

            //simpan ke filterfasilitas
            FilterFasilitas filterFasilitas = new FilterFasilitas(
                    SharedVariable.idKos,
                    "AC");
            String id =  ref.document().getId();
            refFilter.document(id).set(filterFasilitas).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("filterFasilitas:",SharedVariable.idKos+" | AC Berhasil");
                }
            });

        }
        if (checkKmrMandi.isChecked()){
            Fasilitas fasilitas= new Fasilitas(idKmrMandi,"Fasilitas Kamar Mandi");

            ref.document(SharedVariable.idKos).collection("listFasilitas").document(idKmrMandi).set(fasilitas).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("fasilitasKos:","Fasilitas  ac disimpan");
                }
            });

            refFasilitas.document(idKmrMandi).collection("listKos").document(SharedVariable.idKos).set(fasilitas)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("fasilitas:","Fasilitas Kamar Mandi disimpan");
                        }
                    });

            //simpan ke filterfasilitas
            FilterFasilitas filterFasilitas = new FilterFasilitas(
                    SharedVariable.idKos,
                    "KmrMandi");
            String id =  ref.document().getId();
            refFilter.document(id).set(filterFasilitas).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("filterFasilitas:",SharedVariable.idKos+" | KmrMandi Berhasil");
                }
            });
        }
        if (checkWifi.isChecked()){
            Fasilitas fasilitas = new Fasilitas(idWifi,"Fasilitas Free Wifi");

            ref.document(SharedVariable.idKos).collection("listFasilitas").document(idWifi).set(fasilitas).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("fasilitasKos:","Fasilitas wifi disimpan");
                }
            });

            refFasilitas.document(idWifi).collection("listKos").document(SharedVariable.idKos).set(fasilitas)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("fasilitas:","Fasilitas Wifi disimpan");
                        }
                    });

            //simpan ke filterfasilitas
            FilterFasilitas filterFasilitas = new FilterFasilitas(
                    SharedVariable.idKos,
                    "Wifi");
            String id =  ref.document().getId();
            refFilter.document(id).set(filterFasilitas).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("filterFasilitas:",SharedVariable.idKos+" | Wifi Berhasil");
                }
            });
        }
        if (checkLemari.isChecked()){
            Fasilitas fasilitas = new Fasilitas(idLemari,"Fasilitas Lemari");

            ref.document(SharedVariable.idKos).collection("listFasilitas").document(idLemari).set(fasilitas).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("fasilitasKos:","Fasilitas lemari disimpan");
                }
            });

            refFasilitas.document(idLemari).collection("listKos").document(SharedVariable.idKos).set(fasilitas)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("fasilitas:","Fasilitas Lemari disimpan");
                        }
                    });

            //simpan ke filterfasilitas
            FilterFasilitas filterFasilitas = new FilterFasilitas(
                    SharedVariable.idKos,
                    "Lemari");
            String id =  ref.document().getId();
            refFilter.document(id).set(filterFasilitas).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("filterFasilitas:",SharedVariable.idKos+" | lemari Berhasil");
                }
            });
        }
        if (checkKasur.isChecked()){
            Fasilitas fasilitas = new Fasilitas(idKasur,"Fasilitas Kasur");

            ref.document(SharedVariable.idKos).collection("listFasilitas").document(idKasur).set(fasilitas).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("fasilitasKos:","Fasilitas kasur disimpan");
                }
            });

            refFasilitas.document(idKasur).collection("listKos").document(SharedVariable.idKos).set(fasilitas)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("fasilitas:","Fasilitas Kasur disimpan");
                        }
                    });

            //simpan ke filterfasilitas
            FilterFasilitas filterFasilitas = new FilterFasilitas(
                    SharedVariable.idKos,
                    "Kasur");
            String id =  ref.document().getId();
            refFilter.document(id).set(filterFasilitas).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("filterFasilitas:",SharedVariable.idKos+" | kasur Berhasil");
                }
            });
        }

        if (checkMeja.isChecked()){
            Fasilitas fasilitas = new Fasilitas(idMeja,"Fasilitas Meja");

            ref.document(SharedVariable.idKos).collection("listFasilitas").document(idMeja).set(fasilitas).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("fasilitasKos:","Fasilitas Meja disimpan");
                }
            });

            refFasilitas.document(idMeja).collection("listKos").document(SharedVariable.idKos).set(fasilitas)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("fasilitas:","Fasilitas Meja disimpan");
                        }
                    });

            //simpan ke filterfasilitas
            FilterFasilitas filterFasilitas = new FilterFasilitas(
                    SharedVariable.idKos,
                    "Meja");
            String id =  ref.document().getId();
            refFilter.document(id).set(filterFasilitas).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("filterFasilitas:",SharedVariable.idKos+" | Meja Berhasil");
                }
            });
        }

        new SweetAlertDialog(PilihFasilitasActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Sukses!")
                .setContentText("Data Kos Disimpan")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent intent = new Intent(getApplicationContext(),PemilikActivity.class);
                        startActivity(intent);
                    }
                })
                .show();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (SharedVariable.akses.equals("Pemilik")){
            new SweetAlertDialog(PilihFasilitasActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Lewati Pemilihan Fasilitas?")
                    .setContentText("Anda Yakin Ingin melewati penambahan fasilitas Kos?")
                    .setConfirmText("Ya")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {

                            Intent intent = new Intent(getApplicationContext(),PemilikActivity.class);
                            startActivity(intent);
                            sDialog.dismissWithAnimation();

                        }
                    })
                    .setCancelButton("Tidak", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();

                        }
                    })
                    .show();
        }else {
            Intent intent = new Intent(getApplicationContext(),BerandaActivity.class);
            startActivity(intent);
        }


    }
}
