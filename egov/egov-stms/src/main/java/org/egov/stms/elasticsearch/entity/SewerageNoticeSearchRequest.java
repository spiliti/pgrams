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
package org.egov.stms.elasticsearch.entity;

import org.egov.infra.reporting.engine.ReportFormat;
import org.egov.infra.web.support.search.DataTableSearchRequest;

public class SewerageNoticeSearchRequest extends DataTableSearchRequest {
    private String searchText;
    private String shscNumber;
    private String moduleName;
    private String applicationType;
    private String applicantName;
    private String mobileNumber;
    private String revenueWard;
    private String doorNumber;
    private String ulbName;
    private String applicationDate;
    private String noticeGeneratedFrom;
    private String noticeGeneratedTo;
    private String noticeType;
    private ReportFormat printFormat;

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(final String moduleName) {
        this.moduleName = moduleName;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(final String applicationType) {
        this.applicationType = applicationType;
    }

    public String getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(String applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getUlbName() {
        return ulbName;
    }

    public String getShscNumber() {
        return shscNumber;
    }

    public void setShscNumber(String shscNumber) {
        this.shscNumber = shscNumber;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(final String applicantName) {
        this.applicantName = applicantName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(final String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setSearchText(final String searchText) {
        this.searchText = searchText;
    }

    public String searchQuery() {
        return searchText;
    }

    public String getRevenueWard() {
        return revenueWard;
    }

    public void setRevenueWard(String revenueWard) {
        this.revenueWard = revenueWard;
    }

    public String getDoorNumber() {
        return doorNumber;
    }

    public void setDoorNumber(String doorNumber) {
        this.doorNumber = doorNumber;
    }

    public void setUlbName(String ulbName) {
        this.ulbName = ulbName;
    }

    public String getNoticeGeneratedFrom() {
        return noticeGeneratedFrom;
    }

    public void setNoticeGeneratedFrom(String noticeGeneratedFrom) {
        this.noticeGeneratedFrom = noticeGeneratedFrom;
    }

    public String getNoticeGeneratedTo() {
        return noticeGeneratedTo;
    }

    public void setNoticeGeneratedTo(String noticeGeneratedTo) {
        this.noticeGeneratedTo = noticeGeneratedTo;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public ReportFormat getPrintFormat() {
        return printFormat;
    }

    public void setPrintFormat(ReportFormat printFormat) {
        this.printFormat = printFormat;
    }

}
