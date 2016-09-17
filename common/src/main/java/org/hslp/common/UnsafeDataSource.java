package org.hslp.common;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DelegatingDataSource;

import com.google.common.base.Throwables;

/**
 * @author Maxim Butov
 */
public class UnsafeDataSource extends DelegatingDataSource {

    private Supplier<Throwable> exception = SQLException::new;

    public Supplier<Throwable> getException() {
        return exception;
    }

    public void setException(Supplier<Throwable> exception) {
        this.exception = exception;
    }

    private void throwExceptionIfDefined() throws SQLException {
        if (exception != null) {
            try {
                throw exception.get();
            }
            catch (Throwable e) {
                Throwables.propagateIfInstanceOf(e, SQLException.class);
                throw Throwables.propagate(e);
            }
        }
    }

    @Override
    public DataSource getTargetDataSource() {
        return super.getTargetDataSource();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return super.unwrap(iface);
    }

    @Override
    public Connection getConnection() throws SQLException {
        throwExceptionIfDefined();
        return super.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throwExceptionIfDefined();
        return super.getConnection(username, password);
    }

}
