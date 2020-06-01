package ba.unsa.etf.rma.klase;

public class KategorijaOnline {
    private Kategorija kategorija;
    private String onlIdKategorije;

    public KategorijaOnline(Kategorija kategorija,String onlIdKategorije){
        this.kategorija = kategorija;
        this.onlIdKategorije = onlIdKategorije;
    }

    public Kategorija getKategorija() {
        return kategorija;
    }

    public void setKategorija(Kategorija kategorija) {
        this.kategorija = kategorija;
    }

    public String getOnlIdKategorije() {
        return onlIdKategorije;
    }

    public void setOnlIdKategorije(String onlIdKategorije) {
        this.onlIdKategorije = onlIdKategorije;
    }
}
