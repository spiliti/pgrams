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

package org.egov.wtms.application.entity;

import static org.egov.infra.utils.DateUtils.getDefaultFormattedDate;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;


import org.egov.infra.workflow.entity.StateAware;
import org.egov.pims.commons.Position;

@Entity
@Table(name = "egwtr_regularise_connection_detail")
@SequenceGenerator(name = RegularisedConnection.SEQ_REGULARISE_CONNECTION, sequenceName = RegularisedConnection.SEQ_REGULARISE_CONNECTION, allocationSize = 1)
public class RegularisedConnection extends StateAware<Position> {

    public static final String SEQ_REGULARISE_CONNECTION = "SEQ_EGWTR_REGULARISE_CONNECTION_DETAIL";
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = SEQ_REGULARISE_CONNECTION, strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    private String ulbCode;

    @NotNull
    private String propertyIdentifier;

    private String applicationNumber;
    
    @Temporal(value = TemporalType.DATE)
    private Date applicationDate;
    
    private String source;
 
    private String referenceNumber;

    @Override
    public String getStateDetails() {
        return String.format(" Application Number %s with application date %s ",
                applicationNumber != null ? applicationNumber : propertyIdentifier,
                applicationDate != null ? getDefaultFormattedDate(applicationDate) : getDefaultFormattedDate(new Date()));
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    protected void setId(final Long id) {
        this.id = id;
    }

    public String getUlbCode() {
        return ulbCode;
    }

    public void setUlbCode(final String ulbCode) {
        this.ulbCode = ulbCode;
    }

    public String getPropertyIdentifier() {
        return propertyIdentifier;
    }

    public void setPropertyIdentifier(final String propertyIdentifier) {
        this.propertyIdentifier = propertyIdentifier;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(final String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(final Date applicationDate) {
        this.applicationDate = applicationDate;
    }
    
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

}
