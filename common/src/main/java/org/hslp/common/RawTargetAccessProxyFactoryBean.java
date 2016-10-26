package org.hslp.common;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.RawTargetAccess;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.core.InfrastructureProxy;

/**
 * Расширение {@link ProxyFactoryBean}, добавляющее к прокси интерфейсы {@link InfrastructureProxy} и
 * {@link RawTargetAccess}.
 *
 * <p>По контракту необходимо, чтобы {@code InfrastructureProxy} возвращал оригинальный объект ({@code target}).
 * Однако, используемая фабрикой {@link org.springframework.aop.framework.JdkDynamicAopProxy реализация}
 * выполняет подмену значения ({@code target} -> {@code proxy}) в случае, когда обработчик прокси возвращает {@code target}.
 * {@link RawTargetAccess} указывает, что для методов данного интерфейса подмены выполнять не требуется.</p>
 *
 * Нужно в тех случаях, когда target object используется для внешней синхронизации, например для
 * data source или session factory.
 *
 * @author Maxim Butov
 */
public class RawTargetAccessProxyFactoryBean extends ProxyFactoryBean {

    /**
     * Является ли метод {@link InfrastructureProxy#getWrappedObject}.
     */
    private static boolean isGetWrappedObjectMethod(Method method) {
        return method.getName().equals("getWrappedObject") && method.getParameterTypes().length == 0
            && InfrastructureProxy.class.equals(method.getDeclaringClass());
    }

    /**
     * Обработчик {@link InfrastructureProxy#getWrappedObject}.
     */
    private final MethodInterceptor getWrappedObjectMethodInterceptor = new MethodInterceptor() {

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            return isGetWrappedObjectMethod(invocation.getMethod()) ? getTargetSource().getTarget() : invocation.proceed();
        }

    };

    @Override
    protected Object getProxy(AopProxy aopProxy) {

        Object original = super.getProxy(aopProxy);

        ProxyFactory proxyFactory = new ProxyFactory(original);
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.addInterface(InfrastructureProxy.class);
        proxyFactory.addInterface(RawTargetAccess.class);

        proxyFactory.addAdvice(getWrappedObjectMethodInterceptor);

        return proxyFactory.getProxy(original.getClass().getClassLoader());

    }

}
