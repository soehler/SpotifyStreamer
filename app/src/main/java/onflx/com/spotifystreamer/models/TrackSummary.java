package onflx.com.spotifystreamer.models;

import android.os.Parcelable;

import android.os.Parcel;

public class TrackSummary implements Parcelable {

    public String id;
    public String trackName;
    public String albumName;
    public String albumImage;

    public String trackPreviewUrl;
    public String artistName;
    public String trackDurationSeconds;

    public TrackSummary(String id,
                        String trackName,
                        String albumName,
                        String albumImage,
                        String trackPreviewUrl,
                        String artistName,
                        String trackDurationSeconds ){
        this.id = id;
        this.trackName = trackName;
        this.albumName= albumName;
        this.albumImage = albumImage;
        this.trackPreviewUrl =trackPreviewUrl;
        this.artistName = artistName;
        this.trackDurationSeconds = trackDurationSeconds;
    }

    // From here parcelable implementation code

    private TrackSummary(Parcel in){
        id = in.readString();
        trackName = in.readString();
        albumName = in.readString();
        albumImage = in.readString();
        trackPreviewUrl = in.readString();
        artistName = in.readString();
        trackDurationSeconds = in.readString();
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
        out.writeString(trackPreviewUrl);
        out.writeString(artistName);
        out.writeString(trackDurationSeconds);
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


