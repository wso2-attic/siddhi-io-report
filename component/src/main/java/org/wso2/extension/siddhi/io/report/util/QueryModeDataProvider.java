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
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.wso2.siddhi.core.exception.SiddhiAppRuntimeException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This class is the implementation of the query mode data provider.
 */
public class QueryModeDataProvider implements DataProvider {
    private static Logger logger = Logger.getLogger(QueryModeDataProvider.class);
    private HikariDataSource dataSource;
    private String dataSourceName;
    private List<AbstractColumn> abstractColumns;
    private Map<String, AbstractColumn> abstractColumnMap;
    private DynamicReportBuilder reportBuilder;

    public QueryModeDataProvider(String dataSourceName) {
        this.dataSourceName = dataSourceName;
        this.dataSource = RDBMSUtil.getDataSourceService(this.dataSourceName);
    }

    public void setReportBuilder(DynamicReportBuilder reportBuilder) {
        this.reportBuilder = reportBuilder;
    }

    private Connection getConnection() {
        Connection conn;
        try {
            conn = this.dataSource.getConnection();
        } catch (SQLException e) {
            throw new SiddhiAppRuntimeException("Cannot create connection from datasource '" + this.dataSourceName +
                    "'.", e);
        }
        return conn;
    }

    public List<Map<String, Object>> processData(String query) {
        Connection conn = this.getConnection();
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        ResultSetMetaData metaData = null;
        List<Map<String, Object>> data = new ArrayList<>();
        this.abstractColumns = new ArrayList<>();
        this.abstractColumnMap = new HashMap<>();

        try {
            stmt = conn.prepareStatement(query);
            resultSet = stmt.executeQuery();
            metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                Map<String, Object> dataMap = new HashMap<>();
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    dataMap.put(metaData.getColumnName(i + 1), resultSet.getObject(i + 1));
                }
                data.add(dataMap);
            }
            addAbstractColumns(metaData);
            return data;
        } catch (SQLException e) {
            throw new SiddhiAppRuntimeException("Cannot retrieve records from  datasource '" + this.dataSourceName +
                    "'.", e);
        } finally {
            RDBMSUtil.cleanup(resultSet, stmt, conn);
        }
    }

    public void addAbstractColumns(ResultSetMetaData metaData) {
        int columnSize = 0;
        try {
            columnSize = ReportConstants.COLUMN_WIDTH / metaData.getColumnCount();
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                ColumnBuilder columnBuilder = ColumnBuilder.getNew();
                String columnClassName = metaData.getColumnClassName(i + 1);
                String columnName = metaData.getColumnName(i + 1);
                if (columnClassName.equals(Integer.class.getName()) || columnClassName.equals(Float.class.getName()
                ) || columnClassName.equals(Double.class.getName())) {
                    columnBuilder.addConditionalStyle(DynamicStyleProvider.getNumericalConditionalStyle());
                } else if (columnClassName.equals(String.class.getName())) {
                    columnBuilder.addConditionalStyle(DynamicStyleProvider.getStringConditionalStyle());
                }

                AbstractColumn abColumn = columnBuilder.setColumnProperty(columnName, columnClassName)
                        .setTitle(StringUtils.capitalize(columnName)).setWidth(columnSize)
                        .setHeaderStyle(DynamicStyleProvider.getColumnHeaderStyle(columnClassName))
                        .build();
                abstractColumns.add(abColumn);
                abstractColumnMap.put(columnName.toLowerCase(Locale.ENGLISH), abColumn);
                reportBuilder.addField(columnName, columnClassName);
            }
        } catch (SQLException e) {
            throw new SiddhiAppRuntimeException("Could not load metadata from '" + this.dataSourceName + "'.", e);
        }
    }

    public List<AbstractColumn> getColumns() {
        return abstractColumns;
    }

    public AbstractColumn getCategoryColumn(String columnName) {
        return abstractColumnMap.get(columnName.toLowerCase(Locale.ENGLISH));
    }

    public AbstractColumn getSeriesColumn(String columnName) {
        return abstractColumnMap.get(columnName.toLowerCase(Locale.ENGLISH));
    }

    public AbstractColumn getCategoryColumn() {
        return abstractColumns.get(0);
    }

    public AbstractColumn getSeriesColumn() {
        return abstractColumns.get(1);
    }
}
