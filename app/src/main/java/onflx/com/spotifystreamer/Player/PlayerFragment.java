package onflx.com.spotifystreamer.Player;
/*
 * Created by soehler on 18/08/15 20:09.
 */

import android.app.Fragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import onflx.com.spotifystreamer.R;
import onflx.com.spotifystreamer.models.TrackSummary;

public class PlayerFragment extends Fragment {

    private ArrayList<TrackSummary> mTracks;
    private MediaPlayer mMediaPlayer;
    private int mPosition;
    private SeekBar seekBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.audio_player, container, false);
        seekBar = (SeekBar) view.findViewById(R.id.seekPlaybackPosition);
        //fixed because previews are limited to 30 seconds
        seekBar.setMax(30*1000);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity().getIntent()!=null) {
            mTracks = getActivity().getIntent().getParcelableArrayListExtra("tracks");
            mPosition= getActivity().getIntent().getIntExtra("position",0);

            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }

        try {
            mMediaPlayer.setDataSource(mTracks.get(mPosition).trackPreviewUrl);
            mMediaPlayer.setOnPreparedListener(new OnPreparedListener());
            mMediaPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener());
            mMediaPlayer.prepareAsync();

        } catch (IllegalArgumentException e) {
            String x = "";
        }catch (IllegalStateException e) {
            String x = "";
        }catch (IOException e) {
            String x = "";
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    private class OnPreparedListener implements MediaPlayer.OnPreparedListener{

        @Override
        public void onPrepared(MediaPlayer mp) {
            final Timer timer;

            mp.start();

            //update music play progress
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mMediaPlayer.isPlaying()) {
                                seekBar.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        seekBar.setProgress(mMediaPlayer.getCurrentPosition());
                                    }
                                });
                            } else {
                                timer.cancel();
                                timer.purge();
                            }
                        }
                    });
                }
            }, 0, 1000);
        }
    }



    private class OnBufferingUpdateListener implements MediaPlayer.OnBufferingUpdateListener{

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            //seekBar.setProgress(percent);
        }
    }
}
