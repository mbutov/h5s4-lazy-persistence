package org.hslp.hibernate5;

import java.io.IOException;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

/**
 * @author Maxim Butov
 */
public class ValidatingLocalSessionFactoryBean extends LocalSessionFactoryBean {

    @Override
    public void afterPropertiesSet() throws IOException {
        super.afterPropertiesSet();
        validateSessionFactory(getObject());
    }

    private void validateSessionFactory(SessionFactory sessionFactory) throws HibernateException {
        sessionFactory.openSession().close();
    }

}
