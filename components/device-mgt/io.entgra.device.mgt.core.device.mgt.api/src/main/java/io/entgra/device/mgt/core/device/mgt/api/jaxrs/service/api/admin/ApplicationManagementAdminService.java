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
package io.entgra.device.mgt.core.device.mgt.api.jaxrs.service.api.admin;

import io.entgra.device.mgt.core.apimgt.annotations.Scope;
import io.entgra.device.mgt.core.apimgt.annotations.Scopes;
import io.entgra.device.mgt.core.device.mgt.api.jaxrs.beans.ApplicationWrapper;
import io.entgra.device.mgt.core.device.mgt.api.jaxrs.beans.ErrorResponse;
import io.entgra.device.mgt.core.device.mgt.api.jaxrs.util.Constants;
import io.entgra.device.mgt.core.device.mgt.common.operation.mgt.Activity;
import io.swagger.annotations.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@SwaggerDefinition(
        info = @Info(
                version = "1.0.0",
                title = "",
                extensions = {
                        @Extension(properties = {
                                @ExtensionProperty(name = "name", value = "ApplicationManagementAdmin"),
                                @ExtensionProperty(name = "context", value = "/api/device-mgt/v1.0/admin/applications"),
                        })
                }
        ),
        tags = {
                @Tag(name = "device_management", description = "")
        }
)
@Path("/admin/applications")
@Api(value = "Application Management Administrative Service", description = "This an  API intended to be used by " +
        "'internal' components to log in as an admin user and do a selected number of operations. " +
        "Further, this is strictly restricted to admin users only ")
@Scopes(
        scopes = {
                @Scope(
                        name = "Installing an Application (Internal API)",
                        description = "Installing an Application (Internal API)",
                        key = "am:admin:app:install",
                        roles = {"Internal/devicemgt-admin"},
                        permissions = {"/device-mgt/admin/applications/install"}
                ),
                @Scope(
                        name = "Uninstalling an Application (Internal API)",
                        description = "Uninstalling an Application (Internal API)",
                        key = "am:admin:app:uninstall",
                        roles = {"Internal/devicemgt-admin"},
                        permissions = {"/device-mgt/admin/applications/uninstall"}
                )
        }
)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ApplicationManagementAdminService {

    @POST
    @Path("/install-application")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Installing an Application (Internal API)",
            notes = "This is an internal API that can be used to install an application on a device.",
            response = Activity.class,
            tags = "Application Management Administrative Service",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = Constants.SCOPE, value = "am:admin:app:install")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 202,
                    message = "Accepted. \n The install application operation will be delivered to the specified devices",
                    response = Activity.class),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request. \n Invalid request or validation error.",
                    response = ErrorResponse.class),
            @ApiResponse(
                    code = 404,
                    message = "Not Found. \n The specified resource does not exist."),
            @ApiResponse(
                    code = 415,
                    message = "Unsupported media type. \n The format of the requested entity was not supported."),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. \n " +
                            "Server error occurred while executing the application install operation in bulk" +
                            " for a specified set of devices.",
                    response = ErrorResponse.class)
    })
    Response installApplication(
            @ApiParam(
                    name = "applicationWrapper",
                    value = "Application details of the application to be installed.",
                    required = true) ApplicationWrapper applicationWrapper);

    @POST
    @Path("/uninstall-application")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            produces = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Uninstalling an Application (Internal API)\n",
            notes = "This is an internal API that can be used to uninstall an application.",
            response = Activity.class,
            tags = "Application Management Administrative Service",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = Constants.SCOPE, value = "am:admin:app:uninstall")
                    })
            }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 202,
                    message = "Accepted. \n The uninstall application operation will be delivered to the provided devices",
                    response = Activity.class),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request. \n Invalid request or validation error.",
                    response = ErrorResponse.class),
            @ApiResponse(
                    code = 404,
                    message = "Not Found. \n The specified resource does not exist."),
            @ApiResponse(
                    code = 415,
                    message = "Unsupported media type. \n The entity of the request was in a not supported format."),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error. \n Server error occurred while executing the application install" +
                            " operation in bulk for a specified set of devices.",
                    response = ErrorResponse.class)
    })
    Response uninstallApplication(
            @ApiParam(
                    name = "applicationWrapper",
                    value = "Application details of the application to be uninstalled.",
                    required = true) ApplicationWrapper applicationWrapper);

}
