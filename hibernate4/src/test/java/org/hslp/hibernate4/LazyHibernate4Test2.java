package org.hslp.hibernate4;

import java.sql.SQLException;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.GenericXmlContextLoader;

/**
 * @author Maxim Butov
 */
@ContextConfiguration(loader = GenericXmlContextLoader.class, locations = "hibernate4-context.xml")
public class LazyHibernate4Test2 extends AbstractLazyHibernate4Tests {

    @Override
    protected void brakeConnection() {
        unsafeDataSource.setConnectionException(SQLException::new);
    }

}
