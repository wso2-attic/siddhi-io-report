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
        description = "The Report sink publishes (write) event data that is processed via Siddhi into reports.\n" +
                "The Siddhi-io-report extension provides support to generate reports in PDF, Excel and CSV formats.\n" +
                "You can configure report sink parameters in the sink annotation connected to the relevant output" +
                " stream definition. Some parameters are optional. If you do not configure them, the default values" +
                " are applied.\n" +
                "The information you want to publish in the report can be taken from a stream or an RDBMS " +
                "datasource\n" +
                "Further, this extension can generate a report based on a specified JRXML template.",
        parameters = {
                @Parameter(name = "outputpath",
                        description = "The report path to the location to which the data is published.",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "output.format",
                        description = "The format of the report generated. The supported formats are 'PDF', 'XLS', " +
                                "'XLSX', and 'CSV'.",
                        optional = true,
                        defaultValue = "PDF",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "title",
                        description = "The title of the report. If you generate the report via a template, the " +
                                "title specified here is overridden by the title specified in the template. For more" +
                                " information, see the description of the 'template' parameter.",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "description",
                        description = "A description of the report. If you generate the report via a template, the " +
                                "description specified here is overridden by the description specified in the " +
                                "template. For more information, see the description of the 'template' parameter.",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "subtitle",
                        description = "The subtitle of the report. If you generate the report via a template, the " +
                                "subtitle specified here is overridden by the subtitle specified in the template. " +
                                "For more information, see the description of the 'template'parameter.",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "template",
                        description = "This parameter specifies an external JRXML template path to generate " +
                                "the report. The Report sink passes the required data to the selected template and" +
                                " generates the report based on it.",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "dataset",
                        description = "The dataset for the external template. The value for this parameter can be a " +
                                "static stream attribute name or a dynamic value specified via '{}'.\n" +
                                "e.g., 'sink(type='report',dataset='{symbol}', @map(type='json'));'\n" +
                                "'define stream (symbol string, price float, volume long);'",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "header",
                        description = "The path to the image to be used as the header of the report.",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "footer",
                        description = "The path to the image to be used as the footer of the report.",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "chart",
                        description = "The chart type of the report. The possible values are 'line', 'bar', 'pie'," +
                                " and 'table'. The value of this parameter is overridden in the following " +
                                "scenarios:\n" +
                                "- If you generate the report via a template, the chart type specified in that " +
                                "template is used. For more information, see the description of the 'template'" +
                                " parameter.\n" +
                                "- If you use the 'query' mode, the chart typespecified within the RDBMS query is " +
                                "used. For more information, see the description of the 'mode' parameter.",
                        optional = true,
                        defaultValue = "table",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "chart.title",
                        description = "The title of the chart. The title is added along with the chart. The value of" +
                                " this parameter is overridden in the following scenarios:\n" +
                                "- If you generate the report via a template, the chart title specified in that " +
                                "template is used. For more information, see the description of the 'template'" +
                                " parameter.\n" +
                                "- If you use the 'query' mode, the chart title specified within the RDBMS query is" +
                                " used. For more information, see the description of the 'mode' parameter.",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "category",
                        description = "The category variable of the chart.The value of this parameter is taken as " +
                                "the X axis of the chart. The value of this parameter is overridden in the following" +
                                " scenarios:\n" +
                                "- If you generate the report via a template, the category specified in that " +
                                "template is used. For more information, see the description of the 'template'" +
                                " parameter.\n" +
                                "- If you use the 'query' mode, the category specified within the RDBMS query is " +
                                "used. For more information, see the description of the 'mode' parameter.",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "series",
                        description = "The series variable of the chart. This value is taken as the Y axis of the " +
                                "chart, and it is necessary to provide  numerical value for this parameter. The " +
                                "value of this parameter is overridden in the following scenarios:\n" +
                                "- If you generate the report via a template, the series specified in that " +
                                "template is used. For more information, see the description of the 'template'" +
                                " parameter.\n" +
                                "- If you use the 'query' mode, the series specified within the RDBMS query is " +
                                "used. For more information, see the description of the 'mode' parameter.",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "mode",
                        description = " The mode in which the extension is applied. The possible values are as " +
                                "follows:\n" +
                                "- 'stream': This mode allows you to get the information you want to publish in the" +
                                " report from a stream.\n" +
                                "- 'query': This mode allows you to write one or more queries to extract data to" +
                                " publish in the report from an RDBMS data store defined in the " +
                                "'<SP_HOME>/conf/<PROFILE>/deployment.yaml' file.\n",
                        optional = true,
                        defaultValue = "stream",
                        type = {DataType.STRING}
                ),
                @Parameter(name = "queries",
                        description = "If you have specified 'query' as the mode via the 'mode' parameter, use this" +
                                " parameter to define one or more queries to extract information from one or more " +
                                "RDBMS stores defined in the <SP_HOME>/conf/<PROFILE>/deployment.yaml' file, and " +
                                "specify how that data must be published in the report.",
                        optional = true,
                        defaultValue = "none",
                        type = {DataType.STRING}
                ),
        },
        examples = {
                @Example(
                        syntax = "@sink(type='report',outputpath='/abc/example.pdf',@map(type='json'))" +
                                "define stream BarStream(symbol string, price float, volume long);",
                        description = "In the above query, the sink gets processed data from the stream named " +
                                "'BarStream'and publishes that data as a report in PDF format. The query does not " +
                                "specify a chart type. Therefore, the report includes a table."
                ),
                @Example(
                        syntax = "@sink(type='report',outputpath='/abc/{symbol}.pdf',@map(type='json'))" +
                                "define stream BarStream(symbol string, price float, volume long);",
                        description = "In the above query, the sink gets processed data from the stream named " +
                                "'BarStream'and publishes that data as a report in PDF format. The name of the report" +
                                " is the same as the value for the 'symbol' attribute in the first event. The query" +
                                " does not specify a chart type. Therefore, the report includes a table."
                ),
                @Example(
                        syntax = "@sink(type='report',outputpath='/abc/example.pdf',description='This is a sample " +
                                "report for the report sink.',title='Sample Report',subtitle='Report sink sample'," +
                                "@map(type='json'))" +
                                "define stream BarStream(symbol string, price float, volume long);",
                        description = "In the above query, the sink gets processed data from the stream named " +
                                "'BarStream and publishes that data as a report in PDF format. The query does not " +
                                "specify a chart type. Therefore, the report includes a table. The title, description" +
                                " and the subtitle of the report are added as specified in the query. The report is" +
                                " saved in the '/abc/example.pdf' path specified in the query."
                ),
                @Example(
                        syntax = "@sink(type='report',outputpath='/abc/example.pdf',chart='line'," +
                                "chart.title='Line chart for the sample report.',category='symbol',series='price'," +
                                "@map(type='json'))" +
                                "define stream BarStream(symbol string, price float, volume long);",
                        description = "In the above query, the sink gets processed data from the stream named " +
                                "'BarStream' and publishes that data as a report in PDF format. The chart included in" +
                                " this report is a line chart titled 'Line chart for the sample report'. The X axis" +
                                " of this chart (i.e., category) is the symbol, and the Y axis (i.e., the series) is" +
                                " the price. The report is saved in the '/abc/example.pdf' output path."
                ),
                @Example(
                        syntax = "@sink(type='report', outputpath='/abc/example.pdf'," +
                                "mode='query',datasource.name='SAMPLE_DATASOURCE'," +
                                "queries=\"\"\"[{\"query\":\"SELECT * FROM SampleTable;\",\"chart\":\"table\"}]," +
                                "@map(type='json'))",
                        description = "In the above query, the sink generates a report in PDF format for an event " +
                                "trigger. The report includes a table with data from the data source named " +
                                "'SAMPLE_DATASOURCE'. Data is retrieved from this data source by the RDBMS query" +
                                " specified via the 'queries' parameter. The report is saved in the " +
                                "'/abc/example.pdf' output path."
                ),
                @Example(
                        syntax = "@sink(type='report', outputpath='/abc/example.pdf'," +
                                "mode='query',datasource.name='SAMPLE_DATASOURCE'," +
                                "queries=\"\"\"[{\"query\":\"SELECT * FROM SampleTable;\",\"chart\":\"table\"}," +
                                "{\"query\":\"SELECT Value, Age FROM SampleTable;\"," +
                                "\"chart\":\"line\",\"series\":\"Value\",\"category\":\"Age\",\"chart.title\":\"Test " +
                                "chart\"}]\"\"\",\n" +
                                "@map(type='json'))",
                        description = "In the above query, the sink generates a report in PDF format for an event " +
                                "trigger. The 'queries' parameter defines two RDBMS queries to create two charts in " +
                                "the report. Both these queries extract information from a data source named " +
                                "'SAMPLE_DATASOURCE'. The first RDBMS query generates a table, and the second " +
                                "RDBMS query generates a line chart. The report is saved in the '/abc/example.pdf'" +
                                " output path."
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
