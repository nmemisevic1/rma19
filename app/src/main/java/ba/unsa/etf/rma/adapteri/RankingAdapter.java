package ba.unsa.etf.rma.adapteri;

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

import com.maltaisn.icondialog.IconView;

import java.util.ArrayList;
import java.util.List;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.klase.Pitanje;
import ba.unsa.etf.rma.klase.RangIgraca;

public class RankingAdapter extends ArrayAdapter<RangIgraca> {
    public Context mContext;
    public ArrayList<RangIgraca> ranking = new ArrayList<>();
    private int lv;

    public RankingAdapter(@NonNull Context context,@SuppressLint("SupportAnnotationUsage") @LayoutRes ArrayList<RangIgraca> list, int lv){
        super(context,0,list);
        this.mContext = context;
        this.ranking = list;
        this.lv = lv;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.element_rang_igraca, parent, false);
        }
        RangIgraca rangIgraca = ranking.get(position);
        TextView infRedniBroj = (TextView)listItem.findViewById(R.id.infRedniBroj);
        TextView infNazivIgraca = (TextView)listItem.findViewById(R.id.infNazivIgraca);
        TextView infProcenatTacnih = (TextView)listItem.findViewById(R.id.infProcenatTacnih);

        infRedniBroj.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
        infNazivIgraca.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
        infProcenatTacnih.setTextColor(mContext.getResources().getColor(R.color.colorBlack));


        infRedniBroj.setText(rangIgraca.getRedniBroj());
        infNazivIgraca.setText(rangIgraca.getImeIgraca());
        infProcenatTacnih.setText(rangIgraca.getProcenatTacnihOdgovora());

        //IF NAZIV IGRACA == TO I TO ožeži po boji then
        return listItem;
    }
}
