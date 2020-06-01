package ba.unsa.etf.rma.adapteri;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.maltaisn.icondialog.IconHelper;
import com.maltaisn.icondialog.IconView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;

/**
 * Created by deepakr on 3/29/2016.
 */
public class KvizGridBaseAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private List<Kviz> beanList;
    private LayoutInflater inflater;

    private List<Kviz> mStringFilterList;
    ValueFilter valueFilter;

    public void updateAdapter(ArrayList<Kviz> kvizovi){
        mStringFilterList = kvizovi;
        notifyDataSetChanged();
    }

    public List<Kviz> getmStringFilterList(){
        return mStringFilterList;
    }

    public KvizGridBaseAdapter(Context context, List<Kviz> beanList) {
        this.context = context;
        this.beanList = beanList;
        this.mStringFilterList = beanList;
    }

    @Override
    public int getCount() {
        return beanList.size();
    }

    @Override
    public Kviz getItem(int i) {
        return beanList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (view == null) {
            view = inflater.inflate(R.layout.element_grid_liste, null);
        }

        TextView txtName = (TextView) view.findViewById(R.id.Itemname);
        final IconView ikonica = (IconView) view.findViewById(R.id.Ikonica);
        TextView brojPitanja = (TextView)view.findViewById(R.id.brojPitanja);

        txtName.setTextColor(Color.parseColor("#222222"));
        Kviz kviz = new Kviz();
        kviz = beanList.get(i);
        ikonica.setColorFilter(Color.parseColor("#222222"));
        txtName.setText(kviz.getNaziv());
        if(kviz.getNaziv().equals("Dodaj kviz"))brojPitanja.setText("");
        else brojPitanja.setText(String.valueOf(kviz.getPitanja().size()));
        final IconHelper iconHelper = IconHelper.getInstance(getContext());
        final Kviz finalKviz = kviz;
        iconHelper.addLoadCallback(new IconHelper.LoadCallback() {
            @Override
            public void onDataLoaded() {
                if (finalKviz.getNaziv().equals("Dodaj kviz")) {
                    ikonica.setIcon(671);
                    ikonica.isShown();
                } else {
                    ikonica.setIcon(Integer.parseInt(finalKviz.getKategorija().getId()));
                }
            }
        });
        if (kviz.getNaziv().equals("Dodaj kviz")) {
            ikonica.setIcon(671);
            ikonica.isShown();
        } else {
            ikonica.setIcon(Integer.parseInt(kviz.getKategorija().getId()));
        }
        //notifyDataSetChanged();
        return view;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    public Context getContext() {
        return context;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                ArrayList<Kviz> filterList = new ArrayList<Kviz>();
                Kategorija kat=new Kategorija("Svi","0");
                for (int i = 0; i < mStringFilterList.size(); i++) {
                    if (mStringFilterList.get(i).getKategorija().getNaziv().equals(constraint.toString()) || constraint.toString().equals("Svi") && !mStringFilterList.get(i).getNaziv().equals("Dodaj kviz")){
                        kat = mStringFilterList.get(i).getKategorija();
                        Kviz bean = new Kviz(mStringFilterList.get(i).getNaziv(),mStringFilterList.get(i).getKategorija(), mStringFilterList.get(i).getPitanja());
                        filterList.add(bean);
                    }
                    if(mStringFilterList.get(i).getNaziv().equals("Dodaj kviz") && kat!=null){
                        Kviz bean = new Kviz(mStringFilterList.get(i).getNaziv(),kat, mStringFilterList.get(i).getPitanja());
                        filterList.add(bean);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mStringFilterList.size();
                results.values = mStringFilterList;
            }
            //notifyDataSetChanged();
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            beanList = (ArrayList<Kviz>) results.values;
            notifyDataSetChanged();
        }
    }
}