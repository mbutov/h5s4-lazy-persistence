package org.hslp.hibernate4;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Maxim Butov
 */
@Entity
public class PersistentObject {

    @Id
    private String id;

    public PersistentObject(String id) {
        this.id = id;
    }

}
