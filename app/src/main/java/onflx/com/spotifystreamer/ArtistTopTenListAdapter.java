package onflx.com.spotifystreamer;


import android.content.Context;
import android.widget.ArrayAdapter;

import kaaes.spotify.webapi.android.models.Tracks;

public class ArtistTopTenListAdapter extends ArrayAdapter<Tracks> {

    public ArtistTopTenListAdapter(Context context, int resource) {
        super(context, resource);
    }
}
