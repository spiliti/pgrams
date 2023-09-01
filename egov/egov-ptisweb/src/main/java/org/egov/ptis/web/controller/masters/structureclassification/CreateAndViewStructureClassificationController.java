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
package org.egov.ptis.web.controller.masters.structureclassification;

import org.egov.infra.admin.master.service.UserService;
import org.egov.ptis.domain.entity.property.StructureClassification;
import org.egov.ptis.master.service.StructureClassificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value = "/structureclassification")
public class CreateAndViewStructureClassificationController {

	private final StructureClassificationService structureClassificationService;

	@Autowired
	public UserService userService;

	@Autowired
	public CreateAndViewStructureClassificationController(
			final StructureClassificationService structureClassificationService) {
		this.structureClassificationService = structureClassificationService;
	}

	@ModelAttribute
	public StructureClassification structureClassificationModel() {
		return new StructureClassification();
	}

	@ModelAttribute(value = "structuretypes")
	public List<StructureClassification> listStructures() {
		return structureClassificationService.getAllStructureTypes();
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String create(final Model model) {
		model.addAttribute("structureclassification", structureClassificationModel());
		return "structureclassification-create";
	}

	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public String showStructureType(final Model model) {
		model.addAttribute("structureclassifications", structureClassificationService.getAllStructureTypes());
		return "structureclassification-view";
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String create(@Valid @ModelAttribute final StructureClassification structureClassification,
			final BindingResult errors, final RedirectAttributes redirectAttributes, final HttpServletRequest request,
			final Model model) {

		if (errors.hasErrors())
			return "structureclassification-create";
		structureClassification.setNumber(1);
		structureClassification.setStartInstallment(structureClassificationService.getInstallment());
		structureClassification.setIsHistory('N');

		structureClassificationService.create(structureClassification);
		redirectAttributes.addFlashAttribute("message", "msg.structcls.create.success");
		return "redirect:/structureclassification/create";

	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String search(final Model model) {
		return "structureclassification-search";

	}

}