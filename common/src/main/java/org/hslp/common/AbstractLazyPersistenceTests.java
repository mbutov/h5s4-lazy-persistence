package org.hslp.common;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author Maxim Butov
 */
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractLazyPersistenceTests {

    @Autowired
    protected UnsafeDataSource unsafeDataSource;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

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

    @Test(expected = Throwable.class)
    public void testSqlException() throws Exception {
        jdbcTemplate.execute(Connection::getMetaData);
    }

    @Test
    public void testEmptyTransaction() throws Exception {
        transactionTemplate.execute(status -> null);
    }

    protected static String newId() {
        return UUID.randomUUID().toString();
    }

}
