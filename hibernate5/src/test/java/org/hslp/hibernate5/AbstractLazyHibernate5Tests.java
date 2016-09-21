package org.hslp.hibernate5;

import org.hslp.common.AbstractLazyPersistenceTests;
import org.hslp.common.PersistentObject;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.transaction.TransactionException;

/**
 * @author Maxim Butov
 */
public abstract class AbstractLazyHibernate5Tests extends AbstractLazyPersistenceTests {

    @Autowired
    protected HibernateTemplate hibernateTemplate;

    @Test(expected = Throwable.class)
    public void testHibernateException() throws Exception {
        hibernateTemplate.execute(session ->
            session.createQuery(session.getCriteriaBuilder().createQuery(PersistentObject.class)).list());
    }

    @Test
    public void testEmptySession() throws Exception {
        hibernateTemplate.execute(session -> null);
    }

    @Test(expected = Throwable.class)
    public void testConnectionBroken() throws Exception {
        transactionTemplate.execute(status -> hibernateTemplate.save(new PersistentObject(newId())));
    }

    @Test
    public void testConnectionRestored() throws Exception {
        restoreConnection();
        transactionTemplate.execute(status -> hibernateTemplate.save(new PersistentObject(newId())));
    }

    @Test
    public void testConnectionBrokenThenRestored() throws Exception {

        String id1 = newId();
        String id2 = newId();

        restoreConnection();
        transactionTemplate.execute(status -> hibernateTemplate.save(new PersistentObject(id1)));

        brakeConnection();
        boolean success;
        try {
            transactionTemplate.execute(status -> hibernateTemplate.save(new PersistentObject(id2)));
            success = true;
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
