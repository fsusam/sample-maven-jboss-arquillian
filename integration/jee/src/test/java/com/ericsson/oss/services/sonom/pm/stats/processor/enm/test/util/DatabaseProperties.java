/*
 * *------------------------------------------------------------------------------
 * ******************************************************************************
 *  COPYRIGHT Ericsson 2018
 *
 *  The copyright to the computer program(s) herein is the property of
 *  Ericsson Inc. The programs may be used and/or copied only with written
 *  permission from Ericsson Inc. or in accordance with the terms and
 *  conditions stipulated in the agreement/contract under which the
 *  program(s) have been supplied.
 * ******************************************************************************
 * ------------------------------------------------------------------------------
 */
package com.ericsson.oss.services.sonom.pm.stats.processor.enm.test.util;

import java.util.Properties;

/**
 * Container class which loads DB credential data.
 */
final class DatabaseProperties {

    private DatabaseProperties() {

    }

    /**
     * @return the JDBC connection URL for <code>oss_repository_service_db</code>
     */
    static String getOssRepoJdbcConnection() {
        return System.getenv("oss-repository-service-db.jdbcConnection");
    }

    /**
     * @return the JDBC properties ('user', 'password', 'driver') for <code>oss_repository_service_db</code>
     */
    static Properties getOssRepoJdbcProperties() {
        final Properties jdbcProperties = new Properties();
        jdbcProperties.setProperty("user", System.getenv("oss-repository-service-db.user"));
        jdbcProperties.setProperty("password", System.getenv("oss-repository-service-db.password"));
        jdbcProperties.setProperty("driver", System.getenv("oss-repository-service-db.driver"));
        return jdbcProperties;
    }
}
