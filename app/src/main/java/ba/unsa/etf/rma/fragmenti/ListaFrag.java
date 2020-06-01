package ba.unsa.etf.rma.fragmenti;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.aktivnosti.KvizoviAkt;
import ba.unsa.etf.rma.klase.Kategorija;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Pitanje;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListaFrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListaFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListaFrag extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ArrayList<Kviz> kvizovi = new ArrayList<Kviz>();
    public static ArrayList<Kategorija> kategorije = new ArrayList<Kategorija>();
    public ArrayList<Pitanje> neDodijeljenaPitanja = new ArrayList<>();
    public static ArrayList<String> kategorijice = new ArrayList<>();
    public ArrayAdapter<String> kategorijeAdapter;
    public ListView listaKategorija;

    private OnFragmentInteractionListener mListener;
    public static void setKategorije(){
        kategorije = KvizoviAkt.kategorije;
        kategorijice = KvizoviAkt.kategorijice;
    }
    public ListaFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListaFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static ListaFrag newInstance(String param1, String param2) {
        ListaFrag fragment = new ListaFrag();
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
        View rootView = inflater.inflate(R.layout.fragment_lista,container,false);
        // Inflate the layout for this fragment
        /*String[] fav = {"haha","ahahah","hahahah"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,fav);
        ListView listView = (ListView)rootView.findViewById(R.id.listaKategorija);
        listView.setAdapter(adapter);*/
        //String[] fav = {"haha","ahahah","hahahah"};
        //ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,fav);
        //ListView listView = (ListView)rootView.findViewById(R.id.listaKategorija);
        //listView.setAdapter(adapter);

        return rootView;

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        if(getArguments().containsKey("kategorije")) {
            kategorije = getArguments().getParcelableArrayList("kategorije");
        }
        if(getArguments().containsKey("kvizovi")) {
            kvizovi = getArguments().getParcelableArrayList("kvizovi");
        }
        if(getArguments().containsKey("nedodijeljenaPitanja")){
            neDodijeljenaPitanja = getArguments().getParcelableArrayList("nedodijeljenaPitanja");
        }
        kategorije = KvizoviAkt.kategorije;
        kategorijice = new ArrayList<>();

        for(int i=0;i<kategorije.size();i++){kategorijice.add(kategorije.get(i).getNaziv()); }
        kategorijeAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,kategorijice);

        listaKategorija = (ListView)getView().findViewById(R.id.listaKategorija);
        listaKategorija.setAdapter(kategorijeAdapter);
        kategorijeAdapter.notifyDataSetChanged();

        listaKategorija.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DetailFrag.refreshGridView(kategorijice.get(position));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        kategorijeAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,kategorijice);
        listaKategorija.setAdapter(kategorijeAdapter);
        kategorijeAdapter.notifyDataSetChanged();
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
