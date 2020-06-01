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
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.maltaisn.icondialog.Icon;
import com.maltaisn.icondialog.IconView;

import java.util.ArrayList;
import java.util.List;

import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;

public class KvizAdapter extends ArrayAdapter<Kviz> implements Filterable{
    private Context mContext;
    private List<Kviz> listaKvizova = new ArrayList<>();

    public KvizAdapter(@NonNull Context context, ArrayList<Kviz> list){
        super(context,0,list);
        mContext = context;
        listaKvizova = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView,@NonNull ViewGroup parent){
        View listItem = convertView;
        if(listItem == null){
            listItem = LayoutInflater.from(mContext).inflate(R.layout.element_liste,parent,false);
        }
        try {
            Kviz kviz = new Kviz();
            kviz = listaKvizova.get(position);
            Integer kategorijaKviza = Integer.parseInt(kviz.getKategorija().getId());
            IconView iconView = (IconView) listItem.findViewById(R.id.Ikonica);
            TextView textView = (TextView) listItem.findViewById(R.id.Itemname);
            textView.setText(kviz.getNaziv());
            if (kviz.getNaziv().equals("Dodaj kviz")) {
                iconView.setIcon(671);
            } else {
                iconView.setIcon(kategorijaKviza);
            }
        }
        catch(Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }

        return listItem;
    }
}
