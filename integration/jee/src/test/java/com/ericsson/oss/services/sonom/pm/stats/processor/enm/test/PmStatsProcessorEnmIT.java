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

package com.ericsson.oss.services.sonom.pm.stats.processor.enm.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.inject.Inject;

import org.apache.http.HttpStatus;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ericsson.oss.services.sonom.pm.stats.processor.enm.exception.PmStatsProcessorEnmServiceException;
import com.ericsson.oss.services.sonom.pm.stats.processor.enm.test.util.TestEnvironmentBean;

@RunWith(Arquillian.class)
public class PmStatsProcessorEnmIT {

    private static final String OSS_NAME = "enm-1";
    private static final String BASE_URL = "http://localhost:8080";
    private static final String DATA_LOADING_SERVICE_ROOT_URL = BASE_URL + "/son-om/pm-stats-processor-enm/v1";
    private static final String START_PM_STATS_URL = DATA_LOADING_SERVICE_ROOT_URL + "/pm/" + OSS_NAME;

    @Inject
    private TestEnvironmentBean testEnvironmentBean;

    @Deployment(name = "OssRepositoryServiceEar", testable = false, order = 1)
    public static EnterpriseArchive getOssRepositoryServiceEar() {
        return Deployments.getOssRepositoryServiceEar();
    }

    @Deployment(name = "RetServiceEar", testable = false, order = 2)
    public static EnterpriseArchive getRetServiceEar() {
        return Deployments.getRetServiceEar();
    }

    @Deployment(name = "DataLoadingServiceEar", testable = false, order = 4)
    public static EnterpriseArchive getDataLoadingServiceEar() {
        return Deployments.getDataLoadingServiceEar();
    }

    @Deployment(name = "PmStatsParsingServiceStubbedEar", testable = false, order = 3)
    public static EnterpriseArchive getStubbedEar() {
        return Deployments.getPmStatsParsingServiceEarStubbed();
    }

    @Deployment(order = 4)
    public static EnterpriseArchive getTestEar() {
        return Deployments.getTestEar();
    }

    /**
     * Test case to setup database tables & create OSS entry in OSS-repository-service.
     * <p>
     * Had issues injecting the bean variable and hence could not do this in {@link org.junit.BeforeClass} or {@link org.junit.AfterClass} static
     * methods.
     */
    @Test
    @InSequence(1)
    public void setUp() throws IOException, SQLException {
        testEnvironmentBean.setUp();
        assertEquals(HttpStatus.SC_CREATED, testEnvironmentBean.createOss("enm-1", "enm", "OssContent.json"));
    }

    @Test
    @InSequence(2)
    public void startPmDataLoading() throws PmStatsProcessorEnmServiceException {
        testEnvironmentBean.loadPmData(OSS_NAME);
    }

    @Test
    @InSequence(3)
    public void verifyPmDataLoadingIsExecuted() {
        final File pmStatsStubFile = testEnvironmentBean.getTestFile("pmStatsStubFile");
        assertTrue(pmStatsStubFile.exists());
    }

    /**
     * Since {@link org.junit.AfterClass} expects a static method, we can't inject {@link TestEnvironmentBean}. The workaround is to add the clean up
     * task as the last test case in the suite.
     */
    @Test
    @InSequence(99)
    public void cleanUp() throws SQLException {
        testEnvironmentBean.fileSystemCleanUp();
        testEnvironmentBean.dropOssRepositoryServiceTables();
    }
}
