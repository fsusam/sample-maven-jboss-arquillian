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

import static com.ericsson.oss.services.sonom.pm.stats.processor.enm.test.util.DatabaseProperties.getOssRepoJdbcConnection;
import static com.ericsson.oss.services.sonom.pm.stats.processor.enm.test.util.DatabaseProperties.getOssRepoJdbcProperties;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.ejb.Singleton;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.presentation.server.sonom.oss.repository.service.api.v1.CreateOssRequest;
import com.ericsson.oss.services.sonom.common.jndi.JndiServiceFinder;
import com.ericsson.oss.services.sonom.common.resources.utils.ResourceLoaderUtils;
import com.ericsson.oss.services.sonom.common.rest.utils.RestExecutor;
import com.ericsson.oss.services.sonom.common.rest.utils.RestResponse;
import com.ericsson.oss.services.sonom.pm.stats.processor.enm.api.PmStatsProcessorEnmService;
import com.ericsson.oss.services.sonom.pm.stats.processor.enm.exception.PmStatsProcessorEnmServiceException;
import com.google.gson.Gson;

/**
 * Test EJB which sets/cleans the test environment.
 */
@Singleton
public class TestEnvironmentBean {

    public static final String TEST_FILE_DIRECTORY = "/usr/local/stub/";

    private static final Logger LOGGER = LoggerFactory.getLogger(TestEnvironmentBean.class);
    private static final RestExecutor REST_EXECUTOR = new RestExecutor();
    private static final String OSS_REPOSITORY_SERVICE_URI = "http://jboss:8080/son-om/oss-repository-service/v1";
    private static final JndiServiceFinder JNDI_SERVICE_FINDER = new JndiServiceFinder();

    private PmStatsProcessorEnmService pmStatsProcessorEnmService;

    /**
     * Pre-creates the database tables needed for test execution. Find implementation of {@link PmStatsProcessorEnmService} with JNDI Service Finder
     * and set interface
     */
    public void setUp() throws SQLException {
        createOssRepositoryTable();
        pmStatsProcessorEnmService = JNDI_SERVICE_FINDER.findFirst(PmStatsProcessorEnmService.class);
    }

    /**
     * Deletes all files in the file system created by:
     * <ol>
     * <li><code>pm-stats-parsing-service</code></li>
     * </ol>
     */
    public void fileSystemCleanUp() {
        getTestFile("pmStatsStubFile").delete();
    }

    /**
     * Gets {@link File} object for a file created by stubbed classes.
     *
     * @param filename
     *            file to find
     * @return the test file
     */
    public File getTestFile(final String filename) {
        return new File(TEST_FILE_DIRECTORY + filename + ".txt");
    }

    private static void createOssRepositoryTable() throws SQLException {
        createTable(getOssRepoJdbcConnection(), getOssRepoJdbcProperties(), "templates",
                "ID SERIAL PRIMARY KEY,NAME varchar(50) UNIQUE NOT NULL,TEMPLATE_SCHEMA json NOT NULL");
        createTable(getOssRepoJdbcConnection(), getOssRepoJdbcProperties(), "oss",
                "ID SERIAL PRIMARY KEY,NAME varchar(50) UNIQUE NOT NULL,TEMPLATE_ID integer NOT NULL, CONTENT json NOT NULL");
    }

    private static void createTable(final String jdbcUrl, final Properties jdbcProperties, final String tableName, final String columnNames)
            throws SQLException {
        final String createTable = String.format("CREATE TABLE IF NOT EXISTS %s (%s);", tableName, columnNames);

        try (final Connection connection = DriverManager.getConnection(jdbcUrl, jdbcProperties);
                final Statement statement = connection.createStatement()) {
            statement.execute(createTable);
        } catch (final SQLException e) {
            LOGGER.error("Error creating table with name {}: {}", tableName, e.getMessage());
            throw e;
        }
    }

    /**
     * Drop Oss tables for cleaning purpose
     *
     * @throws SQLException
     */
    public void dropOssRepositoryServiceTables() throws SQLException {
        final String[] tables = { "templates", "oss" };
        for (final String table : tables) {
            final String deleteTable = String.format("DROP TABLE %s", table);
            try (final Connection connection = DriverManager.getConnection(getOssRepoJdbcConnection(), getOssRepoJdbcProperties());
                    final Statement statement = connection.createStatement()) {
                statement.execute(deleteTable);
            } catch (final SQLException e) {
                LOGGER.error("Error creating table with name {}: {}", table, e.getMessage());
                throw e;
            }
        }
    }

    /**
     * Create a new OSS
     *
     * @param hostName
     *            Host name of the OSS
     * @param templateName
     *            Name of the template associated with this OSS
     * @param contentFileName
     *            File name which has connectivity information
     * @return Response code
     * @throws IOException
     *             thrown if there is any problem creating OSS
     */
    public int createOss(final String hostName, final String templateName, final String contentFileName) throws IOException {
        final String jsonContent = ResourceLoaderUtils.getClasspathResourceAsString(contentFileName);
        final RestResponse<String> response = executePost(hostName, templateName, jsonContent);
        return response.getStatusCode();
    }

    private RestResponse executePost(final String ossName, final String templateName, final String jsonContent) throws IOException {
        final CreateOssRequest createOssRequest = new CreateOssRequest();
        createOssRequest.setTemplateName(templateName);
        createOssRequest.setName(ossName);
        createOssRequest.setContent(jsonContent);

        final Gson gson = new Gson();
        final StringEntity jsonPayload = new StringEntity(gson.toJson(createOssRequest, CreateOssRequest.class));
        final HttpPost httpPostRequest = new HttpPost(OSS_REPOSITORY_SERVICE_URI + "/oss");
        httpPostRequest.setHeader("Content-Type", ContentType.APPLICATION_JSON.toString());
        httpPostRequest.setHeader("Accept", ContentType.APPLICATION_JSON.toString());
        httpPostRequest.setEntity(jsonPayload);
        return REST_EXECUTOR.sendPostRequest(httpPostRequest);
    }

    /**
     * Load PM data
     *
     * @param ossName
     *            {@link String } get OSS information by name
     * @throws PmStatsProcessorEnmServiceException
     */
    public void loadPmData(final String ossName) throws PmStatsProcessorEnmServiceException {
        pmStatsProcessorEnmService.loadPmData(ossName);
    }
}
