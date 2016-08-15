package com.mark.qpsaralmclock.gpsaralmclock;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class RvAdapter extends RecyclerView.Adapter<RvAdapter.ViewHolder> {

    ArrayList<GifItem> objects;
    final static String LOG_TAG = "myLogss";

    RvAdapter(ArrayList<GifItem> gifs) {
        objects = gifs;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        ViewHolder vh = new ViewHolder(v);

        Log.d(LOG_TAG, "Адаптер onCreateViewHolder ");
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
     //   holder.currentItem = items.get(position);

        GifItem currentGif = objects.get(position);
        String name = currentGif.GetName();

        holder.tvname.setText(name);

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {

        TextView tvname;
        CardView cv;
        RelativeLayout rl;

        ImageView imgdelete;


        public ViewHolder(View itemView) {
            super(itemView);
            Log.d(LOG_TAG, "ViewHolder");

            cv = (CardView) itemView.findViewById(R.id.cardView2);

            tvname = (TextView) itemView.findViewById(R.id.tvname);

            imgdelete = (ImageView) itemView.findViewById(R.id.imgdelete);
            imgdelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(LOG_TAG, "Кликнули на крестик");

                }
            });

        }
    }
}
