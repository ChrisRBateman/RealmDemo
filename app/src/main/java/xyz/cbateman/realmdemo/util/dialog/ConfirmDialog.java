package xyz.cbateman.realmdemo.util.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import xyz.cbateman.realmdemo.R;

@SuppressWarnings("ConstantConditions")
public class ConfirmDialog extends DialogFragment {

    private static final String MESSAGE = "message";

    public interface ConfirmDialogListener {
        void onConfirmDialogPositiveClick(ConfirmDialog dialog);
        void onConfirmDialogNegativeClick(ConfirmDialog dialog);
    }

    private ConfirmDialogListener mListener;

    public ConfirmDialog() {
    }

    public static ConfirmDialog newInstance(String message) {
        ConfirmDialog f = new ConfirmDialog();
        Bundle args = new Bundle();
        args.putString(MESSAGE, message);
        f.setArguments(args);
        f.setCancelable(false);

        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Fragment fragment = this.getTargetFragment();
        try {
            if (fragment != null) {
                mListener = (ConfirmDialogListener)fragment;
            }
            else {
                mListener = (ConfirmDialogListener)context;
            }
        }
        catch (ClassCastException e) {
            throw new ClassCastException((fragment != null) ? fragment.toString() : context.toString() +
                    " must implement ConfirmDialogListener");
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        Resources res = getResources();
        String message = getArguments().getString(MESSAGE);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(res.getString(R.string.yes_text), (dialog, which) -> {
            if (mListener != null) {
                mListener.onConfirmDialogPositiveClick(ConfirmDialog.this);
            }
        });
        alertDialog.setNegativeButton(res.getString(R.string.no_text), (dialog, which) -> {
            if (mListener != null) {
                mListener.onConfirmDialogNegativeClick(ConfirmDialog.this);
            }
            dialog.cancel();
        });

        return alertDialog.create();
    }
}
