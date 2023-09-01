/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */

package org.egov.wtms.reports.entity;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class DCBReportHelperAdaptor implements JsonSerializer<DCBReportResult> {

    @Override
    public JsonElement serialize(final DCBReportResult dCBReportObj, final Type type, final JsonSerializationContext jsc) {
        final JsonObject jsonObject = new JsonObject();
        if (dCBReportObj != null) {
            jsonObject.addProperty("boundaryName", dCBReportObj.getBoundaryName());
            jsonObject.addProperty("id", dCBReportObj.getId());
            jsonObject.addProperty("boundaryId", dCBReportObj.getBoundaryId());
            jsonObject.addProperty("propertyid", dCBReportObj.getPropertyId());
            jsonObject.addProperty("address", dCBReportObj.getAddress());
            jsonObject.addProperty("hscno", dCBReportObj.getHscNo());
            jsonObject.addProperty("username", dCBReportObj.getUserName());
            jsonObject.addProperty("zoneid", dCBReportObj.getZoneId());
            jsonObject.addProperty("wardid", dCBReportObj.getWardId());
            jsonObject.addProperty("block", dCBReportObj.getBlock());
            jsonObject.addProperty("locality", dCBReportObj.getLocality());
            jsonObject.addProperty("street", dCBReportObj.getStreet());
            jsonObject.addProperty("connectiontype", dCBReportObj.getConnectionType());

            jsonObject.addProperty("curr_demand", dCBReportObj.getCurrDemand());
            jsonObject.addProperty("arr_demand", dCBReportObj.getArrDemand());
            jsonObject.addProperty("no_of_users", dCBReportObj.getCountOfConsumerNo());
            jsonObject.addProperty("total_demand", dCBReportObj.getTotalDemand());

            jsonObject.addProperty("curr_coll", dCBReportObj.getCurrColl());
            jsonObject.addProperty("arr_coll", dCBReportObj.getArrColl());
            jsonObject.addProperty("total_coll", dCBReportObj.getTotalColl());

            jsonObject.addProperty("curr_balance", dCBReportObj.getCurrBalance());
            jsonObject.addProperty("arr_balance", dCBReportObj.getArrBalance());
            jsonObject.addProperty("total_balance", dCBReportObj.getTotalBalance());

        }
        return jsonObject;
    }

}
