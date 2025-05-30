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
package io.entgra.device.mgt.core.apimgt.webapp.publisher.utils;

import io.entgra.device.mgt.core.apimgt.webapp.publisher.APIConfig;
import io.entgra.device.mgt.core.apimgt.webapp.publisher.dto.ApiScope;
import io.entgra.device.mgt.core.apimgt.webapp.publisher.dto.ApiUriTemplate;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Contains util methods for webAppPublisher tests.
 */
public class TestUtils {

    public void setAPIURITemplates(APIConfig apiConfig, String uriTemplate) {
        Set<ApiUriTemplate> uriTemplates = new LinkedHashSet<>();
        ApiUriTemplate template = new ApiUriTemplate();
        template.setAuthType("Application & Application User");
        template.setHttpVerb("POST");
        template.setResourceURI("https://localhost:9443/api/device-mgt/windows/v1.0/admin/devices/reboot");
        template.setUriTemplate(uriTemplate);
        ApiScope scope = new ApiScope();
        scope.setKey("win:ops:reboot");
        scope.setName("Reboot");
        scope.setPermissions("/permission/admin/device-mgt/devices/owning-device/operations/windows/reboot");
        scope.setDescription("Lock reset on Windows devices");
        template.setScope(scope);
        uriTemplates.add(template);
        apiConfig.setUriTemplates(uriTemplates);
    }
}
