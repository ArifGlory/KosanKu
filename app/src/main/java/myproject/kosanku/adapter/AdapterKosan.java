package myproject.kosanku.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import myproject.kosanku.Kelas.Kosan;
import myproject.kosanku.Kelas.SharedVariable;
import myproject.kosanku.R;
import myproject.kosanku.activity.DetailKosActivity;
import myproject.kosanku.activity.PemilikActivity;
import myproject.kosanku.activity.PilihFasilitasActivity;
import myproject.kosanku.activity.UbahKosActivity;
import myproject.kosanku.fragment.FragmentHomePemilik;


/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class AdapterKosan extends RecyclerView.Adapter<AdapterKosan.MyViewHolder> {

    private Context mContext;
    private List<Kosan> kosanList;
    FirebaseFirestore firestore;
    CollectionReference ref;
    private SweetAlertDialog pDialogLoading,pDialodInfo;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtNamaKos,txtAlamat,txtHarga;
        public ImageView imgKosan;
        public LinearLayout lineContainer;

        public MyViewHolder(View view) {
            super(view);
            txtNamaKos = (TextView) view.findViewById(R.id.txtNamaKos);
            txtAlamat = (TextView) view.findViewById(R.id.txtAlamat);
            txtHarga = (TextView) view.findViewById(R.id.txtHarga);
            imgKosan = view.findViewById(R.id.ivFeedCenter);
            lineContainer = view.findViewById(R.id.lineContainer);

        }
    }

    public AdapterKosan(Context mContext, List<Kosan> kosanList) {
        this.mContext = mContext;
        this.kosanList = kosanList;
        Firebase.setAndroidContext(mContext);
        FirebaseApp.initializeApp(mContext);
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("kosan");

        pDialogLoading = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feed, parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (kosanList.isEmpty()){

            Log.d("isiKosan: ",""+kosanList.size());
        }else {

            Resources res = mContext.getResources();

            final Kosan kosan  = kosanList.get(position);

            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.ENGLISH);
            Locale localeID = new Locale("in", "ID");
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

            holder.txtNamaKos.setText(kosan.getNamaKos());
            holder.txtHarga.setText(formatRupiah.format((double) kosan.getHarga()));
            holder.txtAlamat.setText(kosan.getAlamat());

            Glide.with(mContext).load(kosan.getGambarUtama())
                    .asBitmap()
                    //.fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imgKosan);

            holder.lineContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,DetailKosActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("kosan",kosan);
                    mContext.startActivity(intent);
                }
            });
            holder.lineContainer.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if (SharedVariable.akses.equals("Pemilik")){
                        new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Edit Kosan")
                                .setContentText("Anda dapat melakukan perubahan pada kosan ini")
                                .setConfirmText("Ubah Data")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {

                                        Intent intent = new Intent(mContext,UbahKosActivity.class);
                                        intent.putExtra("kosan",kosan);
                                        mContext.startActivity(intent);
                                        sDialog.dismissWithAnimation();

                                    }
                                })
                                .setCancelButton("Hapus Kosan", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        pDialogLoading.show();
                                        ref.document(kosan.getIdKos()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                pDialogLoading.dismiss();
                                               Intent intent = new Intent(mContext,PemilikActivity.class);
                                               mContext.startActivity(intent);
                                            }
                                        });



                                    }
                                })
                                .show();
                    }


                    return true;
                }
            });

        }

    }


    @Override
    public int getItemCount() {
        //return namaWisata.length;
        return kosanList.size();
    }
}
