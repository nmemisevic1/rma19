package ba.unsa.etf.rma.aktivnosti;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.provider.AlarmClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.fragmenti.DetailFrag;
import ba.unsa.etf.rma.fragmenti.InformacijeFrag;
import ba.unsa.etf.rma.fragmenti.ListaFrag;
import ba.unsa.etf.rma.fragmenti.PitanjeFrag;
import ba.unsa.etf.rma.fragmenti.RangLista;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.klase.RangIgraca;
import ba.unsa.etf.rma.receiveri.AlarmManagerBroadcastReceiver;
import ba.unsa.etf.rma.receiveri.AlarmReceiver;
import ba.unsa.etf.rma.web.DodajIPovuciRankingOnline;

public class IgrajKvizAkt extends AppCompatActivity implements DodajIPovuciRankingOnline.onDodajIPovuciRankingOnlineDone{

    private AlarmManagerBroadcastReceiver alarm;
    public static ArrayList<Kviz> kvizovi = new ArrayList<Kviz>(){{add(new Kviz("Dodaj kviz"));}};
    public static ArrayList<Kategorija> kategorije = new ArrayList<Kategorija>(){{add(0,new Kategorija("Svi","22"));}};
    public static ArrayList<Pitanje> neDodijeljenaPitanja = new ArrayList<>();
    public static Kviz odabraniKviz = new Kviz();
    public static int brojOdgovorenih = 0;
    public static float procenatTacnih = 100;
    public static int brojTacnih = 0;
    public static String nazivIgraca;
    public static String nazivKviza;
    public static String proce;
    public AlarmManager alarmManager;
    public PendingIntent pendingIntent;
    public static void zamijeniFragmente(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_igraj_kviz_akt);

        alarm = new AlarmManagerBroadcastReceiver();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ArrayList<Kategorija> kat = extras.getParcelableArrayList("kategorije");
            ArrayList<Kviz> kviz = extras.getParcelableArrayList("kvizovi");
            ArrayList<Pitanje> pit = extras.getParcelableArrayList("nedodijeljenaPitanja");

            if(kat!=null){
                for(int i=0;i<kategorije.size();i++){
                    kategorije.remove(i);
                    i--;
                }
                kategorije.addAll(kat);
            }
            if(kviz!=null){
                for(int i=0;i<kvizovi.size();i++){
                    kvizovi.remove(i);
                    i--;
                }
                kvizovi.addAll(kviz);
            }
            if(pit!=null){
                for(int i=0;i<neDodijeljenaPitanja.size();i++){
                    neDodijeljenaPitanja.remove(i);
                    i--;
                }
                neDodijeljenaPitanja.addAll(pit);
            }
            odabraniKviz= extras.getParcelable("odabraniKviz");
        }
        Configuration config = getResources().getConfiguration();

        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        PitanjeFrag pf = new PitanjeFrag();
        Bundle pfBundle = new Bundle();
        pfBundle.putParcelableArrayList("kategorije",kategorije);
        pfBundle.putParcelableArrayList("nedodijeljenaPitanja",neDodijeljenaPitanja);
        pfBundle.putParcelableArrayList("kvizovi",kvizovi);
        InformacijeFrag iif = new InformacijeFrag();

        Bundle ifBundle = new Bundle();
        ifBundle.putParcelableArrayList("kvizovi",kvizovi);
        ifBundle.putParcelableArrayList("kategorije",kategorije);
        ifBundle.putParcelableArrayList("nedodijeljenaPitanja",neDodijeljenaPitanja);

        pf.setArguments(pfBundle);
        iif.setArguments(ifBundle);

        ft.replace(R.id.informacijePlace,iif);
        ft.replace(R.id.pitanjePlace,pf);
        ft.commit();

        //alarm.SetAlarm(this.getApplicationContext());
        //alarm.setAlarm(this.getApplicationContext());

        long ONE_MINUTE_IN_MILLIS = 60000;//millisecs
        Calendar date = Calendar.getInstance();
        long t = date.getTimeInMillis();
        Date afterAddingMins = new Date(t + (odabraniKviz.getPitanja().size() * ONE_MINUTE_IN_MILLIS));
        Calendar vrijemeIzvrsenjaAlarma = Calendar.getInstance();

        vrijemeIzvrsenjaAlarma.set(
                afterAddingMins.getYear(),
                afterAddingMins.getMonth(),
                afterAddingMins.getDate(),
                afterAddingMins.getHours(),
                afterAddingMins.getMinutes()
        );
        if(odabraniKviz.getPitanja().size()!=0)setAlarm(odabraniKviz.getPitanja().size());
            //setAlarm1(vrijemeIzvrsenjaAlarma.getTimeInMillis());
    }

    private void setAlarm(int i) {
        if(i%2!=0)i++;
        i/=2;

        int sati = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE) + i;

        if(minute > 59) {
            sati += minute / 60;
            minute = minute % 60;
        }

        if(sati > 23) {
            sati = sati % 24;
        }
        Intent alarmIntent = new Intent(AlarmClock.ACTION_SET_ALARM);
        alarmIntent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        alarmIntent.putExtra(AlarmClock.EXTRA_HOUR, sati);
        alarmIntent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
        alarmIntent.putExtra(AlarmClock.EXTRA_MESSAGE, "Vrijeme isteklo");
        try {
            startActivity(alarmIntent);
        } catch (android.content.ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void ucitajRankingSaInterneta(RangIgraca rangic){

    }
    public void onetimeTimer(View view){
        Context context = this.getApplicationContext();
        if(alarm != null){
            alarm.setOnetimeTimer(context);
        }else{
            Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onDIPRankingDone(ArrayList<RangIgraca> ranking) {
        //RangLista.osvjeziRangListu(ranking);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //alarmManager.cancel(pendingIntent);
    }
}
