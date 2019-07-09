package myproject.kosanku.activity;

import android.annotation.SuppressLint;
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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.client.Firebase;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.kosanku.Kelas.Fasilitas;
import myproject.kosanku.Kelas.Kosan;
import myproject.kosanku.Kelas.Rating;
import myproject.kosanku.Kelas.SharedVariable;
import myproject.kosanku.R;
import myproject.kosanku.adapter.AdapterFasilitas;
import myproject.kosanku.adapter.AdapterKosan;

public class DetailKosActivity extends AppCompatActivity implements RatingDialogListener {

    TextView txtNamaKos,txtHarga,txtSisaKamar,txtRating,txtTipeBayar;
    ImageView imgKos;
    Button btnHubungi,btnLokasi,btnRating;
    FloatingActionButton btnUbahGambar;
    RecyclerView recycler_view;
    Intent intent;
    FirebaseFirestore firestore;

    private SweetAlertDialog pDialogLoading,pDialodInfo;
    CollectionReference ref,refPemilik;
    Kosan kosan;
    AdapterFasilitas adapter;
    private List<Fasilitas> fasilitasList;
    private String nomorPemilik = "no";
    static final int RC_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    static final int RC_IMAGE_GALLERY = 2;
    Uri uri,file;
    FirebaseUser fbUser;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_kos);
        Firebase.setAndroidContext(DetailKosActivity.this);
        FirebaseApp.initializeApp(DetailKosActivity.this);
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("kosan");
        refPemilik = firestore.collection("users");
        fbUser = FirebaseAuth.getInstance().getCurrentUser();


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
        txtTipeBayar = findViewById(R.id.txtTipeBayar);
        txtRating = findViewById(R.id.txtRating);
        btnHubungi = findViewById(R.id.btnHubungi);
        btnLokasi = findViewById(R.id.btnLokasi);
        btnUbahGambar = findViewById(R.id.btnUbahGambar);
        btnRating = findViewById(R.id.btnRating);
        recycler_view = findViewById(R.id.recycler_view);

        recycler_view.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recycler_view.setHasFixedSize(true);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setAdapter(adapter);

        txtNamaKos.setText(kosan.getNamaKos());
        txtHarga.setText(formatRupiah.format((double) kosan.getHarga()));
        txtSisaKamar.setText("Sisa Kamar : "+kosan.getSisaKamar());
        if (kosan.getTipeBayar().equals("-")){
            txtTipeBayar.setText("Tipe Pembayaran belum ditentukan");
        }else {
            txtTipeBayar.setText(kosan.getTipeBayar());
        }

        if (SharedVariable.akses.equals("Pemilik")){
            btnUbahGambar.setVisibility(View.VISIBLE);
        }

        Glide.with(this)
                .load(kosan.getGambarUtama())
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
        btnUbahGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(DetailKosActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Ubah / Tambah Gambar")
                        .setContentText("Anda Yakin Ingin Mengubah Detail Gambar?")
                        .setConfirmText("Ubah Gambar Utama")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(DetailKosActivity.this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_READ_EXTERNAL_STORAGE);
                                } else {
                                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                    intent.setType("image/*");
                                    startActivityForResult(intent, RC_IMAGE_GALLERY);
                                }
                                sDialog.dismissWithAnimation();

                            }
                        })
                        .setCancelButton("Tambah banyak gambar", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                Intent intent = new Intent(DetailKosActivity.this,MoreImageActivity.class);
                                intent.putExtra("kosan",kosan);
                                startActivity(intent);

                                sDialog.dismissWithAnimation();

                            }
                        })
                        .show();
            }
        });
        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogRating();
            }
        });
        imgKos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),MoreImageActivity.class);
                i.putExtra("kosan",kosan);
                startActivity(i);
            }
        });

        getDataFasilitas();
        getDataPemilik();
        getDataRating();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        // menangkap hasil balikan dari Place Picker, dan menampilkannya pada TextView

        if (requestCode == RC_IMAGE_GALLERY && resultCode == RESULT_OK) {
            uri = data.getData();

            uploadGambar(uri);
        }
        else if (requestCode == 100 && resultCode == RESULT_OK){
            uri = file;
            imgKos.setImageURI(uri);
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
                Toast.makeText(DetailKosActivity.this, "Upload failed!\n" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                pDialogLoading.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Toast.makeText(DetailKosActivity.this, "Upload finished!", Toast.LENGTH_SHORT).show();

                // save image to database
                ref.document(kosan.getIdKos()).update("gambarUtama",downloadUrl.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pDialogLoading.dismiss();
                        imgKos.setImageURI(uri);
                        new SweetAlertDialog(DetailKosActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Sukses!")
                                .setContentText("Gambar Utama diubah")
                                .show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pDialogLoading.dismiss();
                        new SweetAlertDialog(DetailKosActivity.this,SweetAlertDialog.ERROR_TYPE)
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

    private void getDataRating(){
        ref.document(kosan.getIdKos()).collection("listRating").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                QuerySnapshot doc = task.getResult();
                long totalRate = 0;

                if (task.isSuccessful()){
                    if (doc.isEmpty()){
                        txtRating.setText("-");
                    }else {
                        for (DocumentSnapshot dc : task.getResult()){

                            long rate      = (long) dc.get("rate");
                            totalRate      = totalRate + rate;
                        }
                        long showRate = totalRate/ doc.size();
                        txtRating.setText(""+showRate);
                    }
                }else {
                    new SweetAlertDialog(DetailKosActivity.this,SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("terjadi kesalahan")
                            .setContentText("gagal mendapatkan data rating")
                            .show();
                }


            }
        });
    }

    private void showDialogRating(){
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNeutralButtonText("Later")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                .setDefaultRating(4)
                .setTitle("Berikan Penilaian mu")
                .setDescription("Silakan berikan penilaian mu tentang Kos ini")
                .setCommentInputEnabled(true)
                .setDefaultComment("Keren banget !")
                .setStarColor(R.color.startblue)
                .setNoteDescriptionTextColor(R.color.kuningGelap)
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimaryDark)
                .setHint("Tulis komentar mu disini ...")
                .setHintTextColor(R.color.colorlight2)
                .setCommentTextColor(R.color.album_title)
                .setCommentBackgroundColor(R.color.photo_placeholder)
                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .create(DetailKosActivity.this)
                // .setTargetFragment(this, TAG) // only if listener is implemented by fragment
                .show();
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int i, @NotNull String s) {

        Rating rating = new Rating(i,s);
        ref.document(kosan.getIdKos()).collection("listRating").document(SharedVariable.userID).set(rating)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            new SweetAlertDialog(DetailKosActivity.this,SweetAlertDialog.SUCCESS_TYPE)
                                    .setContentText("Berahasil")
                                    .setTitleText("Rate Sukses")
                                    .setContentText("Rating anda ditambahkan")
                                    .show();
                            getDataRating();
                        }else {
                            new SweetAlertDialog(DetailKosActivity.this,SweetAlertDialog.ERROR_TYPE)
                                    .setContentText("Terjadi kesalahan")
                                    .setTitleText("Oops..")
                                    .setConfirmText("OK")
                                    .show();
                            Log.d("erorRating:","erorRating"+task.getException().toString());
                        }
                    }
                });
    }
}
