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
package org.egov.pgr.service;

import org.egov.pgr.entity.ComplaintType;
import org.egov.pgr.entity.EscalationHierarchy;
import org.egov.pgr.repository.EscalationHierarchyRepository;
import org.egov.pims.commons.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EscalationHierarchyService {

    @Autowired
    private EscalationHierarchyRepository escalationHierarchyRepository;

    @Transactional
    public void save(final EscalationHierarchy escalationHierarchy) {
        escalationHierarchyRepository.save(escalationHierarchy);
    }

    public EscalationHierarchy getHierarchyByFromPosition(final Long posId) {
        return escalationHierarchyRepository.findByFromPositionId(posId);
    }

    public EscalationHierarchy getHierarchyByFromPositionAndGrievanceType(final Long posId, final Long grievanceTypeId) {
        return escalationHierarchyRepository.findByFromPositionIdAndGrievanceTypeId(posId, grievanceTypeId);
    }

    public void deleteAllInBatch(final List<EscalationHierarchy> existingPosHierarchy) {
        escalationHierarchyRepository.deleteInBatch(existingPosHierarchy);

    }

    public List<EscalationHierarchy> getHeirarchyByFromPosition(Long fromPositionId) {
        return escalationHierarchyRepository.findByFromPositionIdOrderByGrievanceTypeId(fromPositionId);
    }

    public List<EscalationHierarchy> getHeirarchiesByFromPositionAndGrievanceType(Long fromPositionId, Long grievanceTypeId) {
        if (fromPositionId > 0 && grievanceTypeId > 0) {
            List<EscalationHierarchy> escalationHierarchy = new ArrayList<>();
            EscalationHierarchy hierarchy = escalationHierarchyRepository.findByFromPositionIdAndGrievanceTypeId(fromPositionId, grievanceTypeId);
            if (hierarchy != null)
                escalationHierarchy.add(hierarchy);
            return escalationHierarchy;
        } else if (grievanceTypeId > 0)
            return escalationHierarchyRepository.findByGrievanceTypeId(grievanceTypeId);
        else if (fromPositionId > 0)
            return escalationHierarchyRepository.findByFromPositionIdOrderByGrievanceTypeId(fromPositionId);
        else
            return escalationHierarchyRepository.findAll();
    }

    public List<EscalationHierarchy> getHeirarchiesByFromPositionAndGrievanceTypes(final List<ComplaintType> grievanceTypes,
                                                                                   final Position fromPositionId) {
        return escalationHierarchyRepository.findByGrievanceTypeInAndFromPosition(grievanceTypes, fromPositionId);
    }

}
