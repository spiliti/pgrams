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

package org.egov.ptis.web.controller.reports;

import static org.egov.infra.utils.JsonUtils.toJSON;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.service.BoundaryService;
import org.egov.ptis.actions.common.CommonServices;
import org.egov.ptis.constants.PropertyTaxConstants;
import org.egov.ptis.domain.entity.property.Apartment;
import org.egov.ptis.domain.service.report.ReportService;
import org.egov.ptis.master.service.ApartmentService;
import org.egov.ptis.report.bean.ApartmentDCBReportHelperAdaptor;
import org.egov.ptis.report.bean.ApartmentDCBReportResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/report/apartmentdcbreport")
public class ApartmentDCBReportController {

    @Autowired
    private BoundaryService boundaryService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private ApartmentService apartmentService;
    
    @ModelAttribute("wards")
    public Map<Long, String> wardBoundaries() {
        final List<Boundary> wardList = boundaryService.getActiveBoundariesByBndryTypeNameAndHierarchyTypeName(
                PropertyTaxConstants.WARD,
                PropertyTaxConstants.REVENUE_HIERARCHY_TYPE);
        return CommonServices.getFormattedBndryMap(wardList);
    }
    
    @ModelAttribute("apartmenttype")
	public Map<Long, String> listApartment() {
    	final List<Apartment> wardList =  apartmentService.getAllApartments();
         return CommonServices.getFormattedApartmentMap(wardList);
	}

    @RequestMapping(method = RequestMethod.GET)
    public String searchDcbForm(final Model model) {
        model.addAttribute("ApartmentDCBReport", new ApartmentDCBReportResult());
        model.addAttribute("mode", "apartment");
        return "apartmentdcb-form";
    }

    @RequestMapping(value = "/result", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody void dcbReportSearchResult(@RequestParam final String boundaryId,
            @RequestParam final String mode, @RequestParam final String apartmentId,
            final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {
    	final List<ApartmentDCBReportResult> resultList = reportService.prepareQueryForApartmentDCBReport(Long.valueOf(boundaryId), mode, Long.valueOf(apartmentId));
        final String result = new StringBuilder("{ \"data\":").append(toJSON(resultList, ApartmentDCBReportResult.class,
                ApartmentDCBReportHelperAdaptor.class)).append("}").toString();
        IOUtils.write(result, response.getWriter());
    }
}
