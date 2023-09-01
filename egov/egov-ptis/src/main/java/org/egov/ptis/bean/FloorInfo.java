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

package org.egov.ptis.bean;

import org.apache.commons.lang3.StringUtils;

public class FloorInfo {
    private String floorNo=StringUtils.EMPTY;
    private String buildClassification=StringUtils.EMPTY;
    private String natureOfUsage=StringUtils.EMPTY;
    private String firmName=StringUtils.EMPTY;
    private String occupancy=StringUtils.EMPTY;
    private String occupantName=StringUtils.EMPTY;
    private String constructionDate=StringUtils.EMPTY;
    private String occupancyDate=StringUtils.EMPTY;
    private Float plinthArea=0.0f;
    private Float plinthLength=0.0f;
    private Float plinthBreadth=0.0f;
    private Boolean unstructuredLand;
    private String buildingPermissionNo=StringUtils.EMPTY;
    private String buildingPermissionDate=StringUtils.EMPTY;
    private Float buildingPlanPlinthArea=0.0f;

    public String getFloorNo() {
        return floorNo;
    }

    public void setFloorNo(String floorNo) {
        this.floorNo = floorNo;
    }

    public String getBuildClassification() {
        return buildClassification;
    }

    public void setBuildClassification(String buildClassification) {
        this.buildClassification = buildClassification;
    }

    public String getNatureOfUsage() {
        return natureOfUsage;
    }

    public void setNatureOfUsage(String natureOfUsage) {
        this.natureOfUsage = natureOfUsage;
    }

    public String getFirmName() {
        return firmName;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    public String getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(String occupancy) {
        this.occupancy = occupancy;
    }

    public String getOccupantName() {
        return occupantName;
    }

    public void setOccupantName(String occupantName) {
        this.occupantName = occupantName;
    }

    public String getConstructionDate() {
        return constructionDate;
    }

    public void setConstructionDate(String constructionDate) {
        this.constructionDate = constructionDate;
    }

    public String getOccupancyDate() {
        return occupancyDate;
    }

    public void setOccupancyDate(String occupancyDate) {
        this.occupancyDate = occupancyDate;
    }

    public Float getPlinthArea() {
        return plinthArea;
    }

    public void setPlinthArea(Float plinthArea) {
        this.plinthArea = plinthArea;
    }

    public Float getPlinthLength() {
        return plinthLength;
    }

    public void setPlinthLength(Float plinthLength) {
        this.plinthLength = plinthLength;
    }

    public Float getPlinthBreadth() {
        return plinthBreadth;
    }

    public void setPlinthBreadth(Float plinthBreadth) {
        this.plinthBreadth = plinthBreadth;
    }

    public Boolean getUnstructuredLand() {
        return unstructuredLand;
    }

    public void setUnstructuredLand(Boolean unstructuredLand) {
        this.unstructuredLand = unstructuredLand;
    }

    public String getBuildingPermissionNo() {
        return buildingPermissionNo;
    }

    public void setBuildingPermissionNo(String buildingPermissionNo) {
        this.buildingPermissionNo = buildingPermissionNo;
    }

    public String getBuildingPermissionDate() {
        return buildingPermissionDate;
    }

    public void setBuildingPermissionDate(String buildingPermissionDate) {
        this.buildingPermissionDate = buildingPermissionDate;
    }

    public Float getBuildingPlanPlinthArea() {
        return buildingPlanPlinthArea;
    }

    public void setBuildingPlanPlinthArea(Float buildingPlanPlinthArea) {
        this.buildingPlanPlinthArea = buildingPlanPlinthArea;
    }

}