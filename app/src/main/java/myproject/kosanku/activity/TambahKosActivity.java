package myproject.kosanku.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.kosanku.Kelas.Kosan;
import myproject.kosanku.Kelas.SharedVariable;
import myproject.kosanku.R;

public class TambahKosActivity extends AppCompatActivity {

    TextView txtAlamat,txtInfoGambar;
    ImageView imgGambar;
    EditText etNamaKos,etHarga,etSisaKamar;
    String timeStamp;
    Button btnPilihAlamat,btnNext;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_kos);
        Firebase.setAndroidContext(TambahKosActivity.this);
        FirebaseApp.initializeApp(TambahKosActivity.this);
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("kosan");
        fbUser = FirebaseAuth.getInstance().getCurrentUser();

        txtAlamat = findViewById(R.id.txtAlamat);
        txtInfoGambar = findViewById(R.id.txtInfoGambar);
        imgGambar = findViewById(R.id.imgGambar);
        etNamaKos = findViewById(R.id.etNamaKos);
        etHarga = findViewById(R.id.etHarga);
        etSisaKamar = findViewById(R.id.etSisaKamar);
        btnPilihAlamat = findViewById(R.id.btnPilihAlamat);
        btnNext = findViewById(R.id.btnNext);

        pDialogLoading = new SweetAlertDialog(TambahKosActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);

        time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        btnPilihAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder  = new PlacePicker.IntentBuilder();
                try {
                    //menjalankan place picker
                    startActivityForResult(builder.build(TambahKosActivity.this), PLACE_PICKER_REQUEST);

                    // check apabila <a title="Solusi Tidak Bisa Download Google Play Services di Android" href="http://www.twoh.co/2014/11/solusi-tidak-bisa-download-google-play-services-di-android/" target="_blank">Google Play Services tidak terinstall</a> di HP
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        imgGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(TambahKosActivity.this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_READ_EXTERNAL_STORAGE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, RC_IMAGE_GALLERY);
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
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

            new SweetAlertDialog(TambahKosActivity.this,SweetAlertDialog.ERROR_TYPE)
                    .setContentText("Semua data harus diisi")
                    .setTitleText("Oops..")
                    .setConfirmText("OK")
                    .show();
        }else if (uri == null){
            new SweetAlertDialog(TambahKosActivity.this,SweetAlertDialog.ERROR_TYPE)
                    .setContentText("Pilih Gambar Utama dahulu")
                    .setTitleText("Oops..")
                    .setConfirmText("OK")
                    .show();
        }else {
            int harga = Integer.parseInt(getHarga);
            int sisaKamar = Integer.parseInt(getSisa);

            SharedVariable.tempKosan = new Kosan(getNama,getAlamat,harga,sisaKamar,
                    SharedVariable.userID,"aa",time,latlon);
            SharedVariable.idKos = time;

           uploadGambar(uri);
        }
    }

    private void uploadGambar(final Uri uri){

        pDialogLoading.show();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imagesRef = storageRef.child("images");
        StorageReference userRef = imagesRef.child(fbUser.getUid());
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = fbUser.getUid() + "_" + timeStamp;
        StorageReference fileRef = userRef.child(filename);

        UploadTask uploadTask = fileRef.putFile(uri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(TambahKosActivity.this, "Upload failed!\n" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                pDialogLoading.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Toast.makeText(TambahKosActivity.this, "Upload finished!", Toast.LENGTH_SHORT).show();

                // save image to database

                SharedVariable.tempKosan.setGambarUtama(downloadUrl.toString());
                ref.document(time).set(SharedVariable.tempKosan).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pDialogLoading.dismiss();
                        if (task.isSuccessful()){
                            etHarga.setText("");
                            etNamaKos.setText("");
                            etHarga.setText("");
                            etSisaKamar.setText("");
                            txtAlamat.setText("alamat");
                            imgGambar.setImageResource(R.drawable.img_placeholder);

                            Intent i = new Intent(getApplicationContext(),PilihFasilitasActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pDialogLoading.dismiss();
                        new SweetAlertDialog(TambahKosActivity.this,SweetAlertDialog.ERROR_TYPE)
                                .setContentText("Terjadi kesalahan")
                                .setTitleText("Oops..")
                                .setConfirmText("OK")
                                .show();
                        Log.d("erorUpload:","erorGambar "+e.toString());
                    }
                });


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
        }else

        if (requestCode == RC_IMAGE_GALLERY && resultCode == RESULT_OK) {
            uri = data.getData();
            txtInfoGambar.setText("Gambar Terpilih");
            //Toast.makeText(TambahKosanActivity.this, "Tipe : !\n" + tipe, Toast.LENGTH_LONG).show();

            imgGambar.setImageURI(uri);
        }
        else if (requestCode == 100 && resultCode == RESULT_OK){
            uri = file;
            imgGambar.setImageURI(uri);
        }
    }
}
