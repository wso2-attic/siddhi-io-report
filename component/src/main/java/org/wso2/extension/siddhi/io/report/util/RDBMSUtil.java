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

import com.zaxxer.hikari.HikariDataSource;
import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.wso2.carbon.datasource.core.api.DataSourceService;
import org.wso2.carbon.datasource.core.exception.DataSourceException;
import org.wso2.siddhi.core.exception.SiddhiAppRuntimeException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class is the implementation of the util class of the RDBMS datasources.
 */

public class RDBMSUtil {
    private static final Logger LOG = Logger.getLogger(RDBMSUtil.class);

    /**
     * Utility method to get the datasource service
     *
     * @param dataSourceName The datasource name
     * @return Hikari Data Source
     */
    public static HikariDataSource getDataSourceService(String dataSourceName) {

        BundleContext bundleContext = FrameworkUtil.getBundle(DataSourceService.class)
                .getBundleContext();
        ServiceReference serviceRef = bundleContext.getServiceReference(DataSourceService.class
                .getName());
        if (serviceRef == null) {
            throw new SiddhiAppRuntimeException("DatasourceService : '" +
                    DataSourceService.class.getName() + "' cannot be found.");
        } else {
            DataSourceService dataSourceService = (DataSourceService) bundleContext
                    .getService(serviceRef);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Lookup for datasource '" + dataSourceName + "' completed through " +
                        "DataSource Service lookup.");
            }
            try {
                return (HikariDataSource) dataSourceService.getDataSource(dataSourceName);
            } catch (DataSourceException e) {
                throw new SiddhiAppRuntimeException("Datasource '" + dataSourceName + "' cannot be " +
                        "connected.", e);
            }
        }
    }

    /**
     * Method which can be used to clear up any type of the SQL connectivity artifacts.
     *
     * @param closeable   {@link AutoCloseable} instance (can be null)
     */
    private static void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                LOG.error("Cannot close " + closeable.getClass().getName(), e);
            }
        }
    }

    /**
     * Method which can be used to clear up and ephemeral SQL connectivity artifacts.
     *
     * @param rs   {@link ResultSet} instance (can be null)
     * @param stmt {@link Statement} instance (can be null)
     * @param conn {@link Connection} instance (can be null)
     */
    public static void cleanup(ResultSet rs, Statement stmt, Connection conn) {
        closeQuietly(rs);
        closeQuietly(stmt);
        closeQuietly(conn);
    }
}
