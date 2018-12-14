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

import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class provides the implementation of the data provider for the dynamic reports.
 */
public class DynamicDataProvider implements DataProvider {
    private List<AbstractColumn> abstractColumns;
    private Map<String, AbstractColumn> abstractColumnMap;
    private Map<String, String> reportProperties;
    private JsonParser payloadParser;

    public DynamicDataProvider(Map<String, String> reportProperties) {
        abstractColumns = new ArrayList<>();
        abstractColumnMap = new HashMap<>();
        payloadParser = new JsonParser();
        this.reportProperties = reportProperties;
    }

    public List<Map<String, Object>> getData(Object payload, DynamicReportBuilder reportBuilder) {
        JsonElement firstEvent = getFirstEvent(payload.toString());
        List<Map<String, Object>> data = getParsedData(payload.toString());
        setDynamicReportValue(firstEvent.getAsJsonObject(), ReportConstants.REPORT_DYNAMIC_NAME_VALUE,
                ReportConstants.OUTPUT_PATH);
        Map columnMetadata = getColumnMetaData(firstEvent);
        buildAbstractColumns(columnMetadata, reportBuilder);
        return data;
    }

    public List<Map<String, Object>> getData(Object payload) {
        JsonElement firstEvent = getFirstEvent(payload.toString());
        List<Map<String, Object>> data = getParsedData(payload.toString());
        setDynamicReportValue(firstEvent.getAsJsonObject(), ReportConstants.REPORT_DYNAMIC_NAME_VALUE,
                ReportConstants.OUTPUT_PATH);
        return data;
    }

    private List<Map<String, Object>> getParsedData(String payloadString) {
        JsonElement payloadJson = parsePayload(payloadString);
        JsonArray events = getEvents(payloadJson);
        List<Map<String, Object>> data = new ArrayList<>();
        for (JsonElement eventElement : events) {
            JsonObject jsonObject = eventElement.getAsJsonObject();
            Map<String, Object> eventMap = getMapFromJsonObject(jsonObject);
            data.add(eventMap);
        }
        return data;
    }

    private JsonElement getFirstEvent(String payloadString) {
        JsonElement payloadJson = parsePayload(payloadString);
        return getEvents(payloadJson).get(0);
    }

    private JsonElement parsePayload(String payload) {
        JsonElement payloadJson = payloadParser.parse(payload);
        if (!payloadJson.isJsonArray()) {
            payloadJson = payloadParser.parse("[" + payload + "]");
        }
        return payloadJson;
    }

    private JsonArray getEvents(JsonElement parsedPayload) {
        return parsedPayload.getAsJsonArray();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getMapFromJsonObject(JsonObject jsonObject) {
        return new Gson().fromJson(jsonObject.get("event").toString(), LinkedHashMap.class);
    }

    private Map getColumnMetaData(JsonElement element) {
        JsonObject jsonObject = element.getAsJsonObject();
        //used linked hashmap inorder to keep the insertion order of the json elements.
        Map<String, Object> eventMap = getMapFromJsonObject(jsonObject);
        Map columnMetadata = eventMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getClass().getName(),
                        (oldValue, newValue) -> newValue, LinkedHashMap::new));
        return columnMetadata;
    }

    private void setDynamicReportValue(JsonObject jsonObject, String dynamicValueName, String propertyValueName) {
        if (reportProperties.containsKey(dynamicValueName)) {
            String dynamicReportNameParameter = reportProperties.get(dynamicValueName);
            JsonElement dynamicReportElement = jsonObject.get("event").getAsJsonObject().get
                    (dynamicReportNameParameter.substring(1, dynamicReportNameParameter.length() - 1));
            if (dynamicReportElement != null) {
                String dynamicReportNameValue = dynamicReportElement.getAsString();
                String dynamicOptionPattern = "(\\{\\w*\\})";
                Pattern pattern = Pattern.compile(dynamicOptionPattern);
                Matcher matcher = pattern.matcher(reportProperties.get(propertyValueName));
                String newReportName = matcher.replaceAll(dynamicReportNameValue);
                reportProperties.put(propertyValueName, newReportName);
            }
        }
    }

    private void buildAbstractColumns(Map<String, String> metaData, DynamicReportBuilder reportBuilder) {
        int columnSize = ReportConstants.COLUMN_WIDTH / metaData.size();
        for (Map.Entry<String, String> entry : metaData.entrySet()) {
            ColumnBuilder columnBuilder = ColumnBuilder.getNew();
            if (entry.getValue().equals(Integer.class.getName()) || entry.getValue().equals(Float.class.getName()
            ) || entry.getValue().equals(Double.class.getName())) {
                columnBuilder.addConditionalStyle(DynamicStyleProvider.getNumericalConditionalStyle());
            } else if (entry.getValue().equals(String.class.getName())) {
                columnBuilder.addConditionalStyle(DynamicStyleProvider.getStringConditionalStyle());
            }

            AbstractColumn abColumn = columnBuilder.setColumnProperty(entry.getKey(), entry.getValue())
                    .setTitle(StringUtils.capitalize(entry.getKey())).setWidth(columnSize)
                    .setHeaderStyle(DynamicStyleProvider.getColumnHeaderStyle(entry.getValue()))
                    .build();
            abstractColumns.add(abColumn);
            abstractColumnMap.put(entry.getKey(), abColumn);
            reportBuilder.addField(entry.getKey(), entry.getValue());
        }
    }

    public List<AbstractColumn> getColumns() {
        return abstractColumns;
    }

    public AbstractColumn getCategoryColumn(String columnName) {
        return abstractColumnMap.get(columnName);
    }

    public AbstractColumn getSeriesColumn(String columnName) {
        return abstractColumnMap.get(columnName);
    }

    public Map<String, List<Map<String, Object>>> getDataWithMultipleDatasets(Object payload) {
        Map<String, List<Map<String, Object>>> multipleDatasourceData = new HashMap<>();
        JsonElement payloadJson = parsePayload(payload.toString());
        JsonArray events = getEvents(payloadJson);
        setDynamicReportValue(events.get(0).getAsJsonObject(), ReportConstants.REPORT_DYNAMIC_DATASET_VALUE,
                ReportConstants.DATASET);
        for (JsonElement eventElement : events) {
            JsonObject jsonObject = eventElement.getAsJsonObject();
            Map<String, Object> eventMap = getMapFromJsonObject(jsonObject);
            String datasetAttribute = ReportConstants.EMPTY_STRING;
            if (reportProperties.containsKey(ReportConstants.REPORT_DYNAMIC_DATASET_VALUE)) {
                String datasetAttributeTemp = reportProperties.get(ReportConstants.REPORT_DYNAMIC_DATASET_VALUE);
                datasetAttribute = datasetAttributeTemp.substring(1, datasetAttributeTemp.length() - 1);
            } else if (reportProperties.containsKey(ReportConstants.DATASET)) {
                //this is for the given dataset name directly
                datasetAttribute = reportProperties.get(ReportConstants.DATASET);
            }
            if (datasetAttribute.isEmpty()) {
                // the default value for dataset is taken as the value of the first parameter
                datasetAttribute = eventMap.entrySet().iterator().next().getKey();
            }

            String datasetName = eventMap.get(datasetAttribute).toString();
            List<Map<String, Object>> dataset;
            if (multipleDatasourceData.containsKey(datasetName)) {
                dataset = multipleDatasourceData.get(datasetName);
            } else {
                dataset = new ArrayList<>();
            }
            eventMap.remove(datasetName);
            dataset.add(eventMap);
            multipleDatasourceData.put(datasetName, dataset);
        }
        setDynamicReportValue(events.get(0).getAsJsonObject(), ReportConstants.REPORT_DYNAMIC_NAME_VALUE,
                ReportConstants.OUTPUT_PATH);
        return multipleDatasourceData;
    }

    public AbstractColumn getCategoryColumn() {
        return abstractColumns.get(0);
    }

    public AbstractColumn getSeriesColumn() {
        return abstractColumns.get(1);
    }
}
