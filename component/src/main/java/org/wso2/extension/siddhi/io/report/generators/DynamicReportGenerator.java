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

import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.chart.DJChart;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.wso2.extension.siddhi.io.report.util.DynamicDataProvider;
import org.wso2.extension.siddhi.io.report.util.DynamicLayoutManager;
import org.wso2.extension.siddhi.io.report.util.ReportConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This class is the implementation of the report generation logic.
 */
public class DynamicReportGenerator extends ReportGenerator {
    private DynamicReportBuilder reportBuilder;
    private Map<String, String> reportProperties;
    private String chartTitle;
    private String categoryName;
    private String seriesName;

    public DynamicReportGenerator(Map<String, String> reportProperties) {
        super(reportProperties);
        this.reportProperties = reportProperties;
        initializeReportContent();
    }

    private void initializeReportContent() {
        chartTitle = reportProperties.getOrDefault(ReportConstants.CHART_TITLE, ReportConstants.EMPTY_STRING);
        categoryName = reportProperties.getOrDefault(ReportConstants.CATEGORY, ReportConstants.EMPTY_STRING);
        seriesName = reportProperties.getOrDefault(ReportConstants.SERIES, ReportConstants.EMPTY_STRING);
    }

    @Override
    public void generateReport(Object payload) {
        DynamicDataProvider dataProvider = new DynamicDataProvider(reportProperties);
        reportBuilder = new DynamicReportBuilder();
        List<Map<String, Object>> data = dataProvider.getData(payload, reportBuilder);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);
        Map<String, Object> parameters = setParameters(reportProperties);
        DynamicLayoutManager reportLayout = getLayout(reportProperties);

        reportBuilder.setTemplateFile(reportProperties.get(ReportConstants.TEMPLATE));
        addChartTo(reportProperties, reportBuilder, dataProvider, parameters);
        DynamicReport report = reportBuilder.build();
        JasperPrint jasperPrint = generateReportPrint(report, reportLayout, dataSource, parameters);
        saveReport(jasperPrint, reportProperties.get(ReportConstants.OUTPUT_PATH));
    }

    @Override
    public void generateReport() {
        // do nothing
    }

    private void addChartTo(Map<String, String> reportProperties, DynamicReportBuilder reportBuilder,
                            DynamicDataProvider dataProvider, Map<String, Object> parameters) {
        ChartGenerator chartGenerator = new ChartGenerator();
        DJChart chart = null;
        //no need of the default case since the chart types are validated in Siddhi app creation level.
        switch (reportProperties.get(ReportConstants.CHART).toLowerCase(Locale.ENGLISH)) {
            case ReportConstants.PIE_CHART:
                if (!categoryName.isEmpty() && !seriesName.isEmpty()) {
                    chart = chartGenerator.createPieChart(dataProvider, chartTitle, categoryName, seriesName);
                } else {
                    chart = chartGenerator.createPieChart(dataProvider, chartTitle);
                }
                break;
            case ReportConstants.BAR_CHART:
                if (!categoryName.isEmpty() && !seriesName.isEmpty()) {
                    chart = chartGenerator.createBarChart(dataProvider, chartTitle, categoryName,
                            seriesName);
                } else {
                    chart = chartGenerator.createBarChart(dataProvider, chartTitle);
                }
                break;
            case ReportConstants.LINE_CHART:
                if (!categoryName.isEmpty() && !seriesName.isEmpty()) {
                    chart = chartGenerator.createLineChart(dataProvider, chartTitle, categoryName,
                            seriesName);
                } else {
                    chart = chartGenerator.createLineChart(dataProvider, chartTitle);
                }
                break;
            default:
                chartGenerator.createTable(dataProvider, reportBuilder);
                break;
        }
        if (chart != null) {
            reportBuilder.addChart(chart);
        }
    }

    private Map<String, Object> setParameters(Map<String, String> reportProperties) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ReportConstants.TITLE, reportProperties.get(ReportConstants.TITLE));
        parameters.put(ReportConstants.SUBTITLE, reportProperties.get(ReportConstants.SUBTITLE));
        parameters.put(ReportConstants.DESCRIPTION, reportProperties.get(ReportConstants.DESCRIPTION));
        if ((reportProperties.get(ReportConstants.HEADER) != null)) {
            parameters.put(ReportConstants.HEADER_IMAGE, reportProperties.get(ReportConstants.HEADER));
        }
        return parameters;
    }
}
