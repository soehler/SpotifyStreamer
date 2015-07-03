package onflx.com.spotifystreamer;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

public class GetTopTenFromSpotify extends AsyncTask<String, Void, List<Track>> {

    String TAG = this.getClass().getName();
    private Context mContext;

    private ArtistTopTenListAdapter mArtistsTopTenListAdapter;

    protected GetTopTenFromSpotify withAdapter(ArtistTopTenListAdapter artistTopTenListAdapter) {
        mArtistsTopTenListAdapter = artistTopTenListAdapter;
        return this;
    }

    protected GetTopTenFromSpotify withContext(Context context) {
        mContext = context;
        return this;
    }

    @Override
    protected void onPostExecute(List<Track> tracks) {

        if (tracks != null) {
            mArtistsTopTenListAdapter.clear();
            for (Track track : tracks) {
                mArtistsTopTenListAdapter.add(track);
            }
            if (mContext != null && mArtistsTopTenListAdapter.isEmpty()){
                Toast.makeText(mContext, "No tracks found. Try another artist !", Toast.LENGTH_SHORT).show();
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
