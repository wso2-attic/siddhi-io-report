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

import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;

import java.util.List;

/**
 * This interface provides the basic methods of a data provider..
 */
public interface DataProvider {

    /**
     * This method will return the abstract columns created for the dynamic report builder
     *
     * @return the list of abstract columns created.
     */
    public List<AbstractColumn> getColumns();

    /**
     * This method will return an abstract column for a given column name
     *
     * @param columnName the name of the abstract column
     * @return abstract column created in the dynamic report builder for the given name
     */
    public AbstractColumn getCategoryColumn(String columnName);

    /**
     * This method will return the series column created in the dynamic report builder
     *
     * @param columnName name of the series column
     * @return the abstract series column in the dynamic report builder for the given name
     */
    public AbstractColumn getSeriesColumn(String columnName);

    /**
     * This method will return the category column created in the dynamic report builder
     *
     * @return the abstract category column chosen from the created abstract columns
     */
    public AbstractColumn getCategoryColumn();

    /**
     * This method will returm the series column created in the dynamic report builder
     *
     * @return the abstract series column chosen from the created abstract columns
     */
    public AbstractColumn getSeriesColumn();

}

