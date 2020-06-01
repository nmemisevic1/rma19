package ba.unsa.etf.rma.web;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.util.Lists;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.aktivnosti.KvizoviAkt;
import ba.unsa.etf.rma.baze.SpiralaBazaOpenHelper;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.klase.PitanjeOnline;

public class UcitajKvizoveOnline extends AsyncTask<String, Void, Void> {
    Context context;

    String kategorija="";
    String kolekcija="";
    ArrayList<Kviz> kvizovi;
    OnUcitajKvizoveOnlineDone pozivatelj;

    SpiralaBazaOpenHelper helper;
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        helper.close();
        pozivatelj.onUcitajKvizoveDone(this.kvizovi);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(context,"Molimo sačekajte dok se podaci učitaju sa interneta",Toast.LENGTH_LONG).show();
        //Toast.makeText(context,"Pocelo povlacenje kvizova",Toast.LENGTH_SHORT).show();
    }

    public UcitajKvizoveOnline(Context context, String kategorija, String kolekcija, OnUcitajKvizoveOnlineDone p){
        this.context = context;
        this.kategorija = kategorija;
        this.kolekcija = kolekcija;
        this.pozivatelj = p;
        this.kvizovi = new ArrayList<>();
        this.helper = new SpiralaBazaOpenHelper(context);
    }
    private void ucitajKvizove() {
        GoogleCredential credentials;
        try{
            InputStream tajnaStream = context.getResources().openRawResource(R.raw.secret);
            credentials = GoogleCredential.fromStream(tajnaStream).createScoped(Lists.newArrayList(Collections.singleton("https://www.googleapis.com/auth/datastore")));

            credentials.refreshToken();
            String TOKEN = credentials.getAccessToken();

            Log.d("TOKEN",TOKEN);

            String adresa = "https://firestore.googleapis.com/v1/projects/rma19memisevic10/databases/(default)/documents/" + kolekcija + "?pageSize=1000&access_token=";
            URL urlA = new URL(adresa + URLEncoder.encode(TOKEN,"UTF-8"));
            HttpURLConnection urlGetConnection = (HttpURLConnection)urlA.openConnection();
            InputStream in = new BufferedInputStream(urlGetConnection.getInputStream());
            String rezultat = convertStreamToString(in);

            JSONObject jo = new JSONObject(rezultat);

            String onlIdKategorije="";


            if(jo.length()!=0) {
                JSONArray dokumenti = jo.getJSONArray("documents");
                //this.jsonRezultat = "";

                for (int i = 0; i < dokumenti.length(); i++) {
                    Kategorija kategorijaKviza;
                    ArrayList<String> ideviPitanja=new ArrayList<>();
                    ArrayList<Pitanje> pitanjaKviza = new ArrayList<>();
                    String nazivKviza="";
                    JSONObject dokument = dokumenti.getJSONObject(i);
                    String nameKviza = dokument.getString("name");
                    ArrayList<String> bbrbw = unpack(nameKviza,"/");
                    nameKviza = bbrbw.get(bbrbw.size()-1);
                    JSONObject fields = dokument.getJSONObject("fields");
                    JSONObject idKategorije = fields.getJSONObject("idKategorije");
                    //PARSIRANJE JSON KATEGORIJE
                    String adresaKategorije = "https://firestore.googleapis.com/v1/projects/rma19memisevic10/databases/(default)/documents/Kategorije/" + idKategorije.getString("stringValue") + "?access_token=";
                    URL urlKategorije = new URL(adresaKategorije + URLEncoder.encode(TOKEN, "UTF-8"));
                    HttpURLConnection urlGetConnectionKategorija = (HttpURLConnection) urlKategorije.openConnection();
                    InputStream inKategorije = new BufferedInputStream(urlGetConnectionKategorija.getInputStream());
                    String rezultatKategorije = convertStreamToString(inKategorije);

                    JSONObject joKategorije = new JSONObject(rezultatKategorije);
                    JSONObject fieldsKategorije = joKategorije.getJSONObject("fields");
                    JSONObject nazivKategorije = fieldsKategorije.getJSONObject("naziv");
                    JSONObject idIkonice = fieldsKategorije.getJSONObject("idIkonice");
                    //inKategorije.close();
                    //urlGetConnection.disconnect();

                    if(nazivKategorije.getString("stringValue").equals(kategorija) || kategorija.equals("Svi")){
                        kategorijaKviza = new Kategorija(nazivKategorije.getString("stringValue"), String.valueOf(idIkonice.getInt("integerValue")));
                        JSONObject naziv = fields.getJSONObject("naziv");


                        JSONObject pitanja = fields.getJSONObject("pitanja");
                        JSONObject arrayValue = pitanja.getJSONObject("arrayValue");
                        if (arrayValue.length() != 0) {
                            JSONArray values = arrayValue.getJSONArray("values");
                            for (int j = 0; j < values.length(); j++) {
                                JSONObject idPitanja = values.getJSONObject(j);
                                ideviPitanja.add(idPitanja.getString("stringValue"));
                            }
                        }
                        nazivKviza = naziv.getString("stringValue");
                        //PARSIRANJE JSON PITANJA
                        for (int j = 0; j < ideviPitanja.size(); j++) {
                            String adresaPitanja = "https://firestore.googleapis.com/v1/projects/rma19memisevic10/databases/(default)/documents/Pitanja/" + ideviPitanja.get(j) + "?access_token=";
                            URL urlPitanja = new URL(adresaPitanja + URLEncoder.encode(TOKEN, "UTF-8"));
                            HttpURLConnection urlGetConnectionPitanje = (HttpURLConnection) urlPitanja.openConnection();
                            InputStream inPitanja = new BufferedInputStream(urlGetConnectionPitanje.getInputStream());
                            String rezultatPitanja = convertStreamToString(inPitanja);

                            JSONObject joPitanja = new JSONObject(rezultatPitanja);
                            String namePitanja = joPitanja.getString("name");
                            JSONObject fieldsPitanja = joPitanja.getJSONObject("fields");
                            JSONObject nazivPitanja = fieldsPitanja.getJSONObject("naziv");
                            JSONObject indexTacnog = fieldsPitanja.getJSONObject("indexTacnog");
                            JSONObject odgovori = fieldsPitanja.getJSONObject("odgovori");
                            JSONObject arrayValuePitanja = odgovori.getJSONObject("arrayValue");
                            JSONArray values = arrayValuePitanja.getJSONArray("values");
                            if (values.length() != 0) {

                                if (indexTacnog.getInt("integerValue") >= values.length()) continue;

                                String noviNazivPitanja = "";
                                String tacan = "";
                                ArrayList<String> odgovoriPitanja = new ArrayList<>();
                                String tacanOdgovor = "";
                                for (int k = 0; k < values.length(); k++) {
                                    JSONObject odgovor = values.getJSONObject(k);
                                    odgovoriPitanja.add(odgovor.getString("stringValue"));
                                }
                                tacanOdgovor = odgovoriPitanja.get(indexTacnog.getInt("integerValue"));
                                noviNazivPitanja = nazivPitanja.getString("stringValue");
                                pitanjaKviza.add(new Pitanje(noviNazivPitanja, noviNazivPitanja, odgovoriPitanja, tacanOdgovor));
                            }
                        }
                        this.kvizovi.add(new Kviz(nazivKviza,kategorijaKviza,pitanjaKviza));
                        helper.dodajKviz(new Kviz(nazivKviza,kategorijaKviza,pitanjaKviza),nameKviza,idKategorije.getString("stringValue"),ideviPitanja);
                    }
                }
            }





        }catch(IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("KOLIKO IMA KVIZOVA: ",String.valueOf(this.kvizovi.size()));
    }
    public String convertStreamToString(InputStream is){
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try{
            while((line = reader.readLine())!=null){
                sb.append(line + "\n");
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            try{
                is.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    @Override
    protected Void doInBackground(String... strings) {
        ucitajKvizove();
        return null;
    }

    public interface OnUcitajKvizoveOnlineDone{
        public void onUcitajKvizoveDone(ArrayList<Kviz> kvizovi);
    }
    private ArrayList<String> unpack(String s,String regeks){
        String [] strings = s.split(regeks);
        return new ArrayList<String>(Arrays.asList(strings));
    }
}
