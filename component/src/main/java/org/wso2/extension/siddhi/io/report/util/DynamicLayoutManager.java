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

import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.ImageBanner;
import net.sf.jasperreports.engine.design.JRDesignBand;

import java.util.Vector;

/**
 * This is a class to provide customized header and footer to the report.
 */
public class DynamicLayoutManager extends ClassicLayoutManager {
    private String footerImagePath = "";

    @Override
    protected void applyBanners() {
        super.applyBanners();
        JRDesignBand pageFooter = (JRDesignBand) getDesign().getPageFooter();
        JRDesignBand pageHeader = (JRDesignBand) getDesign().getTitle();

        if (pageFooter == null) {
            pageFooter = new JRDesignBand();
            getDesign().setPageFooter(pageFooter);
        }

        if (pageHeader == null) {
            pageHeader = new JRDesignBand();
            getDesign().setPageHeader(pageHeader);
        }

        Vector<ImageBanner> bannerVector = new Vector<>();
        if (!footerImagePath.isEmpty()) {
            bannerVector.add(new ImageBanner(footerImagePath, 120, 50,
                    ImageBanner.Alignment.Left));
            applyImageBannersToBand(pageFooter, bannerVector, null, false);
        }
    }

    public void setFooterImagePath(String footerImagePath) {
        this.footerImagePath = footerImagePath;
    }
}
