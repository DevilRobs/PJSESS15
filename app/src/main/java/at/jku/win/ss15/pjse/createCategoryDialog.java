package at.jku.win.ss15.pjse;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;


public class createCategoryDialog extends DialogFragment implements AdapterView.OnItemSelectedListener{


    EditText inputAmount;
    EditText inputName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Der Layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.activity_create_category_dialog, null));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //Diese Zeile besagt wohin die Daten geschickt werden sollen, dass muss noch gemacht werden!
                Intent nextScreen = new Intent(getActivity(), DisplayMessageActivity.class);


                inputAmount = (EditText) ((AlertDialog) dialog).findViewById(R.id.amount_editText);
                inputName = (EditText) ((AlertDialog) dialog).findViewById(R.id.time_editText);

                /*
                frequencySpn = (Spinner) ((AlertDialog) dialog).findViewById(R.id.frequency_spinner);

                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(((AlertDialog) dialog).getContext(),
                        R.array.frequency, android.R.layout.simple_spinner_item);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                frequencySpn.setAdapter(adapter);
                */

                Double amount = Double.parseDouble(inputAmount.getText().toString());
                String name = inputName.getText().toString();


                Bundle b = new Bundle();
                b.putDouble("amount", amount);
                b.putString("name", name);

                nextScreen.putExtras(b);

                startActivity(nextScreen);
            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        createCategoryDialog.this.getDialog().cancel();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void onCancel(DialogInterface dialog){
        super.onCancel(dialog);
        Toast.makeText(getActivity(), "The creation has been cancelled", Toast.LENGTH_SHORT).show();
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        parent.getItemAtPosition(pos);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}

/*
Daten aus diesem Dialog können folgenderweise entnommen werden:
        Intent intent = getIntent();
        // Hier wird der Bundle aus dem intent rausgeholt
        Bundle b = intent.getExtras();
        //Und anschließend die ganzen Werte
        double d = b.getDouble("amount");
        int rep = b.getInt("rep");
        String s1 = b.getString("time");
        String s2 = b.getString("date");

        //Um den Inhalt anzuzeigen
        String message = s2 + s1 + d + rep;
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);
        //"Es in die Abteilung layout schicken"
        setContentView(textView);
 */