package org.hslp.hibernate4;

import java.util.List;
import java.util.function.Function;

import org.hslp.common.BasicHibernateOperations;
import org.hslp.common.PersistentObject;
import org.springframework.orm.hibernate4.HibernateTemplate;

/**
 * @author Maxim Butov
 */
public class BasicHibernate4Operations implements BasicHibernateOperations {

    private HibernateTemplate hibernateTemplate;

    public BasicHibernate4Operations(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    @Override
    public String saveNewObject(String id) {
        return (String) hibernateTemplate.save(new PersistentObject(id));
    }

    @Override
    public <T> T getObjectById(Class<T> clazz, String id) {
        return hibernateTemplate.get(clazz, id);
    }

    @Override
    public <T> List<T> findAllObjects(Class<T> clazz) {
        return (List) hibernateTemplate.execute(session -> session.createCriteria(clazz).list());
    }

    @Override
    public <S, T> T doInHibernate(Function<S, T> function) {
        return hibernateTemplate.execute(session -> function.apply((S) session));
    }

}
