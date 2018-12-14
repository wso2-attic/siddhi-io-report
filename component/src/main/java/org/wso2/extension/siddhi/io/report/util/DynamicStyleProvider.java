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

import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import ar.com.fdvs.dj.domain.entities.conditionalStyle.ConditionalStyle;
import ar.com.fdvs.dj.domain.entities.conditionalStyle.StatusLightCondition;
import org.wso2.extension.siddhi.io.report.generators.RangeConditionStyleExpressionGenerator;

import java.awt.Color;

/**
 * This class provides implementation of the table styles for the reports generated.
 */
public class DynamicStyleProvider {
    public static void addStyles(DynamicReportBuilder reportBuilder) {
        Style st = new Style();
        st.setHorizontalAlign(HorizontalAlign.RIGHT);
        st.setVerticalAlign(VerticalAlign.TOP);
        st.setFont(Font.ARIAL_BIG_BOLD);
        Border border = new Border(ReportConstants.BORDER_WIDTH, Border.BORDER_STYLE_SOLID, ReportConstants
                .GREY_BORDER);
        st.setBorderBottom(border);

        Style tableHeaderStyle = new Style();
        tableHeaderStyle.setTransparent(false);
        tableHeaderStyle.setBackgroundColor(Color.decode(ReportConstants.GREY_BACKGROUND));
        tableHeaderStyle.setTextColor(ReportConstants.WHITE_BACKGROUND);
        tableHeaderStyle.setVerticalAlign(VerticalAlign.MIDDLE);
        tableHeaderStyle.setBorder(Border.THIN());
        tableHeaderStyle.setHorizontalAlign(HorizontalAlign.CENTER);
        reportBuilder.setPrintBackgroundOnOddRows(true);

        Style tableStyle = new Style();
        tableStyle.setPaddingLeft(ReportConstants.HORIZONTAL_PADDING);
        tableStyle.setPaddingRight(ReportConstants.HORIZONTAL_PADDING);
        tableStyle.setPaddingTop(ReportConstants.VERTICAL_PADDING);
        tableStyle.setPaddingBottom(ReportConstants.VERTICAL_PADDING);

        Style oddRowStyle = new Style();
        oddRowStyle.setBackgroundColor(ReportConstants.TABLE_ODD_BACKGROUND);
        reportBuilder.setOddRowBackgroundStyle(oddRowStyle);
        reportBuilder.setDefaultStyles(null, null, null, tableStyle);
        reportBuilder.setUseFullPageWidth(true);
    }

    public static Style getColumnHeaderStyle(String className) {
        Style columnHeaderStyle = new Style();
        columnHeaderStyle.setTransparent(false);
        columnHeaderStyle.setBackgroundColor(Color.decode(ReportConstants.GREY_BACKGROUND));
        columnHeaderStyle.setTextColor(Color.white);
        columnHeaderStyle.setVerticalAlign(VerticalAlign.MIDDLE);

        if (className.equals(Integer.class.getName())) {
            columnHeaderStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
            columnHeaderStyle.setPaddingRight(ReportConstants.HORIZONTAL_PADDING);
        } else if (className.equals(String.class.getName())) {
            columnHeaderStyle.setHorizontalAlign(HorizontalAlign.CENTER);
            columnHeaderStyle.setPaddingLeft(ReportConstants.HORIZONTAL_PADDING);
            columnHeaderStyle.setPaddingRight(ReportConstants.HORIZONTAL_PADDING);
        } else if (className.equals(Double.class.getName()) || className.equals(Long.class.getName()) || className
                .equals(Float.class.getName())) {
            columnHeaderStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
            columnHeaderStyle.setPaddingRight(ReportConstants.HORIZONTAL_PADDING);
        } else {
            columnHeaderStyle.setHorizontalAlign(HorizontalAlign.CENTER);
        }
        return columnHeaderStyle;
    }

    public static ConditionalStyle getNumericalConditionalStyle() {
        Style numericalStyle = new Style();
        numericalStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
        StatusLightCondition numericalLightCondition = new StatusLightCondition((double) 0, (double) Integer
                .MAX_VALUE);
        return new ConditionalStyle(numericalLightCondition, numericalStyle);
    }

    public static ConditionalStyle getStringConditionalStyle() {
        Style rangeStyle = new Style();
        rangeStyle.setHorizontalAlign(HorizontalAlign.CENTER);
        RangeConditionStyleExpressionGenerator rangeConditionStyleExpressionGenerator = new
                RangeConditionStyleExpressionGenerator();
        return new ConditionalStyle(rangeConditionStyleExpressionGenerator, rangeStyle);
    }
}
