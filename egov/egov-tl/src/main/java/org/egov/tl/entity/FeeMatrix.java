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

package org.egov.tl.entity;

import org.egov.commons.CFinancialYear;
import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.infra.persistence.validator.annotation.CompareDates;
import org.egov.infra.persistence.validator.annotation.UniqueDateOverlap;
import org.hibernate.envers.AuditMappedBy;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "egtl_feematrix")
@SequenceGenerator(name = FeeMatrix.SEQ, sequenceName = FeeMatrix.SEQ, allocationSize = 1)
@CompareDates(fromDate = "effectiveFrom", toDate = "effectiveTo", message = "{feematrix.effective.date.range}")
@UniqueDateOverlap(fromField = "effectiveFrom", toField = "effectiveTo", uniqueFields =
        {"natureOfBusiness", "licenseCategory", "subCategory", "licenseAppType", "feeType"},
        message = "{feematrix.exist}")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
public class FeeMatrix extends AbstractAuditable {
    public static final String SEQ = "seq_egtl_feematrix";
    private static final long serialVersionUID = 3119126267277124321L;

    @Id
    @GeneratedValue(generator = SEQ, strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "natureOfBusiness", updatable = false)
    private NatureOfBusiness natureOfBusiness;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "licenseCategory", updatable = false)
    private LicenseCategory licenseCategory;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subCategory", updatable = false)
    private LicenseSubCategory subCategory;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "licenseAppType", updatable = false)
    private LicenseAppType licenseAppType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feeType", updatable = false)
    private FeeType feeType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financialYear", updatable = false)
    private CFinancialYear financialYear;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date effectiveFrom;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date effectiveTo;

    @Valid
    @OrderBy("uomFrom")
    @OneToMany(mappedBy = "feeMatrix", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            fetch = FetchType.LAZY, orphanRemoval = true)
    @AuditMappedBy(mappedBy = "feeMatrix")
    private List<FeeMatrixDetail> feeMatrixDetail = new ArrayList<>();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public FeeType getFeeType() {
        return feeType;
    }

    public void setFeeType(FeeType feeType) {
        this.feeType = feeType;
    }

    public NatureOfBusiness getNatureOfBusiness() {
        return natureOfBusiness;
    }

    public void setNatureOfBusiness(NatureOfBusiness natureOfBusiness) {
        this.natureOfBusiness = natureOfBusiness;
    }

    public LicenseCategory getLicenseCategory() {
        return licenseCategory;
    }

    public void setLicenseCategory(LicenseCategory licenseCategory) {
        this.licenseCategory = licenseCategory;
    }

    public LicenseSubCategory getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(LicenseSubCategory subCategory) {
        this.subCategory = subCategory;
    }

    public LicenseAppType getLicenseAppType() {
        return licenseAppType;
    }

    public void setLicenseAppType(LicenseAppType licenseAppType) {
        this.licenseAppType = licenseAppType;
    }

    public List<FeeMatrixDetail> getFeeMatrixDetail() {
        return feeMatrixDetail;
    }

    public void setFeeMatrixDetail(List<FeeMatrixDetail> feeMatrixDetail) {
        this.feeMatrixDetail = feeMatrixDetail;
    }

    public CFinancialYear getFinancialYear() {
        return financialYear;
    }

    public void setFinancialYear(CFinancialYear financialYear) {
        this.financialYear = financialYear;
    }

    public Date getEffectiveFrom() {
        return this.effectiveFrom;
    }

    public void setEffectiveFrom(Date effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public Date getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(Date effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof FeeMatrix))
            return false;
        FeeMatrix feeMatrix = (FeeMatrix) obj;
        return Objects.equals(getNatureOfBusiness().getId(), feeMatrix.getNatureOfBusiness().getId()) &&
                Objects.equals(getLicenseCategory().getId(), feeMatrix.getLicenseCategory().getId()) &&
                Objects.equals(getSubCategory().getId(), feeMatrix.getSubCategory().getId()) &&
                Objects.equals(getLicenseAppType().getId(), feeMatrix.getLicenseAppType().getId()) &&
                Objects.equals(getFeeType().getId(), feeMatrix.getFeeType().getId()) &&
                Objects.equals(getFinancialYear().getId(), feeMatrix.getFinancialYear().getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNatureOfBusiness().getId(), getLicenseCategory().getId(), getSubCategory().getId(),
                getLicenseAppType().getId(), getFeeType().getId(), getFinancialYear().getId());
    }
}