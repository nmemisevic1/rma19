package ba.unsa.etf.rma.aktivnosti;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ba.unsa.etf.rma.DodatneFunkcije;
import ba.unsa.etf.rma.DodatneImplementacije.KategorijeSpinner;
import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.KvizBaseAdapter;
import ba.unsa.etf.rma.baze.SpiralaBazaOpenHelper;
import ba.unsa.etf.rma.fragmenti.DetailFrag;
import ba.unsa.etf.rma.fragmenti.ListaFrag;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.web.DodajRankingOnline;
import ba.unsa.etf.rma.web.PovuciRankingOnline;
import ba.unsa.etf.rma.web.UcitajKategorijeOnline;
import ba.unsa.etf.rma.web.UcitajKvizoveOnline;
import ba.unsa.etf.rma.web.UcitajPitanjaOnline;

public class KvizoviAkt extends AppCompatActivity implements UcitajKategorijeOnline.onUcitajKategorijeOnlineDone,
        UcitajKvizoveOnline.OnUcitajKvizoveOnlineDone, UcitajPitanjaOnline.OnUcitajPitanjaOnlineDone, PovuciRankingOnline.onPovuciRankingOnline , DodajRankingOnline.onDodajRankingOnline{
    public static int brojPokretanja = 0;
    public static int brojKlikovaNaSpinner = 0;
    public ArrayList<Kviz> kvizovi = new ArrayList<Kviz>() {{
        add(new Kviz("Dodaj kviz"));
    }};
    public static ArrayList<Kategorija> kategorije = new ArrayList<Kategorija>()/*{{add(0,new Kategorija("Svi","22"));}}*/;
    public ArrayList<Pitanje> neDodijeljenaPitanja = new ArrayList<>();
    public ArrayAdapter<String> kategorijeAdapter;
    public static ArrayList<String> kategorijice = new ArrayList<>();
    public KvizBaseAdapter adapter;
    public ListView listaKvizova;
    public static boolean jelImaInternetaBozanstvenog;
    public static void setKategorije(ArrayList<Kategorija> kategorije1) {
        kategorije = kategorije1;
        kategorijice.clear();
        for (int i = 0; i < kategorije.size(); i++) {
            kategorijice.add(kategorije.get(i).getNaziv());
        }
    }

    public KategorijeSpinner spPostojeceKategorije;
    public SpiralaBazaOpenHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        helper = new SpiralaBazaOpenHelper(KvizoviAkt.this);
        //SQLiteDatabase db = openOrCreateDatabase("AplikacijaDB",MODE_PRIVATE,null);
        helper = new SpiralaBazaOpenHelper(this);
        //SQLiteDatabase helper = new SpiralaBazaOpenHelper(this).db;
        //helper.onCreate();
        this.brojKlikovaNaSpinner = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kvizovi_akt);

        if(DodatneFunkcije.isNetworkAvailable(getBaseContext()))jelImaInternetaBozanstvenog = true;
        else jelImaInternetaBozanstvenog = false;



        //applyHardCodedData();

        kategorijice = new ArrayList<>();
        if(jelImaInternetaBozanstvenog){
            Toast.makeText(KvizoviAkt.this,"Molimo sačekajte dok se podaci učitaju sa interneta",Toast.LENGTH_LONG).show();
            new DodajRankingOnline(KvizoviAkt.this,(DodajRankingOnline.onDodajRankingOnline)KvizoviAkt.this).execute("");

            //helper.onCreate(db);
            //helper.onUpgrade(db,0,0);
            //new UcitajKvizoveOnline(KvizoviAkt.this,"Svi","Kvizovi",(UcitajKvizoveOnline.OnUcitajKvizoveOnlineDone)KvizoviAkt.this);
            new UcitajKategorijeOnline(KvizoviAkt.this, "Kategorije", (UcitajKategorijeOnline.onUcitajKategorijeOnlineDone) KvizoviAkt.this).execute("");
            new UcitajPitanjaOnline(KvizoviAkt.this,"Pitanja",(UcitajPitanjaOnline.OnUcitajPitanjaOnlineDone)KvizoviAkt.this).execute("");

        }
        else{
            Bundle sve = helper.dajSve();
            kategorije = sve.getParcelableArrayList("kategorije");
            kvizovi = sve.getParcelableArrayList("kvizovi");
        }

        for (int i = 0; i < kategorije.size(); i++) {
            kategorijice.add(kategorije.get(i).getNaziv());
        }
        spPostojeceKategorije = (KategorijeSpinner) findViewById(R.id.spPostojeceKategorije);

        if (spPostojeceKategorije == null) {
            Configuration config = getResources().getConfiguration();

            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ListaFrag lf = new ListaFrag();
            Bundle lfBundle = new Bundle();
            lfBundle.putParcelableArrayList("kategorije", kategorije);
            lfBundle.putParcelableArrayList("nedodijeljenaPitanja", neDodijeljenaPitanja);
            lfBundle.putParcelableArrayList("kvizovi", kvizovi);
            DetailFrag df = new DetailFrag();
            Bundle dfBundle = new Bundle();
            dfBundle.putParcelableArrayList("kvizovi", kvizovi);
            dfBundle.putParcelableArrayList("kategorije", kategorije);
            dfBundle.putParcelableArrayList("nedodijeljenaPitanja", neDodijeljenaPitanja);

            lf.setArguments(lfBundle);
            df.setArguments(dfBundle);

            ft.replace(R.id.listPlace, lf);
            ft.replace(R.id.detailPlace, df);
            ft.commit();
        } else {


            kategorijeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, kategorijice);
            spPostojeceKategorije.setAdapter(kategorijeAdapter);

            listaKvizova = (ListView) findViewById(R.id.lvKvizovi);
            adapter = new KvizBaseAdapter(getApplicationContext(), kvizovi);

            listaKvizova.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            final ArrayList<Kategorija> finalKategorije = kategorije;
            listaKvizova.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (!adapter.getItem(position).getNaziv().equals("Dodaj kviz")) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("kategorije", kategorije);
                        bundle.putParcelableArrayList("nedodijeljenaPitanja", neDodijeljenaPitanja);
                        bundle.putParcelableArrayList("kvizovi", kvizovi);
                        int indeks = 0;
                        for (int i = 0; i < kvizovi.size(); i++) {
                            if (kvizovi.get(i).getNaziv().equals(adapter.getItem(position).getNaziv())) {
                                indeks = i;
                            }
                        }
                        bundle.putParcelable("odabraniKviz", kvizovi.get(indeks));
                        bundle.putInt("pozicijaOdabranogKviza", indeks);

                        int minuteDoDogadjaja = provjeraEvenataOld(kvizovi.get(indeks));
                        if(minuteDoDogadjaja==-1){
                            Intent intent = new Intent(KvizoviAkt.this, IgrajKvizAkt.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }else{
                            DodatneFunkcije.alertDialogBuilder(KvizoviAkt.this,"Imate dogadjaj koji pocinje za " + minuteDoDogadjaja + " minuta.");
                        }
                    }
                }
            });
            listaKvizova.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    if(!jelImaInternetaBozanstvenog!=true) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("kategorije", kategorije);
                        bundle.putParcelableArrayList("kvizovi", kvizovi);
                        bundle.putParcelableArrayList("nedodijeljenaPitanja", neDodijeljenaPitanja);
                        int indeks = 0;
                        for (int i = 0; i < kvizovi.size(); i++) {
                            if (kvizovi.get(i).getNaziv().equals(adapter.getItem(position).getNaziv())) {
                                indeks = i;
                            }
                        }
                        bundle.putParcelable("odabraniKviz", kvizovi.get(indeks));
                        bundle.putInt("pozicijaOdabranogKviza", indeks);
                        Intent intent = new Intent(KvizoviAkt.this, DodajKvizAkt.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        return true;
                    }
                    else{
                        Toast.makeText(KvizoviAkt.this,"Nema internet konekcije pa je onemoguceno dodavanje i izmjena kvizova.",Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
            spPostojeceKategorije.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (brojKlikovaNaSpinner > 0 && jelImaInternetaBozanstvenog){
                        new UcitajKvizoveOnline(KvizoviAkt.this, spPostojeceKategorije.getSelectedItem().toString(), "Kvizovi", (UcitajKvizoveOnline.OnUcitajKvizoveOnlineDone) KvizoviAkt.this).execute("");
                        brojKlikovaNaSpinner++;
                    }else adapter.getFilter().filter(spPostojeceKategorije.getSelectedItem().toString());

                    //listaKvizova.invalidateViews();
                    //listaKvizova.refreshDrawableState();
                    //new UcitajKvizoveOnline(KvizoviAkt.this,spPostojeceKategorije.getSelectedItem().toString(),"Kvizovi",(UcitajKvizoveOnline.OnUcitajKvizoveOnlineDone)KvizoviAkt.this).execute("");
                    listaKvizova.setAdapter(adapter);
                    adapter.updateAdapter(kvizovi);
                    adapter.notifyDataSetChanged();
                    listaKvizova.invalidateViews();
                    listaKvizova.invalidate();
                    listaKvizova.refreshDrawableState();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    //adapter.getFilter().filter(spPostojeceKategorije.getSelectedItem().toString());
                }
            });
            listaKvizova.setAdapter(adapter);
            adapter.updateAdapter(kvizovi);
            adapter.notifyDataSetChanged();
            listaKvizova.invalidateViews();

        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        this.brojKlikovaNaSpinner = 0;
        if (kategorijeAdapter != null) {
            kategorijeAdapter.notifyDataSetChanged();
        }
        brojPokretanja++;

    }

    @Override
    public void onKategorijeDone(ArrayList<Kategorija> k) {
        kategorije.clear();
        kategorije.addAll(k);
        kategorijice.clear();
        //kategorije = k;
        for (int i = 0; i < kategorije.size(); i++) {
            kategorijice.add(kategorije.get(i).getNaziv());
        }
        kategorijeAdapter.notifyDataSetChanged();
        spPostojeceKategorije.setSelection(kategorijice.indexOf("Svi"));
        this.brojPokretanja++;
        this.brojKlikovaNaSpinner++;
    }

    @Override
    public void onUcitajKvizoveDone(ArrayList<Kviz> kvizovi) {
        for (int i = 0; i < this.kvizovi.size() - 1; i++) {
            this.kvizovi.remove(i);
            i--;
        }
        this.kvizovi.addAll(0, kvizovi);
        adapter = new KvizBaseAdapter(KvizoviAkt.this,kvizovi);
        listaKvizova.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listaKvizova.invalidate();
        listaKvizova.invalidateViews();
        listaKvizova.refreshDrawableState();
        Toast.makeText(KvizoviAkt.this,"Kvizovi spremni za koristenje!",Toast.LENGTH_LONG).show();
        //Toast.makeText(KvizoviAkt.this, "Zavrseno dovlacenje kvizova", Toast.LENGTH_SHORT).show();
    }
    public int provjeraEvenataOld(Kviz k) {

        int trajanjeKviza = k.dajRandomPitanja().size();
        if (trajanjeKviza % 2 != 0) trajanjeKviza++;
        trajanjeKviza /= 2;

        //trajanjeKviza = 6;


        String[] projection = new String[]{CalendarContract.Events.CALENDAR_ID, CalendarContract.Events.TITLE, CalendarContract.Events.DESCRIPTION, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND, CalendarContract.Events.ALL_DAY, CalendarContract.Events.EVENT_LOCATION};

        long ONE_MINUTE_IN_MILLIS = 60000;//millisecs

        Calendar date = Calendar.getInstance();
        long t = date.getTimeInMillis();
        Date afterAddingMins = new Date(t + (trajanjeKviza * ONE_MINUTE_IN_MILLIS));


        Calendar novoVrijeme = Calendar.getInstance();
        novoVrijeme.setTime(afterAddingMins);
        //DodatneFunkcije.alertDialogBuilder(KvizoviAkt.this,novoVrijeme.getTime().toString());

// the range is all data from 2014
        //CalendarContract.EventsColumns.CALENDAR_ID;
        String selection = "(( " + CalendarContract.Events.DTSTART + " >= " + date.getTimeInMillis() + " )" +
                " AND (" + CalendarContract.Events.DTSTART + "<=" + novoVrijeme.getTimeInMillis() + "))";

        if (ContextCompat.checkSelfPermission(KvizoviAkt.this,
                Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {

// Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(KvizoviAkt.this,
                    Manifest.permission.READ_CALENDAR)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(KvizoviAkt.this,
                        new String[]{Manifest.permission.READ_CALENDAR},
                        3000);

                // MY_PERMISSIONS_REQUEST_READ_CALENDAR is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else {
            Cursor cursor = this.getBaseContext().getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, selection, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int minuteDoEventa;
                    Date datumcic = new Date(cursor.getLong(3));
                    Date proslo = new Date(date.getTimeInMillis());
                    minuteDoEventa = datumcic.getMinutes()-proslo.getMinutes();
                    if(minuteDoEventa<0)minuteDoEventa=60+minuteDoEventa;
                    //do {
                    //    Toast.makeText( this.getApplicationContext(), "Title: " + cursor.getString(1) + " Start-Time: " + (new Date(cursor.getLong(3))).toString(), Toast.LENGTH_LONG ).show();
                    //} while ( cursor.moveToNext());
                    return minuteDoEventa;
                }
            }
        }

// output the events

        /**/
        return -1;
    }

    @Override
    public void onDone(ArrayList<Pitanje> pitanjaOnline) {
        Toast.makeText(KvizoviAkt.this,"Kvizovi spremni za koristenje!",Toast.LENGTH_LONG).show();
        //Toast.makeText(this,"Pitanja povucena",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPovuciRankingDone() {
        //Toast.makeText(this,"Rankinzi povuceni",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDodajRankingDone() {
        //Toast.makeText(this,"Rankinzi poslani na ",Toast.LENGTH_SHORT).show();
        helper.onDelete();
        new PovuciRankingOnline(KvizoviAkt.this,(PovuciRankingOnline.onPovuciRankingOnline)KvizoviAkt.this).execute("");
    }
}
