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
package io.entgra.device.mgt.core.webapp.authenticator.framework.authenticator.oauth;

import io.entgra.device.mgt.core.webapp.authenticator.framework.authenticator.oauth.impl.LocalOAuthValidator;
import io.entgra.device.mgt.core.webapp.authenticator.framework.authenticator.oauth.impl.RemoteOAuthValidator;

import java.util.Properties;

/**
 * The class validate the configurations and provide the most suitable implementation according to the configuration.
 * Factory class for OAuthValidator.
 */
public class OAuthValidatorFactory {

    public static OAuth2TokenValidator getValidator(String url, String adminUsername, String adminPassword,
            boolean isRemote, Properties properties) throws IllegalArgumentException {
        if (isRemote) {
            if ((url != null) && (!url.trim().isEmpty())) {
                url = url + "/services/OAuth2TokenValidationService.OAuth2TokenValidationServiceHttpsSoap12Endpoint/";
                return new RemoteOAuthValidator(url, adminUsername, adminPassword, properties);
            }
            throw new IllegalArgumentException(
                    "Remote server host can't be empty in OAuthAuthenticator configuration.");
        }

        return new LocalOAuthValidator();
    }

}
