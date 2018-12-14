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

import ar.com.fdvs.dj.core.DJConstants;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.chart.DJChart;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.wso2.extension.siddhi.io.report.util.DataProvider;
import org.wso2.extension.siddhi.io.report.util.DynamicLayoutManager;
import org.wso2.extension.siddhi.io.report.util.QueryModeDataProvider;
import org.wso2.extension.siddhi.io.report.util.ReportConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This class provides the implementation of the query mode report generation logic.
 */
public class QueryModeReportGenerator extends ReportGenerator {
    private DynamicReportBuilder mainReportBuilder;
    private List<Map<String, Object>> allData;
    private Map<String, String> reportProperties;

    public QueryModeReportGenerator(Map<String, String> reportProperties) {
        super(reportProperties);
        mainReportBuilder = new DynamicReportBuilder();
        allData = new ArrayList<>();
        this.reportProperties = reportProperties;
    }

    @Override
    public void generateReport(Object payload) {
        // do nothing
    }

    @Override
    public void generateReport() {
        JsonArray parsedQueries = getParsedQueries(reportProperties.get(ReportConstants.QUERIES));
        QueryModeDataProvider dataProvider = new QueryModeDataProvider(reportProperties.get(ReportConstants
                .DATASOURCE_NAME));
        Map<String, Object> parameters = new HashMap<>();

        mainReportBuilder.setTemplateFile(reportProperties.get(ReportConstants.TEMPLATE));
        fillReport(parsedQueries, dataProvider, parameters);
        setParameters(reportProperties, parameters);

        DynamicLayoutManager reportLayout = getLayout(reportProperties);
        DynamicReport report = mainReportBuilder.build();
        JasperPrint jasperPrint = generateReportPrint(report, reportLayout, new
                JRBeanCollectionDataSource(allData), parameters);
        saveReport(jasperPrint, reportProperties.get(ReportConstants.OUTPUT_PATH));
    }

    private void fillReport(JsonArray parsedQueries, QueryModeDataProvider dataProvider, Map parameters) {
        parsedQueries.forEach(queryElement -> {
            JsonObject queryObject = queryElement.getAsJsonObject();
            DynamicReportBuilder reportBuilder = new DynamicReportBuilder();
            dataProvider.setReportBuilder(reportBuilder);
            String dbQuery = queryObject.get(ReportConstants.QUERY).getAsString();
            List<Map<String, Object>> data = dataProvider.processData(dbQuery);
            allData.addAll(data);

            String chartType = queryObject.get(ReportConstants.CHART).getAsString();
            String category = queryObject.get(ReportConstants.CATEGORY) == null ? dataProvider.getCategoryColumn()
                    .getName() : queryObject.get(ReportConstants.CATEGORY).getAsString();
            String series = queryObject.get(ReportConstants.SERIES) == null ? dataProvider.getSeriesColumn()
                    .getName() : queryObject.get(ReportConstants.SERIES).getAsString();
            String chartTitle = queryObject.get(ReportConstants.CHART_TITLE) == null ? "" : queryObject.get
                    (ReportConstants.CHART_TITLE).getAsString();

            addChartTo(chartType, chartTitle, category, series, reportBuilder, dataProvider);
            DynamicReport subreportBuild = reportBuilder.build();

            String subreportName = Integer.toString(queryObject.hashCode());
            mainReportBuilder.addConcatenatedReport(subreportBuild, new ClassicLayoutManager(), subreportName,
                    DJConstants.DATA_SOURCE_ORIGIN_PARAMETER, DJConstants.DATA_SOURCE_TYPE_COLLECTION);
            parameters.put(subreportName, data);
        });
    }

    private JsonArray getParsedQueries(String payload) {
        JsonParser payloadParser = new JsonParser();
        JsonElement parse = payloadParser.parse(payload);
        return parse.getAsJsonArray();
    }

    private void setParameters(Map<String, String> reportProperties, Map parameters) {
        parameters.put(ReportConstants.TITLE, reportProperties.get(ReportConstants.TITLE));
        parameters.put(ReportConstants.SUBTITLE, reportProperties.get(ReportConstants.SUBTITLE));
        parameters.put(ReportConstants.DESCRIPTION, reportProperties.get(ReportConstants.DESCRIPTION));
        if ((reportProperties.get(ReportConstants.HEADER) != null)) {
            parameters.put(ReportConstants.HEADER_IMAGE, reportProperties.get(ReportConstants.HEADER));
        }
    }

    private void addChartTo(String chartType, String chartTitle, String categoryName, String seriesName,
                            DynamicReportBuilder reportBuilder,
                            DataProvider dataProvider) {
        ChartGenerator chartGenerator = new ChartGenerator();
        DJChart chart = null;

        //no need of the default case since the chart types are validated in Siddhi app creation level.
        switch (chartType.toLowerCase(Locale.ENGLISH)) {
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

        if (!chartType.equalsIgnoreCase(ReportConstants.DEFAULT_CHART)) {
            reportBuilder.addChart(chart);
        }
    }
}
