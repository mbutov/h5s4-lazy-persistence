package org.hslp.hibernate4;

import java.sql.SQLException;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.GenericXmlContextLoader;

/**
 * @author Maxim Butov
 */
@ContextConfiguration(loader = GenericXmlContextLoader.class, locations = "HibernateLazyPersistenceTest-context.xml")
public class LazyHibernate4Test1 extends AbstractLazyHibernate4Tests {

    @Override
    protected void brakeConnection() {
        unsafeDataSource.setDataSourceException(SQLException::new);
    }

}
