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
package org.egov.tl.service;

import org.egov.infra.validation.exception.ValidationException;
import org.egov.tl.entity.DemandGenerationLog;
import org.egov.tl.entity.DemandGenerationLogDetail;
import org.egov.tl.entity.TradeLicense;
import org.egov.tl.entity.enums.ProcessStatus;
import org.egov.tl.repository.DemandGenerationLogDetailRepository;
import org.egov.tl.repository.DemandGenerationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.egov.tl.entity.enums.ProcessStatus.COMPLETED;
import static org.egov.tl.entity.enums.ProcessStatus.INCOMPLETE;
import static org.egov.tl.entity.enums.ProcessStatus.INPROGRESS;

@Service
@Transactional(readOnly = true)
public class DemandGenerationLogService {

    private static final String INSTALLMENT_YEAR = "%d-%s";

    @Autowired
    private DemandGenerationLogRepository demandGenerationLogRepository;

    @Autowired
    private DemandGenerationLogDetailRepository demandGenerationLogDetailRepository;

    public Optional<DemandGenerationLogDetail> getIncompleteLogDetailsByLicenseId(Long licenseId) {
        return getLogDetailsByLicenseIdAndStatus(licenseId, INCOMPLETE).stream().findFirst();
    }

    public List<DemandGenerationLogDetail> getLogDetailsByLicenseIdAndStatus(Long licenseId, ProcessStatus status) {
        return demandGenerationLogDetailRepository.findByLicenseIdAndStatus(licenseId, status);
    }

    public DemandGenerationLog getDemandGenerationLogByInstallmentYear(String installmentYearRange) {
        return demandGenerationLogRepository.findByInstallmentYear(installmentYearRange);
    }

    public DemandGenerationLog getPreviousInstallmentDemandGenerationLog(String installmentYearRange) {
        //Assuming installment year range will always be in the format of YYYY-YY
        int previousInstallmentStartYear = Integer.valueOf(installmentYearRange.split("-")[0]) - 1;
        String previousInstallmentYearRange = String.format(INSTALLMENT_YEAR, previousInstallmentStartYear,
                String.valueOf(previousInstallmentStartYear + 1).substring(2, 4));
        return getDemandGenerationLogByInstallmentYear(previousInstallmentYearRange);
    }

    @Transactional
    public DemandGenerationLog createDemandGenerationLog(String installmentYearRange) {
        return demandGenerationLogRepository.save(new DemandGenerationLog(installmentYearRange));
    }

    @Transactional
    public DemandGenerationLog completeDemandGenerationLog(DemandGenerationLog demandGenerationLog) {
        demandGenerationLog.setDemandGenerationStatus(COMPLETED);
        for (DemandGenerationLogDetail detail : demandGenerationLog.getDetails()) {
            if (detail.getStatus().equals(INCOMPLETE)) {
                demandGenerationLog.setDemandGenerationStatus(INCOMPLETE);
                break;
            }
        }
        demandGenerationLog.setExecutionStatus(COMPLETED);
        return demandGenerationLogRepository.save(demandGenerationLog);
    }

    @Transactional
    public DemandGenerationLogDetail completeDemandGenerationLogDetail(DemandGenerationLogDetail demandGenerationLogDetail) {
        return demandGenerationLogDetailRepository.save(demandGenerationLogDetail);
    }

    @Transactional
    public DemandGenerationLogDetail createOrGetDemandGenerationLogDetail(DemandGenerationLog demandGenerationLog, TradeLicense license) {

        DemandGenerationLogDetail logDetail = demandGenerationLogDetailRepository.
                findByDemandGenerationLogIdAndLicenseId(demandGenerationLog.getId(), license.getId());
        if (logDetail == null) {
            logDetail = new DemandGenerationLogDetail();
            logDetail.setLicenseId(license.getId());
            logDetail.setLicenseNumber(license.getLicenseNumber());
            logDetail.setDemandGenerationLog(demandGenerationLog);
            logDetail.setStatus(INPROGRESS);
            demandGenerationLog.getDetails().add(logDetail);
        }
        return logDetail;
    }

    @Transactional
    public void updateDemandGenerationLogDetailOnException(DemandGenerationLog demandGenerationLog,
                                                           DemandGenerationLogDetail logDetail, RuntimeException exception) {
        logDetail.setStatus(INCOMPLETE);
        logDetail.setDetail(exception instanceof ValidationException ?
                ((ValidationException) exception).getErrors().get(0).getMessage() : "Error : " + exception);
        demandGenerationLog.setDemandGenerationStatus(INCOMPLETE);
        completeDemandGenerationLogDetail(logDetail);
    }
}