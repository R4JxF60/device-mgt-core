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
package io.entgra.device.mgt.core.device.mgt.common.configuration.mgt;

/**
 * This represents the tenant configuration management functionality which should be implemented by
 * the device type plugins.
 */
public interface PlatformConfigurationManagementService {

	/**
	 * Method to add a operation to a device or a set of devices.
	 *
	 * @param platformConfiguration Operation to be added.
	 * @param resourcePath Registry resource path.
     * @return A boolean indicating the status of the operation.
	 * @throws io.entgra.device.mgt.core.device.mgt.common.configuration.mgt.ConfigurationManagementException If some unusual behaviour is observed while adding the
	 * configuration.
	 */
	 boolean saveConfiguration(PlatformConfiguration platformConfiguration,
                               String resourcePath) throws ConfigurationManagementException;

	/**
	 * Method to retrieve the list of general tenant configurations.
	 *
	 * @param resourcePath Registry resource path.
     * @return Platform Configuration object.
	 * @throws io.entgra.device.mgt.core.device.mgt.common.configuration.mgt.ConfigurationManagementException If some unusual behaviour is observed while fetching the
	 * operation list.
	 */
	 PlatformConfiguration getConfiguration(String resourcePath) throws ConfigurationManagementException;

}
