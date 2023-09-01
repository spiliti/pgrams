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

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.egov.dcb.bean.ChequePayment;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.restapi.constants.RestApiConstants;
import org.egov.restapi.model.ContractorHelper;
import org.egov.restapi.model.RestErrors;
import org.egov.restapi.service.ExternalContractorService;
import org.egov.restapi.util.JsonConvertor;
import org.egov.works.master.service.ContractorService;
import org.egov.works.models.masters.Contractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
public class ContractorController {

    @Autowired
    private ContractorService contractorService;

    @Autowired
    private ExternalContractorService externalContractorService;

    @RequestMapping(value = "/egworks/contractors", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String getContractors(@RequestParam(value = "code", required = false) final String code,
            final HttpServletResponse response) {
        if (StringUtils.isBlank(code)) {
            response.setStatus(HttpServletResponse.SC_CREATED);
            return JsonConvertor.convert(externalContractorService.populateContractor());
        } else {
            final RestErrors restErrors = new RestErrors();
            final Contractor contractor = contractorService.getContractorByCode(code);
            if (contractor == null) {
                restErrors.setErrorCode(RestApiConstants.THIRD_PARTY_ERR_CODE_NOT_EXIST_CONTRACTOR);
                restErrors.setErrorMessage(RestApiConstants.THIRD_PARTY_ERR_MSG_NOT_EXIST_CONTRACTOR);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return JsonConvertor.convert(restErrors);
            } else {
                response.setStatus(HttpServletResponse.SC_CREATED);
                return JsonConvertor.convert(externalContractorService.populateContractorData(contractor));
            }
        }
    }

    @RequestMapping(value = "/egworks/contractor", method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public String createContractor(@RequestBody final String requestJson,
            final HttpServletResponse response) throws IOException {
        List<RestErrors> errors = new ArrayList<>();
        final RestErrors restErrors = new RestErrors();
        ApplicationThreadLocals.setUserId(2L);
        if (StringUtils.isBlank(requestJson)) {
            restErrors.setErrorCode(RestApiConstants.THIRD_PARTY_ERR_CODE_NO_JSON_REQUEST);
            restErrors.setErrorMessage(RestApiConstants.THIRD_PARTY_ERR_MSG_NO_JSON_REQUEST);
            errors.add(restErrors);
            return JsonConvertor.convert(errors);
        }
        final ContractorHelper contractorHelper = (ContractorHelper) getObjectFromJSONRequest(requestJson,
                ContractorHelper.class);
        errors = externalContractorService.validateContactorToCreate(contractorHelper);
        if (!errors.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return JsonConvertor.convert(errors);
        } else {
            final Contractor contractor = externalContractorService.populateContractorToCreate(contractorHelper);
            final Contractor savedContractor = externalContractorService.saveContractor(contractor);
            final StringBuilder successMessage = new StringBuilder();
            successMessage.append("Contractor data saved successfully with code ").append(savedContractor.getCode());
            response.setStatus(HttpServletResponse.SC_CREATED);
            return JsonConvertor.convert(successMessage.toString());
        }

    }

    @RequestMapping(value = "/egworks/contractor", method = PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public String updateContractor(@RequestBody final String requestJson,
            final HttpServletResponse response) throws IOException {
        List<RestErrors> errors = new ArrayList<>();
        final RestErrors restErrors = new RestErrors();
        ApplicationThreadLocals.setUserId(2L);
        if (StringUtils.isBlank(requestJson)) {
            restErrors.setErrorCode(RestApiConstants.THIRD_PARTY_ERR_CODE_NO_JSON_REQUEST);
            restErrors.setErrorMessage(RestApiConstants.THIRD_PARTY_ERR_MSG_NO_JSON_REQUEST);
            errors.add(restErrors);
            return JsonConvertor.convert(restErrors);
        }
        final ContractorHelper contractorHelper = (ContractorHelper) getObjectFromJSONRequest(requestJson,
                ContractorHelper.class);
        errors = externalContractorService.validateContactorToUpdate(contractorHelper);
        if (!errors.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return JsonConvertor.convert(errors);
        } else {
            final Contractor contractor = externalContractorService.populateContractorToUpdate(contractorHelper);
            final Contractor savedContractor = externalContractorService.updateContractor(contractor);
            final StringBuilder modifyMessage = new StringBuilder();
            modifyMessage.append("Contractor data modified successfully with code ").append(savedContractor.getCode());
            response.setStatus(HttpServletResponse.SC_CREATED);
            return JsonConvertor.convert(modifyMessage);
        }
    }

    private Object getObjectFromJSONRequest(final String jsonString, final Class cls)
            throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
        mapper.configure(SerializationConfig.Feature.AUTO_DETECT_FIELDS, true);
        mapper.setDateFormat(ChequePayment.CHEQUE_DATE_FORMAT);
        return mapper.readValue(jsonString, cls);
    }

}
