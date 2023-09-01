/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
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
 */
package org.egov.bpa.application.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;

import org.egov.infra.persistence.entity.AbstractAuditable;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "EGBPA_AUTODCR")
@SequenceGenerator(name = AutoDCR.SEQ_EGBPA_AUTODCR, sequenceName = AutoDCR.SEQ_EGBPA_AUTODCR, allocationSize = 1)
public class AutoDCR extends AbstractAuditable {
    private static final long serialVersionUID = 3078684328383202788L;
    public static final String SEQ_EGBPA_AUTODCR = "seq_EGBPA_AUTODCR";

    @Id
    @GeneratedValue(generator = SEQ_EGBPA_AUTODCR, strategy = GenerationType.SEQUENCE)
    private Long id;
    @Length(min = 1, max = 128)
    private String autoDcrNum;
    @Length(min = 1, max = 128)
    private String applicantName;
    @Length(min = 1, max = 128)
    private String address;
    @Length(min = 1, max = 128)
    private String emailId;
    @Length(min = 1, max = 12)
    private String mobilNo;
    @Length(min = 1, max = 128)
    private String zone;
    @Length(min = 1, max = 128)
    private String ward;
    @Length(min = 1, max = 128)
    private String dooNo;
    @Length(min = 1, max = 128)
    private String plotNumber;
    @Length(min = 1, max = 128)
    private String surveyNo;
    @Length(min = 1, max = 128)
    private String village;
    @Length(min = 1, max = 128)
    private String blockNumber;
    private BigDecimal plotArea;
    private Integer floorCount;
    private BigDecimal fileNumber;
    @Length(min = 1, max = 128)
    private String fileCaseType;
    @Length(min = 1, max = 128)
    private String fileBldgcategory;
    @Length(min = 1, max = 128)
    private String fileLandUseZone;
    @Length(min = 1, max = 128)
    private String fileProposalType;

    private Date fileInWardDate;

    @Length(min = 1, max = 128)
    private String fileZone;
    @Length(min = 1, max = 128)
    private String fileDevision;
    @Length(min = 1, max = 128)
    private String filePlotNumber;
    @Length(min = 1, max = 128)
    private String fileRoadName;
    @Length(min = 1, max = 128)
    private String fileDoorNumber;
    @Length(min = 1, max = 128)
    private String fileSurveyNumber;
    @Length(min = 1, max = 128)
    private String fileRevenuevillage;
    @Length(min = 1, max = 128)
    private String fileBlockNumber;
    @Length(min = 1, max = 128)
    private String fileMobileNumber;
    @Length(min = 1, max = 128)
    private String fileEmail;
    @Length(min = 1, max = 128)
    private String fileUniqueId;
    @Length(min = 1, max = 128)
    private BigDecimal filePattaPltArea;
    private BigDecimal fileDocPlotArea;
    private BigDecimal fileSitePlotArea;
    @Length(min = 1, max = 128)
    private String fileStatus;
    private Integer plotUse;
    private BigDecimal plotgrossArea;
    private BigDecimal plototalbuilduparea;
    private BigDecimal plotconsumerFSI;
    private BigDecimal plotCoveragePercentage;
    private BigDecimal plotNetArea;
    private BigDecimal plotWidth;
    private BigDecimal plotaButtingRoad;
    private BigDecimal plotFrontage;
    private BigDecimal plotCompoundWellArea;
    private BigDecimal plotwellOHTSumpTankArea;
    @Length(min = 1, max = 128)
    private String buildingName;
    private BigDecimal buildingHeight;
    private BigDecimal bldgMarginFrontSide;
    private BigDecimal bldgMarginRearSide;
    private BigDecimal bldgMarginSide1;
    private BigDecimal bldgMarginSide2;
    private BigDecimal fileApplicantName;
    @Length(min = 1, max = 128)
    private String logicalPath;

    @Valid
    @OneToMany(mappedBy = "autoDcr", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AutoDCRFloorDetails> appDcRFloorDetails = new ArrayList<>();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    public String getAutoDcrNum() {
        return autoDcrNum;
    }

    public void setAutoDcrNum(final String autoDcrNum) {
        this.autoDcrNum = autoDcrNum;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(final String applicantName) {
        this.applicantName = applicantName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(final String emailId) {
        this.emailId = emailId;
    }

    public String getMobilNo() {
        return mobilNo;
    }

    public void setMobilNo(final String mobilNo) {
        this.mobilNo = mobilNo;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(final String zone) {
        this.zone = zone;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(final String ward) {
        this.ward = ward;
    }

    public String getDooNo() {
        return dooNo;
    }

    public void setDooNo(final String dooNo) {
        this.dooNo = dooNo;
    }

    public String getPlotNumber() {
        return plotNumber;
    }

    public void setPlotNumber(final String plotNumber) {
        this.plotNumber = plotNumber;
    }

    public String getSurveyNo() {
        return surveyNo;
    }

    public void setSurveyNo(final String surveyNo) {
        this.surveyNo = surveyNo;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(final String village) {
        this.village = village;
    }

    public String getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(final String blockNumber) {
        this.blockNumber = blockNumber;
    }

    public BigDecimal getPlotArea() {
        return plotArea;
    }

    public void setPlotArea(final BigDecimal plotArea) {
        this.plotArea = plotArea;
    }

    public Integer getFloorCount() {
        return floorCount;
    }

    public void setFloorCount(final Integer floorCount) {
        this.floorCount = floorCount;
    }

    public BigDecimal getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(final BigDecimal fileNumber) {
        this.fileNumber = fileNumber;
    }

    public String getFileCaseType() {
        return fileCaseType;
    }

    public void setFileCaseType(final String fileCaseType) {
        this.fileCaseType = fileCaseType;
    }

    public String getFileBldgcategory() {
        return fileBldgcategory;
    }

    public void setFileBldgcategory(final String fileBldgcategory) {
        this.fileBldgcategory = fileBldgcategory;
    }

    public String getFileLandUseZone() {
        return fileLandUseZone;
    }

    public void setFileLandUseZone(final String fileLandUseZone) {
        this.fileLandUseZone = fileLandUseZone;
    }

    public String getFileProposalType() {
        return fileProposalType;
    }

    public void setFileProposalType(final String fileProposalType) {
        this.fileProposalType = fileProposalType;
    }

    public Date getFileInWardDate() {
        return fileInWardDate;
    }

    public void setFileInWardDate(final Date fileInWardDate) {
        this.fileInWardDate = fileInWardDate;
    }

    public String getFileZone() {
        return fileZone;
    }

    public void setFileZone(final String fileZone) {
        this.fileZone = fileZone;
    }

    public String getFileDevision() {
        return fileDevision;
    }

    public void setFileDevision(final String fileDevision) {
        this.fileDevision = fileDevision;
    }

    public String getFilePlotNumber() {
        return filePlotNumber;
    }

    public void setFilePlotNumber(final String filePlotNumber) {
        this.filePlotNumber = filePlotNumber;
    }

    public String getFileRoadName() {
        return fileRoadName;
    }

    public void setFileRoadName(final String fileRoadName) {
        this.fileRoadName = fileRoadName;
    }

    public String getFileDoorNumber() {
        return fileDoorNumber;
    }

    public void setFileDoorNumber(final String fileDoorNumber) {
        this.fileDoorNumber = fileDoorNumber;
    }

    public String getFileSurveyNumber() {
        return fileSurveyNumber;
    }

    public void setFileSurveyNumber(final String fileSurveyNumber) {
        this.fileSurveyNumber = fileSurveyNumber;
    }

    public String getFileRevenuevillage() {
        return fileRevenuevillage;
    }

    public void setFileRevenuevillage(final String fileRevenuevillage) {
        this.fileRevenuevillage = fileRevenuevillage;
    }

    public String getFileBlockNumber() {
        return fileBlockNumber;
    }

    public void setFileBlockNumber(final String fileBlockNumber) {
        this.fileBlockNumber = fileBlockNumber;
    }

    public String getFileMobileNumber() {
        return fileMobileNumber;
    }

    public void setFileMobileNumber(final String fileMobileNumber) {
        this.fileMobileNumber = fileMobileNumber;
    }

    public String getFileEmail() {
        return fileEmail;
    }

    public void setFileEmail(final String fileEmail) {
        this.fileEmail = fileEmail;
    }

    public String getFileUniqueId() {
        return fileUniqueId;
    }

    public void setFileUniqueId(final String fileUniqueId) {
        this.fileUniqueId = fileUniqueId;
    }

    public BigDecimal getFilePattaPltArea() {
        return filePattaPltArea;
    }

    public void setFilePattaPltArea(final BigDecimal filePattaPltArea) {
        this.filePattaPltArea = filePattaPltArea;
    }

    public BigDecimal getFileDocPlotArea() {
        return fileDocPlotArea;
    }

    public void setFileDocPlotArea(final BigDecimal fileDocPlotArea) {
        this.fileDocPlotArea = fileDocPlotArea;
    }

    public BigDecimal getFileSitePlotArea() {
        return fileSitePlotArea;
    }

    public void setFileSitePlotArea(final BigDecimal fileSitePlotArea) {
        this.fileSitePlotArea = fileSitePlotArea;
    }

    public String getFileStatus() {
        return fileStatus;
    }

    public void setFileStatus(final String fileStatus) {
        this.fileStatus = fileStatus;
    }

    public Integer getPlotUse() {
        return plotUse;
    }

    public void setPlotUse(final Integer plotUse) {
        this.plotUse = plotUse;
    }

    public BigDecimal getPlotgrossArea() {
        return plotgrossArea;
    }

    public void setPlotgrossArea(final BigDecimal plotgrossArea) {
        this.plotgrossArea = plotgrossArea;
    }

    public BigDecimal getPlototalbuilduparea() {
        return plototalbuilduparea;
    }

    public void setPlototalbuilduparea(final BigDecimal plototalbuilduparea) {
        this.plototalbuilduparea = plototalbuilduparea;
    }

    public BigDecimal getPlotconsumerFSI() {
        return plotconsumerFSI;
    }

    public void setPlotconsumerFSI(final BigDecimal plotconsumerFSI) {
        this.plotconsumerFSI = plotconsumerFSI;
    }

    public BigDecimal getPlotCoveragePercentage() {
        return plotCoveragePercentage;
    }

    public void setPlotCoveragePercentage(final BigDecimal plotCoveragePercentage) {
        this.plotCoveragePercentage = plotCoveragePercentage;
    }

    public BigDecimal getPlotNetArea() {
        return plotNetArea;
    }

    public void setPlotNetArea(final BigDecimal plotNetArea) {
        this.plotNetArea = plotNetArea;
    }

    public BigDecimal getPlotWidth() {
        return plotWidth;
    }

    public void setPlotWidth(final BigDecimal plotWidth) {
        this.plotWidth = plotWidth;
    }

    public BigDecimal getPlotaButtingRoad() {
        return plotaButtingRoad;
    }

    public void setPlotaButtingRoad(final BigDecimal plotaButtingRoad) {
        this.plotaButtingRoad = plotaButtingRoad;
    }

    public BigDecimal getPlotFrontage() {
        return plotFrontage;
    }

    public void setPlotFrontage(final BigDecimal plotFrontage) {
        this.plotFrontage = plotFrontage;
    }

    public BigDecimal getPlotCompoundWellArea() {
        return plotCompoundWellArea;
    }

    public void setPlotCompoundWellArea(final BigDecimal plotCompoundWellArea) {
        this.plotCompoundWellArea = plotCompoundWellArea;
    }

    public BigDecimal getPlotwellOHTSumpTankArea() {
        return plotwellOHTSumpTankArea;
    }

    public void setPlotwellOHTSumpTankArea(final BigDecimal plotwellOHTSumpTankArea) {
        this.plotwellOHTSumpTankArea = plotwellOHTSumpTankArea;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(final String buildingName) {
        this.buildingName = buildingName;
    }

    public BigDecimal getBuildingHeight() {
        return buildingHeight;
    }

    public void setBuildingHeight(final BigDecimal buildingHeight) {
        this.buildingHeight = buildingHeight;
    }

    public BigDecimal getBldgMarginFrontSide() {
        return bldgMarginFrontSide;
    }

    public void setBldgMarginFrontSide(final BigDecimal bldgMarginFrontSide) {
        this.bldgMarginFrontSide = bldgMarginFrontSide;
    }

    public BigDecimal getBldgMarginRearSide() {
        return bldgMarginRearSide;
    }

    public void setBldgMarginRearSide(final BigDecimal bldgMarginRearSide) {
        this.bldgMarginRearSide = bldgMarginRearSide;
    }

    public BigDecimal getBldgMarginSide1() {
        return bldgMarginSide1;
    }

    public void setBldgMarginSide1(final BigDecimal bldgMarginSide1) {
        this.bldgMarginSide1 = bldgMarginSide1;
    }

    public BigDecimal getBldgMarginSide2() {
        return bldgMarginSide2;
    }

    public void setBldgMarginSide2(final BigDecimal bldgMarginSide2) {
        this.bldgMarginSide2 = bldgMarginSide2;
    }

    public BigDecimal getFileApplicantName() {
        return fileApplicantName;
    }

    public void setFileApplicantName(final BigDecimal fileApplicantName) {
        this.fileApplicantName = fileApplicantName;
    }

    public String getLogicalPath() {
        return logicalPath;
    }

    public void setLogicalPath(final String logicalPath) {
        this.logicalPath = logicalPath;
    }

    public List<AutoDCRFloorDetails> getAppDcRFloorDetails() {
        return appDcRFloorDetails;
    }

    public void setAppDcRFloorDetails(final List<AutoDCRFloorDetails> appDcRFloorDetails) {
        this.appDcRFloorDetails = appDcRFloorDetails;
    }

}
