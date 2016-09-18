package org.hslp.common;

import org.springframework.aop.RawTargetAccess;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.InfrastructureProxy;

import com.google.common.base.Throwables;

/**
 * Расширение {@link ProxyFactoryBean}, добавляющее к прокси интерфейсы {@link InfrastructureProxy} и
 * {@link RawTargetAccess}.
 *
 * Нужно в тех случаях, ...
 *
 * @author Maxim Butov
 */
public class RawTargetAccessFactoryProxy extends ProxyFactoryBean implements InitializingBean {

    private interface RawTargetAccessInfrastructureProxy extends InfrastructureProxy, RawTargetAccess {

        @Override
        Object getWrappedObject();

    }

    private Object getTarget() {
        try {
            return getTargetSource().getTarget();
        }
        catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        addInterface(RawTargetAccessInfrastructureProxy.class);
        addAdvice(new DelegatingIntroductionInterceptor((RawTargetAccessInfrastructureProxy) this::getTarget));

    }

}
