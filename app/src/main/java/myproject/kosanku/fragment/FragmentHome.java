package myproject.kosanku.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.kosanku.Kelas.Fasilitas;
import myproject.kosanku.Kelas.Kosan;
import myproject.kosanku.Kelas.SharedVariable;
import myproject.kosanku.R;
import myproject.kosanku.activity.ResultActivity;
import myproject.kosanku.adapter.AdapterFasilitas;
import myproject.kosanku.adapter.AdapterKosan;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHome extends Fragment {


    public FragmentHome() {
        // Required empty public constructor
    }


    RecyclerView recyclerView;
    FirebaseFirestore firestore;

    private SweetAlertDialog pDialogLoading,pDialodInfo;
    AdapterKosan adapter;
    private List<Kosan> kosanList;
    CollectionReference ref;
    LinearLayout lineHarga,lineFasilitas,lineSort;
    public static android.app.AlertDialog dialog;
    private long maxHarga;
    AdapterFasilitas adapterFasilitas;
    private List<Fasilitas> fasilitasList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_beranda, container, false);
        Firebase.setAndroidContext(this.getActivity());
        FirebaseApp.initializeApp(this.getActivity());
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("kosan");

        recyclerView = view.findViewById(R.id.rvFeed);
        lineHarga = view.findViewById(R.id.lineHarga);
        lineFasilitas = view.findViewById(R.id.lineFasilitas);
        lineSort = view.findViewById(R.id.lineSort);

        kosanList = new ArrayList<>();
        adapter = new AdapterKosan(getActivity(),kosanList);

        fasilitasList = new ArrayList<>();
        adapterFasilitas = new AdapterFasilitas(getActivity(),fasilitasList);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        pDialogLoading = new SweetAlertDialog(this.getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);
        pDialogLoading.show();

        Fasilitas fasilitas1 = new Fasilitas("AC","Fasilitas AC");
        Fasilitas fasilitas2 = new Fasilitas("KmrMandi","Fasilitas Kamar Mandi");
        Fasilitas fasilitas3 = new Fasilitas("Wifi","Fasilitas Wifi");
        Fasilitas fasilitas4 = new Fasilitas("Kasur","Fasilitas Kasur");
        Fasilitas fasilitas5 = new Fasilitas("Lemari","Fasilitas Lemari");

       fasilitasList.add(fasilitas1);
       fasilitasList.add(fasilitas2);
       fasilitasList.add(fasilitas3);
       fasilitasList.add(fasilitas4);
       fasilitasList.add(fasilitas5);

        lineSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater minlfater = LayoutInflater.from(getActivity());
                View v2 = minlfater.inflate(R.layout.dialog_sortir, null);
                dialog = new android.app.AlertDialog.Builder(getActivity()).create();
                dialog.setView(v2);

                final CardView cardUpSorting = (CardView) v2.findViewById(R.id.cardUpSorting);
                final CardView cardDownSorting = (CardView) v2.findViewById(R.id.cardDownSorting);
                RelativeLayout relaUpSorting = v2.findViewById(R.id.relaUpSorting);
                RelativeLayout relaDownSorting = v2.findViewById(R.id.relaDownSorting);

                relaUpSorting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedVariable.sortType = "desc";
                        Intent intent = new Intent(getActivity(),ResultActivity.class);
                        intent.putExtra("tipe","sort");
                        startActivity(intent);
                    }
                });
                relaDownSorting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedVariable.sortType = "asc";
                        Intent intent = new Intent(getActivity(),ResultActivity.class);
                        intent.putExtra("tipe","sort");
                        startActivity(intent);
                    }
                });


                dialog.show();
            }
        });
        lineHarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater minlfater = LayoutInflater.from(getActivity());
                View v2 = minlfater.inflate(R.layout.dialog_harga, null);
                dialog = new android.app.AlertDialog.Builder(getActivity()).create();
                dialog.setView(v2);

                NumberFormat format = NumberFormat.getCurrencyInstance(Locale.ENGLISH);
                Locale localeID = new Locale("in", "ID");
                final NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

                final TextView txtHarga = v2.findViewById(R.id.txtHarga);
                Button btnCari = v2.findViewById(R.id.btnCari);

                SeekBar seekBar = v2.findViewById(R.id.seekBar);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        maxHarga = progress;
                        txtHarga.setText("Rp. 0 - "+ formatRupiah.format((double) progress));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                btnCari.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (maxHarga == 0){
                            Toast.makeText(getActivity(),"Anda belum memilih rentang harga",Toast.LENGTH_SHORT).show();
                        }else {
                            SharedVariable.maxHarga = maxHarga;
                            Intent intent = new Intent(getActivity(),ResultActivity.class);
                            intent.putExtra("tipe","harga");
                            startActivity(intent);

                        }
                    }
                });


                dialog.show();
            }
        });
        lineFasilitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater minlfater = LayoutInflater.from(getActivity());
                View v2 = minlfater.inflate(R.layout.dialog_fasilitas, null);
                dialog = new android.app.AlertDialog.Builder(getActivity()).create();
                dialog.setView(v2);

                SharedVariable.isFilterFasilitas = "yes";
                RecyclerView rvFasilitas = v2.findViewById(R.id.rvFasilitas);
                final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                rvFasilitas.setLayoutManager(layoutManager);
                rvFasilitas.setAdapter(adapterFasilitas);
                rvFasilitas.setLayoutManager(new LinearLayoutManager(getActivity()));


                dialog.show();
            }
        });

        getDataKosan();

        return view;
    }

    public void getDataKosan(){


        ref.orderBy("idKos",Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                kosanList.clear();
                adapter.notifyDataSetChanged();

                if (task.isSuccessful()){
                    pDialogLoading.dismiss();
                    for (DocumentSnapshot doc : task.getResult()){
                        Kosan kosan = doc.toObject(Kosan.class);

                        kosanList.add(kosan);
                        adapter.notifyDataSetChanged();
                    }
                }else {
                    pDialogLoading.dismiss();
                    new SweetAlertDialog(getActivity(),SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Pengambilan data gagal")
                            .show();
                    Log.d("gagalGetData:",task.getException().toString());
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pDialogLoading.dismiss();
                new SweetAlertDialog(getActivity(),SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Pengambilan data gagal")
                        .show();
                Log.d("gagalGetData:",e.toString());
            }
        });
    }

    @Override
    public void onResume() {
        Log.d("FragmentHome:","Resume");
        getDataKosan();
        super.onResume();
    }
}
