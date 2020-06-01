package ba.unsa.etf.rma.klase;

public class RangIgraca {
    private String redniBroj;
    private String imeIgraca;
    private String procenatTacnihOdgovora;
    private String nazivKviza;
    public RangIgraca(String redniBroj, String imeIgraca, String procenatTacnihOdgovora){
        this.redniBroj = redniBroj;
        this.imeIgraca = imeIgraca;
        this.procenatTacnihOdgovora = procenatTacnihOdgovora;
        this.nazivKviza = "";
    }
    public RangIgraca(String redniBroj, String imeIgraca, String procenatTacnihOdgovora,String nazivKviza){
        this.redniBroj = redniBroj;
        this.imeIgraca = imeIgraca;
        this.procenatTacnihOdgovora = procenatTacnihOdgovora;
        this.nazivKviza = nazivKviza;
    }



    public String getRedniBroj() {
        return redniBroj;
    }

    public void setRedniBroj(String redniBroj) {
        this.redniBroj = redniBroj;
    }

    public String getImeIgraca() {
        return imeIgraca;
    }

    public void setImeIgraca(String imeIgraca) {
        this.imeIgraca = imeIgraca;
    }

    public String getProcenatTacnihOdgovora() {
        return procenatTacnihOdgovora;
    }

    public void setProcenatTacnihOdgovora(String procenatTacnihOdgovora) {
        this.procenatTacnihOdgovora = procenatTacnihOdgovora;
    }

    public String getNazivKviza() {
        return nazivKviza;
    }

    public void setNazivKviza(String nazivKviza) {
        this.nazivKviza = nazivKviza;
    }
}
