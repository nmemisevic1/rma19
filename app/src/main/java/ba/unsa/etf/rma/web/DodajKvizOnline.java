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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.klase.PitanjeOnline;

public class DodajKvizOnline extends AsyncTask<String, Void, Void> {
    private Kviz kviz;
    private ArrayList<Pitanje> mogucaPitanja;
    private ArrayList<Pitanje> dodanaPitanja;
    private ArrayList<String> ideviPitanjaKviza;
    private ArrayList<PitanjeOnline> mogucaPitanjaSaIdem;
    private ArrayList<PitanjeOnline> dodanaPitanjaSaIdem;
    private String nameKviza;
    private String errorMessage;
    private String kolekcija;
    private Context context;
    public onDodajKvizOnlineDone pozivatelj;
    private String opcija;
    private int counter;

    public interface onDodajKvizOnlineDone{
        public void onDodajKvizDone(Kviz k,String errorMessage);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        pozivatelj.onDodajKvizDone(kviz,errorMessage);
    }

    public DodajKvizOnline(Context context,Kviz kviz, String kolekcija, ArrayList<Pitanje> mogucaPitanja, ArrayList<Pitanje> dodanaPitanja,
                           ArrayList<PitanjeOnline> mogucaPitanjaSaIdem, ArrayList<PitanjeOnline> dodanaPitanjaSaIdem, String nameKviza, String opcija, onDodajKvizOnlineDone p){
        this.context = context;
        this.kolekcija = kolekcija;
        this.mogucaPitanja = mogucaPitanja;
        this.dodanaPitanja = dodanaPitanja;
        this.mogucaPitanjaSaIdem = mogucaPitanjaSaIdem;
        this.dodanaPitanjaSaIdem = dodanaPitanjaSaIdem;
        this.nameKviza = nameKviza;
        this.errorMessage = "";
        this.opcija = opcija;
        this.counter = 0;
        this.pozivatelj = p;
        this.kviz = kviz;

    }
    protected void kreirajNoviDokumentKvizaIGetID(){
        GoogleCredential credentials;
        try {
            InputStream tajnaStream = context.getResources().openRawResource(R.raw.secret);
            credentials = GoogleCredential.fromStream(tajnaStream).createScoped(Lists.newArrayList(Collections.singleton("https://www.googleapis.com/auth/datastore")));

            credentials.refreshToken();
            String TOKEN = credentials.getAccessToken();

            Log.d("TOKEN", TOKEN);
            String url = "https://firestore.googleapis.com/v1/projects/rma19memisevic10/databases/(default)/documents/" + kolekcija + "?access_token=";
            URL urlObj = new URL(url + URLEncoder.encode(TOKEN, "UTF-8"));

            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            String kviz = "";//"{ \"fields\": { \"idIkonice\" : { \"integerValue\":\"" + "" + "\" }, \"naziv\": {\"stringValue\":\"" + "" + "\"}}}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = kviz.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            InputStream odgovor = conn.getInputStream();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(odgovor, "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                br.close();
                Log.d("ODGOVOR", response.toString());
                JSONObject noviDokument = new JSONObject(response.toString());
                String idNovogKviza = noviDokument.getString("name");
                nameKviza = idNovogKviza;
                conn.disconnect();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    private String dajIdKategorije(){
        GoogleCredential credentials;
        String kategorijica="";
        try{
            // provjeri jel postoji vec ta kategorija

            InputStream tajnaStream = context.getResources().openRawResource(R.raw.secret);
            credentials = GoogleCredential.fromStream(tajnaStream).createScoped(Lists.newArrayList(Collections.singleton("https://www.googleapis.com/auth/datastore")));

            credentials.refreshToken();
            String TOKEN = credentials.getAccessToken();

            Log.d("TOKEN",TOKEN);
            //get zahtjev za sve kategorije
            String adresa = "https://firestore.googleapis.com/v1/projects/rma19memisevic10/databases/(default)/documents/" + "Kategorije" + "?access_token=";
            URL urlA = new URL(adresa + URLEncoder.encode(TOKEN,"UTF-8"));
            HttpURLConnection urlGetConnection = (HttpURLConnection)urlA.openConnection();
            InputStream in = new BufferedInputStream(urlGetConnection.getInputStream());
            String rezultat = convertStreamToString(in);
            boolean jelPostojiKategorija = false;
            JSONObject jo = new JSONObject(rezultat);
            if(jo.length()!=0){
                //this.jsonRezultat = rezultat;

                JSONArray dokumenti =  jo.getJSONArray("documents");

                for(int i=0;i<dokumenti.length();i++){
                    JSONObject dokument = dokumenti.getJSONObject(i);
                    JSONObject fields = dokument.getJSONObject("fields");
                    JSONObject naziv = fields.getJSONObject("naziv");
                    if(naziv.getString("stringValue").equals(kviz.getKategorija().getNaziv())){
                        String nameKategorije = dokument.getString("name");

                        String [] strings = nameKategorije.split("/");
                        List<String> stringList = new ArrayList<String>(Arrays.asList(strings));
                        kategorijica = stringList.get(stringList.size()-1);
                        break;
                    }
                    JSONObject id = fields.getJSONObject("idIkonice");
                    String nazivKategorije = naziv.getString("stringValue");
                    int idKategorije = id.getInt("integerValue");
                }
                urlGetConnection.disconnect();
            }
            return kategorijica;
        }catch(IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return kategorijica;
    }
    protected void patchajKviz(){
        GoogleCredential credentials;
        try {
            InputStream tajnaStream = context.getResources().openRawResource(R.raw.secret);
            credentials = GoogleCredential.fromStream(tajnaStream).createScoped(Lists.newArrayList(Collections.singleton("https://www.googleapis.com/auth/datastore")));

            credentials.refreshToken();
            String TOKEN = credentials.getAccessToken();

            Log.d("TOKEN", TOKEN);
            String url = "https://firestore.googleapis.com/v1/"+ nameKviza + "?access_token=";
            URL urlObj = new URL(url + URLEncoder.encode(TOKEN, "UTF-8"));

            String kategorija = dajIdKategorije();
            Log.d("KATEGORIJA",kategorija);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("PATCH");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            String kviz =
                    "{ \"fields\": { " +
                        "\"naziv\" : { \"stringValue\":\"" + this.kviz.getNaziv() + "\" }, " +
                            "\"idKategorije\": {\"stringValue\":\"" + kategorija + "\"}," +
                            "\"pitanja\":{" +
                                "\"arrayValue\":{"+
                                    "\"values\":[";
            for(int i=0;i<dodanaPitanjaSaIdem.size();i++){
                String [] strings = dodanaPitanjaSaIdem.get(i).getNamePitanje().split("/");
                List<String> stringList = new ArrayList<String>(Arrays.asList(strings));
                String idPitanja1 = stringList.get(stringList.size()-1);
                kviz += "{ \"stringValue\": \"" + idPitanja1 + "\"},";
            }
            kviz += "]}}}}";
                            //"}}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = kviz.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            InputStream odgovor = conn.getInputStream();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(odgovor, "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                br.close();
                Log.d("ODGOVOR", response.toString());
                JSONObject noviDokument = new JSONObject(response.toString());
                String idNovogKviza = noviDokument.getString("name");
                nameKviza = idNovogKviza;
                conn.disconnect();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    private void razvrstajIUpdateujPitanjaNaBazi(){
        /* razvrstavanje */

        ArrayList<PitanjeOnline> svaPitanjaSaIdem = new ArrayList<>();
        svaPitanjaSaIdem.addAll(dodanaPitanjaSaIdem);
        svaPitanjaSaIdem.addAll(mogucaPitanjaSaIdem);
        dodanaPitanjaSaIdem = new ArrayList<>();
        mogucaPitanjaSaIdem = new ArrayList<>();
        for(int i=0;i<svaPitanjaSaIdem.size();i++){
            for(int j=0;j<dodanaPitanja.size()-1;j++){
                if(svaPitanjaSaIdem.get(i).getPitanje().getNaziv().equals(dodanaPitanja.get(j).getNaziv())){
                    svaPitanjaSaIdem.get(i).setIdKviza(nameKviza);
                    dodanaPitanjaSaIdem.add(svaPitanjaSaIdem.get(i));
                }
            }
            for(int j=0;j<mogucaPitanja.size();j++){
                if(svaPitanjaSaIdem.get(i).getPitanje().getNaziv().equals(mogucaPitanja.get(j).getNaziv())){
                    svaPitanjaSaIdem.get(i).setIdKviza("");
                    mogucaPitanjaSaIdem.add(svaPitanjaSaIdem.get(i));
                }
            }
        }
        /*dodanaPitanjaSaIdem = new ArrayList<>();
        mogucaPitanjaSaIdem = new ArrayList<>();
        for(int i=0;i<dodanaPitanja.size()-1;i++){
            for(int j=0;j<svaPitanjaSaIdem.size();j++){
                if(dodanaPitanja.get(i).getNaziv().equals(svaPitanjaSaIdem.get(j).getPitanje().getNaziv())) {
                    dodanaPitanjaSaIdem.add(new PitanjeOnline(dodanaPitanja.get(i), nameKviza, svaPitanjaSaIdem.get(j).getIdKviza()));
                    svaPitanjaSaIdem.remove(j);
                    j--;
                }
            }
        }
        mogucaPitanjaSaIdem.addAll(svaPitanjaSaIdem);
        for(int i=0;i<mogucaPitanjaSaIdem.size();i++){
            mogucaPitanjaSaIdem.get(i).setIdKviza("");
        }*/
        /********************************************************/
        //ArrayList<PitanjeOnline> pitanjaSpremnaZaUpdate = new ArrayList<>();
        //pitanjaSpremnaZaUpdate.addAll(dodanaPitanjaSaIdem);
        //pitanjaSpremnaZaUpdate.addAll(mogucaPitanjaSaIdem);
        /* update pitanja na bazi */
        GoogleCredential credentials;
        try {
            InputStream tajnaStream = context.getResources().openRawResource(R.raw.secret);
            credentials = GoogleCredential.fromStream(tajnaStream).createScoped(Lists.newArrayList(Collections.singleton("https://www.googleapis.com/auth/datastore")));

            credentials.refreshToken();
            String TOKEN = credentials.getAccessToken();

            Log.d("TOKEN", TOKEN);
            for(int i=0;i<svaPitanjaSaIdem.size();i++){
                String indeksTacnog="0";
                String url = "https://firestore.googleapis.com/v1/"+ svaPitanjaSaIdem.get(i).getNamePitanje() + "?access_token=";
                URL urlObj = new URL(url + URLEncoder.encode(TOKEN, "UTF-8"));

                HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("PATCH");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                String pitanje =
                        "{ \"fields\": { " +
                                "\"idKviza\" : { \"stringValue\":\"" + svaPitanjaSaIdem.get(i).getIdKviza() + "\" }," +
                                "\"naziv\": {\"stringValue\": \"" + svaPitanjaSaIdem.get(i).getPitanje().getNaziv() + "\"}," +
                                "\"odgovori\": {\"arrayValue\":{\"values\": [";

                for(int j=0;j<svaPitanjaSaIdem.get(i).getPitanje().getOdgovori().size();j++){
                    if(svaPitanjaSaIdem.get(i).getPitanje().getOdgovori().get(j).equals(svaPitanjaSaIdem.get(i).getPitanje().getTacan()))indeksTacnog=String.valueOf(j);
                    pitanje += "{ \"stringValue\": \""+ svaPitanjaSaIdem.get(i).getPitanje().getOdgovori().get(j) + "\"},";
                }
                pitanje += "]}}," + "\"indexTacnog\":{\"integerValue\":\""+indeksTacnog+"\""
                        +"}}}";

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = pitanje.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int code = conn.getResponseCode();
                InputStream odgovor = conn.getInputStream();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(odgovor, "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    Log.d("ODGOVOR", response.toString());
                    JSONObject noviDokument = new JSONObject(response.toString());
                    conn.disconnect();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }


    @Override
    protected Void doInBackground(String... strings) {
        provjeriJelPostojiKvizSaIstimImenom();
        if(!this.errorMessage.equals("")) return null;

        if(this.opcija.equals("Dodaj kviz")) kreirajNoviDokumentKvizaIGetID();
        razvrstajIUpdateujPitanjaNaBazi();
        patchajKviz();

        //razvrstajIUpdateujPitanjaNaBazi();

        //mogucimPitanjimaIDKvizaStaviNaNull();
        return null;
    }
    protected void provjeriJelPostojiKvizSaIstimImenom(){
        GoogleCredential credentials;
        try{
            InputStream tajnaStream = context.getResources().openRawResource(R.raw.secret);
            credentials = GoogleCredential.fromStream(tajnaStream).createScoped(Lists.newArrayList(Collections.singleton("https://www.googleapis.com/auth/datastore")));

            credentials.refreshToken();
            String TOKEN = credentials.getAccessToken();

            Log.d("TOKEN",TOKEN);

            String adresa = "https://firestore.googleapis.com/v1/projects/rma19memisevic10/databases/(default)/documents/" + kolekcija + "?access_token=";
            URL urlA = new URL(adresa + URLEncoder.encode(TOKEN,"UTF-8"));
            HttpURLConnection urlGetConnection = (HttpURLConnection)urlA.openConnection();
            InputStream in = new BufferedInputStream(urlGetConnection.getInputStream());
            String rezultat = convertStreamToString(in);

            JSONObject jo = new JSONObject(rezultat);

            String onlIdKategorije="";
            ArrayList<String> ideviPitanja=new ArrayList<>();
            boolean jelPostojiKvizSaIstimImenom = false;
            int countercic = 0;
            if(jo.length()!=0){
                JSONArray dokumenti =  jo.getJSONArray("documents");
                //this.jsonRezultat = "";
                this.counter = dokumenti.length();
                for(int i=0;i<dokumenti.length();i++){
                    JSONObject dokument = dokumenti.getJSONObject(i);
                    String name = dokument.getString("name");

                    JSONObject fields = dokument.getJSONObject("fields");
                    JSONObject naziv = fields.getJSONObject("naziv");
                    if(naziv.getString("stringValue").equals(this.kviz.getNaziv()) && opcija.equals("Dodaj kviz")){
                        jelPostojiKvizSaIstimImenom = true;
                    }
                    if(naziv.getString("stringValue").equals(this.kviz.getNaziv())&&opcija.equals("Spasi kviz"))countercic++;
                }
            }
            if(countercic>1)jelPostojiKvizSaIstimImenom=true;
            if(jelPostojiKvizSaIstimImenom)errorMessage="Kviz sa ovim imenom već postoji!";
            urlGetConnection.disconnect();
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
