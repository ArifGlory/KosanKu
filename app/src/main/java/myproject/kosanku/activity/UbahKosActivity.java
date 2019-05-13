package myproject.kosanku.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.kosanku.Kelas.Kosan;
import myproject.kosanku.Kelas.SharedVariable;
import myproject.kosanku.R;

public class UbahKosActivity extends AppCompatActivity {

    TextView txtAlamat;
    EditText etNamaKos,etHarga,etSisaKamar;
    String timeStamp;
    Button btnPilihAlamat,btnUpdate;

    private int PLACE_PICKER_REQUEST = 1;
    static final int RC_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    static final int RC_IMAGE_GALLERY = 2;

    FirebaseFirestore firestore;
    private SweetAlertDialog pDialogLoading,pDialodInfo;
    CollectionReference ref;

    private String alamat,latlon,time;
    private Double lat,lon;
    Uri uri,file;
    FirebaseUser fbUser;
    Intent intent;
    Kosan kosan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_kos);
        Firebase.setAndroidContext(UbahKosActivity.this);
        FirebaseApp.initializeApp(UbahKosActivity.this);
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("kosan");
        fbUser = FirebaseAuth.getInstance().getCurrentUser();

        intent = getIntent();
        kosan = (Kosan) intent.getSerializableExtra("kosan");

        txtAlamat = findViewById(R.id.txtAlamat);
        etNamaKos = findViewById(R.id.etNamaKos);
        etHarga = findViewById(R.id.etHarga);
        etSisaKamar = findViewById(R.id.etSisaKamar);
        btnPilihAlamat = findViewById(R.id.btnPilihAlamat);
        btnUpdate = findViewById(R.id.btnUpdate);

        pDialogLoading = new SweetAlertDialog(UbahKosActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);

        txtAlamat.setText(kosan.getAlamat());
        etNamaKos.setText(kosan.getNamaKos());
        etHarga.setText(""+kosan.getHarga());
        etSisaKamar.setText(""+kosan.getSisaKamar());
        latlon = kosan.getLatlon();

        btnPilihAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder  = new PlacePicker.IntentBuilder();
                try {
                    //menjalankan place picker
                    startActivityForResult(builder.build(UbahKosActivity.this), PLACE_PICKER_REQUEST);

                    // check apabila <a title="Solusi Tidak Bisa Download Google Play Services di Android" href="http://www.twoh.co/2014/11/solusi-tidak-bisa-download-google-play-services-di-android/" target="_blank">Google Play Services tidak terinstall</a> di HP
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }
        });

    }

    private void checkValidation(){
        String getNama = etNamaKos.getText().toString();
        String getHarga = etHarga.getText().toString();
        String getSisa = etSisaKamar.getText().toString();
        String getAlamat = txtAlamat.getText().toString();


        if (getNama.equals("") || getNama.length() == 0
                || getHarga.equals("") || getHarga.length() == 0
                || getSisa.equals("") || getSisa.length() == 0
                || getAlamat.equals("Alamat") || getAlamat.length() == 0
                ) {

            new SweetAlertDialog(UbahKosActivity.this,SweetAlertDialog.ERROR_TYPE)
                    .setContentText("Semua data harus diisi")
                    .setTitleText("Oops..")
                    .setConfirmText("OK")
                    .show();
        }else {
            int harga = Integer.parseInt(getHarga);
            int sisaKamar = Integer.parseInt(getSisa);

            SharedVariable.tempKosan = new Kosan(getNama,getAlamat,harga,sisaKamar,
                    SharedVariable.userID,kosan.getGambarUtama(),kosan.getIdKos(),latlon);
            SharedVariable.idKos = time;

            updateDataKosan();
        }
    }

    private void updateDataKosan(){
        pDialogLoading.show();
        ref.document(kosan.getIdKos()).set(SharedVariable.tempKosan).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    new SweetAlertDialog(UbahKosActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Sukses!")
                            .setContentText("Data Kos diubah")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    Intent intent = new Intent(getApplicationContext(),PemilikActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .show();
                }else {
                    pDialogLoading.dismiss();
                    new SweetAlertDialog(UbahKosActivity.this,SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Terjadi kesalahan")
                            .setTitleText("Oops..")
                            .setConfirmText("OK")
                            .show();
                    Log.d("erorUpdate:",""+task.getException().toString());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        // menangkap hasil balikan dari Place Picker, dan menampilkannya pada TextView
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format(
                        "Place: %s \n" +
                                "Alamat: %s \n" +
                                "Latlng %s \n", place.getName(), place.getAddress(), place.getLatLng().latitude+" "+place.getLatLng().longitude);
                //tvPlaceAPI.setText(toastMsg);

                txtAlamat.setText(place.getAddress());
                txtAlamat.setVisibility(View.VISIBLE);

                alamat = (String) place.getAddress();
                lat = place.getLatLng().latitude;
                lon = place.getLatLng().longitude;
                latlon = lat+","+lon;
                Toast.makeText(getApplicationContext()," "+toastMsg,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
