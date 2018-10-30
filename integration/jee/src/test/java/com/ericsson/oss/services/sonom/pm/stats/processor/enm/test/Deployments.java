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

import static com.ericsson.oss.services.sonom.common.test.arquillian.DeploymentUtils.resolveLatestEar;
import static com.ericsson.oss.services.sonom.common.test.arquillian.DeploymentUtils.resolveLatestJar;
import static com.ericsson.oss.services.sonom.common.test.arquillian.DeploymentUtils.resolveLatestJars;
import static com.ericsson.oss.services.sonom.common.test.arquillian.DeploymentUtils.resolveTestBomEar;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Deployments {

    private static final Logger LOGGER = LoggerFactory.getLogger(Deployments.class);

    static EnterpriseArchive getDataLoadingServiceEar() {
        LOGGER.info("Deploying pm-stats-processor-enm-ear...");
        return resolveLatestEar("com.ericsson.oss.services.sonom", "pm-stats-processor-enm-ear");
    }

    static EnterpriseArchive getRetServiceEar() {
        LOGGER.info("Deploying ret-service-ear...");
        return resolveTestBomEar("com.ericsson.oss.services.sonom", "ret-service-ear");
    }

    static EnterpriseArchive getOssRepositoryServiceEar() {
        LOGGER.info("Deploying oss-repository-service-ear...");
        return resolveTestBomEar("com.ericsson.oss.services.sonom", "oss-repository-service-ear");
    }

    static EnterpriseArchive getPmStatsParsingServiceEarStubbed() {
        LOGGER.info("Deploying pm-stats-parsing-service-ear-stubbed...");
        return resolveLatestEar("com.ericsson.oss.services.sonom", "pm-stats-parsing-service-ear-stubbed");
    }

    static EnterpriseArchive getTestEar() {
        LOGGER.info("Deploying Test EAR...");
        final WebArchive ejbJar = ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, PmStatsProcessorEnmIT.class.getPackage())
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsResource("OssContent.json")
                .addAsResource("jsonContentForOssCreation.json");

        return ShrinkWrap.create(EnterpriseArchive.class, "PmStatsProcessorEnmTestEar.ear")
                .addAsLibrary(resolveLatestJar("com.ericsson.oss.services.sonom", "pm-stats-processor-enm-fls-jar"))
                .addAsLibrary(resolveLatestJar("com.ericsson.oss.services.sonom", "pm-stats-processor-enm-api-jar"))
                .addAsLibrary(resolveLatestJar("com.ericsson.oss.presentation.server.sonom", "oss-repository-service-rest-api"))
                .addAsLibrary(resolveLatestJar("com.ericsson.oss.services.sonom", "son-common-jms"))
                .addAsLibrary(resolveLatestJar("com.ericsson.oss.services.sonom", "son-common-test"))
                .addAsLibrary(resolveLatestJar("com.ericsson.oss.services.sonom", "son-common-oss"))
                .addAsLibrary(resolveLatestJar("com.ericsson.oss.services.sonom", "son-common-enm"))
                .addAsLibrary(resolveLatestJar("com.ericsson.oss.services.sonom", "son-common-resources"))
                .addAsLibrary(resolveLatestJar("com.ericsson.oss.services.sonom", "son-common-rest"))
                .addAsLibrary(resolveLatestJar("com.ericsson.oss.services.sonom", "son-common-jndi"))
                .addAsLibrary(resolveLatestJar("org.mockito", "mockito-all"))
                .addAsLibrary(resolveLatestJar("com.google.code.gson", "gson"))
                .addAsLibrary(resolveLatestJar("commons-lang", "commons-lang"))
                .addAsLibrary(resolveLatestJar("org.postgresql", "postgresql"))
                .addAsLibraries(resolveLatestJars("org.jboss.resteasy", "jaxrs-api"))
                .addAsLibraries(resolveLatestJars("org.jboss.resteasy", "resteasy-jaxrs"))
                .addAsLibraries(resolveLatestJars("org.jboss.resteasy", "resteasy-client"))
                .addAsLibraries(resolveLatestJars("org.jboss.resteasy", "resteasy-jackson-provider"))
                .addAsLibraries(resolveLatestJars("org.apache.httpcomponents", "httpclient"))
                .addAsLibraries(resolveLatestJars("org.apache.httpcomponents", "httpcore"))
                .addAsLibraries(resolveLatestJars("joda-time", "joda-time"))
                .addAsModule(ejbJar);
    }
}
