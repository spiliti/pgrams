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

package org.egov.council.web.controller;

import org.apache.commons.lang.StringUtils;
import org.egov.council.entity.CouncilSequenceNumber;
import org.egov.council.service.CouncilSequenceGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/councilsequenceno")
public class CouncilManualSequenceNoController {

    private static final String COUNCILSEQUENCECREATE = "councilsequence";
    @Autowired
    private CouncilSequenceGenerationService councilSequenceGenerationService;

    @RequestMapping(value = "/create", method = GET)
    public String newForm(final Model model) {
        String preambleseq = StringUtils.EMPTY;
        String resolutionseq = StringUtils.EMPTY;
        String agendaSeq = StringUtils.EMPTY;
        String meetingSeq = StringUtils.EMPTY;

        if (!councilSequenceGenerationService.getPreambleLastSeq().isEmpty())
            preambleseq = councilSequenceGenerationService.getPreambleLastSeq();

        if (!councilSequenceGenerationService.getresolutionsequence().isEmpty())
            resolutionseq = councilSequenceGenerationService.getresolutionsequence();

        if (!councilSequenceGenerationService.getAgendaLastSeq().isEmpty())
            agendaSeq = councilSequenceGenerationService.getAgendaLastSeq();

        if (!councilSequenceGenerationService.getMeetingSeqNumber().isEmpty())
            meetingSeq = councilSequenceGenerationService.getMeetingSeqNumber();

        model.addAttribute("preambleseq", preambleseq);
        model.addAttribute("councilSequenceNumber", new CouncilSequenceNumber());
        model.addAttribute("resolutionseq", resolutionseq);
        model.addAttribute("agendaSeq", agendaSeq);
        model.addAttribute("meetingSeq", meetingSeq);

        return COUNCILSEQUENCECREATE;
    }

    @RequestMapping(value = "/create", method = POST)
    public String createCouncilSequence(final Model model, final HttpServletRequest request,
            @ModelAttribute final CouncilSequenceNumber councilSequenceNumber, final BindingResult resultBinder)
            throws SQLException {
        
        String meetingSeq = null;
        String preambleseq = null;
        String resolutionseq = null;
        String agendaSeq = null;
        if (request.getParameter("lastPreambleSeq") != null)
            preambleseq = request.getParameter("lastPreambleSeq");

        if (request.getParameter("lastAgendaSeq") != null)
            agendaSeq = request.getParameter("lastAgendaSeq");

        if (request.getParameter("lastResolutionSeq") != null)
            resolutionseq = request.getParameter("lastResolutionSeq");

        if (request.getParameter("lastMeetingSeq") != null)
            meetingSeq = request.getParameter("lastMeetingSeq");
        councilSequenceGenerationService.validate(resultBinder, councilSequenceNumber, preambleseq, resolutionseq, agendaSeq,
                meetingSeq);

        if (resultBinder.hasErrors()) {
            model.addAttribute("preambleseq", preambleseq);
            model.addAttribute("councilSequenceNumber", councilSequenceNumber);
            model.addAttribute("resolutionseq", resolutionseq);
            model.addAttribute("agendaSeq", agendaSeq);
            model.addAttribute("meetingSeq", meetingSeq);
            return COUNCILSEQUENCECREATE;

        }

        CouncilSequenceNumber sequence = councilSequenceGenerationService.create(councilSequenceNumber);
        councilSequenceGenerationService.updatesequences(sequence);
        model.addAttribute("message", "Sequence Numbers Updated suceesfully");

        return "councilsequence-success";
    }

}
