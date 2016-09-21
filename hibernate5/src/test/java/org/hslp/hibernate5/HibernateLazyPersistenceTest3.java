package org.hslp.hibernate5;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.GenericXmlContextLoader;

/**
 * @author Maxim Butov
 */
@ContextConfiguration(loader = GenericXmlContextLoader.class, locations = "HibernateLazyPersistenceTest-context.xml")
public class HibernateLazyPersistenceTest3 extends AbstractLazyHibernate5Tests {

    @Override
    protected void brakeConnection() {
        unsafeDataSource.setDataSourceException(RuntimeException::new);
    }

}
