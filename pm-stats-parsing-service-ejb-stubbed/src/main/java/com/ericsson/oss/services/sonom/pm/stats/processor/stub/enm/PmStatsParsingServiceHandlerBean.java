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
package com.ericsson.oss.services.sonom.pm.stats.processor.stub.enm;

import java.io.File;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.sonom.pm.stats.parsing.service.api.PmStatsParsingService;
import com.ericsson.oss.services.sonom.pm.stats.parsing.service.api.PmStatsParsingState;

@Stateless
public class PmStatsParsingServiceHandlerBean implements PmStatsParsingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PmStatsParsingServiceHandlerBean.class);
    private static final String TEST_FILE_DIRECTORY = "/usr/local/stub/";

    @Override
    public void handlePmStatsParsing(final Map<String, Map<String, Set<String>>> map, final Map<String, Map<String, Set<String>>> map1, final int i) {
        createTestFile("pmStatsStubFile");
    }

    @Override
    public PmStatsParsingState getCurrentState() {
        return null;
    }

    private static void createTestFile(final String testFileName) {
        try {
            final File testFile = new File(TEST_FILE_DIRECTORY + testFileName + ".txt");
            testFile.createNewFile();
        } catch (final Exception e) {
            LOGGER.error("Error creating stub file '{}'", testFileName, e);
        }
    }
}
