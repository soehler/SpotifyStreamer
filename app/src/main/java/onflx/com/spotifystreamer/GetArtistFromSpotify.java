package onflx.com.spotifystreamer;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import onflx.com.spotifystreamer.models.ArtistSummary;


public class GetArtistFromSpotify extends AsyncTask<String, Void, List<ArtistSummary>> {

    String TAG = this.getClass().getName();
    private Context mContext;
    private ArtistsListAdapter mArtistsAdapter;
    private ArrayList<ArtistSummary> artistsSummary;

    protected GetArtistFromSpotify withAdapter(ArtistsListAdapter artistsAdapter) {
        mArtistsAdapter = artistsAdapter;
        return this;
    }

    protected GetArtistFromSpotify withContext(Context context) {
        mContext = context;
        return this;
    }

    @Override
    protected void onPostExecute(List<ArtistSummary> artists) {

        if (artists != null) {
            mArtistsAdapter.clear();
            for (ArtistSummary artist : artists) {
                mArtistsAdapter.add(artist);
            }
            if (mContext != null && mArtistsAdapter.isEmpty()){
                Toast.makeText(mContext, R.string.artist_not_found_msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected List doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }

        SpotifyApi spotifyApi;
        SpotifyService spotifyService;
        ArtistsPager artistsPager;

        try {
            spotifyApi = new SpotifyApi();
            spotifyService = spotifyApi.getService();
            artistsPager = spotifyService.searchArtists(params[0]);
            String imageUrl;

            if (artistsPager!=null){
                artistsSummary = new <ArtistSummary>ArrayList();
                for (Artist artist:artistsPager.artists.items){
                    if (artist.images.size()>0){
                        imageUrl=artist.images.get(0).url;
                    }else{
                        imageUrl = "";
                    }
                    artistsSummary.add(new ArtistSummary(artist.id,
                                                         artist.name,
                                                         imageUrl));
                }
            }


        } catch (Exception e) {
            //TODO:Just log it, in production requires handling
            Log.e(TAG, e.getMessage());
        }

        return artistsSummary;
    }
}
