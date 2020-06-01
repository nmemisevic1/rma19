package ba.unsa.etf.rma.fragmenti;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.RankingAdapter;
import ba.unsa.etf.rma.aktivnosti.IgrajKvizAkt;
import ba.unsa.etf.rma.aktivnosti.KvizoviAkt;
import ba.unsa.etf.rma.baze.SpiralaBazaOpenHelper;
import ba.unsa.etf.rma.klase.RangIgraca;
import ba.unsa.etf.rma.web.DodajIPovuciRankingOnline;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RangLista.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RangLista#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RangLista extends Fragment implements DodajIPovuciRankingOnline.onDodajIPovuciRankingOnlineDone {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static RankingAdapter rankingAdapter;
    public static ArrayList<RangIgraca> ranking;
    public static ListView lvRanking;
    public static Context context;
    public SpiralaBazaOpenHelper helper;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RangLista() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RangLista.
     */
    // TODO: Rename and change types and number of parameters
    public static RangLista newInstance(String param1, String param2) {
        RangLista fragment = new RangLista();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View gv = inflater.inflate(R.layout.fragment_rang_lista,null);
        ranking = new ArrayList<>();
        helper = new SpiralaBazaOpenHelper(getActivity());
        lvRanking = (ListView)gv.findViewById(R.id.lvRanking);
        RangIgraca rangic = new RangIgraca("0",IgrajKvizAkt.nazivIgraca,IgrajKvizAkt.proce);
        if(KvizoviAkt.jelImaInternetaBozanstvenog)
        new DodajIPovuciRankingOnline(getActivity(),IgrajKvizAkt.nazivKviza,rangic,lvRanking,(DodajIPovuciRankingOnline.onDodajIPovuciRankingOnlineDone)getActivity()).execute("");

        else{popuniRangLokalnimPodacima(rangic);

        }
        return gv;/*inflater.inflate(R.layout.fragment_rang_lista, container, false);*/
    }

    private void popuniRangLokalnimPodacima(RangIgraca rangic) {
        helper.dodajOffRang(rangic,IgrajKvizAkt.nazivKviza);
        ArrayList<RangIgraca> sviRangovi = new ArrayList<>();
        ArrayList<RangIgraca> offRangovi = helper.dajOffRangliste();
        ArrayList<RangIgraca> onlRangovi = helper.dajOnlRangListe();
        sviRangovi.addAll(offRangovi);
        sviRangovi.addAll(onlRangovi);

        ArrayList<RangIgraca> rangListaTrenutnogKviza = new ArrayList<>();
        for(int i=0;i<sviRangovi.size();i++){
            if(sviRangovi.get(i).getNazivKviza().equals(IgrajKvizAkt.nazivKviza))rangListaTrenutnogKviza.add(sviRangovi.get(i));
        }
        ArrayList<RangIgraca> sortiraniRanking = new ArrayList<>();
        while(rangListaTrenutnogKviza.size()!=0){
            int max = 0;
            for(int i=0;i<rangListaTrenutnogKviza.size();i++){
                if(Integer.parseInt(rangListaTrenutnogKviza.get(max).getProcenatTacnihOdgovora().substring(0,rangListaTrenutnogKviza.get(max).getProcenatTacnihOdgovora().length()-1))<Integer.parseInt(rangListaTrenutnogKviza.get(i).getProcenatTacnihOdgovora().substring(0,rangListaTrenutnogKviza.get(i).getProcenatTacnihOdgovora().length()-1)))
                    max = i;
            }
            sortiraniRanking.add(rangListaTrenutnogKviza.get(max));
            rangListaTrenutnogKviza.remove(max);
        }
        ranking.clear();
        ranking = new ArrayList<>();
        ranking.addAll(sortiraniRanking);
        ranking.add(0,new RangIgraca("#","Ime igraca","Procenat tacnih odgovora"));
        for(int i=1;i<ranking.size();i++){
            ranking.get(i).setRedniBroj(String.valueOf(i));
        }
        lvRanking.setAdapter(new RankingAdapter(getContext(),ranking,0));
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ranking = new ArrayList<>();
        String nazivIgraca = IgrajKvizAkt.nazivIgraca;
        String nazivKviza = IgrajKvizAkt.nazivKviza;
        String procenatTacnih = IgrajKvizAkt.proce;
        RangIgraca rangic = new RangIgraca("0",nazivIgraca,procenatTacnih);
        //((IgrajKvizAkt)getActivity()).ucitajRankingSaInterneta(rangic);
        context = getContext();
        //new DodajIPovuciRankingOnline(context,nazivKviza,rangic,(DodajIPovuciRankingOnline.onDodajIPovuciRankingOnlineDone)context);
        //lvRanking = (ListView)getView().findViewById(R.id.lvRanking);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDIPRankingDone(ArrayList<RangIgraca> ranking) {
        /*rankingAdapter = new RankingAdapter(getActivity(),ranking,0);
        rankingAdapter.notifyDataSetChanged();
        lvRanking.setAdapter(rankingAdapter);*/
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=e
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
