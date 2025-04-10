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


package io.entgra.device.mgt.core.policy.mgt.core;

import io.entgra.device.mgt.core.device.mgt.common.Feature;
import io.entgra.device.mgt.core.device.mgt.common.*;
import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.Policy;
import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.Profile;
import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.ProfileFeature;
import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.monitor.ComplianceFeature;
import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.monitor.NonComplianceData;
import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.monitor.PolicyComplianceException;
import io.entgra.device.mgt.core.policy.mgt.common.*;
import io.entgra.device.mgt.core.policy.mgt.core.task.TaskScheduleService;

import java.util.List;

public interface PolicyManagerService {

    Profile addProfile(Profile profile) throws PolicyManagementException;

    Profile updateProfile(Profile profile) throws PolicyManagementException;

    Policy addPolicy(Policy policy) throws PolicyManagementException;

    Policy updatePolicy(Policy policy) throws PolicyManagementException;

    boolean deletePolicy(Policy policy) throws PolicyManagementException;

    boolean deletePolicy(int policyId) throws PolicyManagementException;

    Policy getEffectivePolicy(DeviceIdentifier deviceIdentifier) throws PolicyManagementException;

    List<ProfileFeature> getEffectiveFeatures(DeviceIdentifier deviceIdentifier) throws FeatureManagementException;

    List<Policy> getPolicies(String deviceType) throws PolicyManagementException;

    List<Feature> getFeatures() throws FeatureManagementException;

    PolicyAdministratorPoint getPAP() throws PolicyManagementException;

    PolicyInformationPoint getPIP() throws PolicyManagementException;

    PolicyEvaluationPoint getPEP() throws PolicyManagementException;

    TaskScheduleService getTaskScheduleService() throws PolicyMonitoringTaskException;

    int getPolicyCount() throws PolicyManagementException;

    @Deprecated
    Policy getAppliedPolicyToDevice(DeviceIdentifier deviceIdentifier) throws PolicyManagementException;

    Policy getAppliedPolicyToDevice(Device device) throws PolicyManagementException;

    @Deprecated
    List<ComplianceFeature> checkPolicyCompliance(DeviceIdentifier deviceIdentifier, Object
            deviceResponse) throws PolicyComplianceException;

    List<ComplianceFeature> checkPolicyCompliance(Device device, Object
            deviceResponse) throws PolicyComplianceException;

    @Deprecated
    boolean checkCompliance(DeviceIdentifier deviceIdentifier, Object response) throws PolicyComplianceException;

    boolean checkCompliance(Device device, Object response) throws PolicyComplianceException;

    @Deprecated
    NonComplianceData getDeviceCompliance(DeviceIdentifier deviceIdentifier) throws PolicyComplianceException;

    NonComplianceData getDeviceCompliance(Device device) throws PolicyComplianceException;

    boolean isCompliant(DeviceIdentifier deviceIdentifier) throws PolicyComplianceException;

    PaginationResult getPolicyCompliance(PaginationRequest paginationRequest, String policyId, boolean complianceStatus,
            boolean isPending, String fromDate, String toDate) throws PolicyComplianceException;

    List<ComplianceFeature> getNoneComplianceFeatures(int complianceStatusId) throws PolicyComplianceException;
}
