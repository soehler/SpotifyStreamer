package onflx.com.spotifystreamer;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import onflx.com.spotifystreamer.models.TrackSummary;


public class ArtistTopTenListAdapter extends ArrayAdapter<TrackSummary> {

    private final Context mContext;
    private final int mLayoutResourceId;
    private ArrayList<TrackSummary> mListTracks;

    public ArtistTopTenListAdapter(Context context, int layoutResourceId, ArrayList<TrackSummary>listTracks) {

        super(context, layoutResourceId, listTracks);

        mLayoutResourceId = layoutResourceId;
        mContext = context;
        mListTracks = listTracks;
    }

    public ArrayList<TrackSummary> getAllTracks(){

        return mListTracks;

    }

    public void putAllTracks(ArrayList<TrackSummary> allTracks){

        mListTracks = allTracks;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        TrackHolder holder = null;

        if (row == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(mLayoutResourceId, parent, false);

            holder=new TrackHolder();
            holder.albumImage = (ImageView) row.findViewById(R.id.album_image);
            holder.albumtName= (TextView) row.findViewById(R.id.album_name);
            holder.trackName = (TextView) row.findViewById(R.id.track_name);

            row.setTag(holder);

        }else{
            holder = (TrackHolder)row.getTag();
        }

        TrackSummary track = mListTracks.get(position);
        if (track.albumImage.equals("")) {
            holder.albumImage.setImageResource( R.drawable.img_not_found);
        }else{
            Picasso.with(mContext).load(track.albumImage).resize(200, 200).centerCrop().into(holder.albumImage);
        }
        holder.albumtName.setText(track.albumName);
        holder.trackName.setText(track.trackName);
        return row;
    }


    static class TrackHolder
    {
        ImageView albumImage;
        TextView albumtName;
        TextView trackName;
    }
}


