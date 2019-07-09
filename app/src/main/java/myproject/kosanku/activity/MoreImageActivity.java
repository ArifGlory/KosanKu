package myproject.kosanku.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.kosanku.Kelas.Gambar;
import myproject.kosanku.Kelas.Kosan;
import myproject.kosanku.Kelas.SharedVariable;
import myproject.kosanku.R;
import myproject.kosanku.adapter.AdapterGambar;

public class MoreImageActivity extends AppCompatActivity {

    RecyclerView rvGambar;
    Button btnTambah;

    Intent intent;
    FirebaseFirestore firestore;
    private SweetAlertDialog pDialogLoading,pDialodInfo;
    CollectionReference ref,refPemilik;
    Kosan kosan;

    static final int RC_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    static final int RC_IMAGE_GALLERY = 2;
    Uri uriGambar,file;
    FirebaseUser fbUser;
    private List<Gambar> gambarList;
    AdapterGambar adapter;
    private String timeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_image);

        Firebase.setAndroidContext(MoreImageActivity.this);
        FirebaseApp.initializeApp(MoreImageActivity.this);
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("kosan");
        refPemilik = firestore.collection("users");
        fbUser = FirebaseAuth.getInstance().getCurrentUser();

        intent = getIntent();
        kosan = (Kosan) intent.getSerializableExtra("kosan");
        Log.d("idKos"," "+kosan.getIdKos());
        gambarList = new ArrayList<>();
        adapter = new AdapterGambar(MoreImageActivity.this,gambarList);

        pDialogLoading = new SweetAlertDialog(MoreImageActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);
        pDialogLoading.show();


        rvGambar    = findViewById(R.id.rvGambar);
        btnTambah   = findViewById(R.id.btnTambah);

        rvGambar.setLayoutManager(new LinearLayoutManager(MoreImageActivity.this));
        rvGambar.setHasFixedSize(true);
        rvGambar.setItemAnimator(new DefaultItemAnimator());
        rvGambar.setAdapter(adapter);

        if (SharedVariable.akses.equals("Pemilik")){
            btnTambah.setVisibility(View.VISIBLE);
        }

        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MoreImageActivity.this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_READ_EXTERNAL_STORAGE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, RC_IMAGE_GALLERY);
                }
            }
        });

        getDataGambar();


    }

    public void getDataGambar(){

        ref.document(kosan.getIdKos()).collection("listGambar").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                gambarList.clear();
                adapter.notifyDataSetChanged();

                if (task.isSuccessful()){
                    pDialogLoading.dismiss();
                    for (DocumentSnapshot doc : task.getResult()){


                        Gambar gambar = doc.toObject(Gambar.class);
                        gambarList.add(gambar);
                        adapter.notifyDataSetChanged();
                    }
                }else {
                    pDialogLoading.dismiss();
                    new SweetAlertDialog(MoreImageActivity.this,SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Pengambilan data gagal")
                            .show();
                    Log.d("gagalGetData:",task.getException().toString());
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pDialogLoading.dismiss();
                new SweetAlertDialog(MoreImageActivity.this,SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Pengambilan data gagal")
                        .show();
                Log.d("gagalGetData:",e.toString());
            }
        });
    }

    private void uploadGambar(Uri uri){

        pDialogLoading.show();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imagesRef = storageRef.child("images");
        StorageReference userRef = imagesRef.child(fbUser.getUid());
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = fbUser.getUid() + "_" + timeStamp;
        StorageReference fileRef = userRef.child(filename);

        UploadTask uploadTask = fileRef.putFile(uri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(MoreImageActivity.this, "Upload failed!\n" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                pDialogLoading.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Toast.makeText(MoreImageActivity.this, "Upload finished!", Toast.LENGTH_SHORT).show();

                Gambar moreGambar = new Gambar(
                        downloadUrl.toString()
                );

                ref.document(kosan.getIdKos()).collection("listGambar").document(timeStamp).set(moreGambar).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            pDialogLoading.dismiss();
                            new SweetAlertDialog(MoreImageActivity.this,SweetAlertDialog.SUCCESS_TYPE)
                                    .setContentText("Tambah gambar berhasil");
                            Log.d("moreImage:","sukses ditambahkan");

                            getDataGambar();
                        }else {
                            new SweetAlertDialog(MoreImageActivity.this,SweetAlertDialog.ERROR_TYPE)
                                    .setContentText("Terjadi kesalahan, coba lagi nanti");
                            Log.d("moreImage:","gagal ditambahkan "+task.getException().getMessage().toString());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pDialogLoading.dismiss();
                        new SweetAlertDialog(MoreImageActivity.this,SweetAlertDialog.ERROR_TYPE)
                                .setContentText("Terjadi kesalahan, coba lagi nanti");
                        Log.d("moreImage:","gagal ditambahkan "+e.getMessage().toString());
                    }
                });


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        // menangkap hasil balikan dari Place Picker, dan menampilkannya pada TextView

        if (requestCode == RC_IMAGE_GALLERY && resultCode == RESULT_OK) {
            uriGambar = data.getData();

            uploadGambar(uriGambar);
        }
    }
}
