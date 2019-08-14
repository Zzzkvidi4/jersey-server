package com.zzzkvidi4.server;

import com.google.inject.AbstractModule;
import com.google.inject.servlet.ServletModule;
import com.zzzkvidi4.dal.tables.daos.ProductDao;
import org.jetbrains.annotations.NotNull;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

public final class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new ServletModule());
        bind(ProductDao.class).toInstance(new ProductDao(configuration()));
    }

    @NotNull
    private DataSource dataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUser("postgres");
        dataSource.setPassword("postgres");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/server");
        return dataSource;
    }

    @NotNull
    private Configuration configuration() {
        return new DefaultConfiguration()
                .derive(SQLDialect.POSTGRES_9_4)
                .derive(new DataSourceConnectionProvider(dataSource()));
    }
}
