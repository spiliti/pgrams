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

package org.egov.wtms.web.controller.search;

import org.egov.ptis.domain.model.AssessmentDetails;
import org.egov.ptis.domain.model.enums.BasicPropertyStatus;
import org.egov.ptis.domain.service.property.PropertyExternalService;
import org.egov.wtms.application.entity.WaterConnectionDetails;
import org.egov.wtms.application.service.WaterConnectionDetailsService;
import org.egov.wtms.entity.es.ConnectionSearchRequest;
import org.egov.wtms.masters.entity.enums.ConnectionStatus;
import org.egov.wtms.masters.entity.enums.ConnectionType;
import org.egov.wtms.utils.PropertyExtnUtils;
import org.egov.wtms.utils.WaterTaxUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.egov.wtms.utils.constants.WaterTaxConstants.ADDNLCONNECTION;
import static org.egov.wtms.utils.constants.WaterTaxConstants.APPLICATION_STATUS_CLOSERSANCTIONED;
import static org.egov.wtms.utils.constants.WaterTaxConstants.CHANGEOFUSE;
import static org.egov.wtms.utils.constants.WaterTaxConstants.CLOSECONNECTION;
import static org.egov.wtms.utils.constants.WaterTaxConstants.CLOSINGCONNECTION;
import static org.egov.wtms.utils.constants.WaterTaxConstants.CONNECTIONTYPE_METERED;
import static org.egov.wtms.utils.constants.WaterTaxConstants.DATAENTRYEDIT;
import static org.egov.wtms.utils.constants.WaterTaxConstants.EDITCOLLECTION;
import static org.egov.wtms.utils.constants.WaterTaxConstants.EDITDEMAND;
import static org.egov.wtms.utils.constants.WaterTaxConstants.GENERATEBILL;
import static org.egov.wtms.utils.constants.WaterTaxConstants.MIGRATED_CONNECTION;
import static org.egov.wtms.utils.constants.WaterTaxConstants.NEWCONNECTION;
import static org.egov.wtms.utils.constants.WaterTaxConstants.PERMENENTCLOSECODE;
import static org.egov.wtms.utils.constants.WaterTaxConstants.TEMPERARYCLOSECODE;
import static org.egov.wtms.utils.constants.WaterTaxConstants.RECONNECTION;
import static org.egov.wtms.utils.constants.WaterTaxConstants.SEARCH_MENUTREE_APPLICATIONTYPE_CLOSURE;
import static org.egov.wtms.utils.constants.WaterTaxConstants.SEARCH_MENUTREE_APPLICATIONTYPE_COLLECTTAX;
import static org.egov.wtms.utils.constants.WaterTaxConstants.SEARCH_MENUTREE_APPLICATIONTYPE_METERED;
import static org.egov.wtms.utils.constants.WaterTaxConstants.WATERCHARGES_CONSUMERCODE;

@Controller
@RequestMapping(value = "/search/waterSearch/")
public class CommonWaterTaxSearchController {

    private static final String COMMON_FORM_SEARCH = "waterTaxSearch-commonForm";
    private static final String INVALID_CONSUMERNUMBER = "invalid.consumernumber";
    private static final String APPLICATION_NUMBER = "applicationNo";
    private static final String MEESEVA_APPLICATION_NUMBER = "meesevaApplicationNumber";
    private static final String ERROR_MODE = "errorMode";
    private static final String MODE = "mode";
    private static final String APPLICATIONTYPE = "applicationType";
    private static final String ERR_MIGRATED_CONN = "err.migratedconnection.modify.notallowed";
    private static final String ERR_DATAENTRY_MODIFY = "err.modifynotallowed.collectiondone";
    private static final String COLLECTION_ALREADY_DONE = "invalid.collecttax";
    private static final String ERR_CLOSURE_NOT_ALLOWED= "err.application.inprogress";
    private static final String ERR_APPLY_FOR_RECONNECTION= "err.application.temporary.closed";
    private static final String ERR_APPLY_FOR_NEWCONNECTION= "err.application.permanent.closed";

    @Autowired
    private WaterConnectionDetailsService waterConnectionDetailsService;

    @Autowired
    private PropertyExtnUtils propertyExtnUtils;

    @Autowired
    private WaterTaxUtils waterTaxUtils;

    @Autowired
    private WaterConnectionDetailsService waterConnectionDtlsService;

    @ModelAttribute
    public ConnectionSearchRequest searchRequest() {
        return new ConnectionSearchRequest();
    }

    @GetMapping(value = "commonSearch/meterentry")
    public String addMeterEntry(Model model, HttpServletRequest request) {
        return commonSearchForm(model, SEARCH_MENUTREE_APPLICATIONTYPE_METERED, request.getParameter(APPLICATION_NUMBER));
    }

    @GetMapping(value = "commonSearch/closureconnection")
    public String closeWaterConnection(Model model, HttpServletRequest request) {
        return commonSearchForm(model, SEARCH_MENUTREE_APPLICATIONTYPE_CLOSURE, request.getParameter(APPLICATION_NUMBER));
    }

    @GetMapping(value = "commonSearch/changeofuse")
    public String waterConnectionChangeOfUsage(Model model, HttpServletRequest request) {
        return commonSearchForm(model, CHANGEOFUSE, request.getParameter(APPLICATION_NUMBER));
    }

    @GetMapping(value = "commonSearch/additionalconnection")
    public String getAdditionalWaterConnection(Model model, HttpServletRequest request) {
        return commonSearchForm(model, ADDNLCONNECTION, request.getParameter(APPLICATION_NUMBER));
    }

    @GetMapping(value = "commonSearch/collecttax")
    public String collectTax(Model model, HttpServletRequest request) {
        return commonSearchForm(model, SEARCH_MENUTREE_APPLICATIONTYPE_COLLECTTAX, request.getParameter(APPLICATION_NUMBER));
    }

    @GetMapping(value = "commonSearch/reconnection")
    public String getReconnectionForm(Model model, HttpServletRequest request) {
        return commonSearchForm(model, RECONNECTION, request.getParameter(APPLICATION_NUMBER));
    }

    @GetMapping(value = "commonSearch/editcollection")
    public String editCollection(Model model, HttpServletRequest request) {
        return commonSearchForm(model, EDITCOLLECTION, request.getParameter(APPLICATION_NUMBER));
    }

    @GetMapping(value = "commonSearch/dataentryedit")
    public String editDataEntry(Model model, HttpServletRequest request) {
        return commonSearchForm(model, DATAENTRYEDIT, request.getParameter(APPLICATION_NUMBER));
    }

    @GetMapping(value = "commonSearch/generatebill")
    public String generateBill(Model model, HttpServletRequest request) {
        return commonSearchForm(model, GENERATEBILL, request.getParameter(APPLICATION_NUMBER));
    }

    @GetMapping(value = "commonSearch/editdemand")
    public String editDemand(Model model, HttpServletRequest request) {
        return commonSearchForm(model, EDITDEMAND, request.getParameter(APPLICATION_NUMBER));
    }

    public String commonSearchForm(Model model, String applicationType, String meesevaApplicationNumber) {
        model.addAttribute(APPLICATIONTYPE, applicationType);
        model.addAttribute(MEESEVA_APPLICATION_NUMBER, meesevaApplicationNumber);
        return COMMON_FORM_SEARCH;
    }

    @PostMapping(value = "commonSearch-form/")
    public String searchConnectionSubmit(@ModelAttribute ConnectionSearchRequest searchRequest,
                                         BindingResult resultBinder, Model model, HttpServletRequest request) {
        WaterConnectionDetails waterConnectionDetails = null;
        String applicationType = request.getParameter(APPLICATIONTYPE);
        if (searchRequest.getMeesevaApplicationNumber() != null)
            model.addAttribute(MEESEVA_APPLICATION_NUMBER, searchRequest.getMeesevaApplicationNumber());
        if (isNotBlank(applicationType) && applicationType.equals(RECONNECTION))
            waterConnectionDetails = waterConnectionDetailsService.findByConsumerCodeAndConnectionStatus(
                    searchRequest.getConsumerCode(), ConnectionStatus.CLOSED);
        else
            waterConnectionDetails = waterConnectionDetailsService
                    .findByConsumerCodeAndConnectionStatus(searchRequest.getConsumerCode(), ConnectionStatus.INPROGRESS);

        if (waterConnectionDetails == null)
            waterConnectionDetails = waterConnectionDetailsService
                    .findByConsumerCodeAndConnectionStatus(searchRequest.getConsumerCode(), ConnectionStatus.ACTIVE);
        
        if (waterConnectionDetails == null)
            waterConnectionDetails = waterConnectionDetailsService
                    .findByConsumerCodeAndConnectionStatus(searchRequest.getConsumerCode(), ConnectionStatus.CLOSED);

        if (waterConnectionDetails == null) {
            resultBinder.rejectValue(WATERCHARGES_CONSUMERCODE, INVALID_CONSUMERNUMBER);
            model.addAttribute(APPLICATIONTYPE, applicationType);
            return COMMON_FORM_SEARCH;
        }

        if (isNotBlank(applicationType) && applicationType.equals(ADDNLCONNECTION))
            if (waterConnectionDetails.getCloseConnectionType() != null
                    && PERMENENTCLOSECODE.equals(waterConnectionDetails.getCloseConnectionType())) {
            	if(APPLICATION_STATUS_CLOSERSANCTIONED.equals(waterConnectionDetails.getStatus().getCode())){
                model.addAttribute(MODE, ERROR_MODE);
                model.addAttribute(APPLICATIONTYPE, applicationType);
                resultBinder.rejectValue(WATERCHARGES_CONSUMERCODE, ERR_APPLY_FOR_NEWCONNECTION,new String[] { waterConnectionDetails.getApplicationType().getName(),
                        waterConnectionDetails.getApplicationNumber() },ERR_APPLY_FOR_NEWCONNECTION);
                return COMMON_FORM_SEARCH;
            	}
            	else
            	{
                    model.addAttribute(MODE, ERROR_MODE);
                    model.addAttribute(APPLICATIONTYPE, applicationType);
                    resultBinder.rejectValue(WATERCHARGES_CONSUMERCODE, ERR_CLOSURE_NOT_ALLOWED,new String[] { waterConnectionDetails.getApplicationType().getName(),
                    waterConnectionDetails.getApplicationNumber() },ERR_CLOSURE_NOT_ALLOWED);
                    return COMMON_FORM_SEARCH;
                }	
            		
            } 
            else if (waterConnectionDetails.getCloseConnectionType() != null
                    && TEMPERARYCLOSECODE.equals(waterConnectionDetails.getCloseConnectionType())) {
            	if(APPLICATION_STATUS_CLOSERSANCTIONED.equals(waterConnectionDetails.getStatus().getCode())){
                model.addAttribute(MODE, ERROR_MODE);
                model.addAttribute(APPLICATIONTYPE, applicationType);
                resultBinder.rejectValue(WATERCHARGES_CONSUMERCODE, ERR_APPLY_FOR_RECONNECTION,new String[] { waterConnectionDetails.getApplicationType().getName(),
                        waterConnectionDetails.getApplicationNumber() },ERR_APPLY_FOR_RECONNECTION);
                return COMMON_FORM_SEARCH;
            	}
            	else
            	{
                    model.addAttribute(MODE, ERROR_MODE);
                    model.addAttribute(APPLICATIONTYPE, applicationType);
                    resultBinder.rejectValue(WATERCHARGES_CONSUMERCODE, ERR_CLOSURE_NOT_ALLOWED,new String[] { waterConnectionDetails.getApplicationType().getName(),
                    waterConnectionDetails.getApplicationNumber() },ERR_CLOSURE_NOT_ALLOWED);
                    return COMMON_FORM_SEARCH;
                }	
            }
            
            else if ((CHANGEOFUSE.equals(waterConnectionDetails.getApplicationType().getCode())
                    || NEWCONNECTION.equals(waterConnectionDetails.getApplicationType().getCode())
                    || RECONNECTION.equals(waterConnectionDetails.getApplicationType().getCode()))
                    && ConnectionStatus.ACTIVE.equals(waterConnectionDetails.getConnectionStatus())
                    && waterConnectionDetails.getConnection().getParentConnection() == null)
                return "redirect:/application/addconnection/" + waterConnectionDetails.getConnection().getConsumerCode();
            else {
                model.addAttribute(MODE, ERROR_MODE);
                model.addAttribute(APPLICATIONTYPE, applicationType);
                resultBinder.rejectValue(WATERCHARGES_CONSUMERCODE, INVALID_CONSUMERNUMBER);
                return COMMON_FORM_SEARCH;
            }
        if (isNotBlank(applicationType) && applicationType.equals(CHANGEOFUSE))
            if (waterConnectionDetails.getCloseConnectionType() != null
                    && PERMENENTCLOSECODE.equals(waterConnectionDetails.getCloseConnectionType())) {
            	if(APPLICATION_STATUS_CLOSERSANCTIONED.equals(waterConnectionDetails.getStatus().getCode())){
                model.addAttribute(APPLICATIONTYPE, applicationType);
                model.addAttribute(MODE, ERROR_MODE);
                resultBinder.rejectValue(WATERCHARGES_CONSUMERCODE, ERR_APPLY_FOR_NEWCONNECTION,new String[] { waterConnectionDetails.getApplicationType().getName(),
                        waterConnectionDetails.getApplicationNumber() },ERR_APPLY_FOR_NEWCONNECTION);
                return COMMON_FORM_SEARCH;
            	}
            	else
            	{
                    model.addAttribute(MODE, ERROR_MODE);
                    model.addAttribute(APPLICATIONTYPE, applicationType);
                    resultBinder.rejectValue(WATERCHARGES_CONSUMERCODE, ERR_CLOSURE_NOT_ALLOWED,new String[] { waterConnectionDetails.getApplicationType().getName(),
                    waterConnectionDetails.getApplicationNumber() },ERR_CLOSURE_NOT_ALLOWED);
                    return COMMON_FORM_SEARCH;
                }	
            }
                
             else if (waterConnectionDetails.getCloseConnectionType() != null
                        && TEMPERARYCLOSECODE.equals(waterConnectionDetails.getCloseConnectionType())) {
                  	if(APPLICATION_STATUS_CLOSERSANCTIONED.equals(waterConnectionDetails.getStatus().getCode())){
                    model.addAttribute(APPLICATIONTYPE, applicationType);
                    model.addAttribute(MODE, ERROR_MODE);
                    resultBinder.rejectValue(WATERCHARGES_CONSUMERCODE, ERR_APPLY_FOR_RECONNECTION,new String[] { waterConnectionDetails.getApplicationType().getName(),
                            waterConnectionDetails.getApplicationNumber() },ERR_APPLY_FOR_RECONNECTION);
                    return COMMON_FORM_SEARCH;
                  	}
                  	else
                  	{
                        model.addAttribute(MODE, ERROR_MODE);
                        model.addAttribute(APPLICATIONTYPE, applicationType);
                        resultBinder.rejectValue(WATERCHARGES_CONSUMERCODE, ERR_CLOSURE_NOT_ALLOWED,new String[] { waterConnectionDetails.getApplicationType().getName(),
                        waterConnectionDetails.getApplicationNumber() },ERR_CLOSURE_NOT_ALLOWED);
                        return COMMON_FORM_SEARCH;
                    }	
                }
             else if ((waterConnectionDetails.getApplicationType().getCode().equals(NEWCONNECTION)
                    || waterConnectionDetails.getApplicationType().getCode().equals(ADDNLCONNECTION)
                    || waterConnectionDetails.getApplicationType().getCode().equals(CHANGEOFUSE)
                    || RECONNECTION.equalsIgnoreCase(waterConnectionDetails.getApplicationType().getCode()))
                    && waterConnectionDetails.getConnectionStatus().equals(ConnectionStatus.ACTIVE))
                return "redirect:/application/changeOfUse/" + waterConnectionDetails.getConnection().getConsumerCode();
            else {
                model.addAttribute(APPLICATIONTYPE, applicationType);
                model.addAttribute(MODE, ERROR_MODE);
                resultBinder.rejectValue(WATERCHARGES_CONSUMERCODE, INVALID_CONSUMERNUMBER);
                return COMMON_FORM_SEARCH;
            }
        if (isNotBlank(applicationType) && applicationType.equals(SEARCH_MENUTREE_APPLICATIONTYPE_CLOSURE))
            
            
            
            if (isNotBlank(waterConnectionDetails.getCloseConnectionType())
                    && waterConnectionDetails.getCloseConnectionType().equals(PERMENENTCLOSECODE)) {
            	if(APPLICATION_STATUS_CLOSERSANCTIONED.equals(waterConnectionDetails.getStatus().getCode()))
            	{
                model.addAttribute(MODE, ERROR_MODE);
                model.addAttribute(APPLICATIONTYPE, applicationType);
                resultBinder.rejectValue(WATERCHARGES_CONSUMERCODE, ERR_APPLY_FOR_NEWCONNECTION, new String[] { waterConnectionDetails.getApplicationType().getName(),
                        waterConnectionDetails.getApplicationNumber() },ERR_APPLY_FOR_NEWCONNECTION);
                return COMMON_FORM_SEARCH;
            	}
            	else
            	{
                    model.addAttribute(MODE, ERROR_MODE);
                    model.addAttribute(APPLICATIONTYPE, applicationType);
                    resultBinder.rejectValue(WATERCHARGES_CONSUMERCODE, ERR_CLOSURE_NOT_ALLOWED,new String[] { waterConnectionDetails.getApplicationType().getName(),
                    waterConnectionDetails.getApplicationNumber() },ERR_CLOSURE_NOT_ALLOWED);
                    return COMMON_FORM_SEARCH;
                }	
            } else if ((waterConnectionDetails.getApplicationType().getCode().equals(NEWCONNECTION)
                    || waterConnectionDetails.getApplicationType().getCode().equals(ADDNLCONNECTION)
                    || waterConnectionDetails.getApplicationType().getCode().equals(CHANGEOFUSE)
                    || waterConnectionDetails.getApplicationType().getCode().equals(RECONNECTION))
                    && waterConnectionDetails.getConnectionStatus().equals(ConnectionStatus.ACTIVE))
                return "redirect:/application/close/" + waterConnectionDetails.getConnection().getConsumerCode();
            else 
            {
            	if(APPLICATION_STATUS_CLOSERSANCTIONED.equals(waterConnectionDetails.getStatus().getCode()))
            	{
            		model.addAttribute(MODE, ERROR_MODE);
                    resultBinder.rejectValue(WATERCHARGES_CONSUMERCODE, ERR_APPLY_FOR_RECONNECTION,
                    new String[] { waterConnectionDetails.getApplicationType().getName(),
                     waterConnectionDetails.getApplicationNumber() },ERR_APPLY_FOR_RECONNECTION);
                return COMMON_FORM_SEARCH;
            	}
            	else	
                model.addAttribute(MODE, ERROR_MODE);
                resultBinder.rejectValue(WATERCHARGES_CONSUMERCODE, ERR_CLOSURE_NOT_ALLOWED,
                        new String[] { waterConnectionDetails.getApplicationType().getName(),
                                waterConnectionDetails.getApplicationNumber() },
                        ERR_CLOSURE_NOT_ALLOWED);
                return COMMON_FORM_SEARCH;
              }
        if (isNotBlank(applicationType) && applicationType.equals(RECONNECTION))

            if (waterConnectionDetails.getCloseConnectionType() != null && waterConnectionDetails.getCloseConnectionType().equals(PERMENENTCLOSECODE)) {
            	if(APPLICATION_STATUS_CLOSERSANCTIONED.equals(waterConnectionDetails.getStatus().getCode())){

                model.addAttribute(MODE, ERROR_MODE);
                model.addAttribute(APPLICATIONTYPE, applicationType);
                resultBinder.rejectValue(WATERCHARGES_CONSUMERCODE, ERR_APPLY_FOR_NEWCONNECTION,new String[] { waterConnectionDetails.getApplicationType().getName(),
                        waterConnectionDetails.getApplicationNumber() },ERR_APPLY_FOR_NEWCONNECTION);
                return COMMON_FORM_SEARCH;
            	}
            	else
            	{
                    model.addAttribute(MODE, ERROR_MODE);
                    model.addAttribute(APPLICATIONTYPE, applicationType);
                    resultBinder.rejectValue(WATERCHARGES_CONSUMERCODE, ERR_CLOSURE_NOT_ALLOWED,new String[] { waterConnectionDetails.getApplicationType().getName(),
                    waterConnectionDetails.getApplicationNumber() },ERR_CLOSURE_NOT_ALLOWED);
                    return COMMON_FORM_SEARCH;
                }	
            		
            } else if (waterConnectionDetails.getApplicationType().getCode().equals(CLOSINGCONNECTION)
                    && waterConnectionDetails.getConnectionStatus().equals(ConnectionStatus.CLOSED)
                    && waterConnectionDetails.getStatus().getCode()
                    .equals(APPLICATION_STATUS_CLOSERSANCTIONED)
                    && waterConnectionDetails.getCloseConnectionType().equals("T"))
                return "redirect:/application/reconnection/" + waterConnectionDetails.getConnection().getConsumerCode();
            else {
                model.addAttribute(MODE, ERROR_MODE);
                model.addAttribute(APPLICATIONTYPE, applicationType);
                resultBinder.rejectValue(WATERCHARGES_CONSUMERCODE, ERR_CLOSURE_NOT_ALLOWED,new String[] { waterConnectionDetails.getApplicationType().getName(),
                        waterConnectionDetails.getApplicationNumber() },ERR_CLOSURE_NOT_ALLOWED);
                return COMMON_FORM_SEARCH;
                 }
        if (isNotBlank(applicationType) && applicationType.equals(SEARCH_MENUTREE_APPLICATIONTYPE_METERED))
            if ((waterConnectionDetails.getApplicationType().getCode().equals(NEWCONNECTION)
                    || waterConnectionDetails.getApplicationType().getCode().equals(ADDNLCONNECTION)
                    || waterConnectionDetails.getApplicationType().getCode().equals(CHANGEOFUSE)
                    || waterConnectionDetails.getApplicationType().getCode().equals(RECONNECTION))
                    && waterConnectionDetails.getConnectionStatus().equals(ConnectionStatus.ACTIVE)
                    && waterConnectionDetails.getConnectionType().name()
                    .equals(CONNECTIONTYPE_METERED))
                return "redirect:/application/meterentry/" + waterConnectionDetails.getConnection().getConsumerCode();
            else {
                model.addAttribute(MODE, ERROR_MODE);
                model.addAttribute(APPLICATIONTYPE, applicationType);
                resultBinder.rejectValue(WATERCHARGES_CONSUMERCODE, INVALID_CONSUMERNUMBER);
                return COMMON_FORM_SEARCH;
                 }
        if (isNotBlank(applicationType) && applicationType.equals(DATAENTRYEDIT))
            if ((waterConnectionDetails.getApplicationType().getCode().equals(NEWCONNECTION)
                    || waterConnectionDetails.getApplicationType().getCode().equals(ADDNLCONNECTION))
                    && waterConnectionDetails.getConnectionStatus().equals(ConnectionStatus.ACTIVE)
                    && waterConnectionDetails.getLegacy()) {
                WaterConnectionDetails connectionDetails = waterConnectionDtlsService
                        .findByApplicationNumberOrConsumerCode(waterConnectionDetails.getConnection().getConsumerCode());
                if (connectionDetails != null)
                    if (MIGRATED_CONNECTION.equalsIgnoreCase(connectionDetails.getConnectionReason())) {
                        model.addAttribute(MODE, ERROR_MODE);
                        model.addAttribute(APPLICATIONTYPE, applicationType);
                        resultBinder.rejectValue(WATERCHARGES_CONSUMERCODE, ERR_MIGRATED_CONN);
                        return COMMON_FORM_SEARCH;
                    } else {
                        BigDecimal demand = waterConnectionDetailsService.getTotalDemandTillCurrentFinYear(connectionDetails);
                        BigDecimal arrearBalance = waterConnectionDetailsService.getTotalAmount(connectionDetails);
                        if (demand.compareTo(arrearBalance) > 0) {
                            model.addAttribute(MODE, ERROR_MODE);
                            model.addAttribute(APPLICATIONTYPE, applicationType);
                            resultBinder.rejectValue(WATERCHARGES_CONSUMERCODE, ERR_DATAENTRY_MODIFY);
                            return COMMON_FORM_SEARCH;
                        }
                    }
                return "redirect:/application/newConnection-editExisting/" + waterConnectionDetails.getConnection().getConsumerCode();
            } else {
                model.addAttribute(MODE, ERROR_MODE);
                model.addAttribute(APPLICATIONTYPE, applicationType);
                resultBinder.rejectValue(WATERCHARGES_CONSUMERCODE, INVALID_CONSUMERNUMBER);
                return COMMON_FORM_SEARCH;
            }
        if (isNotBlank(applicationType) && applicationType.equals(SEARCH_MENUTREE_APPLICATIONTYPE_COLLECTTAX)) {
            BigDecimal amoutToBeCollected = ZERO;
            if (waterTaxUtils.getCurrentDemand(waterConnectionDetails).getDemand() != null)
                amoutToBeCollected = waterConnectionDetailsService.getTotalAmount(waterConnectionDetails);
            AssessmentDetails assessmentDetails = propertyExtnUtils.getAssessmentDetailsForFlag(
                    waterConnectionDetails.getConnection().getPropertyIdentifier(),
                    PropertyExternalService.FLAG_FULL_DETAILS, BasicPropertyStatus.ALL);
            if (assessmentDetails != null)
                if (amoutToBeCollected.doubleValue() == 0) {
                    model.addAttribute(MODE, ERROR_MODE);
                    model.addAttribute(APPLICATIONTYPE, applicationType);
                    resultBinder.rejectValue(WATERCHARGES_CONSUMERCODE, COLLECTION_ALREADY_DONE);
                    return COMMON_FORM_SEARCH;
                } else if (amoutToBeCollected.doubleValue() > 0
                        && (waterConnectionDetails.getConnectionType().equals(ConnectionType.METERED)
                        || waterConnectionDetails.getConnectionType().equals(ConnectionType.NON_METERED))
                        && !waterConnectionDetails.getApplicationType().getCode().equals(CLOSECONNECTION)
                        && !waterConnectionDetails.getConnectionStatus().equals(ConnectionStatus.INACTIVE))
                    return "redirect:/application/generatebill/" + waterConnectionDetails.getConnection().getConsumerCode();
                else {
                    model.addAttribute(MODE, ERROR_MODE);
                    model.addAttribute(APPLICATIONTYPE, applicationType);
                    resultBinder.rejectValue(WATERCHARGES_CONSUMERCODE, INVALID_CONSUMERNUMBER);
                    return COMMON_FORM_SEARCH;
                }

        }
        if (isNotBlank(applicationType) && applicationType.equals(EDITCOLLECTION))
            if ((waterConnectionDetails.getApplicationType().getCode().equals(NEWCONNECTION)
                    || waterConnectionDetails.getApplicationType().getCode().equals(ADDNLCONNECTION))
                    && waterConnectionDetails.getConnectionStatus().equals(ConnectionStatus.ACTIVE)
                    && waterConnectionDetails.getLegacy())
                return "redirect:/application/editCollection/"
                        + waterConnectionDetails.getConnection().getConsumerCode();
            else {
                model.addAttribute(MODE, ERROR_MODE);
                model.addAttribute(APPLICATIONTYPE, applicationType);
                resultBinder.rejectValue(WATERCHARGES_CONSUMERCODE, INVALID_CONSUMERNUMBER);
                return COMMON_FORM_SEARCH;
            }

        if (isNotBlank(applicationType) && applicationType.equals(GENERATEBILL))
            if (waterConnectionDetails.getApplicationType().getCode().equals(NEWCONNECTION)
                    || waterConnectionDetails.getApplicationType().getCode().equals(ADDNLCONNECTION)
                    || waterConnectionDetails.getApplicationType().getCode().equals(CHANGEOFUSE)
                    && waterConnectionDetails.getConnectionStatus().equals(ConnectionStatus.ACTIVE)
                    && waterConnectionDetails.getConnectionType().equals(ConnectionType.NON_METERED))
                return "redirect:/report/generateBillForHSCNo/" + waterConnectionDetails.getConnection().getConsumerCode();
            else {
                model.addAttribute(MODE, ERROR_MODE);
                model.addAttribute(APPLICATIONTYPE, applicationType);
                resultBinder.rejectValue(WATERCHARGES_CONSUMERCODE, INVALID_CONSUMERNUMBER);
                return COMMON_FORM_SEARCH;
            }
        if (isNotBlank(applicationType) && applicationType.equals(EDITDEMAND))
            if (waterConnectionDetails.getApplicationType().getCode().equals(NEWCONNECTION)
                    || waterConnectionDetails.getApplicationType().getCode().equals(ADDNLCONNECTION)
                    || waterConnectionDetails.getApplicationType().getCode().equals(CHANGEOFUSE)
                    && waterConnectionDetails.getConnectionStatus().equals(ConnectionStatus.ACTIVE)
                    && waterConnectionDetails.getLegacy())
                return "redirect:/application/editDemand/" + waterConnectionDetails.getConnection().getConsumerCode();
            else {
                model.addAttribute(MODE, ERROR_MODE);
                model.addAttribute(APPLICATIONTYPE, applicationType);
                resultBinder.rejectValue(WATERCHARGES_CONSUMERCODE, INVALID_CONSUMERNUMBER);
                return COMMON_FORM_SEARCH;
            }
        return "";

    }

}