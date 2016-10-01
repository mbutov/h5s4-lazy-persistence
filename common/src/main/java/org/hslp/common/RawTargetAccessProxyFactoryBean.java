package org.hslp.common;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.RawTargetAccess;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.core.InfrastructureProxy;
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
public class RawTargetAccessProxyFactoryBean extends ProxyFactoryBean {

    public interface RawTargetAccessInfrastructureProxy extends InfrastructureProxy, RawTargetAccess {

        @Override
        Object getWrappedObject();

    }

    @Override
    protected Object getProxy(AopProxy aopProxy) {

        final Object originalProxy = super.getProxy(aopProxy);

        ProxyFactory proxyFactory = new ProxyFactory(originalProxy);
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.setInterfaces(getProxiedInterfaces());
        proxyFactory.addInterface(RawTargetAccessInfrastructureProxy.class);

        // обработчик RawTargetAccessInfrastructureProxy.getWrappedObject()
        Method getWrappedObject = ReflectionUtils.findMethod(RawTargetAccessInfrastructureProxy.class, "getWrappedObject");

        proxyFactory.addAdvice(new MethodInterceptor() {

            @Override
            public Object invoke(MethodInvocation invocation) throws Throwable {
                if (getWrappedObject.equals(invocation.getMethod())) {
                    return getTargetSource().getTarget();
                }
                else {
                    return invocation.proceed();
                }
            }
        });

        return proxyFactory.getProxy(originalProxy.getClass().getClassLoader());
    }

}
