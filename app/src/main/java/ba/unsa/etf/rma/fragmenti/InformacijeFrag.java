package ba.unsa.etf.rma.fragmenti;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.aktivnosti.IgrajKvizAkt;
import ba.unsa.etf.rma.aktivnosti.KvizoviAkt;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InformacijeFrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InformacijeFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InformacijeFrag extends Fragment {
    public static TextView nazivKviza;
    public static TextView brojTacnih;
    public static TextView brojPreostalih;
    public static TextView procenatTacnih;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public InformacijeFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InformacijeFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static InformacijeFrag newInstance(String param1, String param2) {
        InformacijeFrag fragment = new InformacijeFrag();
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
        return inflater.inflate(R.layout.fragment_informacije, container, false);
    }
    public static void refreshInformations(){
        nazivKviza.setText(IgrajKvizAkt.odabraniKviz.getNaziv());
        brojTacnih.setText(String.valueOf(IgrajKvizAkt.brojTacnih));
        brojPreostalih.setText(String.valueOf(IgrajKvizAkt.odabraniKviz.getPitanja().size()-IgrajKvizAkt.brojOdgovorenih));
        String procenat = String.valueOf(100*IgrajKvizAkt.brojTacnih/IgrajKvizAkt.brojOdgovorenih);
        procenat += "%";
        procenatTacnih.setText(procenat);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        nazivKviza = (TextView)getView().findViewById(R.id.infNazivKviza);
        brojTacnih = (TextView)getView().findViewById(R.id.infBrojTacnihPitanja);
        brojPreostalih = (TextView)getView().findViewById(R.id.infBrojPreostalihPitanja);
        procenatTacnih = (TextView)getView().findViewById(R.id.infProcenatTacni);

        nazivKviza.setText(IgrajKvizAkt.odabraniKviz.getNaziv());
        brojTacnih.setText(String.valueOf(IgrajKvizAkt.brojOdgovorenih));
        brojPreostalih.setText(String.valueOf(IgrajKvizAkt.odabraniKviz.getPitanja().size()-IgrajKvizAkt.brojOdgovorenih));
        String procenat = String.valueOf(IgrajKvizAkt.procenatTacnih);
        procenat += "%";
        procenatTacnih.setText(procenat);

        Button btnKraj = (Button)getView().findViewById(R.id.btnKraj);
        btnKraj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("kategorije",IgrajKvizAkt.kategorije);
                bundle.putParcelableArrayList("kvizovi",IgrajKvizAkt.kvizovi);
                bundle.putParcelableArrayList("nedodijeljenaPitanja",IgrajKvizAkt.neDodijeljenaPitanja);

                Intent intent = new Intent(getActivity(), KvizoviAkt.class);
                intent.putExtras(bundle);
                startActivity(intent);
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
