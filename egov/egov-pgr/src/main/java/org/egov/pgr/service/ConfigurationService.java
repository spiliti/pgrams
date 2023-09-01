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

package org.egov.pgr.service;

import org.egov.pgr.entity.Configuration;
import org.egov.pgr.entity.Priority;
import org.egov.pgr.repository.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ConfigurationService {

    private static final String DEFAULT_RESOLUTION_TIME_KEY = "DEFAULT_RESOLUTION_SLA_IN_HOURS";
    private static final String DEFAULT_COMPLAINT_PRIORITY = "DEFAULT_COMPLAINT_PRIORITY";
    private static final String USE_AUTO_COMPLETE_FOR_COMPLAINT_TYPE = "USE_AUTO_COMPLETE_FOR_COMPLAINT_TYPE";
    private static final String ASSIGN_REOPENED_COMPLAINT_BASEDON_ROUTER_POSITION = "ASSIGN_REOPENED_COMPLAINT_BASEDON_ROUTER_POSITION";
    private static final String SEND_MESSAGE_ON_ESCALATION = "SEND_MESSAGE_ON_ESCALATION";

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private PriorityService priorityService;

    public Integer getDefaultComplaintResolutionTime() {
        return Integer.valueOf(getValueByKey(DEFAULT_RESOLUTION_TIME_KEY));
    }

    public Priority getDefaultComplaintPriority() {
        return priorityService.getPriorityByCode(getValueByKey(DEFAULT_COMPLAINT_PRIORITY));
    }

    public boolean useAutoCompleteForComplaintType() {
        return Boolean.valueOf(getValueByKey(USE_AUTO_COMPLETE_FOR_COMPLAINT_TYPE));
    }

    public boolean assignReopenedComplaintBasedOnRouterPosition() {
        return Boolean.valueOf(getValueByKey(ASSIGN_REOPENED_COMPLAINT_BASEDON_ROUTER_POSITION));
    }

    public boolean sendMessageOnEscalation() {
        return Boolean.valueOf(getValueByKey(SEND_MESSAGE_ON_ESCALATION));
    }

    public String getValueByKey(String key) {
        return getConfigurationByKey(key).getValue();
    }

    public Configuration getConfigurationByKey(String key) {
        return configurationRepository.findByKey(key);
    }
}
