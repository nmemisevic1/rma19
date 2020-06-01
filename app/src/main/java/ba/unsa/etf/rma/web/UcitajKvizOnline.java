package ba.unsa.etf.rma.web;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
import java.util.Collections;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.klase.PitanjeOnline;

public class UcitajKvizOnline extends AsyncTask<String, Void, Void> {
    public String idKviza="";
    public ArrayList<PitanjeOnline> pitanjaOnline;
    public onUcitajKvizOnlineDone pozivatelj;
    public interface onUcitajKvizOnlineDone{
        public void onUcitajKvizDone(String nazivKviza,String nameKviza,Kategorija kategorijaKviza, ArrayList<PitanjeOnline> pitanjaKvizaSaIdem, ArrayList<PitanjeOnline> mogucaPitanjaKSaIdem);
        //napraviti on POSTEXECUTE METODU
        // popuniti u aktivnosti sve elemente sa elementima koje sam dobio ovdje
        // praviti DodajKvizOnline klasu...
        // ispraviti kategorije malo
        // Povlaciti samo kvizove koji su u odredjenoj kategoriji uff
        // ranglista esselatu-ve-selamu!
    }


    private Context context;
    private String kolekcija;
    private String nazivKviza;

    public Kviz ucitaniKviz;

    private Kategorija kategorijaKviza;
    private ArrayList<PitanjeOnline> pitanjaKvizaSaIdem;
    private ArrayList<PitanjeOnline> mogucaPitanjaKSaIdem;
    private String nameKviza;
    private String noviNazivKviza="";

    public UcitajKvizOnline(Context context,String kolekcija,String nazivKviza,onUcitajKvizOnlineDone p){
        this.context = context;
        this.kolekcija = kolekcija;
        this.nazivKviza = nazivKviza;
        this.pozivatelj = p;
        this.pitanjaKvizaSaIdem = new ArrayList<>();
        this.mogucaPitanjaKSaIdem = new ArrayList<>();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        pozivatelj.onUcitajKvizDone(this.nazivKviza,this.nameKviza,this.kategorijaKviza,this.pitanjaKvizaSaIdem,this.mogucaPitanjaKSaIdem);
    }

    @Override
    protected Void doInBackground(String... strings) {
        if(!this.nazivKviza.equals("Dodaj kviz"))popuniPodatkeOKvizu();
        povuciMogucaPitanja();
        //povuci citav json
        //nadji kviz koji te zanima preko naziva
        //povuci sva pitanja
        //u dodana pitanja stavi ona koja su u kvizu
        //u moguca stavi ona koja ne pripadaju nijednom kvizu

        return null;
    }

    private void popuniPodatkeOKvizu() {
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
            ArrayList<String> ideviPitanja=new ArrayList<>();

            if(jo.length()!=0){
                JSONArray dokumenti =  jo.getJSONArray("documents");
                //this.jsonRezultat = "";

                for(int i=0;i<dokumenti.length();i++){
                    JSONObject dokument = dokumenti.getJSONObject(i);
                    String name = dokument.getString("name");

                    JSONObject fields = dokument.getJSONObject("fields");
                    JSONObject naziv = fields.getJSONObject("naziv");
                    if(naziv.getString("stringValue").equals(this.nazivKviza)){
                        JSONObject idKategorije = fields.getJSONObject("idKategorije");
                        JSONObject pitanja = fields.getJSONObject("pitanja");
                        JSONObject arrayValue = pitanja.getJSONObject("arrayValue");
                        if(arrayValue.length()!=0){
                            JSONArray values = arrayValue.getJSONArray("values");
                            for(int j=0;j<values.length();j++){
                                JSONObject idPitanja = values.getJSONObject(j);
                                ideviPitanja.add(idPitanja.getString("stringValue"));
                            }
                        }
                        this.noviNazivKviza = naziv.getString("stringValue");
                        onlIdKategorije = idKategorije.getString("stringValue");
                        this.nameKviza = name;
                    }
                }
            }
            //PARSIRANJE JSON KATEGORIJE
            String adresaKategorije = "https://firestore.googleapis.com/v1/projects/rma19memisevic10/databases/(default)/documents/Kategorije/" + onlIdKategorije + "?access_token=";
            URL urlKategorije = new URL(adresaKategorije + URLEncoder.encode(TOKEN,"UTF-8"));
            HttpURLConnection urlGetConnectionKategorija = (HttpURLConnection)urlKategorije.openConnection();
            InputStream inKategorije = new BufferedInputStream(urlGetConnectionKategorija.getInputStream());
            String rezultatKategorije = convertStreamToString(inKategorije);

            JSONObject joKategorije = new JSONObject(rezultatKategorije);
            JSONObject fieldsKategorije = joKategorije.getJSONObject("fields");
            JSONObject nazivKategorije = fieldsKategorije.getJSONObject("naziv");
            JSONObject idIkonice = fieldsKategorije.getJSONObject("idIkonice");

            this.kategorijaKviza = new Kategorija(nazivKategorije.getString("stringValue"),String.valueOf(idIkonice.getInt("integerValue")));

            //PARSIRANJE JSON PITANJA
            pitanjaKvizaSaIdem = new ArrayList<>();
            for(int i=0;i<ideviPitanja.size();i++){
                String adresaPitanja = "https://firestore.googleapis.com/v1/projects/rma19memisevic10/databases/(default)/documents/Pitanja/" + ideviPitanja.get(i) + "?access_token=";
                URL urlPitanja = new URL(adresaPitanja + URLEncoder.encode(TOKEN,"UTF-8"));
                HttpURLConnection urlGetConnectionPitanje = (HttpURLConnection)urlPitanja.openConnection();
                InputStream inPitanja = new BufferedInputStream(urlGetConnectionPitanje.getInputStream());
                String rezultatPitanja = convertStreamToString(inPitanja);

                JSONObject joPitanja = new JSONObject(rezultatPitanja);
                String namePitanja = joPitanja.getString("name");
                JSONObject fields = joPitanja.getJSONObject("fields");
                JSONObject naziv = fields.getJSONObject("naziv");
                JSONObject indexTacnog = fields.getJSONObject("indexTacnog");
                JSONObject odgovori = fields.getJSONObject("odgovori");
                JSONObject arrayValue = odgovori.getJSONObject("arrayValue");
                JSONArray values = arrayValue.getJSONArray("values");
                if(indexTacnog.getInt("integerValue")>=values.length())continue;

                String nazivPitanja="";String tacan="";ArrayList<String> odgovoriPitanja=new ArrayList<>();String tacanOdgovor = "";
                for(int j=0;j<values.length();j++){
                    JSONObject odgovor = values.getJSONObject(j);
                    odgovoriPitanja.add(odgovor.getString("stringValue"));
                }
                tacanOdgovor = odgovoriPitanja.get(indexTacnog.getInt("integerValue"));
                nazivPitanja = naziv.getString("stringValue");
                this.pitanjaKvizaSaIdem.add(new PitanjeOnline(new Pitanje(nazivPitanja,nazivPitanja,odgovoriPitanja,tacanOdgovor),nameKviza,namePitanja));
                //this.pitanjaKviza.add(new Pitanje(nazivPitanja,nazivPitanja,odgovoriPitanja,tacanOdgovor));
            }



        }catch(IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void povuciMogucaPitanja(){
        mogucaPitanjaKSaIdem = new ArrayList<>();
        GoogleCredential credentials;
        try{
            // provjeri jel postoji vec ta kategorija

            InputStream tajnaStream = context.getResources().openRawResource(R.raw.secret);
            credentials = GoogleCredential.fromStream(tajnaStream).createScoped(Lists.newArrayList(Collections.singleton("https://www.googleapis.com/auth/datastore")));

            credentials.refreshToken();
            String TOKEN = credentials.getAccessToken();

            Log.d("TOKEN",TOKEN);
            //get zahtjev za sve kategorije
            String adresa = "https://firestore.googleapis.com/v1/projects/rma19memisevic10/databases/(default)/documents/" + "Pitanja" + "?access_token=";
            URL urlA = new URL(adresa + URLEncoder.encode(TOKEN,"UTF-8"));
            HttpURLConnection urlGetConnection = (HttpURLConnection)urlA.openConnection();
            InputStream in = new BufferedInputStream(urlGetConnection.getInputStream());
            String rezultat = convertStreamToString(in);

            JSONObject jo = new JSONObject(rezultat);
            if(jo.length()!=0){
                JSONArray dokumenti =  jo.getJSONArray("documents");
                //this.jsonRezultat = "";

                for(int i=0;i<dokumenti.length();i++){
                    JSONObject dokument = dokumenti.getJSONObject(i);
                    String namePitanja = dokument.getString("name");
                    JSONObject fields = dokument.getJSONObject("fields");
                    JSONObject idKviza = fields.getJSONObject("idKviza");
                    //nameKviza = idKviza.getString("stringValue");
                    if(idKviza.getString("stringValue").equals("")){
                        JSONObject naziv = fields.getJSONObject("naziv");
                        JSONObject indexTacnog = fields.getJSONObject("indexTacnog");
                        JSONObject odgovori = fields.getJSONObject("odgovori");
                        JSONObject arrayValue = odgovori.getJSONObject("arrayValue");
                        JSONArray values = arrayValue.getJSONArray("values");
                        if(indexTacnog.getInt("integerValue")>values.length())continue;

                        String nazivPitanja="";ArrayList<String> odgovoriPitanja=new ArrayList<>();String tacanOdgovor = "";
                        for(int j=0;j<values.length();j++){
                            JSONObject odgovor = values.getJSONObject(j);
                            odgovoriPitanja.add(odgovor.getString("stringValue"));
                        }
                        tacanOdgovor = odgovoriPitanja.get(indexTacnog.getInt("integerValue"));
                        nazivPitanja = naziv.getString("stringValue");

                        this.mogucaPitanjaKSaIdem.add(new PitanjeOnline(new Pitanje(nazivPitanja,nazivPitanja,odgovoriPitanja,tacanOdgovor),nameKviza,namePitanja));
                    }

                }
            }
        }catch(IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
}
