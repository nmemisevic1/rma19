package ba.unsa.etf.rma.adapteri;

public class OdgovorItem {
    private String odgovor;
    private boolean highlighted;

    public OdgovorItem(String odgovor, boolean highlighted) {
        this.odgovor = odgovor;
        this.highlighted = highlighted;
    }

    public OdgovorItem(String odgovor) {
        this.odgovor = odgovor;
        this.highlighted = false;
    }

    public String getOdgovor() {
        return odgovor;
    }

    public void setOdgovor(String odgovor) {
        this.odgovor = odgovor;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }
}
