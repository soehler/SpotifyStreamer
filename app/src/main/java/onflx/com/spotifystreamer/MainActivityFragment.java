package onflx.com.spotifystreamer;

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

    private ArtistsListApapter mAdapter;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mAdapter = new ArtistsListApapter(getActivity(),R.layout.artists_list_view_item,new ArrayList<Artist>());

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ListView artistListView = (ListView)view.findViewById(R.id.artist_listview);
        artistListView.setAdapter(mAdapter);
        artistListView.setOnItemClickListener(new OnItemClickListener());
        EditText searchArtist = (EditText)view.findViewById(R.id.searchArtist);
        searchArtist.setOnEditorActionListener(new OnEditorActionListener());

        return view;

    }


    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        //The screen has been rotated, reload data

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String lastArtistSearch = sharedPref.getString("LAST_ARTIST_SEARCH", "");
        EditText editText = (EditText)getActivity().findViewById(R.id.searchArtist);
        editText.setText(lastArtistSearch);

        if (lastArtistSearch.length()>0 && !(mAdapter==null)){
            GetArtistFromSpotify getArtistFromSpotify = new GetArtistFromSpotify();
            getArtistFromSpotify.withContext(getActivity()).withAdapter(mAdapter).execute(lastArtistSearch);
            editText.setSelection(editText.getText().length());
        }

        if (savedInstanceState != null) {
            ListView artistListView = (ListView) getActivity().findViewById(R.id.artist_listview);
            artistListView.setVerticalScrollbarPosition(savedInstanceState.getInt("ArtistListPosition"));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ListView artistListView = (ListView)getActivity().findViewById(R.id.artist_listview);
        outState.putInt("ArtistListPosition",artistListView.getVerticalScrollbarPosition());
    }

    private class OnEditorActionListener implements TextView.OnEditorActionListener{

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            GetArtistFromSpotify getArtistFromSpotify = new GetArtistFromSpotify();
            getArtistFromSpotify.withContext(getActivity()).withAdapter(mAdapter).execute(v.getText().toString());
            return false;
        }
    }

    private class OnItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            EditText editText = (EditText) getActivity().findViewById(R.id.searchArtist);
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("LAST_ARTIST_SEARCH", editText.getText().toString());
            editor.commit();

            Intent intent = new Intent(getActivity(),ArtistTopTenActivity.class)
                    .putExtra(Intent.EXTRA_TEXT,mAdapter.getItem(position).id)
                    .putExtra(Intent.EXTRA_TITLE,mAdapter.getItem(position).name);
            startActivity(intent);

        }
    }

}
