package xyz.cbateman.realmdemo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

import xyz.cbateman.realmdemo.R;
import xyz.cbateman.realmdemo.model.Contact;
import xyz.cbateman.realmdemo.util.Constants;

public class ContactsAdapter extends RealmRecyclerViewAdapter<Contact, ContactsAdapter.ViewHolder> {

    private static final String TAG = Constants.TAG;

    public enum ContactAction {
        EDIT,
        DELETE,
        NONE
    }

    public interface ClickListener {
        void onItemClicked(int position, ContactAction action);
    }

    private ClickListener mClickListener;
    private Bitmap contactBitmap;

    static class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, View.OnLongClickListener {

        private final ImageView contactImageView;
        private final TextView nameTextView;
        private final ImageView deleteImageView;
        private final ImageView editImageView;
        private ContactsAdapter adapter;

        ViewHolder(View v, ContactsAdapter adapter) {
            super(v);

            contactImageView = v.findViewById(R.id.image);
            nameTextView = v.findViewById(R.id.name);
            deleteImageView = v.findViewById(R.id.delete);
            editImageView = v.findViewById(R.id.edit);

            this.adapter = adapter;

            deleteImageView.setOnClickListener(this);
            deleteImageView.setOnLongClickListener(this);
            editImageView.setOnClickListener(this);
            editImageView.setOnLongClickListener(this);
        }

        ImageView getContactImageView() {
            return contactImageView;
        }

        TextView getNameTextView() {
            return nameTextView;
        }

        @Override
        public void onClick(View v) {
            if (adapter != null) {
                adapter.onItemClicked(getLayoutPosition(), getContactActionForView(v));
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return (adapter != null) &&
                    adapter.onItemLongClicked(getLayoutPosition(), getContactActionForView(v));
        }

        /**
         * Returns ContactAction for view.
         *
         * @param v the View object
         * @return a ContactAction
         */
        private ContactAction getContactActionForView(View v) {
            ContactAction action = ContactAction.NONE;
            switch (v.getId()) {
                case R.id.edit:
                    action = ContactAction.EDIT;
                    break;
                case R.id.delete:
                    action = ContactAction.DELETE;
                    break;
            }

            return action;
        }
    }

    // Constructors --------------------------------------------------------------------------------

    /**
     * ContactsAdapter constructor.
     *
     * @param context the Context object
     * @param contacts list of RealmResults<Contact> objects
     */
    public ContactsAdapter(Context context, RealmResults<Contact> contacts) {
        super(contacts, true, true);
        contactBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.user_profile);

        Log.i(TAG, "ContactsAdapter constructed");
    }

    // Public Methods ------------------------------------------------------------------------------

    /**
     * Set the ClickListener.
     *
     * @param listener the ClickListener object
     */
    public void setClickListener(ClickListener listener) {
        mClickListener = listener;
    }

    /**
     * Bind product data to the item view.
     *
     * @param holder holds item view
     * @param position product position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = getItem(position);
        if (contact != null) {
            holder.getContactImageView().setImageBitmap(contactBitmap);
            holder.getNameTextView().setText(contact.getName());
        }
    }

    /**
     * Returns a ContactsAdapter.ViewHolder object.
     *
     * @param parent the ViewGroup object
     * @param viewType the view type
     * @return a ContactsAdapter.ViewHolder object
     */
    @Override
    @NonNull
    public ContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_item, parent, false);

        return new ViewHolder(v, this);
    }

    // Private Methods -----------------------------------------------------------------------------

    /**
     * Responds to item click.
     *
     * @param position the position of item clicked
     * @param action the ContactAction to take
     */
    private void onItemClicked(int position, ContactAction action) {
        if (mClickListener != null) {
            mClickListener.onItemClicked(position, action);
        }
    }

    /**
     * Returns true if item is long clicked.
     *
     * @param position the position of item
     * @param action the ContactAction to take
     * @return always returns true
     */
    @SuppressWarnings("unused")
    private boolean onItemLongClicked(int position, ContactAction action) {
        return true;
    }
}
