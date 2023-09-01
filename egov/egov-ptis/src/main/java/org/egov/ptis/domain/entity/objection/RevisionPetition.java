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
package org.egov.ptis.domain.entity.objection;

import org.egov.commons.EgwStatus;
import org.egov.infra.persistence.entity.Auditable;
import org.egov.infra.persistence.validator.annotation.Required;
import org.egov.infra.workflow.entity.StateAware;
import org.egov.pims.commons.Position;
import org.egov.ptis.domain.entity.property.BasicProperty;
import org.egov.ptis.domain.entity.property.Document;
import org.egov.ptis.domain.entity.property.PropertyImpl;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static org.egov.ptis.constants.PropertyTaxConstants.PROPERTY_TYPE_CATEGORIES;

public class RevisionPetition extends StateAware<Position> implements Auditable {

    public static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    private static final long serialVersionUID = 1L;

    private Long id;

    private EgwStatus egwStatus;

    private BasicProperty basicProperty;

    @Length(max = 50, message = "objection.objectionNumber.length")
    private String objectionNumber;

    private Date recievedOn;

    @Length(max = 256, message = "objection.objectionNumber.length")
    private String recievedBy;

    private String details;

    private String docNumberObjection;

    private String docNumberOutcome;
    private PropertyImpl property;

    @Valid
    private List<Hearing> hearings = new LinkedList<>();

    @Valid
    private List<Inspection> inspections = new LinkedList<>();

    private Date dateOfOutcome;

    private String remarks;// for dateOfOutcome

    private Boolean objectionRejected;
    private Boolean generateSpecialNotice;
    private String meesevaApplicationNumber;
    private String applicationNo;
    private List<Document> documents = new ArrayList<>();
    private String type;
    private String source;

    @Override
    public String getStateDetails() {
        final StringBuilder stateDetails = new StringBuilder("");
        stateDetails.append(getBasicProperty().getUpicNo()).append(", ")
                .append(getBasicProperty().getPrimaryOwner().getName()).append(", ")
                .append(PROPERTY_TYPE_CATEGORIES
                        .get(getBasicProperty().getProperty().getPropertyDetail().getCategoryType()))
                .append(", ").append(getBasicProperty().getPropertyID().getLocality().getName());
        return stateDetails.toString();
    }

    public EgwStatus getEgwStatus() {
        return egwStatus;
    }

    public void setEgwStatus(final EgwStatus egwStatus) {
        this.egwStatus = egwStatus;
    }

    public String getObjectionNumber() {
        return objectionNumber;
    }

    public void setObjectionNumber(final String objectionNumber) {
        this.objectionNumber = objectionNumber;
    }

    @Required(message = "objection.receiviedOn.null")
    public Date getRecievedOn() {
        return recievedOn;
    }

    public void setRecievedOn(final Date recievedOn) {
        this.recievedOn = recievedOn;
    }

    @Required(message = "objection.receiviedBy.null")
    /* @Length(max = 256, message = "objection.receivedBy.length") */
    public String getRecievedBy() {
        return recievedBy;
    }

    public void setRecievedBy(final String recievedBy) {
        this.recievedBy = recievedBy;
    }

    @Required(message = "objection.details.null")
    @Length(max = 1024, message = "objection.details.length")
    public String getDetails() {
        return details;
    }

    public void setDetails(final String details) {
        this.details = details;
    }

    public List<Hearing> getHearings() {
        return hearings;
    }

    public void setHearings(final List<Hearing> hearings) {
        this.hearings = hearings;
    }

    public List<Inspection> getInspections() {
        return inspections;
    }

    public void setInspections(final List<Inspection> inspections) {
        this.inspections = inspections;
    }

    public Date getDateOfOutcome() {
        return dateOfOutcome;
    }

    public void setDateOfOutcome(final Date dateOfOutcome) {
        this.dateOfOutcome = dateOfOutcome;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(final String remarks) {
        this.remarks = remarks;
    }

    public Boolean getObjectionRejected() {
        return objectionRejected;
    }

    public void setObjectionRejected(final Boolean objectionRejected) {
        this.objectionRejected = objectionRejected;
    }

    public BasicProperty getBasicProperty() {
        return basicProperty;
    }

    public void setBasicProperty(final BasicProperty basicProperty) {
        this.basicProperty = basicProperty;
    }

    public String getDocNumberObjection() {
        return docNumberObjection;
    }

    public void setDocNumberObjection(final String docNumberObjection) {
        this.docNumberObjection = docNumberObjection;
    }

    public String getDocNumberOutcome() {
        return docNumberOutcome;
    }

    public void setDocNumberOutcome(final String docNumberOutcome) {
        this.docNumberOutcome = docNumberOutcome;
    }

    public String getFmtdReceivedOn() {
        if (recievedOn != null)
            return dateFormat.format(recievedOn);
        else
            return "";
    }

    @Override
    public String toString() {

        final StringBuilder sb = new StringBuilder();

        sb.append("UcipNo :").append(null != basicProperty ? basicProperty.getUpicNo() : " ");
        sb.append("status :").append(null != egwStatus ? egwStatus.getDescription() : " ");
        sb.append("objectionNumber :").append(null != objectionNumber ? objectionNumber : " ");

        return sb.toString();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    public PropertyImpl getProperty() {
        return property;
    }

    public void setProperty(final PropertyImpl property) {
        this.property = property;
    }

    public Boolean getGenerateSpecialNotice() {
        return generateSpecialNotice;
    }

    public void setGenerateSpecialNotice(final Boolean generateSpecialNotice) {
        this.generateSpecialNotice = generateSpecialNotice;
    }

    public String getMeesevaApplicationNumber() {
        return meesevaApplicationNumber;
    }

    public void setMeesevaApplicationNumber(String meesevaApplicationNumber) {
        this.meesevaApplicationNumber = meesevaApplicationNumber;
    }

    public String getApplicationNo() {
        return applicationNo;
    }

    public void setApplicationNo(String applicationNo) {
        this.applicationNo = applicationNo;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
