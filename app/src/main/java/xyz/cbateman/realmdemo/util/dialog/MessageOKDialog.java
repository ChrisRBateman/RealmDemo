package xyz.cbateman.realmdemo.util.dialog;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import xyz.cbateman.realmdemo.R;

@SuppressWarnings("ConstantConditions")
public class MessageOKDialog extends DialogFragment {

    private static final String MESSAGE = "message";

    public MessageOKDialog() {
    }
    
    public static MessageOKDialog newInstance(String message) {
        MessageOKDialog f = new MessageOKDialog();
        Bundle args = new Bundle();
        args.putString(MESSAGE, message);
        f.setArguments(args);
        
        return f;
    }
    
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        Resources res = getResources();
        String message = getArguments().getString(MESSAGE);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(res.getString(R.string.ok_text),
                (dialog, which) -> dialog.cancel());
        
        return alertDialog.create();
    }
}