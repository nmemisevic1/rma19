package ba.unsa.etf.rma.klase;

public class PitanjeOnline {
    private Pitanje pitanje;
    private String idKviza;
    private String namePitanje;
    public PitanjeOnline(Pitanje pitanje,String idKviza,String namePitanje){
        this.pitanje = pitanje;
        this.idKviza = idKviza;
        this.namePitanje = namePitanje;
    }

    public String getIdKviza() {
        return idKviza;
    }

    public void setIdKviza(String idKviza) {
        this.idKviza = idKviza;
    }

    public Pitanje getPitanje() {
        return pitanje;
    }

    public void setPitanje(Pitanje pitanje) {
        this.pitanje = pitanje;
    }

    public String getNamePitanje() {
        return namePitanje;
    }

    public void setNamePitanje(String namePitanje) {
        this.namePitanje = namePitanje;
    }
}
