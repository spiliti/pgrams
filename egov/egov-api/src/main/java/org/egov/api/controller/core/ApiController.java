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

package org.egov.api.controller.core;

import org.egov.infra.filestore.service.FileStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public abstract class ApiController {

    public static final String EMAIL_ID_FIELD = "emailId";
    public static final String MOBILE_FIELD = "mobileNumber";
    public static final String DEVICE_ID_FIELD = "deviceId";
    public static final String ALT_CONTACT_NUMBER_FIELD = "altContactNumber";
    public static final String EGOV_API_ERROR = "EGOV-API ERROR ";
    public static final String SERVER_ERROR_KEY = "server.error";

    @Autowired
    @Qualifier("fileStoreService")
    protected FileStoreService fileStoreService;

    @Autowired
    private MessageSource messageSource;

    public String getMessage(String key) {
        return this.messageSource.getMessage(key, null, "Unable to process your request now.", Locale.getDefault());
    }

    public ApiResponse getResponseHandler() {
        return ApiResponse.newInstance();
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<String> apiExceptionHandler(HttpMessageNotReadableException ex) {
        return getResponseHandler().error(ex.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<String> apiExceptionHandler(MissingServletRequestParameterException ex) {
        return getResponseHandler().error(ex.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<String> apiExceptionHandler(Exception ex) {
        return getResponseHandler().error(ex.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<String> apiExceptionHandler(MethodArgumentNotValidException ex) {
        return getResponseHandler().error(ex.getMessage(), BAD_REQUEST);
    }
}
