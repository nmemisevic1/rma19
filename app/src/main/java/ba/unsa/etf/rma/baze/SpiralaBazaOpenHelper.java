package ba.unsa.etf.rma.baze;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.KategorijaOnline;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.klase.PitanjeOnline;
import ba.unsa.etf.rma.klase.RangIgraca;

public class SpiralaBazaOpenHelper extends SQLiteOpenHelper {
    public SQLiteDatabase db = getWritableDatabase();

    // O BAZI PODATAKA
    public static final String DATABASE_NAME = "AplikacijaDB.db";
    public static  int DATABASE_VERSION = 1;

    // O KATEGORIJAMA
    public static final String DATABASE_TABLE_KATEGORIJE = "Kategorije";
    public static final String KATEGORIJA_ID = "_id";
    public static final String KATEGORIJA_IDK = "idk";
    public static final String KATEGORIJA_IME = "imeKategorije";

    //  O PITANJIMA
    public static final String DATABASE_TABLE_PITANJA = "Pitanja";
    public static final String PITANJE_ID = "_id";
    public static final String PITANJE_NAZIV = "naziv";
    public static final String PITANJE_ODGOVORI = "odgovori"; //neparsirani
    public static final String PITANJE_TACAN = "tacan";

    // O KVIZOVIMA
    public static final String DATABASE_TABLE_KVIZOVI = "Kvizovi";
    public static final String KVIZ_ID = "_id";
    public static final String KVIZ_KATEGORIJAID = "kategorija";
    public static final String KVIZ_PITANJAIDS = "pitanja";
    public static final String KVIZ_NAZIV = "naziv";

    // O OFFLINE RANGLISTAMA ( USAMLJENE RANG LISTE KOJE JEDVA ČEKAJU DA IH SE UPLOADUJE NA BOŽANSTVENI FIREBASE
    public static final String DATABASE_TABLE_OFFRANGLISTE = "OffRangListe";
    public static final String OFFRANGLISTA_ID = "_id";
    public static final String OFFRANGLISTA_REDNIBROJ = "redniBroj";
    public static final String OFFRANGLISTA_NAZIVIGRACA = "nazivIgraca";
    public static final String OFFRANGLISTA_NAZIVKVIZA = "nazivKviza";
    public static final String OFFRANGLISTA_PROCENATTACNIH = "procenatTacnih";

    // O ONLINE RANGLISTAMA (RANGLISTE KOJE SU SKINUTE SA BOZANSTVENOG FIREBASEA, KOJE SE PRILIKOM PRIKAZA UPOREDJUJU SA OFFLINE RANGLISTAMA I UPDATEUJU SE SA NAJNOVIJIM PODACIMA SA FIREBASEA
    public static final String DATABASE_TABLE_ONLRANGLISTE = "OnlRangListe";
    public static final String ONLRANGLISTA_ID = "_id";
    public static final String ONLRANGLISTA_REDNIBROJ = "redniBroj";
    public static final String ONLRANGLISTA_NAZIVIGRACA = "nazivIgraca";
    public static final String ONLRANGLISTA_NAZIVKVIZA = "nazivKviza";
    public static final String ONLRANGLISTA_PROCENATTACNIH = "procenatTacnih";

    private static final String CREATE_TABLE_OFFRANGLISTE = "CREATE TABLE " + DATABASE_TABLE_OFFRANGLISTE + " (" +
            OFFRANGLISTA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            OFFRANGLISTA_REDNIBROJ + " TEXT, " +
            OFFRANGLISTA_NAZIVIGRACA + " TEXT, " +
            OFFRANGLISTA_PROCENATTACNIH + " TEXT, " +
            OFFRANGLISTA_NAZIVKVIZA + " TEXT );";

    private static final String CREATE_TABLE_ONLRANGLISTE = "CREATE TABLE " + DATABASE_TABLE_ONLRANGLISTE + " (" +
            ONLRANGLISTA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ONLRANGLISTA_REDNIBROJ + " TEXT, " +
            ONLRANGLISTA_NAZIVIGRACA + " TEXT, " +
            ONLRANGLISTA_PROCENATTACNIH + " TEXT, " +
            ONLRANGLISTA_NAZIVKVIZA + " TEXT );";



    private static final String CREATE_TABLE_KATEGORIJE = "CREATE TABLE " + DATABASE_TABLE_KATEGORIJE + " (" +
            KATEGORIJA_ID + " TEXT PRIMARY KEY, " +
            KATEGORIJA_IDK + " TEXT, " +
            KATEGORIJA_IME + " TEXT );";
    private static final String CREATE_TABLE_PITANJA = "CREATE TABLE " + DATABASE_TABLE_PITANJA + " (" +
            PITANJE_ID + " TEXT PRIMARY KEY, " +
            PITANJE_NAZIV + " TEXT, " +
            PITANJE_ODGOVORI + " TEXT, " +
            PITANJE_TACAN + " TEXT );";
    private static final String CREATE_TABLE_KVIZOVI = "CREATE TABLE " + DATABASE_TABLE_KVIZOVI + " (" +
            KVIZ_ID + " TEXT PRIMARY KEY, " +
            KVIZ_KATEGORIJAID + " TEXT, " +
            KVIZ_PITANJAIDS + " TEXT, " +
            KVIZ_NAZIV + " TEXT );";







    public SpiralaBazaOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_KATEGORIJE);
        db.execSQL(CREATE_TABLE_KVIZOVI);
        db.execSQL(CREATE_TABLE_OFFRANGLISTE);
        db.execSQL(CREATE_TABLE_ONLRANGLISTE);
        db.execSQL(CREATE_TABLE_PITANJA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_KATEGORIJE);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_KVIZOVI);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_OFFRANGLISTE);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_ONLRANGLISTE);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_PITANJA);
        onCreate(sqLiteDatabase);
    }

    public void onDelete(){
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_KATEGORIJE);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_KVIZOVI);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_OFFRANGLISTE);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_ONLRANGLISTE);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_PITANJA);
        db.execSQL(CREATE_TABLE_KATEGORIJE);
        db.execSQL(CREATE_TABLE_KVIZOVI);
        db.execSQL(CREATE_TABLE_OFFRANGLISTE);
        db.execSQL(CREATE_TABLE_ONLRANGLISTE);
        db.execSQL(CREATE_TABLE_PITANJA);
    }
    public void onDeleteOffRangliste(){
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_OFFRANGLISTE);
        db.execSQL(CREATE_TABLE_OFFRANGLISTE);
    }
    @Override
    public void onOpen(SQLiteDatabase db){
        super.onOpen(db);
    }
    public long dodajKategoriju(Kategorija k,String OnlIdKategorije){
        long x;
        ContentValues values = new ContentValues();
        values.put(KATEGORIJA_ID,OnlIdKategorije);
        values.put(KATEGORIJA_IDK,k.getId());
        values.put(KATEGORIJA_IME,k.getNaziv());
        try{
            x = db.insertOrThrow(DATABASE_TABLE_KATEGORIJE,null,values);
        }catch(SQLiteConstraintException r){
            r.printStackTrace();
            return -1;
        }
        return x;
    }
    private String pack(ArrayList<String> s){
        String upakovano = "";
        for(int i=0;i<s.size();i++){
            if(i==s.size()-1) upakovano += s.get(i);
            else upakovano += s.get(i) + ",";
        }
        return upakovano;
    }
    private ArrayList<String> unpack(String s,String regeks){
        String [] strings = s.split(regeks);
        return new ArrayList<String>(Arrays.asList(strings));
    }
    public long dodajKviz(Kviz k,String onlIdKviza, String onlIdKategorije, ArrayList<String> ideviPitanja){
        long x;
        ContentValues values = new ContentValues();
        values.put(KVIZ_ID,onlIdKviza);
        values.put(KVIZ_KATEGORIJAID,onlIdKategorije);
        values.put(KVIZ_PITANJAIDS,pack(ideviPitanja));
        values.put(KVIZ_NAZIV,k.getNaziv());
        try{
            x = db.insertOrThrow(DATABASE_TABLE_KVIZOVI,null,values);
        }catch(SQLiteConstraintException r){
            r.printStackTrace();
            return -1;
        }
        return x;
    }
    public long dodajPitanje(Pitanje pitanje,String onlIdPitanja){
        long x;
        ContentValues values = new ContentValues();
        values.put(PITANJE_ID,onlIdPitanja);
        values.put(PITANJE_NAZIV,pitanje.getNaziv());
        values.put(PITANJE_ODGOVORI,pack(pitanje.getOdgovori()));
        values.put(PITANJE_TACAN,pitanje.getTacan());
        try{
            x = db.insertOrThrow(DATABASE_TABLE_PITANJA,null,values);
        }catch(SQLiteConstraintException e){
            return -1;
        }
        return x;
    }
    public long dodajOffRang(RangIgraca rangIgraca, String nazivKviza){
        long x;
        ContentValues values = new ContentValues();
        values.put(OFFRANGLISTA_REDNIBROJ,rangIgraca.getRedniBroj());
        values.put(OFFRANGLISTA_NAZIVIGRACA,rangIgraca.getImeIgraca());
        values.put(OFFRANGLISTA_NAZIVKVIZA,nazivKviza);
        values.put(OFFRANGLISTA_PROCENATTACNIH,rangIgraca.getProcenatTacnihOdgovora());
        try{
            x = db.insertOrThrow(DATABASE_TABLE_OFFRANGLISTE,null,values);
        }catch (SQLiteConstraintException e){
            e.printStackTrace();
            return -1;
        }
        return x;
    }
    public long dodajOnlRang(RangIgraca rangIgraca, String nazivKviza){
        long x;
        ContentValues values = new ContentValues();
        values.put(ONLRANGLISTA_REDNIBROJ,rangIgraca.getRedniBroj());
        values.put(ONLRANGLISTA_NAZIVIGRACA,rangIgraca.getImeIgraca());
        values.put(ONLRANGLISTA_NAZIVKVIZA,nazivKviza);
        values.put(ONLRANGLISTA_PROCENATTACNIH,rangIgraca.getProcenatTacnihOdgovora());
        try{
            x = db.insertOrThrow(DATABASE_TABLE_ONLRANGLISTE,null,values);
        }catch (SQLiteConstraintException e){
            e.printStackTrace();
            return -1;
        }
        return x;
    }
    public ArrayList<RangIgraca> dajOffRangliste(){
        long x;
        ArrayList<RangIgraca> rangIgracas = new ArrayList<>();
        try{
            Cursor cur = db.rawQuery("SELECT * FROM " + DATABASE_TABLE_OFFRANGLISTE,null);
            if(cur!=null){
                if(cur.getCount()!=0){
                    while(cur.moveToNext()){
                        rangIgracas.add(new RangIgraca(cur.getString(cur.getColumnIndex(OFFRANGLISTA_REDNIBROJ)),cur.getString(cur.getColumnIndex(OFFRANGLISTA_NAZIVIGRACA)),
                                cur.getString(cur.getColumnIndex(OFFRANGLISTA_PROCENATTACNIH)),cur.getString(cur.getColumnIndex(OFFRANGLISTA_NAZIVKVIZA))));
                    }
                }
            }
            return rangIgracas;
        }catch(SQLiteException e){
            e.printStackTrace();
        }
        return rangIgracas;
    }
    public ArrayList<RangIgraca> dajOnlRangListe(){
        long x;
        ArrayList<RangIgraca> rangIgracas = new ArrayList<>();
        try{
            Cursor cur = db.rawQuery("SELECT * FROM " + DATABASE_TABLE_ONLRANGLISTE,null);
            if(cur!=null){
                if(cur.getCount()!=0){
                    while(cur.moveToNext()){
                        rangIgracas.add(new RangIgraca(cur.getString(cur.getColumnIndex(ONLRANGLISTA_REDNIBROJ)),cur.getString(cur.getColumnIndex(ONLRANGLISTA_NAZIVIGRACA)),
                                cur.getString(cur.getColumnIndex(ONLRANGLISTA_PROCENATTACNIH)),cur.getString(cur.getColumnIndex(ONLRANGLISTA_NAZIVKVIZA))));
                    }
                }
            }
            return rangIgracas;
        }catch(SQLiteException e){
            e.printStackTrace();
        }
        return rangIgracas;
    }
    public ArrayList<KategorijaOnline> dajSveOnlineKategorije(){
        ArrayList<KategorijaOnline> kategorije = new ArrayList<>();
        try{
            Cursor cur = db.rawQuery("SELECT * FROM " + DATABASE_TABLE_KATEGORIJE,null);
            if(cur!=null){
                if(cur.getCount()!=0){
                    while(cur.moveToNext()){
                        kategorije.add(new KategorijaOnline(new Kategorija(cur.getString(2),cur.getString(1)),cur.getString(0)));
                    }
                }
                cur.close();
            }
            return kategorije;
        }catch(SQLiteException e){
            e.printStackTrace();
        }
        return kategorije;
    }
    public ArrayList<PitanjeOnline> dajSvaPitanja(){
        ArrayList<PitanjeOnline> pitanja = new ArrayList<>();
        try{
            Cursor cur = db.rawQuery("SELECT * FROM " + DATABASE_TABLE_PITANJA,null);
            if(cur!=null){
                if(cur.getCount()!=0){
                    while(cur.moveToNext()){
                        Pitanje pitanje = new Pitanje(cur.getString(1),cur.getString(1),unpack(cur.getString(2),","),cur.getString(3));
                        pitanja.add(new PitanjeOnline(pitanje,"",cur.getString(0)));
                    }
                }
                return pitanja;
            }
        }catch(SQLiteException e){
            e.printStackTrace();
        }
        return pitanja;
    }

    public ArrayList<Kviz> dajSveKvizove(){
        ArrayList<Kviz> kvizovi = new ArrayList<>();
        ArrayList<KategorijaOnline> kategorijeOnline = dajSveOnlineKategorije();
        ArrayList<PitanjeOnline> pitanjaOnline = dajSvaPitanja();

        try{
            Cursor cur = db.rawQuery("SELECT * FROM " + DATABASE_TABLE_KVIZOVI,null);
            if(cur!=null){
                if(cur.getCount()!=0){
                    while(cur.moveToNext()){
                        Kategorija kategorijaKviza = new Kategorija("Svi","32");
                        ArrayList<Pitanje> pitanjaKviza = new ArrayList<>();
                        String idKategorijeKviza = cur.getString(cur.getColumnIndex(KVIZ_KATEGORIJAID));
                        ArrayList<String> idPitanjaKviza = unpack(cur.getString(cur.getColumnIndex(KVIZ_PITANJAIDS)),",");
                        for(int i=0;i<kategorijeOnline.size();i++){
                            if(idKategorijeKviza.equals(kategorijeOnline.get(i).getOnlIdKategorije()))kategorijaKviza = kategorijeOnline.get(i).getKategorija();
                        }
                        for(int i=0;i<idPitanjaKviza.size();i++){
                            for(int j=0;j<pitanjaOnline.size();j++){
                                if(idPitanjaKviza.get(i).equals(pitanjaOnline.get(j).getNamePitanje()))pitanjaKviza.add(pitanjaOnline.get(j).getPitanje());
                            }
                        }
                        Kviz kviz = new Kviz(cur.getString(3),kategorijaKviza,pitanjaKviza);
                        kvizovi.add(kviz);
                    }
                }
            }
            return kvizovi;
        }catch(SQLiteException e){
            e.printStackTrace();
        }
        return kvizovi;
    }
    public ArrayList<Kategorija> dajSveKategorije(){
        ArrayList<Kategorija> kategorije = new ArrayList<>();

        ArrayList<KategorijaOnline> kategorijeOnline = dajSveOnlineKategorije();
        for(int i=0;i<kategorijeOnline.size();i++){
            kategorije.add(kategorijeOnline.get(i).getKategorija());
        }
        return kategorije;
    }
    public Bundle dajSve(){
        Bundle bundle = new Bundle();
        ArrayList<Kviz> kvizovi = dajSveKvizove();
        ArrayList<Kategorija> kategorije = dajSveKategorije();

        bundle.putParcelableArrayList("kvizovi",kvizovi);
        bundle.putParcelableArrayList("kategorije",kategorije);
        return bundle;
    }
    //public ArrayList<RangIgraca>
}
