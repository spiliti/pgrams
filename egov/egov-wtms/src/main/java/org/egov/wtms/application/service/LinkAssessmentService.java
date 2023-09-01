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

package org.egov.wtms.application.service;

import static org.egov.wtms.utils.constants.WaterTaxConstants.ACTIVE;
import static org.egov.wtms.utils.constants.WaterTaxConstants.ADDNLCONNECTION;
import static org.egov.wtms.utils.constants.WaterTaxConstants.INACTIVE;

import java.util.List;

import javax.validation.ValidationException;

import org.egov.ptis.domain.model.AssessmentDetails;
import org.egov.ptis.domain.model.enums.BasicPropertyStatus;
import org.egov.ptis.domain.service.property.PropertyExternalService;
import org.egov.wtms.application.entity.LinkedAssessment;
import org.egov.wtms.application.entity.WaterConnection;
import org.egov.wtms.application.entity.WaterConnectionDetails;
import org.egov.wtms.masters.entity.enums.ConnectionStatus;
import org.egov.wtms.masters.service.ApplicationTypeService;
import org.egov.wtms.utils.PropertyExtnUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LinkAssessmentService {

    @Autowired
    private WaterConnectionDetailsService waterConnectionDetailsService;

    @Autowired
    private ApplicationTypeService applicationTypeService;

    @Autowired
    private PropertyExtnUtils propertyExtnUtils;

    @Transactional
    public void linkActiveAssessmentToConnection(final LinkedAssessment linkedAssessment) {
        if (ACTIVE.equalsIgnoreCase(linkedAssessment.getPropertyAssessmentDetails().getStatus()))
            throw new ValidationException("err.assessment.no.active");
        if (INACTIVE.equalsIgnoreCase(linkedAssessment.getActiveAssessmentDetails().getStatus()))
            throw new ValidationException("err.assessment.no.inactive");
        final List<WaterConnectionDetails> waterconnectiondetailslist = waterConnectionDetailsService
                .getAllConnectionDetailsByPropertyID(
                        linkedAssessment.getPropertyAssessmentDetails().getAssessmentNumber());
        final List<WaterConnectionDetails> activeWaterConnectionDetailsList = waterConnectionDetailsService
                .getAllConnectionDetailsByPropertyID(
                        linkedAssessment.getActiveAssessmentDetails().getAssessmentNumber());
        WaterConnection parentConnection = null;
        if (!activeWaterConnectionDetailsList.isEmpty())
            for (final WaterConnectionDetails connectionDetails : activeWaterConnectionDetailsList)
                if (connectionDetails.getConnection().getParentConnection() == null)
                    parentConnection = connectionDetails.getConnection();
        if (waterconnectiondetailslist.isEmpty())
            throw new ValidationException("err.no.active.connections");
        else
            for (final WaterConnectionDetails connectionDetails : waterconnectiondetailslist) {
                final WaterConnectionDetails waterconnectionDetails = waterConnectionDetailsService
                        .findByConsumerCodeAndConnectionStatus(connectionDetails.getConnection().getConsumerCode(),
                                ConnectionStatus.ACTIVE);
                waterconnectionDetails.getConnection()
                        .setPropertyIdentifier(linkedAssessment.getActiveAssessmentDetails().getAssessmentNumber());
                if (!activeWaterConnectionDetailsList.isEmpty()) {
                    waterconnectionDetails.getConnection().setParentConnection(parentConnection);
                    waterconnectionDetails
                            .setApplicationType(applicationTypeService.findByCode(ADDNLCONNECTION));
                }
                waterConnectionDetailsService.save(waterconnectionDetails);
                final AssessmentDetails assessmentDetails = propertyExtnUtils.getAssessmentDetailsForFlag(
                        waterconnectionDetails.getConnection().getPropertyIdentifier(),
                        PropertyExternalService.FLAG_FULL_DETAILS, BasicPropertyStatus.ALL);
                waterConnectionDetailsService.createWaterChargeIndex(waterconnectionDetails, assessmentDetails,
                        waterConnectionDetailsService.getTotalAmount(waterconnectionDetails));

            }
    }

}
