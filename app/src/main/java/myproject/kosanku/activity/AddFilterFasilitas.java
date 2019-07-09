package myproject.kosanku.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.kosanku.Kelas.Fasilitas;
import myproject.kosanku.Kelas.FilterFasilitas;
import myproject.kosanku.Kelas.SharedVariable;
import myproject.kosanku.R;

public class AddFilterFasilitas extends AppCompatActivity {

    Button btnSelesai;
    CheckBox checkAc,checkKmrMandi,checkWifi,checkKasur,checkLemari,checkMeja;
    FirebaseFirestore firestore;
    private SweetAlertDialog pDialogLoading,pDialodInfo;
    CollectionReference ref,refFasilitas;
    private String idAC,idKmrMandi,idLemari,idKasur,idWifi,idMeja;
    EditText etIdkos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_filter_fasilitas);
        Firebase.setAndroidContext(AddFilterFasilitas.this);
        FirebaseApp.initializeApp(AddFilterFasilitas.this);
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("filterFasilitas");

        etIdkos = findViewById(R.id.etIdkos);

        idAC = "AC";
        idKasur = "Kasur";
        idKmrMandi = "KmrMandi";
        idLemari = "Lemari";
        idWifi = "Wifi";
        idMeja = "Meja";

        checkAc = findViewById(R.id.checkAC);
        checkKmrMandi = findViewById(R.id.checkKmrMandi);
        checkWifi = findViewById(R.id.checkWifi);
        checkKasur = findViewById(R.id.checkKasur);
        checkLemari = findViewById(R.id.checkLemari);
        checkMeja = findViewById(R.id.checkMeja);
        btnSelesai = findViewById(R.id.btnSelesai);

        btnSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpanFasilitas();
            }
        });
    }

    private void simpanFasilitas(){
        if (checkAc.isChecked()){

            FilterFasilitas filterFasilitas = new FilterFasilitas(
                    etIdkos.getText().toString(),
                    "AC");
            String id =  ref.document().getId();
            ref.document(id).set(filterFasilitas).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("filterFasilitas:",etIdkos.getText().toString()+" | AC Berhasil");
                }
            });
        }
        if (checkKmrMandi.isChecked()){
            FilterFasilitas filterFasilitas = new FilterFasilitas(
                    etIdkos.getText().toString(),
                    "KmrMandi");
            String id =  ref.document().getId();
            ref.document(id).set(filterFasilitas).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("filterFasilitas:",etIdkos.getText().toString()+" | kamarmandi Berhasil");
                }
            });
        }
        if (checkWifi.isChecked()){
            FilterFasilitas filterFasilitas = new FilterFasilitas(
                    etIdkos.getText().toString(),
                    "Wifi");
            String id =  ref.document().getId();
            ref.document(id).set(filterFasilitas).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("filterFasilitas:",etIdkos.getText().toString()+" | Wifi Berhasil");
                }
            });
        }
        if (checkLemari.isChecked()){
            FilterFasilitas filterFasilitas = new FilterFasilitas(
                    etIdkos.getText().toString(),
                    "Lemari");
            String id =  ref.document().getId();
            ref.document(id).set(filterFasilitas).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("filterFasilitas:",etIdkos.getText().toString()+" | Lemari Berhasil");
                }
            });
        }
        if (checkKasur.isChecked()){
            FilterFasilitas filterFasilitas = new FilterFasilitas(
                    etIdkos.getText().toString(),
                    "Kasur");
            String id =  ref.document().getId();
            ref.document(id).set(filterFasilitas).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("filterFasilitas:",etIdkos.getText().toString()+" | Kasur Berhasil");
                }
            });
        }
        if (checkMeja.isChecked()){
            FilterFasilitas filterFasilitas = new FilterFasilitas(
                    etIdkos.getText().toString(),
                    "Meja");
            String id =  ref.document().getId();
            ref.document(id).set(filterFasilitas).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                   Log.d("filterFasilitas:",etIdkos.getText().toString()+" | Meja Berhasil");
                }
            });
        }


    }
}
