package ba.unsa.etf.rma.servisi;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class AlarmService extends Service {
    private Context mContext;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public AlarmService(Context mContext) {
        this.mContext = mContext;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // do your jobs here
        Toast.makeText(mContext,"Pocelo igranje kviza",Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
    }
}