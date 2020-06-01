package ba.unsa.etf.rma;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class DodatneFunkcije {
    /*public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }*/
    public static void refreshTextColor(final Context context,final EditText etNaziv){
        etNaziv.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                etNaziv.setTextColor(-16777216);
                etNaziv.setHintTextColor(context.getResources().getColor(R.color.hintGray));
            }
        });
    }
    public static void higlightTextColor(final Context context,final EditText etNaziv){
        if(etNaziv.isEnabled()){
            etNaziv.setTextColor(-65536);
        }
        else{
            etNaziv.setTextColor(context.getResources().getColor(R.color.hintRed));
        }
        etNaziv.setHintTextColor(context.getResources().getColor(R.color.hintRed));
    }
    public static void upozorenje(final Context context,String naslovPoruke,String poruka){
        new AlertDialog.Builder(context)
                .setTitle(naslovPoruke)
                .setMessage(poruka)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Uredu", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    };
    public static void rankingAlert(final Context context,String naslovPoruke){
        naslovPoruke = "ranking";
        new AlertDialog.Builder(context).setTitle(naslovPoruke).setMessage("Unesite svoje ime:")
                .setPositiveButton("Registruj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setIcon(android.R.drawable.ic_input_add)
                .show();
    }
    public static void alertDialogBuilder(final Context context, String poruka){
        new AlertDialog.Builder(context).setTitle("Poruka").setMessage(poruka).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setIcon(android.R.drawable.ic_dialog_alert).show();
    }
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
