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

import ar.com.fdvs.dj.domain.entities.conditionalStyle.ConditionStyleExpression;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides implementation of the identifying ranges in data.
 */
public class RangeConditionStyleExpressionGenerator extends ConditionStyleExpression {
    private static final long serialVersionUID = 6106269076155338045L;

    @Override
    public Object evaluate(Map fields, Map variables, Map parameters) {
        Object value = this.getCurrentValue();
        if (value == null) {
            return null;
        } else {
            String text = value.toString();
            String patternString = "([0-9]*\\s*-\\s*[0-9]*)";
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(text);
            return matcher.matches();
        }
    }

    @Override
    public String getClassName() {
        return Boolean.class.getName();
    }
}
