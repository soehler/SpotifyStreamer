package onflx.com.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import onflx.com.spotifystreamer.models.TrackSummary;

/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistTopTenActivityFragment extends Fragment {

    private ArtistTopTenListAdapter mAdapter;
    private ArrayList artistTopTenListFromCache;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState!=null) {
            artistTopTenListFromCache = savedInstanceState.getParcelableArrayList("listCache");
        }else{
            artistTopTenListFromCache = null;
        }

        // String artistName ="";
        String artistId="";
        ArrayList artistTopTenList;

        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            artistId = intent.getStringExtra(Intent.EXTRA_TEXT);
        }

        artistTopTenList = new ArrayList<TrackSummary>();

        mAdapter = new ArtistTopTenListAdapter(getActivity(),R.layout.top_ten_list_view_item,artistTopTenList);

        GetTopTenFromSpotify getTopTenFromSpotify = new GetTopTenFromSpotify();
        getTopTenFromSpotify.withContext(getActivity()).withAdapter(mAdapter).withCache(artistTopTenListFromCache).execute(artistId);

        View view = inflater.inflate(R.layout.fragment_artist_top_ten, container, false);

        ListView topTenListView = (ListView)view.findViewById(R.id.artist_top10_listview);
        topTenListView.setAdapter(mAdapter);
        topTenListView.setOnItemClickListener(new OnItemClickListener());

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("listCache",mAdapter.getAllTracks());
    }



    private class OnItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getActivity(), "Clicked on track "+ mAdapter.getItem(position).id, Toast.LENGTH_SHORT).show();
        }
    }
}
