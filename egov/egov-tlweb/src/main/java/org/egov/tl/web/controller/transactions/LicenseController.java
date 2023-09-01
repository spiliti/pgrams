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
package org.egov.tl.web.controller.transactions;

import org.egov.tl.entity.LicenseDocument;
import org.egov.tl.entity.TradeLicense;
import org.egov.tl.service.TradeLicenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

import static org.egov.infra.reporting.util.ReportUtil.reportAsResponseEntity;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;

@Controller
@RequestMapping(value = "/license")
public class LicenseController {

    @Autowired
    @Qualifier("tradeLicenseService")
    private TradeLicenseService tradeLicenseService;

    @GetMapping(value = "/document/{licenseId}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Map<String, List<LicenseDocument>>> getAttachedDocument(@PathVariable Long licenseId) {
        return tradeLicenseService.getAttachedDocument(licenseId);
    }

    @GetMapping(value = "/acknowledgement/{uid}", produces = APPLICATION_PDF_VALUE)
    @ResponseBody
    public ResponseEntity<InputStreamResource> acknowledgment(@PathVariable String uid) {
        return reportAsResponseEntity(tradeLicenseService.generateAcknowledgment(uid));
    }

    @GetMapping(value = "/generate-provisionalcertificate/{uid}", produces = APPLICATION_PDF_VALUE)
    @ResponseBody
    public ResponseEntity<InputStreamResource> generateProvisionalCertificate(@PathVariable String uid) {
        TradeLicense tradeLicense = tradeLicenseService.getLicenseByUID(uid);
        return reportAsResponseEntity(tradeLicenseService.generateLicenseCertificate(tradeLicense, true));
    }

    @GetMapping("success/{licenseId}")
    public String successView(@PathVariable Long licenseId, Model model) {
        model.addAttribute("tradeLicense", tradeLicenseService.getLicenseById(licenseId));
        return "license-success-view";
    }

    @GetMapping("view/{licenseId}")
    public String licenseView(@PathVariable Long licenseId, Model model) {
        TradeLicense license = tradeLicenseService.getLicenseById(licenseId);
        return tradeLicenseView(model, license);
    }

    @GetMapping("show/{uid}")
    public String licenseView(@PathVariable String uid, Model model) {
        TradeLicense license = tradeLicenseService.getLicenseByUID(uid);
        return tradeLicenseView(model, license);
    }

    private String tradeLicenseView(Model model, TradeLicense license) {
        if (license == null) {
            model.addAttribute("message", "msg.license.notfound");
        } else {
            model.addAttribute("outstandingFee", tradeLicenseService.getOutstandingFee(license));
            model.addAttribute("licenseHistory", tradeLicenseService.populateHistory(license));
            model.addAttribute("tradeLicense", license);
        }
        return "view-tradelicense";
    }
}
