package myproject.kosanku.activity;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.kosanku.Kelas.UserModel;
import myproject.kosanku.R;
import myproject.kosanku.Utils;

public class DaftarActivity extends AppCompatActivity {

    Button btnDaftar;
    EditText etUsername,etEmail,etNope,etPassword,etPassword2;
    private SweetAlertDialog pDialogLoading,pDialodInfo;
    FirebaseFirestore firestore;
    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener fStateListener;
    CollectionReference ref;
    Spinner sp_level;
    private String akses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);
        Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(DaftarActivity.this);
        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("users");

        etEmail = findViewById(R.id.etEmail);
        etUsername = findViewById(R.id.etUsername);
        etNope = findViewById(R.id.etNope);
        etPassword = findViewById(R.id.etPassword);
        etPassword2 = findViewById(R.id.etPassword2);
        btnDaftar = findViewById(R.id.btnDaftar);
        sp_level = findViewById(R.id.sp_level);

        pDialogLoading = new SweetAlertDialog(DaftarActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }
        });

        sp_level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                akses = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        fStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = fAuth.getCurrentUser();
                if (user != null ){
                    //user sedang login
                    Log.d("Fauth : ","onAuthStateChanged:signed_in:" + user.getUid());
                }
                //user sedang logout
                Log.d("Fauth : ","onAuthStateChanged:signed_out");
            }
        };

    }

    private void checkValidation() {


        // Get all edittext texts
        String getFullName = etUsername.getText().toString();
        String getEmailId = etEmail.getText().toString();
        String getPassword = etPassword.getText().toString();
        String getConfirmPassword = etPassword2.getText().toString();
        String getPhone = etNope.getText().toString();

        Pattern p = Pattern.compile(Utils.regEx);
        Matcher m = p.matcher(getEmailId);

        // Check if all strings are null or not
        if (getFullName.equals("") || getFullName.length() == 0
                || getEmailId.equals("") || getEmailId.length() == 0
                || getPassword.equals("") || getPassword.length() == 0
                || getConfirmPassword.equals("")
                || getConfirmPassword.length() == 0) {

            new SweetAlertDialog(DaftarActivity.this,SweetAlertDialog.ERROR_TYPE)
                    .setContentText("Semua data harus diisi")
                    .setTitleText("Oops..")
                    .setConfirmText("OK")
                    .show();

        }else if (getPhone.equals("") || getPhone.length() == 0){
            new SweetAlertDialog(DaftarActivity.this,SweetAlertDialog.ERROR_TYPE)
                    .setContentText("Semua data harus diisi")
                    .setTitleText("Oops..")
                    .setConfirmText("OK")
                    .show();
        }
        else if (akses.equals("") || akses == null || akses.equals("Pilih Akses Akun")) {
            new SweetAlertDialog(DaftarActivity.this,SweetAlertDialog.ERROR_TYPE)
                    .setContentText("Anda belum memilih akses akun")
                    .setTitleText("Oops..")
                    .setConfirmText("Siap")
                    .show();
        }
        //check valid email
        else if (!m.find()) {
            new SweetAlertDialog(DaftarActivity.this,SweetAlertDialog.ERROR_TYPE)
                    .setContentText("Email yang anda masukkan tidak valid")
                    .setTitleText("Oops..")
                    .setConfirmText("Siap")
                    .show();
        }
        // Check if both password should be equal
        else if (!getConfirmPassword.equals(getPassword)) {
            new SweetAlertDialog(DaftarActivity.this,SweetAlertDialog.ERROR_TYPE)
                    .setContentText("Konfirmasi Password Salah")
                    .setTitleText("Oops..")
                    .setConfirmText("Siap")
                    .show();
        }
        // Else do signup or do your stuff
        else{
            pDialogLoading.show();
            signUp(getEmailId,getPassword);
        }


    }

    private void signUp(final String email, String passwordUser){

        fAuth.createUserWithEmailAndPassword(email,passwordUser).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                Log.d("Fauth :","createUserWithEmail:onComplete: " + task.isSuccessful());
                /**
                 * Jika sign in gagal, tampilkan pesan ke user. Jika sign in sukses
                 * maka auth state listener akan dipanggil dan logic untuk menghandle
                 * signed in user bisa dihandle di listener.
                 */

                if (!task.isSuccessful()){
                    pDialogLoading.dismiss();
                    Log.e("Eror gagal daftar ",task.getException().toString());
                    new SweetAlertDialog(DaftarActivity.this,SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Proses pendaftarann gagal")
                            .setTitleText("Oops..")
                            .setConfirmText("Siap")
                            .show();

                }else {
                    pDialogLoading.dismiss();
                    FirebaseUser user = fAuth.getCurrentUser();
                    String userID =  fAuth.getCurrentUser().getUid();
                    String token  = FirebaseInstanceId.getInstance().getToken();
                    //ganti nama
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                            .setDisplayName(etUsername.getText().toString()).build();
                    user.updateProfile(profileChangeRequest);

                    UserModel userModel = new UserModel(
                            etUsername.getText().toString(),
                            etEmail.getText().toString(),
                            etNope.getText().toString(),
                            "no",
                            akses
                    );

                    DocumentReference docUser = firestore.collection("users").document(userID);
                    docUser.set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            new SweetAlertDialog(DaftarActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Pendaftaran Berhasil !")
                                    .setContentText("Akun mu telah dibuat, silakan kembali ke halaman login")
                                    .show();

                            etUsername.setText("");
                            etEmail.setText("");
                            etPassword.setText("");
                            etPassword2.setText("");
                            etNope.setText("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            new SweetAlertDialog(DaftarActivity.this,SweetAlertDialog.ERROR_TYPE)
                                    .setContentText("Proses pendaftarann gagal")
                                    .setTitleText("Oops..")
                                    .setConfirmText("Siap")
                                    .show();
                            Log.d("ErorTambahUser", e.toString());
                        }
                    });

                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        fAuth.addAuthStateListener(fStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (fStateListener != null) {
            fAuth.removeAuthStateListener(fStateListener);
        }
    }
}
