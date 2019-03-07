package xyz.cbateman.realmdemo;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import xyz.cbateman.realmdemo.adapter.ContactsAdapter;
import xyz.cbateman.realmdemo.adapter.ContactsAdapter.ContactAction;
import xyz.cbateman.realmdemo.model.Contact;
import xyz.cbateman.realmdemo.storage.StorageWrapper;
import xyz.cbateman.realmdemo.storage.StorageWrapper.StorageAction;
import xyz.cbateman.realmdemo.util.Constants;
import xyz.cbateman.realmdemo.util.ViewUtil;
import xyz.cbateman.realmdemo.util.dialog.ConfirmDialog;

/**
 * MainActivity displays a list of contacts. You can also create, edit and delete
 * contacts from this screen.
 */
public class MainActivity extends AppCompatActivity implements
        ContactsAdapter.ClickListener,
        ConfirmDialog.ConfirmDialogListener,
        StorageWrapper.StorageWrapperCallback,
        StorageWrapper.StorageWrapperChangeCallback {

    public static final String TAG = Constants.TAG;

    private StorageWrapper storageWrapper;

    private ContactsAdapter mContactsAdapter;

    private RecyclerView recyclerView;
    private ImageView addAContactImageView;
    private TextView addAContactTextView;

    // MainActivity life cycle methods -------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storageWrapper = new StorageWrapper();

        Log.i(TAG, "MainActivity onCreate");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mContactsAdapter =
                new ContactsAdapter(this, storageWrapper.getAllContacts(this));
        mContactsAdapter.setClickListener(this);
        recyclerView.setAdapter(mContactsAdapter);

        // Add divider for each row of recyclerview
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(recyclerView.getContext(),
                        layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Add an animation when displaying recyclerview
        recyclerView.setLayoutAnimation(ViewUtil.getCascadeAnimation());

        addAContactImageView = findViewById(R.id.add_a_contact_image);
        addAContactTextView = findViewById(R.id.add_a_contact_text);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> createContact());
    }

    @Override
    protected void onDestroy() {
        storageWrapper.close();
        super.onDestroy();
    }

    // ContactsAdapter.ClickListener ---------------------------------------------------------------

    @Override
    public void onItemClicked(int position, ContactAction action) {
        if (mContactsAdapter.getData() != null) {
            Contact contact = mContactsAdapter.getData().get(position);
            switch (action) {
                case EDIT:
                    editContact(contact);
                    break;
                case DELETE:
                    deleteContact(contact);
                    break;
            }
        }
    }

    // ConfirmDialog.ConfirmDialogListener ---------------------------------------------------------

    public void onConfirmDialogPositiveClick(ConfirmDialog dialog) {
        String tag = dialog.getTag();
        if ((tag != null) && tag.equals(Constants.CONTACT_DELETE)) {
            Bundle bundle = dialog.getArguments();
            if (bundle != null) {
                storageWrapper.deleteContact(
                        bundle.getLong(Constants.CONTACT_BUNDLE_ID), this);
            }
        }
    }

    public void onConfirmDialogNegativeClick(ConfirmDialog dialog) {
        // Do nothing
    }

    // StorageWrapper.StorageWrapperCallback -------------------------------------------------------

    public void onSuccess(StorageAction action, Object data) {
        Log.i(TAG, "MainActivity onSuccess");
        // Do nothing - recyclerview gets updated automatically
    }

    public void onError(Throwable error) {
        Log.i(TAG, "MainActivity onError error = " + error.getMessage());
    }

    // StorageWrapper.StorageWrapperChangeCallback -------------------------------------------------

    /**
     * Called when the contact list updates. This should be called automatically after adding,
     * updating or deleting a contact.
     */
    public void onChange() {
        int size = 0;
        if (mContactsAdapter.getData() != null) {
            size = mContactsAdapter.getData().size();
        }
        if (size == 0) {
            recyclerView.setVisibility(View.GONE);
            addAContactImageView.setVisibility(View.VISIBLE);
            addAContactTextView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            addAContactImageView.setVisibility(View.GONE);
            addAContactTextView.setVisibility(View.GONE);
        }
    }

    // Private Methods -----------------------------------------------------------------------------

    /**
     * Create a contact. The EditActivity does the actual work.
     */
    private void createContact() {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(Constants.CONTACT_ACTION, Constants.CONTACT_CREATE);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }

    /**
     * Edit one of the contacts from list. The EditActivity does the actual work.
     *
     * @param contact the Contact object
     */
    private void editContact(Contact contact) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(Constants.CONTACT_ID, contact.getId());
        intent.putExtra(Constants.CONTACT_ACTION, Constants.CONTACT_EDIT);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }

    /**
     * Delete one of the contacts from list. The dialog listener does the actual work.
     *
     * @param contact the Contact object
     */
    private void deleteContact(Contact contact) {
        String text = String.format(
                getResources().getString(R.string.delete_confirm_string), contact.getName());
        DialogFragment fragment = ConfirmDialog.newInstance(text);
        Bundle bundle = fragment.getArguments();
        if (bundle != null) {
            bundle.putLong(Constants.CONTACT_BUNDLE_ID, contact.getId());
        }
        fragment.show(this.getSupportFragmentManager(), Constants.CONTACT_DELETE);
    }
}
