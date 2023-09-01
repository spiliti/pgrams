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
package org.egov.lcms.web.controller.masters;

import org.egov.lcms.masters.entity.CaseTypeMaster;
import org.egov.lcms.masters.service.CaseTypeMasterService;
import org.egov.lcms.web.adaptor.CaseTypeMasterJsonAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

import static org.egov.infra.utils.JsonUtils.toJSON;

@Controller
@RequestMapping("/casetypemaster")
public class CaseTypeMasterController {

    private static final String CASETYPEMASTER_NEW = "casetypemaster-new";
    private static final String CASETYPEMASTER_RESULT = "casetypemaster-result";
    private static final String CASETYPEMASTER_EDIT = "casetypemaster-edit";
    private static final String CASETYPEMASTER_VIEW = "casetypemaster-view";
    private static final String CASETYPEMASTER_SEARCH = "casetypemaster-search";
    private static final String CASETYPEMASTER = "caseTypeMaster";

    @Autowired
    private CaseTypeMasterService casetypeMasterService;

    @Autowired
    private MessageSource messageSource;

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String newForm(final Model model) {
        model.addAttribute(CASETYPEMASTER, new CaseTypeMaster());
        return CASETYPEMASTER_NEW;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute final CaseTypeMaster caseTypeMaster, final BindingResult errors,
            final Model model, final RedirectAttributes redirectAttrs) {
        if (errors.hasErrors()) {
            return CASETYPEMASTER_NEW;
        }
        casetypeMasterService.create(caseTypeMaster);
        redirectAttrs.addFlashAttribute("message", messageSource.getMessage("msg.casetypeMaster.success", null, null));
        return "redirect:/casetypemaster/result/" + caseTypeMaster.getId();
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable("id") final Long id, Model model) {
        CaseTypeMaster caseTypeMaster = casetypeMasterService.findOne(id);
        model.addAttribute(CASETYPEMASTER, caseTypeMaster);
        return CASETYPEMASTER_EDIT;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(@Valid @ModelAttribute final CaseTypeMaster caseTypeMaster, final BindingResult errors,
            final Model model, final RedirectAttributes redirectAttrs) {
        if (errors.hasErrors()) {
            return CASETYPEMASTER_EDIT;
        }
        casetypeMasterService.update(caseTypeMaster);
        redirectAttrs.addFlashAttribute("message", messageSource.getMessage("msg.casetypeMaster.update", null, null));
        return "redirect:/casetypemaster/result/" + caseTypeMaster.getId();
    }

    @RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
    public String view(@PathVariable("id") final Long id, Model model) {
        CaseTypeMaster caseTypeMaster = casetypeMasterService.findOne(id);
        model.addAttribute(CASETYPEMASTER, caseTypeMaster);
        return CASETYPEMASTER_VIEW;
    }

    @RequestMapping(value = "/result/{id}", method = RequestMethod.GET)
    public String result(@PathVariable("id") final Long id, Model model) {
        CaseTypeMaster caseTypeMaster = casetypeMasterService.findOne(id);
        model.addAttribute(CASETYPEMASTER, caseTypeMaster);
        return CASETYPEMASTER_RESULT;
    }

    @RequestMapping(value = "/search/{mode}", method = RequestMethod.GET)
    public String search(@PathVariable("mode") final String mode, Model model) {
        CaseTypeMaster caseTypeMaster = new CaseTypeMaster();
        model.addAttribute(CASETYPEMASTER, caseTypeMaster);
        return CASETYPEMASTER_SEARCH;

    }

    @RequestMapping(value = "/ajaxsearch/{mode}", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody String ajaxsearch(@PathVariable("mode") final String mode, Model model,
            @ModelAttribute final CaseTypeMaster casetypeMaster) {
        List<CaseTypeMaster> searchResultList = casetypeMasterService.search(casetypeMaster);
        return new StringBuilder("{ \"data\":")
                .append(toJSON(searchResultList, CaseTypeMaster.class, CaseTypeMasterJsonAdaptor.class)).append("}")
                .toString();
    }
}