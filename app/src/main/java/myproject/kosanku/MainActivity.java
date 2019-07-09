package myproject.kosanku;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.kosanku.Kelas.SharedVariable;
import myproject.kosanku.activity.DaftarActivity;
import myproject.kosanku.activity.SplashActivity;

public class MainActivity extends AppCompatActivity {

    RelativeLayout relaContainer;
    TextView txtSalam,txtGuest;
    Button btnDaftar,btnLogin;
    EditText etEmail,etPassword;
    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseFirestore firestore;
    private SweetAlertDialog pDialogLoading,pDialodInfo;
    int klik = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(MainActivity.this);
        fAuth = FirebaseAuth.getInstance();        setContentView(R.layout.activity_main);

        firestore = FirebaseFirestore.getInstance();

        relaContainer = findViewById(R.id.relaContainer);
        txtSalam = findViewById(R.id.txtSalam);
        btnDaftar = findViewById(R.id.btnDaftar);
        btnLogin = findViewById(R.id.btnLogin);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        pDialogLoading = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),DaftarActivity.class);
                startActivity(i);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }
        });
    }

    private void checkValidation() {
        String getEmailId = etEmail.getText().toString();
        String getPassword = etPassword.getText().toString();

        Pattern p = Pattern.compile(Utils.regEx);
        Matcher m = p.matcher(getEmailId);

        if (getEmailId.equals("") || getEmailId.length() == 0
                || getPassword.equals("") || getPassword.length() == 0) {

            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Semua Field Harus diisi")
                    .show();
        }
        else if (!m.find()) {
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Email anda tidak valid")
                    .show();
        }
        else {
            pDialogLoading.show();
            doLogin(getEmailId,getPassword);

        }
    }

    private void doLogin(final String email,String passwordUser){
        fAuth.signInWithEmailAndPassword(email,passwordUser).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (!task.isSuccessful()){
                    pDialogLoading.dismiss();
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Login gagal")
                            .setContentText("Periksa kembali Email dan Password anda")
                            .show();

                }else{
                    pDialogLoading.dismiss();
                    // Successfully signed in
                    SharedVariable.nama = fAuth.getCurrentUser().getDisplayName();
                    SharedVariable.userID = fAuth.getCurrentUser().getUid();
                    // get the Firebase user
                    FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();

                    // get the FCM token
                    String token = FirebaseInstanceId.getInstance().getToken();


                    Intent i = new Intent(MainActivity.this,SplashActivity.class);
                    startActivity(i);

                }


            }
        });
    }

    @Override
    public void onBackPressed() {
        klik++;
        if (klik<2){
            Toast.makeText(getApplicationContext(),"Tekan sekali lagi untuk keluar",Toast.LENGTH_SHORT).show();
        }else {
            klik = 0;
            finishAffinity();
            System.exit(0);

        }

       /* DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
    }
}
