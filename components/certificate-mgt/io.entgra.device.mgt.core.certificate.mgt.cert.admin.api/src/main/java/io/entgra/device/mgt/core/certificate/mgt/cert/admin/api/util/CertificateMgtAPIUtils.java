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

package io.entgra.device.mgt.core.certificate.mgt.cert.admin.api.util;

import io.entgra.device.mgt.core.certificate.mgt.core.scep.SCEPManager;
import io.entgra.device.mgt.core.certificate.mgt.core.service.CertificateManagementService;
import io.entgra.device.mgt.core.device.mgt.core.search.mgt.SearchManagerService;
import io.entgra.device.mgt.core.identity.jwt.client.extension.service.JWTClientManagerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;

import javax.ws.rs.core.MediaType;

/**
 * CertificateMgtAPIUtils class provides utility functions used by Certificate Mgt REST-API classes.
 */
public class CertificateMgtAPIUtils {

    public static final MediaType DEFAULT_CONTENT_TYPE = MediaType.APPLICATION_JSON_TYPE;
    private static Log log = LogFactory.getLog(CertificateMgtAPIUtils.class);

    public static CertificateManagementService getCertificateManagementService() {

        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        CertificateManagementService certificateManagementService = (CertificateManagementService)
                ctx.getOSGiService(CertificateManagementService.class, null);

        if (certificateManagementService == null) {
            String msg = "CertificateManagementAdminServiceImpl Management service not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }

        return certificateManagementService;
    }


    public static JWTClientManagerService getJwtClientManagerService() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        JWTClientManagerService jwtClientManagerService = (JWTClientManagerService)
                ctx.getOSGiService(JWTClientManagerService.class, null);

        if (jwtClientManagerService == null) {
            String msg = "JWTClientManagerService Management service not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }

        return jwtClientManagerService;
    }


    public static SCEPManager getSCEPManagerService() {

        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        SCEPManager scepManagerService = (SCEPManager)
                ctx.getOSGiService(SCEPManager.class, null);

        if (scepManagerService == null) {
            String msg = "SCEPManagerImpl Management service not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }

        return scepManagerService;
    }


    public static MediaType getResponseMediaType(String acceptHeader) {
        MediaType responseMediaType;
        if (acceptHeader == null || MediaType.WILDCARD.equals(acceptHeader)) {
            responseMediaType = DEFAULT_CONTENT_TYPE;
        } else {
            responseMediaType = MediaType.valueOf(acceptHeader);
        }

        return responseMediaType;
    }

    public static SearchManagerService getSearchManagerService() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        SearchManagerService searchManagerService =
                (SearchManagerService) ctx.getOSGiService(SearchManagerService.class, null);
        if (searchManagerService == null) {
            String msg = "DeviceImpl search manager service has not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return searchManagerService;
    }
}
