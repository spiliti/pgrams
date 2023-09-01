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

package org.egov.tl.web.controller.transactions.legacy;

import org.egov.tl.entity.TradeLicense;
import org.egov.tl.service.LicenseAppTypeService;
import org.egov.tl.web.validator.legacy.CreateLegacyLicenseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.Date;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.egov.tl.utils.Constants.AUTO;
import static org.egov.tl.utils.Constants.RENEW_APPTYPE_CODE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
@RequestMapping("/legacylicense")
public class CreateLegacyLicenseController extends LegacyLicenseController {

    private static final String CREATE_LEGACY_LICENSE = "create-legacylicense";

    @Autowired
    private CreateLegacyLicenseValidator createLegacyLicenseValidator;

    @Autowired
    private LicenseAppTypeService licenseAppTypeService;

    @ModelAttribute("tradeLicense")
    public TradeLicense tradeLicense() {
        TradeLicense license = new TradeLicense();
        license.setLicenseAppType(licenseAppTypeService.getLicenseAppTypeByCode(RENEW_APPTYPE_CODE));
        license.setApplicationDate(new Date());
        license.setApplicationNumber(AUTO);
        return license;
    }

    @ModelAttribute("legacyFeePayStatus")
    public Map<Integer, Boolean> legacyFeePayStatus() {
        return legacyService.legacyFeePayStatusForCreate();
    }

    @ModelAttribute("legacyInstallmentwiseFees")
    public Map<Integer, Integer> legacyInstallmentwiseFees() {
        return legacyService.legacyInstallmentwiseFeesForCreate();
    }

    @GetMapping("/create")
    public String create() {
        return CREATE_LEGACY_LICENSE;
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute TradeLicense tradeLicense, BindingResult binding, Model model) {
        createLegacyLicenseValidator.validate(tradeLicense, binding);
        if (binding.hasErrors()) {
            model.addAttribute("legacyInstallmentwiseFees", legacyService.legacyInstallmentfee(tradeLicense));
            model.addAttribute("legacyFeePayStatus", legacyService.legacyFeeStatus(tradeLicense));
            return CREATE_LEGACY_LICENSE;
        }
        legacyService.createLegacy(tradeLicense);
        return "redirect:/legacylicense/view/" + tradeLicense.getApplicationNumber();
    }

    @GetMapping(value = "/old-licenseno-is-unique", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Boolean checkOldLicenseNumber(@RequestParam String oldLicenseNumber) {
        return isNotBlank(oldLicenseNumber) && legacyService.getLicenseByOldLicenseNumber(oldLicenseNumber) != null;
    }
}
