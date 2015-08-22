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

import kaaes.spotify.webapi.android.models.Artist;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArtistsListAdapter mAdapter;
    private int mListPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_ITEM = "selected_item";
    private Bundle args = new Bundle();
    private Activity mActivity;
    private ListView artistListView;

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


        mAdapter = new ArtistsListAdapter(getActivity(),R.layout.artists_list_view_item,new ArrayList<Artist>());

        view = inflater.inflate(R.layout.fragment_main, container, false);

        artistListView = (ListView)view.findViewById(R.id.artist_listview);
        artistListView.setAdapter(mAdapter);
        artistListView.setOnItemClickListener(new OnItemClickListener());
        searchArtist = (EditText)view.findViewById(R.id.searchArtist);
        searchArtist.setOnEditorActionListener(new OnEditorActionListener());

        if (savedInstanceState != null) {
            mListPosition = savedInstanceState.getInt(SELECTED_ITEM);
        }

        //Scroll to last position
        if (mListPosition >-1){
            artistListView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    artistListView.smoothScrollToPosition(mListPosition);
                }
            },1000);
        }

        return view;

    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        //The screen has been rotated, reload data

        SharedPreferences sharedPref;
        String lastArtistSearch;
        EditText editText;

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        lastArtistSearch = sharedPref.getString(getString(R.string.shared_preference_last_searched_artist), getString(R.string.empty_string));
        editText = (EditText)getActivity().findViewById(R.id.searchArtist);
        editText.setText(lastArtistSearch);


        if (lastArtistSearch.length()>0 && !(mAdapter==null)){
            GetArtistFromSpotify getArtistFromSpotify = new GetArtistFromSpotify();
            getArtistFromSpotify.withContext(getActivity()).withAdapter(mAdapter).execute(lastArtistSearch);
            editText.setSelection(editText.getText().length());
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mListPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_ITEM, mListPosition);
        }

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
                args.putString("id", mAdapter.getItem(position).id.toString());
                args.putString("name", mAdapter.getItem(position).name);
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
                        .putExtra(Intent.EXTRA_TITLE,mAdapter.getItem(position).name);
                startActivity(intent);
            }
        }
    }

}
