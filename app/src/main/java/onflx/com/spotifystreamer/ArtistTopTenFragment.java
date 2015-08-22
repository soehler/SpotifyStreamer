package onflx.com.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import onflx.com.spotifystreamer.models.TrackSummary;

/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistTopTenFragment extends Fragment {

    private ArtistTopTenListAdapter mAdapter;
    private ArrayList <TrackSummary> artistTopTenListFromCache;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String artistId = "";
        artistTopTenListFromCache = new ArrayList();
        ArrayList artistTopTenList;
        Intent intent = getActivity().getIntent();
        View view;


        if (savedInstanceState!=null) {
            artistTopTenListFromCache = savedInstanceState.getParcelableArrayList("listCache");
        }

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            artistId = intent.getStringExtra(Intent.EXTRA_TEXT);
        }

        Bundle arguments = getArguments();
        if (arguments != null) {
            artistId = arguments.getString("id");
        }



        if (artistTopTenListFromCache.size() == 0){
            artistTopTenList = new ArrayList();
            mAdapter = new ArtistTopTenListAdapter(getActivity(),R.layout.top_ten_list_view_item,artistTopTenList);
            if (!artistId.equals("")) {
                artistTopTenList = new ArrayList();
                mAdapter = new ArtistTopTenListAdapter(getActivity(),R.layout.top_ten_list_view_item,artistTopTenList);
                GetTopTenFromSpotify getTopTenFromSpotify = new GetTopTenFromSpotify();
                getTopTenFromSpotify.withContext(getActivity()).withAdapter(mAdapter).execute(artistId);
            }
        }else{
            mAdapter = new ArtistTopTenListAdapter(getActivity(),R.layout.top_ten_list_view_item,artistTopTenListFromCache);
        }

        view = inflater.inflate(R.layout.fragment_artist_top_ten, container, false);

        ListView topTenListView = (ListView)view.findViewById(R.id.artist_top10_listview);
        topTenListView.setAdapter(mAdapter);
        topTenListView.setOnItemClickListener(new OnItemClickListener());

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(getString(R.string.parcelable_toptenlist),mAdapter.getAllTracks());
    }


    private class OnItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        // TODO: Just a place marker to next project interaction, play a track
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (getActivity().findViewById(R.id.artist_top_ten_container) != null){
                //It is a tablet !
                FragmentManager fm = getActivity().getSupportFragmentManager();
                PlayerFragment playerFragment = new PlayerFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                bundle.putParcelableArrayList("tracks", mAdapter.getAllTracks());
                playerFragment.setArguments(bundle);
                playerFragment.show(fm, "play_fragment");
            }else{
                //It is a phone !
                Intent intent;
                intent = new Intent(getActivity(),PlayerActivity.class)
                        .putParcelableArrayListExtra("tracks", mAdapter.getAllTracks())
                        .putExtra("position", position);
                startActivity(intent);
            }


        }
    }
}
