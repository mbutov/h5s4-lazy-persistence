package org.hslp.hibernate5;

import org.hslp.common.AbstractLazyPersistenceTests;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.GenericXmlContextLoader;

/**
 * @author Maxim Butov
 */
@ContextConfiguration(loader = GenericXmlContextLoader.class, locations = "hibernate5-context.xml")
public class LazyHibernate5Test6 extends AbstractLazyPersistenceTests {

    @Override
    protected void brakeConnection() {
        unsafeDataSource.setConnectionException(Error::new);
    }

}
