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
import ba.unsa.etf.rma.klase.Pitanje;

public class DodajPitanjeOnline extends AsyncTask<String, Void, Void> {
    Context context;
    String kolekcija;
    Pitanje pitanje;
    String nameKviza;
    String namePitanja;
    String idPitanja;
    String errorMessage;
    public onDodajPitanjeOnlineDone pozivatelj;
    public interface onDodajPitanjeOnlineDone{
        public void onDodajPitanjeDone(Pitanje pitanje,String errorMessage);

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        this.pozivatelj.onDodajPitanjeDone(pitanje,this.errorMessage);
    }

    public DodajPitanjeOnline(Context context, String kolekcija, Pitanje pitanje,onDodajPitanjeOnlineDone p){
        this.context = context;
        this.kolekcija = kolekcija;
        this.pitanje = pitanje;
        //this.nameKviza = nameKviza;
        this.pozivatelj = p;
        this.errorMessage = "";
    }
    @Override
    protected Void doInBackground(String... strings) {
        provjeriJelPostojiPitanjeSaIstimNazivom();
        if(!errorMessage.equals(""))return null;
        napraviNoviDokumentPitanja();
        patchajPitanje();
    //    dodajUKvizArrayPitanjaID();
        return null;
    }

    private void napraviNoviDokumentPitanja(){
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

            String pitanje = "";//"{ \"fields\": { \"idIkonice\" : { \"integerValue\":\"" + "" + "\" }, \"naziv\": {\"stringValue\":\"" + "" + "\"}}}";

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
                br.close();
                Log.d("ODGOVOR", response.toString());
                JSONObject noviDokument = new JSONObject(response.toString());
                String idNovog = noviDokument.getString("name");
                String [] strings = idNovog.split("/");
                List<String> stringList = new ArrayList<String>(Arrays.asList(strings));
                this.idPitanja = stringList.get(stringList.size()-1);
                this.namePitanja = idNovog;
                conn.disconnect();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        Log.d("ID pitanja novog:",idPitanja);
    }
    protected void patchajPitanje(){
        GoogleCredential credentials;
        try {
            InputStream tajnaStream = context.getResources().openRawResource(R.raw.secret);
            credentials = GoogleCredential.fromStream(tajnaStream).createScoped(Lists.newArrayList(Collections.singleton("https://www.googleapis.com/auth/datastore")));

            credentials.refreshToken();
            String TOKEN = credentials.getAccessToken();

            Log.d("TOKEN", TOKEN);
            String url = "https://firestore.googleapis.com/v1/"+ namePitanja + "?access_token=";
            URL urlObj = new URL(url + URLEncoder.encode(TOKEN, "UTF-8"));
            String indexTacnog = "";
            for(int i=0;i<pitanje.getOdgovori().size();i++){
                if(pitanje.getOdgovori().get(i).equals(pitanje.getTacan()))indexTacnog=String.valueOf(i);
            }

            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("PATCH");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            String pitanjee =
                    "{ \"fields\": { " +
                            "\"naziv\" : { \"stringValue\":\"" + this.pitanje.getNaziv() + "\" }, " +
                            "\"idKviza\": {\"stringValue\":\"" + "" + "\"}," +
                            "\"indexTacnog\": {\"integerValue\":\"" + indexTacnog + "\"}," +
                            "\"odgovori\":{" +
                            "\"arrayValue\":{"+
                            "\"values\":[";
            for(int i=0;i<pitanje.getOdgovori().size();i++){
                pitanjee += "{ \"stringValue\": \"" + pitanje.getOdgovori().get(i) + "\"},";
            }
            pitanjee += "]}}}}";
            //"}}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = pitanjee.getBytes("utf-8");
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
                //String idNovogKviza = noviDokument.getString("name");
                //nameKviza = idNovogKviza;
                conn.disconnect();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    protected void provjeriJelPostojiPitanjeSaIstimNazivom(){
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
            boolean jelPostojiPitanjeSaIstimImenom = false;
            int countercic = 0;
            if(jo.length()!=0){
                JSONArray dokumenti =  jo.getJSONArray("documents");
                //this.jsonRezultat = "";
                for(int i=0;i<dokumenti.length();i++){
                    JSONObject dokument = dokumenti.getJSONObject(i);
                    String name = dokument.getString("name");
                    JSONObject fields = dokument.getJSONObject("fields");
                    JSONObject naziv = fields.getJSONObject("naziv");
                    if(naziv.getString("stringValue").equals(this.pitanje.getNaziv())){
                        jelPostojiPitanjeSaIstimImenom = true;
                    }
                }
            }
            if(jelPostojiPitanjeSaIstimImenom)errorMessage="Pitanje sa ovim nazivom već postoji!";
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
