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

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.domain.DynamicReport;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import org.wso2.extension.siddhi.io.report.util.DynamicLayoutManager;
import org.wso2.extension.siddhi.io.report.util.ReportConstants;
import org.wso2.siddhi.core.exception.SiddhiAppRuntimeException;

import java.io.File;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

/**
 * This abstract class provides implementation of methods for the report generation.
 */
public abstract class ReportGenerator {
    private Map<String, String> reportProperties = Collections.EMPTY_MAP;

    public ReportGenerator(Map<String, String> reportProperties) {
        this.reportProperties = reportProperties;
    }
    
    public void saveReport(JasperPrint jasperPrint, String outputPath) {
        String fileName = reportProperties.get(ReportConstants.OUTPUT_PATH);
        File destFile = null;
        try {
            switch (reportProperties.get(ReportConstants.OUTPUT_FORMAT).toLowerCase(Locale.ENGLISH)) {
                case "csv":
                    fileName += ".csv";
                    destFile = new File(fileName);
                    JRCsvExporter csvExporter = new JRCsvExporter();
                    csvExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                    csvExporter.setExporterOutput(new SimpleWriterExporterOutput(destFile));
                    csvExporter.exportReport();
                    break;
                case "excel":
                    fileName += ".xlsx";
                    destFile = new File(fileName);
                    JRXlsxExporter xlsxExporter = new JRXlsxExporter();
                    xlsxExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                    xlsxExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(destFile));
                    xlsxExporter.exportReport();
                    break;
                case "pdf":
                    outputPath += ".pdf";
                    JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);
                    break;
                default:
                    break;
            }
        } catch (JRException e) {
            throw new SiddhiAppRuntimeException("Cannot save report " + fileName + " to " + outputPath + ".", e);
        }
    }

    public JasperPrint generateReportPrint(DynamicReport report, DynamicLayoutManager reportLayout,
                                           JRBeanCollectionDataSource dataSource, Map<String, Object> parameters)
            throws SiddhiAppRuntimeException {
        JasperPrint jasperPrint;
        try {
            jasperPrint = DynamicJasperHelper.generateJasperPrint(report, reportLayout, dataSource,
                    parameters);
        } catch (JRException e) {
            throw new SiddhiAppRuntimeException("Failed to generate the JasperPrint " + report.getReportName() + ".", e);
        } catch (ClassCastException e) {
            throw new SiddhiAppRuntimeException("Failed to generate the report. Provide a numeric series column. ", e);
        }
        return jasperPrint;
    }

    public JasperPrint fillReportData(JasperReport jasperReport, Map<String, Object> parameters, JRDataSource
            dataSource) {
        JasperPrint jasperPrint;
        try {
            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        } catch (JRException e) {
            throw new SiddhiAppRuntimeException("Failed to fill data into report template. " + jasperReport.getName()
                    , e);
        }
        return jasperPrint;
    }

    public JasperDesign loadTemplate(String template) {
        JasperDesign jasperDesign;
        try {
            jasperDesign = JRXmlLoader.load(template);
        } catch (JRException e) {
            throw new SiddhiAppRuntimeException("Failed to load the report template " + template + ".", e);
        }
        return jasperDesign;
    }

    public JasperReport compileTemplate(JasperDesign jasperDesign, String templateName) {
        JasperReport jasperReport;
        try {
            jasperReport = JasperCompileManager.compileReport(jasperDesign);
        } catch (JRException e) {
            throw new SiddhiAppRuntimeException("Failed to compile the template " + templateName + ".", e);
        }
        return jasperReport;
    }

    public DynamicLayoutManager getLayout(Map<String, String> reportProperties) {
        DynamicLayoutManager dynamicLayoutManager = new DynamicLayoutManager();
        if (reportProperties.get(ReportConstants.FOOTER) != null) {
            dynamicLayoutManager.setFooterImagePath(reportProperties.get(ReportConstants.FOOTER));
        }
        return dynamicLayoutManager;
    }

    public abstract void generateReport(Object payload);

    public abstract void generateReport();
}
