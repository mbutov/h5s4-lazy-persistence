package org.hslp.hibernate4;

import org.hslp.common.AbstractLazyPersistenceTests;
import org.hslp.common.PersistentObject;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;

/**
 * @author Maxim Butov
 */
public abstract class AbstractLazyHibernate4Tests extends AbstractLazyPersistenceTests {

    @Autowired
    protected HibernateTemplate hibernateTemplate;

    @Test(expected = Throwable.class)
    public void testHibernateException() throws Throwable {
        hibernateTemplate.execute(session -> session.createCriteria(PersistentObject.class).list());
    }

    @Test
    public void testEmptySession() throws Throwable {
        hibernateTemplate.execute(session -> null);
    }

    @Test(expected = Throwable.class)
    public void testConnectionBroken() throws Throwable {
        transactionTemplate.execute(status -> hibernateTemplate.save(new PersistentObject(newId())));
    }

    @Test
    public void testConnectionRestored() throws Throwable {
        restoreConnection();
        transactionTemplate.execute(status -> hibernateTemplate.save(new PersistentObject(newId())));
    }

    @Test
    public void testConnectionBrokenThenRestored() throws Throwable {

        String id1 = newId();
        String id2 = newId();

        restoreConnection();
        transactionTemplate.execute(status -> hibernateTemplate.save(new PersistentObject(id1)));

        brakeConnection();
        boolean success = true;
        try {
            transactionTemplate.execute(status -> hibernateTemplate.save(new PersistentObject(id2)));
        }
        catch (Throwable e) {
            success = false;
        }
        Assert.assertFalse(success);

        restoreConnection();

        Assert.assertNull(transactionTemplate.execute(status -> hibernateTemplate.get(PersistentObject.class, id2)));

        transactionTemplate.execute(status -> hibernateTemplate.save(new PersistentObject(id2)));

        Assert.assertNotNull(transactionTemplate.execute(status -> hibernateTemplate.get(PersistentObject.class, id1)));
        Assert.assertNotNull(transactionTemplate.execute(status -> hibernateTemplate.get(PersistentObject.class, id2)));

    }

}
