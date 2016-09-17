package org.hslp.hibernate5;

import java.sql.SQLException;

import org.hibernate.SessionFactory;
import org.hslp.common.PersistentObject;
import org.hslp.common.UnsafeDataSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.GenericXmlContextLoader;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author Maxim Butov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = GenericXmlContextLoader.class)
public class Hibernate5LazyPersistenceTest {

    @Autowired
    private UnsafeDataSource unsafeDataSource;

    @Autowired
    @Qualifier("lazySessionFactory")
    private SessionFactory sessionFactory;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Before
    public void beforeTest() {
        System.out.println("Hibernate4LazyPersistenceTest.beforeTest");
        unsafeDataSource.setException(SQLException::new);
    }

    @Test(expected = Exception.class)
    public void testException() {
        System.out.println("Hibernate4LazyPersistenceTest.testException");
        sessionFactory.getCurrentSession();
    }

    @Test
    public void testSaveInTransactionTemplate() {
        System.out.println("Hibernate4LazyPersistenceTest.testSaveInTransactionTemplate");
        unsafeDataSource.setException(null);
        transactionTemplate.execute(status -> sessionFactory.getCurrentSession().save(new PersistentObject("test 1")));
    }

}
