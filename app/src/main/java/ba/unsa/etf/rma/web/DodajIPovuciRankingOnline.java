package ba.unsa.etf.rma.web;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.GridView;
import android.widget.ListView;

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
import java.util.Collections;
import java.util.Map;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.RankingAdapter;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.klase.RangIgraca;

public class DodajIPovuciRankingOnline extends AsyncTask<String,Void,Void> {
    Activity context;
    ListView mListView;
    private ArrayList<RangIgraca> ranking;
    private String nazivKviza;
    onDodajIPovuciRankingOnlineDone pozivatelj;
    private int kolikoIhIma;
    private RangIgraca rankingIgraca;

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mListView.setAdapter(new RankingAdapter(context,ranking,0));
        pozivatelj.onDIPRankingDone(this.ranking);
    }

    public interface onDodajIPovuciRankingOnlineDone{
        public void onDIPRankingDone(ArrayList<RangIgraca> ranking);
    }
    public DodajIPovuciRankingOnline(Activity context,String nazivKviza,RangIgraca rankingIgraca,ListView lvRanking,onDodajIPovuciRankingOnlineDone p){
        this.ranking = new ArrayList<>();
        this.context = context;
        this.pozivatelj = p;
        this.nazivKviza = nazivKviza;
        this.kolikoIhIma = 0;
        this.rankingIgraca = rankingIgraca;
        this.mListView = lvRanking;
    }
    protected void dajSveIgrace(){
        GoogleCredential credentials;
        try{

            InputStream tajnaStream = context.getResources().openRawResource(R.raw.secret);
            credentials = GoogleCredential.fromStream(tajnaStream).createScoped(Lists.newArrayList(Collections.singleton("https://www.googleapis.com/auth/datastore")));

            credentials.refreshToken();
            String TOKEN = credentials.getAccessToken();

            Log.d("TOKEN",TOKEN);
            String adresa = "https://firestore.googleapis.com/v1/projects/rma19memisevic10/databases/(default)/documents/" + "Rangliste" + "?access_token=";
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
                    if(naziv.getString("stringValue").equals(nazivKviza)){
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
                        this.ranking.add(rangIgraca);
                    }
                }
                this.ranking.add(this.rankingIgraca);
            }
        }catch(IOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected Void doInBackground(String... strings) {
        dajSveIgrace();
        dodajNovogIgraca();
        sortirajRanking();
        return null;
    }

    private void sortirajRanking() {
        ArrayList<RangIgraca> sortiraniRanking = new ArrayList<>();
        while(this.ranking.size()!=0){
            int max = 0;
            for(int i=0;i<this.ranking.size();i++){
                if(Integer.parseInt(ranking.get(max).getProcenatTacnihOdgovora().substring(0,ranking.get(max).getProcenatTacnihOdgovora().length()-1))<Integer.parseInt(ranking.get(i).getProcenatTacnihOdgovora().substring(0,ranking.get(i).getProcenatTacnihOdgovora().length()-1)))
                    max = i;
            }
            sortiraniRanking.add(ranking.get(max));
            ranking.remove(max);
        }
        ranking.clear();
        ranking = new ArrayList<>();
        ranking.addAll(sortiraniRanking);
        ranking.add(0,new RangIgraca("#","Ime igraca","Procenat tacnih odgovora"));
        for(int i=1;i<ranking.size();i++){
            ranking.get(i).setRedniBroj(String.valueOf(i));
        }
    }

    protected void dodajNovogIgraca(){
        GoogleCredential credentials;
        try{
            InputStream tajnaStream = context.getResources().openRawResource(R.raw.secret);
            credentials = GoogleCredential.fromStream(tajnaStream).createScoped(Lists.newArrayList(Collections.singleton("https://www.googleapis.com/auth/datastore")));

            credentials.refreshToken();
            String TOKEN = credentials.getAccessToken();

            Log.d("TOKEN",TOKEN);


                String url = "https://firestore.googleapis.com/v1/projects/rma19memisevic10/databases/(default)/documents/Rangliste?access_token=";
                URL urlObj = new URL(url+ URLEncoder.encode(TOKEN,"UTF-8"));

                HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type","application/json");
                conn.setRequestProperty("Accept","application/json");

                String rang = "{ \"fields\": { \"lista\" : { \"mapValue\": { \"fields\": {\n" +
                        "              \"mapa\": {\n" +
                        "                \"mapValue\": {\n" +
                        "                  \"fields\": {\n" +
                        "                    \"imeIgraca\": {\n" +
                        "                      \"stringValue\": \""+ rankingIgraca.getImeIgraca() +"\"\n" +
                        "                    },\n" +
                        "                    \"procenatTacnih\": {\n" +
                        "                      \"stringValue\": \""+ rankingIgraca.getProcenatTacnihOdgovora() + "\"\n" +
                        "                    }\n" +
                        "                  }\n" +
                        "                }\n" +
                        "              },\n" +
                        "              \"pozicija\": {\n" +
                        "                \"integerValue\": \"" + String.valueOf(kolikoIhIma) +"\"\n" +
                        "              }\n" +
                        "            }\n" +
                        "          }\n" +
                        "        },\n" +
                        "        \"nazivKviza\": {\n" +
                        "          \"stringValue\": \"" + nazivKviza + "\"\n" +
                        "        }}}";

                try(OutputStream os = conn.getOutputStream()){
                    byte[] input = rang.getBytes("utf-8");
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
        }catch(IOException e){
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
