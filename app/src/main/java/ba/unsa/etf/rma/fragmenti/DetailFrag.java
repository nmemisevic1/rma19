package ba.unsa.etf.rma.fragmenti;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.KvizBaseAdapter;
import ba.unsa.etf.rma.adapteri.KvizGridBaseAdapter;
import ba.unsa.etf.rma.aktivnosti.DodajKvizAkt;
import ba.unsa.etf.rma.aktivnosti.IgrajKvizAkt;
import ba.unsa.etf.rma.aktivnosti.KvizoviAkt;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFrag extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static ArrayList<Kviz> kvizovi = new ArrayList<Kviz>();
    public ArrayList<Kategorija> kategorije = new ArrayList<Kategorija>();
    public ArrayList<Pitanje> neDodijeljenaPitanja = new ArrayList<>();


    public static KvizGridBaseAdapter kvizBaseAdapter;
    public static GridView gridKvizovi;

    private OnFragmentInteractionListener mListener;

    public static void refreshGridView(String constraint){
        kvizBaseAdapter.getFilter().filter(constraint);
        gridKvizovi.setAdapter(kvizBaseAdapter);
        kvizBaseAdapter.updateAdapter(kvizovi);
        kvizBaseAdapter.notifyDataSetChanged();
        //listaKvizova.invalidateViews();
    }
    public DetailFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFrag newInstance(String param1, String param2) {
        DetailFrag fragment = new DetailFrag();
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
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        kvizovi = new ArrayList<>();
        kategorije = new ArrayList<>();
        neDodijeljenaPitanja = new ArrayList<>();
        if(getArguments().containsKey("kategorije")) {
            kategorije = getArguments().getParcelableArrayList("kategorije");
        }
        if(getArguments().containsKey("kvizovi")) {
            kvizovi = getArguments().getParcelableArrayList("kvizovi");
        }
        if(getArguments().containsKey("nedodijeljenaPitanja")){
            neDodijeljenaPitanja = getArguments().getParcelableArrayList("nedodijeljenaPitanja");
        }
        gridKvizovi = (GridView) getView().findViewById(R.id.gridKvizovi);
        /*
        ArrayList<String> kategorijice = new ArrayList<>();

        for(int i=0;i<kategorije.size();i++){kategorijice.add(kategorije.get(i).getNaziv()); }
        ArrayAdapter<String> kategorijeAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,kategorijice);

        ListView listaKategorija = (ListView)getView().findViewById(R.id.listaKategorija);
        listaKategorija.setAdapter(kategorijeAdapter);
        kategorijeAdapter.notifyDataSetChanged();*/
        kvizBaseAdapter = new KvizGridBaseAdapter(getContext(),kvizovi);
        gridKvizovi.setAdapter(kvizBaseAdapter);
        kvizBaseAdapter.notifyDataSetChanged();
        gridKvizovi.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("kategorije",kategorije);
                bundle.putParcelableArrayList("kvizovi",kvizovi);
                bundle.putParcelableArrayList("nedodijeljenaPitanja",neDodijeljenaPitanja);
                List<Kviz> lista = kvizBaseAdapter.getmStringFilterList();
                int indeks=0;
                for (int i = 0; i < kvizovi.size(); i++) {
                    if(kvizovi.get(i).getNaziv().equals(kvizBaseAdapter.getItem(position).getNaziv())){
                        indeks = i;
                    }
                }
                bundle.putParcelable("odabraniKviz",kvizovi.get(indeks));
                bundle.putInt("pozicijaOdabranogKviza",indeks);
                Intent intent = new Intent(getActivity(),DodajKvizAkt.class);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            }
        });
        gridKvizovi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!kvizBaseAdapter.getItem(position).getNaziv().equals("Dodaj kviz")){
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("kategorije",kategorije);
                    bundle.putParcelableArrayList("nedodijeljenaPitanja",neDodijeljenaPitanja);
                    bundle.putParcelableArrayList("kvizovi",kvizovi);
                    int indeks=0;
                    for (int i = 0; i < kvizovi.size(); i++) {
                        if(kvizovi.get(i).getNaziv().equals(kvizBaseAdapter.getItem(position).getNaziv())){
                            indeks = i;
                        }
                    }
                    bundle.putParcelable("odabraniKviz",kvizovi.get(indeks));
                    bundle.putInt("pozicijaOdabranogKviza",indeks);
                    Intent intent = new Intent(getActivity(),IgrajKvizAkt.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    /*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
