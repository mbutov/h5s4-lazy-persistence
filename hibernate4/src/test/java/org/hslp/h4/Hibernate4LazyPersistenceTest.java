package org.hslp.h4;

import org.hibernate.SessionFactory;
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
public class Hibernate4LazyPersistenceTest {

    @Autowired
    @Qualifier("lazySessionFactory")
    private SessionFactory sessionFactory;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    public void testSaveInTransaction() {
        transactionTemplate.execute(status -> sessionFactory.getCurrentSession().save(new PersistentObject("test 1")));
    }

    @Test
    @Transactional
    public void testSaveInTransaction2() {
        sessionFactory.getCurrentSession().save(new PersistentObject("test 2"));
    }

}
