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

import onflx.com.spotifystreamer.models.ArtistSummary;

public class ArtistsListAdapter extends ArrayAdapter<ArtistSummary> {

    private final Context mContext;
    private final int mLayoutResourceId;
    private ArrayList<ArtistSummary> mListArtists;

    public ArtistsListAdapter(Context context, int layoutResourceId, ArrayList<ArtistSummary> listArtist) {

        super(context, layoutResourceId, listArtist);

        mLayoutResourceId = layoutResourceId;
        mContext = context;
        mListArtists = listArtist;
    }

    public ArrayList<ArtistSummary> getAllArtists(){

        return mListArtists;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ArtistHolder holder = null;

        if (row == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(mLayoutResourceId, parent, false);

            holder=new ArtistHolder();
            holder.artistImage = (ImageView) row.findViewById(R.id.album_image);
            holder.artistName= (TextView) row.findViewById(R.id.album_name);

            row.setTag(holder);

        }else{
            holder = (ArtistHolder)row.getTag();
        }

        ArtistSummary artist = mListArtists.get(position);
        if (artist.artistImageUrl.equals("")) {
            Picasso.with(mContext).load(R.drawable.img_not_found).resize(150, 150).centerCrop().into(holder.artistImage);
        }else{
            Picasso.with(mContext).load(artist.artistImageUrl).resize(150, 150).centerCrop().into(holder.artistImage);
        }
        holder.artistName.setText(artist.artistName);
        return row;
    }

    static class ArtistHolder
    {
        ImageView artistImage;
        TextView artistName;
    }
}
