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
package org.egov.ptis.actions.notice;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.egov.commons.Installment;
import org.egov.commons.dao.InstallmentDao;
import org.egov.commons.entity.Source;
import org.egov.demand.model.EgDemandDetails;
import org.egov.eis.entity.Assignment;
import org.egov.infra.admin.master.entity.Module;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.CityService;
import org.egov.infra.admin.master.service.ModuleService;
import org.egov.infra.admin.master.service.UserService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.filestore.entity.FileStoreMapper;
import org.egov.infra.persistence.entity.Address;
import org.egov.infra.reporting.engine.ReportFormat;
import org.egov.infra.reporting.engine.ReportOutput;
import org.egov.infra.reporting.engine.ReportRequest;
import org.egov.infra.reporting.engine.ReportService;
import org.egov.infra.reporting.viewer.ReportViewerUtil;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.workflow.entity.StateAware;
import org.egov.infstr.services.PersistenceService;
import org.egov.pims.commons.Position;
import org.egov.ptis.actions.common.PropertyTaxBaseAction;
import org.egov.ptis.bean.PropertyNoticeInfo;
import org.egov.ptis.client.util.PropertyTaxNumberGenerator;
import org.egov.ptis.constants.PropertyTaxConstants;
import org.egov.ptis.domain.dao.demand.PtDemandDao;
import org.egov.ptis.domain.entity.demand.Ptdemand;
import org.egov.ptis.domain.entity.objection.RevisionPetition;
import org.egov.ptis.domain.entity.property.BasicProperty;
import org.egov.ptis.domain.entity.property.BasicPropertyImpl;
import org.egov.ptis.domain.entity.property.Floor;
import org.egov.ptis.domain.entity.property.PropertyDetail;
import org.egov.ptis.domain.entity.property.PropertyID;
import org.egov.ptis.domain.entity.property.PropertyImpl;
import org.egov.ptis.domain.entity.property.PropertyMutation;
import org.egov.ptis.domain.entity.property.VacancyRemissionApproval;
import org.egov.ptis.domain.service.notice.NoticeService;
import org.egov.ptis.domain.service.property.PropertyService;
import org.egov.ptis.domain.service.property.VacancyRemissionService;
import org.egov.ptis.domain.service.revisionPetition.RevisionPetitionService;
import org.egov.ptis.domain.service.transfer.PropertyTransferService;
import org.egov.ptis.notice.PtNotice;
import org.egov.ptis.report.bean.PropertyAckNoticeInfo;
import org.egov.ptis.service.utils.PropertyTaxCommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.egov.ptis.constants.PropertyTaxConstants.*;

@ParentPackage("egov")
@Results({ @Result(name = PropertyTaxNoticeAction.NOTICE, location = "propertyTaxNotice-notice.jsp"),
		@Result(name = PropertyTaxNoticeAction.DIGITAL_SIGNATURE_REDIRECTION, location = "propertyTaxNotice-digitalSignatureRedirection.jsp") })
public class PropertyTaxNoticeAction extends PropertyTaxBaseAction {
	private static final String EXEMPTION_REASON = "exemptionReason";
	private static final String COMMISSIONER = "commissioner";
	private static final String NOTICE_DATE = "noticeDate";
	private static final String LOCALITY = "Locality";
	private static final String ASSESSMENT_NUMBER = "AssessmentNumber";
	private static final String DOOR_NUMBER = "doorNumber";
	private static final String OWNER_NAME = "ownerName";
	private static final String APPLICATION_DATE = "applicationDate";
	private static final String CURRENT_PROPERTY_TAX = "currentPropertyTax";
	private static final String ALTER = "Alter";
	private static final String BIFURCATE = "Bifurcate";
	private static final String DEMOLITION = "Demolition";
	private static final String GRP = "GRP";
	private static final String RP = "RP";
	private static final String MODIFY = "modify";
	private static final String CREATE = "create";
	private static final String VACANCYREMISSIONAPPROVAL = "Vacancy Remission Approval";
	protected static final String DIGITAL_SIGNATURE_REDIRECTION = "digitalSignatureRedirection";
	private static final String PREVIEW = "Preview";
	private static final long serialVersionUID = -396864022983903198L;
	private static final Logger LOGGER = Logger.getLogger(PropertyTaxNoticeAction.class);
	public static final String NOTICE = "notice";
	private static final String VACANT_LAND = "Vacant Land";
	private static final String TAXEXEMPT = "Tax_Exemption";
	private static final String IS_COMMISSIONER = "isCommissioner";
	private static final String AMALGAMATION = "Amalgamation";
	private static final String OBJECTION = "objection";
	private PropertyImpl property;
	private transient ReportService reportService;
	private transient NoticeService noticeService;
	private transient PropertyTaxNumberGenerator propertyTaxNumberGenerator;
	private String reportId;
	private String noticeType;
	private transient InputStream NoticePDF;
	private Long basicPropId;
	private String noticeMode;
	private transient PersistenceService<BasicProperty, Long> basicPropertyService;
	private transient PropertyService propService;
	final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	private String actionType;
	private String basicPropertyIds;
	private String fileStoreIds;
	private String ulbCode;
	private transient RevisionPetitionService revisionPetitionService;
	private String signedFileStoreId;
	private boolean digitalSignEnabled;
	@Autowired
	private transient PtDemandDao ptDemandDAO;

	@Autowired
	@Qualifier("transferOwnerService")
	private transient PropertyTransferService transferOwnerService;

	@Autowired
	private transient InstallmentDao installmentDao;

	@Autowired
	private transient ModuleService moduleDao;

	@Autowired
	private transient PropertyTaxCommonUtils propertyTaxCommonUtils;

	@Autowired
	private transient ReportViewerUtil reportViewerUtil;

	@Autowired
	private transient SecurityUtils securityUtils;
	
	@Autowired
	private transient VacancyRemissionService vacancyRemissionService;
	
	@Autowired
	private transient UserService userService;

	@Autowired
	private transient CityService cityService;

	@Override
	public StateAware getModel() {
		return null;
	}

	@Override
	public void prepare() {
		digitalSignEnabled = propertyTaxCommonUtils.isDigitalSignatureEnabled();
	}

	/**
	 * @return
	 */
	@Action(value = "/notice/propertyTaxNotice-generateBulkNotice")
	public String generateBulkNotice() {
		setUlbCode(ApplicationThreadLocals.getCityCode());
		noticeType = NOTICE_TYPE_SPECIAL_NOTICE;
		actionType = WFLOW_ACTION_STEP_SIGN;
		final String[] entries = basicPropertyIds.split(",");
		final StringBuilder fileStoreId = new StringBuilder();
		for (final String entry : entries) {
			if (!fileStoreId.toString().equals(""))
				fileStoreId.append(",");
			final String[] id = entry.split("~");
			if (CREATE.equalsIgnoreCase(id[1]) || ALTER.equalsIgnoreCase(id[1]) || BIFURCATE.equalsIgnoreCase(id[1]) || AMALGAMATION.equalsIgnoreCase(id[1])) {
				noticeMode = CREATE.equalsIgnoreCase(id[1]) ? CREATE : MODIFY;
				fileStoreId.append(generatePropertyNotice(Long.valueOf(id[0]), id[1]));
			} else if (PropertyTaxConstants.APPLICATION_TYPE_TAX_EXEMTION.equalsIgnoreCase(id[1])
					|| NOTICE_TYPE_EXEMPTION.equalsIgnoreCase(id[1])) {
				final BasicPropertyImpl basicProperty = (BasicPropertyImpl) getPersistenceService()
						.findByNamedQuery(QUERY_BASICPROPERTY_BY_BASICPROPID, Long.valueOf(id[0]));
				property = (PropertyImpl) basicProperty.getProperty();
				if (property == null)
					property = (PropertyImpl) basicProperty.getWFProperty();
				if (property.getTaxExemptedReason() == null) {
					noticeType = NOTICE_TYPE_SPECIAL_NOTICE;
					noticeMode = APPLICATION_TYPE_TAX_EXEMTION;
				} else {
					noticeType = NOTICE_TYPE_EXEMPTION;
					noticeMode = NOTICE_TYPE_EXEMPTION;
				}
				fileStoreId.append(generatePropertyNotice(Long.valueOf(id[0]), id[1]));
				noticeType = NOTICE_TYPE_SPECIAL_NOTICE;
			} else if (GRP.equalsIgnoreCase(id[1]) || RP.equalsIgnoreCase(id[1])) {
				noticeMode = OBJECTION;
				noticeType = id[1].equalsIgnoreCase(RP)
	                                ? NOTICE_TYPE_RPPROCEEDINGS : NOTICE_TYPE_GRPPROCEEDINGS;
				fileStoreId.append(generatePropertyNotice(Long.valueOf(id[0]), id[1]));
			} else if (DEMOLITION.equalsIgnoreCase(id[1])) {
				noticeMode = APPLICATION_TYPE_DEMOLITION;
				final BasicPropertyImpl basicProperty = (BasicPropertyImpl) getPersistenceService()
						.findByNamedQuery(QUERY_BASICPROPERTY_BY_BASICPROPID, Long.valueOf(id[0]));
				if (basicProperty.getWFProperty() != null) {
					basicProperty.getProperty().setStatus(STATUS_ISHISTORY);
					basicProperty.getWFProperty().setStatus(STATUS_ISACTIVE);
					basicPropertyService.update(basicProperty);
				}
				fileStoreId.append(generatePropertyNotice(Long.valueOf(id[0]), id[1]));
			} 
			else {
			    generateNoticeForVRorMutation(fileStoreId, id);
			}
		}
		setFileStoreIds(fileStoreId.toString());
		return DIGITAL_SIGNATURE_REDIRECTION;
	}

        private void generateNoticeForVRorMutation(final StringBuilder fileStoreId, final String[] id) {
            if (VACANCYREMISSIONAPPROVAL.equalsIgnoreCase(id[2])) {
                noticeMode = VACANCYREMISSIONAPPROVAL;
                noticeType = NOTICE_TYPE_VRPROCEEDINGS;
                fileStoreId.append(generatePropertyNotice(Long.valueOf(id[0]), id[2]));
            } else {
                final String cityGrade = cityService.getCityGrade();
                Boolean isCorporation;
                if (cityGrade != null && cityGrade != ""
                        && cityGrade.equalsIgnoreCase(PropertyTaxConstants.CITY_GRADE_CORPORATION))
                    isCorporation = true;
                else
                    isCorporation = false;

                final PropertyMutation propertyMutation = (PropertyMutation) persistenceService
                        .find("From PropertyMutation where id = ? ", Long.valueOf(id[0]));
                final BasicProperty basicProperty = propertyMutation.getBasicProperty();
                transferOwnerService.generateTransferNotice(basicProperty, propertyMutation,
                        WFLOW_ACTION_STEP_SIGN, isCorporation);
                final PtNotice notice = noticeService.getNoticeByNoticeTypeAndApplicationNumber(
                        NOTICE_TYPE_MUTATION_CERTIFICATE, propertyMutation.getApplicationNo());
                fileStoreId.append(notice.getFileStore().getFileStoreId());
            }
        }

	private String generatePropertyNotice(final Long basicPropertyId, final String type) {
		BasicPropertyImpl basicProperty;
		PtNotice notice;
		Position ownerPosition;
		RevisionPetition revisionPetition = null;
		VacancyRemissionApproval vacancyRemissionApproval = null;
		if (GRP.equalsIgnoreCase(type) || RP.equalsIgnoreCase(type)) {
			revisionPetition = revisionPetitionService.findById(basicPropertyId, false);
			ownerPosition = revisionPetition.getCurrentState().getOwnerPosition();
			basicProperty = (BasicPropertyImpl) revisionPetition.getBasicProperty();
			property = (PropertyImpl) basicProperty.getProperty();
			if (property == null)
				property = (PropertyImpl) basicProperty.getWFProperty();
			notice = noticeService.getNoticeByNoticeTypeAndApplicationNumber(noticeType,
					revisionPetition.getObjectionNumber());
		} else if (VACANCYREMISSIONAPPROVAL.equalsIgnoreCase(type)) {
			vacancyRemissionApproval = vacancyRemissionService.getVacancyRemissionApprovalById(basicPropertyId);
			ownerPosition = vacancyRemissionApproval.getCurrentState().getOwnerPosition();
			basicProperty = vacancyRemissionApproval.getVacancyRemission().getBasicProperty();
			notice = noticeService.getNoticeByNoticeTypeAndApplicationNumber(NOTICE_TYPE_VRPROCEEDINGS,
					vacancyRemissionApproval.getVacancyRemission().getApplicationNumber());
		} else {
			basicProperty = (BasicPropertyImpl) getPersistenceService()
					.findByNamedQuery(QUERY_BASICPROPERTY_BY_BASICPROPID, basicPropertyId);
			property = (PropertyImpl) basicProperty.getProperty();
			if (property == null)
				property = (PropertyImpl) basicProperty.getWFProperty();
			ownerPosition = property.getCurrentState().getOwnerPosition();
			notice = noticeService.getNoticeByNoticeTypeAndApplicationNumber(noticeType, property.getApplicationNo());
		}
		ReportOutput reportOutput;
		PropertyNoticeInfo propertyNotice;
		final String noticeNo = propertyTaxNumberGenerator.generateNoticeNumber(noticeType);
		propertyNotice = new PropertyNoticeInfo(property, noticeNo);
		ReportRequest reportInput = getReportInputs(basicProperty, ownerPosition, revisionPetition, vacancyRemissionApproval,
                propertyNotice, noticeNo);
		reportOutput = reportService.createReport(reportInput);
		if (reportOutput != null && reportOutput.getReportOutputData() != null)
			NoticePDF = new ByteArrayInputStream(reportOutput.getReportOutputData());
		PtNotice savedNotice;
		if (notice == null) {
			savedNotice = saveAllNotice(type, basicProperty, revisionPetition, vacancyRemissionApproval, noticeNo);
		} else
			savedNotice = noticeService.updateNotice(notice, NoticePDF);
		noticeService.getSession().flush();
		return savedNotice.getFileStore().getFileStoreId();
	}

        private ReportRequest generateObjectionReportRequest(final RevisionPetition revisionPetition,
                        final PropertyNoticeInfo propertyNotice, final Position ownerPosition) {
                final Map<String, Object> reportParams = new HashMap<>();
                ReportRequest reportInput = null;
                String natureOfWork;
                    getCommonReportParams(ownerPosition, reportParams);
                    if (NATURE_OF_WORK_RP.equalsIgnoreCase(revisionPetition.getType()))
                        natureOfWork = NATURE_REVISION_PETITION;
                else
                        natureOfWork = NATURE_GENERAL_REVISION_PETITION;
                reportParams.put("natureOfWork", natureOfWork);
                revisionPetitionService.setNoticeInfo(revisionPetition.getProperty(), propertyNotice, (BasicPropertyImpl) revisionPetition.getBasicProperty(), revisionPetition);
                final List<PropertyAckNoticeInfo> floorDetails = getFloorDetailsForNotice();
                propertyNotice.setFloorDetailsForNotice(floorDetails);
                reportInput = new ReportRequest(PropertyTaxConstants.REPORT_TEMPLATENAME_RP_SPECIAL_NOTICE, propertyNotice,
                        reportParams);
                reportInput.setPrintDialogOnOpenReport(true);
                reportInput.setReportFormat(ReportFormat.PDF);
                return reportInput;
        }
        
    @Action(value = "/notice/propertyTaxNotice-generateNoticeForActionExemption")
	public String generateNoticeForActionExemption() {
		final BasicPropertyImpl basicProperty = (BasicPropertyImpl) getPersistenceService()
				.findByNamedQuery(QUERY_BASICPROPERTY_BY_BASICPROPID, basicPropId);
		property = (PropertyImpl) basicProperty.getProperty();
		if (property == null)
			property = (PropertyImpl) basicProperty.getWFProperty();
		if (property.getTaxExemptedReason() == null) {
			noticeType = PropertyTaxConstants.NOTICE_TYPE_SPECIAL_NOTICE;
			noticeMode = PropertyTaxConstants.APPLICATION_TYPE_TAX_EXEMTION;
			return generateNotice();
		} else {
			noticeType = NOTICE_TYPE_EXEMPTION;
			noticeMode = NOTICE_TYPE_EXEMPTION;
			return generateExemptionNotice();
		}
	}

	@Action(value = "/notice/propertyTaxNotice-generateNotice")
	public String generateNotice() {
		setUlbCode(ApplicationThreadLocals.getCityCode());
		Position ownerPosition;
		final BasicPropertyImpl basicProperty = (BasicPropertyImpl) getPersistenceService()
				.findByNamedQuery(QUERY_BASICPROPERTY_BY_BASICPROPID, basicPropId);
		property = (PropertyImpl) basicProperty.getProperty();
		if (property == null)
			property = (PropertyImpl) basicProperty.getWFProperty();
		ownerPosition = property.getCurrentState().getOwnerPosition();
		final PtNotice notice = noticeService.getNoticeByNoticeTypeAndApplicationNumber(noticeType,
				property.getApplicationNo());
		ReportOutput reportOutput = new ReportOutput();
		if (WFLOW_ACTION_STEP_NOTICE_GENERATE.equalsIgnoreCase(actionType)) {
			final FileStoreMapper fsm = notice.getFileStore();
			final File file = fileStoreService.fetch(fsm, FILESTORE_MODULE_NAME);
			byte[] bFile;
			try {
				bFile = FileUtils.readFileToByteArray(file);
			} catch (final IOException e) {
				throw new ApplicationRuntimeException("Exception while generating Special Notcie : " + e);
			}
			reportOutput.setReportOutputData(bFile);
			reportOutput.setReportFormat(ReportFormat.PDF);
			reportId = reportViewerUtil.addReportToTempCache(reportOutput);
			endWorkFlow(basicProperty);
		} else {
			PropertyNoticeInfo propertyNotice = null;
			String noticeNo = null;
			if (WFLOW_ACTION_STEP_SIGN.equals(actionType) && notice == null)
				noticeNo = propertyTaxNumberGenerator.generateNoticeNumber(noticeType);
			propertyNotice = new PropertyNoticeInfo(property, noticeNo);
			final ReportRequest reportInput = generateNoticeReportRequest(basicProperty, propertyNotice, ownerPosition);
			reportOutput = reportService.createReport(reportInput);
			if (reportOutput != null && reportOutput.getReportOutputData() != null)
				NoticePDF = new ByteArrayInputStream(reportOutput.getReportOutputData());
			if (WFLOW_ACTION_STEP_SIGN.equals(actionType)) {
				if (notice == null) {
					final PtNotice savedNotice = noticeService.saveNotice(
							basicProperty.getPropertyForBasicProperty().getApplicationNo(), noticeNo, noticeType,
							basicProperty, NoticePDF);
					setFileStoreIds(savedNotice.getFileStore().getFileStoreId());
				} else {
					final PtNotice savedNotice = noticeService.updateNotice(notice, NoticePDF);
					setFileStoreIds(savedNotice.getFileStore().getFileStoreId());
				}
				noticeService.getSession().flush();
				if (Source.CITIZENPORTAL.toString().equalsIgnoreCase(property.getSource()))
				    propService.updatePortalMessage(property, APPLICATION_TYPE_NEW_ASSESSENT);
				return DIGITAL_SIGNATURE_REDIRECTION;
			} else
				reportId = reportViewerUtil.addReportToTempCache(reportOutput);
		}
		if (!PREVIEW.equals(actionType)) {
			noticeService.getSession().flush();
			propService.updateIndexes(property, APPLICATION_TYPE_ALTER_ASSESSENT);
			if (Source.CITIZENPORTAL.toString().equalsIgnoreCase(property.getSource()))
			    propService.updatePortalMessage(property, APPLICATION_TYPE_NEW_ASSESSENT);
			basicPropertyService.update(basicProperty);
		}

		return NOTICE;
	}

	@Action(value = "/notice/propertyTaxNotice-generateExemptionNotice")
	public String generateExemptionNotice() {
		setUlbCode(ApplicationThreadLocals.getCityCode());
		final BasicPropertyImpl basicProperty = (BasicPropertyImpl) getPersistenceService()
				.findByNamedQuery(QUERY_BASICPROPERTY_BY_BASICPROPID, basicPropId);
		property = (PropertyImpl) basicProperty.getProperty();
		if (property == null)
			property = (PropertyImpl) basicProperty.getWFProperty();

		final PtNotice notice = noticeService.getNoticeByNoticeTypeAndApplicationNumber(noticeType,
				property.getApplicationNo());
		ReportOutput reportOutput = new ReportOutput();
		if (WFLOW_ACTION_STEP_NOTICE_GENERATE.equalsIgnoreCase(actionType)) {
			final FileStoreMapper fsm = notice.getFileStore();
			final File file = fileStoreService.fetch(fsm, FILESTORE_MODULE_NAME);
			byte[] bFile;
			try {
				bFile = FileUtils.readFileToByteArray(file);
			} catch (final IOException e) {
				throw new ApplicationRuntimeException("Exception while generating Special Notcie : " + e);
			}
			reportOutput.setReportOutputData(bFile);
			reportOutput.setReportFormat(ReportFormat.PDF);
			reportId = reportViewerUtil.addReportToTempCache(reportOutput);
			endWorkFlow(basicProperty);
		} else {
			PropertyNoticeInfo propertyNotice;
			String noticeNo = null;
			if (WFLOW_ACTION_STEP_SIGN.equals(actionType) && notice == null)
				noticeNo = propertyTaxNumberGenerator.generateNoticeNumber(noticeType);
			propertyNotice = new PropertyNoticeInfo(property, noticeNo);
			final ReportRequest reportInput = generateExemptedNoticeReportRequest(basicProperty, propertyNotice,
					noticeNo);
			reportOutput = reportService.createReport(reportInput);
			if (reportOutput != null && reportOutput.getReportOutputData() != null)
				NoticePDF = new ByteArrayInputStream(reportOutput.getReportOutputData());
			if (WFLOW_ACTION_STEP_SIGN.equals(actionType)) {
				if (notice == null) {
					final PtNotice savedNotice = noticeService.saveNotice(
							basicProperty.getPropertyForBasicProperty().getApplicationNo(), noticeNo, noticeType,
							basicProperty, NoticePDF);
					setFileStoreIds(savedNotice.getFileStore().getFileStoreId());
				} else {
					final PtNotice savedNotice = noticeService.updateNotice(notice, NoticePDF);
					setFileStoreIds(savedNotice.getFileStore().getFileStoreId());
				}
				noticeService.getSession().flush();
				if (Source.CITIZENPORTAL.toString().equalsIgnoreCase(property.getSource()))
				    propService.updatePortal(property, APPLICATION_TYPE_NEW_ASSESSENT);
				return DIGITAL_SIGNATURE_REDIRECTION;
			} else
				reportId = reportViewerUtil.addReportToTempCache(reportOutput);
		}
		if (!PREVIEW.equals(actionType)) {
			propService.updateIndexes(property, APPLICATION_TYPE_ALTER_ASSESSENT);
			if (Source.CITIZENPORTAL.toString().equalsIgnoreCase(property.getSource()))
			    propService.updatePortal(property, APPLICATION_TYPE_NEW_ASSESSENT);
			basicPropertyService.update(basicProperty);
		}
		return NOTICE;
	}

	@Action(value = "/notice/propertyTaxNotice-generateSpecialNotice")
	public String generateSpecialNotice() {
		new HashMap<String, Object>();
		ReportRequest reportInput = null;
		Position ownerPosition = null;
		final BasicPropertyImpl basicProperty = (BasicPropertyImpl) getPersistenceService()
				.findByNamedQuery(QUERY_BASICPROPERTY_BY_BASICPROPID, basicPropId);
		property = (PropertyImpl) basicProperty.getProperty();

		if (property == null)
			property = (PropertyImpl) basicProperty.getWFProperty();
		ownerPosition = property.getCurrentState().getOwnerPosition();
		PropertyNoticeInfo propertyNotice = null;
		final String noticeNo = propertyTaxNumberGenerator.generateNoticeNumber(noticeType);
		propertyNotice = new PropertyNoticeInfo(property, noticeNo);
		reportInput = generateNoticeReportRequest(basicProperty, propertyNotice, ownerPosition);
        ReportOutput reportOutput = reportService.createReport(reportInput);
		reportId = reportViewerUtil.addReportToTempCache(reportOutput);
		if (reportOutput != null && reportOutput.getReportOutputData() != null)
			NoticePDF = new ByteArrayInputStream(reportOutput.getReportOutputData());
		noticeService.saveNotice(basicProperty.getPropertyForBasicProperty().getApplicationNo(), noticeNo, noticeType,
				basicProperty, NoticePDF);
		endWorkFlow(basicProperty);
		propService.updateIndexes(property, APPLICATION_TYPE_TAX_EXEMTION);
		basicPropertyService.update(basicProperty);
		return NOTICE;
	}

	@Action(value = "/notice/previewSignedNotice")
	public String previewSignedNotice() {
		final File file = fileStoreService.fetch(signedFileStoreId, FILESTORE_MODULE_NAME);
		byte[] bFile;
		try {
			bFile = FileUtils.readFileToByteArray(file);
		} catch (final IOException e) {
			throw new ApplicationRuntimeException("Exception while generating Special Notcie : " + e);
		}
		final ReportOutput reportOutput = new ReportOutput();
		reportOutput.setReportOutputData(bFile);
		reportOutput.setReportFormat(ReportFormat.PDF);
		reportId = reportViewerUtil.addReportToTempCache(reportOutput);
		return NOTICE;
	}

	private ReportRequest generateNoticeReportRequest(final BasicPropertyImpl basicProperty,
			final PropertyNoticeInfo propertyNotice, final Position ownerPosition) {
		final Map<String, Object> reportParams = new HashMap<>();
		ReportRequest reportInput = null;
		if (NOTICE_TYPE_SPECIAL_NOTICE.equals(noticeType)) {
		    getCommonReportParams(ownerPosition, reportParams);
		    reportParams.put("isStructure",
                            basicProperty.getProperty().getPropertyDetail().isStructure() ? Boolean.TRUE : Boolean.FALSE);
			if (CREATE.equalsIgnoreCase(noticeMode))
				reportParams.put("mode", CREATE);
			else if (MODIFY.equalsIgnoreCase(noticeMode))
				reportParams.put("mode", MODIFY);
			else if (TAXEXEMPT.equalsIgnoreCase(noticeMode))
				reportParams.put("mode", TAXEXEMPT);
			else
				reportParams.put("mode", APPLICATION_TYPE_DEMOLITION);
			reportParams.put("actionType", actionType);
			setNoticeInfo(propertyNotice, basicProperty, noticeMode);
			final List<PropertyAckNoticeInfo> floorDetails = getFloorDetailsForNotice();
			propertyNotice.setFloorDetailsForNotice(floorDetails);
			reportInput = new ReportRequest(PropertyTaxConstants.REPORT_TEMPLATENAME_SPECIAL_NOTICE, propertyNotice,
					reportParams);
			reportInput.setPrintDialogOnOpenReport(true);
			reportInput.setReportFormat(ReportFormat.PDF);
		}
		return reportInput;
	}

    private void getCommonReportParams(final Position ownerPosition,
            final Map<String, Object> reportParams) {
        List<Assignment> loggedInUserAssignment;
        String loggedInUserDesignation;
        reportParams.put("userSignature", securityUtils.getCurrentUser().getSignature() != null
                ? new ByteArrayInputStream(securityUtils.getCurrentUser().getSignature()) : "");

                final String cityGrade = cityService.getCityGrade();
                Boolean isCorporation;
                reportParams.put("logoPath", cityService.getCityLogoURL());
                reportParams.put("cityName", cityService.getMunicipalityName());
                if (cityGrade != null && cityGrade != ""
                                && cityGrade.equalsIgnoreCase(PropertyTaxConstants.CITY_GRADE_CORPORATION))
                        isCorporation = true;
                else
                        isCorporation = false;
                reportParams.put("isCorporation", isCorporation);
                final User user = securityUtils.getCurrentUser();
                loggedInUserAssignment = assignmentService.getAssignmentByPositionAndUserAsOnDate(ownerPosition.getId(),
                                user.getId(), new Date());
                loggedInUserDesignation = !loggedInUserAssignment.isEmpty()
                                ? loggedInUserAssignment.get(0).getDesignation().getName() : "";
                if (COMMISSIONER_DESGN.equalsIgnoreCase(loggedInUserDesignation))
                        reportParams.put(IS_COMMISSIONER, true);
                else
                        reportParams.put(IS_COMMISSIONER, false);
    }

	private ReportRequest generateExemptedNoticeReportRequest(final BasicPropertyImpl basicProperty,
			final PropertyNoticeInfo propertyNotice, final String noticeNo) {
		final Map<String, Object> reportParams = new HashMap<>();
		List<Assignment> loggedInUserAssignment;
		String loggedInUserDesignation;
		ReportRequest reportInput;
		reportParams.put("userSignature", securityUtils.getCurrentUser().getSignature() != null
				? new ByteArrayInputStream(securityUtils.getCurrentUser().getSignature()) : "");
		reportParams.put("noticeNo", noticeNo);
		String installment = null;
		final Installment currInstallment = propertyTaxCommonUtils.getCurrentInstallment();
		final Map<String, Installment> currInstallments = propertyTaxUtil.getInstallmentsForCurrYear(new Date());
		for (final Entry<String, Installment> entry : currInstallments.entrySet())
			if (currInstallment.equals(entry.getValue()))
				installment = entry.getKey();
		BigDecimal currentPropertyTax;
		final Map<String, BigDecimal> propertyTaxDetails = ptDemandDAO.getDemandCollMap(basicProperty.getProperty());
		if (CURRENTYEAR_FIRST_HALF.equalsIgnoreCase(installment))
			currentPropertyTax = propertyTaxDetails.get("CURR_FIRSTHALF_DMD");
		else
			currentPropertyTax = propertyTaxDetails.get("CURR_SECONDHALF_DMD");
		final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		reportParams.put(CURRENT_PROPERTY_TAX, currentPropertyTax);
		reportParams.put(APPLICATION_DATE, formatter.format(basicProperty.getActiveProperty().getCreatedDate()));
		reportParams.put(OWNER_NAME, basicProperty.getFullOwnerName().trim());
		reportParams.put(DOOR_NUMBER, basicProperty.getAddress().getHouseNoBldgApt());
		reportParams.put(ASSESSMENT_NUMBER, basicProperty.getUpicNo());
		reportParams.put(LOCALITY, basicProperty.getPropertyID().getLocality().getName().trim());
		reportParams.put(NOTICE_DATE, formatter.format(new Date()));
		reportParams.put(COMMISSIONER, securityUtils.getCurrentUser().getName());
		reportParams.put(EXEMPTION_REASON, basicProperty.getProperty().getTaxExemptedReason().getName());
		final String cityGrade = cityService.getCityGrade();
		Boolean isCorporation;
		reportParams.put("logoPath", cityService.getCityLogoURL());
		reportParams.put("cityName", cityService.getMunicipalityName());
		if (cityGrade != null && cityGrade != ""
				&& cityGrade.equalsIgnoreCase(PropertyTaxConstants.CITY_GRADE_CORPORATION))
			isCorporation = true;
		else
			isCorporation = false;
		reportParams.put("isCorporation", isCorporation);
		reportParams.put("actionType", actionType);

		final User user = securityUtils.getCurrentUser();
		loggedInUserAssignment = assignmentService.getAssignmentByPositionAndUserAsOnDate(
				property.getCurrentState().getOwnerPosition().getId(), user.getId(), new Date());
		loggedInUserDesignation = !loggedInUserAssignment.isEmpty()
				? loggedInUserAssignment.get(0).getDesignation().getName() : "";
		if (COMMISSIONER_DESGN.equalsIgnoreCase(loggedInUserDesignation))
			reportParams.put(IS_COMMISSIONER, true);
		else
			reportParams.put(IS_COMMISSIONER, false);

		setNoticeInfo(propertyNotice, basicProperty, noticeMode);
		reportInput = getReportByExemptionReason(propertyNotice,
				basicProperty.getProperty().getTaxExemptedReason().getCode(), reportParams);
		if (reportInput != null) {
			reportInput.setPrintDialogOnOpenReport(true);
			reportInput.setReportFormat(ReportFormat.PDF);
		}
		return reportInput;
	}

	private ReportRequest getReportByExemptionReason(final PropertyNoticeInfo propertyNotice,
			final String exemptionReason, final Map<String, Object> reportParams) {
		ReportRequest reportInput = null;
		if (EXEMPTION_NGO.equalsIgnoreCase(exemptionReason))
			reportInput = new ReportRequest(REPORT_NGO_EXEMPTION_NOTICE, propertyNotice, reportParams);
		else if (EXEMPTION_PENSIONER.equalsIgnoreCase(exemptionReason))
			reportInput = new ReportRequest(REPORT_RETIRED_EXEMPTION_NOTICE, propertyNotice, reportParams);
		else if (EXEMPTION_EDU_INST.equalsIgnoreCase(exemptionReason))
			reportInput = new ReportRequest(REPORT_EDU_INST_EXEMPTION_NOTICE, propertyNotice, reportParams);
		else if (EXEMPTION_EXSERVICE.equalsIgnoreCase(exemptionReason)
				|| "EX-SERVICE".equalsIgnoreCase(exemptionReason))
			reportInput = new ReportRequest(REPORT_EXSRVICE_EXEMPTION_NOTICE, propertyNotice, reportParams);
		else if (EXEMPTION_CHOULTRY.equalsIgnoreCase(exemptionReason))
			reportInput = new ReportRequest(REPORT_CHOULTRY_EXEMPTION_NOTICE, propertyNotice, reportParams);
		else if (EXEMPTION_PUBLIC_WORSHIP.equalsIgnoreCase(exemptionReason))
			reportInput = new ReportRequest(REPORT_PUBLIC_WORSHIP_EXEMPTION_NOTICE, propertyNotice, reportParams);
		return reportInput;
	}

	private void setNoticeInfo(final PropertyNoticeInfo propertyNotice, final BasicPropertyImpl basicProperty,
			final String noticeMode) {
		String ownerType = null;
		String owner = "";
		final PropertyAckNoticeInfo infoBean = new PropertyAckNoticeInfo();
		final Address ownerAddress = basicProperty.getAddress();
		BigDecimal totalTax = BigDecimal.ZERO;
		BigDecimal propertyTax = BigDecimal.ZERO;

		infoBean.setOwnerName(basicProperty.getFullOwnerName());
		infoBean.setOwnerAddress(basicProperty.getAddress().toString());
		infoBean.setApplicationNo(property.getApplicationNo());
		infoBean.setDoorNo(ownerAddress.getHouseNoBldgApt());
		if (org.apache.commons.lang.StringUtils.isNotBlank(ownerAddress.getLandmark()))
			infoBean.setStreetName(ownerAddress.getLandmark());
		else
			infoBean.setStreetName("N/A");
		final SimpleDateFormat formatNowYear = new SimpleDateFormat("MMMM yyyy");
		final Module module = moduleDao.getModuleByName(PTMODULENAME);
		infoBean.setAssessmentNo(basicProperty.getUpicNo());
		infoBean.setAssessmentDate(sdf.format(basicProperty.getAssessmentdate()).toString());
		Ptdemand currDemand = null;
		Installment installment = null;
		String occupancyYear = "";
		if (noticeMode.equalsIgnoreCase(CREATE)) {
			// Sets data for the current property
			currDemand = ptDemandDAO.getNonHistoryCurrDmdForProperty(property);
			prepareTaxInfoForActiveProperty(infoBean, totalTax, propertyTax, currDemand, noticeMode);
			if (currDemand.getDmdCalculations() != null && currDemand.getDmdCalculations().getAlv() != null)
				infoBean.setNew_rev_ARV(currDemand.getDmdCalculations().getAlv());
			installment = installmentDao.getInsatllmentByModuleForGivenDate(module,
					basicProperty.getPropOccupationDate());
			occupancyYear = formatNowYear.format(installment.getFromDate());
			infoBean.setInstallmentYear(occupancyYear);
		} else
			installment = installmentDao.getInsatllmentByModuleForGivenDate(module, property.getEffectiveDate());
		if (noticeMode.equalsIgnoreCase(MODIFY) || noticeMode.equalsIgnoreCase(DEMOLITION)) {
			// Sets data for the current property
			currDemand = ptDemandDAO.getNonHistoryCurrDmdForProperty(property);
			prepareTaxInfoForActiveProperty(infoBean, totalTax, propertyTax, currDemand, noticeMode);
			if (currDemand.getDmdCalculations() != null && currDemand.getDmdCalculations().getAlv() != null)
				infoBean.setNew_rev_ARV(currDemand.getDmdCalculations().getAlv());

			// Sets data for the latest history property
			final PropertyImpl historyProperty = propService.getLatestHistoryProperty(basicProperty.getUpicNo());
			final Ptdemand historyDemand = ptDemandDAO.getNonHistoryCurrDmdForProperty(historyProperty);
			if (historyProperty != null && historyDemand != null) {
				totalTax = BigDecimal.ZERO;
				propertyTax = BigDecimal.ZERO;
				prepareTaxInfoForHistoryProperty(infoBean, totalTax, propertyTax, historyDemand);
				if (historyDemand.getDmdCalculations() != null && historyDemand.getDmdCalculations().getAlv() != null)
					infoBean.setExistingARV(historyDemand.getDmdCalculations().getAlv());
			}
		} else if (noticeMode.equalsIgnoreCase(TAXEXEMPT))
			if (property.getIsExemptedFromTax()) {
				currDemand = ptDemandDAO.getNonHistoryCurrDmdForProperty(property);
				prepareTaxInfoForHistoryProperty(infoBean, totalTax, propertyTax, currDemand);
				infoBean.setExistingARV(currDemand.getDmdCalculations().getAlv() != null
						? currDemand.getDmdCalculations().getAlv() : BigDecimal.ZERO);
			} else if (!property.getIsExemptedFromTax()) {
				final PropertyImpl historyProperty = propService.getLatestHistoryProperty(basicProperty.getUpicNo());
				final Ptdemand historyDemand = ptDemandDAO.getNonHistoryCurrDmdForProperty(historyProperty);
				if (historyProperty != null && historyDemand != null) {
					totalTax = BigDecimal.ZERO;
					propertyTax = BigDecimal.ZERO;
					prepareTaxInfoForActiveProperty(infoBean, totalTax, propertyTax, historyDemand, noticeMode);
				}
			}
		occupancyYear = formatNowYear.format(installment.getFromDate());
		infoBean.setInstallmentYear(occupancyYear);
		final PropertyID propertyId = basicProperty.getPropertyID();
		infoBean.setZoneName(propertyId.getZone().getName());
		infoBean.setWardName(propertyId.getWard().getName());
		infoBean.setAreaName(propertyId.getArea().getName());
		infoBean.setLocalityName(propertyId.getLocality().getName());
		if (checkMeesevaSource(property))
			infoBean.setMeesevaNo(property.getApplicationNo());
		infoBean.setNoticeDate(new Date());
		ownerType = property.getPropertyDetail().getPropertyTypeMaster().getType();
		infoBean.setOwnershipType(ownerType);
		if (ownerType.equalsIgnoreCase(VACANT_LAND) || noticeMode != null && noticeMode.equalsIgnoreCase(DEMOLITION)) {
			owner = "(On Land)";
			infoBean.setExtentOfSite(new BigDecimal(property.getPropertyDetail().getSitalArea().getArea()));
			infoBean.setSurveyNumber(property.getPropertyDetail().getSurveyNumber());
			infoBean.setNorthBoundary(propertyId.getNorthBoundary());
			infoBean.setSouthBoundary(propertyId.getSouthBoundary());
			infoBean.setEastBoundary(propertyId.getEastBoundary());
			infoBean.setWestBoundary(propertyId.getWestBoundary());
		}
		infoBean.setOwnerTypeForReport(owner);
		propertyNotice.setOwnerInfo(infoBean);

	}

	/**
	 * Sets data for the current property(new/modify)
	 */
	private void prepareTaxInfoForActiveProperty(final PropertyAckNoticeInfo infoBean, BigDecimal totalTax,
			BigDecimal propertyTax, final Ptdemand currDemand, final String noticeMode) {
		for (final EgDemandDetails demandDetail : currDemand.getEgDemandDetails())
			if (demandDetail.getEgDemandReason().getEgInstallmentMaster()
					.equals(propertyTaxCommonUtils.getCurrentPeriodInstallment())) {
				if (!demandDetail.getEgDemandReason().getEgDemandReasonMaster().getCode()
						.equalsIgnoreCase(PropertyTaxConstants.DEMANDRSN_CODE_PENALTY_FINES))
					totalTax = totalTax.add(demandDetail.getAmount());
				if (demandDetail.getEgDemandReason().getEgDemandReasonMaster().getCode()
						.equalsIgnoreCase(PropertyTaxConstants.DEMANDRSN_CODE_EDUCATIONAL_TAX))
					propertyTax = propertyTax.add(demandDetail.getAmount());
				if (demandDetail.getEgDemandReason().getEgDemandReasonMaster().getCode()
						.equalsIgnoreCase(PropertyTaxConstants.DEMANDRSN_CODE_LIBRARY_CESS)) {
					if (noticeMode.equalsIgnoreCase(CREATE))
						infoBean.setNewLibraryCess(demandDetail.getAmount());
					if (noticeMode.equalsIgnoreCase(MODIFY) || noticeMode.equalsIgnoreCase(TAXEXEMPT)
							|| noticeMode.equalsIgnoreCase(DEMOLITION))
						infoBean.setRevLibraryCess(demandDetail.getAmount());
				}

				if (NON_VACANT_TAX_DEMAND_CODES.contains(demandDetail.getEgDemandReason().getEgDemandReasonMaster().getCode())
						|| demandDetail.getEgDemandReason().getEgDemandReasonMaster().getCode()
								.equalsIgnoreCase(PropertyTaxConstants.DEMANDRSN_CODE_VACANT_TAX))
					propertyTax = propertyTax.add(demandDetail.getAmount());
				if (demandDetail.getEgDemandReason().getEgDemandReasonMaster().getCode()
						.equalsIgnoreCase(PropertyTaxConstants.DEMANDRSN_CODE_UNAUTHORIZED_PENALTY)) {
					if (noticeMode.equalsIgnoreCase(CREATE))
						infoBean.setNewUCPenalty(demandDetail.getAmount());
					if (noticeMode.equalsIgnoreCase(MODIFY) || noticeMode.equalsIgnoreCase(TAXEXEMPT)
							|| noticeMode.equalsIgnoreCase(DEMOLITION))
						infoBean.setRevUCPenalty(demandDetail.getAmount());
				}
			}
		if (noticeMode.equalsIgnoreCase(CREATE)) {
			infoBean.setNewTotalTax(totalTax);
			infoBean.setNewPropertyTax(propertyTax);
		}
		if (noticeMode.equalsIgnoreCase(MODIFY) || noticeMode.equalsIgnoreCase(TAXEXEMPT)
				|| noticeMode.equalsIgnoreCase(DEMOLITION)) {
			infoBean.setRevTotalTax(totalTax);
			infoBean.setRevPropertyTax(propertyTax);
		}
	}

	/**
	 * Sets data for the latest history property
	 */
	private void prepareTaxInfoForHistoryProperty(final PropertyAckNoticeInfo infoBean, BigDecimal totalTax,
			BigDecimal propertyTax, final Ptdemand currDemand) {
		for (final EgDemandDetails demandDetail : currDemand.getEgDemandDetails())
			if (demandDetail.getEgDemandReason().getEgInstallmentMaster()
					.equals(propertyTaxCommonUtils.getCurrentPeriodInstallment())) {
				if (!demandDetail.getEgDemandReason().getEgDemandReasonMaster().getCode()
						.equalsIgnoreCase(PropertyTaxConstants.DEMANDRSN_CODE_PENALTY_FINES))
					totalTax = totalTax.add(demandDetail.getAmount());
				if (demandDetail.getEgDemandReason().getEgDemandReasonMaster().getCode()
						.equalsIgnoreCase(PropertyTaxConstants.DEMANDRSN_CODE_EDUCATIONAL_TAX))
					propertyTax = propertyTax.add(demandDetail.getAmount());
				if (demandDetail.getEgDemandReason().getEgDemandReasonMaster().getCode()
						.equalsIgnoreCase(PropertyTaxConstants.DEMANDRSN_CODE_LIBRARY_CESS))
					infoBean.setExistingLibraryCess(demandDetail.getAmount());
				if (NON_VACANT_TAX_DEMAND_CODES.contains(demandDetail.getEgDemandReason().getEgDemandReasonMaster().getCode())
						|| demandDetail.getEgDemandReason().getEgDemandReasonMaster().getCode()
								.equalsIgnoreCase(PropertyTaxConstants.DEMANDRSN_CODE_VACANT_TAX))
					propertyTax = propertyTax.add(demandDetail.getAmount());
				if (demandDetail.getEgDemandReason().getEgDemandReasonMaster().getCode()
						.equalsIgnoreCase(PropertyTaxConstants.DEMANDRSN_CODE_UNAUTHORIZED_PENALTY))
					infoBean.setExistingUCPenalty(demandDetail.getAmount());
			}
		infoBean.setExistingTotalTax(totalTax);
		infoBean.setExistingPropertyTax(propertyTax);
	}

	private List<PropertyAckNoticeInfo> getFloorDetailsForNotice() {
		final List<PropertyAckNoticeInfo> floorDetailsList = new ArrayList<>();
		final PropertyDetail detail = property.getPropertyDetail();
		PropertyAckNoticeInfo floorInfo = null;
		for (final Floor floor : detail.getFloorDetails()) {
			floorInfo = new PropertyAckNoticeInfo();
			floorInfo.setBuildingClassification(floor.getStructureClassification().getTypeName());
			floorInfo.setNatureOfUsage(floor.getPropertyUsage().getUsageName());
			floorInfo.setPlinthArea(new BigDecimal(floor.getBuiltUpArea().getArea()));
			floorInfo.setBuildingAge(floor.getDepreciationMaster() != null
					? floor.getDepreciationMaster().getDepreciationName() : "N/A");
			floorInfo.setMonthlyRentalValue(
					floor.getFloorDmdCalc() != null ? floor.getFloorDmdCalc().getMrv() : BigDecimal.ZERO);
			floorInfo.setYearlyRentalValue(
					floor.getFloorDmdCalc() != null ? floor.getFloorDmdCalc().getAlv() : BigDecimal.ZERO);
			floorInfo.setTaxPayableForCurrYear(
					floor.getFloorDmdCalc() != null ? floor.getFloorDmdCalc().getTotalTaxPayble() : BigDecimal.ZERO);
			floorInfo.setRate(
					floor.getFloorDmdCalc() != null ? floor.getFloorDmdCalc().getCategoryAmt() : BigDecimal.ZERO);
			floorInfo.setBldngFloorNo(FLOOR_MAP.get(floor.getFloorNo()));
			floorDetailsList.add(floorInfo);
		}
		return floorDetailsList;
	}

	/**
	 * This method ends the workflow. The Property is transitioned to END state.
	 */
	private void endWorkFlow(final BasicPropertyImpl basicProperty) {
		LOGGER.debug("endWorkFlow: Workflow will end for Property: " + property);
		property.transition().end().withNextAction(null);
		basicProperty.setUnderWorkflow(false);
		LOGGER.debug("Exit method endWorkFlow, Workflow ended");
	}

	Boolean checkMeesevaSource(final PropertyImpl property) {
		return property.getSource() != null ? property.getSource().equals(PropertyTaxConstants.SOURCE_MEESEVA) : false;
	}

	private PtNotice saveAllNotice(final String type, BasicPropertyImpl basicProperty, RevisionPetition revisionPetition,
	            VacancyRemissionApproval vacancyRemissionApproval, final String noticeNo) {
	        PtNotice savedNotice;
	        if (GRP.equalsIgnoreCase(type) || RP.equalsIgnoreCase(type))
	                savedNotice = noticeService.saveNotice(revisionPetition.getObjectionNumber(),
	                        noticeNo,
	                        type.equalsIgnoreCase(RP)
	                                ? NOTICE_TYPE_RPPROCEEDINGS : NOTICE_TYPE_GRPPROCEEDINGS, revisionPetition.getBasicProperty(),
	                                NoticePDF);
	        else if (VACANCYREMISSIONAPPROVAL.equalsIgnoreCase(type))
	                        savedNotice = noticeService.saveNotice(vacancyRemissionApproval.getVacancyRemission().getApplicationNumber(),
	                                noticeNo,NOTICE_TYPE_VRPROCEEDINGS, vacancyRemissionApproval.getVacancyRemission().getBasicProperty(),
	                                        NoticePDF);
	        else
	                savedNotice = noticeService.saveNotice(basicProperty.getPropertyForBasicProperty().getApplicationNo(),
	                                noticeNo, noticeType, basicProperty, NoticePDF);
	        return savedNotice;
	}
	
	private ReportRequest getReportInputs(BasicPropertyImpl basicProperty, Position ownerPosition,
	            RevisionPetition revisionPetition, VacancyRemissionApproval vacancyRemissionApproval,
	            PropertyNoticeInfo propertyNotice, final String noticeNo) {
	        ReportRequest reportInput = null;
	                if (!noticeMode.equalsIgnoreCase(NOTICE_TYPE_EXEMPTION) && !noticeMode.equalsIgnoreCase(VACANCYREMISSIONAPPROVAL) && !noticeMode.equalsIgnoreCase(OBJECTION))
	                        reportInput = generateNoticeReportRequest(basicProperty, propertyNotice, ownerPosition);
	                else if (noticeMode.equalsIgnoreCase(NOTICE_TYPE_EXEMPTION))
	                        reportInput = generateExemptedNoticeReportRequest(basicProperty, propertyNotice, noticeNo);
	                else if (noticeMode.equalsIgnoreCase(OBJECTION))
	                    reportInput = generateObjectionReportRequest(revisionPetition, propertyNotice, ownerPosition);
	                else if (noticeMode.equalsIgnoreCase(VACANCYREMISSIONAPPROVAL))
	                    reportInput = vacancyRemissionService.generateVRReportRequest(vacancyRemissionApproval.getVacancyRemission(), noticeNo, ServletActionContext.getRequest(),userService.getUserById(ApplicationThreadLocals.getUserId()).getName());
	        return reportInput;
	}

	public void setReportService(final ReportService reportService) {
		this.reportService = reportService;
	}

	public String getReportId() {
		return reportId;
	}

	@Override
	public PropertyImpl getProperty() {
		return property;
	}

	@Override
	public void setProperty(final PropertyImpl property) {
		this.property = property;
	}

	public void setPropertyTaxNumberGenerator(final PropertyTaxNumberGenerator propertyTaxNumberGenerator) {
		this.propertyTaxNumberGenerator = propertyTaxNumberGenerator;
	}

	public String getNoticeType() {
		return noticeType;
	}

	public void setNoticeType(final String noticeType) {
		this.noticeType = noticeType;
	}

	public NoticeService getNoticeService() {
		return noticeService;
	}

	public void setNoticeService(final NoticeService noticeService) {
		this.noticeService = noticeService;
	}

	public Long getBasicPropId() {
		return basicPropId;
	}

	public void setBasicPropId(final Long basicPropId) {
		this.basicPropId = basicPropId;
	}

	public String getNoticeMode() {
		return noticeMode;
	}

	public void setNoticeMode(final String noticeMode) {
		this.noticeMode = noticeMode;
	}

	public void setBasicPropertyService(final PersistenceService<BasicProperty, Long> basicPropertyService) {
		this.basicPropertyService = basicPropertyService;
	}

	public void setPtDemandDAO(final PtDemandDao ptDemandDAO) {
		this.ptDemandDAO = ptDemandDAO;
	}

	public PropertyService getPropService() {
		return propService;
	}

	public void setPropService(final PropertyService propService) {
		this.propService = propService;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(final String actionType) {
		this.actionType = actionType;
	}

	public String getBasicPropertyIds() {
		return basicPropertyIds;
	}

	public void setBasicPropertyIds(final String basicPropertyIds) {
		this.basicPropertyIds = basicPropertyIds;
	}

	public String getFileStoreIds() {
		return fileStoreIds;
	}

	public void setFileStoreIds(final String fileStoreIds) {
		this.fileStoreIds = fileStoreIds;
	}

	public String getUlbCode() {
		return ulbCode;
	}

	public void setUlbCode(final String ulbCode) {
		this.ulbCode = ulbCode;
	}

	public RevisionPetitionService getRevisionPetitionService() {
		return revisionPetitionService;
	}

	public void setRevisionPetitionService(final RevisionPetitionService revisionPetitionService) {
		this.revisionPetitionService = revisionPetitionService;
	}

	public String getSignedFileStoreId() {
		return signedFileStoreId;
	}

	public void setSignedFileStoreId(final String signedFileStoreId) {
		this.signedFileStoreId = signedFileStoreId;
	}

	public boolean isDigitalSignEnabled() {
		return digitalSignEnabled;
	}

	public void setDigitalSignEnabled(final boolean digitalSignEnabled) {
		this.digitalSignEnabled = digitalSignEnabled;
	}

}
