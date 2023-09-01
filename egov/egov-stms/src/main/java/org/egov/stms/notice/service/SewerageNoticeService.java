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
package org.egov.stms.notice.service;

import static org.egov.infra.utils.DateUtils.getDefaultFormattedDate;
import static org.egov.stms.utils.constants.SewerageTaxConstants.FILESTORE_MODULECODE;
import static org.egov.stms.utils.constants.SewerageTaxConstants.NOTICE_TYPE_DEMAND_BILL_NOTICE;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.egov.demand.model.EgDemand;
import org.egov.demand.model.EgDemandDetails;
import org.egov.demand.model.EgdmCollectedReceipt;
import org.egov.eis.entity.Assignment;
import org.egov.eis.service.AssignmentService;
import org.egov.eis.service.DesignationService;
import org.egov.infra.admin.master.entity.Module;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.CityService;
import org.egov.infra.admin.master.service.ModuleService;
import org.egov.infra.filestore.entity.FileStoreMapper;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.reporting.engine.ReportOutput;
import org.egov.infra.reporting.engine.ReportRequest;
import org.egov.infra.reporting.engine.ReportService;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.utils.NumberToWordConverter;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.ptis.domain.model.AssessmentDetails;
import org.egov.ptis.domain.model.OwnerName;
import org.egov.ptis.domain.service.property.PropertyExternalService;
import org.egov.stms.notice.entity.SewerageNotice;
import org.egov.stms.transactions.entity.SewerageApplicationDetails;
import org.egov.stms.transactions.entity.SewerageConnectionFee;
import org.egov.stms.transactions.repository.SewerageNoticeRepository;
import org.egov.stms.utils.SewerageTaxUtils;
import org.egov.stms.utils.constants.SewerageTaxConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Transactional(readOnly = true)
public class SewerageNoticeService {

    private static final String DISTRICT = "district";
    private static final String ASSESSMENT_NO = "assessmentNo";
    private static final String EXCEPTION_IN_ADD_FILES_TO_ZIP = "Exception in addFilesToZip : ";
    private static final String PRESENT_COMMISSIONER = "presentCommissioner";
    private static final String SEWERAGE_TAX = "sewerageTax";
    private static final String APPLICATION_TYPE = "applicationType";
    private static final String ESTIMATION_CHARGES = "estimationCharges";
    private static final String DONATION_CHARGES = "donationCharges";
    private static final String ADDRESS = "address";
    private static final String APPLICATION_DATE = "applicationDate";
    private static final String TOTAL_CHARGES = "totalCharges";
    private static final String NO_OF_SEATS_RESIDENTIAL = "noOfSeatsResidential";
    private static final String NO_OF_SEATS_NON_RESIDENTIAL = "noOfSeatsNonResidential";
    public static final String ESTIMATION_NOTICE = "sewerageEstimationNotice";
    public static final String REJECTION_NOTICE = "sewerageRejectionNotice";
    public static final String SANCTION_NOTICE = "sewerageSanctionNotice";
    public static final String CLOSECONNECTIONNOTICE = "sewerageCloseConnectionNotice";
    private static final Logger LOGGER = Logger.getLogger(SewerageNoticeService.class);
    private static final String APPLICATION_PDF = "application/pdf";
    @Autowired
    @Qualifier("fileStoreService")
    private FileStoreService fileStoreService;
    @Autowired
    private SecurityUtils securityUtils;
    @Autowired
    private ModuleService moduleDao;
    @Autowired
    private SewerageNoticeRepository sewerageNoticeRepository;
    private InputStream generateNoticePDF;
    @Autowired
    private ReportService reportService;
    @Autowired
    private SewerageTaxUtils sewerageTaxUtils;
    @Autowired
    @Qualifier("parentMessageSource")
    private MessageSource stmsMessageSource;
    @Autowired
    private AssignmentService assignmentService;
    @Autowired
    private DesignationService designationService;

    @Autowired
    private CityService cityService;

    private BigDecimal donationCharges = BigDecimal.ZERO;
    private BigDecimal sewerageCharges = BigDecimal.ZERO;
    private BigDecimal estimationCharges = BigDecimal.ZERO;
    private static final String IS_COMMISSIONER = "isCommissioner";

    public SewerageNotice findByNoticeTypeAndApplicationNumber(final String noticeType, final String applicationNumber) {
        return sewerageNoticeRepository.findByNoticeTypeAndApplicationNumber(noticeType, applicationNumber);
    }

    public List<SewerageNotice> findByNoticeType(final String noticeType) {
        return sewerageNoticeRepository.findByNoticeType(noticeType);
    }

    public SewerageNotice findByNoticeNoAndNoticeType(final String noticeNo, final String noticeType) {
        return sewerageNoticeRepository.findByNoticeNoAndNoticeType(noticeNo, noticeType);
    }
    
    public SewerageNotice saveEstimationNotice(final SewerageApplicationDetails sewerageApplicationDetails,
            final InputStream fileStream) {
        SewerageNotice sewerageNotice = null;

        if (sewerageApplicationDetails != null) {
            sewerageNotice = new SewerageNotice();

            final String estNoticeNo = sewerageApplicationDetails.getEstimationNumber();
            buildSewerageNotice(sewerageApplicationDetails, sewerageNotice, estNoticeNo,
                    sewerageApplicationDetails.getEstimationDate(), SewerageTaxConstants.NOTICE_TYPE_ESTIMATION_NOTICE);
            final String fileName = estNoticeNo + ".pdf";
            final FileStoreMapper fileStore = fileStoreService.store(fileStream, fileName, APPLICATION_PDF,
                    SewerageTaxConstants.FILESTORE_MODULECODE);
            sewerageNotice.setFileStore(fileStore);
        }
        return sewerageNotice;
    }

    public SewerageNotice saveRejectionNotice(final SewerageApplicationDetails sewerageApplicationDetails,
            final InputStream fileStream) {
        SewerageNotice sewerageNotice = null;

        if (sewerageApplicationDetails != null) {
            sewerageNotice = new SewerageNotice();

            final String rejectionNoticeNo = sewerageApplicationDetails.getRejectionNumber();
            buildSewerageNotice(sewerageApplicationDetails, sewerageNotice, rejectionNoticeNo,
                    sewerageApplicationDetails.getEstimationDate(), SewerageTaxConstants.NOTICE_TYPE_REJECTION_NOTICE);
            final String fileName = rejectionNoticeNo + ".pdf";
            final FileStoreMapper fileStore = fileStoreService.store(fileStream, fileName, APPLICATION_PDF,
                    SewerageTaxConstants.FILESTORE_MODULECODE);
            sewerageNotice.setFileStore(fileStore);
        }
        return sewerageNotice;
    }

    private void buildSewerageNotice(final SewerageApplicationDetails sewerageApplicationDetails,
            final SewerageNotice sewerageNotice, final String noticeNumber, final Date noticeDate, final String noticeType) {
        final Module module = moduleDao.getModuleByName(SewerageTaxConstants.MODULE_NAME);
        sewerageNotice.setModule(module);
        sewerageNotice.setApplicationNumber(sewerageApplicationDetails.getApplicationNumber());
        sewerageNotice.setNoticeType(noticeType);
        sewerageNotice.setNoticeNo(noticeNumber);
        sewerageNotice.setNoticeDate(noticeDate);
        sewerageNotice.setApplicationDetails(sewerageApplicationDetails);
    }

    public SewerageNotice saveWorkOrderNotice(final SewerageApplicationDetails sewerageApplicationDetails,
            final InputStream fileStream) {

        SewerageNotice sewerageNotice = null;
        if (sewerageApplicationDetails != null) {
            sewerageNotice = new SewerageNotice();
            final String workOrederNo = sewerageApplicationDetails.getWorkOrderNumber();

            buildSewerageNotice(sewerageApplicationDetails, sewerageNotice, workOrederNo,
                    sewerageApplicationDetails.getWorkOrderDate(), SewerageTaxConstants.NOTICE_TYPE_WORK_ORDER_NOTICE);

            final String fileName = workOrederNo + ".pdf";
            final FileStoreMapper fileStore = fileStoreService.store(fileStream, fileName, APPLICATION_PDF,
                    SewerageTaxConstants.FILESTORE_MODULECODE);
            sewerageNotice.setFileStore(fileStore);
        }
        return sewerageNotice;
    }

    public SewerageNotice generateReportForEstimation(final SewerageApplicationDetails sewerageApplicationDetails) {
        ReportOutput reportOutput = null;
        SewerageNotice sewerageNotice = null;
        reportOutput = generateReportOutputDataForEstimation(sewerageApplicationDetails);
        if (reportOutput != null && reportOutput.getReportOutputData() != null) {
            generateNoticePDF = new ByteArrayInputStream(reportOutput.getReportOutputData());
            sewerageNotice = saveEstimationNotice(sewerageApplicationDetails, generateNoticePDF);
        }
        return sewerageNotice;
    }

    public SewerageNotice generateReportForRejection(final SewerageApplicationDetails sewerageApplicationDetails,
            final HttpSession session, final HttpServletRequest request) {
        SewerageNotice sewerageNotice = null;
        final ReportOutput reportOutput = generateReportOutputDataForRejection(sewerageApplicationDetails, session, request);
        if (reportOutput != null && reportOutput.getReportOutputData() != null) {
            generateNoticePDF = new ByteArrayInputStream(reportOutput.getReportOutputData());
            sewerageNotice = saveRejectionNotice(sewerageApplicationDetails, generateNoticePDF);
        }
        return sewerageNotice;
    }

    public SewerageNotice generateReportForWorkOrder(final SewerageApplicationDetails sewerageApplicationDetails) {
        SewerageNotice sewerageNotice = null;
        final ReportOutput reportOutput = generateReportOutputForWorkOrder(sewerageApplicationDetails);
        if (reportOutput != null && reportOutput.getReportOutputData() != null) {
            generateNoticePDF = new ByteArrayInputStream(reportOutput.getReportOutputData());
            sewerageNotice = saveWorkOrderNotice(sewerageApplicationDetails, generateNoticePDF);
        }
        return sewerageNotice;
    }

    public ReportOutput generateReportOutputDataForEstimation(
            final SewerageApplicationDetails sewerageApplicationDetails) {
        ReportRequest reportInput = null;
        final Map<String, Object> reportParams = new HashMap<>();
        if (sewerageApplicationDetails != null) {
            final AssessmentDetails assessmentDetails = sewerageTaxUtils.getAssessmentDetailsForFlag(
                    sewerageApplicationDetails.getConnectionDetail().getPropertyIdentifier(),
                    PropertyExternalService.FLAG_FULL_DETAILS);
            String[] doorNo = null;
            if (null != assessmentDetails.getPropertyAddress())
                doorNo = assessmentDetails.getPropertyAddress().split(",");
            String ownerName = "";
            if (null != assessmentDetails.getOwnerNames())
                for (final OwnerName names : assessmentDetails.getOwnerNames()) {
                    ownerName = names.getOwnerName();
                    break;
                }

            if (sewerageApplicationDetails
                    .getApplicationType() != null)
                reportParams.put(APPLICATION_TYPE,
                        WordUtils.capitalize(sewerageApplicationDetails.getApplicationType().getName()));
            reportParams.put("cityName", cityService.getMunicipalityName());
            reportParams.put(DISTRICT, cityService.getDistrictName());
            reportParams.put("estimationDate", getDefaultFormattedDate(sewerageApplicationDetails.getApplicationDate()));
            reportParams.put("cityLogo", cityService.getCityLogoURL());
            reportParams.put("estimationNumber", sewerageApplicationDetails.getEstimationNumber());
            reportParams.put(ASSESSMENT_NO, sewerageApplicationDetails.getConnectionDetail().getPropertyIdentifier());
            if (sewerageApplicationDetails.getCurrentDemand() != null)
                for (final EgDemandDetails egDmdDetails : sewerageApplicationDetails.getCurrentDemand().getEgDemandDetails())
                    if (egDmdDetails.getEgDemandReason().getEgDemandReasonMaster().getCode()
                            .equalsIgnoreCase(SewerageTaxConstants.FEES_DONATIONCHARGE_CODE))
                        donationCharges = egDmdDetails.getAmount().subtract(egDmdDetails.getAmtCollected());
            // TODO: CHECK THIS LOGIC AGAIN. IF FEE TYPE IS ESTIMATION FEES,
            // THEN WE NEED TO GROUP ALL FEESES.
            for (final SewerageConnectionFee scf : sewerageApplicationDetails.getConnectionFees()){
                if (scf.getFeesDetail().getCode().equalsIgnoreCase(SewerageTaxConstants.FEES_ESTIMATIONCHARGES_CODE))
                    estimationCharges = BigDecimal.valueOf(scf.getAmount());
                if (scf.getFeesDetail().getCode().equalsIgnoreCase(SewerageTaxConstants.FEES_SEWERAGETAX_CODE))
                    sewerageCharges = BigDecimal.valueOf(scf.getAmount());
            }
            final BigDecimal totalCharges =  estimationCharges.add(donationCharges).add(sewerageCharges);
            reportParams.put(ESTIMATION_CHARGES, estimationCharges);
            reportParams.put(DONATION_CHARGES, donationCharges);
            reportParams.put("sewerageCharges", sewerageCharges);
            reportParams.put(TOTAL_CHARGES, totalCharges);
            reportParams.put("amountInWords", getTotalAmountInWords(totalCharges.setScale(2,
                    BigDecimal.ROUND_HALF_EVEN)));
            reportParams.put(APPLICATION_DATE, getDefaultFormattedDate(sewerageApplicationDetails.getApplicationDate()));
            reportParams.put("applicantName", ownerName);
            reportParams.put(ADDRESS, assessmentDetails.getPropertyAddress());
            reportParams.put("inspectionDetails", sewerageApplicationDetails.getFieldInspections().get(0)
                    .getFieldInspectionDetails());
            reportParams.put("estimationDetails", sewerageApplicationDetails.getEstimationDetails());
            reportParams.put("houseNo", doorNo != null ? doorNo[0] : "");
            reportInput = new ReportRequest(ESTIMATION_NOTICE, sewerageApplicationDetails,
                    reportParams);
        }
        return reportService.createReport(reportInput);
    }

    public ReportOutput generateReportOutputDataForRejection(final SewerageApplicationDetails sewerageApplicationDetails,
            final HttpSession session,
            final HttpServletRequest request) {
        final List<Assignment> assignList = assignmentService
                .getAllActiveAssignments(
                        designationService.getDesignationByName(
                                SewerageTaxConstants.DESIGNATION_COMMISSIONER).getId());

        ReportRequest reportInput = null;
        final Map<String, Object> reportParams = new HashMap<>();

        if (sewerageApplicationDetails != null) {
            final AssessmentDetails assessmentDetails = sewerageTaxUtils.getAssessmentDetailsForFlag(
                    sewerageApplicationDetails.getConnectionDetail().getPropertyIdentifier(),
                    PropertyExternalService.FLAG_FULL_DETAILS);
            String ownerName = "";
            if (null != assessmentDetails.getOwnerNames())
                for (final OwnerName names : assessmentDetails.getOwnerNames()) {
                    ownerName = names.getOwnerName();
                    break;
                }
            reportParams.put(APPLICATION_TYPE,
                    WordUtils.capitalize(sewerageApplicationDetails.getApplicationType().getName()));
            reportParams.put("applicantName", ownerName);
            reportParams.put("cityName", session.getAttribute("citymunicipalityname"));
            reportParams.put("remarks", request.getParameter("approvalComent"));
            reportParams.put("rejectionDate", getDefaultFormattedDate(sewerageApplicationDetails.getRejectionDate()));
            reportParams.put("rejectionNumber", sewerageApplicationDetails.getRejectionNumber());
            reportParams.put(PRESENT_COMMISSIONER, assignList == null ? StringUtils.EMPTY: assignList
                    .get(0)
                    .getEmployee().getName());
            reportInput = new ReportRequest(REJECTION_NOTICE, sewerageApplicationDetails,
                    reportParams);

        }
        return reportService.createReport(reportInput);
    }

    public ReportOutput generateReportOutputForWorkOrder(final SewerageApplicationDetails sewerageApplicationDetails) {
        ReportRequest reportInput = null;
        final Map<String, Object> reportParams = new HashMap<>();
        if (null != sewerageApplicationDetails) {
            final AssessmentDetails assessmentDetails = sewerageTaxUtils.getAssessmentDetailsForFlag(
                    sewerageApplicationDetails.getConnectionDetail().getPropertyIdentifier(),
                    PropertyExternalService.FLAG_FULL_DETAILS);
            final String[] doorno = assessmentDetails.getPropertyAddress().split(",");
            String ownerName = "";
            for (final OwnerName names : assessmentDetails.getOwnerNames()) {
                ownerName = names.getOwnerName();
                break;
            }

            if (sewerageApplicationDetails
                    .getApplicationType() != null)
                reportParams.put("conntitle",
                        WordUtils.capitalize(sewerageApplicationDetails.getApplicationType().getName()));
            final User user = securityUtils.getCurrentUser();
            List<Assignment> loggedInUserAssignmentForRegistration;
            String loggedInUserDesignationForReg;
            loggedInUserAssignmentForRegistration = assignmentService.getAssignmentByPositionAndUserAsOnDate(
                    sewerageApplicationDetails.getCurrentState().getOwnerPosition().getId(), user.getId(), new Date());
            loggedInUserDesignationForReg = !loggedInUserAssignmentForRegistration.isEmpty()
                    ? loggedInUserAssignmentForRegistration.get(0).getDesignation().getName() : null;
            if (loggedInUserDesignationForReg != null
                    && SewerageTaxConstants.DESIGNATION_COMMISSIONER.equalsIgnoreCase(loggedInUserDesignationForReg))
                reportParams.put(IS_COMMISSIONER, true);
            else
                reportParams.put(IS_COMMISSIONER, false);
            reportParams.put("userSignature",
                    user.getSignature() == null ? new ByteArrayInputStream(new byte[0])
                            : new ByteArrayInputStream(user.getSignature()));
            reportParams.put("applicationtype", stmsMessageSource.getMessage("msg.new.sewerage.conn", null, null));
            reportParams.put("municipality", cityService.getMunicipalityName());
            reportParams.put(DISTRICT, cityService.getDistrictName());
            reportParams.put("purpose", null);
            reportParams.put(
                    PRESENT_COMMISSIONER,
                    assignmentService
                            .getAllActiveAssignments(
                                    designationService.getDesignationByName(
                                            SewerageTaxConstants.DESIGNATION_COMMISSIONER).getId())
                            .get(0)
                            .getEmployee().getName());

            if (sewerageApplicationDetails.getApplicationType().getCode()
                    .equalsIgnoreCase(SewerageTaxConstants.NEWSEWERAGECONNECTION)) {
                for (final SewerageConnectionFee scf : sewerageApplicationDetails.getConnectionFees())
                    if (scf.getFeesDetail().getCode().equalsIgnoreCase(SewerageTaxConstants.FEES_ESTIMATIONCHARGES_CODE))
                        estimationCharges = BigDecimal.valueOf(scf.getAmount());
                    else if (scf.getFeesDetail().getCode().equalsIgnoreCase(SewerageTaxConstants.FEES_DONATIONCHARGE_CODE))
                        donationCharges = BigDecimal.valueOf(scf.getAmount());
                    else if (scf.getFeesDetail().getCode().equalsIgnoreCase(SewerageTaxConstants.FEES_SEWERAGETAX_CODE))
                        sewerageCharges = BigDecimal.valueOf(scf.getAmount());
            } else if (sewerageApplicationDetails.getCurrentDemand() != null) {
                final Map<String, BigDecimal> donationSewerageFeesDtls = getFeesForChangeInClosets(
                        sewerageApplicationDetails.getCurrentDemand());
                estimationCharges = donationSewerageFeesDtls.get(ESTIMATION_CHARGES);
                donationCharges = donationSewerageFeesDtls.get(DONATION_CHARGES);
                sewerageCharges = donationSewerageFeesDtls.get(SEWERAGE_TAX);
            }
            reportParams.put("cityLogo", cityService.getCityLogoURL());
            reportParams.put(ESTIMATION_CHARGES, estimationCharges);
            reportParams.put(DONATION_CHARGES, donationCharges);
            reportParams.put(SEWERAGE_TAX, sewerageCharges);
            reportParams.put(TOTAL_CHARGES, donationCharges.add(sewerageCharges).add(estimationCharges));

            reportParams.put(ASSESSMENT_NO, sewerageApplicationDetails.getConnectionDetail().getPropertyIdentifier());
            reportParams.put(NO_OF_SEATS_RESIDENTIAL,sewerageApplicationDetails.getConnectionDetail()
                    .getNoOfClosetsResidential()== null?0:sewerageApplicationDetails.getConnectionDetail()
                            .getNoOfClosetsResidential());
            reportParams.put(NO_OF_SEATS_NON_RESIDENTIAL,  sewerageApplicationDetails.getConnectionDetail()
                    .getNoOfClosetsNonResidential()== null? 0:sewerageApplicationDetails.getConnectionDetail()
                            .getNoOfClosetsNonResidential());
            reportParams.put("revenueWardNo", assessmentDetails.getBoundaryDetails().getWardName());
            reportParams.put("locality", assessmentDetails.getBoundaryDetails().getLocalityName());

            reportParams.put("workorderdate", sewerageApplicationDetails.getWorkOrderDate() != null
                    ? getDefaultFormattedDate(sewerageApplicationDetails.getWorkOrderDate()) : "");
            reportParams.put("workorderno", sewerageApplicationDetails.getWorkOrderNumber());
            if (sewerageApplicationDetails.getConnection().getShscNumber() != null)
                reportParams.put("consumerNumber", sewerageApplicationDetails.getConnection().getShscNumber());
            reportParams.put("applicantname", WordUtils.capitalize(ownerName));
            reportParams.put(ADDRESS, assessmentDetails.getPropertyAddress());
            reportParams.put("doorno", doorno[0]);
            reportParams.put(APPLICATION_DATE, getDefaultFormattedDate(sewerageApplicationDetails.getApplicationDate()));
            reportInput = new ReportRequest(SANCTION_NOTICE, sewerageApplicationDetails, reportParams);
        }
        return reportService.createReport(reportInput);
    }

    public Map<String, BigDecimal> getFeesForChangeInClosets(final EgDemand demand) {
        BigDecimal currentEstimationCharges = BigDecimal.ZERO;

        BigDecimal totalDontationCharge = BigDecimal.ZERO;
        BigDecimal totalSewerageTax = BigDecimal.ZERO;

        final Map<String, BigDecimal> donationSewerageFees = new HashMap<>();

        for (final EgDemandDetails dmdDtl : demand.getEgDemandDetails())
            for (final EgdmCollectedReceipt collectedReceipt : dmdDtl.getEgdmCollectedReceipts())
                if (SewerageTaxConstants.FEES_DONATIONCHARGE_CODE
                        .equalsIgnoreCase(dmdDtl.getEgDemandReason().getEgDemandReasonMaster().getCode()))
                    totalDontationCharge = totalDontationCharge.add(collectedReceipt.getReasonAmount());
                else if (SewerageTaxConstants.FEES_SEWERAGETAX_CODE
                        .equalsIgnoreCase(dmdDtl.getEgDemandReason().getEgDemandReasonMaster().getCode()))
                    totalSewerageTax = totalSewerageTax.add(collectedReceipt.getReasonAmount());
                else if (SewerageTaxConstants.FEES_ESTIMATIONCHARGES_CODE
                        .equalsIgnoreCase(dmdDtl.getEgDemandReason().getEgDemandReasonMaster().getCode()))
                    currentEstimationCharges = currentEstimationCharges.add(collectedReceipt.getReasonAmount());
        donationSewerageFees.put(DONATION_CHARGES, totalDontationCharge);
        donationSewerageFees.put(SEWERAGE_TAX, totalSewerageTax);
        donationSewerageFees.put(ESTIMATION_CHARGES, currentEstimationCharges);
        return donationSewerageFees;
    }

    /**
     * @param inputStream
     * @param noticeNo
     * @param out
     * @return zip output stream file
     */
    public ZipOutputStream addFilesToZip(final InputStream inputStream, final String noticeNo, final ZipOutputStream out) {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Entered into addFilesToZip method");
        final byte[] buffer = new byte[1024];
        try {
            out.setLevel(Deflater.DEFAULT_COMPRESSION);
            out.putNextEntry(new ZipEntry(noticeNo.replaceAll("/", "_")));
            int len;
            while ((len = inputStream.read(buffer)) > 0)
                out.write(buffer, 0, len);
            inputStream.close();

        } catch (final IllegalArgumentException iae) {
            LOGGER.error(EXCEPTION_IN_ADD_FILES_TO_ZIP, iae);
            throw new ValidationException(Arrays.asList(new ValidationError("error", iae.getMessage())));
        } catch (final FileNotFoundException fnfe) {
            LOGGER.error(EXCEPTION_IN_ADD_FILES_TO_ZIP, fnfe);
            throw new ValidationException(Arrays.asList(new ValidationError("error", fnfe.getMessage())));
        } catch (final IOException ioe) {
            LOGGER.error(EXCEPTION_IN_ADD_FILES_TO_ZIP, ioe);
            throw new ValidationException(Arrays.asList(new ValidationError("error", ioe.getMessage())));
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Exit from addFilesToZip method");
        return out;
    }

    public SewerageNotice generateReportForCloseConnection(final SewerageApplicationDetails sewerageApplicationDetails,
            final HttpSession session) {
        ReportOutput reportOutput = null;

        SewerageNotice sewerageNotice = null;
        reportOutput = generateReportOutputForSewerageCloseConnection(sewerageApplicationDetails, session);
        if (reportOutput != null && reportOutput.getReportOutputData() != null) {
            generateNoticePDF = new ByteArrayInputStream(reportOutput.getReportOutputData());
            sewerageNotice = saveCloseConnectionNotice(sewerageApplicationDetails, generateNoticePDF);
        }
        return sewerageNotice;
    }

    public ReportOutput generateReportOutputForSewerageCloseConnection(
            final SewerageApplicationDetails sewerageApplicationDetails,
            final HttpSession session) {
        ReportRequest reportInput = null;
        final Map<String, Object> reportParams = new HashMap<>();
        if (null != sewerageApplicationDetails) {
            final AssessmentDetails assessmentDetails = sewerageTaxUtils.getAssessmentDetailsForFlag(
                    sewerageApplicationDetails.getConnectionDetail().getPropertyIdentifier(),
                    PropertyExternalService.FLAG_FULL_DETAILS);
            final String[] doorno = assessmentDetails.getPropertyAddress().split(",");
            String ownerName = "";
            for (final OwnerName names : assessmentDetails.getOwnerNames()) {
                ownerName = names.getOwnerName();
                break;
            }
            reportParams.put("conntitle",
                    WordUtils.capitalize(sewerageApplicationDetails.getApplicationType().getName()));
            reportParams.put("municipality", session.getAttribute("citymunicipalityname"));
            reportParams.put(DISTRICT, session.getAttribute("districtName"));

            reportParams.put(
                    PRESENT_COMMISSIONER,
                    assignmentService
                    .getAllActiveAssignments(
                            designationService.getDesignationByName(
                                    SewerageTaxConstants.DESIGNATION_COMMISSIONER).getId())
                            .get(0)
                            .getEmployee().getName());

            reportParams.put(ASSESSMENT_NO, sewerageApplicationDetails.getConnectionDetail().getPropertyIdentifier());
            reportParams.put(NO_OF_SEATS_RESIDENTIAL, sewerageApplicationDetails.getConnectionDetail()
                    .getNoOfClosetsResidential());
            reportParams.put(NO_OF_SEATS_NON_RESIDENTIAL, sewerageApplicationDetails.getConnectionDetail()
                    .getNoOfClosetsNonResidential());
            reportParams.put("revenueWardNo", assessmentDetails.getBoundaryDetails().getWardName());
            reportParams.put("locality", assessmentDetails.getBoundaryDetails().getLocalityName());

            reportParams.put("eeApprovalDate", getDefaultFormattedDate(sewerageApplicationDetails.getLastModifiedDate()));
            reportParams.put("consumerNumber", sewerageApplicationDetails.getConnection().getShscNumber());
            reportParams.put("applicantname", WordUtils.capitalize(ownerName));
            reportParams.put(ADDRESS, assessmentDetails.getPropertyAddress());
            reportParams.put("doorno", doorno[0]);
            reportParams.put(APPLICATION_DATE, getDefaultFormattedDate(sewerageApplicationDetails.getApplicationDate()));
            reportInput = new ReportRequest(CLOSECONNECTIONNOTICE, sewerageApplicationDetails, reportParams);
        }
        return reportService.createReport(reportInput);
    }

    public SewerageNotice saveCloseConnectionNotice(final SewerageApplicationDetails sewerageApplicationDetails,
            final InputStream fileStream) {
        SewerageNotice sewerageNotice = null;

        if (sewerageApplicationDetails != null) {
            sewerageNotice = new SewerageNotice();
            buildSewerageNotice(sewerageApplicationDetails, sewerageNotice, sewerageApplicationDetails.getClosureNoticeNumber(),
                    new Date(), SewerageTaxConstants.NOTICE_TYPE_CLOSER_NOTICE);
            final String fileName = sewerageApplicationDetails.getClosureNoticeNumber() + ".pdf";
            final FileStoreMapper fileStore = fileStoreService.store(fileStream, fileName, APPLICATION_PDF,
                    SewerageTaxConstants.FILESTORE_MODULECODE);
            sewerageNotice.setFileStore(fileStore);
        }
        return sewerageNotice;
    }

    @Transactional
    public SewerageNotice buildDemandBillNotice(final SewerageApplicationDetails sewerageApplicationDetails,
            final InputStream fileStream,
            final String demandBillNumber) {
        SewerageNotice sewerageNotice = null;
        if (sewerageApplicationDetails != null) {
            sewerageNotice = new SewerageNotice();
            buildSewerageNotice(sewerageApplicationDetails, sewerageNotice, demandBillNumber, new Date(),
                    NOTICE_TYPE_DEMAND_BILL_NOTICE);
        }
        final String fileName = demandBillNumber + ".pdf";
        final FileStoreMapper fileStoreMapper = fileStoreService.store(fileStream, fileName, APPLICATION_PDF,
                FILESTORE_MODULECODE);
        sewerageNotice.setFileStore(fileStoreMapper);
        return sewerageNotice;
    }

    public String getTotalAmountInWords(final BigDecimal totalCharges) {
        return NumberToWordConverter.amountInWordsWithCircumfix(totalCharges);
    }

}
