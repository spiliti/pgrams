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

package org.egov.restapi.web.controller.tradelicense;

import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.entity.BoundaryType;
import org.egov.infra.admin.master.repository.BoundaryRepository;
import org.egov.infra.admin.master.repository.BoundaryTypeRepository;
import org.egov.infra.admin.master.service.CrossHierarchyService;
import org.egov.restapi.web.contracts.tradelicense.LicenseCreateRequest;
import org.egov.tl.entity.LicenseSubCategory;
import org.egov.tl.repository.LicenseCategoryRepository;
import org.egov.tl.repository.LicenseSubCategoryRepository;
import org.egov.tl.repository.NatureOfBusinessRepository;
import org.egov.tl.repository.ValidityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

@Component
public class LicenseCreateAPIValidator implements Validator {

    @Autowired
    private LicenseCategoryRepository licenseCategoryRepository;

    @Autowired
    private LicenseSubCategoryRepository licenseSubCategoryRepository;

    @Autowired
    private NatureOfBusinessRepository natureOfBusinessRepository;

    @Autowired
    private ValidityRepository validityRepository;

    @Autowired
    private BoundaryTypeRepository boundaryTypeRepository;

    @Autowired
    private CrossHierarchyService crossHierarchyService;

    @Autowired
    private BoundaryRepository boundaryRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return LicenseCreateRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        LicenseCreateRequest license = (LicenseCreateRequest) target;

        if ((license.getAgreementDate() == null && isNotBlank(license.getAgreementDocNo()))
                || license.getAgreementDate() != null && isBlank(license.getAgreementDocNo()))
            errors.rejectValue("agreementDate", "Provide both Agreement Date and Agreement Doc No",
                    "Provide both Agreement Date and Agreement Doc No");

        if (natureOfBusinessRepository.findOne(license.getNatureOfBusiness()) == null)
            errors.rejectValue("natureOfBusiness", "Invalid Nature Of Business", "Invalid Nature Of Business");

        validateLicenseCategory(license, errors);
        validateLicenseLocalities(license, errors);
    }

    private void validateLicenseCategory(LicenseCreateRequest license, Errors errors) {
        Long categoryId = null;
        if (license.getCategory() != null && license.getCategory().length() < 6)
            categoryId = licenseCategoryRepository.findByCodeIgnoreCase(license.getCategory()).getId();

        if (categoryId == null) {
            errors.rejectValue("category", "Invalid Category", "Invalid Category");
            return;
        }

        List<LicenseSubCategory> subCategories = licenseSubCategoryRepository.findByCategoryIdOrderByNameAsc(categoryId);
        if (subCategories.stream().noneMatch(subCategory -> subCategory.getCode().equalsIgnoreCase(license.getSubCategory())))
            errors.rejectValue("subCategory", "Invalid SubCategory", "Invalid SubCategory");

        if (Optional
                .ofNullable(validityRepository.findByNatureOfBusinessIdAndLicenseCategoryId(license.getNatureOfBusiness(), categoryId))
                .orElse(validityRepository.findByNatureOfBusinessIdAndLicenseCategoryIsNull(license.getNatureOfBusiness())) == null)
            errors.rejectValue("category", "License validity not defined for this Category",
                    "License validity not defined for this Category");
    }

    private void validateLicenseLocalities(LicenseCreateRequest license, Errors errors) {
        BoundaryType locality = boundaryTypeRepository.findByNameAndHierarchyTypeName("Locality", "LOCATION");
        Boundary childBoundary = boundaryRepository.findByIdAndBoundaryType(license.getBoundary(), locality);
        if (childBoundary == null)
            errors.rejectValue("boundary", "Boundary does not exist", "Boundary does not exist");
        else {
            BoundaryType blockType = boundaryTypeRepository.findByNameAndHierarchyTypeName("Block", "REVENUE");
            if (crossHierarchyService
                    .getParentBoundaryByChildBoundaryAndParentBoundaryType(childBoundary.getId(), blockType.getId())
                    .stream()
                    .noneMatch(boundary -> boundary.getParent().getId().equals(license.getParentBoundary())))
                errors.rejectValue("boundary", "Parent Boundary does not exist", "Parent Boundary does not exist");
        }
    }
}