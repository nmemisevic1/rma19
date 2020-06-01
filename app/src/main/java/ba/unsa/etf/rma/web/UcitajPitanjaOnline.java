package ba.unsa.etf.rma.web;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Spinner;
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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.baze.SpiralaBazaOpenHelper;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Pitanje;

public class UcitajPitanjaOnline extends AsyncTask<String, Void, Void> {
    private Context context;
    private String kolekcija;
    public ArrayList<Pitanje> pitanja;
    private OnUcitajPitanjaOnlineDone pozivatelj;
    private SpiralaBazaOpenHelper helper;

    public UcitajPitanjaOnline(Context context,String kolekcija,OnUcitajPitanjaOnlineDone p){
        this.context = context;
        this.kolekcija = kolekcija;
        this.pozivatelj = p;
        this.helper = new SpiralaBazaOpenHelper(context);
    }
    public interface OnUcitajPitanjaOnlineDone{
        public void onDone(ArrayList<Pitanje> pitanjaOnline);
    }
    @Override
    protected void onPostExecute(Void aVoid){
        super.onPostExecute(aVoid);
        if(pitanja.size()!=0){
            //Toast.makeText(context,"Moguca pitanja učitana iz baze podataka!",Toast.LENGTH_SHORT).show();
        }
        else{
            //Toast.makeText(context,"Nema pitanja u bazi podataka ili je došlo do greške!",Toast.LENGTH_SHORT).show();
        }
        helper.close();
        pozivatelj.onDone(this.pitanja);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Toast.makeText(context,"Ucitavam pitanja iz baze",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Void doInBackground(String... strings) {
        pitanja = new ArrayList<>();
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

            JSONObject jo = new JSONObject(rezultat);
            if(jo.length()!=0){
                JSONArray dokumenti =  jo.getJSONArray("documents");
                //this.jsonRezultat = "";

                for(int i=0;i<dokumenti.length();i++){
                    JSONObject dokument = dokumenti.getJSONObject(i);

                    String namePitanja = dokument.getString("name");
                    ArrayList<String> bbrbw = unpack(namePitanja,"/");
                    namePitanja = bbrbw.get(bbrbw.size()-1);

                    JSONObject fields = dokument.getJSONObject("fields");
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

                    this.pitanja.add(new Pitanje(nazivPitanja,nazivPitanja,odgovoriPitanja,tacanOdgovor));
                    helper.dodajPitanje(new Pitanje(nazivPitanja,nazivPitanja,odgovoriPitanja,tacanOdgovor),namePitanja);
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("KOLIKO IMA PITANJA: ",String.valueOf(pitanja.size()));
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
