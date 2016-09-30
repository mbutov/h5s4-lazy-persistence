package org.hslp.hibernate4;

import java.sql.SQLException;

import org.hslp.common.AbstractLazyPersistenceTests;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.GenericXmlContextLoader;

/**
 * @author Maxim Butov
 */
@ContextConfiguration(loader = GenericXmlContextLoader.class, locations = "hibernate4-context.xml")
public class LazyHibernate4Test1 extends AbstractLazyPersistenceTests {

    @Override
    protected void brakeConnection() {
        unsafeDataSource.setDataSourceException(SQLException::new);
    }

}
