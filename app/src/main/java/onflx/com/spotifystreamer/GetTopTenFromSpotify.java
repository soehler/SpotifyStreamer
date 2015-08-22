package onflx.com.spotifystreamer;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import onflx.com.spotifystreamer.models.TrackSummary;

public class GetTopTenFromSpotify extends AsyncTask<String, Void, List<TrackSummary>> {

    String TAG = this.getClass().getName();
    private Context mContext;
    private ArtistTopTenListAdapter mArtistsTopTenListAdapter;
    private ArrayList<TrackSummary> mListCache;

    protected GetTopTenFromSpotify withAdapter(ArtistTopTenListAdapter artistTopTenListAdapter) {
        mArtistsTopTenListAdapter = artistTopTenListAdapter;
        return this;
    }

    protected GetTopTenFromSpotify withContext(Context context) {
        mContext = context;
        return this;
    }

    protected GetTopTenFromSpotify withCache(ArrayList<TrackSummary> listCache) {
        mListCache = listCache;
        return this;
    }

    @Override
    protected void onPostExecute(List<TrackSummary> tracks) {

        if (tracks != null) {
            mArtistsTopTenListAdapter.clear();
            for (TrackSummary track : tracks) {
                mArtistsTopTenListAdapter.add(track);
            }
            if (mContext != null && mArtistsTopTenListAdapter.isEmpty()){
                Toast.makeText(mContext, R.string.no_tracks_found_msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected List doInBackground(String... params) {

        SpotifyApi spotifyApi;
        SpotifyService spotifyService;
        Tracks tracks;
        List<TrackSummary> tracksSummary;

        tracksSummary = new ArrayList<TrackSummary>();

        // missing input parameter
        if (params.length == 0) {
            return null;
        }

        // data is cached, return it
        if (mListCache != null) {
            tracksSummary = mListCache;
        }

        //Data is not cached, fetch it from Spotify

        if (tracksSummary.size() == 0){

            Map<String, Object> queryMap;
            queryMap = new HashMap();
            queryMap.put(mContext.getString(R.string.spotify_query_param_country),
                    mContext.getString(R.string.spotify_query_param_country_value_us));
            try {
                spotifyApi = new SpotifyApi();
                spotifyService = spotifyApi.getService();
                tracks = spotifyService.getArtistTopTrack(params[0], queryMap);

                if (tracks != null) {
                    for (Track track : tracks.tracks) {

                        tracksSummary.add(new TrackSummary(track.id, track.name, track.album.name,
                                track.album.images.get(0).url, track.preview_url,
                                track.artists.get(0).name,
                                String.valueOf(track.duration_ms / 1000)));
                    }
                }


            } catch (Exception e) {
                //TODO:Just log it, in production requires handling
                Log.e(TAG, e.getMessage());
                if (tracksSummary.size()==0) {
                    tracksSummary = null;
                }
            }
        }


        return tracksSummary;

    }
}

