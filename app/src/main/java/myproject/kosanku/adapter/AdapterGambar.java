package myproject.kosanku.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import myproject.kosanku.Kelas.Gambar;
import myproject.kosanku.R;


/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class AdapterGambar extends RecyclerView.Adapter<AdapterGambar.MyViewHolder> {

    private Context mContext;
    private List<Gambar> GambarList;


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


            Glide.with(mContext)
                    .load(gambar.getUrlGambar())
                    .into(holder.ivKosan);

            holder.ivKosan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
