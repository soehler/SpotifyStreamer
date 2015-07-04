package onflx.com.spotifystreamer.models;

import android.os.Parcelable;

/*
 *  Information on how to create a parcelable used on this implementation, learned from:
 *
 *  http://stackoverflow.com/questions/12503836/how-to-save-custom-arraylist-on-android-screen-rotate
 *
 */

import android.os.Parcel;

public class TrackSummary implements Parcelable {

    public String id;
    public String trackName;
    public String albumName;
    public String albumImage;

    public TrackSummary(String id, String trackName, String albumName, String albumImage){
        this.id = id;
        this.trackName = trackName;
        this.albumName= albumName;
        this.albumImage = albumImage;
    }

    // From here parceable implementation code

    private TrackSummary(Parcel in){
        id = in.readString();
        trackName = in.readString();
        albumName = in.readString();
        albumImage = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out,int flags){
        out.writeString(id);
        out.writeString(trackName);
        out.writeString(albumName);
        out.writeString(albumImage);
    }


    public static final Parcelable.Creator<TrackSummary> CREATOR = new Parcelable.Creator<TrackSummary>() {
        public TrackSummary createFromParcel(Parcel in) {
            return new TrackSummary(in);
        }

        public TrackSummary[] newArray(int size) {
            return new TrackSummary[size];
        }
    };
 }


