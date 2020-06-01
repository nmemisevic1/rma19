package ba.unsa.etf.rma.aktivnosti;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.net.ParseException;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ba.unsa.etf.rma.DodatneFunkcije;
import ba.unsa.etf.rma.DodatneImplementacije.KategorijeSpinner;
import ba.unsa.etf.rma.PitanjeAdapter;
import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.fragmenti.ListaFrag;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.klase.PitanjeOnline;
import ba.unsa.etf.rma.web.DodajKvizOnline;
import ba.unsa.etf.rma.web.UcitajKategorijeOnline;
import ba.unsa.etf.rma.web.UcitajKvizOnline;
import ba.unsa.etf.rma.web.UcitajPitanjaOnline;

public class DodajKvizAkt extends AppCompatActivity implements UcitajKvizOnline.onUcitajKvizOnlineDone, DodajKvizOnline.onDodajKvizOnlineDone, UcitajKategorijeOnline.onUcitajKategorijeOnlineDone {
    int spinnerTeller;
    private EditText string;
    ArrayList<Kategorija> kategorije = new ArrayList<>();
    ArrayList<Pitanje> nedodijeljenaPitanja = new ArrayList<Pitanje>();
    ArrayList<Kviz> kvizovi = new ArrayList<>();
    int pozicijaOdabranogKviza = 0;
    Kviz odabraniKviz = new Kviz();

    ba.unsa.etf.rma.DodatneImplementacije.KategorijeSpinner spKategorije;
    ListView lvDodanaPitanja;
    ListView lvMogucaPitanja;
    EditText etNaziv;
    Button btnDodajKviz;
    Button btnImportKviz;
    ArrayAdapter<String> kategorijeAdapter;
    public ArrayList<Pitanje> dodanaPitanja=new ArrayList<>();
    public PitanjeAdapter dodanaPitanjaAdapter;
    public ArrayList<String> naziviKategorija = new ArrayList<>();
    public PitanjeAdapter mogucaPitanjaAdapter;

    public static int brojPokretanja=0;

    private String nazivKviza;
    public  String nameKviza;
    private Kategorija kategorijaKviza;
    private ArrayList<PitanjeOnline> pitanjaKvizaSaIdem;
    private ArrayList<PitanjeOnline> mogucaPitanjaKSaIdem;









    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_kviz_akt);
        spinnerTeller = 0;
        naziviKategorija = new ArrayList<>();
        pitanjaKvizaSaIdem = new ArrayList<>();
        mogucaPitanjaKSaIdem = new ArrayList<>();
        kategorije = new ArrayList<>();
        nedodijeljenaPitanja = new ArrayList<Pitanje>();
        kvizovi = new ArrayList<>();
        pozicijaOdabranogKviza = 0;
        odabraniKviz = new Kviz();

        spKategorije = findViewById(R.id.spKategorije);
        lvDodanaPitanja = (ListView)findViewById(R.id.lvDodanaPitanja);
        lvMogucaPitanja = (ListView)findViewById(R.id.lvMogucaPitanja);
        etNaziv = (EditText)findViewById(R.id.etNaziv);
        btnDodajKviz = (Button)findViewById(R.id.btnDodajKviz);
        btnImportKviz = (Button)findViewById(R.id.btnImportKviz);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            kategorije = extras.getParcelableArrayList("kategorije");
            //nedodijeljenaPitanja = extras.getParcelableArrayList("nedodijeljenaPitanja");
            kvizovi = extras.getParcelableArrayList("kvizovi");
            odabraniKviz = extras.getParcelable("odabraniKviz");
            pozicijaOdabranogKviza = extras.getInt("pozicijaOdabranogKviza");
        }

        new UcitajKategorijeOnline(DodajKvizAkt.this,"Kvizovi",(UcitajKategorijeOnline.onUcitajKategorijeOnlineDone)DodajKvizAkt.this);
        new UcitajKvizOnline(DodajKvizAkt.this,"Kvizovi",odabraniKviz.getNaziv(),(UcitajKvizOnline.onUcitajKvizOnlineDone)DodajKvizAkt.this).execute("");






        if(kategorije.size()==0){
            kategorije.add(new Kategorija("Dodaj kategoriju","0"));
        }
        else if(kategorije.size()!=0 && !kategorije.get(kategorije.size() - 1).getNaziv().equals("Dodaj kategoriju")){
                kategorije.add(new Kategorija("Dodaj kategoriju","0"));
        }
        else;

        for(int i=0;i<kategorije.size();i++){
            naziviKategorija.add(kategorije.get(i).getNaziv());
        }
        kategorijeAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,naziviKategorija);
        kategorijeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if(odabraniKviz.getNaziv().equals("Dodaj kviz")){
            etNaziv.setText("");
            btnDodajKviz.setText("Dodaj kviz");
        }
        else{
            etNaziv.setText(odabraniKviz.getNaziv());
        }

        //dodanaPitanja = odabraniKviz.getPitanja();
        if(dodanaPitanja.size()==0){
            dodanaPitanja.add(new Pitanje("Dodaj pitanje","",null,""));
        }
        else if(dodanaPitanja.size()!=0 && !dodanaPitanja.get(dodanaPitanja.size() - 1).getNaziv().equals("Dodaj pitanje")){
            dodanaPitanja.add(new Pitanje("Dodaj pitanje","",null,""));
        }

        dodanaPitanjaAdapter = new PitanjeAdapter(this,dodanaPitanja,0);
        lvDodanaPitanja.setAdapter(dodanaPitanjaAdapter);



        mogucaPitanjaAdapter = new PitanjeAdapter(this,nedodijeljenaPitanja,1);
        lvMogucaPitanja.setAdapter(mogucaPitanjaAdapter);




        final ArrayList<Kategorija> finalKategorije = kategorije;
        final ArrayList<Kviz> finalKvizovi1 = kvizovi;
        final Kviz finalOdabraniKviz1 = odabraniKviz;
        final ArrayList<Kviz> finalKvizovi = kvizovi;

        final int finalPozicijaOdabranogKviza1 = pozicijaOdabranogKviza;
        lvDodanaPitanja.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position != dodanaPitanja.size()-1){
                    nedodijeljenaPitanja.add(dodanaPitanja.get(position));
                    dodanaPitanja.remove(position);
                    mogucaPitanjaAdapter.notifyDataSetChanged();
                    dodanaPitanjaAdapter.notifyDataSetChanged();
                }
                else if(position == dodanaPitanja.size()-1){
                    odabraniKviz.setPitanja(new ArrayList<Pitanje>(dodanaPitanja.subList(0,dodanaPitanja.size()-1)));
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("odabraniKviz",odabraniKviz);
                    bundle.putParcelableArrayList("kategorije", kategorije);
                    bundle.putParcelableArrayList("nedodijeljenaPitanja",nedodijeljenaPitanja);
                    bundle.putParcelableArrayList("kvizovi", kvizovi);
                    bundle.putInt("pozicijaOdabranogKviza", finalPozicijaOdabranogKviza1);
                    Intent intent = new Intent(DodajKvizAkt.this,DodajPitanjeAkt.class);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
            }
        });

        lvMogucaPitanja.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dodanaPitanja.add(0,nedodijeljenaPitanja.get(position));
                nedodijeljenaPitanja.remove(position);
                mogucaPitanjaAdapter.notifyDataSetChanged();
                dodanaPitanjaAdapter.notifyDataSetChanged();
                lvDodanaPitanja.invalidate();
                lvMogucaPitanja.invalidate();
            }
        });
        spKategorije.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spinnerTeller>0 && spKategorije.getSelectedItem().toString().equals("Dodaj kategoriju")){
                    Bundle bundle = new Bundle();
                    finalOdabraniKviz1.setPitanja(dodanaPitanja);
                    finalOdabraniKviz1.setNaziv(etNaziv.getText().toString());

                    bundle.putParcelable("odabraniKviz",odabraniKviz);
                    bundle.putParcelableArrayList("kategorije", new ArrayList<Kategorija>(kategorije.subList(0,kategorije.size()-1)));
                    bundle.putParcelableArrayList("nedodijeljenaPitanja",nedodijeljenaPitanja);
                    bundle.putParcelableArrayList("kvizovi", kvizovi);
                    bundle.putInt("pozicijaOdabranogKviza", finalPozicijaOdabranogKviza1);

                    Intent intent = new Intent(DodajKvizAkt.this,DodajKategorijuAkt.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                spinnerTeller++;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spKategorije.setAdapter(kategorijeAdapter);
        spKategorije.setSelection(kategorijeAdapter.getPosition(odabraniKviz.getKategorija().getNaziv()));

        final int finalPozicijaOdabranogKviza = pozicijaOdabranogKviza;
        btnDodajKviz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean ispravniPodaci = true;
                if(etNaziv.getText().toString().equals("")){
                    DodatneFunkcije.higlightTextColor(DodajKvizAkt.this,etNaziv);
                    Toast.makeText(DodajKvizAkt.this,"Unesite naziv kviza",Toast.LENGTH_SHORT).show();
                    ispravniPodaci = false;
                }
                if(spKategorije.getSelectedItem().toString().equals("Dodaj kategoriju")){
                    Toast.makeText(DodajKvizAkt.this,"Dodajte kategoriju i pridruzite je kvizu!",Toast.LENGTH_LONG).show();
                    ispravniPodaci = false;
                }
                if(ispravniPodaci){
                    odabraniKviz.setNaziv(etNaziv.getText().toString());
                    odabraniKviz.setKategorija(kategorije.get(spKategorije.getSelectedItemPosition()));
                    odabraniKviz.setPitanja(new ArrayList<Pitanje>(dodanaPitanja.subList(0,dodanaPitanja.size()-1)));
                    new DodajKvizOnline(DodajKvizAkt.this,odabraniKviz,"Kvizovi",nedodijeljenaPitanja,dodanaPitanja,mogucaPitanjaKSaIdem,pitanjaKvizaSaIdem,nameKviza,btnDodajKviz.getText().toString(),(DodajKvizOnline.onDodajKvizOnlineDone)DodajKvizAkt.this).execute("");
                    odabraniKviz.setPitanja(new ArrayList<Pitanje>(dodanaPitanja.subList(0,dodanaPitanja.size()-1)));
                    odabraniKviz.setKategorija(kategorije.get(spKategorije.getSelectedItemPosition()));
                    odabraniKviz.setNaziv(etNaziv.getText().toString());

                    if(finalPozicijaOdabranogKviza == kvizovi.size()-1){
                        kvizovi.add(0,odabraniKviz);
                    }
                    else{
                        kvizovi.set(finalPozicijaOdabranogKviza,odabraniKviz);
                    }


                }

            }
        });
        DodatneFunkcije.refreshTextColor(DodajKvizAkt.this,etNaziv);
        btnImportKviz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/*");
                startActivityForResult(Intent.createChooser(intent,"Importuj kviz!"),7);
            }
        });
        brojPokretanja++;
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        switch (requestCode){
            case 7:
                if(resultCode == RESULT_OK){
                    //String pathHolder = data.getData().getPath();
                    //Toast.makeText(DodajKvizAkt.this,pathHolder,Toast.LENGTH_LONG).show();
                    Uri uri = data.getData();
                    popuniPodatkeImportovanimKvizom(uri);
                }
        }
    }
    private void izbaciPoruku(Context context, String poruka){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(poruka);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert1 = builder1.create();
        alert1.show();
    }
    public void popuniPodatkeImportovanimKvizom(Uri importovaniKviz){
        BufferedReader br = null;
        try {
            //String nn = importovaniKviz.getName();
            //String nf = importovaniKviz.getPath();
            br = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(importovaniKviz)));
            StringBuffer stringBuffer = new StringBuffer();
            String tempStr = "";
            ArrayList<String> podaci = new ArrayList<>();
            while((tempStr=br.readLine())!=null){
                stringBuffer.append(tempStr);
                podaci.add(tempStr);
            }
            // podaci[0] -> informacije o kvizu
            // podaci[i] i=1..n -> informacije o pitanjima

            if(podaci.size()!=0){
                String naziv;
                ArrayList<Pitanje> pitanja = new ArrayList<>();
                Kategorija kategorija = new Kategorija();

                ArrayList<String> podaciOKvizu = new ArrayList<>(Arrays.asList(podaci.get(0).split(",")));
                for(int i=0;i<kvizovi.size();i++){
                    if(podaciOKvizu.get(0).equals(kvizovi.get(i).getNaziv())){
                        izbaciPoruku(DodajKvizAkt.this,"Kviz kojeg importujete već postoji!");
                        return;
                    }
                }
                naziv = podaciOKvizu.get(0);
                kategorija = new Kategorija(podaciOKvizu.get(1),"111");
                if(Integer.parseInt(podaciOKvizu.get(2))==podaci.size()-1){
                    for(int i=1;i<podaci.size();i++){
                        ArrayList<String> podaciOPitanju = new ArrayList<>();
                        podaciOPitanju.addAll(Arrays.asList(podaci.get(i).split(",")));
                        String nazivPitanja = podaciOPitanju.get(0);
                        int brojOdgovora = Integer.parseInt(podaciOPitanju.get(1));
                        if(brojOdgovora!=podaciOPitanju.size()-3){
                            izbaciPoruku(DodajKvizAkt.this,"Kviz kojeg importujete ima neispravan broj odgovora!");
                            return;
                        }
                        int indeksTacnogOdgovora = Integer.parseInt(podaciOPitanju.get(2));
                        if(indeksTacnogOdgovora<3 || indeksTacnogOdgovora>podaciOPitanju.size()-1){
                            izbaciPoruku(DodajKvizAkt.this,"Kviz kojeg importujete ima neispravan index tačnog odgovora!");
                            return;
                        }
                        ArrayList<String> odgovori = new ArrayList<>();
                        String tacanOdgovor = new String();
                        for(int j=3;j<podaciOPitanju.size();j++){
                            odgovori.add(podaciOPitanju.get(j));
                            if(j==indeksTacnogOdgovora)tacanOdgovor = podaciOPitanju.get(j);
                        }
                        pitanja.add(new Pitanje(nazivPitanja,nazivPitanja,odgovori,tacanOdgovor));
                    }
                    boolean jelPostojiKategorija=false;
                    for(int i=0;i<spKategorije.getCount();i++){
                        if(spKategorije.getItemAtPosition(i).toString().equals(kategorija.getNaziv())){
                            spKategorije.setSelection(i);
                            jelPostojiKategorija = true;
                            break;
                        }
                    }
                    if(jelPostojiKategorija==false){
                        kategorije.add(kategorije.size()-1,kategorija);
                        naziviKategorija.add(naziviKategorija.size()-1,kategorija.getNaziv());
                        KvizoviAkt.setKategorije(new ArrayList<Kategorija>(kategorije.subList(0,kategorije.size()-1)));
                        ListaFrag.setKategorije();
                        kategorijeAdapter.notifyDataSetChanged();
                        spKategorije.setSelection(spKategorije.getCount()-2);
                    }
                    //ciscenje dodanih pitanja prethodno
                    for(int i=0;i<dodanaPitanja.size()-1;i++){
                        dodanaPitanja.remove(i);
                        i--;
                    }
                    dodanaPitanja.addAll(0,pitanja);
                    dodanaPitanjaAdapter.notifyDataSetChanged();
                    etNaziv.setText(naziv);
                    Toast.makeText(DodajKvizAkt.this,"Kviz uspješno importovan, spasite ga!",Toast.LENGTH_SHORT).show();
                }
                else{
                    izbaciPoruku(DodajKvizAkt.this,"Kviz kojeg importujete ima neispravan broj pitanja!");
                }
            }
            else{
                izbaciPoruku(DodajKvizAkt.this,"Podaci iz datoteke nisu validni!");
            }

        } catch (FileNotFoundException e) {
            izbaciPoruku(DodajKvizAkt.this,"Neispravan fajl koji želite importovati!");
            e.printStackTrace();
        } catch (IOException f) {
            izbaciPoruku(DodajKvizAkt.this,"Neispravan fajl koji želite importovati!");
            f.printStackTrace();
        } catch(ParseException g){
            izbaciPoruku(DodajKvizAkt.this,"Neispravan fajl koji želite importovati!");
            g.printStackTrace();
        } catch(ArrayIndexOutOfBoundsException h){
            izbaciPoruku(DodajKvizAkt.this,"Neispravan fajl koji želite importovati!");
            h.printStackTrace();
        } catch(Exception k){
            izbaciPoruku(DodajKvizAkt.this,"Neispravan fajl koji želite importovati!");
            k.printStackTrace();
        }
        finally{
            if(br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /*
    @Override
    public void onDone(ArrayList<Pitanje> pitanjaOnline) {
        nedodijeljenaPitanja.addAll(pitanjaOnline);
        lvMogucaPitanja.invalidate();
        mogucaPitanjaAdapter.notifyDataSetChanged();
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        this.brojPokretanja++;
    }

    @Override
    public void onUcitajKvizDone(String nazivKviza, String nameKviza, Kategorija kategorijaKviza, ArrayList<PitanjeOnline> pitanjaKvizaSaIdem, ArrayList<PitanjeOnline> mogucaPitanjaKSaIdem) {
        this.nazivKviza = nazivKviza;
        this.nameKviza = nameKviza;
        this.kategorijaKviza = kategorijaKviza;
        this.pitanjaKvizaSaIdem = pitanjaKvizaSaIdem;
        this.mogucaPitanjaKSaIdem = mogucaPitanjaKSaIdem;

        if(nazivKviza.equals("Dodaj kviz"))etNaziv.setText("");
        else etNaziv.setText(nazivKviza);
        if(kategorijaKviza!=null) spKategorije.setSelection(kategorijeAdapter.getPosition(kategorijaKviza.getNaziv()));
        else spKategorije.setSelection(0);

        //dodanaPitanja = new ArrayList<>();
        if(pitanjaKvizaSaIdem!=null){
            for(int i=0;i<pitanjaKvizaSaIdem.size();i++){
                dodanaPitanja.add(0,pitanjaKvizaSaIdem.get(i).getPitanje());
            }
        }
        nedodijeljenaPitanja.clear();
        if(mogucaPitanjaKSaIdem!=null){
            for(int i=0;i<mogucaPitanjaKSaIdem.size();i++){
                nedodijeljenaPitanja.add(mogucaPitanjaKSaIdem.get(i).getPitanje());
            }
        }
        mogucaPitanjaAdapter.notifyDataSetChanged();
        dodanaPitanjaAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDodajKvizDone(Kviz k, String errorMessage) {
        if(!errorMessage.equals("")){
            Toast.makeText(DodajKvizAkt.this,errorMessage,Toast.LENGTH_SHORT).show();
        }
        else{
            /*Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("kvizovi",kvizovi);
            bundle.putParcelableArrayList("kategorije",new ArrayList<Kategorija>(kategorije.subList(0,kategorije.size()-1)));
            bundle.putParcelableArrayList("nedodijeljenaPitanja",nedodijeljenaPitanja);*/

            Intent intent = new Intent(DodajKvizAkt.this,KvizoviAkt.class);
            //intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    public void onKategorijeDone(ArrayList<Kategorija> k) {
        kategorije.addAll(0,k);
        naziviKategorija.clear();
        for(int i=0;i<kategorije.size();i++){
            naziviKategorija.add(kategorije.get(i).getNaziv());
        }
        kategorijeAdapter.notifyDataSetChanged();
        spKategorije.invalidate();
    }
}
