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

import kaaes.spotify.webapi.android.models.Track;

/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistTopTenActivityFragment extends Fragment {

    private ArtistTopTenListAdapter mAdapter;

    public ArtistTopTenActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAdapter = new ArtistTopTenListAdapter(getActivity(),R.layout.top_ten_list_view_item,new ArrayList<Track>());

        View view = inflater.inflate(R.layout.fragment_artist_top_ten, container, false);

        String artistName ="";
        String artistId="";
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            artistId = intent.getStringExtra(Intent.EXTRA_TEXT);
            artistName = intent.getStringExtra(Intent.EXTRA_TITLE);
        }
        getActivity().setTitle(artistName + "'s Top 10");

        ListView topTenListView = (ListView)view.findViewById(R.id.artist_top10_listview);
        topTenListView.setAdapter(mAdapter);
        topTenListView.setOnItemClickListener(new OnItemClickListener());

        GetTopTenFromSpotify getTopTenFromSpotify = new GetTopTenFromSpotify();
        getTopTenFromSpotify.withAdapter(mAdapter).execute(artistId);

        return view;
    }

    private class OnItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getActivity(), "Clicked on track "+ mAdapter.getItem(position).id, Toast.LENGTH_SHORT).show();
        }
    }
}
