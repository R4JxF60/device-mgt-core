/*
 * Copyright (c) 2019, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.entgra.device.mgt.core.application.mgt.store.api.services.impl.admin;

import io.entgra.device.mgt.core.application.mgt.common.exception.SubscriptionManagementException;
import io.entgra.device.mgt.core.application.mgt.store.api.beans.SubscriptionStatusBean;
import io.entgra.device.mgt.core.application.mgt.store.api.services.admin.SubscriptionManagementAdminAPI;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import io.entgra.device.mgt.core.application.mgt.common.exception.ApplicationManagementException;
import io.entgra.device.mgt.core.application.mgt.common.services.SubscriptionManager;
import io.entgra.device.mgt.core.application.mgt.core.exception.BadRequestException;
import io.entgra.device.mgt.core.application.mgt.core.exception.NotFoundException;
import io.entgra.device.mgt.core.application.mgt.core.util.APIUtil;
import io.entgra.device.mgt.core.application.mgt.store.api.services.impl.util.RequestValidationUtil;
import io.entgra.device.mgt.core.device.mgt.common.PaginationRequest;
import io.entgra.device.mgt.core.device.mgt.common.PaginationResult;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Implementation of Subscription Management related APIs.
 */
@Produces({"application/json"})
@Path("/admin/subscription")
public class SubscriptionManagementAdminAPIImpl implements SubscriptionManagementAdminAPI {

    private static final Log log = LogFactory.getLog(SubscriptionManagementAdminAPIImpl.class);

    @Override
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/device/{deviceId}/status")
    public Response updateSubscription(
            @PathParam("deviceId") int deviceId,
            SubscriptionStatusBean subscriptionStatusBean
    ) {
        try {
            RequestValidationUtil.validateSubscriptionStatus(subscriptionStatusBean.getStatus());
            SubscriptionManager subscriptionManager = APIUtil.getSubscriptionManager();
            subscriptionManager.updateSubscriptionStatus(deviceId, subscriptionStatusBean.getSubId(),
                    subscriptionStatusBean.getStatus());
            return Response.status(Response.Status.OK).entity("Subscription status updated successfully").build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (SubscriptionManagementException e) {
            String msg = "Error occurred while changing subscription status of the subscription with the id "
                    + subscriptionStatusBean.getSubId();
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).build();
        }
    }

    @GET
    @Consumes("application/json")
    @Produces("application/json")
    @Path("/{uuid}")
    public Response getAppInstalledDevices(
            @QueryParam("name") String name,
            @QueryParam("user") String user,
            @QueryParam("action") String action,
            @QueryParam("actionStatus") String actionStatus,
            @QueryParam("status") List<String> status,
            @QueryParam("installedVersion") String installedVersion,
            @PathParam("uuid") String uuid,
            @DefaultValue("0")
            @QueryParam("offset") int offset,
            @DefaultValue("5")
            @QueryParam("limit") int limit) {

        try {
            PaginationRequest request = new PaginationRequest(offset, limit);
            if (name != null && !name.isEmpty()) {
                request.setDeviceName(name);
            }
            if (user != null && !user.isEmpty()) {
                request.setOwner(user);
            }
            if (action != null && !action.isEmpty()) {
                RequestValidationUtil.validateAction(action);
            }
            if (status != null && !status.isEmpty()) {
                boolean isStatusEmpty = true;
                for (String statusString : status) {
                    if (StringUtils.isNotBlank(statusString)) {
                        isStatusEmpty = false;
                        break;
                    }
                }
                if (!isStatusEmpty) {
                    RequestValidationUtil.validateStatus(status);
                    request.setStatusList(status);
                }
            }
            if (actionStatus != null && !actionStatus.isEmpty()) {
                if (StringUtils.isNotBlank(actionStatus)) {
                    RequestValidationUtil.validateStatusFiltering(actionStatus);
                }
            }
            SubscriptionManager subscriptionManager = APIUtil.getSubscriptionManager();
            PaginationResult subscriptionData = subscriptionManager.getAppSubscriptionDetails
                    (request, uuid, actionStatus, action, installedVersion);
            return Response.status(Response.Status.OK).entity(subscriptionData).build();
        } catch (NotFoundException e) {
            String msg = "Application with application release UUID: " + uuid + " is not found";
            log.error(msg, e);
            return Response.status(Response.Status.NOT_FOUND).entity(msg).build();
        } catch (BadRequestException e) {
            String msg = "User requested details are not valid. Please verify the request payload.";
            log.error(msg, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).build();
        } catch (ApplicationManagementException e) {
            String msg = "Error occurred while getting app installed devices which has application release UUID of: "
                    + uuid;
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).build();
        }
    }
}