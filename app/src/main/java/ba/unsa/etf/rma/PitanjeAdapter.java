package ba.unsa.etf.rma;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.maltaisn.icondialog.Icon;
import com.maltaisn.icondialog.IconView;

import java.util.ArrayList;
import java.util.List;

import ba.unsa.etf.rma.klase.Pitanje;

public class PitanjeAdapter extends ArrayAdapter<Pitanje>{
    private Context mContext;
    private List<Pitanje> listaPitanja = new ArrayList<>();
    private int lv;
    public PitanjeAdapter(@NonNull Context context, @SuppressLint("SupportAnnotationUsage") @LayoutRes ArrayList<Pitanje> list,int lv){
        super(context,0,list);
        mContext = context;
        listaPitanja = list;
        this.lv = lv;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView,@NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.element_liste, parent, false);
        }
        Pitanje pitanje = listaPitanja.get(position);

        IconView iconView = (IconView) listItem.findViewById(R.id.Ikonica);
        TextView textView = (TextView) listItem.findViewById(R.id.Itemname);

        if(lv==1){
            iconView.setIcon(671);
            textView.setText(pitanje.getNaziv());
        }
        else if(pitanje.getNaziv().equals("Dodaj pitanje")||pitanje.getNaziv().equals("Dodaj kviz") && lv == 0){
            iconView.setIcon(671);
            textView.setText(pitanje.getNaziv());
        }
        else{
            iconView.setIcon(902);
            textView.setText(pitanje.getNaziv());
        }
        return listItem;
    }
}
