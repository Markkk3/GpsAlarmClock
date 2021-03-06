package com.mark.gpsalarmclock;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import com.mark.qpsalarmclock.R;


public class NewPointDialog extends DialogFragment implements DialogInterface.OnClickListener {
    private View form=null;
    MainActivity main;
    private AlertDialog dialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        form= getActivity().getLayoutInflater()
                .inflate(R.layout.newpoint_dialog, null);
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());


        View view = getActivity().getLayoutInflater().inflate(R.layout.title, null);
        dialog = (builder.setView(form)

                .setTitle("Введите имя точки")
                .setCustomTitle(view)
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, null).create());


        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
                                      @Override
                                      public void onShow(DialogInterface dialogInterface) {
                                          dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.dialogButton));
                                             dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.dialogButton));
                                      }
                                  });
/*
        int textViewId = dialog.getContext().getResources().getIdentifier("android:id/alertTitle", null, null);
        TextView tv = (TextView) dialog.findViewById(textViewId);
        tv.setTextColor(getResources().getColor(R.color.white));
        */
        return dialog;

    }
    /*
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onShow(DialogInterface arg0) {
    //    dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(getContext().getResources().getColor(R.color.white));
     //   dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getContext().getResources().getColor(R.color.white));
        //dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.colorPrimary);
    }
*/

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
