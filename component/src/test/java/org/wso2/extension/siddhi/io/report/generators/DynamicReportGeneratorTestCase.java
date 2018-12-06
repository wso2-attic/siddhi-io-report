/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.extension.siddhi.io.report.generators;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;
import org.wso2.siddhi.core.exception.SiddhiAppRuntimeException;

import java.util.Map;

/**
 * DynamicReportGenerator class test case.
 */
public class DynamicReportGeneratorTestCase {

    private static final Logger LOGGER = Logger.getLogger(DynamicReportGeneratorTestCase.class);

    @Test(expectedExceptions = SiddhiAppRuntimeException.class, expectedExceptionsMessageRegExp = "Failed to generate" +
            " the report. Provide a numeric series column. ")
    public void staticReportGeneratorTest1() {
        LOGGER.info("--------------------------------------------------------------------------------");
        LOGGER.info("DynamicReportGenerator TestCase 1 - Generate reports with invalid template given.");
        LOGGER.info("--------------------------------------------------------------------------------");

        Map<String, String> reportProperties = DummyData.getDyanmicReportParameters();
        DynamicReportGenerator dynamicReportGenerator = new DynamicReportGenerator(reportProperties);
        dynamicReportGenerator.generateReport(DummyData.STRING_DUMMY_DATA);
    }
}
