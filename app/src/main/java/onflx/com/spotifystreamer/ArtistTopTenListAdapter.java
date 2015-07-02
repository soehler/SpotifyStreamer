package onflx.com.spotifystreamer;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;


public class ArtistTopTenListAdapter extends ArrayAdapter<Track> {

    private final Context mContext;
    private final int mLayoutResourceId;
    private List<Track> mListTracks;

    public ArtistTopTenListAdapter(Context context, int layoutResourceId, List<Track>listTracks) {

        super(context, layoutResourceId, listTracks);

        mLayoutResourceId = layoutResourceId;
        mContext = context;
        mListTracks = listTracks;
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

        Track track = mListTracks.get(position);
        if (track.album.images.size()>0) {
            Picasso.with(mContext).load(track.album.images.get(0).url).resize(200, 200).centerCrop().into(holder.albumImage);
        }else{
            holder.albumImage.setImageResource( R.drawable.img_not_found);
        }
        holder.albumtName.setText(track.album.name);
        holder.trackName.setText(track.name);
        return row;
    }


    static class TrackHolder
    {
        ImageView albumImage;
        TextView albumtName;
        TextView trackName;
    }
}


