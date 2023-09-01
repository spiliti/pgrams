/**
 * eGov suite of products aim to improve the internal efficiency,transparency,
   accountability and the service delivery of the government  organizations.
    Copyright (C) <2015>  eGovernments Foundation
    The updated version of eGov suite of products as by eGovernments Foundation
    is available at http://www.egovernments.org
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    You should have received a copy of the GNU General Public License
    along with this program. If not, see http://www.gnu.org/licenses/ or
    http://www.gnu.org/licenses/gpl.html .
    In addition to the terms of the GPL license to be adhered to in using this
    program, the following additional terms are to be complied with:
        1) All versions of this program, verbatim or modified must carry this
           Legal Notice.
        2) Any misrepresentation of the origin of the material is prohibited. It
           is required that all modified versions of this material be marked in
           reasonable ways as different from the original version.
        3) This license does not grant any rights to any user of the program
           with regards to rights under trademark law for use of the trade names
           or trademarks of eGovernments Foundation.
  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.bpa.application.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.egov.infra.persistence.entity.AbstractAuditable;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "EGBPA_MSTR_CHECKLIST")
@SequenceGenerator(name = Checklist.SEQ_CHECKLIST, sequenceName = Checklist.SEQ_CHECKLIST, allocationSize = 1)
public class Checklist extends AbstractAuditable {

    private static final long serialVersionUID = 3078684328383202788L;
    public static final String SEQ_CHECKLIST = "SEQ_EGBPA_MSTR_CHECKLIST";

    @Id
    @GeneratedValue(generator = SEQ_CHECKLIST, strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "serviceType")
    private ServiceType serviceType;

    @NotNull
    @Length(min = 1, max = 128)
    private String checklistType;

    @NotNull
    @OneToMany(mappedBy = "checkList", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<CheckListDetail> checkListDetail = new ArrayList<>(0);

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(final ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public String getChecklistType() {
        return checklistType;
    }

    public void setChecklistType(final String checklistType) {
        this.checklistType = checklistType;
    }

    public List<CheckListDetail> getCheckListDetail() {
        return checkListDetail;
    }

}