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

package org.egov.tl.service;

import org.egov.commons.CFinancialYear;
import org.egov.commons.service.CFinancialYearService;
import org.egov.infra.config.persistence.datasource.routing.annotation.ReadOnly;
import org.egov.infra.persistence.utils.Page;
import org.egov.infra.reporting.engine.ReportOutput;
import org.egov.infra.reporting.engine.ReportRequest;
import org.egov.infra.reporting.engine.ReportService;
import org.egov.tl.entity.contracts.InstallmentWiseDCBRequest;
import org.egov.tl.entity.view.InstallmentWiseDCB;
import org.egov.tl.repository.InstallmentwiseDCBReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Transactional(readOnly = true)
public class InstallmentwiseDCBReportService {

    @Autowired
    private CFinancialYearService cFinancialYearService;

    @Autowired
    private InstallmentwiseDCBReportRepository installmentwiseDCBReportRepository;

    @Autowired
    private ReportService reportService;

    public List<CFinancialYear> getFinancialYears() {
        final List<CFinancialYear> financialYearList = cFinancialYearService.findAll();
        Collections.reverse(financialYearList);
        return financialYearList;
    }

    @ReadOnly
    public Date financialYearStartDate(final InstallmentWiseDCBRequest installmentWiseDCBRequest) {
        return cFinancialYearService
                .getFinacialYearByYearRange(installmentWiseDCBRequest.getInstallment()).getStartingDate();
    }

    @ReadOnly
    public Page<InstallmentWiseDCB> getReportResult(final InstallmentWiseDCBRequest installmentWiseDCBRequest) {
        return installmentwiseDCBReportRepository.findByInstallmentWiseDCB(installmentWiseDCBRequest,
                financialYearStartDate(installmentWiseDCBRequest));
    }

    @ReadOnly
    public List<InstallmentWiseDCB> getInstallmentWiseDCBReport(Long licenseId) {
        return installmentwiseDCBReportRepository.findAllByLicenseId(licenseId);
    }

    @ReadOnly
    public ReportOutput generateInstallmentwiseDCBReport(InstallmentWiseDCBRequest reportCriteria) {
        Map<String, Object> reportParams = new HashMap<>();
        reportParams.put("year", reportCriteria.getInstallment());
        ReportRequest reportRequest = new ReportRequest("tl_installment_yearwise_dcb_report",
                installmentwiseDCBReportRepository.findInstallmentWiseReport(reportCriteria,
                        financialYearStartDate(reportCriteria)), reportParams);
        reportRequest.setReportFormat(reportCriteria.getPrintFormat());
        return reportService.createReport(reportRequest);
    }

    @ReadOnly
    public Object[] reportGrandTotal(final InstallmentWiseDCBRequest installmentWiseDCBRequest) {
        return installmentwiseDCBReportRepository.findReportTotal(installmentWiseDCBRequest,
                financialYearStartDate(installmentWiseDCBRequest));
    }
}