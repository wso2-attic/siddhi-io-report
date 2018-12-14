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

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import org.apache.log4j.Logger;
import org.wso2.extension.siddhi.io.report.util.DynamicDataProvider;
import org.wso2.extension.siddhi.io.report.util.ReportConstants;
import org.wso2.siddhi.core.exception.SiddhiAppCreationException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides implementation for the report generation for an external JRXML template file.
 */
public class StaticReportGenerator extends ReportGenerator {
    private static final Logger LOGGER = Logger.getLogger(StaticReportGenerator.class);
    private Map<String, String> reportProperties;
    private Map<String, Object> parameters;
    private List<Map<String, Object>> data;
    private JasperReport jasperReport;
    private Object[] datasetParameters;

    public StaticReportGenerator(Map<String, String> reportProperties) {
        super(reportProperties);
        this.reportProperties = reportProperties;
        this.parameters = new HashMap<>();
        initializeReportContent();
    }

    private void initializeReportContent() {
        JasperDesign jasperDesign = loadTemplate(reportProperties.get(ReportConstants.TEMPLATE));
        jasperReport = compileTemplate(jasperDesign, reportProperties.get(ReportConstants.TEMPLATE));
        JRParameter[] reportParameters = jasperReport.getParameters();
        datasetParameters = Arrays.stream(reportParameters)
                .filter(parameter ->
                        (parameter.getValueClass().equals(JRDataSource.class)) &&
                                (!parameter.getName().equals("REPORT_DATA_SOURCE"))).toArray();

        if (datasetParameters.length == 0) {
            throw new SiddhiAppCreationException("Datasets are missing in the template provided " +
                    reportProperties.get(ReportConstants.TEMPLATE));
        }
        if (datasetParameters.length > 1) {
            LOGGER.warn("Too many parameters for dataset. Expected 1, found " + datasetParameters.length);
        }
    }

    @Override
    public void generateReport(Object payload) {
        DynamicDataProvider dataProvider = new DynamicDataProvider(reportProperties);
        fillDataToDatasets(dataProvider, payload);
        JasperPrint jasperPrint = fillReportData(jasperReport, parameters, new JREmptyDataSource());
        saveReport(jasperPrint, reportProperties.get(ReportConstants.OUTPUT_PATH));
    }

    @Override
    public void generateReport() {
        // do nothing
    }

    private void fillDataToDatasets(DynamicDataProvider dataProvider, Object payload) {
        if (datasetParameters.length > 1) {
            Map<String, List<Map<String, Object>>> dataWithMultipleDatasets = dataProvider
                    .getDataWithMultipleDatasets(payload);
            for (Map.Entry<String, List<Map<String, Object>>> entry : dataWithMultipleDatasets.entrySet()) {
                data = entry.getValue();
                JRMapArrayDataSource mapArrayDataSource = new JRMapArrayDataSource(data.toArray());
                parameters.put(entry.getKey(), mapArrayDataSource);
            }
        } else {
            data = getDataFromPayload(dataProvider, payload);
            JRMapArrayDataSource mapArrayDataSource = new JRMapArrayDataSource(data.toArray());
            parameters.put(((JRParameter) datasetParameters[0]).getName(), mapArrayDataSource);
        }
    }

    public List<Map<String, Object>> getDataFromPayload(DynamicDataProvider dataProvider, Object payload) {
        return dataProvider.getData(payload);
    }
}
