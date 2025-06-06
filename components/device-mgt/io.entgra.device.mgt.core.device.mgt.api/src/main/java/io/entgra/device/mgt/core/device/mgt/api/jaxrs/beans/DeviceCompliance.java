/*
 * Copyright (c) 2018 - 2023, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.entgra.device.mgt.core.device.mgt.api.jaxrs.beans;

import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.monitor.NonComplianceData;
import io.swagger.annotations.ApiModel;

@ApiModel(value = "DeviceCompliance", description = "Device's policy compliance status")
public class DeviceCompliance {

    private String deviceID;
    private NonComplianceData complianceData;
    private Long code;

    public NonComplianceData getComplianceData() {
        return complianceData;
    }

    public void setComplianceData(NonComplianceData complianceData) {
        this.complianceData = complianceData;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

}
