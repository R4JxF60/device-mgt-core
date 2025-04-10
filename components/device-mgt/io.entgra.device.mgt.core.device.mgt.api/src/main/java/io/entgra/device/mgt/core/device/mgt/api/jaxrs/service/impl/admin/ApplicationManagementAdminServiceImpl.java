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
package io.entgra.device.mgt.core.device.mgt.api.jaxrs.service.impl.admin;

import io.entgra.device.mgt.core.device.mgt.api.jaxrs.beans.ApplicationWrapper;
import io.entgra.device.mgt.core.device.mgt.api.jaxrs.beans.ErrorResponse;
import io.entgra.device.mgt.core.device.mgt.api.jaxrs.service.api.admin.ApplicationManagementAdminService;
import io.entgra.device.mgt.core.device.mgt.api.jaxrs.service.impl.util.RequestValidationUtil;
import io.entgra.device.mgt.core.device.mgt.api.jaxrs.util.DeviceMgtAPIUtils;
import io.entgra.device.mgt.core.device.mgt.common.DeviceIdentifier;
import io.entgra.device.mgt.core.device.mgt.common.Platform;
import io.entgra.device.mgt.core.device.mgt.common.app.mgt.App;
import io.entgra.device.mgt.core.device.mgt.common.app.mgt.ApplicationManagementException;
import io.entgra.device.mgt.core.device.mgt.common.app.mgt.ApplicationManager;
import io.entgra.device.mgt.core.device.mgt.common.exceptions.UnknownApplicationTypeException;
import io.entgra.device.mgt.core.device.mgt.common.operation.mgt.Activity;
import io.entgra.device.mgt.core.device.mgt.common.operation.mgt.Operation;
import io.entgra.device.mgt.core.device.mgt.core.util.MDMAndroidOperationUtil;
import io.entgra.device.mgt.core.device.mgt.core.util.MDMIOSOperationUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/admin/applications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ApplicationManagementAdminServiceImpl implements ApplicationManagementAdminService {

    private static final Log log = LogFactory.getLog(ApplicationManagementAdminServiceImpl.class);

    @POST
    @Path("/install-application")
    @Override
    public Response installApplication(ApplicationWrapper applicationWrapper) {
        ApplicationManager appManagerConnector;
        Operation operation = null;
        Activity activity = null;

        RequestValidationUtil.validateApplicationInstallationContext(applicationWrapper);
        try {
            appManagerConnector = DeviceMgtAPIUtils.getAppManagementService();
            App app = applicationWrapper.getApplication();

            if (applicationWrapper.getDeviceIdentifiers() != null) {
                for (DeviceIdentifier deviceIdentifier : applicationWrapper.getDeviceIdentifiers()) {
                    String deviceType = deviceIdentifier.getType().toUpperCase();
                    if (Platform.ANDROID.toString().equals(deviceType)) {
                        operation = MDMAndroidOperationUtil.createInstallAppOperation(app);
                    } else if (Platform.IOS.toString().equals(deviceType)) {
                        operation = MDMIOSOperationUtil.createInstallAppOperation(app);
                    }
                }
                if (applicationWrapper.getRoleNameList() != null && applicationWrapper.getRoleNameList().size() > 0) {
                    activity = appManagerConnector.installApplicationForUserRoles(operation, applicationWrapper.getRoleNameList());
                } else if (applicationWrapper.getUserNameList() != null &&
                        applicationWrapper.getUserNameList().size() > 0) {
                    activity = appManagerConnector.installApplicationForUsers(operation, applicationWrapper.getUserNameList());
                } else if (applicationWrapper.getDeviceIdentifiers() != null &&
                        applicationWrapper.getDeviceIdentifiers().size() > 0) {
                    activity = appManagerConnector.installApplicationForDevices(operation, applicationWrapper.getDeviceIdentifiers());
                } else {
                    String msg = "No application installation criteria i.e. user/role/device is given";
                    return Response.status(Response.Status.BAD_REQUEST).entity(msg).build();
                }
            }
            return Response.status(Response.Status.ACCEPTED).entity(activity).build();
        } catch (ApplicationManagementException e) {
            String msg = "Error occurred while processing application installation request";
            log.error(msg, e);
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(msg).build()).build();
        } catch (UnknownApplicationTypeException e) {
            String msg = "The type of application requested to be installed is not supported";
            log.error(msg, e);
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(msg).build()).build();
        }
    }

    @POST
    @Path("/uninstall-application")
    @Override
    public Response uninstallApplication(ApplicationWrapper applicationWrapper) {
        ApplicationManager appManagerConnector;
        io.entgra.device.mgt.core.device.mgt.common.operation.mgt.Operation operation = null;
        Activity activity = null;

        RequestValidationUtil.validateApplicationInstallationContext(applicationWrapper);
        try {
            appManagerConnector = DeviceMgtAPIUtils.getAppManagementService();
            App app = applicationWrapper.getApplication();

            if (applicationWrapper.getDeviceIdentifiers() != null) {
                for (DeviceIdentifier deviceIdentifier : applicationWrapper.getDeviceIdentifiers()) {
                    String deviceType = deviceIdentifier.getType().toUpperCase();
                    if (Platform.ANDROID.toString().equals(deviceType)) {
                        operation = MDMAndroidOperationUtil.createAppUninstallOperation(app);
                    } else if (deviceType.equals(Platform.IOS.toString())) {
                        operation = MDMIOSOperationUtil.createAppUninstallOperation(app);
                    }
                }
                if (applicationWrapper.getRoleNameList() != null && applicationWrapper.getRoleNameList().size() > 0) {
                    activity = appManagerConnector.installApplicationForUserRoles(operation, applicationWrapper.getRoleNameList());
                } else if (applicationWrapper.getUserNameList() != null &&
                        applicationWrapper.getUserNameList().size() > 0) {
                    activity = appManagerConnector.installApplicationForUsers(operation, applicationWrapper.getUserNameList());
                } else if (applicationWrapper.getDeviceIdentifiers() != null &&
                        applicationWrapper.getDeviceIdentifiers().size() > 0) {
                    activity = appManagerConnector.installApplicationForDevices(operation, applicationWrapper.getDeviceIdentifiers());
                } else {
                    String msg = "No application un-installation criteria i.e. user/role/device is given";
                    return Response.status(Response.Status.BAD_REQUEST).entity(msg).build();
                }
            }
            return Response.status(Response.Status.ACCEPTED).entity(activity).build();
        } catch (ApplicationManagementException e) {
            String msg = "Error occurred while processing application un-installation request";
            log.error(msg, e);
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(msg).build()).build();
        } catch (UnknownApplicationTypeException e) {
            String msg = "The type of application requested to be un-installed is not supported";
            log.error(msg, e);
            return Response.serverError().entity(
                    new ErrorResponse.ErrorResponseBuilder().setMessage(msg).build()).build();
        }
    }

}
