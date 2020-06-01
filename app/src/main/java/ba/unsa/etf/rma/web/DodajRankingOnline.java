package ba.unsa.etf.rma.web;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.util.Lists;

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

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.baze.SpiralaBazaOpenHelper;
import ba.unsa.etf.rma.klase.RangIgraca;

public class DodajRankingOnline extends AsyncTask<String,Void,Void> {
    private Context context;
    private SpiralaBazaOpenHelper helper;
    public onDodajRankingOnline pozivatelj;
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        helper.close();
        pozivatelj.onDodajRankingDone();
    }

    public interface onDodajRankingOnline{
        public void onDodajRankingDone();
    }
    public DodajRankingOnline(Context context,onDodajRankingOnline p){
        this.pozivatelj = p;
        this.context = context;
        this.helper = new SpiralaBazaOpenHelper(context);
    }

    @Override
    protected Void doInBackground(String... strings) {
        dodajOfflineIgrace();
        return null;
    }
    protected void dodajOfflineIgrace(){
        ArrayList<RangIgraca> offRangliste = helper.dajOffRangliste();
        ArrayList<RangIgraca> onlRangListe = helper.dajOnlRangListe();
        int brojac = onlRangListe.size();
        for(int i=0;i<offRangliste.size();i++){
            dodajNovogIgraca(offRangliste.get(i),brojac++,offRangliste.get(i).getNazivKviza());
        }
        helper.onDeleteOffRangliste();

    }
    protected void dodajNovogIgraca(RangIgraca rankingIgraca,int kolikoIhIma,String nazivKviza){
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
