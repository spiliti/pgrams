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
package org.egov.wtms.web.controller.reports;

import org.apache.commons.io.IOUtils;
import org.egov.demand.dao.EgDemandDao;
import org.egov.demand.model.EgDemandDetails;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.service.BoundaryService;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.wtms.application.entity.DefaultersReport;
import org.egov.wtms.application.service.ConnectionDemandService;
import org.egov.wtms.application.service.DefaultersWTReportService;
import org.egov.wtms.application.service.WaterConnectionDetailsService;
import org.egov.wtms.reports.entity.DefaultersReportAdaptor;
import org.egov.wtms.utils.DemandComparatorByInstallmentOrder;
import org.egov.wtms.utils.constants.WaterTaxConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.egov.infra.utils.JsonUtils.toJSON;
import static org.egov.ptis.constants.PropertyTaxConstants.REVENUE_HIERARCHY_TYPE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/report/defaultersWTReport/search")
public class DefaultersWTReportController {

    @Autowired
    private BoundaryService boundaryService;

    @Autowired
    private DefaultersWTReportService defaultersWTReportService;

    @Autowired
    public WaterConnectionDetailsService waterConnectionDetailsService;

    @Autowired
    public ConnectionDemandService connectionDemandService;

    @Autowired
    private EgDemandDao egDemandDao;

    @RequestMapping(method = GET)
    public String search(final Model model) {
        model.addAttribute("currentDate", new Date());
        return "defaultersWTReport-search";
    }

    @ModelAttribute
    public DefaultersReport reportModel() {
        return new DefaultersReport();
    }

    @ModelAttribute("revenueWards")
    public List<Boundary> revenueWardList() {
        return boundaryService.getActiveBoundariesByBndryTypeNameAndHierarchyTypeName(WaterTaxConstants.REVENUE_WARD,
                REVENUE_HIERARCHY_TYPE);
    }

    @ModelAttribute("topDefaultersList")
    public List<Integer> defaultersList() {
        final List<Integer> topdefaultersList = new ArrayList<>();
        topdefaultersList.add(10);
        topdefaultersList.add(50);
        topdefaultersList.add(100);
        topdefaultersList.add(500);
        topdefaultersList.add(1000);
        return topdefaultersList;
    }

    @RequestMapping(value = "/result", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void searchResult(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {
        String ward = "";
        String topDefaulters = "";
        String fromAmount = "";
        String toAmount = "";
        try {
            if (null != request.getParameter("ward"))
                ward = request.getParameter("ward");
            if (null != request.getParameter("topDefaulters"))
                topDefaulters = request.getParameter("topDefaulters");
            if (null != request.getParameter("fromAmount"))
                fromAmount = request.getParameter("fromAmount");
            if (null != request.getParameter("toAmount"))
                toAmount = request.getParameter("toAmount");
            final List<DefaultersReport> defaultersreportlist = defaultersWTReportService.getDefaultersReportDetails(fromAmount,
                    toAmount, ward,
                    topDefaulters, Integer.valueOf(request.getParameter("start")),
                    Integer.valueOf(request.getParameter("length")));
            long foundRows;
            if (null != request.getParameter("topDefaulters"))
                foundRows = defaultersWTReportService.getTotalCountFromLimit(fromAmount, toAmount, ward, topDefaulters);
            else
                foundRows = defaultersWTReportService.getTotalCount(fromAmount, toAmount, ward);
            String result;
            int count = Integer.valueOf(request.getParameter("start"));
            for (final DefaultersReport dd : defaultersreportlist) {
                count = count + 1;
                dd.setDuePeriodFrom(getDuePeriodFrom(dd.getDemandId()));
                dd.setSlNo(count);
            }
            result = new StringBuilder("{ \"draw\":").append(request.getParameter("draw")).append(", \"recordsTotal\":")
                    .append(foundRows).append(", \"recordsFiltered\":").append(foundRows).append(", \"data\":")
                    .append(toJSON(defaultersreportlist, DefaultersReport.class, DefaultersReportAdaptor.class)).append("}")
                    .toString();
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            IOUtils.write(result, response.getWriter());
        } catch (final ParseException e) {
            throw new ApplicationRuntimeException("Exception while getting defaulters report list " + e);
        }
    }

    public String getDuePeriodFrom(final BigInteger demandId) {
        final List<EgDemandDetails> demandDetList = new ArrayList<>(
                egDemandDao.findById(demandId.longValue(), false).getEgDemandDetails());
        final List<EgDemandDetails> demandDetFinalList = new ArrayList<>();

        for (final EgDemandDetails egDemandTemp : demandDetList)
            if (!egDemandTemp.getAmount().equals(egDemandTemp.getAmtCollected()))
                demandDetFinalList.addAll(egDemandTemp.getEgDemand().getEgDemandDetails());

        if (demandDetFinalList.isEmpty())
            return "";
        else {
            Collections.sort(demandDetFinalList, new DemandComparatorByInstallmentOrder());
            return demandDetFinalList.get(0).getEgDemandReason().getEgInstallmentMaster().getDescription();
        }

    }
}