
package myproject.kosanku.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import myproject.kosanku.Kelas.SharedVariable;
import myproject.kosanku.MainActivity;
import myproject.kosanku.R;

public class SplashActivity extends AppCompatActivity {

    ProgressBar progressBar;
    Intent i;
    int delay =  3000;
    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener fStateListener;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(SplashActivity.this);
        fAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();

        progressBar = findViewById(R.id.progressBar);

        if (fbUser!=null){
            String token = FirebaseInstanceId.getInstance().getToken();
            SharedVariable.userID = fAuth.getCurrentUser().getUid();
            SharedVariable.nama = fAuth.getCurrentUser().getDisplayName();

            DocumentReference user = firestore.collection("users").document(SharedVariable.userID);
            user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        String akses = doc.get("akses").toString();
                        String foto = doc.get("foto").toString();

                        SharedVariable.foto = foto;
                        SharedVariable.akses = akses;

                        if (akses.equals("Pemilik")){
                            i = new Intent(SplashActivity.this, PemilikActivity.class);
                            startActivity(i);
                        }else {
                            i = new Intent(SplashActivity.this, BerandaActivity.class);
                            startActivity(i);
                        }

                    }else {
                        Log.d("erorGetDatauser:",task.getException().toString());
                        i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("erorGetDatauser:",e.toString());
                    i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                }
            });

        }else {
            i = new Intent(SplashActivity.this, BerandaActivity.class);
            startActivity(i);
        }
    }
}
