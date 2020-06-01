package ba.unsa.etf.rma.aktivnosti;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ba.unsa.etf.rma.DodatneFunkcije;
import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.DodajOdgovorAdapter;
import ba.unsa.etf.rma.adapteri.OdgovorAdapter;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.web.DodajPitanjeOnline;

public class DodajPitanjeAkt extends AppCompatActivity implements DodajPitanjeOnline.onDodajPitanjeOnlineDone{
    public String tacanOdgovor="";
    public ArrayList<String> odgovori = new ArrayList<>();
    public ArrayList<Kategorija> kategorije = new ArrayList<>();
    public ArrayList<Pitanje> nedodijeljenaPitanja = new ArrayList<Pitanje>();
    public ArrayList<Kviz> kvizovi = new ArrayList<>();
    public Kviz odabraniKviz = new Kviz();
    public int pozicijaOdabranogKviza = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_pitanje_akt);


        kategorije = new ArrayList<>();
        nedodijeljenaPitanja = new ArrayList<Pitanje>();
        kvizovi = new ArrayList<>();
        odabraniKviz = new Kviz();
        pozicijaOdabranogKviza = 0;

        Bundle bratex = getIntent().getExtras();
        if(bratex != null){
            kategorije = bratex.getParcelableArrayList("kategorije");
            nedodijeljenaPitanja = bratex.getParcelableArrayList("nedodijeljenaPitanja");
            kvizovi = bratex.getParcelableArrayList("kvizovi");
            odabraniKviz = bratex.getParcelable("odabraniKviz");
            pozicijaOdabranogKviza = bratex.getInt("pozicijaOdabranogKviza");
        }
        final ArrayList<Kategorija> kategorije1 = kategorije;
        final ArrayList<Pitanje> nedodijeljenaPitanja1 = nedodijeljenaPitanja;
        final ArrayList<Kviz> kvizovi1 = kvizovi;
        final Kviz odabraniKviz1 = odabraniKviz;

        Button dodajPitanj = (Button)findViewById(R.id.btnDodajPitanje);
        Button dodajOdgovor = (Button)findViewById(R.id.btnDodajOdgovor);
        final Button dodajTacan = (Button)findViewById(R.id.btnDodajTacan);

        final EditText etNaziv = (EditText)findViewById(R.id.etNaziv);
        final EditText etOdgovor = (EditText)findViewById(R.id.etOdgovor);

        final ListView lvOdgovori = (ListView)findViewById(R.id.lvOdgovori);

        final DodajOdgovorAdapter adapter = new DodajOdgovorAdapter(this,odgovori);


        lvOdgovori.setAdapter(adapter);
        dodajOdgovor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ispravniPodaci = true;
                if(etOdgovor.getText().toString().equals("")){
                    Toast.makeText(DodajPitanjeAkt.this,"Morate unijeti odgovor da bi ga dodali", Toast.LENGTH_SHORT).show();
                    ispravniPodaci = false;
                }
                if(etOdgovor.getText().toString().equals(tacanOdgovor) && tacanOdgovor!=""){
                    Toast.makeText(DodajPitanjeAkt.this,"Već postoji isti odgovor koji je tačan. Probajte nešto drugo!", Toast.LENGTH_LONG).show();
                    ispravniPodaci = false;
                }
                if(ispravniPodaci){
                    odgovori.add(etOdgovor.getText().toString());
                    adapter.notifyDataSetChanged();
                    etOdgovor.setText("");
                    lvOdgovori.invalidate();
                    lvOdgovori.invalidateViews();
                    lvOdgovori.refreshDrawableState();
                }
            }
        });
        dodajTacan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ispravniPodaci = true;
                if(etOdgovor.getText().toString().equals("")){
                    Toast.makeText(DodajPitanjeAkt.this,"Morate unijeti odgovor da bi ga dodali", Toast.LENGTH_SHORT).show();
                    ispravniPodaci = false;
                }
                if(odgovori.size()!=0){
                    for(int i=0;i<odgovori.size();i++){
                        if(etOdgovor.getText().toString().equals(odgovori.get(i))){
                            Toast.makeText(DodajPitanjeAkt.this,"Već postoji isti odgovor koji nije tačan. Probajte nešto drugo!", Toast.LENGTH_LONG).show();
                            ispravniPodaci = false;
                        }
                    }
                }
                if(ispravniPodaci){
                    odgovori.add(etOdgovor.getText().toString());
                    tacanOdgovor = etOdgovor.getText().toString();
                    adapter.notifyDataSetChanged();
                    adapter.setTacanOdgovor(tacanOdgovor);
                    lvOdgovori.invalidate();
                    lvOdgovori.invalidateViews();
                    lvOdgovori.refreshDrawableState();
                    etOdgovor.setText("");
                    dodajTacan.setEnabled(false);
                }
            }
        });
        lvOdgovori.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(odgovori.get(position).equals(tacanOdgovor)){
                    dodajTacan.setEnabled(true);
                }
                odgovori.remove(position);
                adapter.notifyDataSetChanged();
            }
        });
        final int finalPozicijaOdabranogKviza = pozicijaOdabranogKviza;
        dodajPitanj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean ispravniPodaci = true;
                if(odgovori.size()==0 || (odgovori.size()==1 && !dodajTacan.isEnabled())){
                    Toast.makeText(DodajPitanjeAkt.this,"Dodajte odgovore!",Toast.LENGTH_SHORT).show();
                    DodatneFunkcije.higlightTextColor(DodajPitanjeAkt.this,etOdgovor);
                    ispravniPodaci = false;
                }
                if(etNaziv.getText().toString().equals("")){
                    ispravniPodaci = false;
                    DodatneFunkcije.higlightTextColor(DodajPitanjeAkt.this,etNaziv);
                    Toast.makeText(DodajPitanjeAkt.this,"Unesite naziv pitanja!",Toast.LENGTH_SHORT).show();
                }
                if(dodajTacan.isEnabled()){
                    ispravniPodaci = false;
                    Toast.makeText(DodajPitanjeAkt.this,"Unesite tačan odgovor!",Toast.LENGTH_SHORT).show();
                    DodatneFunkcije.higlightTextColor(DodajPitanjeAkt.this,etOdgovor);
                }
                if(ispravniPodaci){
                    Pitanje pitanjce = new Pitanje(etNaziv.getText().toString(),etNaziv.getText().toString(),odgovori,tacanOdgovor);
                    new DodajPitanjeOnline(DodajPitanjeAkt.this,"Pitanja",pitanjce,(DodajPitanjeOnline.onDodajPitanjeOnlineDone)DodajPitanjeAkt.this).execute("");

                }
            }
        });


        DodatneFunkcije.refreshTextColor(DodajPitanjeAkt.this,etNaziv);
        DodatneFunkcije.refreshTextColor(DodajPitanjeAkt.this,etOdgovor);
    }

    @Override
    public void onDodajPitanjeDone(Pitanje pitanjce,String errorMessage) {
        if(!errorMessage.equals(""))Toast.makeText(DodajPitanjeAkt.this,errorMessage,Toast.LENGTH_SHORT).show();
        else{
            odabraniKviz.dodajPitanje(pitanjce);

            Bundle bundlex = new Bundle();
            bundlex.putParcelableArrayList("kategorije",kategorije);
            bundlex.putParcelableArrayList("nedodijeljenaPitanja",nedodijeljenaPitanja);
            bundlex.putParcelableArrayList("kvizovi",kvizovi);
            bundlex.putParcelable("odabraniKviz",odabraniKviz);
            bundlex.putInt("pozicijaOdabranogKviza", pozicijaOdabranogKviza);
            Intent intent = new Intent(DodajPitanjeAkt.this,DodajKvizAkt.class);
            intent.putExtras(bundlex);
            startActivity(intent);
        }
    }
}
