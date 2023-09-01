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

import org.egov.eis.entity.Assignment;
import org.egov.eis.service.AssignmentService;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.eis.web.controller.workflow.GenericWorkFlowController;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.utils.DateUtils;
import org.egov.ptis.client.util.PropertyTaxUtil;
import org.egov.ptis.constants.PropertyTaxConstants;
import org.egov.ptis.domain.entity.property.BasicProperty;
import org.egov.ptis.domain.entity.property.VacancyRemission;
import org.egov.ptis.domain.service.property.VacancyRemissionService;
import org.egov.ptis.domain.service.reassign.ReassignService;
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

import static org.egov.ptis.constants.PropertyTaxConstants.APPLICATION_TYPE_VACANCY_REMISSION;
import static org.egov.ptis.constants.PropertyTaxConstants.COMMISSIONER_DESGN;
import static org.egov.ptis.constants.PropertyTaxConstants.VR_STATUS_ASSISTANT_FORWARDED;
import static org.egov.ptis.constants.PropertyTaxConstants.WF_STATE_ASSISTANT_APPROVAL_PENDING;

import java.util.Date;

@Controller
@RequestMapping(value = "/vacancyremission")
public class UpdateVacancyRemissionController extends GenericWorkFlowController {

    private static final String VACANCYREMISSION_EDIT = "vacancyRemission-edit";
    private static final String VACANCYREMISSION_SUCCESS = "vacancyRemission-success";
    private static final String APPROVAL_POS = "approvalPosition";
    private final VacancyRemissionService vacancyRemissionService;
    private final PropertyTaxUtil propertyTaxUtil;
    private static final String PROPERTY_MODIFY_REJECT_FAILURE = "Initiator is not active so can not do rejection with the Assessment number :";

    @Autowired
    private ReassignService reassignService;

    @Autowired
    private PropertyTaxCommonUtils propertyTaxCommonUtils;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    public UpdateVacancyRemissionController(final VacancyRemissionService vacancyRemissionService,
                                            final PropertyTaxUtil propertyTaxUtil) {
        this.propertyTaxUtil = propertyTaxUtil;
        this.vacancyRemissionService = vacancyRemissionService;
    }

    @ModelAttribute
    public VacancyRemission vacancyRemissionModel(@PathVariable final Long id) {
        return vacancyRemissionService.getVacancyRemissionById(id);
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
    public String view(final Model model, @PathVariable final Long id, final HttpServletRequest request) {
        final VacancyRemission vacancyRemission = vacancyRemissionService.getVacancyRemissionById(id);
        final String userDesignationList = propertyTaxCommonUtils
                .getAllDesignationsForUser(securityUtils.getCurrentUser().getId());
        if (vacancyRemission != null) {
            model.addAttribute("isReassignEnabled", reassignService.isReassignEnabled());
            model.addAttribute("transactionType", APPLICATION_TYPE_VACANCY_REMISSION);
            model.addAttribute("stateAwareId", vacancyRemission.getId());
            model.addAttribute("endorsementNotices", propertyTaxCommonUtils.getEndorsementNotices(vacancyRemission.getApplicationNumber()));
            model.addAttribute("endorsementRequired", propertyTaxCommonUtils.getEndorsementGenerate(securityUtils.getCurrentUser().getId(),
                    vacancyRemission.getCurrentState()));
            model.addAttribute("ownersName", vacancyRemission.getBasicProperty().getFullOwnerName());
            model.addAttribute("applicationNo", vacancyRemission.getApplicationNumber());
            if (propertyTaxUtil.enableVRApproval(vacancyRemission.getBasicProperty().getUpicNo())) {
                return "redirect:/vacancyremissionapproval/create/" + vacancyRemission.getBasicProperty().getUpicNo();
            } else if (propertyTaxUtil.enableMonthlyUpdate(vacancyRemission.getBasicProperty().getUpicNo())) {
                return "redirect:/vacancyremission/monthlyupdate/" + vacancyRemission.getBasicProperty().getUpicNo();
            }
            final BasicProperty basicProperty = vacancyRemission.getBasicProperty();
            model.addAttribute("stateType", vacancyRemission.getClass().getSimpleName());
            model.addAttribute("currentState", vacancyRemission.getCurrentState().getValue());
            model.addAttribute("detailsHistory", vacancyRemissionService.getMonthlyDetailsHistory(vacancyRemission));
            if (!vacancyRemission.getDocuments().isEmpty())
                model.addAttribute("attachedDocuments", vacancyRemission.getDocuments());
            if (vacancyRemission.getStatus().equals(PropertyTaxConstants.VR_STATUS_APPROVED)) {
                model.addAttribute("updated", true);
                model.addAttribute("allowUpdate" , DateUtils.noOfMonthsBetween(vacancyRemission.getVacancyFromDate(), new Date())<1);
                vacancyRemissionService.addModelAttributes(model, basicProperty);
                return VACANCYREMISSION_EDIT;
            } else if (vacancyRemission.getStatus().equals(PropertyTaxConstants.VR_STATUS_REJECTED)) {
                prepareWorkflow(model, vacancyRemission, new WorkflowContainer());
                vacancyRemissionService.addModelAttributes(model, basicProperty);

            } else{
                model.addAttribute("userDesignationList", userDesignationList);
                model.addAttribute("designation", COMMISSIONER_DESGN);
                prepareWorkflow(model, vacancyRemission, new WorkflowContainer());
                vacancyRemissionService.addModelAttributes(model, basicProperty);
            }
        }
        return VACANCYREMISSION_EDIT;
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public String update(@Valid @ModelAttribute final VacancyRemission vacancyRemission, final BindingResult resultBinder,
                         final RedirectAttributes redirectAttributes, final HttpServletRequest request, final Model model) {

        String senderName = vacancyRemission.getCurrentState().getSenderName();
        assignmentService.getPrimaryAssignmentForUser(securityUtils.getCurrentUser().getId());
        senderName = senderName.substring(senderName.lastIndexOf(":") + 1);
        if (!resultBinder.hasErrors()) {
            String workFlowAction = "";
            if (request.getParameter("workFlowAction") != null)
                workFlowAction = request.getParameter("workFlowAction");

            Long approvalPosition = 0l;
            String approvalComent = "";
            String successMsg = "";
            if (request.getParameter("approvalComent") != null)
                approvalComent = request.getParameter("approvalComent");
            if (request.getParameter(APPROVAL_POS) != null && !request.getParameter(APPROVAL_POS).isEmpty())
                approvalPosition = Long.valueOf(request.getParameter(APPROVAL_POS));
            else if (vacancyRemission.getCurrentState().getValue().endsWith(VR_STATUS_ASSISTANT_FORWARDED)
                    && workFlowAction.equalsIgnoreCase(PropertyTaxConstants.WFLOW_ACTION_STEP_FORWARD))
                approvalPosition = vacancyRemission.getState().getInitiatorPosition().getId();
            final Boolean propertyByEmployee = Boolean.valueOf(request.getParameter("propertyByEmployee"));
            if (PropertyTaxConstants.WFLOW_ACTION_STEP_NOTICE_GENERATE.equalsIgnoreCase(workFlowAction)) {
                final String pathVars = vacancyRemission.getBasicProperty().getUpicNo() + "," + senderName;
                vacancyRemissionService.closeVacancyRemission(vacancyRemission);
                return "redirect:/vacancyremission/rejectionacknowledgement?pathVar=" + pathVars;

            }
            if (!isWfReject(workFlowAction))
                vacancyRemissionService.saveVacancyRemission(vacancyRemission, approvalPosition, approvalComent, null,
                        workFlowAction, propertyByEmployee);

            if (isWfNotNoticeGen(workFlowAction))
                if (isWfApprove(workFlowAction))
                    successMsg = "Vacancy Remission Approved Successfully in the System";
                else if (isWfReject(workFlowAction))
                    successMsg = wfReject(vacancyRemission, workFlowAction, approvalPosition, approvalComent, propertyByEmployee);
                else if (isWfForwardOrApprovalPending(vacancyRemission, workFlowAction, propertyByEmployee))
                    successMsg = "Vacancy Remission Approved successfully and forwarded to : "
                            + vacancyRemissionService.getInitiatorName(vacancyRemission);
                else
                    successMsg = "Vacancy Remission forwarded to : "
                            + propertyTaxUtil.getApproverUserName(approvalPosition) + " with application number : "
                            + vacancyRemission.getApplicationNumber();

            model.addAttribute("successMessage", successMsg);
            return VACANCYREMISSION_SUCCESS;
        } else {
            prepareWorkflow(model, vacancyRemission, new WorkflowContainer());
            final BasicProperty basicProperty = vacancyRemission.getBasicProperty();
            vacancyRemissionService.addModelAttributes(model, basicProperty);
            return VACANCYREMISSION_EDIT;
        }

    }

    private boolean isWfReject(final String workFlowAction) {
        return workFlowAction.equalsIgnoreCase(PropertyTaxConstants.WFLOW_ACTION_STEP_REJECT);
    }

    private boolean isWfApprove(final String workFlowAction) {
        return workFlowAction.equalsIgnoreCase(PropertyTaxConstants.WFLOW_ACTION_STEP_APPROVE);
    }

    private boolean isWfNotNoticeGen(final String workFlowAction) {
        return org.apache.commons.lang.StringUtils.isNotBlank(workFlowAction)
                && !workFlowAction.equalsIgnoreCase(PropertyTaxConstants.WFLOW_ACTION_STEP_NOTICE_GENERATE);
    }

    private boolean isWfForwardOrApprovalPending(final VacancyRemission vacancyRemission, final String workFlowAction, final Boolean propertyByEmployee) {
        return PropertyTaxConstants.WFLOW_ACTION_STEP_FORWARD.equalsIgnoreCase(workFlowAction)
                && (WF_STATE_ASSISTANT_APPROVAL_PENDING.equalsIgnoreCase(vacancyRemission.getCurrentState().getNextAction())
                && (!vacancyRemission.getStateHistory().isEmpty()));
    }

    private String wfReject(final VacancyRemission vacancyRemission, final String workFlowAction, final Long approvalPosition,
                            final String approvalComent, final Boolean propertyByEmployee) {
        String successMsg;
        Assignment wfInitiator;
        wfInitiator = vacancyRemissionService.getWorkflowInitiator(vacancyRemission);
        if (wfInitiator != null) {
            successMsg = "Vacancy Remission rejected successfully and forwarded to : "
                    + vacancyRemissionService.getInitiatorName(vacancyRemission);
            vacancyRemissionService.saveVacancyRemission(vacancyRemission, approvalPosition, approvalComent, null,
                    workFlowAction, propertyByEmployee);
        } else
            successMsg = PROPERTY_MODIFY_REJECT_FAILURE+vacancyRemission.getBasicProperty().getUpicNo();
        return successMsg;
    }

}