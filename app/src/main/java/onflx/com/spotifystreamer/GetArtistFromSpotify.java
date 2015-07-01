package onflx.com.spotifystreamer;

import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;


public class GetArtistFromSpotify extends AsyncTask<String, Void, List<Artist>> {

    String TAG = this.getClass().getName();

    private ArtistsListApapter mArtistsAdapter;

    protected GetArtistFromSpotify withAdapter(ArtistsListApapter artistsAdapter) {
        mArtistsAdapter = artistsAdapter;
        return this;
    }

    @Override
    protected void onPostExecute(List<Artist> artists) {

        if (artists != null) {
            mArtistsAdapter.clear();
            for (Artist artist : artists) {
                mArtistsAdapter.add(artist);
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
            return artistsPager.artists.items;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }
}
