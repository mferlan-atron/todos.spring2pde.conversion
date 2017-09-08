// package de.atron.todos.dvc;
//
// import java.sql.SQLException;
//
// import javax.annotation.Resource;
// import javax.enterprise.inject.Produces;
// import javax.sql.DataSource;
//
// import liquibase.integration.cdi.CDILiquibaseConfig;
// import liquibase.integration.cdi.annotations.LiquibaseType;
// import liquibase.resource.ClassLoaderResourceAccessor;
// import liquibase.resource.ResourceAccessor;
//
// public class LiquibaseProducer {
//
// @Resource
// protected DataSource ds;
//
// @Produces
// @LiquibaseType
// public CDILiquibaseConfig createConfig() {
// CDILiquibaseConfig config = new CDILiquibaseConfig();
// config.setChangeLog("liquibase/changelog.xml");
//
// return config;
// }
//
// @Produces
// @LiquibaseType
// public DataSource createDataSource() throws SQLException {
// return ds;
// }
//
// @Produces
// @LiquibaseType
// public ResourceAccessor create() {
// return new ClassLoaderResourceAccessor(getClass().getClassLoader());
// }
// }
