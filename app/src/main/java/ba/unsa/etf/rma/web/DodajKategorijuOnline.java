package ba.unsa.etf.rma.web;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.aktivnosti.DodajKategorijuAkt;
import ba.unsa.etf.rma.klase.Kategorija;


public class DodajKategorijuOnline extends AsyncTask<String, Void, Void> {
    private String jsonRezultat="";
    private Context context;
    public String kolekcija;
    public String poruka="";
    public Kategorija kategorija;
    public String nazivNoveKategorije;
    public String idNoveKategorije;
    private OnKategorijaAddDone pozivatelj;
    public DodajKategorijuOnline(Context context, String kolekcija, String nazivNoveKategorije, String idNoveKategorije,OnKategorijaAddDone p){
        this.context = context;
        this.kolekcija = kolekcija;
        this.nazivNoveKategorije = nazivNoveKategorije;
        this.idNoveKategorije = idNoveKategorije;
        this.pozivatelj = p;
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        pozivatelj.onDone(kategorija);
    }
    protected void dodajKategoriju(){
        GoogleCredential credentials;
        try{
            // provjeri jel postoji vec ta kategorija

            InputStream tajnaStream = context.getResources().openRawResource(R.raw.secret);
            credentials = GoogleCredential.fromStream(tajnaStream).createScoped(Lists.newArrayList(Collections.singleton("https://www.googleapis.com/auth/datastore")));

            credentials.refreshToken();
            String TOKEN = credentials.getAccessToken();

            Log.d("TOKEN",TOKEN);
            //get zahtjev za sve kategorije
            String adresa = "https://firestore.googleapis.com/v1/projects/rma19memisevic10/databases/(default)/documents/" + kolekcija + "?access_token=";
            URL urlA = new URL(adresa + URLEncoder.encode(TOKEN,"UTF-8"));
            HttpURLConnection urlGetConnection = (HttpURLConnection)urlA.openConnection();
            InputStream in = new BufferedInputStream(urlGetConnection.getInputStream());
            String rezultat = convertStreamToString(in);
            boolean jelPostojiKategorija = false;
            JSONObject jo = new JSONObject(rezultat);
            if(jo.length()!=0){
                //this.jsonRezultat = rezultat;

                JSONArray dokumenti =  jo.getJSONArray("documents");
                this.jsonRezultat = "";

                for(int i=0;i<dokumenti.length();i++){
                    JSONObject dokument = dokumenti.getJSONObject(i);
                    JSONObject fields = dokument.getJSONObject("fields");
                    JSONObject naziv = fields.getJSONObject("naziv");
                    JSONObject id = fields.getJSONObject("idIkonice");
                    String nazivKategorije = naziv.getString("stringValue");
                    int idKategorije = id.getInt("integerValue");
                    if(nazivKategorije.equals(this.nazivNoveKategorije) || String.valueOf(idKategorije).equals(this.idNoveKategorije)){
                        jelPostojiKategorija = true;
                    }
                    this.jsonRezultat += nazivKategorije + " " + String.valueOf(idKategorije) + "\n";
                }
            }

            if(!jelPostojiKategorija){
                String url = "https://firestore.googleapis.com/v1/projects/rma19memisevic10/databases/(default)/documents/Kategorije?access_token=";
                URL urlObj = new URL(url+ URLEncoder.encode(TOKEN,"UTF-8"));

                HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type","application/json");
                conn.setRequestProperty("Accept","application/json");

                String kategorija = "{ \"fields\": { \"idIkonice\" : { \"integerValue\":\""+this.idNoveKategorije+"\" }, \"naziv\": {\"stringValue\":\""+this.nazivNoveKategorije+"\"}}}";

                try(OutputStream os = conn.getOutputStream()){
                    byte[] input = kategorija.getBytes("utf-8");
                    os.write(input,0,input.length);
                }

                int code = conn.getResponseCode();
                InputStream odgovor = conn.getInputStream();
                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(odgovor,"utf-8"))){
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while((responseLine = br.readLine())!=null){
                        response.append(responseLine.trim());
                    }
                    Log.d("ODGOVOR",response.toString());
                }
                this.kategorija = new Kategorija(nazivNoveKategorije,idNoveKategorije);
            }
            else{
                poruka = "Kategorija koju želite dodati već postoji u bazi";
            }


        }catch(IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected Void doInBackground(String... strings){
        dodajKategoriju();
        return null;
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
    //https://firestore.googleapis.com/v1/projects/rma19memisevic10/databases/(default)/documents/Kategorije?access_token=

    public interface OnKategorijaAddDone{
        public void onDone(Kategorija kategorija);
    }
}