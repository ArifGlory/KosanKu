package myproject.kosanku.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import myproject.kosanku.Kelas.Fasilitas;
import myproject.kosanku.Kelas.Kosan;
import myproject.kosanku.R;
import myproject.kosanku.activity.DetailKosActivity;


/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class AdapterFasilitas extends RecyclerView.Adapter<AdapterFasilitas.MyViewHolder> {

    private Context mContext;
    private List<Fasilitas> fasilitasList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtNamaFasilitas;
        public ImageView imgFasilitas;

        public MyViewHolder(View view) {
            super(view);
            txtNamaFasilitas = (TextView) view.findViewById(R.id.txtNamaFasilitas);
            imgFasilitas = view.findViewById(R.id.imgFasilitas);

        }
    }

    public AdapterFasilitas(Context mContext, List<Fasilitas> fasilitasList) {
        this.mContext = mContext;
        this.fasilitasList = fasilitasList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.itemlist_fasilitas, parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (fasilitasList.isEmpty()){

            Log.d("isiFasilitas: ",""+fasilitasList.size());
        }else {

            Resources res = mContext.getResources();

            final Fasilitas fasilitas  = fasilitasList.get(position);


            holder.txtNamaFasilitas.setText(fasilitas.getNamaFasilitas());

            if (fasilitas.getIdFasilitas().equals("AC")){
                holder.imgFasilitas.setImageResource(R.drawable.ic_ac);
            }else if (fasilitas.getIdFasilitas().equals("KmrMandi")){
                holder.imgFasilitas.setImageResource(R.drawable.kamar_mandi);
            }else if (fasilitas.getIdFasilitas().equals("Wifi")){
                holder.imgFasilitas.setImageResource(R.drawable.ic_wifi_black_24dp);
            }else if (fasilitas.getIdFasilitas().equals("Kasur")){
                holder.imgFasilitas.setImageResource(R.drawable.bed);
            }else if (fasilitas.getIdFasilitas().equals("Lemari")){
                holder.imgFasilitas.setImageResource(R.drawable.cupboard);
            }

        }

    }


    @Override
    public int getItemCount() {
        //return namaWisata.length;
        return fasilitasList.size();
    }
}
