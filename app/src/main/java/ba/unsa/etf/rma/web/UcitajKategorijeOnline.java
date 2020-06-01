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
import ba.unsa.etf.rma.baze.SpiralaBazaOpenHelper;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

public class UcitajKategorijeOnline extends AsyncTask<String, Void, Void> {
    private Context context;
    private String kolekcija;
    public ArrayList<Kategorija> kategorije;
    private onUcitajKategorijeOnlineDone pozivatelj;
    private SpiralaBazaOpenHelper helper;
    public UcitajKategorijeOnline(Context context,String kolekcija,onUcitajKategorijeOnlineDone p){
        this.context = context;
        this.kolekcija = kolekcija;
        this.pozivatelj = p;
        kategorije = new ArrayList<>();
        this.helper = new SpiralaBazaOpenHelper(context);
    }
    public interface onUcitajKategorijeOnlineDone{
        public void onKategorijeDone(ArrayList<Kategorija> k);
    }
    @Override
    protected void onPostExecute(Void aVoid){
        super.onPostExecute(aVoid);
        if(kategorije.size()!=0){
            //Toast.makeText(context,"Kategorije učitane iz baze podataka!",Toast.LENGTH_SHORT).show();
        }
        else{
            //Toast.makeText(context,"Nema kategorija u bazi podataka ili je došlo do greške!",Toast.LENGTH_SHORT).show();
        }
        pozivatelj.onKategorijeDone(this.kategorije);
    }
    @Override
    protected Void doInBackground(String... strings) {

        GoogleCredential credentials;
        try{
            // provjeri jel postoji vec ta kategorija

            InputStream tajnaStream = context.getResources().openRawResource(R.raw.secret);
            credentials = GoogleCredential.fromStream(tajnaStream).createScoped(Lists.newArrayList(Collections.singleton("https://www.googleapis.com/auth/datastore")));

            credentials.refreshToken();
            String TOKEN = credentials.getAccessToken();

            Log.d("TOKEN",TOKEN);
            //get zahtjev za sve kategorije
            String adresa = "https://firestore.googleapis.com/v1/projects/rma19memisevic10/databases/(default)/documents/" + kolekcija + "?pageSize=1000&access_token=";
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

                    String nameKategorije = dokument.getString("name");
                    ArrayList<String> bbrbw = unpack(nameKategorije,"/");
                    nameKategorije = bbrbw.get(bbrbw.size()-1);

                    JSONObject fields = dokument.getJSONObject("fields");
                    JSONObject naziv = fields.getJSONObject("naziv");
                    JSONObject id = fields.getJSONObject("idIkonice");
                    String nazivKategorije = naziv.getString("stringValue");
                    int idKategorije = id.getInt("integerValue");
                    this.kategorije.add(new Kategorija(nazivKategorije,String.valueOf(idKategorije)));
                    this.helper.dodajKategoriju(new Kategorija(nazivKategorije,String.valueOf(idKategorije)),nameKategorije);
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
    private ArrayList<String> unpack(String s,String regeks){
        String [] strings = s.split(regeks);
        return new ArrayList<String>(Arrays.asList(strings));
    }
}
