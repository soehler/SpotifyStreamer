package onflx.com.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import onflx.com.spotifystreamer.models.ArtistSummary;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArtistsListAdapter mAdapter;
    private int mListPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_ITEM = "selected_item";
    private Activity mActivity;
    private ListView mArtistListView;
    private ArrayList <ArtistSummary> mArtistsListFromCache = new ArrayList<ArtistSummary>();
    private String mLastArtistSearch;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view;
        EditText searchArtist;

        if (savedInstanceState!=null) {
            mArtistsListFromCache = savedInstanceState.getParcelableArrayList("listCacheArtists");
        }

        getLastSearchedArtist();


        if (mArtistsListFromCache.size() > 0 ) {
            mAdapter = new ArtistsListAdapter(getActivity(), R.layout.artists_list_view_item, mArtistsListFromCache);
        } else{
            mAdapter = new ArtistsListAdapter(getActivity(), R.layout.artists_list_view_item, new ArrayList<ArtistSummary>());
            if (mLastArtistSearch.length() > 0 ) {
                GetArtistFromSpotify getArtistFromSpotify = new GetArtistFromSpotify();
                getArtistFromSpotify.withContext(getActivity()).withAdapter(mAdapter).execute(mLastArtistSearch);
            }
        }

        view = inflater.inflate(R.layout.fragment_main, container, false);

        mArtistListView = (ListView)view.findViewById(R.id.artist_listview);
        mArtistListView.setAdapter(mAdapter);
        mArtistListView.setOnItemClickListener(new OnItemClickListener());
        searchArtist = (EditText)view.findViewById(R.id.searchArtist);
        if (mLastArtistSearch.length() > 0 ) {
            searchArtist.setText(mLastArtistSearch);
            searchArtist.setSelection(searchArtist.getText().length());
        }
        searchArtist.setOnEditorActionListener(new OnEditorActionListener());

        if (savedInstanceState != null) {
            mListPosition = savedInstanceState.getInt(SELECTED_ITEM);
        }

        //Scroll to last position
        if (mListPosition >-1){
            mArtistListView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mArtistListView.smoothScrollToPosition(mListPosition);
                }
            }, 1000);
        }

        return view;

    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mListPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_ITEM, mListPosition);
        }
        outState.putParcelableArrayList("listCacheArtists", mAdapter.getAllArtists());
    }


    private class OnEditorActionListener implements TextView.OnEditorActionListener{

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            SharedPreferences.Editor editor;
            EditText editText;
            SharedPreferences sharedPref;

            GetArtistFromSpotify getArtistFromSpotify = new GetArtistFromSpotify();
            getArtistFromSpotify.withContext(getActivity()).withAdapter(mAdapter).execute(v.getText().toString());

            if (getActivity().findViewById(R.id.artist_top10_listview)!= null){
                ListView lv = (ListView)getActivity().findViewById(R.id.artist_top10_listview);
                lv.setAdapter(null);
            }

            mArtistListView.clearChoices();

            editText = (EditText) getActivity().findViewById(R.id.searchArtist);
            sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            editor = sharedPref.edit();
            editor.putString(getString(R.string.shared_preference_last_searched_artist), editText.getText().toString());
            editor.commit();

            return false;
        }
    }

    private class OnItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            mListPosition = position;


            if (getActivity().findViewById(R.id.artist_top_ten_container) != null){
                // It is a tablet !
                Bundle args = new Bundle();
                args.putString("id", mAdapter.getItem(position).id.toString());
                args.putString("name", mAdapter.getItem(position).artistName);
                ArtistTopTenFragment fragment = new ArtistTopTenFragment();
                fragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.artist_top_ten_container, fragment, "TTFTAG")
                        .commit();
            }else{
                //It is a phone !
                Intent intent;
                intent = new Intent(getActivity(),ArtistTopTenActivity.class)
                        .putExtra(Intent.EXTRA_TEXT,mAdapter.getItem(position).id)
                        .putExtra(Intent.EXTRA_TITLE,mAdapter.getItem(position).artistName);
                startActivity(intent);
            }
        }
    }

    private void getLastSearchedArtist(){

        //retrieve last searched artist
        SharedPreferences sharedPref;
        EditText editText;

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        mLastArtistSearch = sharedPref.getString(getString(R.string.shared_preference_last_searched_artist), getString(R.string.empty_string));

    }

}
