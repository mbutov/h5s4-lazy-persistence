package org.hslp.hibernate4;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.GenericXmlContextLoader;

/**
 * @author Maxim Butov
 */
@ContextConfiguration(loader = GenericXmlContextLoader.class, locations = "HibernateLazyPersistenceTest-context.xml")
public class LazyHibernate4Test3 extends AbstractLazyHibernate4Tests {

    @Override
    protected void brakeConnection() {
        unsafeDataSource.setDataSourceException(RuntimeException::new);
    }

}
