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


package io.entgra.device.mgt.core.policy.mgt.common;

import io.entgra.device.mgt.core.device.mgt.common.DeviceIdentifier;
import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.Policy;
import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.ProfileFeature;

import java.util.List;

/**
 * This is the interface which will be used to create plug-able policy decision points.
 */
public interface PolicyEvaluationPoint {


    /**
     * This method returns the effective policy from the list.
     * @param deviceIdentifier  device  information.
     * @return returns the effective policy.
     */
    Policy getEffectivePolicy(DeviceIdentifier deviceIdentifier) throws PolicyEvaluationException;


    /**
     * This class will return the effective feature set from the list.
     *
     * @param deviceIdentifier device  information.
     * @return returns the effective feature set.
     */
    List<ProfileFeature> getEffectiveFeatures(DeviceIdentifier deviceIdentifier)  throws PolicyEvaluationException;

    /**
     * This method returns the name of the Policy Evaluation Point
     * @return returns Policy Evaluation Point name
     */
    String getName();
}
