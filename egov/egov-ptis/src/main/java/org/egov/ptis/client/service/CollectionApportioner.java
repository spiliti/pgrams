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
package org.egov.ptis.client.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.egov.collection.entity.ReceiptDetail;
import org.egov.collection.integration.models.BillAccountDetails.PURPOSE;
import org.egov.commons.dao.ChartOfAccountsHibernateDAO;
import org.egov.commons.dao.FunctionHibernateDAO;
import org.egov.demand.model.EgBillDetails;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.ptis.client.util.PropertyTaxUtil;
import org.springframework.beans.factory.annotation.Autowired;

public class CollectionApportioner {

    private static final String REBATE_STR = "REBATE";
    public static final String STRING_FULLTAX = "FULLTAX";
    public static final String STRING_ADVANCE = "ADVANCE";
    private static final Logger LOGGER = Logger.getLogger(CollectionApportioner.class);
    private boolean isEligibleForCurrentRebate;
    @Autowired
    private PropertyTaxUtil propertyTaxUtil;

    public CollectionApportioner(boolean isEligibleForCurrentRebate, boolean isEligibleForAdvanceRebate,
            BigDecimal rebate) {
        this.isEligibleForCurrentRebate = isEligibleForCurrentRebate;
    }

    public void apportion(BigDecimal amtPaid, List<ReceiptDetail> receiptDetails, Map<String, BigDecimal> instDmdMap,
            String consumerId) {
        LOGGER.info("receiptDetails before apportioning amount " + amtPaid + ": " + receiptDetails);
        Boolean isFullPayment = Boolean.FALSE;
        boolean canWaiveOff = false;

        BigDecimal totalCrAmountToBePaid = BigDecimal.ZERO;
        BigDecimal totalPenalty = BigDecimal.ZERO;
        for (final ReceiptDetail receiptDetail : receiptDetails) {
            if (!PURPOSE.ADVANCE_AMOUNT.toString().equals(receiptDetail.getPurpose()))
                totalCrAmountToBePaid = totalCrAmountToBePaid.add(receiptDetail.getCramountToBePaid());
            if (isPenaltyReceipt(receiptDetail))
                totalPenalty = totalPenalty.add(receiptDetail.getCramountToBePaid());
        }

        if (amtPaid.compareTo(totalCrAmountToBePaid) >= 0 && isEligibleForCurrentRebate)
            isFullPayment = Boolean.TRUE;

        canWaiveOff = propertyTaxUtil
                .isEligibleforWaiver(amtPaid.compareTo(totalCrAmountToBePaid.subtract(totalPenalty)) >= 0, consumerId);

        Amount balance = new Amount(amtPaid);
        BigDecimal crAmountToBePaid = null;
        for (ReceiptDetail rd : receiptDetails) {
            // For partial payment, we revert IsActualDemand to true
            if (isPenaltyReceipt(rd))
                // FROM REST API isActualDemand NULL, so normalize before its too late
                // ( only for penalty receipts now, others are null, handled their by taking value from EgBillDetails )
                if (canWaiveOff)
                rd.setIsActualDemand(Boolean.FALSE);
                else
                rd.setIsActualDemand(Boolean.TRUE);

            if (balance.isZero()) {
                // nothing left to apportion
                rd.zeroDrAndCrAmounts();
                continue;
            }

            crAmountToBePaid = rd.getCramountToBePaid();

            if (rd.getDescription().contains(REBATE_STR)) {
                if (isFullPayment)
                    balance = balance.minus(crAmountToBePaid);
                else
                    rd.setDramount(BigDecimal.ZERO);
            } else if (canWaiveOff && isPenaltyReceipt(rd)) {
                // Skip Penalty Heads
                rd.zeroDrAndCrAmounts();
                continue;
            } else if (balance.isLessThanOrEqualTo(crAmountToBePaid)) {
                // partial or exact payment
                rd.setCramount(balance.amount);
                balance = Amount.ZERO;
            } else { // excess payment
                rd.setCramount(crAmountToBePaid);
                balance = balance.minus(crAmountToBePaid);
            }
            LOGGER.info(String.format("apportion; bottom of loop"));
        }

        if (balance.isGreaterThanZero()) {
            LOGGER.error("Apportioning failed: excess payment!");
            throw new ValidationException(Arrays.asList(new ValidationError(
                    "Paid Amount is greater than Total Amount to be paid",
                    "Paid Amount is greater than Total Amount to be paid")));
        }

        LOGGER.info("receiptDetails after apportioning: " + receiptDetails);
    }

    public List<ReceiptDetail> reConstruct(final BigDecimal amountPaid, final List<EgBillDetails> billDetails,
            FunctionHibernateDAO functionDAO, ChartOfAccountsHibernateDAO chartOfAccountsDAO) {
        final List<ReceiptDetail> receiptDetails = new ArrayList<>(0);
        LOGGER.info("receiptDetails before reApportion amount " + amountPaid + ": " + receiptDetails);
        LOGGER.info("billDetails before reApportion " + billDetails);
        Amount balance = new Amount(amountPaid);
        Boolean isFullPayment = Boolean.FALSE;
        BigDecimal crAmountToBePaid = BigDecimal.ZERO;

        if (isEligibleForCurrentRebate) {
            BigDecimal totalCrAmountToBePaid = BigDecimal.ZERO;
            for (final EgBillDetails billDetail : billDetails)
                if (PURPOSE.REBATE.toString().equals(billDetail.getPurpose()))
                    totalCrAmountToBePaid = totalCrAmountToBePaid.subtract(billDetail.getDrAmount());
                else if (!PURPOSE.ADVANCE_AMOUNT.toString().equals(billDetail.getPurpose()))
                    totalCrAmountToBePaid = totalCrAmountToBePaid.add(billDetail.getCrAmount());
            if (amountPaid.compareTo(totalCrAmountToBePaid) >= 0)
                isFullPayment = Boolean.TRUE;
        }

        Collections.sort(billDetails, (b1, b2) -> b1.getOrderNo().compareTo(b2.getOrderNo()));

        for (final EgBillDetails billDetail : billDetails) {
            crAmountToBePaid = billDetail.getCrAmount().subtract(billDetail.getDrAmount());
            final String glCode = billDetail.getGlcode();
            final ReceiptDetail receiptDetail = new ReceiptDetail();
            receiptDetail.setPurpose(billDetail.getPurpose());
            receiptDetail.setOrdernumber(Long.valueOf(billDetail.getOrderNo()));
            receiptDetail.setDescription(billDetail.getDescription());
            receiptDetail.setIsActualDemand(true);
            if (billDetail.getFunctionCode() != null)
                receiptDetail.setFunction(functionDAO.getFunctionByCode(billDetail.getFunctionCode()));
            receiptDetail.setAccounthead(chartOfAccountsDAO.getCChartOfAccountsByGlCode(glCode));
            receiptDetail.setCramountToBePaid(crAmountToBePaid);
            if (billDetail.getDescription().contains(REBATE_STR))
                receiptDetail.setDramount(billDetail.getDrAmount());
            else
                receiptDetail.setDramount(BigDecimal.ZERO);

            if (balance.isZero()) {
                // nothing left to apportion
                receiptDetail.zeroDrAndCrAmounts();
                receiptDetails.add(receiptDetail);
                continue;
            }

            if (receiptDetail.getDescription().contains(REBATE_STR)) {
                if (isFullPayment)
                    balance = balance.minus(crAmountToBePaid);
                else
                    receiptDetail.setDramount(BigDecimal.ZERO);
                receiptDetail.setCramount(BigDecimal.ZERO);
            } else if (balance.isLessThanOrEqualTo(crAmountToBePaid)) {
                // partial or exact payment
                receiptDetail.setCramount(balance.amount);
                receiptDetail.setCramountToBePaid(crAmountToBePaid);
                balance = Amount.ZERO;
            } else { // excess payment
                receiptDetail.setCramount(crAmountToBePaid);
                receiptDetail.setCramountToBePaid(crAmountToBePaid);
                balance = balance.minus(crAmountToBePaid);
            }
            receiptDetails.add(receiptDetail);
        }

        if (balance.isGreaterThanZero()) {
            LOGGER.error("reApportion failed: excess payment!");
            throw new ValidationException(Arrays.asList(
                    new ValidationError("Paid Amount is greater than Total Amount to be paid",
                            "Paid Amount is greater than Total Amount to be paid")));
        }

        LOGGER.info("receiptDetails after reApportion: " + receiptDetails);
        return receiptDetails;
    }

    private boolean isPenaltyReceipt(ReceiptDetail receiptDetail) {
        return PURPOSE.ARREAR_LATEPAYMENT_CHARGES.toString().equals(receiptDetail.getPurpose())
                || PURPOSE.CURRENT_LATEPAYMENT_CHARGES.toString().equals(receiptDetail.getPurpose());
    }

    private static class Amount {
        private BigDecimal amount;
        private static Amount ZERO = new Amount(BigDecimal.ZERO);

        private Amount(BigDecimal amount) {
            this.amount = amount;
        }

        private boolean isZero() {
            return amount.compareTo(BigDecimal.ZERO) == 0;
        }

        private boolean isGreaterThan(BigDecimal bd) {
            return amount.compareTo(bd) > 0;
        }

        private boolean isGreaterThanZero() {
            return isGreaterThan(BigDecimal.ZERO);
        }

        private boolean isGreaterThanOrEqualTo(BigDecimal bd) {
            return amount.compareTo(bd) >= 0;
        }

        private boolean isLessThanOrEqualTo(BigDecimal bd) {
            return amount.compareTo(bd) <= 0;
        }

        private Amount minus(BigDecimal bd) {
            return new Amount(amount.subtract(bd));
        }

    }

    void setEligibleForCurrentRebate(boolean isEligibleForCurrentRebate) {
        this.isEligibleForCurrentRebate = isEligibleForCurrentRebate;
    }

    void setEligibleForAdvanceRebate(boolean isEligibleForAdvanceRebate) {
    }

    public void setPropertyTaxUtil(PropertyTaxUtil propertyTaxUtil) {
        this.propertyTaxUtil = propertyTaxUtil;
    }

}