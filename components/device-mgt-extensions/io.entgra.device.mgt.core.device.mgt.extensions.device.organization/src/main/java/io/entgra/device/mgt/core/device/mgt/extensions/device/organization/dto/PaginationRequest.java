/*
 * Copyright (c) 2018 - 2024, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
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

package io.entgra.device.mgt.core.device.mgt.extensions.device.organization.dto;

/**
 * This class holds required parameters for a querying a paginated device response.
 */
public class PaginationRequest {

    private int offSet;
    private int limit;

    public PaginationRequest(int start, int limit) {
        this.offSet = start;
        this.limit = limit;
    }

    public int getOffSet() {
        return offSet;
    }

    public void setOffSet(int offSet) {
        this.offSet = offSet;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public boolean validatePaginationRequest(int offSet, int limit) {
        if (offSet < 0) {
            throw new IllegalArgumentException("off set value can't be negative");
        } else if (limit < 0) {
            throw new IllegalArgumentException("limit value can't be negative");
        } else {
            return true;
        }
    }

    @Override
    public String toString() {
        return "Off Set'" + this.offSet + "' row count '" + this.limit;
    }
}
