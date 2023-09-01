/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
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
 */
package org.egov.bpa.web.controller.application;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.ArrayUtils;
import org.egov.bpa.application.entity.ApplicationDocument;
import org.egov.bpa.application.entity.ApplicationStakeHolder;
import org.egov.bpa.application.entity.BpaApplication;
import org.egov.bpa.application.service.ApplicationBpaService;
import org.egov.bpa.application.service.collection.GenericBillGeneratorService;
import org.egov.bpa.service.BpaUtils;
import org.egov.bpa.utils.BpaConstants;
import org.egov.infra.workflow.matrix.entity.WorkFlowMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/application")
public class NewApplicationController extends BpaGenericApplicationController {

    @Autowired
    private GenericBillGeneratorService genericBillGeneratorService;
    @Autowired
    private BpaUtils bpaUtils;
    @Autowired
    private ApplicationBpaService applicationBpaService;
 

    @RequestMapping(value = "/newApplication-newform", method = GET)
    public String showNewApplicationForm(@ModelAttribute final BpaApplication bpaApplication,
            final Model model, final HttpServletRequest request) {
        bpaApplication.setApplicationDate(new Date());
        model.addAttribute("mode", "new");
        return "newapplication-form";
    }

    @RequestMapping(value = "/newApplication-create", method = POST)
    public String createNewConnection(@Valid @ModelAttribute final BpaApplication bpaApplication,
            final BindingResult resultBinder, final RedirectAttributes redirectAttributes,
            final HttpServletRequest request, final Model model,
            final BindingResult errors) {

        final List<ApplicationDocument> applicationDocs = new ArrayList<>(0);
        int i = 0;
        if (!bpaApplication.getApplicationDocument().isEmpty())
            for (final ApplicationDocument applicationDocument : bpaApplication.getApplicationDocument()) {
                validateDocuments(applicationDocs, applicationDocument, i, resultBinder);
                i++;
            }
        Long userPosition = null;
        final WorkFlowMatrix wfmatrix = bpaUtils.getWfMatrixByCurrentState(bpaApplication, BpaConstants.WF_NEW_STATE);
        if (wfmatrix != null)
            userPosition = bpaUtils.getUserPositionByZone(wfmatrix.getNextDesignation(), bpaApplication.getWardId() != null
                    ? bpaApplication.getWardId() : bpaApplication.getZoneId() != null ? bpaApplication.getZoneId() : null);
        if (userPosition == null) {
            model.addAttribute("noJAORSAMessage", "No Superintendant exists to forward the application.");
            return "newapplication-form";
        }
        List<ApplicationStakeHolder> applicationStakeHolders = new ArrayList<>();
        ApplicationStakeHolder applicationStakeHolder= new ApplicationStakeHolder();
        applicationStakeHolder.setApplication(bpaApplication);
        applicationStakeHolder.setStakeHolder(bpaApplication.getStakeHolder().get(0).getStakeHolder());
        applicationStakeHolders.add(applicationStakeHolder);
        bpaApplication.setStakeHolder(applicationStakeHolders);
        bpaApplication.getApplicationDocument().clear();
        bpaApplication.setApplicationDocument(applicationDocs);
        processAndStoreApplicationDocuments(bpaApplication);
        bpaApplication.setAdmissionfeeAmount(applicationBpaService
                .setAdmissionFeeAmountForRegistration(String.valueOf(bpaApplication.getServiceType().getId())));
        BpaApplication bpaApplicationRes = applicationBpaService.createNewApplication(bpaApplication);
        return genericBillGeneratorService.generateBillAndRedirectToCollection(bpaApplicationRes, model);
    }

    private void validateDocuments(final List<ApplicationDocument> applicationDocs,
            final ApplicationDocument applicationDocument, final int i, final BindingResult resultBinder) {
        Iterator<MultipartFile> stream = null;
        if (ArrayUtils.isNotEmpty(applicationDocument.getFiles()))
            stream = Arrays.asList(applicationDocument.getFiles()).stream().filter(file -> !file.isEmpty())
                    .iterator();
        if (stream == null) {
            final String fieldError = "applicationDocs[" + i + "].files";
            resultBinder.rejectValue(fieldError, "files.required");
        } else
            applicationDocs.add(applicationDocument);
    }

}