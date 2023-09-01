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
package org.egov.wtms.web.controller.application;

import static org.egov.wtms.masters.entity.enums.ConnectionType.METERED;
import static org.egov.wtms.utils.constants.WaterTaxConstants.METERED_DMDRSN_CODE_MAP;
import static org.egov.wtms.utils.constants.WaterTaxConstants.NON_METERED_DMDRSN_CODE_MAP;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.egov.commons.Installment;
import org.egov.demand.model.EgDemandDetails;
import org.egov.demand.model.EgDemandReason;
import org.egov.wtms.application.entity.DemandDetail;
import org.egov.wtms.application.entity.WaterConnectionDetails;
import org.egov.wtms.application.repository.WaterConnectionDetailsRepository;
import org.egov.wtms.application.service.ConnectionDemandService;
import org.egov.wtms.application.service.WaterConnectionDetailsService;
import org.egov.wtms.masters.entity.enums.ConnectionStatus;
import org.egov.wtms.utils.WaterTaxUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/application")
public class EditDemandForDataEntryController {

    @Autowired
    private WaterConnectionDetailsService waterConnectionDetailsService;
    private final ConnectionDemandService connectionDemandService;
    @Autowired
    private WaterTaxUtils waterTaxUtils;

    @Autowired
    public EditDemandForDataEntryController(final WaterConnectionDetailsRepository waterConnectionDetailsRepository,
            final ConnectionDemandService connectionDemandService) {
        this.connectionDemandService = connectionDemandService;
    }

    @ModelAttribute
    public WaterConnectionDetails getWaterConnectionDetails(@PathVariable final String consumerCode) {
        WaterConnectionDetails waterConnectionDetails = null;
        waterConnectionDetails = waterConnectionDetailsService
                .findByConsumerCodeAndConnectionStatus(consumerCode, ConnectionStatus.ACTIVE);
        if (waterConnectionDetails == null)
            waterConnectionDetails = waterConnectionDetailsService.findByConsumerCodeAndConnectionStatus(consumerCode,
                    ConnectionStatus.INPROGRESS);
        return waterConnectionDetails;
    }

    @RequestMapping(value = "/editDemand/{consumerCode}", method = GET)
    public String newForm(final Model model, @PathVariable final String consumerCode,
            @ModelAttribute final WaterConnectionDetails waterConnectionDetails, final HttpServletRequest request) {
        if (waterConnectionDetails == null && consumerCode != null) {
            final WaterConnectionDetails connectionDetails = waterConnectionDetailsService
                    .findByConsumerCodeAndConnectionStatus(consumerCode, ConnectionStatus.INPROGRESS);
            if (connectionDetails != null)
                return loadViewData(model, connectionDetails);
        }
        return loadViewData(model, waterConnectionDetails);

    }

    private String loadViewData(final Model model, final WaterConnectionDetails waterConnectionDetails) {
        final List<DemandDetail> demandDetailBeanList = new ArrayList<>();
        final Set<DemandDetail> tempDemandDetail = new LinkedHashSet<>();
        List<Installment> allInstallments;
        Set<Map.Entry<String, String>> demandReasonMap;
        if (waterConnectionDetails.getConnectionType().equals(METERED)) {
            allInstallments = waterTaxUtils
                    .getMonthlyInstallments(waterConnectionDetails.getExecutionDate());
            demandReasonMap = METERED_DMDRSN_CODE_MAP.entrySet();
        } else {
            allInstallments = waterTaxUtils.getInstallmentsForCurrYear(waterConnectionDetails.getExecutionDate());
            demandReasonMap = NON_METERED_DMDRSN_CODE_MAP.entrySet();
        }
        Collections.sort(allInstallments);
        DemandDetail dmdDtl = null;
        for (final Map.Entry<String, String> entry : demandReasonMap)
            for (final Installment installObj : allInstallments) {
                final EgDemandReason demandReasonObj = connectionDemandService
                        .getDemandReasonByCodeAndInstallment(entry.getKey(), installObj);
                if (demandReasonObj != null) {
                    EgDemandDetails demanddet = null;
                    if (waterTaxUtils.getCurrentDemand(waterConnectionDetails).getDemand() != null)
                        demanddet = getDemandDetailsExist(waterConnectionDetails, demandReasonObj);
                    if (demanddet != null)
                        dmdDtl = createDemandDetailBean(installObj, demandReasonObj.getEgDemandReasonMaster().getCode(),
                                entry.getValue(), demanddet.getAmount(), demanddet.getAmtCollected(), demanddet.getId());
                    else
                        dmdDtl = createDemandDetailBean(installObj, entry.getKey(), entry.getValue(), BigDecimal.ZERO,
                                BigDecimal.ZERO, null);

                }
                tempDemandDetail.add(dmdDtl);
            }
        for (final DemandDetail demandDetList : tempDemandDetail)
            if (demandDetList != null)
                demandDetailBeanList.add(demandDetList);
        final BigDecimal waterTaxDueforParent = waterConnectionDetailsService.getTotalAmount(waterConnectionDetails);
        model.addAttribute("mode", "editdemand");
        model.addAttribute("waterTaxDueforParent", waterTaxDueforParent);
        model.addAttribute("demandDetailBeanList", demandDetailBeanList);
        model.addAttribute("waterConnectionDetails", waterConnectionDetails);
        model.addAttribute("current1HalfInstallment", !demandDetailBeanList.isEmpty() && demandDetailBeanList.size() > 1
                ? demandDetailBeanList.get(demandDetailBeanList.size() - 2).getInstallment() : null);
        model.addAttribute("current2HalfInstallment", !demandDetailBeanList.isEmpty()
                ? demandDetailBeanList.get(demandDetailBeanList.size() - 1).getInstallment() : null);
        model.addAttribute("connectionType", waterConnectionDetailsService.getConnectionTypesMap()
                .get(waterConnectionDetails.getConnectionType().name()));
        return "editDemandDateEntry-newForm";
    }

    private EgDemandDetails getDemandDetailsExist(final WaterConnectionDetails waterConnectionDetails,
            final EgDemandReason demandReasonObj) {
        EgDemandDetails demandDet = null;
        for (final EgDemandDetails dd : waterTaxUtils.getCurrentDemand(waterConnectionDetails).getDemand()
                .getEgDemandDetails())
            if (dd.getEgDemandReason().equals(demandReasonObj)) {
                demandDet = dd;
                break;
            }
        return demandDet;

    }

    private DemandDetail createDemandDetailBean(final Installment installment, final String reasonMaster,
            final String reasonMasterDesc, final BigDecimal amount, final BigDecimal amountCollected,
            final Long demanddetailId) {
        final DemandDetail demandDetail = new DemandDetail();
        demandDetail.setInstallment(installment.getDescription());
        demandDetail.setReasonMaster(reasonMaster);
        demandDetail.setId(demanddetailId);
        demandDetail.setActualAmount(amount);
        demandDetail.setActualCollection(amountCollected);
        demandDetail.setReasonMasterDesc(reasonMasterDesc);
        return demandDetail;
    }

    @RequestMapping(value = "/editDemand/{consumerCode}", method = RequestMethod.POST)
    public String updateMeterEntry(@ModelAttribute final WaterConnectionDetails waterConnectionDetails,
            final BindingResult errors, final RedirectAttributes redirectAttrs, final Model model,
            final HttpServletRequest request) {
        final String sourceChannel = request.getParameter("Source");
        final WaterConnectionDetails connectionDetails = connectionDemandService
                .updateDemandForNonMeteredConnectionDataEntry(waterConnectionDetails, sourceChannel);
        model.addAttribute("waterConnectionDetails", connectionDetails);
        return "editDemand-dataEntryAck";
    }

}