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

package org.egov.ptis.bean.dashboard;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

public class CollTableData {
     
    private String boundaryName = StringUtils.EMPTY;
    private String regionName = StringUtils.EMPTY;
    private String districtName = StringUtils.EMPTY;
    private String ulbGrade = StringUtils.EMPTY;
    private String ulbName = StringUtils.EMPTY;
    private String wardName = StringUtils.EMPTY;
    private String billCollector = StringUtils.EMPTY;
    private String billCollMobNo = StringUtils.EMPTY;
    private String revenueInspector = StringUtils.EMPTY;
    private String revInspectorMobNo = StringUtils.EMPTY;
    private String revenueOfficer = StringUtils.EMPTY;
    private String revOfficerMobNo = StringUtils.EMPTY;
    private BigDecimal todayColl = BigDecimal.ZERO;
    private BigDecimal totalDmd = BigDecimal.ZERO;
    private BigDecimal cytdDmd = BigDecimal.ZERO;
    private BigDecimal cytdColl = BigDecimal.ZERO;
    private BigDecimal performance = BigDecimal.ZERO;
    private BigDecimal cytdBalDmd = BigDecimal.ZERO;
    private BigDecimal lytdColl = BigDecimal.ZERO;
    private BigDecimal arrearDemand = BigDecimal.ZERO;
    private BigDecimal currentDemand = BigDecimal.ZERO;
    private BigDecimal proportionalArrearDemand = BigDecimal.ZERO;
    private BigDecimal proportionalCurrentDemand = BigDecimal.ZERO;
    private BigDecimal totalAssessments = BigDecimal.ZERO;
    private BigDecimal lyTodayColl = BigDecimal.ZERO;
    private BigDecimal dayTargetDemand = BigDecimal.ZERO;
    private BigDecimal arrearInterestDemand = BigDecimal.ZERO;
    private BigDecimal currentInterestDemand = BigDecimal.ZERO;
    private BigDecimal lyVar = BigDecimal.ZERO;
    private BigDecimal cyArrearColl = BigDecimal.ZERO;
    private BigDecimal cyCurrentColl = BigDecimal.ZERO;
    private BigDecimal cyPenaltyColl = BigDecimal.ZERO;
    private BigDecimal cyRebate = BigDecimal.ZERO;
    private BigDecimal cyAdvanceColl = BigDecimal.ZERO;
    private BigDecimal lyArrearColl = BigDecimal.ZERO;
    private BigDecimal lyCurrentColl = BigDecimal.ZERO;
    private BigDecimal lyPenaltyColl = BigDecimal.ZERO;
    private BigDecimal lyRebate = BigDecimal.ZERO;
    private BigDecimal lyAdvanceColl = BigDecimal.ZERO;
    private BigDecimal lyTotalArrearsColl = BigDecimal.ZERO;
    private BigDecimal lyTotalCurrentColl = BigDecimal.ZERO;
    private BigDecimal lyTotalPenaltyColl = BigDecimal.ZERO;
    private BigDecimal lyTotalRebate = BigDecimal.ZERO;
    private BigDecimal lyTotalAdvanceColl = BigDecimal.ZERO;
    private BigDecimal lyTotalColl = BigDecimal.ZERO;
    private BigDecimal lyArrearsPenaltyColl = BigDecimal.ZERO;
    private BigDecimal lyCurrentPenaltyColl = BigDecimal.ZERO;
    private BigDecimal cyArrearsPenaltyColl = BigDecimal.ZERO;
    private BigDecimal cyCurrentPenaltyColl = BigDecimal.ZERO;

    public String getBoundaryName() {
        return boundaryName;
    }

    public void setBoundaryName(String boundaryName) {
        this.boundaryName = boundaryName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getUlbGrade() {
        return ulbGrade;
    }

    public void setUlbGrade(String ulbGrade) {
        this.ulbGrade = ulbGrade;
    }

    public String getUlbName() {
        return ulbName;
    }

    public void setUlbName(String ulbName) {
        this.ulbName = ulbName;
    }

    public String getWardName() {
        return wardName;
    }

    public void setWardName(String wardName) {
        this.wardName = wardName;
    }

    public String getBillCollector() {
        return billCollector;
    }

    public void setBillCollector(String billCollector) {
        this.billCollector = billCollector;
    }

    public BigDecimal getTotalDmd() {
        return totalDmd;
    }

    public void setTotalDmd(BigDecimal totalDmd) {
        this.totalDmd = totalDmd;
    }

    public BigDecimal getCytdDmd() {
        return cytdDmd;
    }

    public void setCytdDmd(BigDecimal cytdDmd) {
        this.cytdDmd = cytdDmd;
    }

    public BigDecimal getCytdColl() {
        return cytdColl;
    }

    public void setCytdColl(BigDecimal cytdColl) {
        this.cytdColl = cytdColl;
    }

    public BigDecimal getPerformance() {
        return performance;
    }

    public void setPerformance(BigDecimal performance) {
        this.performance = performance;
    }

    public BigDecimal getCytdBalDmd() {
        return cytdBalDmd;
    }

    public void setCytdBalDmd(BigDecimal cytdBalDmd) {
        this.cytdBalDmd = cytdBalDmd;
    }

    public BigDecimal getLytdColl() {
        return lytdColl;
    }

    public void setLytdColl(BigDecimal lytdColl) {
        this.lytdColl = lytdColl;
    }

    public BigDecimal getLyVar() {
        return lyVar;
    }

    public void setLyVar(BigDecimal lyVar) {
        this.lyVar = lyVar;
    }

    public BigDecimal getTodayColl() {
        return todayColl;
    }

    public void setTodayColl(BigDecimal todayColl) {
        this.todayColl = todayColl;
    }

    public BigDecimal getArrearDemand() {
        return arrearDemand;
    }

    public void setArrearDemand(BigDecimal arrearDemand) {
        this.arrearDemand = arrearDemand;
    }

    public BigDecimal getCurrentDemand() {
        return currentDemand;
    }

    public void setCurrentDemand(BigDecimal currentDemand) {
        this.currentDemand = currentDemand;
    }

    public BigDecimal getProportionalArrearDemand() {
        return proportionalArrearDemand;
    }

    public void setProportionalArrearDemand(BigDecimal proportionalArrearDemand) {
        this.proportionalArrearDemand = proportionalArrearDemand;
    }

    public BigDecimal getProportionalCurrentDemand() {
        return proportionalCurrentDemand;
    }

    public void setProportionalCurrentDemand(BigDecimal proportionalCurrentDemand) {
        this.proportionalCurrentDemand = proportionalCurrentDemand;
    }

    public BigDecimal getTotalAssessments() {
        return totalAssessments;
    }

    public void setTotalAssessments(BigDecimal totalAssessments) {
        this.totalAssessments = totalAssessments;
    }

    public BigDecimal getLyTodayColl() {
        return lyTodayColl;
    }

    public void setLyTodayColl(BigDecimal lyTodayColl) {
        this.lyTodayColl = lyTodayColl;
    }

    public BigDecimal getDayTargetDemand() {
        return dayTargetDemand;
    }

    public void setDayTargetDemand(BigDecimal dayTargetDemand) {
        this.dayTargetDemand = dayTargetDemand;
    }

    public BigDecimal getArrearInterestDemand() {
        return arrearInterestDemand;
    }

    public void setArrearInterestDemand(BigDecimal arrearInterestDemand) {
        this.arrearInterestDemand = arrearInterestDemand;
    }

    public BigDecimal getCurrentInterestDemand() {
        return currentInterestDemand;
    }

    public void setCurrentInterestDemand(BigDecimal currentInterestDemand) {
        this.currentInterestDemand = currentInterestDemand;
    }

    public BigDecimal getCyArrearColl() {
        return cyArrearColl;
    }

    public void setCyArrearColl(BigDecimal cyArrearColl) {
        this.cyArrearColl = cyArrearColl;
    }

    public BigDecimal getCyCurrentColl() {
        return cyCurrentColl;
    }

    public void setCyCurrentColl(BigDecimal cyCurrentColl) {
        this.cyCurrentColl = cyCurrentColl;
    }

    public BigDecimal getCyPenaltyColl() {
        return cyPenaltyColl;
    }

    public void setCyPenaltyColl(BigDecimal cyPenaltyColl) {
        this.cyPenaltyColl = cyPenaltyColl;
    }

    public BigDecimal getLyArrearColl() {
        return lyArrearColl;
    }

    public void setLyArrearColl(BigDecimal lyArrearColl) {
        this.lyArrearColl = lyArrearColl;
    }

    public BigDecimal getLyCurrentColl() {
        return lyCurrentColl;
    }

    public void setLyCurrentColl(BigDecimal lyCurrentColl) {
        this.lyCurrentColl = lyCurrentColl;
    }

    public BigDecimal getCyRebate() {
        return cyRebate;
    }

    public void setCyRebate(BigDecimal cyRebate) {
        this.cyRebate = cyRebate;
    }

    public BigDecimal getCyAdvanceColl() {
        return cyAdvanceColl;
    }

    public void setCyAdvanceColl(BigDecimal cyAdvanceColl) {
        this.cyAdvanceColl = cyAdvanceColl;
    }

    public BigDecimal getLyPenaltyColl() {
        return lyPenaltyColl;
    }

    public void setLyPenaltyColl(BigDecimal lyPenaltyColl) {
        this.lyPenaltyColl = lyPenaltyColl;
    }

    public BigDecimal getLyRebate() {
        return lyRebate;
    }

    public void setLyRebate(BigDecimal lyRebate) {
        this.lyRebate = lyRebate;
    }

    public BigDecimal getLyAdvanceColl() {
        return lyAdvanceColl;
    }

    public void setLyAdvanceColl(BigDecimal lyAdvanceColl) {
        this.lyAdvanceColl = lyAdvanceColl;
    }

    public BigDecimal getLyTotalArrearsColl() {
        return lyTotalArrearsColl;
    }

    public void setLyTotalArrearsColl(BigDecimal lyTotalArrearsColl) {
        this.lyTotalArrearsColl = lyTotalArrearsColl;
    }

    public BigDecimal getLyTotalCurrentColl() {
        return lyTotalCurrentColl;
    }

    public void setLyTotalCurrentColl(BigDecimal lyTotalCurrentColl) {
        this.lyTotalCurrentColl = lyTotalCurrentColl;
    }

    public BigDecimal getLyTotalPenaltyColl() {
        return lyTotalPenaltyColl;
    }

    public void setLyTotalPenaltyColl(BigDecimal lyTotalPenaltyColl) {
        this.lyTotalPenaltyColl = lyTotalPenaltyColl;
    }

    public BigDecimal getLyTotalRebate() {
        return lyTotalRebate;
    }

    public void setLyTotalRebate(BigDecimal lyTotalRebate) {
        this.lyTotalRebate = lyTotalRebate;
    }

    public BigDecimal getLyTotalAdvanceColl() {
        return lyTotalAdvanceColl;
    }

    public void setLyTotalAdvanceColl(BigDecimal lyTotalAdvanceColl) {
        this.lyTotalAdvanceColl = lyTotalAdvanceColl;
    }

    public BigDecimal getLyTotalColl() {
        return lyTotalColl;
    }

    public void setLyTotalColl(BigDecimal lyTotalColl) {
        this.lyTotalColl = lyTotalColl;
    }

    public BigDecimal getLyArrearsPenaltyColl() {
        return lyArrearsPenaltyColl;
    }

    public void setLyArrearsPenaltyColl(BigDecimal lyArrearsPenaltyColl) {
        this.lyArrearsPenaltyColl = lyArrearsPenaltyColl;
    }

    public BigDecimal getLyCurrentPenaltyColl() {
        return lyCurrentPenaltyColl;
    }

    public void setLyCurrentPenaltyColl(BigDecimal lyCurrentPenaltyColl) {
        this.lyCurrentPenaltyColl = lyCurrentPenaltyColl;
    }

    public BigDecimal getCyArrearsPenaltyColl() {
        return cyArrearsPenaltyColl;
    }

    public void setCyArrearsPenaltyColl(BigDecimal cyArrearsPenaltyColl) {
        this.cyArrearsPenaltyColl = cyArrearsPenaltyColl;
    }

    public BigDecimal getCyCurrentPenaltyColl() {
        return cyCurrentPenaltyColl;
    }

    public void setCyCurrentPenaltyColl(BigDecimal cyCurrentPenaltyColl) {
        this.cyCurrentPenaltyColl = cyCurrentPenaltyColl;
    }

    public String getBillCollMobNo() {
        return billCollMobNo;
    }

    public void setBillCollMobNo(String billCollMobNo) {
        this.billCollMobNo = billCollMobNo;
    }

    public String getRevenueInspector() {
        return revenueInspector;
    }

    public void setRevenueInspector(String revenueInspector) {
        this.revenueInspector = revenueInspector;
    }

    public String getRevInspectorMobNo() {
        return revInspectorMobNo;
    }

    public void setRevInspectorMobNo(String revInspectorMobNo) {
        this.revInspectorMobNo = revInspectorMobNo;
    }

    public String getRevenueOfficer() {
        return revenueOfficer;
    }

    public void setRevenueOfficer(String revenueOfficer) {
        this.revenueOfficer = revenueOfficer;
    }

    public String getRevOfficerMobNo() {
        return revOfficerMobNo;
    }

    public void setRevOfficerMobNo(String revOfficerMobNo) {
        this.revOfficerMobNo = revOfficerMobNo;
    }

}