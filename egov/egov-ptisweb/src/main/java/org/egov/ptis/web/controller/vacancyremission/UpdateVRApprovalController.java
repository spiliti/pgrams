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
package org.egov.ptis.web.controller.vacancyremission;

import org.egov.commons.entity.Source;
import org.egov.eis.entity.Assignment;
import org.egov.eis.service.AssignmentService;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.eis.web.controller.workflow.GenericWorkFlowController;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.pims.commons.Designation;
import org.egov.ptis.client.util.PropertyTaxUtil;
import org.egov.ptis.constants.PropertyTaxConstants;
import org.egov.ptis.domain.entity.property.BasicProperty;
import org.egov.ptis.domain.entity.property.VacancyRemission;
import org.egov.ptis.domain.entity.property.VacancyRemissionApproval;
import org.egov.ptis.domain.service.property.PropertyService;
import org.egov.ptis.domain.service.property.VacancyRemissionService;
import org.egov.ptis.service.utils.PropertyTaxCommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.egov.ptis.constants.PropertyTaxConstants.APPLICATION_TYPE_VACANCY_REMISSION;
import static org.egov.ptis.constants.PropertyTaxConstants.ASSISTANT_DESGN;
import static org.egov.ptis.constants.PropertyTaxConstants.COMMISSIONER_DESGN;
import static org.egov.ptis.constants.PropertyTaxConstants.REVENUE_INSPECTOR_DESGN;
import static org.egov.ptis.constants.PropertyTaxConstants.WFLOW_ACTION_STEP_NOTICE_GENERATE;
import static org.egov.ptis.constants.PropertyTaxConstants.WFLOW_ACTION_STEP_SIGN;

@Controller
@RequestMapping(value = "/vacancyremissionapproval/update/{id}")
public class UpdateVRApprovalController extends GenericWorkFlowController {

    protected static final String VRAPPROVAL_EDIT = "vacancyRemissionApproval-edit";
    private static final String APPROVAL_POS = "approvalPosition";
    private static final String PROPERTY_MODIFY_REJECT_FAILURE = "Initiator is not active so can not do rejection with the Assessment number :";

    private final VacancyRemissionService vacancyRemissionService;

    private final PropertyTaxUtil propertyTaxUtil;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private PropertyTaxCommonUtils propertyTaxCommonUtils;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    public UpdateVRApprovalController(final VacancyRemissionService vacancyRemissionService,
                                      final PropertyTaxUtil propertyTaxUtil) {
        this.propertyTaxUtil = propertyTaxUtil;
        this.vacancyRemissionService = vacancyRemissionService;
    }

    @ModelAttribute
    public VacancyRemissionApproval vacancyRemissionApprovalModel(@PathVariable final Long id) {
        return vacancyRemissionService.getVacancyRemissionApprovalById(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String view(final Model model, @PathVariable final Long id, final HttpServletRequest request) {
        final VacancyRemissionApproval vacancyRemissionApproval = vacancyRemissionService.getVacancyRemissionApprovalById(id);
        VacancyRemission vacancyRemission = null;
        String userDesgn = "";
        Designation designation = propertyTaxUtil.getDesignationForUser(vacancyRemissionService.getLoggedInUser().getId());
        final String userDesignationList = propertyTaxCommonUtils
                .getAllDesignationsForUser(securityUtils.getCurrentUser().getId());
        if (null != designation)
            userDesgn = designation.getName();
        if (vacancyRemissionApproval != null) {
            final String currentDesignation = vacancyRemissionService.getLoggedInUserDesignation(
                    vacancyRemissionApproval.getCurrentState().getOwnerPosition().getId(),
                    securityUtils.getCurrentUser());
            model.addAttribute("currentDesignation", currentDesignation);
            model.addAttribute("pendingActions", vacancyRemissionApproval.getCurrentState().getNextAction());
            model.addAttribute("stateType", vacancyRemissionApproval.getClass().getSimpleName());
            model.addAttribute("currentState", vacancyRemissionApproval.getCurrentState().getValue());
            final WorkflowContainer workflowContainer = new WorkflowContainer();
            workflowContainer.setPendingActions(vacancyRemissionApproval.getCurrentState().getNextAction());
            prepareWorkflow(model, vacancyRemissionApproval, workflowContainer);
            final BasicProperty basicProperty = vacancyRemissionApproval.getVacancyRemission().getBasicProperty();
            vacancyRemission = vacancyRemissionService.getApprovedVacancyRemissionForProperty(basicProperty.getUpicNo());
            vacancyRemissionService.addModelAttributes(model, basicProperty);
            model.addAttribute("detailsHistory",
                    vacancyRemissionService.getMonthlyDetailsHistory(vacancyRemissionApproval.getVacancyRemission()));
            model.addAttribute("workflowHistory", vacancyRemissionApproval.getStateHistory());
            model.addAttribute("userDesgn", userDesgn);
            model.addAttribute("designation", COMMISSIONER_DESGN);
            model.addAttribute("userDesignationList", userDesignationList);
            model.addAttribute("commissionerDesignation", COMMISSIONER_DESGN);
            model.addAttribute("revenueClerkDesignation", ASSISTANT_DESGN);
            model.addAttribute("endorsementNotices", propertyTaxCommonUtils.getEndorsementNotices(vacancyRemission.getApplicationNumber()));
            model.addAttribute("vacancyFromDate", vacancyRemission.getVacancyFromDate());
            model.addAttribute("vacancyToDate", vacancyRemission.getVacancyToDate());
        }
        if (vacancyRemission != null && !vacancyRemission.getDocuments().isEmpty())
            model.addAttribute("attachedDocuments", vacancyRemission.getDocuments());
        return VRAPPROVAL_EDIT;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String update(@Valid @ModelAttribute final VacancyRemissionApproval vacancyRemissionApproval,
                         final BindingResult resultBinder, final RedirectAttributes redirectAttributes,
                         final HttpServletRequest request, final Model model) {

        final String senderName = vacancyRemissionApproval.getCurrentState().getSenderName();
        if (!resultBinder.hasErrors()) {
            String workFlowAction = "";
            if (request.getParameter("workFlowAction") != null)
                workFlowAction = request.getParameter("workFlowAction");

            Long approvalPosition = 0l;
            String approvalComent = "";
            String successMsg = "";

            if (request.getParameter("approvalComent") != null)
                approvalComent = request.getParameter("approvalComent");
            if (isApprovalPosNotEmpty(request))
                approvalPosition = Long.valueOf(request.getParameter(APPROVAL_POS));
            if (isNotPreviewSignOrNoticeGen(workFlowAction))
                vacancyRemissionService.saveVacancyRemissionApproval(vacancyRemissionApproval, approvalPosition,
                        approvalComent, null, workFlowAction);

            if (org.apache.commons.lang.StringUtils.isNotBlank(workFlowAction)) {
                if (isPreviewOrSignOrNoticeGen(workFlowAction)) {
                    final String pathVars = vacancyRemissionApproval.getVacancyRemission().getBasicProperty().getUpicNo()
                            + "," + senderName;
                    return "redirect:/vacancyremission/generatenotice?pathVar=" + pathVars + "&workFlowAction="
                            + workFlowAction;
                } else if (isReject(workFlowAction))
                    successMsg = wfReject(vacancyRemissionApproval, workFlowAction, approvalPosition, approvalComent);
                else if (isApprove(workFlowAction))
                    successMsg = "Vacancy Remission Approved Successfully in the System and forwarded to " + propertyTaxUtil
                            .getApproverUserName(vacancyRemissionApproval.getCurrentState().getOwnerPosition().getId());
                else
                    successMsg = "Vacancy Remission Saved Successfully in the System and forwarded to : "
                            + propertyTaxUtil.getApproverUserName(approvalPosition);
                if (Source.CITIZENPORTAL.toString().equalsIgnoreCase(vacancyRemissionApproval.getVacancyRemission().getSource())
                        && propertyService.getPortalInbox(vacancyRemissionApproval.getVacancyRemission().getApplicationNumber()) != null) {
                    propertyService.updatePortal(vacancyRemissionApproval.getVacancyRemission(),
                            APPLICATION_TYPE_VACANCY_REMISSION);
                }
            }

            model.addAttribute("successMessage", successMsg);
        }
        return "vacancyRemission-success";
    }

    private boolean isApprovalPosNotEmpty(final HttpServletRequest request) {
        return request.getParameter(APPROVAL_POS) != null && !request.getParameter(APPROVAL_POS).isEmpty();
    }

    private boolean isNotPreviewSignOrNoticeGen(final String workFlowAction) {
        return !workFlowAction.equalsIgnoreCase(PropertyTaxConstants.WFLOW_ACTION_STEP_PREVIEW)
                && !WFLOW_ACTION_STEP_SIGN.equalsIgnoreCase(workFlowAction)
                && !isReject(workFlowAction);
    }

    private boolean isPreviewOrSignOrNoticeGen(final String workFlowAction) {
        return workFlowAction.equalsIgnoreCase(PropertyTaxConstants.WFLOW_ACTION_STEP_PREVIEW)
                || WFLOW_ACTION_STEP_SIGN.equalsIgnoreCase(workFlowAction)
                || workFlowAction.equalsIgnoreCase(WFLOW_ACTION_STEP_NOTICE_GENERATE);
    }

    private boolean isReject(final String workFlowAction) {
        return workFlowAction.equalsIgnoreCase(PropertyTaxConstants.WFLOW_ACTION_STEP_REJECT);
    }

    private boolean isApprove(final String workFlowAction) {
        return workFlowAction.equalsIgnoreCase(PropertyTaxConstants.WFLOW_ACTION_STEP_APPROVE);
    }

    private String wfReject(final VacancyRemissionApproval vacancyRemissionApproval, final String workFlowAction,
                            final Long approvalPosition,
                            final String approvalComent) {
        String successMsg;
        String loggedInUserDesignation = "";
        Assignment wfInitiator;
        final User user = securityUtils.getCurrentUser();
        List<Assignment> loggedInUserAssign;
        Designation designation = propertyTaxUtil.getDesignationForUser(vacancyRemissionService.getLoggedInUser().getId());
        if (vacancyRemissionApproval.getState() != null) {
            loggedInUserAssign = assignmentService.getAssignmentByPositionAndUserAsOnDate(
                    vacancyRemissionApproval.getCurrentState().getOwnerPosition().getId(), user.getId(), new Date());
            if (!loggedInUserAssign.isEmpty())
                loggedInUserDesignation = loggedInUserAssign.get(0).getDesignation().getName();
        }

        if (propertyTaxCommonUtils.isRoOrCommissioner(loggedInUserDesignation))
            wfInitiator = vacancyRemissionService.getWorkflowInitiatorAssignment(vacancyRemissionApproval.getCreatedBy().getId(), Arrays.asList(PropertyTaxConstants.REVENUE_INSPECTOR_DESGN));
        else
            wfInitiator = vacancyRemissionService.getWorkflowInitiator(vacancyRemissionApproval);

        if (wfInitiator != null || designation.getName().equalsIgnoreCase(REVENUE_INSPECTOR_DESGN)) {
            vacancyRemissionService.saveVacancyRemissionApproval(vacancyRemissionApproval, approvalPosition,
                    approvalComent, null, workFlowAction);
            if (designation.getName().equalsIgnoreCase(REVENUE_INSPECTOR_DESGN))
                successMsg = "Vacancy Remission rejected successfully";
            else
                successMsg = "Vacancy Remission rejected successfully and forwarded to : "
                        + (wfInitiator != null
                        ? wfInitiator.getEmployee().getName().concat("~").concat(wfInitiator.getPosition().getName())
                        : "");
        } else
            successMsg = PROPERTY_MODIFY_REJECT_FAILURE
                    + vacancyRemissionApproval.getVacancyRemission().getBasicProperty().getUpicNo();
        return successMsg;
    }
}