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
package org.egov.pgr.service;

import org.egov.builder.entities.BoundaryBuilder;
import org.egov.builder.entities.DepartmentBuilder;
import org.egov.eis.entity.PositionBuilder;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.service.BoundaryService;
import org.egov.infra.exception.ApplicationValidationException;
import org.egov.pgr.entity.Complaint;
import org.egov.pgr.entity.ComplaintBuilder;
import org.egov.pgr.entity.ComplaintRouter;
import org.egov.pgr.entity.ComplaintRouterBuilder;
import org.egov.pgr.entity.ComplaintType;
import org.egov.pgr.entity.ComplaintTypeBuilder;
import org.egov.pgr.repository.ComplaintRouterRepository;
import org.egov.pims.commons.Position;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ComplaintRouterServiceTest {

    @InjectMocks
    private ComplaintRouterService complaintRouterService;

    @Mock
    private ComplaintRouterRepository complaintRouterRepository;

    @Mock
    private BoundaryService boundaryService;

    private Complaint complaint;

    private ComplaintType complaintType;

    private Boundary ward;

    private Position wardOfficer;

    private Boundary city;

    private Boundary zone;

    private Position healthInspector;

    private Position zonalOfficer;

    private Position grievanceOfficer;

    @Before
    public void before() {
        initMocks(this);
        setupRoutingMaster();
    }

    private void setupRoutingMaster() {
        final Department dept = new DepartmentBuilder().withDbDefaults().build();
        complaintType = new ComplaintTypeBuilder().withDepartment(dept).withName("test-ctype").build();
        city = new BoundaryBuilder().withDbDefaults().build();
        zone = new BoundaryBuilder().withDbDefaults().build();
        ward = new BoundaryBuilder().withDbDefaults().build();
        wardOfficer = new PositionBuilder().withId(1L).withName("WardOfficer").build();
        healthInspector = new PositionBuilder().withId(2L).withName("HO").build();
        zonalOfficer = new PositionBuilder().withId(3L).withName("ZonalOfficer").build();
        grievanceOfficer = new PositionBuilder().withId(4L).withName("Grievance Officer").build();

        final ComplaintRouter type_boundary_position = new ComplaintRouterBuilder().withComplaintType(complaintType)
                .withBoundary(ward).withPosition(wardOfficer).build();

        final ComplaintRouter type_position = new ComplaintRouterBuilder().withComplaintType(complaintType)
                .withPosition(healthInspector).build();

        final ComplaintRouter boundary_position = new ComplaintRouterBuilder().withBoundary(zone)
                .withPosition(zonalOfficer).build();

        when(complaintRouterRepository.findByComplaintTypeAndBoundary(complaintType, ward)).thenReturn(
                type_boundary_position);
        when(complaintRouterRepository.findByOnlyComplaintType(complaintType)).thenReturn(type_position);
        when(complaintRouterRepository.findByBoundary(zone)).thenReturn(boundary_position);
        when(complaintRouterRepository.findByOnlyBoundary(ward)).thenReturn(
                type_boundary_position);

    }

    @Test
    public void testGetAssigneeByTypeLocation() {

        complaint = new ComplaintBuilder().withComplaintType(complaintType).withLocation(ward).withDbDefaults().build();
        final Position assignee = complaintRouterService.getComplaintAssignee(complaint);
        assertEquals(wardOfficer, assignee);

    }

    @Test
    public void testGetAssigneeByType() {
        // this will create a new boundary which is not mapped
        complaint = new ComplaintBuilder().withComplaintType(complaintType).withDbDefaults().build();
        final Position assignee = complaintRouterService.getComplaintAssignee(complaint);
        assertEquals(healthInspector, assignee);

    }

    @Test
    public void testGetAssigneeByBoundary() {
        // this will create a new boundary which is not mapped
        complaint = new ComplaintBuilder().withComplaintType(complaintType).withLocation(zone).withDbDefaults().build();
        final Position assignee = complaintRouterService.getComplaintAssignee(complaint);
        assertEquals(healthInspector, assignee);

    }

    @Test(expected = ApplicationValidationException.class)
    public void testGetAssigneeByGoWithoutGoInsertion() {
        // this will create a new boundary which is not mapped
        complaintType = new ComplaintTypeBuilder().withDbDefaults().build();

        complaint = new ComplaintBuilder().withComplaintType(complaintType).withLocation(city).build();
        final Position assignee = complaintRouterService.getComplaintAssignee(complaint);
        assertNull(assignee);

    }

    @Test
    public void testGetAssigneeByGoAfterGoInsertion() {
        // this will create a new boundary which is mapped
        final ComplaintRouter GoPosition = new ComplaintRouterBuilder().withBoundary(city)
                .withPosition(grievanceOfficer).build();
        when(complaintRouterRepository.findGrievanceOfficer()).thenReturn(GoPosition);

        complaint = new ComplaintBuilder().withComplaintType(complaintType).withLocation(city).withDbDefaults().build();
        final Position assignee = complaintRouterService.getComplaintAssignee(complaint);
        assertEquals(healthInspector, assignee);
    }

}