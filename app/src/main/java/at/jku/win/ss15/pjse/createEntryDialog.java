package at.jku.win.ss15.pjse;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class createEntryDialog extends DialogFragment implements AdapterView.OnItemSelectedListener {

    Spinner frequencySpn;
    EditText inputTime;
    EditText inputDate;

    EditText inputAmount;
    EditText inputRepetition;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Der Layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.activity_create_entry_dialog, null));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //Diese Zeile besagt wohin die Daten geschickt werden sollen, dass muss noch gemacht werden!
                Intent nextScreen = new Intent(getActivity(), DisplayMessageActivity.class);

                inputAmount = (EditText) ((AlertDialog) dialog).findViewById(R.id.amount_editText);
                inputRepetition = (EditText) ((AlertDialog) dialog).findViewById(R.id.repetition_editText);
                inputTime = (EditText) ((AlertDialog) dialog).findViewById(R.id.time_editText);
                inputDate = (EditText) ((AlertDialog) dialog).findViewById(R.id.date_editText);
                /*
                frequencySpn = (Spinner) ((AlertDialog) dialog).findViewById(R.id.frequency_spinner);

                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(((AlertDialog) dialog).getContext(),
                        R.array.frequency, android.R.layout.simple_spinner_item);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                frequencySpn.setAdapter(adapter);
                */

                Double amount = Double.parseDouble(inputAmount.getText().toString());
                int rep = Integer.parseInt(inputRepetition.getText().toString());
                String time = inputTime.getText().toString();
                String date = inputDate.getText().toString();

                Bundle b = new Bundle();
                b.putDouble("amount", amount);
                b.putInt("rep", rep);
                b.putString("time", time);
                b.putString("date", date);

                nextScreen.putExtras(b);

                startActivity(nextScreen);
            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        createEntryDialog.this.getDialog().cancel();
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


