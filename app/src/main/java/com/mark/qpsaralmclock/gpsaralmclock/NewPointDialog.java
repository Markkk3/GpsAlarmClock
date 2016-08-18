package com.mark.qpsaralmclock.gpsaralmclock;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;


public class NewPointDialog extends DialogFragment implements DialogInterface.OnClickListener {
    private View form=null;
    MainActivity main;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        form= getActivity().getLayoutInflater()
                .inflate(R.layout.newpoint_dialog, null);
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        return(builder.setView(form).setTitle("Введите имя точки")
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, null).create());

    }

    public static interface OnCompleteListener {
        public abstract void onComplete(String name);
    }

    private OnCompleteListener mListener;

    // make sure the Activity implemented it
    public void onAttach(Activity activity) {
        try {
            this.mListener = (OnCompleteListener)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
        super.onAttach(activity);
    }


    public void onClick(DialogInterface dialog, int which) {

        EditText edname=(EditText)form.findViewById(R.id.editText);
        edname.setFocusable(true);
        String name = edname.getText().toString();
        mListener.onComplete(name);


    }
    @Override
    public void onDismiss(DialogInterface unused) {
        super.onDismiss(unused);
    }
    @Override
    public void onCancel(DialogInterface unused) {
        super.onCancel(unused);
    }


}
