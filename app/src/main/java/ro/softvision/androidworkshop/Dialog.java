package ro.softvision.androidworkshop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

public class Dialog extends DialogFragment {

    Callbacks mCallbacks;

    public static Dialog NewInstance(String positiveText, String negativeText, String message, String title) {
        Dialog dialog = new Dialog();

        Bundle args = new Bundle();
        args.putString(Contract.Dialog.POSITIVE_BUTTON, positiveText);
        args.putString(Contract.Dialog.NEGATIVE_BUTTON, negativeText);
        args.putString(Contract.Dialog.MESSAGE, message);
        args.putString(Contract.Dialog.TITLE, title);
        dialog.setArguments(args);

        return dialog;
    }

    //  Set the button callbacks to the Activity that spawned this Dialog
    //  This was deprecated in Marshmallow
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setCallbacks();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setCallbacks();
    }

    private void setCallbacks() {
        if (getActivity() instanceof Callbacks) {
            mCallbacks = (Callbacks) getActivity();
        }
    }

    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            return new AlertDialog.Builder(getActivity())
                    .setPositiveButton(args.getString(Contract.Dialog.POSITIVE_BUTTON),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (mCallbacks != null) {
                                        mCallbacks.onDialogPositiveClick(Dialog.this);
                                    }
                                }
                            })
                    .setNegativeButton(args.getString(Contract.Dialog.NEGATIVE_BUTTON),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (mCallbacks != null) {
                                        mCallbacks.onDialogNegativeClick(Dialog.this);
                                    }
                                }
                            })
                    .setMessage(args.getString(Contract.Dialog.MESSAGE))
                    .setTitle(args.getString(Contract.Dialog.TITLE))
                    .create();
        }
        //  Whoops
        return new AlertDialog.Builder(getActivity()).create();
    }

    public interface Callbacks {
        //  It's a good idea to be able to identify which dialog was clicked
        void onDialogPositiveClick(Dialog dialog);
        void onDialogNegativeClick(Dialog dialog);
    }
}
