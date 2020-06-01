package ba.unsa.etf.rma.klase;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;

public class Kviz implements Parcelable {
    private String naziv;
    private ArrayList<Pitanje> pitanja;
    private Kategorija kategorija;

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public void setKategorija(Kategorija kategorija) {
        this.kategorija = kategorija;
    }

    public void setPitanja(ArrayList<Pitanje> pitanja) {
        this.pitanja = pitanja;
    }


    public String getNaziv() {
        return naziv;
    }

    public ArrayList<Pitanje> getPitanja() {
        return pitanja;
    }

    public Kategorija getKategorija() {
        return kategorija;
    }
    public void dodajPitanje(Pitanje p){
        this.pitanja.add(p);
    }

    public ArrayList<Pitanje> dajRandomPitanja(){
        ArrayList<Pitanje> novaPitanja = new ArrayList<>();
        if(pitanja!=null)novaPitanja = pitanja;
        Collections.shuffle(novaPitanja);
        return novaPitanja;
    }

    public Kviz(){
        this.naziv = "";
        this.pitanja = new ArrayList<>();
        this.kategorija = new Kategorija();
    }
    public Kviz(String naziv,Kategorija kategorija,ArrayList<Pitanje> pitanja){
        this.kategorija=kategorija;
        this.pitanja=pitanja;
        this.naziv = naziv;
    }
    public Kviz(String naziv,Kategorija kategorija){
        this.kategorija=kategorija;
        this.pitanja=new ArrayList<Pitanje>();
        this.naziv = naziv;
    }
    public Kviz(String naziv){
        this.naziv = naziv;
        this.kategorija=new Kategorija("","220");
        this.pitanja = new ArrayList<>();
    }

    public Kviz(Parcel in) {
        super();
        readFromParcel(in);
    }

    private void readFromParcel(Parcel in) {
        this.naziv = in.readString();
        this.kategorija = in.readParcelable(getClass().getClassLoader());
        this.pitanja = in.readArrayList(getClass().getClassLoader());
    }

    public static final Parcelable.Creator<Kviz> CREATOR = new Parcelable.Creator<Kviz>() {
        public Kviz createFromParcel(Parcel in) {
            return new Kviz(in);
        }

        public Kviz[] newArray(int size) {

            return new Kviz[size];
        }

    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.naziv);
        dest.writeParcelable(this.kategorija,flags);
        dest.writeList(this.pitanja);
    }
}
