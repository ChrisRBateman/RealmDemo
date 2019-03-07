package xyz.cbateman.realmdemo.storage;

import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;

import xyz.cbateman.realmdemo.model.Contact;

/**
 * Wrapper class for all storage operations on Contact objects.
 */
public class StorageWrapper {

    public enum StorageAction {
        ADD,
        UPDATE,
        DELETE,
        GET
    }

    public interface StorageWrapperCallback {
        void onSuccess(StorageAction action, Object data);
        void onError(Throwable error);
    }

    public interface StorageWrapperChangeCallback {
        void onChange();
    }

    private Realm realm;
    private RealmResults<Contact> result;

    /**
     * Create a StorageWrapper object.
     */
    public StorageWrapper() {
        realm = Realm.getDefaultInstance();
    }

    /**
     * Close StorageWrapper.
     */
    public void close() {
        if (result != null) {
            result.removeAllChangeListeners();
            result = null;
        }
        if (realm != null) {
            realm.close();
            realm = null;
        }
    }

    /**
     * Get all contacts from storage.
     *
     * @param callback the StorageWrapperChangeCallback object to receive results
     * @return list of RealmResults<Contact>
     */
    public RealmResults<Contact> getAllContacts(StorageWrapperChangeCallback callback) {
        Objects.requireNonNull(callback);

        result = realm.where(Contact.class).findAllAsync();
        result.addChangeListener(contacts -> callback.onChange());
        return result;
    }

    /**
     * Add a new contact to storage. Results are reported to a callback object.
     *
     * @param contact the Contact object to add
     * @param callback the StorageWrapperCallback object to receive results
     */
    public void addContact(Contact contact, StorageWrapperCallback callback) {
        Objects.requireNonNull(contact);
        Objects.requireNonNull(callback);

        realm.executeTransactionAsync(bgRealm -> {
            Number maxId = bgRealm.where(Contact.class).max("id");
            long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
            contact.setId(nextId);
            bgRealm.copyToRealm(contact);
        }, () -> callback.onSuccess(StorageAction.ADD, null), callback::onError);
    }

    /**
     * Update contact to storage. Results are reported to a callback object.
     *
     * @param contact the Contact object to update
     * @param callback the StorageWrapperCallback object to receive results
     */
    public void updateContact(Contact contact, StorageWrapperCallback callback) {
        Objects.requireNonNull(contact);
        Objects.requireNonNull(callback);

        realm.executeTransactionAsync(bgRealm ->
                bgRealm.copyToRealmOrUpdate(contact),
                () -> callback.onSuccess(StorageAction.UPDATE, null), callback::onError);
    }

    /**
     * Get a contact from storage. Results are reported to a callback object.
     *
     * @param id the Contact id
     * @param callback the StorageWrapperCallback object to receive results
     */
    public void getContact(long id, StorageWrapperCallback callback) {
        Objects.requireNonNull(callback);

        final Contact contact = new Contact();
        realm.executeTransactionAsync(bgRealm -> {
            RealmResults<Contact> result =
                    bgRealm.where(Contact.class).equalTo("id", id).findAll();
            contact.copyData(result.get(0));
        }, () -> callback.onSuccess(StorageAction.GET, contact), callback::onError);
    }

    /**
     * Delete a contact from storage. Results are reported to a callback object.
     *
     * @param id the Contact id
     * @param callback the StorageWrapperCallback object to receive results
     */
    public void deleteContact(long id, StorageWrapperCallback callback) {
        Objects.requireNonNull(callback);

        realm.executeTransactionAsync(bgRealm -> {
            RealmResults<Contact> result =
                    bgRealm.where(Contact.class).equalTo("id", id).findAll();
            result.deleteAllFromRealm();
        }, () -> callback.onSuccess(StorageAction.DELETE, null), callback::onError);
    }
}
