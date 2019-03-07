package xyz.cbateman.realmdemo.util;

import android.app.Activity;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import xyz.cbateman.realmdemo.util.dialog.MessageOKDialog;

@SuppressWarnings("unused")
public class ViewUtil {

    private ViewUtil() {}

    public static LayoutAnimationController getCascadeAnimation() {
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(50);
        set.addAnimation(animation);

        animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
        );
        animation.setDuration(100);
        set.addAnimation(animation);

        return new LayoutAnimationController(set, 0.5f);
    }

    /**
     * Show a message in a modal dialog (with OK button).
     *
     * @param activity FragmentActivity object
     * @param stringId resource id of string
     */
    public static void showMessage(FragmentActivity activity, int stringId) {
        String msg = activity.getResources().getString(stringId);
        DialogFragment newFragment = MessageOKDialog.newInstance(msg);
        newFragment.show(activity.getSupportFragmentManager(), null);
    }

    /**
     * Show a formatted message in a modal dialog (with OK button).
     *
     * @param activity FragmentActivity object
     * @param stringId resource id of string (with one placeholder)
     * @param s string added to stringId string
     */
    public static void showMessage(FragmentActivity activity, int stringId, String s) {
        String msg = String.format(activity.getResources().getString(stringId), s);
        DialogFragment newFragment = MessageOKDialog.newInstance(msg);
        newFragment.show(activity.getSupportFragmentManager(), null);
    }

    /**
     * Show a formatted toast message.
     *
     * @param activity Activity object
     * @param stringId resource id of string (with one placeholder)
     * @param s string added to stringId string
     */
    public static void showToast(Activity activity, int stringId, String s) {
        String text = String.format(activity.getResources().getString(stringId), s);
        Toast toast = Toast.makeText(activity, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Show a toast message.
     *
     * @param activity Activity object
     * @param stringId resource id of string
     */
    public static void showToast(Activity activity, int stringId) {
        String text = activity.getResources().getString(stringId);
        Toast toast = Toast.makeText(activity, text, Toast.LENGTH_SHORT);
        toast.show();
    }
}
