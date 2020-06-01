package ba.unsa.etf.rma.klase;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ArrayAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class Pitanje implements Parcelable{

    private String naziv;
    private String tekstPitanja;
    private ArrayList<String> odgovori;
    private String tacan;

    public String getNaziv() {
        return naziv;
    }

    public String getTacan() {
        return tacan;
    }

    public ArrayList<String> getOdgovori() {
        return odgovori;
    }

    public String getTekstPitanja() {
        return tekstPitanja;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public void setOdgovori(ArrayList<String> odgovori) {
        this.odgovori = odgovori;
    }

    public void setTacan(String tacan) {
        this.tacan = tacan;
    }

    public void setTekstPitanja(String tekstPitanja) {
        this.tekstPitanja = tekstPitanja;
    }



    public ArrayList<String> dajRandomOdgovore(){
        ArrayList<String> noviOdgovori = odgovori;
        Collections.shuffle(noviOdgovori);
        return noviOdgovori;
    }
    public Pitanje(){
        this.naziv="";
        this.tekstPitanja="";
        this.odgovori=new ArrayList<>();
        this.tacan = "";
    }
    public Pitanje(String naziv, String tekstPitanja, ArrayList<String> odgovori,String tacan){
        this.naziv = naziv;
        this.tekstPitanja = tekstPitanja;
        this.odgovori = odgovori;
        this.tacan = tacan;
    }
    public Pitanje(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator<Pitanje> CREATOR = new Parcelable.Creator<Pitanje>() {
        public Pitanje createFromParcel(Parcel in) {
            return new Pitanje(in);
        }

        public Pitanje[] newArray(int size) {

            return new Pitanje[size];
        }

    };

    public void readFromParcel(Parcel in) {



        this.naziv = in.readString();
        this.tekstPitanja = in.readString();
        this.odgovori = in.createStringArrayList();
        this.tacan = in.readString();

    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.naziv);
        dest.writeString(this.tekstPitanja);
        dest.writeStringList(this.odgovori);
        dest.writeString(this.tacan);
    }
}
