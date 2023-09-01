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
package org.egov.wtms.web.controller.application;

import static org.egov.infra.utils.ApplicationConstant.CITY_DIST_NAME_KEY;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.filestore.entity.FileStoreMapper;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.reporting.engine.ReportOutput;
import org.egov.infra.utils.FileStoreUtils;
import org.egov.infra.utils.autonumber.AutonumberServiceBeanResolver;
import org.egov.infra.workflow.entity.StateAware;
import org.egov.infra.workflow.entity.WorkflowType;
import org.egov.infra.workflow.inbox.InboxRenderServiceDelegate;
import org.egov.infra.workflow.service.WorkflowTypeService;
import org.egov.ptis.domain.model.AssessmentDetails;
import org.egov.ptis.domain.model.OwnerName;
import org.egov.ptis.domain.model.enums.BasicPropertyStatus;
import org.egov.ptis.domain.service.property.PropertyExternalService;
import org.egov.wtms.application.entity.WaterConnectionDetails;
import org.egov.wtms.application.service.ReportGenerationService;
import org.egov.wtms.application.service.WaterConnectionDetailsService;
import org.egov.wtms.autonumber.WorkOrderNumberGenerator;
import org.egov.wtms.utils.PropertyExtnUtils;
import org.egov.wtms.utils.constants.WaterTaxConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author ranjit
 */
@Controller
@RequestMapping(value = "/digitalSignature")
public class DigitalSignatureConnectionController {

    @Autowired
    @Qualifier("fileStoreService")
    private FileStoreService fileStoreService;
    @Autowired
    private WaterConnectionDetailsService waterConnectionDetailsService;
    @Autowired
    private InboxRenderServiceDelegate<StateAware> inboxRenderServiceDeligate;
    @Autowired
    private WorkflowTypeService workflowTypeService;
    @Autowired
    private PropertyExtnUtils propertyExtnUtils;

    @Autowired
    private ReportGenerationService reportGenerationService;

    @Autowired
    private AutonumberServiceBeanResolver beanResolver;

    @Autowired
    private FileStoreUtils fileStoreUtils;

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/waterTax/transitionWorkflow")
    public String transitionWorkflow(final HttpServletRequest request, final Model model) {
        final String fileStoreIds = request.getParameter("fileStoreId");
        final String[] fileStoreIdArr = fileStoreIds.split(",");
        final HttpSession session = request.getSession();
        final String sourceChannel = request.getParameter("Source");
        Long approvalPosition = (Long) session.getAttribute(WaterTaxConstants.APPROVAL_POSITION);
        final String approvalComent = (String) session.getAttribute(WaterTaxConstants.APPROVAL_COMMENT);
        final Map<String, String> appNoFileStoreIdsMap = (Map<String, String>) session
                .getAttribute(WaterTaxConstants.FILE_STORE_ID_APPLICATION_NUMBER);
        WaterConnectionDetails waterConnectionDetails = null;
        for (final String fileStoreId : fileStoreIdArr) {
            final String applicationNumber = appNoFileStoreIdsMap.get(fileStoreId);
            if (null != applicationNumber && !applicationNumber.isEmpty()) {
                waterConnectionDetails = waterConnectionDetailsService.findByApplicationNumber(applicationNumber);
                if (null == approvalPosition) {
                    String additionalRule = waterConnectionDetails.getApplicationType().getCode();
                    if (additionalRule.equalsIgnoreCase(WaterTaxConstants.CLOSINGCONNECTION))
                        additionalRule = WaterTaxConstants.CLOSECONNECTION;
                    approvalPosition = waterConnectionDetailsService.getApprovalPositionByMatrixDesignation(
                            waterConnectionDetails, approvalPosition, additionalRule, "",
                            WaterTaxConstants.SIGNWORKFLOWACTION);
                }
                waterConnectionDetailsService.updateWaterConnection(waterConnectionDetails, approvalPosition,
                        approvalComent, waterConnectionDetails.getApplicationType().getCode(),
                        WaterTaxConstants.SIGNWORKFLOWACTION, "", null, sourceChannel);
            }
        }
        model.addAttribute("successMessage", "Digitally Signed Successfully");
        model.addAttribute("workOrderNumber",
                waterConnectionDetails == null ? null : waterConnectionDetails.getWorkOrderNumber());
        model.addAttribute("fileStoreId", fileStoreIdArr.length == 1 ? fileStoreIdArr[0] : "");
        return "digitalSignature-success";
    }

    @RequestMapping(value = "/waterTax/downloadSignedWorkOrderConnection")
    public ResponseEntity<InputStreamResource> downloadSignedWorkOrderConnection(@RequestParam final String signedFileStoreId,
            @RequestParam final String workOrderNumber) {
        return fileStoreUtils.fileAsResponseEntity(signedFileStoreId, WaterTaxConstants.FILESTORE_MODULECODE, true);
    }

    @RequestMapping(value = "/digitalSignaturePending-form", method = RequestMethod.GET)
    public String searchForm(final HttpServletRequest request, final Model model) {
        final String cityMunicipalityName = (String) request.getSession().getAttribute("citymunicipalityname");
        final String districtName = (String) request.getSession().getAttribute(CITY_DIST_NAME_KEY);
        final List<HashMap<String, Object>> resultList = getRecordsForDigitalSignature();
        model.addAttribute("digitalSignatureReportList", resultList);
        model.addAttribute("noticeType", WaterTaxConstants.NOTICE_TYPE_SPECIAL_NOTICE);
        model.addAttribute("cityMunicipalityName", cityMunicipalityName);
        model.addAttribute(CITY_DIST_NAME_KEY, districtName);
        return "digitalSignaturePending-form";
    }

    @RequestMapping(value = "/waterTax/signWorkOrder", method = RequestMethod.POST)
    public String signWorkOrder(final HttpServletRequest request, final Model model) {
        WaterConnectionDetails waterConnectionDetails;
        String[] applicationNumbers;
        String[] appNumberConTypePair = null;
        final Map<String, String> fileStoreIdsApplicationNoMap = new HashMap<>();
        final StringBuilder fileStoreIds = new StringBuilder();
        final String pathVar = request.getParameter("pathVar");
        final String applicationNoStatePair = request.getParameter("applicationNoStatePair");
        String currentState = request.getParameter("currentState");
        final String signAll = request.getParameter("signAll");
        final WorkOrderNumberGenerator workOrderGen = beanResolver
                .getAutoNumberServiceFor(WorkOrderNumberGenerator.class);

        String fileName;
        if (pathVar != null) {
            applicationNumbers = request.getParameter("pathVar").split(",");
            if (applicationNoStatePair != null)
                appNumberConTypePair = applicationNoStatePair.split(",");
            for (int i = 0; i < applicationNumbers.length; i++) {
                waterConnectionDetails = waterConnectionDetailsService.findByApplicationNumber(applicationNumbers[i]);
                waterConnectionDetails.setWorkOrderDate(new Date());
                waterConnectionDetails.setWorkOrderNumber(workOrderGen.generateWorkOrderNumber());
                ReportOutput reportOutput;
                if (signAll != null && signAll.equalsIgnoreCase(WaterTaxConstants.SIGN_ALL))
                    for (final String element : appNumberConTypePair) {
                        final String[] appNoStatePair = element.split(":");
                        if (applicationNumbers[i].equalsIgnoreCase(appNoStatePair[0]))
                            currentState = appNoStatePair[1];
                    }
                if (currentState.equalsIgnoreCase(WaterTaxConstants.CLOSECONNECTION)) {
                    reportOutput = reportGenerationService.generateClosureConnectionReport(waterConnectionDetails,
                            WaterTaxConstants.SIGNWORKFLOWACTION);
                    fileName = WaterTaxConstants.SIGNED_DOCUMENT_PREFIX + waterConnectionDetails.getApplicationNumber()
                            + ".pdf";
                } else if (currentState.equalsIgnoreCase(WaterTaxConstants.RECONNECTION)) {
                    reportOutput = reportGenerationService.generateReconnectionReport(waterConnectionDetails,
                            WaterTaxConstants.SIGNWORKFLOWACTION);
                    fileName = WaterTaxConstants.SIGNED_DOCUMENT_PREFIX + waterConnectionDetails.getApplicationNumber()
                            + ".pdf";
                } else {
                    reportOutput = reportGenerationService.getReportOutput(waterConnectionDetails,
                            WaterTaxConstants.SIGNWORKFLOWACTION);
                    fileName = WaterTaxConstants.SIGNED_DOCUMENT_PREFIX + waterConnectionDetails.getWorkOrderNumber()
                            + ".pdf";
                }
                // Setting FileStoreMap object while Commissioner Signs the
                // document
                if (reportOutput != null) {
                    final InputStream fileStream = new ByteArrayInputStream(reportOutput.getReportOutputData());
                    final FileStoreMapper fileStore = fileStoreService.store(fileStream, fileName, "application/pdf",
                            WaterTaxConstants.FILESTORE_MODULECODE);
                    waterConnectionDetails.setFileStore(fileStore);
                    waterConnectionDetails = waterConnectionDetailsService
                            .updateWaterConnectionDetailsWithFileStore(waterConnectionDetails);
                    fileStoreIdsApplicationNoMap.put(waterConnectionDetails.getFileStore().getFileStoreId(),
                            applicationNumbers[i]);
                    fileStoreIds.append(waterConnectionDetails.getFileStore().getFileStoreId());
                    if (i < applicationNumbers.length - 1)
                        fileStoreIds.append(",");
                }
            }
            request.getSession().setAttribute(WaterTaxConstants.FILE_STORE_ID_APPLICATION_NUMBER,
                    fileStoreIdsApplicationNoMap);
            model.addAttribute("fileStoreIds", fileStoreIds.toString());
            model.addAttribute("ulbCode", ApplicationThreadLocals.getCityCode());
        }
        return "newConnection-digitalSignatureRedirection";
    }

    public List<HashMap<String, Object>> getRecordsForDigitalSignature() {
        final List<HashMap<String, Object>> resultList = new ArrayList<>();
        final List<StateAware> stateAwareList = fetchItems();
        WaterConnectionDetails waterConnectionDetails;
        if (null != stateAwareList && !stateAwareList.isEmpty()) {
            HashMap<String, Object> tempMap;
            for (final StateAware record : stateAwareList)
                if (record != null && record.getState() != null && record.getState().getNextAction() != null && record
                        .getState().getNextAction().equalsIgnoreCase(WaterTaxConstants.DIGITAL_SIGNATURE_PENDING)) {
                    tempMap = new HashMap<>();
                    final WorkflowType workflowType = workflowTypeService
                            .getEnabledWorkflowTypeByType(record.getStateType());
                    if (WaterTaxConstants.MODULE_NAME.equalsIgnoreCase(workflowType.getModule().getName())) {
                        waterConnectionDetails = (WaterConnectionDetails) record;
                        tempMap.put("objectId", ((WaterConnectionDetails) record).getApplicationNumber());
                        tempMap.put("type", record.getState().getNatureOfTask());
                        tempMap.put("module", workflowType.getModule().getDisplayName());
                        tempMap.put("details", record.getStateDetails());
                        tempMap.put("hscNumber", waterConnectionDetails.getConnection().getConsumerCode());
                        tempMap.put("status", record.getCurrentState().getValue());
                        tempMap.put("applicationNumber", waterConnectionDetails.getApplicationNumber());
                        tempMap.put("waterConnectionDetails", waterConnectionDetails);
                        tempMap.put("ownerName", getOwnerName(waterConnectionDetails));
                        tempMap.put("propertyAddress", getPropertyAddress(waterConnectionDetails));
                        String additionalRule = waterConnectionDetails.getApplicationType().getCode();
                        if ("CLOSINGCONNECTION".equals(additionalRule))
                            additionalRule = WaterTaxConstants.CLOSECONNECTION;
                        tempMap.put("state", additionalRule);
                        resultList.add(tempMap);
                    }
                }
        }
        return resultList;
    }

    public List<StateAware> fetchItems() {
        final List<StateAware> digitalSignWFItems = new ArrayList<>();
        digitalSignWFItems.addAll(inboxRenderServiceDeligate.getAssignedWorkflowItems());
        return digitalSignWFItems;
    }

    private String getOwnerName(final WaterConnectionDetails waterConnectionDetails) {
        String ownerName = "";
        final AssessmentDetails assessmentDetails = propertyExtnUtils.getAssessmentDetailsForFlag(
                waterConnectionDetails.getConnection().getPropertyIdentifier(),
                PropertyExternalService.FLAG_FULL_DETAILS, BasicPropertyStatus.ALL);
        if (null != assessmentDetails && null != assessmentDetails.getOwnerNames()) {
            final Iterator<OwnerName> ownerNameItr = assessmentDetails.getOwnerNames().iterator();
            if (ownerNameItr.hasNext()) {
                final OwnerName owner = ownerNameItr.next();
                ownerName = owner.getOwnerName() != null ? owner.getOwnerName() : ownerName;
            }
        }
        return ownerName;
    }

    private String getPropertyAddress(final WaterConnectionDetails waterConnectionDetails) {
        String propAddress = "";
        final AssessmentDetails assessmentDetails = propertyExtnUtils.getAssessmentDetailsForFlag(
                waterConnectionDetails.getConnection().getPropertyIdentifier(),
                PropertyExternalService.FLAG_FULL_DETAILS, BasicPropertyStatus.ALL);
        if (null != assessmentDetails)
            propAddress = assessmentDetails.getPropertyAddress() != null ? assessmentDetails.getPropertyAddress()
                    : propAddress;
        return propAddress;
    }
}
