package org.hslp.common;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author Maxim Butov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class AbstractLazyPersistenceTests {

    @Autowired
    protected UnsafeDataSource unsafeDataSource;

    @Autowired
    @Qualifier("lazyConnectionDataSource")
    protected DataSource lazyDataSource;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected BasicHibernateOperations hibernateOperations;

    @Autowired
    protected TransactionTemplate transactionTemplate;

    protected void brakeConnection() {
        unsafeDataSource.setDataSourceException(SQLException::new);
    }

    protected void restoreConnection() {
        unsafeDataSource.clearExceptions();
    }

    @Before
    public void beforeTest() {
        brakeConnection();
    }

    protected String newId() {
        return UUID.randomUUID().toString();
    }

    @Test
    public void testContextLoaded() throws Throwable {
        Assert.assertNotNull(unsafeDataSource);
        Assert.assertNotNull(lazyDataSource);
        Assert.assertNotNull(jdbcTemplate);
        Assert.assertNotNull(hibernateOperations);
        Assert.assertNotNull(transactionTemplate);
    }

    @Test
    public void testGetLazyConnection() throws Throwable {
        lazyDataSource.getConnection().close();
    }

    @Test(expected = Throwable.class)
    public void testSqlException() throws Throwable {
        jdbcTemplate.execute(Connection::getMetaData);
    }

    @Test(expected = Throwable.class)
    public void testEmptySession() throws Throwable {
        hibernateOperations.doInHibernate(session -> null);
    }

    @Test(expected = Throwable.class)
    public void testEmptyTransaction() throws Throwable {
        transactionTemplate.execute(status -> null);
    }

    @Test(expected = Throwable.class)
    public void testEmptySessionInTransaction() throws Throwable {
        transactionTemplate.execute(status -> hibernateOperations.doInHibernate(session -> null));
    }

    @Test(expected = Throwable.class)
    public void testHibernateException() throws Throwable {
        hibernateOperations.findAllObjects(PersistentObject.class);
    }

    @Test(expected = Throwable.class)
    public void testConnectionBroken() throws Throwable {
        transactionTemplate.execute(status -> hibernateOperations.saveNewObject(newId()));
    }

    @Test
    public void testConnectionRestored() throws Throwable {
        restoreConnection();
        transactionTemplate.execute(status -> hibernateOperations.saveNewObject(newId()));
    }

    @Test
    public void testConnectionBrokenThenRestored() throws Throwable {

        String id1 = newId();
        String id2 = newId();

        restoreConnection();
        transactionTemplate.execute(status -> hibernateOperations.saveNewObject(id1));

        brakeConnection();
        boolean success = true;
        try {
            transactionTemplate.execute(status -> hibernateOperations.saveNewObject(id2));
        }
        catch (Throwable e) {
            success = false;
        }
        Assert.assertFalse(success);

        restoreConnection();

        Assert.assertNull(transactionTemplate.execute(status ->
            hibernateOperations.getObjectById(PersistentObject.class, id2)));

        transactionTemplate.execute(status -> hibernateOperations.saveNewObject(id2));

        Assert.assertNotNull(transactionTemplate.execute(status ->
            hibernateOperations.getObjectById(PersistentObject.class, id1)));
        Assert.assertNotNull(transactionTemplate.execute(status ->
            hibernateOperations.getObjectById(PersistentObject.class, id2)));

    }

}
