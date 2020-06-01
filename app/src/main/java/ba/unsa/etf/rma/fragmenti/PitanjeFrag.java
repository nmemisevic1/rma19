package ba.unsa.etf.rma.fragmenti;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ba.unsa.etf.rma.R;
import ba.unsa.etf.rma.adapteri.OdgovorAdapter;
import ba.unsa.etf.rma.adapteri.OdgovorItem;
import ba.unsa.etf.rma.aktivnosti.DodajKvizAkt;
import ba.unsa.etf.rma.aktivnosti.IgrajKvizAkt;
import ba.unsa.etf.rma.aktivnosti.KvizoviAkt;
import ba.unsa.etf.rma.klase.Pitanje;

import static android.os.SystemClock.sleep;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PitanjeFrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PitanjeFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PitanjeFrag extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Handler mHandler = new Handler();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    public ArrayList<Pitanje> randomPitanja;
    public ArrayList<String> randomOdgovori;
    public int brojOdgovorenih = 0;
    public int brojTacnih = 0;
    public ListView odgovori;
    public OdgovorAdapter odgovoriAdapter;
    public PitanjeFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PitanjeFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static PitanjeFrag newInstance(String param1, String param2) {
        PitanjeFrag fragment = new PitanjeFrag();
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
        return inflater.inflate(R.layout.fragment_pitanje, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceBundle){
        super.onActivityCreated(savedInstanceBundle);

        randomPitanja = new ArrayList<>();
        randomPitanja = IgrajKvizAkt.odabraniKviz.dajRandomPitanja();
        final TextView pitanje = (TextView)getView().findViewById(R.id.tekstPitanja);
        odgovori = (ListView)getView().findViewById(R.id.odgovoriPitanja);


        if(randomPitanja!=null){
            if(randomPitanja.size()==0){
                pitanje.setText("Ovaj kviz nema pitanja!");
            }
            else{
                pitanje.setText(randomPitanja.get(brojOdgovorenih).getNaziv());
                randomOdgovori = randomPitanja.get(brojOdgovorenih).dajRandomOdgovore();
                odgovoriAdapter = new OdgovorAdapter(getContext(),randomOdgovori);
                odgovoriAdapter.setTacanOdgovor(randomPitanja.get(brojOdgovorenih).getTacan());
                odgovoriAdapter.setFlag(1);
                odgovori.setAdapter(odgovoriAdapter);
                odgovori.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {
                            int childPosition = position - odgovori.getFirstVisiblePosition();

                            int broj = odgovori.getFirstVisiblePosition();
                            int broj2 = odgovori.getLastVisiblePosition();
                            if(odgovori.getChildAt(childPosition).isFocusable()) {
                                odgovori.getChildAt(childPosition).setFocusable(true);
                            }

                            OdgovorItem odgovor = (OdgovorItem)odgovori.getItemAtPosition(position);
                            if((odgovor.getOdgovor().equals(randomPitanja.get(brojOdgovorenih).getTacan()))) {
                                IgrajKvizAkt.brojTacnih++;
                            }

                            for(int i=0;i<odgovoriAdapter.getCount(); i++){
                                OdgovorItem curr = (OdgovorItem)odgovori.getItemAtPosition(i);
                                if(position == i && odgovor.getOdgovor().equals(randomPitanja.get(brojOdgovorenih).getTacan())){
                                    odgovoriAdapter.setHighlightItem(i, true);
                                }
                                if(position == i && !odgovor.getOdgovor().equals(randomPitanja.get(brojOdgovorenih).getTacan())){
                                    odgovoriAdapter.setHighlightItem(i, true);
                                }
                                if(curr.getOdgovor().equals(randomPitanja.get(brojOdgovorenih).getTacan())){
                                    odgovoriAdapter.setHighlightItem(i, true);
                                }
                            }
                            odgovoriAdapter.notifyDataSetChanged();
                            odgovori.invalidate();

                            IgrajKvizAkt.brojOdgovorenih++;
                            InformacijeFrag.refreshInformations();
                            odgovori.setEnabled(false);
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    brojOdgovorenih++;

                                    if(brojOdgovorenih>=randomPitanja.size()){
                                        pitanje.setText("Kviz je završen!");
                                        final EditText nazivIgraca = new EditText(getContext());
                                        new AlertDialog.Builder(getContext()).setTitle("Registrujte se na rang listu!").setMessage("Unesite svoje ime:")
                                              .setPositiveButton("Registruj", new DialogInterface.OnClickListener() {
                                              @Override
                                              public void onClick(DialogInterface dialogInterface, int i) {
                                                  IgrajKvizAkt.nazivIgraca = nazivIgraca.getText().toString();
                                                  IgrajKvizAkt.nazivKviza = InformacijeFrag.nazivKviza.getText().toString();
                                                  IgrajKvizAkt.proce = InformacijeFrag.procenatTacnih.getText().toString();
                                                  RangLista rangLista = new RangLista();
                                                  FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                                  transaction.replace(R.id.pitanjePlace,rangLista);
                                                  transaction.addToBackStack(null);
                                                  transaction.commit();
                                              }
                                              })
                                             .setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                                              @Override
                                              public void onClick(DialogInterface dialogInterface, int i) {

                                              }
                                              }).setIcon(android.R.drawable.ic_input_add).setView(nazivIgraca)
                                              .show();
                                        //}
                                        ArrayList<String> praznaLista = new ArrayList<>();
                                        ArrayAdapter<String> prazniOdgovoriAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,praznaLista);
                                        IgrajKvizAkt.brojTacnih=0;
                                        IgrajKvizAkt.brojOdgovorenih=0;
                                        IgrajKvizAkt.procenatTacnih=100;

                                        odgovori.setAdapter(prazniOdgovoriAdapter);
                                        prazniOdgovoriAdapter.notifyDataSetChanged();
                                    }
                                    else{
                                        pitanje.setText(randomPitanja.get(brojOdgovorenih).getNaziv());
                                        randomOdgovori = randomPitanja.get(brojOdgovorenih).dajRandomOdgovore();
                                        odgovoriAdapter = new OdgovorAdapter(getContext(),randomOdgovori);
                                        odgovoriAdapter.setFlag(1);
                                        odgovoriAdapter.setTacanOdgovor(randomPitanja.get(brojOdgovorenih).getTacan());
                                        odgovori.setAdapter(odgovoriAdapter);
                                        odgovoriAdapter.notifyDataSetChanged();
                                        odgovori.refreshDrawableState();
                                        odgovori.invalidateViews();

                                    }
                                    odgovori.setEnabled(true);
                                }
                            },2000);
                            //AKO JE TACAN ODGOVOR

                        /*catch(Exception e){
                            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        }*/

                    }
                });
            }
        }
        else{
            pitanje.setText("Ovaj kviz nema pitanja!");
        }
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
