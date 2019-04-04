package myproject.kosanku.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import myproject.kosanku.Kelas.SharedVariable;
import myproject.kosanku.MainActivity;
import myproject.kosanku.R;



/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentProfil extends Fragment {


    public FragmentProfil() {
        // Required empty public constructor
    }


    FirebaseFirestore firestore;

    private SweetAlertDialog pDialogLoading,pDialodInfo;

    CollectionReference ref,refUser;
    TextView txtNama,txtEmail,txtNope;
    CircleImageView ivUserProfilePhoto;
    RelativeLayout relaLogout;
    FirebaseUser fbUser;
    private FirebaseAuth fAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_profil, container, false);
        Firebase.setAndroidContext(this.getActivity());
        FirebaseApp.initializeApp(this.getActivity());
        firestore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        ref = firestore.collection("users");

        txtNama = view.findViewById(R.id.txtNamaProfil);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtNope = view.findViewById(R.id.txtPhone);
        relaLogout = view.findViewById(R.id.relaLogout);
        ivUserProfilePhoto = view.findViewById(R.id.imgProfile);


        pDialogLoading = new SweetAlertDialog(this.getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Menampilkan data..");
        pDialogLoading.setCancelable(false);
        pDialogLoading.show();

        txtNama.setText(SharedVariable.nama);

        if (!SharedVariable.foto.equals("no")){
            Glide.with(getActivity())
                    .load(SharedVariable.foto)
                    .into(ivUserProfilePhoto);
        }

        relaLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fbUser!=null){
                    fAuth.signOut();
                    Intent i = new Intent(getActivity(), MainActivity.class);
                    startActivity(i);
                }

            }
        });

        getDataUser();

        return view;
    }

    private void getDataUser(){
        ref.document(SharedVariable.userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                pDialogLoading.dismiss();

                DocumentSnapshot dc = task.getResult();
                if (dc.exists()){
                    String nope = dc.get("nope").toString();
                    String email = dc.get("email").toString();

                    txtEmail.setText(email);
                    txtNope.setText(nope);
                }
            }
        });
    }






}
