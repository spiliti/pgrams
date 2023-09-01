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

import org.egov.commons.Installment;
import org.egov.infra.utils.DateUtils;
import org.egov.tl.entity.LicenseAppType;
import org.egov.tl.entity.PenaltyRates;
import org.egov.tl.entity.TradeLicense;
import org.egov.tl.repository.PenaltyRatesRepository;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.math.BigDecimal.ZERO;

@Service
@Transactional(readOnly = true)
public class PenaltyRatesService {

    @Autowired
    private PenaltyRatesRepository penaltyRatesRepository;

    public PenaltyRates findByDaysAndLicenseAppType(Integer days, LicenseAppType licenseAppType) {
        return penaltyRatesRepository.findByDaysAndLicenseAppType(Long.valueOf(days), licenseAppType);
    }

    public PenaltyRates findOne(Long id) {
        return penaltyRatesRepository.findOne(id);
    }

    public Long getMinFromRange(LicenseAppType licenseAppType) {
        return penaltyRatesRepository.findTopByLicenseAppTypeOrderByFromRangeAsc(licenseAppType).getFromRange();
    }

    public Long getMaxToRange(LicenseAppType licenseAppType) {
        return penaltyRatesRepository.findTopByLicenseAppTypeOrderByToRangeDesc(licenseAppType).getToRange();
    }

    @Transactional
    public List<PenaltyRates> create(List<PenaltyRates> penaltyRates) {
        return penaltyRatesRepository.save(penaltyRates);
    }

    public List<PenaltyRates> getPenaltyRatesByLicenseAppType(LicenseAppType licenseAppType) {
        return licenseAppType == null ? penaltyRatesRepository.findAll() :
                penaltyRatesRepository.findByLicenseAppTypeOrderByIdAsc(licenseAppType);
    }

    @Transactional
    public void delete(PenaltyRates penaltyRates) {
        penaltyRatesRepository.delete(penaltyRates);
    }

    public BigDecimal calculatePenalty(TradeLicense license, Date fromDate, Date toDate, BigDecimal amount) {
        if (fromDate != null) {
            int paymentDueDays = DateUtils.daysBetween(fromDate, toDate);
            PenaltyRates penaltyRates = findByDaysAndLicenseAppType(paymentDueDays, license.getLicenseAppType());
            if (penaltyRates == null) {
                return BigDecimal.ZERO;
            }

            return amount.multiply(BigDecimal.valueOf(penaltyRates.getRate() / 100));
        }
        return BigDecimal.ZERO;
    }

    public Date getPenaltyDate(LicenseAppType licenseAppType, Installment installment) {
        Optional<PenaltyRates> penaltyRates = getPenaltyRatesByLicenseAppType(licenseAppType)
                .stream()
                .filter(penaltyRate -> penaltyRate.getRate().doubleValue() <= ZERO.doubleValue())
                .findFirst();

        return LocalDate.fromDateFields(installment.getFromDate())
                .plusDays(penaltyRates.isPresent() ? penaltyRates.get().getToRange().intValue() : ZERO.intValue())
                .toDate();
    }
}
