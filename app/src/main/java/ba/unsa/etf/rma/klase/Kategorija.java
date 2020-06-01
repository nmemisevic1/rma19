package ba.unsa.etf.rma.klase;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Kategorija implements Parcelable{
    public static final Parcelable.Creator<Kategorija> CREATOR = new Parcelable.Creator<Kategorija>() {
        @Override
        public Kategorija createFromParcel(Parcel in) {
            return new Kategorija(in);
        }

        @Override
        public Kategorija[] newArray(int size) {

            return new Kategorija[size];
        }
    };

    private String naziv;
    private String id;

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public String getId() {
        return id;
    }
    public Kategorija(String naziv, String id){
        this.naziv = naziv;
        this.id = id;
    }
    public Kategorija(){
        this.naziv ="";
        this.id = "";
    }

    private Kategorija(Parcel in) {
        super();
        readFromParcel(in);
    }
    public void readFromParcel(Parcel in) {
        this.naziv = in.readString();
        this.id = in.readString();

    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.naziv);
        dest.writeString(this.id);
    }
}
