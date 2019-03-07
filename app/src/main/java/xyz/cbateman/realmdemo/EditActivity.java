package xyz.cbateman.realmdemo;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

import xyz.cbateman.realmdemo.model.Contact;
import xyz.cbateman.realmdemo.storage.StorageWrapper;
import xyz.cbateman.realmdemo.storage.StorageWrapper.StorageAction;
import xyz.cbateman.realmdemo.util.Constants;
import xyz.cbateman.realmdemo.util.ViewUtil;
import xyz.cbateman.realmdemo.util.dialog.ConfirmDialog;

public class EditActivity extends AppCompatActivity implements
        TextWatcher,
        ConfirmDialog.ConfirmDialogListener,
        StorageWrapper.StorageWrapperCallback {

    public static final String TAG = Constants.TAG;

    private TextView titleTextView;
    private AppCompatEditText nameEditText;
    private AppCompatEditText phoneEditText;
    private AppCompatEditText emailEditText;

    private StorageWrapper storageWrapper;

    private boolean isModified = false;
    private StorageAction actionMode = StorageAction.UPDATE;
    private long contactId = -1;

    // EditActivity life cycle methods -------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        storageWrapper = new StorageWrapper();

        Log.i(TAG, "EditActivity onCreate");

        titleTextView = findViewById(R.id.edit_contacts_header);
        nameEditText = findViewById(R.id.name_edit);
        phoneEditText = findViewById(R.id.phone_edit);
        emailEditText = findViewById(R.id.email_edit);

        Button saveButton = findViewById(R.id.save_button);
        Button cancelButton = findViewById(R.id.cancel_button);

        saveButton.setOnClickListener(v -> handleSave());
        cancelButton.setOnClickListener(v -> handleCancel());

        Intent intent = getIntent();
        String contactAction = intent.getStringExtra(Constants.CONTACT_ACTION);

        switch (contactAction) {
            case Constants.CONTACT_CREATE:
                createContactAction();
                break;
            case Constants.CONTACT_EDIT:
                long id = intent.getLongExtra(Constants.CONTACT_ID, 0);
                editContactAction(id);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        storageWrapper.close();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        checkForModifications();
    }

    // TextWatcher ---------------------------------------------------------------------------------

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Do nothing
    }

    @Override
    public void afterTextChanged(Editable s) {
        isModified = true;
    }

    // ConfirmDialog.ConfirmDialogListener ---------------------------------------------------------

    public void onConfirmDialogPositiveClick(ConfirmDialog dialog) {
        exitActivity();
    }

    public void onConfirmDialogNegativeClick(ConfirmDialog dialog) {
        // Do nothing
    }

    // StorageWrapper.StorageWrapperCallback -------------------------------------------------------

    /**
     * This is called if GET, ADD or UPDATE are successful.
     * For GET we just fill form fields. For ADD and UPDATE we
     * exit the activity.
     *
     * @param action the current storage action
     * @param data data associated with current action
     */
    public void onSuccess(StorageAction action, Object data) {
        if ((action == StorageAction.GET) && (data instanceof Contact)) {
            // Set the form fields
            Contact contact = (Contact)data;
            String name = contact.getName();
            nameEditText.setText((name != null) ? name : "");

            String phone = contact.getPhone();
            phoneEditText.setText((phone != null) ? phone : "");

            String email = contact.getEmail();
            emailEditText.setText((email != null) ? email : "");

            // Add listeners after setting form fields
            nameEditText.addTextChangedListener(this);
            phoneEditText.addTextChangedListener(this);
            emailEditText.addTextChangedListener(this);
        }
        else {
            isModified = false;
            int resId = ((action == StorageAction.ADD) ? R.string.add_contact_text
                    : R.string.update_contact_text);
            ViewUtil.showToast(this, resId);
            exitActivity();
        }
    }

    public void onError(Throwable error) {
        Log.i(TAG, "onError error = " + error.getMessage());
        ViewUtil.showMessage(this, R.string.storage_error_text);
    }

    // Private Methods -----------------------------------------------------------------------------

    /**
     * Setup screen for creating contact.
     */
    private void createContactAction() {
        titleTextView.setText(R.string.create_contact_header_text);

        nameEditText.addTextChangedListener(this);
        phoneEditText.addTextChangedListener(this);
        emailEditText.addTextChangedListener(this);

        actionMode = StorageAction.ADD;
    }

    /**
     * Setup screen for editing the contact.
     * @param id the id of the contact
     */
    private void editContactAction(long id) {
        titleTextView.setText(R.string.edit_contact_header_text);

        actionMode = StorageAction.UPDATE;
        contactId = id;

        storageWrapper.getContact(contactId, this);
    }

    /**
     * Handle Save button pressed.
     */
    private void handleSave() {
        String name = Objects.requireNonNull(nameEditText.getText()).toString().trim();
        String phone = Objects.requireNonNull(phoneEditText.getText()).toString().trim();
        String email = Objects.requireNonNull(emailEditText.getText()).toString().trim();

        if (TextUtils.isEmpty(name)) {
            nameEditText.requestFocus();
            nameEditText.setError(getString(R.string.invalid_name_text));
            return;
        }

        if (TextUtils.isEmpty(phone) || !phone.matches(Constants.NA_PHONE_REGEX)) {
            phoneEditText.requestFocus();
            phoneEditText.setError(getString(R.string.invalid_phone_text));
            return;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.requestFocus();
            emailEditText.setError(getString(R.string.invalid_email_text));
            return;
        }

        Contact contact = new Contact();
        contact.setName(name);
        contact.setPhone(phone);
        contact.setEmail(email);

        switch (actionMode) {
            case ADD:
                storageWrapper.addContact(contact, this);
                break;
            case UPDATE:
                contact.setId(contactId);
                storageWrapper.updateContact(contact, this);
                break;
        }
    }

    /**
     * Handle Cancel button pressed.
     */
    private void handleCancel() {
        checkForModifications();
    }

    /**
     * Check for form modifications and if true display warning.
     */
    private void checkForModifications() {
        if (isModified) {
            String text = getResources().getString(R.string.modified_confirm_string);
            DialogFragment fragment = ConfirmDialog.newInstance(text);
            fragment.show(this.getSupportFragmentManager(), null);
        }
        else {
            exitActivity();
        }
    }

    /**
     * Exit the activity.
     */
    private void exitActivity() {
        finish();
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }
}
