/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2018  eGovernments Foundation
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

package org.egov.stms.transactions.service;

import org.apache.commons.lang.StringUtils;
import org.egov.eis.entity.Assignment;
import org.egov.eis.service.AssignmentService;
import org.egov.eis.service.DesignationService;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.pims.commons.Designation;
import org.egov.pims.commons.Position;
import org.egov.stms.entity.SewerageReassignDetails;
import org.egov.stms.transactions.entity.SewerageApplicationDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.egov.stms.utils.constants.SewerageTaxConstants.APPCONFKEY_REASSIGN_BUTTONENABLED;
import static org.egov.stms.utils.constants.SewerageTaxConstants.MODULE_NAME;

@Service
public class SewerageReassignService {

    private static final String NEWSEWERAGECONNECTION = "NEWSEWERAGECONNECTION";

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DesignationService designationService;

    @Autowired
    private SewerageApplicationDetailsService sewerageApplicationDetailsService;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private SewerageWorkflowService sewerageWorkflowService;

    @Autowired
    private AppConfigValueService appConfigValuesService;

    public User getLoggedInUser() {
        return securityUtils.getCurrentUser();
    }

    public Map<String, String> getEmployees() {

        final String designationStr = sewerageWorkflowService.getDesignationForCscOperatorWorkFlow();
        final String departmentStr = sewerageWorkflowService.getDepartmentForReassignment();
        List<String> departmentCodes = departmentService.getDepartmentsByNames(Arrays.asList(departmentStr.split(",")))
                .stream().map(Department::getCode).collect(Collectors.toList());
        List<String> designationCodes = designationService.getDesignationsByNames(Arrays.asList(StringUtils
                .upperCase(designationStr).split(",")))
                .stream().map(Designation::getCode).collect(Collectors.toList());
        List<Assignment> assignments = new ArrayList<>();
        departmentCodes.stream().forEach(deptCode -> assignments.addAll(assignmentService
                .findByDepartmentCodeAndDesignationCode(deptCode, designationCodes)));
        assignments.removeAll(assignmentService.getAllAssignmentsByEmpId(ApplicationThreadLocals.getUserId()));
        return assignments
                .stream()
                .collect(Collectors.toMap(assignment -> new StringBuffer().append(assignment.getId())
                                .append("-").append(assignment.getPosition().getId()).toString(),
                        assignment -> new StringBuffer().append(assignment.getEmployee().getName())
                                .append("-").append(assignment.getPosition().getName()).toString()));
    }

    public String getSewerageApplication(final SewerageReassignDetails sewerageReassignDetails, final Position position) {
        SewerageApplicationDetails sewerageApplicationDetails;
        String applicationType = sewerageReassignDetails.getStateType();
        if (NEWSEWERAGECONNECTION.equalsIgnoreCase(applicationType)) {
            sewerageApplicationDetails = sewerageApplicationDetailsService
                    .findByApplicationNumber(sewerageReassignDetails.getApplicationNo());
            sewerageApplicationDetails.changeProcessInitiator(position);
            sewerageApplicationDetails.changeProcessOwner(position);
            sewerageApplicationDetailsService.updateSewerageApplicationDetails(sewerageApplicationDetails);
            sewerageApplicationDetailsService.updateIndexes(sewerageApplicationDetails);
            return sewerageApplicationDetails.getApplicationNumber();
        } else {
            return StringUtils.EMPTY;
        }
    }

    public boolean isReassignEnabled() {
        final List<AppConfigValues> appConfigValues = appConfigValuesService.getConfigValuesByModuleAndKey(
                MODULE_NAME,
                APPCONFKEY_REASSIGN_BUTTONENABLED);
        return !appConfigValues.isEmpty() && "YES".equals(appConfigValues.get(0).getValue()) ? true : false;
    }

}
