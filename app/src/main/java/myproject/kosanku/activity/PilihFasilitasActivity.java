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

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.kosanku.Kelas.Fasilitas;
import myproject.kosanku.Kelas.SharedVariable;
import myproject.kosanku.R;

public class PilihFasilitasActivity extends AppCompatActivity {

    Button btnSelesai;
    CheckBox checkAc,checkKmrMandi,checkWifi,checkKasur,checkLemari;
    FirebaseFirestore firestore;
    private SweetAlertDialog pDialogLoading,pDialodInfo;
    CollectionReference ref;
    private String idAC,idKmrMandi,idLemari,idKasur,idWifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_fasilitas);
        Firebase.setAndroidContext(PilihFasilitasActivity.this);
        FirebaseApp.initializeApp(PilihFasilitasActivity.this);
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("kosan");

        idAC = "AC";
        idKasur = "Kasur";
        idKmrMandi = "KmrMandi";
        idLemari = "Lemari";
        idWifi = "Wifi";

        checkAc = findViewById(R.id.checkAC);
        checkKmrMandi = findViewById(R.id.checkKmrMandi);
        checkWifi = findViewById(R.id.checkWifi);
        checkKasur = findViewById(R.id.checkKasur);
        checkLemari = findViewById(R.id.checkLemari);
        btnSelesai = findViewById(R.id.btnSelesai);

        btnSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }
        });


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

            ref.document(SharedVariable.tempKosan.getIdKos()).collection("listFasilitas").document(idAC).set(fasilitasAc)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("fasilitas:","Fasilitas Ac disimpan");
                }
            });
        }
        if (checkKmrMandi.isChecked()){
            Fasilitas fasilitas= new Fasilitas(idKmrMandi,"Fasilitas Kamar Mandi");

            ref.document(SharedVariable.idKos).collection("listFasilitas").document(idKmrMandi).set(fasilitas)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("fasilitas:","Fasilitas KmrMandi disimpan");
                        }
                    });
        }
        if (checkWifi.isChecked()){
            Fasilitas fasilitas = new Fasilitas(idWifi,"Fasilitas Free Wifi");

            ref.document(SharedVariable.idKos).collection("listFasilitas").document(idWifi).set(fasilitas)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("fasilitas:","Fasilitas FreeWifi disimpan");
                        }
                    });
        }
        if (checkLemari.isChecked()){
            Fasilitas fasilitas = new Fasilitas(idLemari,"Fasilitas Lemari");

            ref.document(SharedVariable.idKos).collection("listFasilitas").document(idLemari).set(fasilitas)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("fasilitas:","Fasilitas Lemari disimpan");
                        }
                    });
        }
        if (checkKasur.isChecked()){
            Fasilitas fasilitas = new Fasilitas(idKasur,"Fasilitas Kasur");

            ref.document(SharedVariable.idKos).collection("listFasilitas").document(idKasur).set(fasilitas)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("fasilitas:","Fasilitas Kasur disimpan");
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
        //super.onBackPressed();
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
    }
}
