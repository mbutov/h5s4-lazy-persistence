package org.hslp.hibernate5;

import java.sql.SQLException;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.GenericXmlContextLoader;

/**
 * @author Maxim Butov
 */
@ContextConfiguration(loader = GenericXmlContextLoader.class, locations = "hibernate5-context.xml")
public class LazyHibernate5Test1 extends AbstractLazyHibernate5Tests {

    @Override
    protected void brakeConnection() {
        unsafeDataSource.setDataSourceException(SQLException::new);
    }

}
