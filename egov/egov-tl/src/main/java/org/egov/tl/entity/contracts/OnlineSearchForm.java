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
package org.egov.tl.entity.contracts;

import org.egov.tl.entity.TradeLicense;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.egov.tl.utils.Constants.CLOSURE_APPTYPE_CODE;
import static org.egov.tl.utils.Constants.STATUS_UNDERWORKFLOW;

public class OnlineSearchForm {

    private Long licenseId;
    private String applicationNumber;
    private String licenseNumber;
    private String tradeOwnerName;
    private String propertyAssessmentNo;
    private String mobileNo;
    private List<String> actions = new ArrayList<>();
    private BigDecimal arrDmd;
    private BigDecimal currDmd;
    private BigDecimal totColl;
    private String status;
    private String uid;

    public OnlineSearchForm() {
        // For form binding
    }

    public OnlineSearchForm(final TradeLicense license, final BigDecimal... dmdColl) {
        setLicenseId(license.getId());
        setUid(license.getUid());
        setApplicationNumber(license.getApplicationNumber());
        setLicenseNumber(license.getLicenseNumber());
        setTradeOwnerName(license.getLicensee().getApplicantName());
        setMobileNo(license.getLicensee().getMobilePhoneNumber());
        setStatus(license.getStatus().getName());
        setPropertyAssessmentNo(license.getAssessmentNo() == null ? EMPTY : license.getAssessmentNo());
        setArrDmd(dmdColl[0]);
        setCurrDmd(dmdColl[1]);
        setTotColl(dmdColl[2]);
        if (license.canCollectLicenseFee() || license.canCollectFee())
            actions.add("Payment");
        if (license.getIsActive())
            actions.add("View DCB");
        if (license.isReadyForRenewal())
            actions.add("Renew License");
        if (license.isClosureApplicable())
            actions.add("Closure");
        if (license.isStatusActive() && !license.isLegacy())
            actions.add("Print Certificate");
        if (!CLOSURE_APPTYPE_CODE.equals(license.getLicenseAppType().getCode())
                && STATUS_UNDERWORKFLOW.equals(license.getStatus().getStatusCode()))
            actions.add("Print Provisional Certificate");
        if (license.isClosed())
            actions.add("Closure Endorsement Notice");
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(final String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(final String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }


    public String getTradeOwnerName() {
        return tradeOwnerName;
    }

    public void setTradeOwnerName(final String tradeOwnerName) {
        this.tradeOwnerName = tradeOwnerName;
    }

    public String getPropertyAssessmentNo() {
        return propertyAssessmentNo;
    }

    public void setPropertyAssessmentNo(final String propertyAssessmentNo) {
        this.propertyAssessmentNo = propertyAssessmentNo;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(final String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public Long getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(final Long licenseId) {
        this.licenseId = licenseId;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(final List<String> actions) {
        this.actions = actions;
    }

    public BigDecimal getArrDmd() {
        return arrDmd;
    }

    public void setArrDmd(BigDecimal arrDmd) {
        this.arrDmd = arrDmd;
    }

    public BigDecimal getCurrDmd() {
        return currDmd;
    }

    public void setCurrDmd(BigDecimal currDmd) {
        this.currDmd = currDmd;
    }

    public BigDecimal getTotColl() {
        return totColl;
    }

    public void setTotColl(BigDecimal totColl) {
        this.totColl = totColl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
