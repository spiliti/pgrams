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

package org.egov.pgr.elasticsearch.entity.contract;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.joda.time.DateTime;
import org.springframework.data.domain.PageRequest;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.egov.infra.config.core.ApplicationThreadLocals.getCityCode;
import static org.egov.infra.utils.ApplicationConstant.ES_DATE_FORMAT;
import static org.egov.infra.utils.DateUtils.endOfGivenDate;
import static org.egov.infra.utils.DateUtils.startOfGivenDate;
import static org.egov.infra.utils.DateUtils.toDateTimeUsingDefaultPattern;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

public class ComplaintSearchRequest {
    private static final int MAX_RESULT_SIZE = 10000;
    private static final int MIN_PAGE_SIZE = 0;
    private String fromDate;
    private String toDate;
    private boolean singleULB = true;
    private int page = MIN_PAGE_SIZE;
    private int size = MAX_RESULT_SIZE;
    private BoolQueryBuilder searchQueryBuilder;

    public ComplaintSearchRequest() {
        searchQueryBuilder = QueryBuilders.boolQuery().filter(QueryBuilders.matchAllQuery());
    }

    public void setComplaintNumber(String complaintNumber) {
        if (isNotBlank(complaintNumber))
            searchQueryBuilder.filter(matchQuery("crn", complaintNumber));
    }

    public void setComplaintStatus(String complaintStatus) {
        if (isNotBlank(complaintStatus))
            searchQueryBuilder.filter(matchQuery("complaintStatusName", complaintStatus));
    }

    public void setComplainantName(String complainantName) {
        if (isNotBlank(complainantName))
            searchQueryBuilder.filter(matchQuery("complainantName", complainantName));
    }

    public void setLocation(String location) {
        if (isNotBlank(location))
            searchQueryBuilder.filter(matchQuery("wardName", location));
    }

    public void setComplainantPhoneNumber(String phoneNumber) {
        if (isNotBlank(phoneNumber))
            searchQueryBuilder.filter(matchQuery("complainantMobile", phoneNumber));
    }

    public void setComplainantEmail(String email) {
        if (isNotBlank(email))
            searchQueryBuilder.filter(matchQuery("complainantEmail", email));
    }

    public void setReceivingMode(String receivingMode) {
        if (isNotBlank(receivingMode))
            searchQueryBuilder.filter(matchQuery("receivingMode", receivingMode));
    }

    public void setComplaintType(String complaintType) {
        if (isNotBlank(complaintType))
            searchQueryBuilder.filter(matchQuery("complaintTypeName", complaintType));
    }

    public void setFromDate(String fromDate) {
        if (fromDate != null)
            this.fromDate = startOfGivenDate(toDateTimeUsingDefaultPattern(fromDate)).toString(ES_DATE_FORMAT);
    }

    public void setToDate(String toDate) {
        if (toDate != null)
            this.toDate = endOfGivenDate(toDateTimeUsingDefaultPattern(toDate)).toString(ES_DATE_FORMAT);
    }

    public void setComplaintDepartment(String complaintDepartment) {
        if (isNotBlank(complaintDepartment))
            searchQueryBuilder.filter(matchQuery("departmentName", complaintDepartment));
    }

    public void setSingleULB(boolean singleULB) {
        this.singleULB = singleULB;
    }

    public void setPage(int page) {
        this.page = page < MIN_PAGE_SIZE ? MIN_PAGE_SIZE : page;
    }

    public void setSize(int size) {
        this.size = size > MAX_RESULT_SIZE ? MAX_RESULT_SIZE : size;
    }

    public PageRequest getPageRequest() {
        return new PageRequest(page, size);
    }

    public void setComplaintDate(String complaintDate) {
        if (complaintDate != null) {
            DateTime currentDate = new DateTime();
            this.toDate = endOfGivenDate(currentDate).toString(ES_DATE_FORMAT);
            if ("today".equalsIgnoreCase(complaintDate)) {
                this.fromDate = currentDate.withTimeAtStartOfDay().toString(ES_DATE_FORMAT);
            } else if ("lastsevendays".equalsIgnoreCase(complaintDate)) {
                this.fromDate = currentDate.minusDays(7).toString(ES_DATE_FORMAT);
            } else if ("lastthirtydays".equalsIgnoreCase(complaintDate)) {
                this.fromDate = currentDate.minusDays(30).toString(ES_DATE_FORMAT);
            } else if ("lastninetydays".equalsIgnoreCase(complaintDate)) {
                this.fromDate = currentDate.minusDays(90).toString(ES_DATE_FORMAT);
            } else {
                this.fromDate = null;
                this.toDate = null;
            }
        }

    }

    public BoolQueryBuilder query() {
        if (singleULB)
            searchQueryBuilder.filter(matchQuery("cityCode", getCityCode()));
        if (isNotBlank(this.fromDate) || isNotBlank(this.toDate))
            searchQueryBuilder.must(rangeQuery("createdDate")
                    .from(this.fromDate).to(this.toDate));

        return searchQueryBuilder;
    }
}