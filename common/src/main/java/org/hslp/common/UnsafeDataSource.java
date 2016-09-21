package org.hslp.common;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.jdbc.datasource.DelegatingDataSource;

/**
 * @author Maxim Butov
 */
public class UnsafeDataSource extends DelegatingDataSource {

    private Supplier<Throwable> dataSourceException;
    private Supplier<Throwable> connectionException;

    public void setDataSourceException(Supplier<Throwable> dataSourceException) {
        clearExceptions();
        this.dataSourceException = dataSourceException;
    }

    public void setConnectionException(Supplier<Throwable> connectionException) {
        clearExceptions();
        this.connectionException = connectionException;
    }

    public void clearExceptions() {
        this.dataSourceException = null;
        this.connectionException = null;
    }

    @Override
    public Connection getConnection() throws SQLException {
        throwExceptionIfDefined(dataSourceException);
        return wrapConnection(super.getConnection());
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throwExceptionIfDefined(dataSourceException);
        return wrapConnection(super.getConnection(username, password));
    }

    private Connection wrapConnection(Connection connection) {
        ProxyFactory proxyFactory = new ProxyFactory(connection);
        proxyFactory.setInterfaces(connection.getClass().getInterfaces());
        proxyFactory.addAdvice((MethodBeforeAdvice) (method, args, target) -> {
            // throw only for connection methods
            if (Connection.class.isAssignableFrom(method.getDeclaringClass())) {
                throwExceptionIfDefined(connectionException);
            }
        });
        return (Connection) proxyFactory.getProxy();
    }

    private void throwExceptionIfDefined(Supplier<Throwable> exception) {
        if (exception != null) {
            Rethrow<RuntimeException> rethrow = new Rethrow(exception.get());
            rethrow.doThrow();
        }
    }

    private static class Rethrow<E extends Throwable> {

        final E throwable;

        Rethrow(E throwable) {
            this.throwable = throwable;
        }

        void doThrow() throws E {
            throw throwable;
        }

    }

}
