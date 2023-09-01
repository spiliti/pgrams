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

package org.egov.mrs.domain.service;

import org.apache.commons.lang3.StringUtils;
import org.egov.commons.entity.Source;
import org.egov.eis.service.EisCommonService;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.entity.Module;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.admin.master.service.ModuleService;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.workflow.entity.State;
import org.egov.infra.workflow.entity.StateHistory;
import org.egov.mrs.application.MarriageConstants;
import org.egov.mrs.application.MarriageUtils;
import org.egov.mrs.application.service.MarriageCertificateService;
import org.egov.mrs.application.service.ReIssueDemandService;
import org.egov.mrs.application.service.workflow.RegistrationWorkflowService;
import org.egov.mrs.autonumber.MarriageRegistrationApplicationNumberGenerator;
import org.egov.mrs.domain.entity.MarriageCertificate;
import org.egov.mrs.domain.entity.MarriageDocument;
import org.egov.mrs.domain.entity.MarriageRegistration;
import org.egov.mrs.domain.entity.ReIssue;
import org.egov.mrs.domain.enums.MarriageCertificateType;
import org.egov.mrs.domain.repository.ReIssueRepository;
import org.egov.mrs.masters.service.MarriageFeeService;
import org.egov.mrs.service.es.ReIssueCertificateUpdateIndexesService;
import org.egov.pims.commons.Position;
import org.egov.portal.entity.PortalInbox;
import org.egov.portal.entity.PortalInboxBuilder;
import org.egov.portal.service.PortalInboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ReIssueService {

    private static final String REISSUE_APPLICATION_VIEW = "/mrs/reissue/view/%s";
    private static final String USER = "user";
    private static final String DEPARTMENT = "department";
    private final ReIssueRepository reIssueRepository;

    @Autowired
    @Qualifier("parentMessageSource")
    private MessageSource mrsMessageSource;
    @Autowired
    private RegistrationWorkflowService workflowService;
    @Autowired
    private ReIssueDemandService reIssueDemandService;
    @Autowired
    private MarriageRegistrationApplicationNumberGenerator marriageRegistrationApplicationNumberGenerator;
    @Autowired
    private MarriageDocumentService marriageDocumentService;
    @Autowired
    private MarriageApplicantService marriageApplicantService;
    @Autowired
    private MarriageUtils marriageUtils;
    @Autowired
    private EisCommonService eisCommonService;
    @Autowired
    private MarriageFeeService marriageFeeService;
    @Autowired
    private MarriageCertificateService marriageCertificateService;
    @Autowired
    private MarriageSmsAndEmailService marriageSmsAndEmailService;
    @Autowired
    private ReIssueCertificateUpdateIndexesService reiSsueUpdateIndexesService;
    @Autowired
    private AppConfigValueService appConfigValuesService;
    @Autowired
    private SecurityUtils securityUtils;
    @Autowired
    private ModuleService moduleDao;
    @Autowired
    private PortalInboxService portalInboxService;
    @Autowired
    private MarriageRegistrationService marriageRegistrationService;

    @Autowired
    public ReIssueService(final ReIssueRepository reIssueRepository) {
        this.reIssueRepository = reIssueRepository;
    }

    @Transactional
    public void create(final ReIssue reIssue) {
        reIssueRepository.save(reIssue);
    }

    @Transactional
    public ReIssue update(final ReIssue reIssue) {
        return reIssueRepository.saveAndFlush(reIssue);
    }

    public ReIssue get(final Long id) {
        return reIssueRepository.findById(id);
    }

    public ReIssue findByApplicationNo(final String applicationNo) {
        return reIssueRepository.findByApplicationNo(applicationNo);
    }

    @Transactional
    public String createReIssueApplication(final ReIssue reIssue, final WorkflowContainer workflowContainer) {
        final boolean citizenPortalUser = workflowService.isCitizenPortalUser(securityUtils.getCurrentUser());
        if (StringUtils.isBlank(reIssue.getApplicationNo())) {
            reIssue.setApplicationNo(marriageRegistrationApplicationNumberGenerator.getNextReIssueApplicationNumber(reIssue));
            reIssue.setApplicationDate(new Date());
        }
        if (reIssue.getFeeCriteria() != null)
            reIssue.setFeeCriteria(marriageFeeService.getFee(reIssue.getFeeCriteria().getId()));
        reIssue.setStatus(marriageUtils.getStatusByCodeAndModuleType(ReIssue.ReIssueStatus.CREATED.toString(),
                MarriageConstants.MODULE_NAME));
        reIssue.setSource(Source.SYSTEM.toString());
        if (reIssue.getFeePaid() != null && reIssue.getDemand() == null)
            reIssue.setDemand(reIssueDemandService.createDemand(new BigDecimal(reIssue.getFeePaid())));

        final Map<Long, MarriageDocument> applicantDocumentAndId = new HashMap<>();
        marriageDocumentService.getIndividualDocuments()
                .forEach(document -> applicantDocumentAndId.put(document.getId(), document));

        marriageApplicantService.addDocumentsToFileStore(reIssue.getApplicant(), applicantDocumentAndId);

        workflowService.transition(reIssue, workflowContainer, reIssue.getApprovalComent());

        create(reIssue);
        reiSsueUpdateIndexesService.createReIssueAppIndex(reIssue);
        marriageSmsAndEmailService.sendSMSForReIssueApplication(reIssue);
        if (citizenPortalUser)
            pushPortalMessage(reIssue);
        marriageSmsAndEmailService.sendEmailForReIssueApplication(reIssue);
        return reIssue.getApplicationNo();
    }

    @Transactional
    public ReIssue forwardReIssue(final Long id, final ReIssue reissue,
                                  final WorkflowContainer workflowContainer) {
        updateReIssueData(reissue);
        reissue.setStatus(
                marriageUtils.getStatusByCodeAndModuleType(ReIssue.ReIssueStatus.CREATED.toString(),
                        MarriageConstants.MODULE_NAME));
        update(reissue);
        workflowService.transition(reissue, workflowContainer, reissue.getApprovalComent());
        if (reissue.getSource() != null
                && Source.CITIZENPORTAL.name().equalsIgnoreCase(reissue.getSource())
                && getPortalInbox(reissue.getApplicationNo()) != null)
            updatePortalMessage(reissue);
        return reissue;
    }

    @Transactional
    public ReIssue approveReIssue(final ReIssue reissue, final WorkflowContainer workflowContainer) throws IOException {
        reissue.setStatus(
                marriageUtils.getStatusByCodeAndModuleType(ReIssue.ReIssueStatus.APPROVED.toString(),
                        MarriageConstants.MODULE_NAME));
        final ReIssue reissue1 = update(reissue);
        if (marriageUtils.isDigitalSignEnabled()) {
            workflowService.transition(reissue1, workflowContainer, workflowContainer.getApproverComments());
            reiSsueUpdateIndexesService.updateReIssueAppIndex(reissue1);
        } else
            printCertificate(reissue1, workflowContainer);
        if (reissue.getSource() != null
                && Source.CITIZENPORTAL.name().equalsIgnoreCase(reissue.getSource())
                && getPortalInbox(reissue.getApplicationNo()) != null)
            updatePortalMessage(reissue);
        marriageSmsAndEmailService.sendSMSForReIssueApplication(reissue1);
        marriageSmsAndEmailService.sendEmailForReIssueApplication(reissue1);
        return reissue1;
    }

    @Transactional
    public ReIssue rejectReIssue(final ReIssue reissue, final WorkflowContainer workflowContainer)
            throws IOException {
        reissue.setStatus(
                workflowContainer.getWorkFlowAction().equalsIgnoreCase(MarriageConstants.WFLOW_ACTION_STEP_REJECT) ? marriageUtils
                        .getStatusByCodeAndModuleType(ReIssue.ReIssueStatus.REJECTED.toString(), MarriageConstants.MODULE_NAME)
                        : marriageUtils.getStatusByCodeAndModuleType(ReIssue.ReIssueStatus.CANCELLED.toString(),
                        MarriageConstants.MODULE_NAME));

        if (workflowContainer.getWorkFlowAction().equalsIgnoreCase(MarriageConstants.WFLOW_ACTION_STEP_CANCEL_REISSUE)) {
            final List<AppConfigValues> appConfigValues = appConfigValuesService.getConfigValuesByModuleAndKey(
                    MarriageConstants.MODULE_NAME, MarriageConstants.REISSUE_PRINTREJECTIONCERTIFICATE);
            // As per configuration, save rejection certificate.
            if (appConfigValues != null && !appConfigValues.isEmpty()
                    && "YES".equalsIgnoreCase(appConfigValues.get(0).getValue())) {

                final MarriageCertificate marriageCertificate = marriageCertificateService.reIssueCertificate(reissue,
                        MarriageCertificateType.REJECTION);
                reissue.addCertificate(marriageCertificate);
            }
        }
        reiSsueUpdateIndexesService.updateReIssueAppIndex(reissue);
        if (reissue.getSource() != null
                && Source.CITIZENPORTAL.name().equalsIgnoreCase(reissue.getSource())
                && getPortalInbox(reissue.getApplicationNo()) != null)
            updatePortalMessage(reissue);
        marriageSmsAndEmailService.sendSMSForReIssueApplication(reissue);
        marriageSmsAndEmailService.sendEmailForReIssueApplication(reissue);
        reissue.setRejectionReason(workflowContainer.getApproverComments());
        workflowService.transition(reissue, workflowContainer, workflowContainer.getApproverComments());

        return reissue;
    }

    private void updateReIssueData(final ReIssue reissue) {
        if (reissue.getFeeCriteria() != null)
            reissue.setFeeCriteria(marriageFeeService.getFee(reissue.getFeeCriteria().getId()));
        reissue.setFeePaid(reissue.getFeePaid());
        if (reissue.getFeePaid() != null)
            if (reissue.getDemand() == null)
                reissue.setDemand(reIssueDemandService.createDemand(new BigDecimal(reissue.getFeePaid())));
            else
                reIssueDemandService.updateDemand(reissue.getDemand(), new BigDecimal(reissue.getFeePaid()));
        reissue.setStatus(marriageUtils.getStatusByCodeAndModuleType(ReIssue.ReIssueStatus.CREATED.toString(),
                MarriageConstants.MODULE_NAME));

        updateDocuments(reissue);
    }

    private void updateDocuments(final ReIssue reissue) {

        final Map<Long, MarriageDocument> individualDocumentAndId = new HashMap<>();
        marriageDocumentService.getIndividualDocuments()
                .forEach(document -> individualDocumentAndId.put(document.getId(), document));

        marriageApplicantService.addDocumentsToFileStore(reissue.getApplicant(), individualDocumentAndId);
    }

    @Transactional
    public MarriageCertificate generateReIssueCertificate(final ReIssue reIssue) throws IOException {
        final MarriageCertificate marriageCertificate = marriageCertificateService.reIssueCertificate(
                reIssue, MarriageCertificateType.REISSUE);
        reIssue.addCertificate(marriageCertificate);
        return marriageCertificate;
    }

    @Transactional
    public ReIssue digiSignCertificate(final ReIssue reIssue,
                                       final WorkflowContainer workflowContainer) {
        reIssue.setStatus(marriageUtils.getStatusByCodeAndModuleType(
                ReIssue.ReIssueStatus.CERTIFICATEREISSUED.toString(), MarriageConstants.MODULE_NAME));
        workflowService.transition(reIssue, workflowContainer, workflowContainer.getApproverComments());
        reiSsueUpdateIndexesService.updateReIssueAppIndex(reIssue);
        if (reIssue.getSource() != null
                && Source.CITIZENPORTAL.name().equalsIgnoreCase(reIssue.getSource())
                && getPortalInbox(reIssue.getApplicationNo()) != null)
            updatePortalMessage(reIssue);
        marriageSmsAndEmailService.sendSMSForReIssueApplication(reIssue);
        marriageSmsAndEmailService.sendEmailForReIssueApplication(reIssue);
        return reIssue;
    }

    @Transactional
    public ReIssue printCertificate(final ReIssue reIssue, final WorkflowContainer workflowContainer)
            throws IOException {
        if (reIssue.getMarriageCertificate().isEmpty()) {
            final MarriageCertificate marriageCertificate = marriageCertificateService.reIssueCertificate(
                    reIssue, MarriageCertificateType.REISSUE);
            reIssue.addCertificate(marriageCertificate);
        }
        reIssue.setStatus(
                marriageUtils.getStatusByCodeAndModuleType(ReIssue.ReIssueStatus.CERTIFICATEREISSUED.toString(),
                        MarriageConstants.MODULE_NAME));
        reIssue.setActive(true);
        workflowService.transition(reIssue, workflowContainer, workflowContainer.getApproverComments());
        if (reIssue.getSource() != null
                && Source.CITIZENPORTAL.name().equalsIgnoreCase(reIssue.getSource())
                && getPortalInbox(reIssue.getApplicationNo()) != null)
            updatePortalMessage(reIssue);
        return reIssue;
    }

    /**
     * @param registration
     * @return
     */
    public List<Map<String, Object>> getHistory(final ReIssue reIssue) {
        User user;
        final List<Map<String, Object>> historyTable = new ArrayList<>();
        final State<Position> state = reIssue.getState();
        final Map<String, Object> map = new HashMap<>(0);
        if (null != state) {
            if (!reIssue.getStateHistory().isEmpty()
                    && reIssue.getStateHistory() != null)
                Collections.reverse(reIssue.getStateHistory());
            Map<String, Object> historyMap;
            for (final StateHistory<Position> stateHistory : reIssue.getStateHistory()) {
                historyMap = new HashMap<>(0);
                historyMap.put("date", stateHistory.getDateInfo());
                historyMap.put("comments", stateHistory.getComments() != null ? stateHistory.getComments() : "");
                historyMap.put("updatedBy", stateHistory.getLastModifiedBy().getUsername() + "::"
                        + stateHistory.getLastModifiedBy().getName());
                historyMap.put("status", stateHistory.getValue());
                final Position owner = stateHistory.getOwnerPosition();
                user = stateHistory.getOwnerUser();
                if (null != user) {
                    historyMap.put(USER, user.getUsername() + "::" + user.getName());
                    historyMap.put(DEPARTMENT,
                            null != eisCommonService.getDepartmentForUser(user.getId()) ? eisCommonService
                                    .getDepartmentForUser(user.getId()).getName() : "");
                } else if (null != owner && null != owner.getDeptDesig()) {
                    user = eisCommonService.getUserForPosition(owner.getId(), new Date());
                    historyMap
                            .put(USER, null != user.getUsername() ? user.getUsername() + "::" + user.getName() : "");
                    historyMap.put(DEPARTMENT, null != owner.getDeptDesig().getDepartment() ? owner.getDeptDesig()
                            .getDepartment().getName() : "");
                }
                historyTable.add(historyMap);
            }

            map.put("date", state.getDateInfo());
            map.put("comments", state.getComments() != null ? state.getComments() : "");
            map.put("updatedBy", state.getLastModifiedBy().getUsername() + "::" + state.getLastModifiedBy().getName());
            map.put("status", state.getValue());
            final Position ownerPosition = state.getOwnerPosition();
            user = state.getOwnerUser();
            if (null != user) {
                map.put(USER, user.getUsername() + "::" + user.getName());
                map.put(DEPARTMENT, null != eisCommonService.getDepartmentForUser(user.getId()) ? eisCommonService
                        .getDepartmentForUser(user.getId()).getName() : "");
            } else if (null != ownerPosition && null != ownerPosition.getDeptDesig()) {
                user = eisCommonService.getUserForPosition(ownerPosition.getId(), new Date());
                map.put(USER, null != user.getUsername() ? user.getUsername() + "::" + user.getName() : "");
                map.put(DEPARTMENT, null != ownerPosition.getDeptDesig().getDepartment() ? ownerPosition
                        .getDeptDesig().getDepartment().getName() : "");
            }
            historyTable.add(map);
        }
        return historyTable;
    }

    public boolean checkAnyWorkFlowInProgressForRegistration(final MarriageRegistration registration) {
        return reIssueRepository.findReIssueInProgressForRegistration(registration.getRegistrationNo()) == null ? false : true;
    }

    /**
     * Method to push data for citizen portal inbox
     */

    @Transactional
    public void pushPortalMessage(final ReIssue reIssue) {
        final Module module = moduleDao.getModuleByName(MarriageConstants.MODULE_NAME);

        final PortalInboxBuilder portalInboxBuilder = new PortalInboxBuilder(module,
                reIssue.getState().getNatureOfTask() + " : " + module.getDisplayName(),
                reIssue.getApplicationNo(), reIssue.getRegistration().getRegistrationNo(), reIssue.getId(),
                null, "Success",
                String.format(REISSUE_APPLICATION_VIEW, reIssue.getId()),
                isResolved(reIssue), reIssue.getStatus().getDescription(),
                marriageRegistrationService.calculateDueDateForMrgReIssue(), reIssue.getState(),
                Arrays.asList(securityUtils.getCurrentUser()));
        final PortalInbox portalInbox = portalInboxBuilder.build();
        portalInboxService.pushInboxMessage(portalInbox);
    }

    private boolean isResolved(final ReIssue reIssue) {
        return "END".equalsIgnoreCase(reIssue.getState().getValue())
                || "CLOSED".equalsIgnoreCase(reIssue.getState().getValue());
    }

    public PortalInbox getPortalInbox(final String applicationNumber) {
        final Module module = moduleDao.getModuleByName(MarriageConstants.MODULE_NAME);
        return portalInboxService.getPortalInboxByApplicationNo(applicationNumber, module.getId());
    }

    /**
     * Method to update data for citizen portal inbox
     */
    public void updatePortalMessage(final ReIssue reIssue) {
        final Module module = moduleDao.getModuleByName(MarriageConstants.MODULE_NAME);
        portalInboxService.updateInboxMessage(reIssue.getApplicationNo(), module.getId(),
                reIssue.getStatus().getDescription(),
                isResolved(reIssue), marriageRegistrationService.calculateDueDateForMrgReIssue(), reIssue.getState(),
                null,
                reIssue.getApplicationNo(),
                String.format(REISSUE_APPLICATION_VIEW, reIssue.getId()));
    }

}
