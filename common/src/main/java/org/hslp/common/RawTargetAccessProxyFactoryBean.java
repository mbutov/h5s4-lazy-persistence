package org.hslp.common;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.RawTargetAccess;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.InfrastructureProxy;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * Расширение {@link ProxyFactoryBean}, добавляющее к прокси интерфейсы {@link InfrastructureProxy} и
 * {@link RawTargetAccess}.
 *
 * Нужно в тех случаях, когда target object используется для внешней синхронизации, например для
 * data source или session factory.
 *
 * @author Maxim Butov
 */
public class RawTargetAccessProxyFactoryBean extends ProxyFactoryBean implements InitializingBean {

    public interface RawTargetAccessInfrastructureProxy extends InfrastructureProxy, RawTargetAccess {

        @Override
        Object getWrappedObject();

    }

    @Override
    public void afterPropertiesSet() throws Exception {

        // нужно добавить RawTargetAccessInfrastructureProxy к списку интерфейсов
        if (getProxiedInterfaces().length == 0 && !isProxyTargetClass()) {

            // самый короткий путь получить ProxyFactoryBean.proxyClassLoader
            ClassLoader proxyClassLoader = (ClassLoader) getProxy(new AopProxy() {
                @Override
                public Object getProxy() {
                    return ClassUtils.getDefaultClassLoader();
                }

                @Override
                public Object getProxy(ClassLoader classLoader) {
                    return classLoader != null ? classLoader : getProxy();
                }
            });

            setInterfaces(ClassUtils.getAllInterfacesForClass(getTargetClass(), proxyClassLoader));

        }

        addInterface(RawTargetAccessInfrastructureProxy.class);

        // обработчик RawTargetAccessInfrastructureProxy.getWrappedObject()
        Method getWrappedObject = RawTargetAccessInfrastructureProxy.class.getMethod("getWrappedObject");
        addAdvice((MethodInterceptor) invocation ->
            getWrappedObject.equals(invocation.getMethod()) ? getTargetSource().getTarget() : invocation.proceed());

    }

}
