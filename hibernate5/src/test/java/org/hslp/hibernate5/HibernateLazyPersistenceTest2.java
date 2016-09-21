package org.hslp.hibernate5;

import java.sql.SQLException;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.GenericXmlContextLoader;

/**
 * @author Maxim Butov
 */
@ContextConfiguration(loader = GenericXmlContextLoader.class, locations = "HibernateLazyPersistenceTest-context.xml")
public class HibernateLazyPersistenceTest2 extends AbstractLazyHibernate5Tests {

    @Override
    protected void brakeConnection() {
        unsafeDataSource.setConnectionException(SQLException::new);
    }

}
