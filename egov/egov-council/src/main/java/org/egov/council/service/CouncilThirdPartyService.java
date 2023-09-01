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
 */
package org.egov.council.service;

import org.egov.council.entity.CouncilPreamble;
import org.egov.eis.entity.Assignment;
import org.egov.eis.service.AssignmentService;
import org.egov.eis.service.EisCommonService;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.workflow.entity.State;
import org.egov.infra.workflow.entity.StateHistory;
import org.egov.pims.commons.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CouncilThirdPartyService {
    private static final String DEPARTMENT = "department";
    @Autowired
    private EisCommonService eisCommonService;
    @Autowired
    private AssignmentService assignmentService;

    public List<HashMap<String, Object>> getHistory(final CouncilPreamble councilPreamble) {
        User userObject;
        Assignment primaryAssignment;
        final List<HashMap<String, Object>> historyTable = new ArrayList<>();
        final State<Position> workflowState = councilPreamble.getState();
        final HashMap<String, Object> workFlowHistory = new HashMap<>();
        if (null != workflowState) {
            if (null != councilPreamble.getStateHistory() && !councilPreamble.getStateHistory().isEmpty()) {
                Collections.reverse(councilPreamble.getStateHistory());
            }

            for (final StateHistory<Position> stateHistory : councilPreamble.getStateHistory()) {
                final HashMap<String, Object> historyMap = new HashMap<>();
                historyMap.put("date", stateHistory.getDateInfo());
                historyMap.put("comments", stateHistory.getComments());
                historyMap.put("updatedBy", stateHistory.getLastModifiedBy().getUsername() + "::"
                        + stateHistory.getLastModifiedBy().getName());
                historyMap.put("status", stateHistory.getValue());
                final Position owner = stateHistory.getOwnerPosition();
                userObject = stateHistory.getOwnerUser();
                if (null != userObject) {
                    historyMap.put("user", userObject.getUsername() + "::" + userObject.getName());
                    historyMap.put(DEPARTMENT,
                            null != eisCommonService.getDepartmentForUser(userObject.getId()) ? eisCommonService
                                    .getDepartmentForUser(userObject.getId()).getName() : "");
                } else if (null != owner && null != owner.getDeptDesig()) {
                    primaryAssignment=assignmentService.getPrimaryAssignmentForPositon(owner.getId());
                    if(primaryAssignment!=null) {
                        userObject=primaryAssignment.getEmployee();
                    } else{
                        userObject = eisCommonService.getUserForPosition(owner.getId(), new Date());
                    }   
                    historyMap
                            .put("user", null != userObject.getUsername() ? userObject.getUsername() + "::" + userObject.getName()
                                    : "");
                    historyMap.put(DEPARTMENT, null != owner.getDeptDesig().getDepartment() ? owner.getDeptDesig()
                            .getDepartment().getName() : "");
                }
                historyTable.add(historyMap);
            }

            workFlowHistory.put("date", workflowState.getDateInfo());
            workFlowHistory.put("comments", workflowState.getComments() != null ? workflowState.getComments() : "");
            workFlowHistory.put("updatedBy",
                    workflowState.getLastModifiedBy().getUsername() + "::" + workflowState.getLastModifiedBy().getName());
            workFlowHistory.put("status", workflowState.getValue());
            final Position ownerPosition = workflowState.getOwnerPosition();
            userObject = workflowState.getOwnerUser();
            if (null != userObject) {
                workFlowHistory.put("user", userObject.getUsername() + "::" + userObject.getName());
                workFlowHistory.put(DEPARTMENT,
                        null != eisCommonService.getDepartmentForUser(userObject.getId()) ? eisCommonService
                                .getDepartmentForUser(userObject.getId()).getName() : "");
            } else if (null != ownerPosition && null != ownerPosition.getDeptDesig()) {
                primaryAssignment = assignmentService.getPrimaryAssignmentForPositon(ownerPosition.getId());
                if (primaryAssignment != null) {
                    userObject = primaryAssignment.getEmployee();
                } else {
                    userObject = eisCommonService.getUserForPosition(ownerPosition.getId(), new Date());
                }
                workFlowHistory.put("user",
                        null != userObject.getUsername() ? userObject.getUsername() + "::" + userObject.getName() : "");
                workFlowHistory.put(DEPARTMENT, null != ownerPosition.getDeptDesig().getDepartment() ? ownerPosition
                        .getDeptDesig().getDepartment().getName() : "");
            }
            historyTable.add(workFlowHistory);
        }
        return historyTable;
    }
}
