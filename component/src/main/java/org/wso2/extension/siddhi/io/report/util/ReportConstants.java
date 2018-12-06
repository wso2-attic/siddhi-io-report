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

package org.wso2.extension.siddhi.io.report.util;

import java.awt.Color;

/**
 * Constants used in siddhi-io-report extension.
 */
public class ReportConstants {
    /* configuration parameters*/
    public static final String TEMPLATE = "template";
    public static final String HEADER = "header";
    public static final String FOOTER = "footer";
    public static final String CHART = "chart";
    public static final String SERIES = "series";
    public static final String CATEGORY = "category";
    public static final String DATASET = "dataset";
    public static final String TITLE = "title";
    public static final String PIE_CHART = "pie";
    public static final String BAR_CHART = "bar";
    public static final String LINE_CHART = "line";
    public static final String CHART_TITLE = "chart.title";
    public static final String SUBTITLE = "subtitle";
    public static final String DESCRIPTION = "description";
    public static final String OUTPUT_PATH = "outputpath";
    public static final String OUTPUT_FORMAT = "output.format";
    public static final String HEADER_IMAGE = "headerImage";
    public static final String MODE = "mode";
    public static final String DATASOURCE_NAME = "datasource.name";
    public static final String QUERIES = "queries";
    public static final String QUERY = "query";
    public static final String REPORT_DYNAMIC_NAME_VALUE = "report.dynamic.name.value";
    public static final String REPORT_DYNAMIC_DATASET_VALUE = "report.dynamic.dataset.value";

    /* default values of configuration parameters*/
    public static final String DEFAULT_TEMPLATE = "dynamicTemplate.jrxml";
    public static final String DEFAULT_CHART = "table";
    public static final String DEFAULT_TITLE = "Siddhi Report";
    public static final String DEFAULT_REPORT_NAME = "SiddhiReport";
    public static final String DEFAULT_MODE = "stream";
    public static final String PDF = "pdf";

    public static final int COLUMN_WIDTH = 400;
    public static final String GREY_BACKGROUND = "#616161";
    public static final Color WHITE_BACKGROUND = new Color(255, 255, 255);
    public static final Color GREY_BORDER = new Color(196, 186, 186);
    public static final Color TABLE_ODD_BACKGROUND = new Color(243, 242, 242);
    public static final int BORDER_WIDTH = 2;
    public static final int HORIZONTAL_PADDING = 20;
    public static final int VERTICAL_PADDING = 5;

    public static final String EMPTY_STRING = "";

    /**
     * Valid chart types
     */
    public enum ChartTypes {
        TABLE,
        LINE,
        BAR,
        PIE,
    }

    /**
     * Valid output formats
     */
    public enum OutputFormatTypes {
        PDF,
        XLS,
        XLSX,
        CSV,
    }
}
