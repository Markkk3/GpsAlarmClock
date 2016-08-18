package com.mark.qpsaralmclock.gpsaralmclock;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.internal.overlay.zzo;

import java.util.ArrayList;


public class RvAdapter extends RecyclerView.Adapter<RvAdapter.ViewHolder> {

    ArrayList<GifItem> objects;
    final static String LOG_TAG = "myLogss";
    MainActivity main;

    RvAdapter(ArrayList<GifItem> gifs) {
        objects = gifs;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        main = (MainActivity) parent.getContext();

        Log.d(LOG_TAG, "Адаптер onCreateViewHolder ");
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
     //   holder.currentItem = items.get(position);
        GifItem currentGif = objects.get(position);

        String name = currentGif.getName();
        Log.d(LOG_TAG, "Получили расстоние " + currentGif.getDistance());
        holder.tvname.setText(name);
        holder.tvKm.setText(main.convertDistance(currentGif.getDistance()));

        if(objects.get(position).getRun()) {
            holder.linlayout.setBackgroundColor(Color.argb(255, 76, 175, 80));
            holder.imgstart.setImageResource(R.drawable.mr_ic_pause_light);
        } else  {
            holder.linlayout.setBackgroundColor(Color.argb(255, 229, 115, 115));
            holder.imgstart.setImageResource(R.drawable.mr_ic_play_light);
        }


    }
/*
    public void upDatedistance() {



        holder.tvKm.setText(main.convertDistance(currentGif.getDistance()));

    }
*/


    @Override
    public int getItemCount() {
        return objects.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {

        TextView tvname;
        TextView tvKm;
        CardView cv;
        RelativeLayout rl;
        LinearLayout linlayout;

        ImageView imgdelete;
        ImageView imgstart;


        public ViewHolder(View itemView) {
            super(itemView);
            Log.d(LOG_TAG, "ViewHolder");

            cv = (CardView) itemView.findViewById(R.id.cardView);

            tvname = (TextView) itemView.findViewById(R.id.tvname);
            tvKm = (TextView) itemView.findViewById(R.id.tvkm);
            imgstart = (ImageView) itemView.findViewById(R.id.imgstart);
            imgdelete = (ImageView) itemView.findViewById(R.id.imgdelete);
            linlayout = (LinearLayout) itemView.findViewById(R.id.lilayout);

            imgstart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(LOG_TAG, "Кликнули на play: " + getAdapterPosition());
                    if(!objects.get(getAdapterPosition()).getRun()) {
                        linlayout.setBackgroundColor(Color.argb(255, 76, 175, 80));
                        main.saveRun(true, objects.get(getAdapterPosition()).getId());
                        main.runAlarm(objects.get(getAdapterPosition()).getId(), getAdapterPosition());
                        objects.get(getAdapterPosition()).setRun(true);
                        imgstart.setImageResource(R.drawable.mr_ic_pause_light);


                    }
                    else {
                        linlayout.setBackgroundColor(Color.argb(255, 229, 115, 115));
                        main.saveRun(false, objects.get(getAdapterPosition()).getId());
                        objects.get(getAdapterPosition()).setRun(false);
                        main.stopAlarm(objects.get(getAdapterPosition()).getId(), getAdapterPosition());
                        imgstart.setImageResource(R.drawable.mr_ic_play_light);
                    }


                   //

                }
            });


            imgdelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(LOG_TAG, "Кликнули на крестик: " + getAdapterPosition());
                    Log.d(LOG_TAG, "имя: " + objects.get(getAdapterPosition()).getName());

                    main.deleteItem(objects.get(getAdapterPosition()).getId(), getAdapterPosition());


                }
            });

        }

    }
}
