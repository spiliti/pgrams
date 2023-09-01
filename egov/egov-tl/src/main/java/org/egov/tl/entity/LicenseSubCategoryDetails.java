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
package org.egov.tl.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.egov.infra.persistence.entity.AbstractPersistable;
import org.egov.tl.entity.enums.RateType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.Objects;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "egtl_subcategory_details")
@SequenceGenerator(name = LicenseSubCategoryDetails.SEQUENCE, sequenceName = LicenseSubCategoryDetails.SEQUENCE, allocationSize = 1)
public class LicenseSubCategoryDetails extends AbstractPersistable<Long> {

    protected static final String SEQUENCE = "SEQ_egtl_subcategory_details";
    private static final long serialVersionUID = 5084451633368214374L;

    @Id
    @GeneratedValue(generator = SEQUENCE, strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "subcategory_id")
    private LicenseSubCategory subCategory;

    @ManyToOne
    @JoinColumn(name = "uom_id")
    @NotNull
    private UnitOfMeasurement uom;

    @ManyToOne
    @JoinColumn(name = "feetype_id")
    @NotNull
    private FeeType feeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "RATETYPE")
    @NotNull
    private RateType rateType;

    @Transient
    private boolean markedForRemoval;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public LicenseSubCategory getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(LicenseSubCategory subCategory) {
        this.subCategory = subCategory;
    }

    public UnitOfMeasurement getUom() {
        return uom;
    }

    public void setUom(UnitOfMeasurement uom) {
        this.uom = uom;
    }

    public RateType getRateType() {
        return rateType;
    }

    public void setRateType(RateType rateType) {
        this.rateType = rateType;
    }

    public FeeType getFeeType() {
        return feeType;
    }

    public void setFeeType(FeeType feeType) {
        this.feeType = feeType;
    }

    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }

    public void setMarkedForRemoval(final boolean markedForRemoval) {
        this.markedForRemoval = markedForRemoval;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof LicenseSubCategoryDetails))
            return false;
        LicenseSubCategoryDetails that = (LicenseSubCategoryDetails) obj;
        return Objects.equals(getSubCategory(), that.getSubCategory()) &&
                Objects.equals(getFeeType(), that.getFeeType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSubCategory(), getFeeType());
    }
}
