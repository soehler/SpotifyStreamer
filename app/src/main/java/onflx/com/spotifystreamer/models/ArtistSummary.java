package onflx.com.spotifystreamer.models;/*
 * Created by soehler on 23/08/15 19:35.
 */

import android.os.Parcel;
import android.os.Parcelable;

public class ArtistSummary implements Parcelable {

    public String id;
    public String artistName;
    public String artistImageUrl;

    public ArtistSummary( String id,
                          String artistName,
                          String artistImageUrl){
        this.id = id;
        this.artistName = artistName;
        this.artistImageUrl = artistImageUrl;
    }


    private ArtistSummary(Parcel in){
        id = in.readString();
        artistName = in.readString();
        artistImageUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(artistName);
        dest.writeString(artistImageUrl);
    }


    public static final Parcelable.Creator<ArtistSummary> CREATOR = new Parcelable.Creator<ArtistSummary>() {
        public ArtistSummary createFromParcel(Parcel in) {
            return new ArtistSummary(in);
        }

        public ArtistSummary[] newArray(int size) {
            return new ArtistSummary[size];
        }
    };
}
