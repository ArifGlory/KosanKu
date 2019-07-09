package myproject.kosanku.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import myproject.kosanku.Kelas.SharedVariable;
import myproject.kosanku.MainActivity;
import myproject.kosanku.R;
import myproject.kosanku.fragment.FragmentHome;
import myproject.kosanku.fragment.FragmentProfil;

public class BerandaActivity extends AppCompatActivity {

    RelativeLayout container;
    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener fStateListener;
    FirebaseUser fbUser;
    private Toolbar mTopToolbar;
    FragmentHome fragmentHome;
    FragmentProfil fragmentProfil;
    EditText etFilterAlamat;
    CollectionReference ref;
    FirebaseFirestore firestore;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    goToFragment(fragmentHome,true);
                    return true;
                case R.id.navigation_profil:
                    goToFragment(fragmentProfil,true);
                    return true;
            }
            return false;
        }
    };

    void goToFragment(Fragment fragment, boolean isTop) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_admin_container, fragment);
        if (!isTop)
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beranda);
        Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(BerandaActivity.this);
        fAuth = FirebaseAuth.getInstance();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("kosan");

        mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);
        mTopToolbar.setTitle("KosanKu");
        etFilterAlamat = findViewById(R.id.etFilterAlamat);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        container = findViewById(R.id.container);

        fragmentHome = new FragmentHome();
        fragmentProfil = new FragmentProfil();

        goToFragment(fragmentHome,true);

        etFilterAlamat.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    //Toast.makeText(BerandaActivity.this, etFilterAlamat.getText(), Toast.LENGTH_SHORT).show();
                    String keyword = etFilterAlamat.getText().toString();
                    searchByAlamat(keyword);
                    return true;
                }
                return false;
            }
        });
    }

    private void searchByAlamat(String keyword){
        SharedVariable.keyword = keyword;
        Intent intent = new Intent(getApplicationContext(),ResultActivity.class);
        intent.putExtra("tipe","search");
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pojok, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

            if (fbUser!=null){
                fAuth.signOut();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }else {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }

        }

        return super.onOptionsItemSelected(item);
    }

}
