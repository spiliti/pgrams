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
package org.egov.stms.web.controller.transactions;

import org.apache.commons.lang3.StringUtils;
import org.egov.eis.entity.Assignment;
import org.egov.eis.service.AssignmentService;
import org.egov.eis.service.PositionMasterService;
import org.egov.pims.commons.Position;
import org.egov.stms.entity.SewerageReassignDetails;
import org.egov.stms.transactions.service.SewerageReassignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

@Controller
@RequestMapping(value = "/reassignseweragetax/{applicationNum}/{appType}")
public class SewerageReassignController {

    private static final String REASSIGN_SUCCESS = "reassign-success";

    private static final String SEWERAGE_REASSIGN = "sewerage-reassign";

    private static final String SUCCESSMESSAGE = "successMessage";

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private SewerageReassignService sewerageReassignService;

    @Autowired
    private PositionMasterService positionMasterService;

    @ModelAttribute
    public SewerageReassignDetails reassign() {
        return new SewerageReassignDetails();
    }

    @RequestMapping(method = RequestMethod.GET)
    public String getReassign(@ModelAttribute("reassign") final SewerageReassignDetails reassignDetails, final Model model,
                              @PathVariable final String appType, @PathVariable final String applicationNum,
                              final HttpServletRequest request) {
        Map<String, String> employeeWithPosition = sewerageReassignService.getEmployees();
        if (!employeeWithPosition.isEmpty())
            model.addAttribute("assignments", employeeWithPosition);

        else
            model.addAttribute("message", "notexists.position");

        reassignDetails.setApplicationNo(applicationNum);
        reassignDetails.setStateType(appType);
        return SEWERAGE_REASSIGN;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String update(@ModelAttribute("reassign") final SewerageReassignDetails reassignDetails, final Model model,
                         @Valid final BindingResult errors, final HttpServletRequest request) {
        String[] selectedId = request.getParameter("approvalPosition").split("-");
        Long positionId = Long.valueOf(selectedId[1]);
        Position position = positionMasterService.getPositionById(positionId);
        Assignment assignment = assignmentService.getAssignmentsForPosition(positionId).get(0);
        String applicationNo = sewerageReassignService.getSewerageApplication(reassignDetails, position);
        if (StringUtils.isNotEmpty(applicationNo) && assignment != null) {
            String successMessage = "Sewearge Tax  application with reference number : " + applicationNo
                    + " successfully re-assigned to "
                    + assignment.getEmployee().getName() + "~" + assignment.getDesignation().getName() + ":"
                    + assignment.getDepartment().getCode();
            model.addAttribute(SUCCESSMESSAGE, successMessage);
        } else {
            model.addAttribute(SUCCESSMESSAGE, "Reassign Failed!");
        }
        return REASSIGN_SUCCESS;
    }

}
