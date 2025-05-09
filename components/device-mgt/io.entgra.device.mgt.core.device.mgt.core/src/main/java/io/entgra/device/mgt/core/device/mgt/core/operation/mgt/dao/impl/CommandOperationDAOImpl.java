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

package io.entgra.device.mgt.core.device.mgt.core.operation.mgt.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import io.entgra.device.mgt.core.device.mgt.core.dao.util.DeviceManagementDAOUtil;
import io.entgra.device.mgt.core.device.mgt.core.dto.operation.mgt.CommandOperation;
import io.entgra.device.mgt.core.device.mgt.core.dto.operation.mgt.Operation;
import io.entgra.device.mgt.core.device.mgt.core.operation.mgt.dao.OperationManagementDAOException;
import io.entgra.device.mgt.core.device.mgt.core.operation.mgt.dao.OperationManagementDAOFactory;
import io.entgra.device.mgt.core.device.mgt.core.operation.mgt.dao.OperationManagementDAOUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommandOperationDAOImpl extends GenericOperationDAOImpl {
    private static final Log log = LogFactory.getLog(CommandOperationDAOImpl.class);

    @Override
    public int addOperation(Operation operation) throws OperationManagementDAOException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            Connection connection = OperationManagementDAOFactory.getConnection();
            String sql = "INSERT INTO DM_OPERATION(TYPE, CREATED_TIMESTAMP, RECEIVED_TIMESTAMP, OPERATION_CODE, " +
                    "INITIATED_BY, ENABLED, TENANT_ID) VALUES (?, ?, ?, ?, ?, ?, ?)";
            stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, operation.getType().toString());
            stmt.setLong(2, DeviceManagementDAOUtil.getCurrentUTCTime());
            stmt.setLong(3, 0);
            stmt.setString(4, operation.getCode());
            stmt.setString(5, operation.getInitiatedBy());
            stmt.setBoolean(6, operation.isEnabled());
            stmt.setInt(7, PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId());
            stmt.executeUpdate();

            rs = stmt.getGeneratedKeys();
            int id = -1;
            if (rs.next()) {
                id = rs.getInt(1);
            }
            return id;
        } catch (SQLException e) {
            throw new OperationManagementDAOException("Error occurred while adding command operation", e);
        } finally {
            OperationManagementDAOUtil.cleanupResources(stmt, rs);
        }
    }

    public CommandOperation getOperation(int id) throws OperationManagementDAOException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        CommandOperation commandOperation = null;
        try {
            Connection conn = OperationManagementDAOFactory.getConnection();
            String sql = "SELECT ID, ENABLED FROM DM_OPERATION WHERE ID = ? AND TYPE='COMMAND'";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                commandOperation = new CommandOperation();
                commandOperation.setId(rs.getInt("ID"));
                commandOperation.setEnabled(rs.getBoolean("ENABLED"));
            }
        } catch (SQLException e) {
            throw new OperationManagementDAOException("SQL Error occurred while retrieving the command operation " +
                    "object available for the id '" + id, e);
        } finally {
            OperationManagementDAOUtil.cleanupResources(stmt, rs);
        }
        return commandOperation;
    }

    @Override
    public List<? extends Operation> getOperationsByDeviceAndStatus(
            int enrolmentId, Operation.Status status) throws OperationManagementDAOException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        CommandOperation commandOperation;
        List<CommandOperation> commandOperations = new ArrayList<>();
        try {
            Connection conn = OperationManagementDAOFactory.getConnection();
            String sql = "SELECT co1.ID, co1.ENABLED, co1.STATUS, co1.TYPE, co1.CREATED_TIMESTAMP, co1.RECEIVED_TIMESTAMP, " +
                    "co1.OPERATION_CODE FROM (SELECT co.ID, co.TYPE, co.CREATED_TIMESTAMP, co.RECEIVED_TIMESTAMP, co.OPERATION_CODE, co.ENABLED, dm.STATUS " +
                    "FROM DM_OPERATION co INNER JOIN (SELECT ENROLMENT_ID, OPERATION_ID, STATUS " +
                    "FROM DM_ENROLMENT_OP_MAPPING WHERE ENROLMENT_ID = ? AND STATUS = ?) dm " +
                    "ON dm.OPERATION_ID = co.ID and co.TYPE='COMMAND') co1";

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, enrolmentId);
            stmt.setString(2, status.toString());

            rs = stmt.executeQuery();
            while (rs.next()) {
                commandOperation = new CommandOperation();
                commandOperation.setId(rs.getInt("ID"));
                //commandOperation.setEnabled(rs.getInt("ENABLED") != 0);
                commandOperation.setEnabled(rs.getBoolean("ENABLED") != false);
                commandOperation.setStatus(Operation.Status.valueOf(rs.getString("STATUS")));
                commandOperation.setType(Operation.Type.valueOf(rs.getString("TYPE")));
                commandOperation.setCreatedTimeStamp(rs.getString("CREATED_TIMESTAMP"));
                commandOperation.setReceivedTimeStamp(rs.getString("RECEIVED_TIMESTAMP"));
                commandOperation.setCode(rs.getString("OPERATION_CODE"));
                commandOperations.add(commandOperation);
            }
        } catch (SQLException e) {
            throw new OperationManagementDAOException("SQL error occurred while retrieving the operation available " +
                    "for the device'" + enrolmentId + "' with status '" + status.toString(), e);
        } finally {
            OperationManagementDAOUtil.cleanupResources(stmt, rs);
        }
        return commandOperations;
    }

}
