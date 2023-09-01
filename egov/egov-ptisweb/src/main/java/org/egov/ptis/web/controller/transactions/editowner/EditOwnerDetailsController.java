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

package org.egov.ptis.web.controller.transactions.editowner;

import static org.egov.ptis.constants.PropertyTaxConstants.DOORNO_EDIT_MODE;
import static org.egov.ptis.constants.PropertyTaxConstants.MOBILENO_EDIT_MODE;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.egov.infra.persistence.entity.Address;
import org.egov.infra.persistence.entity.enums.Gender;
import org.egov.infra.persistence.entity.enums.GuardianRelation;
import org.egov.ptis.bean.PropertyOwner;
import org.egov.ptis.domain.dao.property.BasicPropertyDAO;
import org.egov.ptis.domain.entity.property.BasicProperty;
import org.egov.ptis.domain.entity.property.OwnerAudit;
import org.egov.ptis.domain.entity.property.PropertyImpl;
import org.egov.ptis.domain.entity.property.PropertyOwnerInfo;
import org.egov.ptis.domain.service.property.OwnerAuditService;
import org.egov.ptis.domain.service.property.PropertyPersistenceService;
import org.egov.ptis.domain.service.property.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/editowner/{assessmentNo}")
public class EditOwnerDetailsController {

    private static final String SUCCESS_MESSAGE = "successMessage";
    protected static final String OWNERDETAILS_FROM = "ownerdetails-form";
    protected static final String OWNERDETAILS_SUCCESS = "ownerdetails-success";
    private static final String ERROR_MSG = "errorMsg";

    @Autowired
    private BasicPropertyDAO basicPropertyDAO;

    @Autowired
    private PropertyPersistenceService basicPropertyService;

    @Autowired
    private OwnerAuditService ownerAuditService;

    @Autowired
    private PropertyService propertyService;

    @ModelAttribute
    public PropertyOwner getPropertyOwner(@PathVariable final String assessmentNo) {
        final PropertyOwner propertyOwner = new PropertyOwner();
        final BasicProperty basicProperty = basicPropertyDAO.getBasicPropertyByPropertyID(assessmentNo);
        PropertyImpl property;
        if (null != basicProperty) {
            property = (PropertyImpl) basicProperty.getProperty();
            propertyOwner.setProperty(property);
            propertyOwner.setPropertyOwnerInfo(basicProperty.getPropertyOwnerInfo());
        }
        return propertyOwner;
    }

    @GetMapping
    public String newForm(@ModelAttribute final PropertyOwner propertyOwner, final Model model,
            @PathVariable final String assessmentNo, @RequestParam final String mode) {
        final BasicProperty basicProperty = basicPropertyDAO.getBasicPropertyByPropertyID(assessmentNo);
        List<OwnerAudit> ownerAuditList;
        String pageTitle = setPageTitle(mode);
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("guardianRelations", Arrays.asList(GuardianRelation.values()));
        model.addAttribute("gender", Gender.values());
        final Address address = basicProperty.getAddress();
        model.addAttribute("mode", mode);
        model.addAttribute("doorNumber", address.getHouseNoBldgApt());
        model.addAttribute("existingDoorNumber", address.getHouseNoBldgApt());
        model.addAttribute("pinCode", address.getPinCode());
        ownerAuditList = ownerAuditService.setOwnerAuditDetails(basicProperty);
        propertyOwner.setOwnerAudit(ownerAuditList);
        model.addAttribute("propertyOwner", propertyOwner);
        model.addAttribute(ERROR_MSG, "");
        return OWNERDETAILS_FROM;
    }

    @PostMapping
    public String updateOwnerDetails(@ModelAttribute final PropertyOwner propertyOwner,
            final RedirectAttributes redirectAttrs, final BindingResult errors, final Model model,
            final HttpServletRequest request, @RequestParam final String doorNumber, @RequestParam final String mode) {
        String errMsg;
        String pageTitle = setPageTitle(mode);
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("doorNumber", doorNumber);
        model.addAttribute("guardianRelations", Arrays.asList(GuardianRelation.values()));
        for (final PropertyOwnerInfo ownerInfo : propertyOwner.getPropertyOwnerInfo())
            for (final Address address : ownerInfo.getOwner().getAddress())
                model.addAttribute("existingDoorNumber", address.getHouseNoBldgApt());
        if (!StringUtils.isBlank(doorNumber) && mode.equalsIgnoreCase(DOORNO_EDIT_MODE)
                && propertyService.isDuplicateDoorNumber(doorNumber, propertyOwner.getProperty().getBasicProperty()))
            errMsg = "error.accept";
        else
            errMsg = basicPropertyService.updateOwners(propertyOwner.getProperty(),
                    propertyOwner.getProperty().getBasicProperty(), doorNumber, errors);
        if (!errMsg.isEmpty()) {
            model.addAttribute(ERROR_MSG, errMsg);
            model.addAttribute("mode", mode);
            return OWNERDETAILS_FROM;
        } else {
            ownerAuditService.saveOwnerDetails(propertyOwner.getOwnerAudit());
            if (mode.equals(DOORNO_EDIT_MODE))
                model.addAttribute(SUCCESS_MESSAGE, "Door Number Updated Successfully!");
            else if (mode.equals(MOBILENO_EDIT_MODE))
                model.addAttribute(SUCCESS_MESSAGE, "Mobile Number Updated Successfully!");
            else
                model.addAttribute(SUCCESS_MESSAGE, "Owner Details Updated Successfully!");
            return OWNERDETAILS_SUCCESS;
        }
    }

    private String setPageTitle(final String mode) {
        String pageTitle;
        if (mode.equals(DOORNO_EDIT_MODE))
            pageTitle = "Edit Door Number";
        else if (mode.equals(MOBILENO_EDIT_MODE))
            pageTitle = "Edit Mobile Number";
        else
            pageTitle = "Edit Owner Details";
        return pageTitle;
    }

}
