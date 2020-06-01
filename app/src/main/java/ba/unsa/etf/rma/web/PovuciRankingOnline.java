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
import ba.unsa.etf.rma.adapteri.RankingAdapter;
import ba.unsa.etf.rma.baze.SpiralaBazaOpenHelper;
import ba.unsa.etf.rma.klase.RangIgraca;

public class PovuciRankingOnline extends AsyncTask<String,Void,Void> {
    private Context context;
    private int kolikoIhIma;
    private SpiralaBazaOpenHelper helper;
    private onPovuciRankingOnline pozivatelj;
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        helper.close();
        pozivatelj.onPovuciRankingDone();
    }

    public interface onPovuciRankingOnline{
        public void onPovuciRankingDone();
    }
    @Override
    protected Void doInBackground(String... strings) {
        povuciRanking();
        return null;
    }
    public PovuciRankingOnline(Context context, onPovuciRankingOnline p){
        this.context = context;
        this.kolikoIhIma = 0;
        this.helper = new SpiralaBazaOpenHelper(context);
        this.pozivatelj = p;
    }
    protected void povuciRanking(){
        GoogleCredential credentials;
        try{

            InputStream tajnaStream = context.getResources().openRawResource(R.raw.secret);
            credentials = GoogleCredential.fromStream(tajnaStream).createScoped(Lists.newArrayList(Collections.singleton("https://www.googleapis.com/auth/datastore")));

            credentials.refreshToken();
            String TOKEN = credentials.getAccessToken();

            Log.d("TOKEN",TOKEN);
            String adresa = "https://firestore.googleapis.com/v1/projects/rma19memisevic10/databases/(default)/documents/" + "Rangliste" + "?pageSize=1000&access_token=";
            URL urlA = new URL(adresa + URLEncoder.encode(TOKEN,"UTF-8"));
            HttpURLConnection urlGetConnection = (HttpURLConnection)urlA.openConnection();
            InputStream in = new BufferedInputStream(urlGetConnection.getInputStream());
            String rezultat = convertStreamToString(in);

            JSONObject jo = new JSONObject(rezultat);
            if(jo.length()!=0){
                JSONArray dokumenti =  jo.getJSONArray("documents");
                //this.jsonRezultat = "";

                for(int i=0;i<dokumenti.length();i++){
                    String nazivIgraca="";String procenatTacnih="";
                    int pozicijaIgraca=0;
                    JSONObject dokument = dokumenti.getJSONObject(i);
                    Log.e("Objekat", dokument.toString());//radid
                    JSONObject fields = dokument.getJSONObject("fields");
                    JSONObject naziv = fields.getJSONObject("nazivKviza");
                    this.kolikoIhIma++;
                    JSONObject lista = fields.getJSONObject("lista");
                    JSONObject mapValue = lista.getJSONObject("mapValue");
                    JSONObject mapValueFields = mapValue.getJSONObject("fields");
                    JSONObject mapa = mapValueFields.getJSONObject("mapa");
                    JSONObject mapara = mapa.getJSONObject("mapValue");
                    JSONObject maparaFields = mapara.getJSONObject("fields");
                    JSONObject joImeIgraca = maparaFields.getJSONObject("imeIgraca");
                    nazivIgraca = joImeIgraca.getString("stringValue");
                    JSONObject joProcenatTacnih = maparaFields.getJSONObject("procenatTacnih");
                    procenatTacnih = joProcenatTacnih.getString("stringValue");
                    JSONObject pozicija = mapValueFields.getJSONObject("pozicija");
                    pozicijaIgraca = pozicija.getInt("integerValue");
                    RangIgraca rangIgraca = new RangIgraca(String.valueOf(pozicijaIgraca),nazivIgraca,procenatTacnih);
                    helper.dodajOnlRang(rangIgraca,naziv.getString("stringValue"));
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("KOLIKO IMA RANKINGA: ",String.valueOf(kolikoIhIma));
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
