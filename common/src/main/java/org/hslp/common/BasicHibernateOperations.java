package org.hslp.common;

import java.util.List;
import java.util.function.Function;

/**
 * @author Maxim Butov
 */
public interface BasicHibernateOperations {

    String saveNewObject(String id);

    <T> T getObjectById(Class<T> clazz, String id);

    <T> List<T> findAllObjects(Class<T> clazz);

    <S, T> T doInHibernate(Function<S, T> function);

}
