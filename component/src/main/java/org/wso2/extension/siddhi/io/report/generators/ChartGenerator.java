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

import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.chart.DJChart;
import ar.com.fdvs.dj.domain.chart.DJChartOptions;
import ar.com.fdvs.dj.domain.chart.builder.DJBarChartBuilder;
import ar.com.fdvs.dj.domain.chart.builder.DJLineChartBuilder;
import ar.com.fdvs.dj.domain.chart.builder.DJPieChartBuilder;
import ar.com.fdvs.dj.domain.chart.plot.DJAxisFormat;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;
import org.wso2.extension.siddhi.io.report.util.DataProvider;
import org.wso2.extension.siddhi.io.report.util.DynamicStyleProvider;

import java.awt.Color;
import java.util.List;

/**
 * This is the implementation of the chart generation for the report chart types.
 */
public class ChartGenerator {
    DJChart createPieChart(DataProvider dataProvider, String chartTitle) {
        return new DJPieChartBuilder()
                .setX(10)
                .setY(10)
                .setWidth(550)
                .setHeight(250)
                .setCentered(false)
                .setBackColor(Color.LIGHT_GRAY)
                .setShowLegend(true)
                .setTitle(chartTitle)
                .setTitleColor(Color.DARK_GRAY)
                .setTitleFont(Font.ARIAL_BIG_BOLD)
                .setTitlePosition(DJChartOptions.EDGE_TOP)
                .setLineStyle(DJChartOptions.LINE_STYLE_DOTTED)
                .setLineWidth(1)
                .setLineColor(Color.DARK_GRAY)
                .setPadding(5)
                .setKey((PropertyColumn) dataProvider.getCategoryColumn())
                .addSerie(dataProvider.getSeriesColumn())
                .setCircular(true)
                .setLabelFormat("{0}{2}")
                .build();
    }

    DJChart createPieChart(DataProvider dataProvider, String chartTitle, String category, String
            series) {
        return new DJPieChartBuilder()
                .setX(10)
                .setY(10)
                .setWidth(550)
                .setHeight(250)
                .setCentered(false)
                .setBackColor(Color.LIGHT_GRAY)
                .setShowLegend(true)
                .setTitle(chartTitle)
                .setTitleColor(Color.DARK_GRAY)
                .setTitleFont(Font.ARIAL_BIG_BOLD)
                .setTitlePosition(DJChartOptions.EDGE_TOP)
                .setLineStyle(DJChartOptions.LINE_STYLE_DOTTED)
                .setLineWidth(1)
                .setLineColor(Color.DARK_GRAY)
                .setPadding(5)
                .setKey((PropertyColumn) dataProvider.getCategoryColumn(category))
                .addSerie(dataProvider.getSeriesColumn(series))
                .setCircular(true)
                .setLabelFormat("{0}{2}")
                .build();
    }

    DJChart createBarChart(DataProvider dataProvider, String chartTitle) {
        DJAxisFormat categoryAxisFormat = new DJAxisFormat(dataProvider.getCategoryColumn().getTitle());
        DJAxisFormat valueAxisFormat = new DJAxisFormat(dataProvider.getSeriesColumn().getTitle());

        return new DJBarChartBuilder()
                .setX(10)
                .setY(10)
                .setWidth(550)
                .setHeight(250)
                .setCentered(false)
                .setBackColor(Color.LIGHT_GRAY)
                .setShowLegend(true)
                .setTitle(chartTitle)
                .setTitleColor(Color.DARK_GRAY)
                .setTitleFont(Font.ARIAL_BIG_BOLD)
                .setTitlePosition(DJChartOptions.EDGE_TOP)
                .setLineStyle(DJChartOptions.LINE_STYLE_DOTTED)
                .setLineWidth(1)
                .setLineColor(Color.DARK_GRAY)
                .setPadding(5)
                .setCategory((PropertyColumn) dataProvider.getCategoryColumn())
                .addSerie(dataProvider.getSeriesColumn())
                .setShowTickMarks(true)
                .setCategoryAxisFormat(categoryAxisFormat)
                .setValueAxisFormat(valueAxisFormat)
                .build();
    }

    DJChart createBarChart(DataProvider dataProvider, String chartTitle, String category, String series) {
        DJAxisFormat categoryAxisFormat = new DJAxisFormat(dataProvider.getCategoryColumn().getTitle());
        DJAxisFormat valueAxisFormat = new DJAxisFormat(dataProvider.getSeriesColumn().getTitle());

        return new DJBarChartBuilder()
                .setX(10)
                .setY(10)
                .setWidth(550)
                .setHeight(250)
                .setCentered(false)
                .setBackColor(Color.LIGHT_GRAY)
                .setShowLegend(true)
                .setTitle(chartTitle)
                .setTitleColor(Color.DARK_GRAY)
                .setTitleFont(Font.ARIAL_BIG_BOLD)
                .setTitlePosition(DJChartOptions.EDGE_TOP)
                .setLineStyle(DJChartOptions.LINE_STYLE_DOTTED)
                .setLineWidth(1)
                .setLineColor(Color.DARK_GRAY)
                .setPadding(5)
                .setCategory((PropertyColumn) dataProvider.getCategoryColumn(category))
                .addSerie(dataProvider.getSeriesColumn(series))
                .setShowTickMarks(true)
                .setCategoryAxisFormat(categoryAxisFormat)
                .setValueAxisFormat(valueAxisFormat)
                .build();
    }

    DJChart createLineChart(DataProvider dataProvider, String chartTitle) {
        DJAxisFormat categoryAxisFormat = new DJAxisFormat(dataProvider.getCategoryColumn().getTitle());
        DJAxisFormat valueAxisFormat = new DJAxisFormat(dataProvider.getSeriesColumn().getTitle());

        return new DJLineChartBuilder()
                .setX(10)
                .setY(10)
                .setWidth(550)
                .setHeight(250)
                .setCentered(false)
                .setBackColor(Color.LIGHT_GRAY)
                .setShowLegend(true)
                .setTitle(chartTitle)
                .setTitleColor(Color.DARK_GRAY)
                .setTitleFont(Font.ARIAL_BIG_BOLD)
                .setTitlePosition(DJChartOptions.EDGE_TOP)
                .setLineStyle(DJChartOptions.LINE_STYLE_DOTTED)
                .setLineWidth(1)
                .setLineColor(Color.DARK_GRAY)
                .setPadding(5)
                .setCategory((PropertyColumn) dataProvider.getCategoryColumn())
                .addSerie(dataProvider.getSeriesColumn())
                .setShowShapes(true)
                .setShowLines(true)
                .setCategoryAxisFormat(categoryAxisFormat)
                .setValueAxisFormat(valueAxisFormat)
                .build();
    }

    DJChart createLineChart(DataProvider dataProvider, String chartTitle, String category, String
            series) {
        DJAxisFormat categoryAxisFormat = new DJAxisFormat(category);
        DJAxisFormat valueAxisFormat = new DJAxisFormat(series);

        return new DJLineChartBuilder()
                .setX(10)
                .setY(10)
                .setWidth(550)
                .setHeight(250)
                .setCentered(false)
                .setBackColor(Color.LIGHT_GRAY)
                .setShowLegend(true)
                .setTitle(chartTitle)
                .setTitleColor(Color.DARK_GRAY)
                .setTitleFont(Font.ARIAL_BIG_BOLD)
                .setTitlePosition(DJChartOptions.EDGE_TOP)
                .setLineStyle(DJChartOptions.LINE_STYLE_DOTTED)
                .setLineWidth(1)
                .setLineColor(Color.DARK_GRAY)
                .setPadding(5)
                .setCategory((PropertyColumn) dataProvider.getCategoryColumn(category))
                .addSerie(dataProvider.getSeriesColumn(series))
                .setShowShapes(true)
                .setShowLines(true)
                .setCategoryAxisFormat(categoryAxisFormat)
                .setValueAxisFormat(valueAxisFormat)
                .build();
    }

    DynamicReportBuilder createTable(DataProvider dataProvider, DynamicReportBuilder reportBuilder) {
        List<AbstractColumn> tableColumns = dataProvider.getColumns();
        for (AbstractColumn column : tableColumns) {
            reportBuilder.addColumn(column);
        }
        DynamicStyleProvider.addStyles(reportBuilder);
        return reportBuilder;
    }
}
