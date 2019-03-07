package xyz.cbateman.realmdemo.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Contact is a Realm object for storing contact information.
 */
@SuppressWarnings("unused")
public class Contact extends RealmObject {

    @PrimaryKey
    private long id;
    @Required
    private String name = null;
    private String email = null;
    private String phone = null;

    // Constructors --------------------------------------------------------------------------------

    public Contact() {
    }

    // Public Methods ------------------------------------------------------------------------------

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void copyData(Contact contact) {
        if (contact != null) {
            this.id = contact.getId();
            this.name = contact.getName();
            this.email = contact.getEmail();
            this.phone = contact.getPhone();
        }
    }
}

