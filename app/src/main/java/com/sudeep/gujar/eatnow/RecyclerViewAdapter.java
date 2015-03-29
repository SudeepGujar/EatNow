package com.sudeep.gujar.eatnow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import sudeep.gujar.eatnow.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context mContext;
    private List<FeedItem> feedItemList;
    boolean first;

    public RecyclerViewAdapter(Context context, List<FeedItem> feedItemList) {
        this.mContext = context;
        this.feedItemList = feedItemList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        //String[] values = mDataset[position].split(",");
        FeedItem feedItem = feedItemList.get(position);
        //String countryName = values[0];
       // int flagResId = mContext.getResources().getIdentifier(values[1], "drawable", mContext.getPackageName());
        viewHolder.mTextView.setText(Html.fromHtml(feedItem.getTitle()));
        viewHolder.dist.setText(Html.fromHtml(feedItem.getDist()));
        viewHolder.address.setText(Html.fromHtml(feedItem.getAddress()));
        String ranktemp = Html.fromHtml(feedItem.getRank()).toString();
       // int rankint = Integer.parseInt(ranktemp);
        if(ranktemp == null || ranktemp.equals("")){
            ranktemp="NA";
        }
        viewHolder.rank.setText(ranktemp);
       // viewHolder.mTextView.setCompoundDrawablesWithIntrinsicBounds(flagResId, 0, 0, 0);
    }



    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTextView;
        public TextView dist;
        public TextView rank;
        public TextView address;
        ImageView thumbnail;
        //private Context mContext;


        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            mTextView = (TextView)v.findViewById(R.id.title);
            dist = (TextView)v.findViewById(R.id.distance);
            rank = (TextView) v.findViewById(R.id.rank);
            address = (TextView)v.findViewById(R.id.tip);
            thumbnail = (ImageView)v.findViewById(R.id.thumbnail);
            SharedPreferences pref01 = PreferenceManager.getDefaultSharedPreferences(mContext);
            int search = pref01.getInt("search", 0);

            if(search == 1 ){
                rank.setVisibility(View.INVISIBLE);
                dist.setVisibility(View.INVISIBLE);
                thumbnail.setImageResource(R.drawable.search_blue);
            }

        }




        @Override
        public void onClick(View v) {
           // mContext.startActivity(new Intent(mContext,Venue.class));

            FeedItem feedItem = feedItemList.get(getPosition());
            Intent intent = new Intent(mContext,Venue.class);
            intent.putExtra("name",Html.fromHtml(feedItem.getTitle()).toString());
            intent.putExtra("address",Html.fromHtml(feedItem.getAddress()).toString());
            intent.putExtra("id",Html.fromHtml(feedItem.getVenueID()).toString());
            Toast.makeText(mContext,"id ="+Html.fromHtml(feedItem.getVenueID()) + "positon = " + getPosition(),Toast.LENGTH_LONG).show();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);

            /*SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
            editor.putString("name",Html.fromHtml(feedItem.getTitle()).toString());

            if(hs){
                editor.putString("highscore"+String.valueOf(level),encoded( dst/10));}
            editor.putString("Noic", encoded(coins_yo + coin_count));
            editor.putInt("seeklen", song.getCurrentPosition());
            editor.apply();*/

        }


    }
}