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
package io.entgra.device.mgt.core.tenant.mgt.core.impl;

import io.entgra.device.mgt.core.application.mgt.common.exception.ApplicationManagementException;
import io.entgra.device.mgt.core.application.mgt.common.services.ApplicationManager;
import io.entgra.device.mgt.core.application.mgt.core.config.ConfigurationManager;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.MetadataManagementException;
import io.entgra.device.mgt.core.device.mgt.common.permission.mgt.PermissionManagementException;
import io.entgra.device.mgt.core.device.mgt.common.roles.config.Role;
import io.entgra.device.mgt.core.device.mgt.core.config.DeviceConfigurationManager;
import io.entgra.device.mgt.core.device.mgt.core.config.DeviceManagementConfig;
import io.entgra.device.mgt.core.device.mgt.core.permission.mgt.PermissionUtils;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.TransactionManagementException;
import io.entgra.device.mgt.core.device.mgt.core.dao.DeviceManagementDAOException;
import io.entgra.device.mgt.core.device.mgt.core.dao.DeviceManagementDAOFactory;
import io.entgra.device.mgt.core.device.mgt.core.dao.TenantDAO;
import io.entgra.device.mgt.core.tenant.mgt.core.TenantManager;
import io.entgra.device.mgt.core.tenant.mgt.common.exception.TenantMgtException;
import io.entgra.device.mgt.core.tenant.mgt.core.internal.TenantMgtDataHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.stratos.common.beans.TenantInfoBean;
import org.wso2.carbon.user.api.Permission;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TenantManagerImpl implements TenantManager {
    private static final Log log = LogFactory.getLog(TenantManagerImpl.class);
    private static final String PERMISSION_ACTION = "ui.execute";
    TenantDAO tenantDao;

    public TenantManagerImpl() {
        this.tenantDao = DeviceManagementDAOFactory.getTenantDAO();
    }

    @Override
    public void addDefaultRoles(TenantInfoBean tenantInfoBean) throws TenantMgtException {
        initTenantFlow(tenantInfoBean);
        DeviceManagementConfig config = DeviceConfigurationManager.getInstance().getDeviceManagementConfig();
        if (config.getDefaultRoles().isEnabled()) {
            Map<String, List<Permission>> roleMap = getValidRoleMap(config);
            try {
                UserStoreManager userStoreManager = TenantMgtDataHolder.getInstance().getRealmService()
                        .getTenantUserRealm(tenantInfoBean.getTenantId()).getUserStoreManager();

                roleMap.forEach((key, value) -> {
                    try {
                        userStoreManager.addRole(key, null, value.toArray(new Permission[0]));
                    } catch (UserStoreException e) {
                        log.error("Error occurred while adding default roles into user store", e);
                    }
                });
            } catch (UserStoreException e) {
                String msg = "Error occurred while getting user store manager";
                log.error(msg, e);
                throw new TenantMgtException(msg, e);
            }
        }
        try {
            TenantMgtDataHolder.getInstance().getWhiteLabelManagementService().
                    addDefaultWhiteLabelThemeIfNotExist(tenantInfoBean.getTenantId());
        } catch (MetadataManagementException e) {
            String msg = "Error occurred while adding default white label theme to created tenant - id "+tenantInfoBean.getTenantId();
            log.error(msg, e);
            throw new TenantMgtException(msg, e);
        } finally {
            endTenantFlow();
        }
    }

    @Override
    public void addDefaultAppCategories(TenantInfoBean tenantInfoBean) throws TenantMgtException {
        initTenantFlow(tenantInfoBean);
        try {
            ApplicationManager applicationManager = TenantMgtDataHolder.getInstance().getApplicationManager();
            applicationManager
                    .addApplicationCategories(ConfigurationManager.getInstance().getConfiguration().getAppCategories());
        } catch (ApplicationManagementException e) {
            String msg = "Error occurred while getting default application categories";
            log.error(msg, e);
            throw new TenantMgtException(msg, e);
        } finally {
            endTenantFlow();
        }
    }

    @Override
    public void addDefaultDeviceStatusFilters(TenantInfoBean tenantInfoBean) throws TenantMgtException {
        initTenantFlow(tenantInfoBean);
        try {
            TenantMgtDataHolder.getInstance().getDeviceStatusManagementService().
                    addDefaultDeviceStatusFilterIfNotExist(tenantInfoBean.getTenantId());
        } catch (MetadataManagementException e) {
            String msg = "Error occurred while adding default device status filter";
            log.error(msg, e);
            throw new TenantMgtException(msg, e);
        } finally {
            endTenantFlow();
        }
    }

    @Override
    public void deleteTenantApplicationData(int tenantId) throws TenantMgtException {
        try {
            TenantMgtDataHolder.getInstance().getApplicationManager().
                    deleteApplicationDataOfTenant(tenantId);
        } catch (ApplicationManagementException e) {
            String msg = "Error occurred while deleting Application related data of Tenant of " +
                    "tenant Id" + tenantId;
            log.error(msg, e);
            throw new TenantMgtException(msg, e);
        }
    }

    @Override
    public void deleteTenantDeviceData(int tenantId) throws TenantMgtException {
        if (log.isDebugEnabled()) {
            log.debug("Request is received to delete Device related data of tenant with ID: " + tenantId);
        }
        try {
            DeviceManagementDAOFactory.beginTransaction();

            tenantDao.deleteExternalPermissionMapping(tenantId);
            tenantDao.deleteExternalDeviceMappingByTenantId(tenantId);
            tenantDao.deleteExternalGroupMappingByTenantId(tenantId);
            // TODO: Check whether deleting DM_DEVICE_ORGANIZATION table data is necessary
//            tenantDao.deleteDeviceOrganizationByTenantId(tenantId);
            tenantDao.deleteDeviceHistoryLastSevenDaysByTenantId(tenantId);
            tenantDao.deleteDeviceDetailByTenantId(tenantId);
            tenantDao.deleteDeviceLocationByTenantId(tenantId);
            tenantDao.deleteDeviceInfoByTenantId(tenantId);
            tenantDao.deleteNotificationByTenantId(tenantId);
            tenantDao.deleteAppIconsByTenantId(tenantId);
            tenantDao.deleteTraccarUnsyncedDevicesByTenantId(tenantId);
            tenantDao.deleteDeviceEventGroupMappingByTenantId(tenantId);
            tenantDao.deleteGeofenceEventMappingByTenantId(tenantId);
            tenantDao.deleteDeviceEventByTenantId(tenantId);
            tenantDao.deleteGeofenceGroupMappingByTenantId(tenantId);
            tenantDao.deleteGeofenceByTenantId(tenantId);
            tenantDao.deleteDeviceGroupPolicyByTenantId(tenantId);
            tenantDao.deleteDynamicTaskPropertiesByTenantId(tenantId);
            tenantDao.deleteDynamicTaskByTenantId(tenantId);
            tenantDao.deleteMetadataByTenantId(tenantId);
            tenantDao.deleteOTPDataByTenantId(tenantId);
            tenantDao.deleteSubOperationTemplate(tenantId);
            tenantDao.deleteDeviceSubTypeByTenantId(tenantId);
            tenantDao.deleteCEAPoliciesByTenantId(tenantId);

            tenantDao.deleteApplicationByTenantId(tenantId);
            tenantDao.deletePolicyCriteriaPropertiesByTenantId(tenantId);
            tenantDao.deletePolicyCriteriaByTenantId(tenantId);
            tenantDao.deleteCriteriaByTenantId(tenantId);
            tenantDao.deletePolicyChangeManagementByTenantId(tenantId);
            tenantDao.deletePolicyComplianceFeaturesByTenantId(tenantId);
            tenantDao.deletePolicyComplianceStatusByTenantId(tenantId);
            tenantDao.deleteRolePolicyByTenantId(tenantId);
            tenantDao.deleteUserPolicyByTenantId(tenantId);
            tenantDao.deleteDeviceTypePolicyByTenantId(tenantId);
            tenantDao.deleteDevicePolicyAppliedByTenantId(tenantId);
            tenantDao.deleteDevicePolicyByTenantId(tenantId);
            tenantDao.deletePolicyCorrectiveActionByTenantId(tenantId);
            tenantDao.deletePolicyByTenantId(tenantId);
            tenantDao.deleteProfileFeaturesByTenantId(tenantId);
            tenantDao.deleteProfileByTenantId(tenantId);

            tenantDao.deleteDeviceOperationResponseLargeByTenantId(tenantId);
            tenantDao.deleteDeviceOperationResponseByTenantId(tenantId);
            tenantDao.deleteEnrolmentOpMappingByTenantId(tenantId);
            tenantDao.deleteDeviceStatusByTenantId(tenantId);
            tenantDao.deleteEnrolmentByTenantId(tenantId);
            tenantDao.deleteOperationByTenantId(tenantId);
            tenantDao.deleteDeviceGroupMapByTenantId(tenantId);
            tenantDao.deleteGroupPropertiesByTenantId(tenantId);
            tenantDao.deleteDevicePropertiesByTenantId(tenantId);
            tenantDao.deleteDeviceByTenantId(tenantId);
            tenantDao.deleteRoleGroupMapByTenantId(tenantId);
            tenantDao.deleteGroupByTenantId(tenantId);
            tenantDao.deleteDeviceCertificateByTenantId(tenantId);

            DeviceManagementDAOFactory.commitTransaction();
        } catch (DeviceManagementDAOException e) {
            DeviceManagementDAOFactory.rollbackTransaction();
            String msg = "Error deleting data of tenant of ID: '" + tenantId + "'";
            log.error(msg);
            throw new TenantMgtException(msg, e);
        } catch (TransactionManagementException e) {
            String msg = "Error initializing transaction when trying to delete tenant info of '" + tenantId + "'";
            log.error(msg);
            throw new TenantMgtException(msg, e);
        } finally {
            DeviceManagementDAOFactory.closeConnection();
        }

    }

    private void initTenantFlow(TenantInfoBean tenantInfoBean) {
        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext privilegedCarbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        privilegedCarbonContext.setTenantId(tenantInfoBean.getTenantId());
        privilegedCarbonContext.setTenantDomain(tenantInfoBean.getTenantDomain());
    }

    private void endTenantFlow() {
        PrivilegedCarbonContext.endTenantFlow();
    }

    private Map<String, List<Permission>> getValidRoleMap(DeviceManagementConfig config) {
        Map<String, List<Permission>> roleMap = new HashMap<>();
        try {
            for (Role role : config.getDefaultRoles().getRoles()) {
                List<Permission> permissionList = new ArrayList<>();
                for (String permissionPath : role.getPermissions()) {
                    if (PermissionUtils.checkResourceExists(permissionPath)) {
                        Permission permission = new Permission(permissionPath, PERMISSION_ACTION);

                        permissionList.add(permission);
                    } else {
                        log.warn("Permission  " + permissionPath + " does not exist. Hence it will not add to role "
                                + role.getName());
                    }
                }
                roleMap.put(role.getName(), permissionList);
            }
        } catch (PermissionManagementException | RegistryException e) {
            log.error("Error occurred while checking permission existence.", e);
        }
        return roleMap;
    }
}
