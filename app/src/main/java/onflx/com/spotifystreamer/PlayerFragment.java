package onflx.com.spotifystreamer;
/*
 * Created by soehler on 18/08/15 20:09.
 */

import android.app.Dialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import onflx.com.spotifystreamer.models.TrackSummary;

public class PlayerFragment extends DialogFragment {

    private ArrayList<TrackSummary> mTracks;
    private MediaPlayer mMediaPlayer;
    private int mPosition;
    private int mTrackPositionPlaying;
    private SeekBar seekBar;
    private TextView tvTrackName;
    private TextView tvAlbumName;
    private ImageView ivAlbumImage;
    private TextView tvArtistName;
    private TextView tvTrackStart;
    private TextView tvTrackEnd;
    private ImageButton ibPrevious;
    private ImageButton ibPlayPause;
    private ImageButton ibNext;
    private Timer timer;
    private boolean mPlayProcessIsUpdating;

    public void PlayerFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.audio_player, container, false);

        seekBar = (SeekBar) view.findViewById(R.id.seekPlaybackPosition);
        tvTrackName = (TextView) view.findViewById(R.id.textTrackName);
        tvAlbumName = (TextView) view.findViewById(R.id.textAlbumName);
        ivAlbumImage = (ImageView) view.findViewById(R.id.imageAlbumArtwork);
        tvArtistName = (TextView) view.findViewById(R.id.textArtistName);
        tvTrackStart = (TextView) view.findViewById(R.id.textTrackStart);
        tvTrackEnd = (TextView) view.findViewById(R.id.textTrackEnd);
        ibPrevious = (ImageButton) view.findViewById(R.id.imageBPrevious);
        ibPlayPause = (ImageButton) view.findViewById(R.id.imageBPlayPause);
        ibNext = (ImageButton) view.findViewById(R.id.imageBNext);
        mMediaPlayer = new MediaPlayer();

        //fixed because previews are limited to 30 seconds
        seekBar.setMax(30 * 1000);

        ibPrevious.setOnClickListener(new OnClickListener());
        ibPlayPause.setOnClickListener(new OnClickListener());
        ibNext.setOnClickListener(new OnClickListener());
        mMediaPlayer.setOnPreparedListener(new OnPreparedListener());
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener());

        Dialog dialog = getDialog();
        if (dialog!=null){
            dialog.setTitle(getResources().getString(R.string.title_player));
        }


        playMusic();

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Screen rotation resume playing exactly where it was before orientation change
        if (savedInstanceState != null) {

            mPosition = savedInstanceState.getInt("savedTrackPosition");
            mTrackPositionPlaying = savedInstanceState.getInt("savedTrackPositionPlaying");
            mTracks = savedInstanceState.getParcelableArrayList("savedTracks");

        }else{

            if (getActivity().findViewById(R.id.artist_top_ten_container) != null){
                //It is a tablet !
                Bundle bundle = this.getArguments();
                if (bundle != null){
                    mTracks = bundle.getParcelableArrayList("tracks");
                    mPosition= bundle.getInt("position",0);
                }
            }else{
                //It is a phone !
                if (getActivity().getIntent()!=null) {
                    mTracks = getActivity().getIntent().getParcelableArrayListExtra("tracks");
                    mPosition= getActivity().getIntent().getIntExtra("position", 0);
                }
            }

        }




    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("savedTrackPosition", mPosition);
        outState.putInt("savedTrackPositionPlaying",mMediaPlayer.getCurrentPosition());
        outState.putParcelableArrayList("savedTracks",mTracks);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (timer!=null){
            timer.cancel();
            timer.purge();
            mPlayProcessIsUpdating=false;
        }

        if (mMediaPlayer!= null){
            if(mMediaPlayer.isPlaying()){
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }



    private class OnPreparedListener implements MediaPlayer.OnPreparedListener{

        @Override
        public void onPrepared(MediaPlayer mp) {
            tvTrackStart.setText("0:00");
            tvTrackEnd.setText("0:30"); // hardcoded because all music previews last for 30 secs
            if (mTrackPositionPlaying > 0){
                mp.seekTo(mTrackPositionPlaying);
                mTrackPositionPlaying = 0;
            }
            mp.start();
            playerControlsEnabled(true);
            ibPlayPause.setTag("pause");
            startUpdatePlayProgress();
        }
    }

    public void displayStatusMessage(String message){
        tvTrackStart.setText(message);
        tvTrackEnd.setText("");
    }

    public void playMusic(){

        //Just to make sure no simultaneous playing
        if (mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }

        if (mTracks!= null) {

            //Display Track details
            tvTrackName.setText(mTracks.get(mPosition).trackName);
            tvAlbumName.setText(mTracks.get(mPosition).albumName);
            tvArtistName.setText(mTracks.get(mPosition).artistName);
            if (mTracks.get(mPosition).albumImage.trim().equals("")){
                Picasso.with(getActivity()).load(R.drawable.img_not_found).resize(150, 150).centerCrop().into(ivAlbumImage);
            }else {
                Picasso.with(getActivity()).load(mTracks.get(mPosition).albumImage).resize(300, 300).centerCrop().into(ivAlbumImage);
            }

            //adjust controls
            ibPlayPause.setTag("play");
            ((ImageButton)ibPlayPause).setImageResource(android.R.drawable.ic_media_pause);
            seekBar.setProgress(0);

            // Play track
            try {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource(mTracks.get(mPosition).trackPreviewUrl);
                displayStatusMessage(getResources().getString(R.string.preparing_to_play_msg));
                playerControlsEnabled(false);
                mMediaPlayer.prepareAsync();

            } catch (IllegalArgumentException e) {
                displayStatusMessage(getResources().getString(R.string.ilegal_argument_exception_msg));
            } catch (IllegalStateException e) {
                displayStatusMessage(getResources().getString(R.string.illegal_state_exception_msg));
            } catch (IOException e) {
                displayStatusMessage(getResources().getString(R.string.io_exception_mediaplayer_msg));
            }
        }
    }

    private void playerControlsEnabled (boolean bol){
        ibPlayPause.setEnabled(bol);
        ibNext.setEnabled(bol);
        ibPrevious.setEnabled(bol);
    }
    public void startUpdatePlayProgress(){
        //update music play progress

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mMediaPlayer.isPlaying()) {
                            mPlayProcessIsUpdating = true;
                            seekBar.post(new Runnable() {
                                @Override
                                public void run() {
                                    seekBar.setProgress(mMediaPlayer.getCurrentPosition());
                                }
                            });
                        } else {
                            timer.cancel();
                            timer.purge();
                            mPlayProcessIsUpdating = false;
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    private class OnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser){
                mMediaPlayer.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // avoid scratching
            mMediaPlayer.pause();
            if (!mPlayProcessIsUpdating){
                startUpdatePlayProgress();
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // avoid scratching
            mMediaPlayer.start();
            if (!mPlayProcessIsUpdating){
                startUpdatePlayProgress();
            }
        }
    }
    private class OnClickListener implements ImageButton.OnClickListener{

        @Override
        public void onClick(View v) {
            String TAG = (String)v.getTag();
            switch (v.getId()){
                case R.id.imageBPrevious:
                    mMediaPlayer.stop();
                    mMediaPlayer.reset();
                    if (mPosition > 0){
                        mPosition = mPosition -1;
                    }
                    playMusic();
                    break;

                case R.id.imageBPlayPause:
                    if (mMediaPlayer.isPlaying()){
                        v.setTag("pause");
                        ((ImageButton)v).setImageResource(android.R.drawable.ic_media_play);
                        mMediaPlayer.pause();
                    } else {
                        v.setTag("play");
                        ((ImageButton)v).setImageResource(android.R.drawable.ic_media_pause);
                        mMediaPlayer.start();
                        startUpdatePlayProgress();
                    }
                    break;

                case R.id.imageBNext:
                    mMediaPlayer.stop();
                    mMediaPlayer.reset();
                    if (mPosition < mTracks.size()-1){
                        mPosition = mPosition + 1;
                    }
                    playMusic();
                    break;
            }
        }
    }



}
