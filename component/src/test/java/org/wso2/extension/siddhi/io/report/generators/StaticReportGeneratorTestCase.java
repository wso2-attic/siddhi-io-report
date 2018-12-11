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

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.extension.siddhi.io.report.util.DynamicDataProvider;
import org.wso2.extension.siddhi.io.report.util.ReportConstants;
import org.wso2.siddhi.core.exception.SiddhiAppCreationException;
import org.wso2.siddhi.core.exception.SiddhiAppRuntimeException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * StaticReportGenerator test case.
 */
public class StaticReportGeneratorTestCase {

    private static final Logger LOGGER = Logger.getLogger(StaticReportGeneratorTestCase.class);
    private ClassLoader classLoader;

    @BeforeClass
    public void init() {
        classLoader = StaticReportGeneratorTestCase.class.getClassLoader();
    }

    @Test(expectedExceptions = SiddhiAppRuntimeException.class)
    public void staticReportGeneratorTest1() {
        LOGGER.info("--------------------------------------------------------------------------------");
        LOGGER.info("StaticReportGenerator TestCase 1 - Generate reports with invalid template given.");
        LOGGER.info("--------------------------------------------------------------------------------");

        Map<String, String> reportProperties = DummyData.getInvalidTemplateReportProperties();
        StaticReportGenerator staticReportGenerator = new StaticReportGenerator(reportProperties);
        staticReportGenerator.generateReport(DummyData.DUMMY_PAYLOAD);
    }

    @Test(expectedExceptions = SiddhiAppRuntimeException.class, expectedExceptionsMessageRegExp = "Failed to compile " +
            "the template(?s) .*")
    public void staticReportGeneratorTest2() {
        LOGGER.info("--------------------------------------------------------------------------------");
        LOGGER.info("StaticReportGenerator TestCase 2 - Generate reports with invalid template given.");
        LOGGER.info("--------------------------------------------------------------------------------");

        Map<String, String> reportProperties = DummyData.getIncorrectTemplateReportProperties();
        StaticReportGenerator staticReportGenerator = new StaticReportGenerator(reportProperties);
        staticReportGenerator.generateReport(DummyData.DUMMY_PAYLOAD);
    }

    @Test(expectedExceptions = SiddhiAppCreationException.class, expectedExceptionsMessageRegExp = "Datasets are " +
            "missing in the template provided(?s) .*")
    public void staticReportGeneratorTest3() {
        LOGGER.info("-------------------------------------------------------------------------------------------");
        LOGGER.info("StaticReportGenerator TestCase 3 - Generate reports without datasets given in the template.");
        LOGGER.info("-------------------------------------------------------------------------------------------");

        Map<String, String> reportProperties = DummyData.getTemplateWithoutDatasetsReportProperties();
        StaticReportGenerator staticReportGenerator = new StaticReportGenerator(reportProperties);
        staticReportGenerator.generateReport(DummyData.DUMMY_PAYLOAD);
    }

    @Test(expectedExceptions = SiddhiAppRuntimeException.class, expectedExceptionsMessageRegExp = "Failed to compile " +
            "the template(?s) .*")
    public void staticReportGeneratorTest4() {
        //test with invalid report element size in the external JRXML template.
        LOGGER.info("-----------------------------------------------------------------------------------");
        LOGGER.info("StaticReportGenerator TestCase 4 - Generate reports with invalid filler properties.");
        LOGGER.info("-----------------------------------------------------------------------------------");

        String template = classLoader.getResource("incorrectFromResultsetData.jrxml").getFile();
        Map<String, String> reportProperties = DummyData.getWithoutParametersReportProperties();
        StaticReportGenerator staticReportGenerator = new StaticReportGenerator(reportProperties);
        JasperDesign jasperDesign = staticReportGenerator.loadTemplate(template);
        JasperReport jasperReport = staticReportGenerator.compileTemplate(jasperDesign, reportProperties.get(ReportConstants.TEMPLATE));
        DynamicDataProvider dynamicDataProvider = new DynamicDataProvider(reportProperties);
        List<Map<String, Object>> dataFromPayload = staticReportGenerator.getDataFromPayload(dynamicDataProvider,
                DummyData.DUMMY_PAYLOAD);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("TableDataSource", new JRMapArrayDataSource(dataFromPayload.toArray()));
        staticReportGenerator.fillReportData(jasperReport, parameters, new JREmptyDataSource());
    }
}
