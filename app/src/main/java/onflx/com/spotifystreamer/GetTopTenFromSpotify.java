package onflx.com.spotifystreamer;

import android.os.AsyncTask;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

public class GetTopTenFromSpotify extends AsyncTask<String, Void, List<Track>> {

    String TAG = this.getClass().getName();

    private ArtistTopTenListAdapter mArtistsTopTenListAdapter;

    protected GetTopTenFromSpotify withAdapter(ArtistTopTenListAdapter artistTopTenListAdapter) {
        mArtistsTopTenListAdapter = artistTopTenListAdapter;
        return this;
    }

    @Override
    protected void onPostExecute(List<Track> tracks) {

        if (tracks != null) {
            mArtistsTopTenListAdapter.clear();
            for (Track track : tracks) {
                mArtistsTopTenListAdapter.add(track);
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
        Tracks tracks;

        Map<String,Object> queryMap;
        queryMap = new HashMap();
        queryMap.put("country","us");


        try {
            spotifyApi = new SpotifyApi();
            spotifyService = spotifyApi.getService();
            tracks = spotifyService.getArtistTopTrack(params[0],queryMap);
            return tracks.tracks;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }
}
