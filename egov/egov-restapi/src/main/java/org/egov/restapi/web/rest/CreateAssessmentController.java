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
package org.egov.restapi.web.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.text.ParseException;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.egov.dcb.bean.ChequePayment;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.ptis.constants.PropertyTaxConstants;
import org.egov.ptis.domain.model.DocumentDetailsRequest;
import org.egov.ptis.domain.model.ErrorDetails;
import org.egov.ptis.domain.model.NewPropertyDetails;
import org.egov.ptis.domain.model.ViewPropertyDetails;
import org.egov.ptis.domain.repository.master.vacantland.LayoutApprovalAuthorityRepository;
import org.egov.ptis.domain.service.property.PropertyExternalService;
import org.egov.restapi.model.AmenitiesDetails;
import org.egov.restapi.model.AssessmentsDetails;
import org.egov.restapi.model.ConstructionTypeDetails;
import org.egov.restapi.model.CreatePropertyDetails;
import org.egov.restapi.model.DocumentTypeDetails;
import org.egov.restapi.model.PropertyAddressDetails;
import org.egov.restapi.model.SurroundingBoundaryDetails;
import org.egov.restapi.model.VacantLandDetails;
import org.egov.restapi.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreateAssessmentController {

    private static final String NO_APPROVAL = "No Approval";

    @Autowired
    private ValidationUtil validationUtil;

    @Autowired
    private PropertyExternalService propertyExternalService;

    @Autowired
    private LayoutApprovalAuthorityRepository layoutApprovalAuthorityRepo;

    /**
     * This method is used to create property.
     * 
     * @param createPropertyDetails - Property details request
     * @return
     * @throws IOException
     * @throws ParseException
     */

    @RequestMapping(value = "/property/createProperty", method = RequestMethod.POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public NewPropertyDetails createProperty(@RequestBody String createPropertyDetails)
            throws IOException, ParseException {
        ApplicationThreadLocals.setUserId(2L);
        CreatePropertyDetails createPropDetails = (CreatePropertyDetails) getObjectFromJSONRequest(createPropertyDetails,
                CreatePropertyDetails.class);
        NewPropertyDetails newPropertyDetails;
        ErrorDetails errorDetails = validationUtil.validateCreateRequest(createPropDetails,
                PropertyTaxConstants.PROPERTY_MODE_CREATE);
        if (errorDetails != null && errorDetails.getErrorCode() != null) {
            newPropertyDetails = new NewPropertyDetails();
            newPropertyDetails.setReferenceId(createPropDetails.getReferenceId());
            newPropertyDetails.setApplicationNo("-1");
            newPropertyDetails.setErrorDetails(errorDetails);
        } else {
            ViewPropertyDetails viewPropertyDetails = setRequestParameters(createPropDetails);

            if (createPropDetails.isAppurtenantLandChecked())
                newPropertyDetails = propertyExternalService.createAppurTenantProperties(viewPropertyDetails);
            else
                newPropertyDetails = propertyExternalService.createNewProperty(viewPropertyDetails);
        }
        return newPropertyDetails;
    }
    
    /**
     * API to allow uploading documents
     * @param applicationNo
     * @param ulbCode
     * @param documentDetail
     * @return
     */
    @RequestMapping(value = "/property/uploaddocument/{applicationNo}", method = RequestMethod.POST)
    public NewPropertyDetails uploadSupportDocuments(@PathVariable String applicationNo, @RequestParam String ulbCode,
    		DocumentDetailsRequest documentDetail) {
    	NewPropertyDetails newPropertyDetails;
    	ErrorDetails errorDetails = validationUtil.validateDocumentUploadRequest(documentDetail, applicationNo);
    	if (errorDetails != null && StringUtils.isNotBlank(errorDetails.getErrorCode())) {
            newPropertyDetails = new NewPropertyDetails();
            newPropertyDetails.setApplicationNo("-1");
            newPropertyDetails.setErrorDetails(errorDetails);
        } else {
        	newPropertyDetails = propertyExternalService.saveDocument(documentDetail, applicationNo);
        }
    	return newPropertyDetails;
    }

    /**
     * Prepares the ViewPropertyDetails bean for modification
     * @param createPropDetails
     * @return
     */
    public ViewPropertyDetails setRequestParameters(CreatePropertyDetails createPropDetails) {
        ViewPropertyDetails viewPropertyDetails = new ViewPropertyDetails();
        viewPropertyDetails.setAssessmentNumber(createPropDetails.getAssessmentNumber());
        viewPropertyDetails.setPropertyTypeMaster(createPropDetails.getPropertyTypeMasterCode());
        viewPropertyDetails.setCategory(createPropDetails.getCategoryCode());
        viewPropertyDetails.setApartmentCmplx(createPropDetails.getApartmentCmplxCode());
        viewPropertyDetails.setPropertyDepartment(createPropDetails.getPropertyDepartment());
        viewPropertyDetails.setFloorDetailsEntered(createPropDetails.getFloorDetailsEntered());
        viewPropertyDetails.setIsExtentAppurtenantLand(createPropDetails.getFloorDetailsEntered());

        viewPropertyDetails.setOwnerDetails(createPropDetails.getOwnerDetails());

        PropertyAddressDetails propertyAddressDetails = createPropDetails.getPropertyAddressDetails();
        viewPropertyDetails.setLocalityName(propertyAddressDetails.getLocalityNum());
        viewPropertyDetails.setStreetName(propertyAddressDetails.getStreetNum());
        viewPropertyDetails.setElectionWardName(propertyAddressDetails.getElectionWardNum());
        viewPropertyDetails.setDoorNo(propertyAddressDetails.getDoorNo());
        viewPropertyDetails.setEnumerationBlockName(propertyAddressDetails.getEnumerationBlockCode());
        viewPropertyDetails.setWardName(propertyAddressDetails.getWardNum());
        viewPropertyDetails.setZoneName(propertyAddressDetails.getZoneNum());
        viewPropertyDetails.setBlockName(propertyAddressDetails.getBlockNum());
        viewPropertyDetails.setPinCode(propertyAddressDetails.getPinCode());
        viewPropertyDetails.setIsCorrAddrDiff(propertyAddressDetails.getIsCorrAddrDiff());
        if (viewPropertyDetails.getIsCorrAddrDiff()) {
            viewPropertyDetails.setCorrAddr1(propertyAddressDetails.getCorrAddressDetails().getCorrAddr1());
            viewPropertyDetails.setCorrAddr2(propertyAddressDetails.getCorrAddressDetails().getCorrAddr2());
            viewPropertyDetails.setCorrPinCode(propertyAddressDetails.getCorrAddressDetails().getCorrPinCode());
        }

        AssessmentsDetails assessmentDetails = createPropDetails.getAssessmentDetails();
        viewPropertyDetails.setMutationReason(assessmentDetails.getMutationReasonCode());
        viewPropertyDetails.setExtentOfSite(assessmentDetails.getExtentOfSite());
        viewPropertyDetails.setOccupancyCertificationNo(assessmentDetails.getOccupancyCertificationNo());
        viewPropertyDetails.setOccupancyCertificationDate(assessmentDetails.getOccupancyCertificationDate());

        // Amenities Details
        AmenitiesDetails amenities = createPropDetails.getAmenitiesDetails();
        if (amenities != null) {
            viewPropertyDetails.setHasLift(amenities.hasLift());
            viewPropertyDetails.setHasToilet(amenities.hasToilet());
            viewPropertyDetails.setHasWaterTap(amenities.hasWaterTap());
            viewPropertyDetails.setHasElectricity(amenities.hasElectricity());
            viewPropertyDetails.setHasAttachedBathroom(amenities.hasAttachedBathroom());
            viewPropertyDetails.setHasWaterHarvesting(amenities.hasWaterHarvesting());
            viewPropertyDetails.setHasCableConnection(amenities.hasCableConnection());
        } else {
            viewPropertyDetails.setHasLift(false);
            viewPropertyDetails.setHasToilet(false);
            viewPropertyDetails.setHasWaterTap(false);
            viewPropertyDetails.setHasElectricity(false);
            viewPropertyDetails.setHasAttachedBathroom(false);
            viewPropertyDetails.setHasWaterHarvesting(false);
            viewPropertyDetails.setHasCableConnection(false);
        }

        // Construction Type Details
        ConstructionTypeDetails constructionTypeDetails = createPropDetails.getConstructionTypeDetails();
        if (constructionTypeDetails != null) {
            viewPropertyDetails.setFloorType(constructionTypeDetails.getFloorTypeId());
            viewPropertyDetails.setRoofType(constructionTypeDetails.getRoofTypeId());
            viewPropertyDetails.setWallType(constructionTypeDetails.getWallTypeId());
            viewPropertyDetails.setWoodType(constructionTypeDetails.getWoodTypeId());
        } else {
            viewPropertyDetails.setFloorType(null);
            viewPropertyDetails.setRoofType(null);
            viewPropertyDetails.setWallType(null);
            viewPropertyDetails.setWoodType(null);
        }
        if (createPropDetails.getPropertyTypeMasterCode().equalsIgnoreCase(PropertyTaxConstants.OWNERSHIP_TYPE_VAC_LAND)) {
            VacantLandDetails vacantLandDetails = createPropDetails.getVacantLandDetails();
            viewPropertyDetails.setSurveyNumber(vacantLandDetails.getSurveyNumber());
            viewPropertyDetails.setPattaNumber(vacantLandDetails.getPattaNumber());
            viewPropertyDetails.setVacantLandArea(vacantLandDetails.getVacantLandArea());
            viewPropertyDetails.setMarketValue(vacantLandDetails.getMarketValue());
            viewPropertyDetails.setCurrentCapitalValue(vacantLandDetails.getCurrentCapitalValue());
            viewPropertyDetails.setEffectiveDate(vacantLandDetails.getEffectiveDate());
            viewPropertyDetails.setVlPlotArea(vacantLandDetails.getVacantLandPlot());
            viewPropertyDetails.setLaAuthority(vacantLandDetails.getLayoutApprovalAuthority());
            if (!NO_APPROVAL.equals(
                    layoutApprovalAuthorityRepo.findOne(vacantLandDetails.getLayoutApprovalAuthority()).getName())) {
                viewPropertyDetails.setLpNo(vacantLandDetails.getLayoutPermitNumber());
                viewPropertyDetails.setLpDate(vacantLandDetails.getLayoutPermitDate());
            }
            // Surrounding Boundary Details
            SurroundingBoundaryDetails surroundingBoundaryDetails = createPropDetails.getSurroundingBoundaryDetails();
            viewPropertyDetails.setNorthBoundary(surroundingBoundaryDetails.getNorthBoundary());
            viewPropertyDetails.setSouthBoundary(surroundingBoundaryDetails.getSouthBoundary());
            viewPropertyDetails.setEastBoundary(surroundingBoundaryDetails.getEastBoundary());
            viewPropertyDetails.setWestBoundary(surroundingBoundaryDetails.getWestBoundary());
        } else {
            viewPropertyDetails.setFloorDetails(createPropDetails.getFloorDetails());
            viewPropertyDetails.setEffectiveDate(createPropDetails.getFloorDetails().get(0).getOccupancyDate());
        }

        // Documents Type
        DocumentTypeDetails documentTypeDetails = createPropDetails.getDocumentTypeDetails();
        viewPropertyDetails.setDocType(documentTypeDetails.getDocumentName());
        viewPropertyDetails.setRegdDocNo(documentTypeDetails.getDocumentNumber());
        viewPropertyDetails.setRegdDocDate(documentTypeDetails.getDocumentDate());
        viewPropertyDetails.setCourtName(documentTypeDetails.getCourtName());
        viewPropertyDetails.setMroProcNo(documentTypeDetails.getMroProceedingNumber());
        viewPropertyDetails.setMroProcDate(documentTypeDetails.getMroProceedingDate());
        viewPropertyDetails
                .setTwSigned(documentTypeDetails.getSigned() == null ? Boolean.FALSE : documentTypeDetails.getSigned());
        viewPropertyDetails.setParcelId(createPropDetails.getParcelId());
        viewPropertyDetails.setReferenceId(createPropDetails.getReferenceId());
        viewPropertyDetails.setLatitude(createPropDetails.getLatitude());
        viewPropertyDetails.setLongitude(createPropDetails.getLongitude());
        return viewPropertyDetails;
    }

    /**
     * This method is used to get POJO object from JSON request.
     * 
     * @param jsonString - request JSON string
     * @return
     * @throws IOException
     */
    private Object getObjectFromJSONRequest(String jsonString, Class cls) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
        mapper.configure(SerializationConfig.Feature.AUTO_DETECT_FIELDS, true);
        mapper.setDateFormat(ChequePayment.CHEQUE_DATE_FORMAT);
        return mapper.readValue(jsonString, cls);
    }

}
