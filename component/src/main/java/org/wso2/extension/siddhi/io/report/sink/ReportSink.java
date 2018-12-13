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

package org.wso2.extension.siddhi.io.report.sink;

import org.apache.log4j.Logger;
import org.wso2.extension.siddhi.io.report.generators.DynamicReportGenerator;
import org.wso2.extension.siddhi.io.report.generators.QueryModeReportGenerator;
import org.wso2.extension.siddhi.io.report.generators.StaticReportGenerator;
import org.wso2.extension.siddhi.io.report.util.ReportConstants;
import org.wso2.siddhi.annotation.Example;
import org.wso2.siddhi.annotation.Extension;
import org.wso2.siddhi.annotation.Parameter;
import org.wso2.siddhi.annotation.util.DataType;
import org.wso2.siddhi.core.config.SiddhiAppContext;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.exception.ConnectionUnavailableException;
import org.wso2.siddhi.core.exception.SiddhiAppCreationException;
import org.wso2.siddhi.core.stream.output.sink.Sink;
import org.wso2.siddhi.core.util.config.ConfigReader;
import org.wso2.siddhi.core.util.transport.DynamicOptions;
import org.wso2.siddhi.core.util.transport.OptionHolder;
import org.wso2.siddhi.query.api.definition.Attribute;
import org.wso2.siddhi.query.api.definition.StreamDefinition;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class contains the implementation of siddhi-io-file sink which provides the functionality of publishing data
 * to reports as pdf files through siddhi.
 */

@Extension(
        name = "report",
        namespace = "sink",
        description = "Report sink can be used to publish (write) event data which is processed within siddhi" +
                "into reports.\n" +
                "Siddhi-io-report provides support to generate reports in pdf, excel and csv formats.\n" +
                "The user can define report sink parameters in the stream definition. If certain parameters are not" +
                "configured, default values are considered for optional parameters.\n" +
                "The report extension consists of two modes: stream and query. By default, the stream mode " +
                "is activated. There the user can get data from the stream as events or RDBMS data from siddhi-rdbms." +
                "The query mode enables the user to write queries for a given data source which is defined in " +
                "the deployment.yaml.\n" +
                "Further, the user can provide an external JRXML template to fill the data.",
        parameters = {
                @Parameter(name = "outputpath",
                        description = "This parameter is used to specify the report path for data to be written.",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "output.format",
                        description = "This parameter is used to specify the format of the report generated. Only " +
                                "PDF, XLS, XLSX, CSV are supported.",
                        optional = true,
                        defaultValue = "PDF",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "title",
                        description = "This parameter is used to specify the title of the report",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "description",
                        description = "This parameter is used to specify the description of the report.",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "subtitle",
                        description = "This parameter is used to specify the subtitle of the report",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "template",
                        description = "This parameter is used to specify an external JRXML template path to generate " +
                                "the report. The given template will be filled and generate the report accordingly.",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "dataset",
                        description = "This parameter is used to specify the dataset for the external template. This " +
                                "value can have a static stream attribute name or a dynamic value specified by '{}'" +
                                "eg:sink(type='report',dataset='{symbol}', @map(type='json'));" +
                                "define stream (symbol string, price float, volume long);",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "header",
                        description = "This parameter is used to specify the header image for the report.",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "footer",
                        description = "This parameter is used to specify the footer image for the report",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "chart",
                        description = "Used to specify the chart type in the report. The value can be 'line', 'bar', " +
                                "'pie', 'table'. The chart is added into the report according to the parameter value.",
                        optional = true,
                        defaultValue = "table",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "chart.title",
                        description = "This parameter is used to specify the title of the chart. The title is added " +
                                "along with the chart.",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "category",
                        description = "This parameter is used to specify the category variable for the chart defined." +
                                " The value of this parameter will be taken as the X axis of the chart.",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "series",
                        description = "This parameter is used to specify the series variable for the chart. The value" +
                                " of this parameter will be taken as the Y axis of the chart and it is necessary to " +
                                "provide  numerical value for this parameter.",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "mode",
                        description = "This parameter is used to specify the series variable for the chart. The value" +
                                " of this parameter will be taken as the Y axis of the chart and it is necessary to " +
                                "provide  numerical value for this parameter.",
                        optional = true,
                        defaultValue = "stream",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "queries",
                        description = "This parameter is used to specify the series variable for the chart. The value" +
                                " of this parameter will be taken as the Y axis of the chart and it is necessary to " +
                                "provide  numerical value for this parameter.",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
        },
        examples = {
                @Example(
                        syntax = " " +
                                "@sink(type='report',outputpath='/abc/example.pdf',@map(type='json'))" +
                                "define stream BarStream(symbol string, price float, volume long);",
                        description = " " +
                                "Under above configuration, for an event chunck," +
                                "a report of type PDF will be generated. There will be a table in the report."
                ),
                @Example(
                        syntax = " " +
                                "@sink(type='report',outputpath='/abc/{symbol}.pdf',@map(type='json'))" +
                                "define stream BarStream(symbol string, price float, volume long);",
                        description = " " +
                                "Under above configuration, for an event chunck," +
                                "a report of type PDF will be generated. The name of the report will be the first " +
                                "event value of the symbol parameter in the stream. There will be a table in the " +
                                "report."
                ),
                @Example(
                        syntax = " " +
                                "@sink(type='report',outputpath='/abc/example.pdf',description='This is a sample " +
                                "report for the report sink.',title='Sample Report',subtitle='Report sink sample'," +
                                "@map(type='json'))" +
                                "define stream BarStream(symbol string, price float, volume long);",
                        description = " " +
                                "Under above configuration, for an event chunck," +
                                "a report of type PDF will be generated. There will be a table in the report." +
                                "The report title, description and subtitle will include the values specified as the " +
                                "parameters. The report will be generated in the given output path."
                ),
                @Example(
                        syntax = " " +
                                "@sink(type='report',outputpath='/abc/example.pdf',chart='Line'," +
                                "chart.title='Line chart for the sample report.',category='symbol',series='price'," +
                                "@map(type='json'))" +
                                "define stream BarStream(symbol string, price float, volume long);",
                        description = " " +
                                "Under above configuration, for an event chunck," +
                                "a report of type PDF will be generated.The report report will include a line chart" +
                                " with the specified chart title. The chart will be generated with the specified " +
                                "category and series. The report will be generated in the given output path."
                ),
                @Example(
                        syntax = " " +
                                "@sink(type='report', outputpath='/abc/example.pdf'," +
                                "mode='query',datasource.name='SAMPLE_DATASOURCE'," +
                                "queries=\"\"\"[{\"query\":\"SELECT * FROM SampleTable;\",\"chart\":\"table\"}," +
                                "@map(type='json'))",
                        description = " " +
                                "Under above configuration, for an event trigger," +
                                "a report of type PDF will be generated.The report report will include a table with " +
                                "the data from the RDBMS datasource specifies as 'datasource.name' and the data from " +
                                "the query as specified in 'queries'. The report will be saved in the given output " +
                                "path."
                ),
                @Example(
                        syntax = " " +
                                "@sink(type='report', outputpath='/abc/example.pdf'," +
                                "mode='query',datasource.name='SAMPLE_DATASOURCE'," +
                                "queries=\"\"\"[{\"query\":\"SELECT * FROM SampleTable;\",\"chart\":\"table\"}," +
                                "{\"query\":\"SELECT Value, Age FROM SampleTable;\"," +
                                "\"chart\":\"line\",\"series\":\"Value\",\"category\":\"Age\",\"chart.title\":\"Test " +
                                "chart\"}]\"\"\",\n" +
                                "@map(type='json'))",
                        description = " " +
                                "Under above configuration, for an event trigger," +
                                "a report of type PDF will be generated. The will be two charts as per each RDBMS " +
                                "query. The datasource for both queries will be the value specified as 'datasource" +
                                ".name'. The first query will generate a table with the data from the query as " +
                                "specified in 'queries'. The second query will generate a line chart where the data " +
                                "will be taken from the second query as defined in the 'queries' parameter. The " +
                                "report will be saved in the given output path."
                )
        }
)

public class ReportSink extends Sink {
    private static final Logger log = Logger.getLogger(ReportSink.class);
    private OptionHolder optionHolder;
    private StreamDefinition streamDefinition;
    private SiddhiAppContext siddhiAppContext;
    private Map<String, String> reportProperties = new HashMap<>();
    private StaticReportGenerator staticReportGenerator;
    private DynamicReportGenerator dynamicReportGenerator;
    private QueryModeReportGenerator queryModeReportGenerator;

    @Override
    protected void init(StreamDefinition streamDefinition, OptionHolder optionHolder, ConfigReader configReader,
                        SiddhiAppContext siddhiAppContext) {
        this.optionHolder = optionHolder;
        this.streamDefinition = streamDefinition;
        this.siddhiAppContext = siddhiAppContext;
        validateAndGetParameters();
        validateInitialReportSettings();
    }

    @Override
    public Class[] getSupportedInputEventClasses() {
        return new Class[]{String.class, Event.class};
    }

    @Override
    public String[] getSupportedDynamicOptions() {
        return new String[]{};
    }

    @Override
    public void publish(Object payload, DynamicOptions dynamicOptions) throws ConnectionUnavailableException {
        if (reportProperties.get(ReportConstants.MODE).equalsIgnoreCase(ReportConstants.DEFAULT_MODE)) {
            if (!reportProperties.get(ReportConstants.TEMPLATE).equals(ReportConstants.DEFAULT_TEMPLATE)) {
                ignoreOtherParameters(reportProperties);
                staticReportGenerator.generateReport(payload);
            } else {
                dynamicReportGenerator.generateReport(payload);
            }
        } else {
            queryModeReportGenerator.generateReport();
        }
    }

    private void ignoreOtherParameters(Map<String, String> reportProperties) {
        String[] ignoringParameters = {ReportConstants.HEADER, ReportConstants.FOOTER, ReportConstants.SERIES,
                ReportConstants.CATEGORY, ReportConstants.CHART, ReportConstants.DESCRIPTION, ReportConstants.SUBTITLE,
                ReportConstants.TITLE, ReportConstants.CHART_TITLE};
        Arrays.stream(ignoringParameters).forEach(parameter -> {
            if (reportProperties.containsKey(parameter)) {
                log.debug("In 'report' sink of siddhi app " + siddhiAppContext.getName() + " Ignoring " +
                        reportProperties.get(parameter) + " for " + parameter + " as JRXML is provided.");
            }
        });
    }

    private void validateInitialReportSettings() {
        if (reportProperties.get(ReportConstants.MODE).equalsIgnoreCase(ReportConstants.DEFAULT_MODE)) {
            if (!reportProperties.get(ReportConstants.TEMPLATE).equalsIgnoreCase(ReportConstants.DEFAULT_TEMPLATE)) {
                staticReportGenerator = new StaticReportGenerator(reportProperties);
            } else {
                dynamicReportGenerator = new DynamicReportGenerator(reportProperties);
            }
        } else {
            queryModeReportGenerator = new QueryModeReportGenerator(reportProperties);
        }
    }

    private void validateAndGetParameters() {
        String template = optionHolder.validateAndGetStaticValue(ReportConstants.TEMPLATE, ReportConstants
                .DEFAULT_TEMPLATE);
        validatePath(template, ReportConstants.TEMPLATE);

        String header = optionHolder.validateAndGetStaticValue(ReportConstants.HEADER, ReportConstants.EMPTY_STRING);
        validatePath(header, ReportConstants.HEADER);

        String footer = optionHolder.validateAndGetStaticValue(ReportConstants.FOOTER, ReportConstants.EMPTY_STRING);
        validatePath(footer, ReportConstants.FOOTER);

        String chart = optionHolder.validateAndGetStaticValue(ReportConstants.CHART, ReportConstants.DEFAULT_CHART);
        validateChart(chart);

        String seriesVariable = optionHolder.validateAndGetStaticValue(ReportConstants.SERIES, ReportConstants
                .EMPTY_STRING);
        validateVariable(ReportConstants.SERIES, seriesVariable);

        String categoryVariable = optionHolder.validateAndGetStaticValue(ReportConstants.CATEGORY, ReportConstants
                .EMPTY_STRING);
        validateVariable(ReportConstants.CATEGORY, categoryVariable);

        String description = optionHolder.validateAndGetStaticValue(ReportConstants.DESCRIPTION, ReportConstants
                .EMPTY_STRING);
        validateStringParameters(ReportConstants.DESCRIPTION, description);

        String reportTitle = optionHolder.validateAndGetStaticValue(ReportConstants.TITLE, ReportConstants
                .DEFAULT_TITLE);
        validateStringParameters(ReportConstants.TITLE, reportTitle);

        String reportSubtitle = optionHolder.validateAndGetStaticValue(ReportConstants.SUBTITLE, ReportConstants
                .EMPTY_STRING);
        validateStringParameters(ReportConstants.SUBTITLE, reportSubtitle);

        String chartTitle = optionHolder.validateAndGetStaticValue(ReportConstants.CHART_TITLE, ReportConstants
                .EMPTY_STRING);
        validateStringParameters(ReportConstants.CHART_TITLE, chartTitle);

        String outputPath = optionHolder.validateAndGetStaticValue(ReportConstants.OUTPUT_PATH, ReportConstants
                .EMPTY_STRING);
        if (!outputPath.isEmpty() && outputPath.contains(File.separator)) {
            validatePath(outputPath.substring(0, outputPath.lastIndexOf(File.separator)), ReportConstants.OUTPUT_PATH);
        }
        validateStringParameters(ReportConstants.OUTPUT_PATH, outputPath);

        String outputFormat = optionHolder.validateAndGetStaticValue(ReportConstants.OUTPUT_FORMAT, ReportConstants
                .PDF);
        validateOutputFormat(outputFormat);

        String datasetName = optionHolder.validateAndGetStaticValue(ReportConstants.DATASET, ReportConstants
                .EMPTY_STRING);
        validateStringParameters(ReportConstants.DATASET, datasetName);
        validateMapType();

        String queryMode = optionHolder.validateAndGetStaticValue(ReportConstants.MODE, ReportConstants.DEFAULT_MODE);
        validateMode(queryMode);

        String datasourceName = optionHolder.validateAndGetStaticValue(ReportConstants.DATASOURCE_NAME,
                ReportConstants.EMPTY_STRING);
        validateQueryParameter(queryMode, datasourceName, ReportConstants.DATASOURCE_NAME);

        String queries = optionHolder.validateAndGetStaticValue(ReportConstants.QUERIES,
                ReportConstants.EMPTY_STRING);
        validateQueryParameter(queryMode, queries, ReportConstants.QUERIES);
    }

    private void validateOutputFormat(String outputFormat) {
        List<String> validOutputFormatTypes = Stream.of(ReportConstants.OutputFormatTypes.values()).map(ReportConstants
                .OutputFormatTypes::name).collect(Collectors.toList());
        if (!validOutputFormatTypes.contains(outputFormat.toUpperCase(Locale.ENGLISH))) {
            throw new SiddhiAppCreationException("In 'report' sink of siddhi app " + siddhiAppContext.getName() + " " +
                    outputFormat + " is not a valid output format. Only PDF, XLS, XLSX, CSV are supported.");
        }
        reportProperties.put(ReportConstants.OUTPUT_FORMAT, outputFormat);
    }

    private void validateMode(String mode) {
        if (mode.equalsIgnoreCase(ReportConstants.DEFAULT_MODE) || mode.equalsIgnoreCase(ReportConstants
                .QUERY)) {
            reportProperties.put(ReportConstants.MODE, mode);
        } else {
            throw new SiddhiAppCreationException("In 'report' sink of siddhi app " + siddhiAppContext.getName() +
                    " '" + mode + "' is invalid. Should be either query or stream.");
        }
    }

    private void validateQueryParameter(String queryMode, String queryValue, String parameterName) {
        if (Boolean.parseBoolean(queryMode)) {
            if (queryValue.isEmpty()) {
                throw new SiddhiAppCreationException("In 'report' sink of siddhi app " + siddhiAppContext.getName() +
                        " '" + parameterName + "' Should be defined when 'mode' is query.");
            }
        }
        reportProperties.put(parameterName, queryValue);
    }

    private void validateMapType() {
        String mapType = streamDefinition.getAnnotations().get(0).getAnnotations().get(0)
                .getElements().get(0).getValue();
        if (!mapType.equals("json")) {
            throw new SiddhiAppCreationException("In 'report' sink of siddhi app " + siddhiAppContext.getName() +
                    " Invalid map type " + mapType + " Only JSON map type is allowed.");
        }
    }

    private void validateStringParameters(String property, String value) {
        if (property.equals(ReportConstants.OUTPUT_PATH) || property.equals(ReportConstants.DATASET)) {
            String dynamicOptionPattern = "(\\{\\w*\\})";
            Pattern pattern = Pattern.compile(dynamicOptionPattern);
            Matcher matcher = pattern.matcher(value);

            if (matcher.find()) {
                String matchingPart = matcher.group().substring(1, matcher.group().length() - 1);
                Attribute matchingAttribute = streamDefinition.getAttributeList().stream()
                        .filter(attribute -> attribute.getName().equals(matchingPart))
                        .findAny()
                        .orElse(null);
                if (matchingAttribute != null) {
                    if (property.equals(ReportConstants.OUTPUT_PATH)) {
                        reportProperties.put(ReportConstants.REPORT_DYNAMIC_NAME_VALUE, matcher.group());
                    } else if (property.equals(ReportConstants.DATASET)) {
                        reportProperties.put(ReportConstants.REPORT_DYNAMIC_DATASET_VALUE, matcher.group());
                    }
                } else {
                    throw new SiddhiAppCreationException("In 'report' sink of siddhi app " + siddhiAppContext.getName()
                            + " Invalid Property '" + matchingPart + "'. No such parameter in the stream definition");
                }
            }
        }
        //doesn't check for empty strings as they are ignored in report generation in default.
        reportProperties.put(property, value);
    }

    private void validateVariable(String property, String chartVariable) {
        if (!chartVariable.isEmpty()) {
            Optional<Attribute> validAttribute = streamDefinition.getAttributeList().stream()
                    .filter(attribute -> attribute.getName().equals(chartVariable))
                    .findAny();
            if (validAttribute.isPresent()) {
                if (property.equals(ReportConstants.SERIES)) {
                    if (!isNumeric(validAttribute.get().getType())) {
                        throw new SiddhiAppCreationException("In 'report' sink of siddhi app " + siddhiAppContext
                                .getName() + " " + chartVariable + "is invalid. Provide a numeric series column.");
                    }
                }
            } else {
                throw new SiddhiAppCreationException("In 'report' sink of siddhi app " + siddhiAppContext.getName()
                        + " Invalid property " + chartVariable + " for " + property);
            }
            reportProperties.put(property, chartVariable);
        }
    }

    private boolean isNumeric(Attribute.Type attributeType) {
        switch (attributeType) {
            case INT:
            case LONG:
            case DOUBLE:
            case FLOAT:
                return true;
            default:
                return false;
        }
    }

    private void validatePath(String path, String parameter) {
        Path file = new File(path).toPath();
        FileSystem fileSystem = FileSystems.getDefault();
        if (!path.equals(ReportConstants.DEFAULT_TEMPLATE) && !Files.exists(file)) {
            if (!path.equals(ReportConstants.DEFAULT_TEMPLATE)) {
                throw new SiddhiAppCreationException("In 'report' sink of siddhi app " + siddhiAppContext.getName() +
                        " " + path + " does not exists. " + parameter + " should be a valid path");
            }
        }

        if (parameter.equals(ReportConstants.TEMPLATE)) {
            PathMatcher matcher = fileSystem.getPathMatcher("glob:**.jrxml");
            if (!path.isEmpty()) {
                if (!matcher.matches(file)) {
                    throw new SiddhiAppCreationException("In 'report' sink of siddhi app " + siddhiAppContext.getName()
                            + " " + path + " is invalid." + ReportConstants
                            .TEMPLATE + " should have a JRXML template");
                } else {
                    reportProperties.put(parameter, path);
                }
            }
        }

        if (parameter.equals(ReportConstants.HEADER) || parameter.equals(ReportConstants.FOOTER)) {
            PathMatcher matcher = fileSystem.getPathMatcher("glob:**.{png,jpeg,JPEG}");
            if (!path.isEmpty()) {
                if (!matcher.matches(file)) {
                    throw new SiddhiAppCreationException("In 'report' sink of siddhi app " + siddhiAppContext.getName()
                            + " Invalid path " + path + ". " + parameter + " should be an image");
                } else {
                    reportProperties.put(parameter, path);
                }
            }
        }
    }

    private void validateChart(String chart) {
        List<String> validChartTypes = Stream.of(ReportConstants.ChartTypes.values()).map(ReportConstants
                .ChartTypes::name).collect(Collectors.toList());
        if (!validChartTypes.contains(chart.toUpperCase(Locale.ENGLISH))) {
            throw new SiddhiAppCreationException("In 'report' sink of siddhi app " + siddhiAppContext.getName() + " " +
                    chart + " is not a valid chart type. Only table,line,bar,pie charts are supported.");
        }
        if (!chart.equals(ReportConstants.DEFAULT_CHART)) {
            if (!reportProperties.containsKey(ReportConstants.SERIES)) {
                boolean numericAttributeFound = streamDefinition.getAttributeList().stream()
                        .anyMatch(attribute -> isNumeric(attribute.getType()));
                if (!numericAttributeFound) {
                    throw new SiddhiAppCreationException("In 'report' sink of siddhi app " +
                            siddhiAppContext.getName() + " " + chart + " chart definition is invalid. " +
                            "There is no numeric stream attribute for the series in. Provide a numeric series column.");
                }
            }
        } else {
            //warn for unnecessary parameter definition for table chart.
            if (reportProperties.containsKey(ReportConstants.SERIES) || reportProperties.containsKey(ReportConstants
                    .CATEGORY)) {
                log.warn("In 'report' sink of siddhi app " + siddhiAppContext.getName() + " Invalid " + chart + " " +
                        "definition. Series or category parameters is ignored for table chart.");
            }
        }
        reportProperties.put(ReportConstants.CHART, chart);
    }

    @Override
    public void connect() throws ConnectionUnavailableException {
        // do nothing
    }

    @Override
    public void disconnect() {
        // do nothing
    }

    @Override
    public void destroy() {
        // do nothing
    }

    @Override
    public Map<String, Object> currentState() {
        return null;
    }

    @Override
    public void restoreState(Map<String, Object> map) {
        // no state
    }
}
