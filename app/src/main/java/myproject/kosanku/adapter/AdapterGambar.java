package myproject.kosanku.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.kosanku.Kelas.Gambar;
import myproject.kosanku.Kelas.SharedVariable;
import myproject.kosanku.R;
import myproject.kosanku.activity.FulllImageActivity;
import myproject.kosanku.activity.MoreImageActivity;


/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class AdapterGambar extends RecyclerView.Adapter<AdapterGambar.MyViewHolder> {

    private Context mContext;
    private List<Gambar> GambarList;
    FirebaseFirestore firestore;
    private SweetAlertDialog pDialogLoading,pDialodInfo;
    CollectionReference ref,refPemilik;
    MoreImageActivity moreImageActivity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivKosan;


        public MyViewHolder(View view) {
            super(view);
            ivKosan = view.findViewById(R.id.ivKosan);


        }
    }

    public AdapterGambar(Context mContext, List<Gambar> GambarList) {
        this.mContext = mContext;
        this.GambarList = GambarList;
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("kosan");
        moreImageActivity = new MoreImageActivity();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gambar, parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (GambarList.isEmpty()){

            Log.d("isi Gambar: ",""+GambarList.size());
        }else {

            Resources res = mContext.getResources();

            final Gambar gambar = GambarList.get(position);
            Log.d("idgambar:",""+gambar.getIdGambar());


            Glide.with(mContext)
                    .load(gambar.getUrlGambar())
                    .into(holder.ivKosan);

            holder.ivKosan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, FulllImageActivity.class);
                    intent.putExtra("url",gambar.getUrlGambar());
                    mContext.startActivity(intent);
                }
            });
            holder.ivKosan.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (SharedVariable.akses.equals("Pemilik")){
                        new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Hapus gambar ini ?")
                                .setContentText("Anda yakin menghapus gambar ini?")
                                .setConfirmText("Hapus")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {

                                        if (mContext instanceof MoreImageActivity) {
                                            ((MoreImageActivity)mContext).showLoading();

                                            ref.document(SharedVariable.idKosAktif).collection("listGambar")
                                                    .document(gambar.getIdGambar()).delete()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                ((MoreImageActivity)mContext).dismissLoading();
                                                                ((MoreImageActivity)mContext).getDataGambar();

                                                            }else{
                                                                ((MoreImageActivity)mContext).failDialog();
                                                            }
                                                        }
                                                    });

                                        }


                                        sDialog.dismissWithAnimation();
                                    }
                                })
                                .setCancelButton("Batal", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {

                                        sDialog.dismissWithAnimation();

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
        return GambarList.size();
    }
}
