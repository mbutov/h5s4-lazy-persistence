package org.hslp.hibernate5;

import org.hslp.common.AbstractLazyPersistenceTests;
import org.hslp.common.PersistentObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Maxim Butov
 */
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractLazyHibernate5Tests extends AbstractLazyPersistenceTests {

    @Autowired
    protected HibernateTemplate hibernateTemplate;

    @Test(expected = Exception.class)
    public void testHibernateException() throws Exception {
        hibernateTemplate.execute(session -> session.createCriteria(PersistentObject.class).list());
    }

    @Test
    public void testEmptySession() throws Exception {
        hibernateTemplate.execute(session -> null);
    }

    @Test(expected = Exception.class)
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
        try {
            transactionTemplate.execute(status -> hibernateTemplate.save(new PersistentObject(id2)));
            Assert.fail();
        }
        catch (Exception e) {
        }

        restoreConnection();

        Assert.assertNull(transactionTemplate.execute(status -> hibernateTemplate.get(PersistentObject.class, id2)));

        transactionTemplate.execute(status -> hibernateTemplate.save(new PersistentObject(id2)));

        Assert.assertNotNull(transactionTemplate.execute(status -> hibernateTemplate.get(PersistentObject.class, id1)));
        Assert.assertNotNull(transactionTemplate.execute(status -> hibernateTemplate.get(PersistentObject.class, id2)));

    }

}
