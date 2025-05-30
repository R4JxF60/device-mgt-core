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

package io.entgra.device.mgt.core.policy.mgt.core.dao.impl.feature;

import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.ProfileFeature;
import io.entgra.device.mgt.core.policy.mgt.core.dao.FeatureManagerDAOException;
import io.entgra.device.mgt.core.policy.mgt.core.dao.PolicyManagementDAOFactory;
import io.entgra.device.mgt.core.policy.mgt.core.dao.util.PolicyManagementDAOUtil;
import io.entgra.device.mgt.core.policy.mgt.core.util.PolicyManagerUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * FeatureDAO implementation for MSSQL DB engine.
 */
public final class SQLServerFeatureDAOImpl extends AbstractFeatureDAO {

    private static final Log log = LogFactory.getLog(SQLServerFeatureDAOImpl.class);

    @Override
    public List<ProfileFeature> addProfileFeatures(List<ProfileFeature> features, int profileId) throws
                                                                                                 FeatureManagerDAOException {

        Connection conn;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;
        int tenantId = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId();

        try {
            conn = this.getConnection();
            String query = "INSERT INTO DM_PROFILE_FEATURES (PROFILE_ID, FEATURE_CODE, DEVICE_TYPE, CONTENT, " +
                           "TENANT_ID) VALUES (?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(query, new String[] {"id"});

            for (ProfileFeature feature : features) {
                stmt.setInt(1, profileId);
                stmt.setString(2, feature.getFeatureCode());
                stmt.setString(3, feature.getDeviceType());
                // if (conn.getMetaData().getDriverName().contains("H2")) {
                //    stmt.setBytes(4, PolicyManagerUtil.getBytes(feature.getContent()));
                // } else {
                stmt.setBytes(4, PolicyManagerUtil.getBytes(feature.getContent()));
                //}
                stmt.setInt(5, tenantId);
                stmt.addBatch();
                //Not adding the logic to check the size of the stmt and execute if the size records added is over 1000
            }
            stmt.executeBatch();
// This logic has been commented out due to getGeneratedKeys method is not supported in MSSQL.
//            generatedKeys = stmt.getGeneratedKeys();
//            int i = 0;
//
//            while (generatedKeys.next()) {
//                features.get(i).setId(generatedKeys.getInt(1));
//                i++;
//            }

        } catch (SQLException | IOException e) {
            throw new FeatureManagerDAOException("Error occurred while adding the feature list to the database.", e);
        } finally {
            PolicyManagementDAOUtil.cleanupResources(stmt, generatedKeys);
        }
        return features;
    }

    private Connection getConnection() throws FeatureManagerDAOException {
        return PolicyManagementDAOFactory.getConnection();
    }

}
