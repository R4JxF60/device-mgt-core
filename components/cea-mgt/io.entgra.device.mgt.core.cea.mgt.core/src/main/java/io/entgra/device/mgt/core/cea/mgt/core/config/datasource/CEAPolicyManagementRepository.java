/*
 *  Copyright (c) 2018 - 2024, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package io.entgra.device.mgt.core.cea.mgt.core.config.datasource;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ManagementRepository")
public class CEAPolicyManagementRepository {
    private CEADatasourceConfiguration ceaDatasourceConfiguration;

    @XmlElement(name = "DataSourceConfiguration", nillable = false)
    public CEADatasourceConfiguration getDataSourceConfig() {
        return ceaDatasourceConfiguration;
    }

    public void setDataSourceConfig(CEADatasourceConfiguration ceaDatasourceConfiguration) {
        this.ceaDatasourceConfiguration = ceaDatasourceConfiguration;
    }
}
