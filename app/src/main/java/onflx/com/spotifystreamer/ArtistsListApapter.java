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

import kaaes.spotify.webapi.android.models.Artist;

public class ArtistsListApapter extends ArrayAdapter<Artist> {

    private final Context mContext;
    private final int mLayoutResourceId;
    private List<Artist> mListArtist;

    public ArtistsListApapter(Context context, int layoutResourceId, List<Artist>listArtist) {

        super(context, layoutResourceId, listArtist);

        mLayoutResourceId = layoutResourceId;
        mContext = context;
        mListArtist = listArtist;
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

        Artist artist = mListArtist.get(position);
        if (artist.images.size()>0) {
            Picasso.with(mContext).load(artist.images.get(0).url).resize(200, 200).centerCrop().into(holder.artistImage);
        }else{
            holder.artistImage.setImageResource( R.drawable.img_not_found);
        }
        holder.artistName.setText(artist.name);

        return row;
    }

    static class ArtistHolder
    {
        ImageView artistImage;
        TextView artistName;
    }
}
