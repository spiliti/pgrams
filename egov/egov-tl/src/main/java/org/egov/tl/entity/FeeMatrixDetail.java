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

import org.egov.infra.persistence.entity.AbstractPersistable;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "egtl_feematrix_detail")
@SequenceGenerator(name = FeeMatrixDetail.SEQ, sequenceName = FeeMatrixDetail.SEQ, allocationSize = 1)
@Audited
public class FeeMatrixDetail extends AbstractPersistable<Long> {
    public static final String SEQ = "seq_egtl_feematrix_detail";
    private static final long serialVersionUID = -1477850420070873621L;

    @Id
    @GeneratedValue(generator = SEQ, strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "feeMatrix")
    private FeeMatrix feeMatrix;

    @NotNull
    @Min(0)
    private Integer uomFrom;

    @NotNull
    @Min(1)
    private Integer uomTo;

    @NotNull
    @Min(1)
    private BigDecimal amount;

    @Transient
    private boolean markedForRemoval;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUomFrom() {
        return uomFrom;
    }

    public void setUomFrom(Integer uomFrom) {
        this.uomFrom = uomFrom;
    }

    public Integer getUomTo() {
        return uomTo;
    }

    public void setUomTo(Integer uomTo) {
        this.uomTo = uomTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public FeeMatrix getFeeMatrix() {
        return feeMatrix;
    }

    public void setFeeMatrix(FeeMatrix feeMatrix) {
        this.feeMatrix = feeMatrix;
    }

    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }

    public void setMarkedForRemoval(boolean markedForRemoval) {
        this.markedForRemoval = markedForRemoval;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof FeeMatrixDetail))
            return false;
        FeeMatrixDetail that = (FeeMatrixDetail) other;
        return Objects.equals(getFeeMatrix(), that.getFeeMatrix()) &&
                Objects.equals(getUomFrom(), that.getUomFrom()) &&
                Objects.equals(getUomTo(), that.getUomTo());
    }


    @Override
    public int hashCode() {
        return Objects.hash(getFeeMatrix(), getUomFrom(), getUomTo());
    }
}
