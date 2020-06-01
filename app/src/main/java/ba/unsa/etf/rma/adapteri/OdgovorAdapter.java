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
import java.util.stream.Collectors;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Pitanje;

public class OdgovorAdapter extends ArrayAdapter<OdgovorItem> {
    private Context mContext;
    private String tacanOdgovor;
    private ArrayList<String> odgovori;
    private int flag = 0;

    private static ArrayList<OdgovorItem> convertToOdgovori(List<String> odgovoriStringovi) {
        ArrayList<OdgovorItem> res = new ArrayList<>();
        for (String s : odgovoriStringovi) {
            res.add(new OdgovorItem(s));
        }
        return res;
    }

    public OdgovorAdapter(@NonNull Context context, @SuppressLint("SupportAnnotationUsage") @LayoutRes ArrayList<String> list) {
        super(context,0, convertToOdgovori(list));
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.odgovor_liste, parent, false);
        }
        OdgovorItem odgovor = getItem(position);

        ImageView imageView = (ImageView)listItem.findViewById(R.id.Kruzic);
        TextView textView = (TextView) listItem.findViewById(R.id.Odgovor);

        if(flag==0){
            if(odgovor.getOdgovor().equals(tacanOdgovor)){
                imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_tacan_odgovor_icon));
                textView.setText(odgovor.getOdgovor());
                textView.setTextColor(Color.parseColor("#badd8d"));
            }
            else{
                imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_odgovor_icon));
                textView.setText(odgovor.getOdgovor());
                textView.setTextColor(Color.parseColor("#8dacdd"));
            }
        }
        else{
            imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_odgovor_icon));
            textView.setText(odgovor.getOdgovor());
            textView.setTextColor(Color.parseColor("#000000"));

            if ( odgovor.isHighlighted() && odgovor.getOdgovor().equals(this.tacanOdgovor)) {
                listItem.setBackgroundColor(getContext().getResources().getColor(R.color.zelena));
            }
            else if(odgovor.isHighlighted() && !odgovor.getOdgovor().equals(this.tacanOdgovor)){
                listItem.setBackgroundColor(getContext().getResources().getColor(R.color.crvena));
            }
            else {
                listItem.setBackgroundColor(getContext().getResources().getColor(R.color.dirtyWhite));
            }
        }

        return listItem;
    }

    public void setHighlightItem(int position, boolean highlight) {
        getItem(position).setHighlighted(highlight);
    }

    public void setTacanOdgovor(String tacanOdgovor) {
        this.tacanOdgovor = tacanOdgovor;
    }
    public String getTacanOdgovor(){
        return this.tacanOdgovor;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
