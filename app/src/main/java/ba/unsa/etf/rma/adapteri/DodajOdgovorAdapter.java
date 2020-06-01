package ba.unsa.etf.rma.adapteri;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.maltaisn.icondialog.IconView;

import java.util.ArrayList;
import java.util.List;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Pitanje;

public class DodajOdgovorAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private List<String> listaOdgovora = new ArrayList<>();
    private String tacanOdgovor;

    public DodajOdgovorAdapter(@NonNull Context context, @SuppressLint("SupportAnnotationUsage") @LayoutRes ArrayList<String> list){
        super(context,0,list);
        mContext = context;
        listaOdgovora = list;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.odgovor_liste, parent, false);
        }
        String odgovor = listaOdgovora.get(position);

        ImageView imageView = (ImageView)listItem.findViewById(R.id.Kruzic);
        TextView textView = (TextView) listItem.findViewById(R.id.Odgovor);

        if(odgovor.equals(tacanOdgovor)){
            imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_tacan_odgovor_icon));
            textView.setText(odgovor);
            textView.setTextColor(Color.parseColor("#badd8d"));
        }
        else{
            imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_odgovor_icon));
            textView.setText(odgovor);
            textView.setTextColor(Color.parseColor("#8dacdd"));
        }
        return listItem;
    }

    public void setTacanOdgovor(String tacanOdgovor) {
        this.tacanOdgovor = tacanOdgovor;
    }
    public String getTacanOdgovor(){
        return this.tacanOdgovor;
    }
}