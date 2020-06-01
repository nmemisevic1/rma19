package ba.unsa.etf.rma.aktivnosti;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.maltaisn.icondialog.Icon;
import com.maltaisn.icondialog.IconDialog;

import java.util.ArrayList;

import ba.unsa.etf.rma.DodatneFunkcije;
import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.web.DodajKategorijuOnline;

public class DodajKategorijuAkt extends AppCompatActivity implements IconDialog.Callback,DodajKategorijuOnline.OnKategorijaAddDone{

    private Icon[] selectedIcons;
    public static String nazivNoveKategorije;
    public static String idNoveKategorije;
    public static Bundle bundle;

    public ArrayList<Kategorija> kategorijes = new ArrayList<>();
    public ArrayList<Kviz> kvizovi = new ArrayList<>();
    public ArrayList<Pitanje> nedodijeljenaPitanja = new ArrayList<>();
    public Kviz odabraniKviz = new Kviz();
    public int pozicijaOdabranogKviza = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        nazivNoveKategorije = "";
        idNoveKategorije = "";
        kategorijes = new ArrayList<>();
        kvizovi = new ArrayList<>();
        nedodijeljenaPitanja = new ArrayList<>();
        odabraniKviz = new Kviz();
        pozicijaOdabranogKviza = 0;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kategoriju_akt);

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            kategorijes = extras.getParcelableArrayList("kategorije");
            kvizovi = extras.getParcelableArrayList("kvizovi");
            nedodijeljenaPitanja = extras.getParcelableArrayList("nedodijeljenaPitanja");
            odabraniKviz = extras.getParcelable("odabraniKviz");
            pozicijaOdabranogKviza = extras.getInt("pozicijaOdabranogKviza");
        }
        else{

        }

        final IconDialog iconDialog = new IconDialog();
        Button dodajIkonuBtn = (Button)findViewById(R.id.btnDodajIkonu);
        Button spasiKategoriju = (Button)findViewById(R.id.btnDodajKategoriju);

        final EditText etIkona = (EditText)findViewById(R.id.etIkona);
        final EditText etNaziv = (EditText)findViewById(R.id.etNaziv);

        dodajIkonuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iconDialog.setSelectedIcons(selectedIcons);
                iconDialog.show(getSupportFragmentManager(),"icon_dialog");
            }
        });
        spasiKategoriju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNaziv.clearFocus();
                Boolean ispravniPodaci = true;
                if(etIkona.getText().toString().equals("Ikona")){
                    ispravniPodaci = false;
                    DodatneFunkcije.higlightTextColor(DodajKategorijuAkt.this,etIkona);
                }
                if(kategorijes !=null){
                    for(int i = 0; i < kategorijes.size(); i++){
                        if(etNaziv.getText().toString().equals(kategorijes.get(i).getNaziv())){
                            ispravniPodaci = false;
                            Toast.makeText(DodajKategorijuAkt.this,"Kategorija koju želite dodati već postoji!", Toast.LENGTH_SHORT).show();
                            DodatneFunkcije.higlightTextColor(DodajKategorijuAkt.this,etNaziv);
                        }
                    }
                }
                if(etNaziv.getText().toString().equals("")){
                    DodatneFunkcije.higlightTextColor(DodajKategorijuAkt.this,etNaziv);
                    ispravniPodaci = false;
                }

                if(ispravniPodaci){
                    try{
                        nazivNoveKategorije = etNaziv.getText().toString();
                        idNoveKategorije = etIkona.getText().toString();
                        new DodajKategorijuOnline(DodajKategorijuAkt.this,"Kategorije",nazivNoveKategorije,idNoveKategorije,(DodajKategorijuOnline.OnKategorijaAddDone)DodajKategorijuAkt.this).execute("");




                    }
                    catch(Exception e){
                        e.printStackTrace();
                        Toast.makeText(DodajKategorijuAkt.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        DodatneFunkcije.refreshTextColor(DodajKategorijuAkt.this,etNaziv);
        DodatneFunkcije.refreshTextColor(DodajKategorijuAkt.this,etIkona);
    }

    @Override
    public void onIconDialogIconsSelected(Icon[] icons) {
        selectedIcons = icons;
        EditText etIkona = (EditText)findViewById(R.id.etIkona);
        etIkona.setText(String.valueOf(icons[0].getId()));
    }

    @Override
    public void onDone(Kategorija kategorija) {
        if(kategorija!=null){
            kategorijes.add(kategorija);
            bundle = new Bundle();
            odabraniKviz.setKategorija(kategorija);
            bundle.putParcelableArrayList("kategorije", kategorijes);
            bundle.putParcelableArrayList("kvizovi", kvizovi);
            bundle.putParcelableArrayList("nedodijeljenaPitanja", nedodijeljenaPitanja);
            bundle.putParcelable("odabraniKviz", odabraniKviz);
            bundle.putInt("pozicijaOdabranogKviza", pozicijaOdabranogKviza);
            Intent intent = new Intent(ba.unsa.etf.rma.aktivnosti.DodajKategorijuAkt.this,ba.unsa.etf.rma.aktivnosti.DodajKvizAkt.class);
            intent.putExtras(bundle);
            startActivity(intent);
            Toast.makeText(DodajKategorijuAkt.this,"Kategorija uspješno dodana!",Toast.LENGTH_SHORT).show();
        }
        else{
            DodatneFunkcije.upozorenje(DodajKategorijuAkt.this,"Upozorenje","Kategorija koju želite dodati već postoji!");
        }

    }
}
