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

package org.egov.restapi.web.controller.pgr.integration.ivrs;

import org.egov.pgr.integration.ivrs.entity.contract.IVRSFeedbackUpdateRequest;
import org.egov.pgr.integration.ivrs.entity.contract.IVRSFeedbackUpdateResponse;
import org.egov.pgr.integration.ivrs.service.IVRSFeedbackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class IVRSFeedbackUpdateAPIController {

    private static final Logger LOG = LoggerFactory.getLogger(IVRSFeedbackUpdateAPIController.class);

    @Autowired
    private IVRSFeedbackService ivrsFeedbackService;

    @Autowired
    private IVRSFeedbackUpdateAPIValidator ivrsFeedbackUpdateAPIValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(ivrsFeedbackUpdateAPIValidator);
    }

    @PostMapping("complaint/ivrs/feedback/update")
    public IVRSFeedbackUpdateResponse updateComplaint(@Valid @RequestBody IVRSFeedbackUpdateRequest updateRequest, BindingResult binding) {
        if (binding.hasErrors()) {
            List<String> complaintResponse = binding.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            return new IVRSFeedbackUpdateResponse(false, updateRequest.getCrn(), HttpStatus.BAD_REQUEST.toString(),
                    complaintResponse.toString());
        } else {
            return new IVRSFeedbackUpdateResponse(true, ivrsFeedbackService.createFeedback(updateRequest).getComplaint().getCrn(),
                    HttpStatus.OK.toString(), "Complaint Feedback Rating Updated Successfully");
        }
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    public ResponseEntity<Object> parameterErrors(ServletRequestBindingException exception) {
        LOG.error("Error occurred while updating feedback", exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new IVRSFeedbackUpdateResponse(false, HttpStatus.BAD_REQUEST.toString(), exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> restErrors(Exception exception) {
        LOG.error("Error occurred while taking IVRS response", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new IVRSFeedbackUpdateResponse(false, HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Server Error"));
    }
}
