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

import static org.egov.council.utils.constants.CouncilConstants.AGENDAUSEDINMEETING;
import static org.egov.council.utils.constants.CouncilConstants.AGENDA_MODULENAME;
import static org.egov.council.utils.constants.CouncilConstants.AGENDA_STATUS_APPROVED;
import static org.egov.council.utils.constants.CouncilConstants.APPROVED;
import static org.egov.council.utils.constants.CouncilConstants.ATTENDANCEFINALIZED;
import static org.egov.council.utils.constants.CouncilConstants.COUNCILMEETING;
import static org.egov.council.utils.constants.CouncilConstants.MEETINGCANCELLED;
import static org.egov.council.utils.constants.CouncilConstants.MEETINGRESOLUTIONFILENAME;
import static org.egov.council.utils.constants.CouncilConstants.MEETING_MODULENAME;
import static org.egov.council.utils.constants.CouncilConstants.MODULE_NAME;
import static org.egov.council.utils.constants.CouncilConstants.MOM_FINALISED;
import static org.egov.council.utils.constants.CouncilConstants.getMeetingTimings;
import static org.egov.infra.utils.JsonUtils.toJSON;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.council.autonumber.CouncilMeetingNumberGenerator;
import org.egov.council.entity.CommitteeMembers;
import org.egov.council.entity.CommitteeType;
import org.egov.council.entity.CouncilAgenda;
import org.egov.council.entity.CouncilAgendaDetails;
import org.egov.council.entity.CouncilMeeting;
import org.egov.council.entity.CouncilMeetingType;
import org.egov.council.entity.MeetingAttendence;
import org.egov.council.entity.MeetingMOM;
import org.egov.council.service.CommitteeTypeService;
import org.egov.council.service.CouncilAgendaService;
import org.egov.council.service.CouncilCommitteeMemberService;
import org.egov.council.service.CouncilMeetingService;
import org.egov.council.service.CouncilMeetingTypeService;
import org.egov.council.service.CouncilPreambleService;
import org.egov.council.service.CouncilReportService;
import org.egov.council.service.CouncilSmsAndEmailService;
import org.egov.council.utils.constants.CouncilConstants;
import org.egov.council.web.adaptor.CouncilMeetingJsonAdaptor;
import org.egov.council.web.adaptor.MeetingAttendanceJsonAdaptor;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.utils.FileStoreUtils;
import org.egov.infra.utils.FileUtils;
import org.egov.infra.utils.autonumber.AutonumberServiceBeanResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/councilmeeting")
public class CouncilMeetingController {

    private static final String REDIRECT_COUNCILMEETING_RESULT = "redirect:/councilmeeting/result/";
    private static final String MEETING_NUMBER_AUTO = "MEETING_NUMBER_AUTO";
    private static final String APPLICATION_RTF = "application/rtf";
    private static final String DATA = "{ \"data\":";
    private static final String MSG_ATTENDANCE_ALREADY_FINALIZD = "msg.attendance.already.finalizd";
    private static final String COUNCIL_MEETING = "councilMeeting";
    private static final String MESSAGE = "message";
    private static final String COUNCILMEETING_NEW = "councilmeeting-new";
    private static final String COMMONERRORPAGE = "common-error-page";
    private static final String COUNCILMEETING_RESULT = "councilmeeting-result";
    private static final String COUNCILMEETING_EDIT = "councilmeeting-edit";
    private static final String COUNCILMEETING_VIEW = "councilmeeting-view";
    private static final String COUNCILMEETING_SEARCH = "councilmeeting-search";
    private static final String COUNCIL_MEETING_AGENDA_SEARCH = "councilmeetingAgenda-search";
    private static final String COUNCILMEETING_ATTENDANCE_SEARCH = "councilmeeting-attendsearch";
    private static final String COUNCILMEETING_ATTENDANCE_VIEW = "councilmeeting-attendsearch-view";
    private static final String COUNCILMEETING_SEND_SMS_EMAIL = "councilmeetingsearch-tosendsms-email";
    private static final String COUNCILMEETING_EDIT_ATTENDANCE = "councilmeeting-attend-form";
    private static final String COUNCILMEETING_ATTENDANCE_RESULT = "councilmeeting-attend-result";
    private static final String MSG_MOM_RESOLUTION_CREATED = "msg.mom.create";
    @Autowired
    protected FileStoreUtils fileStoreUtils;
    @Autowired
    private CouncilMeetingService councilMeetingService;
    @Autowired
    private EgwStatusHibernateDAO egwStatusHibernateDAO;
    @Autowired
    private CouncilAgendaService councilAgendaService;
    @Autowired
    private CouncilPreambleService councilPreambleService;
    @Autowired
    private AutonumberServiceBeanResolver autonumberServiceBeanResolver;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private CommitteeTypeService committeeTypeService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private CouncilSmsAndEmailService councilSmsAndEmailService;
    @Autowired
    private CouncilReportService councilReportService;
    @Qualifier("fileStoreService")
    @Autowired
    private FileStoreService fileStoreService;
    @Autowired
    private CouncilCommitteeMemberService committeeMemberService;
    @Autowired
    private CouncilMeetingTypeService councilMeetingTypeService;

    @ModelAttribute("committeeType")
    public List<CommitteeType> getCommitteTypeList() {
        return committeeTypeService.getActiveCommiteeType();
    }

    @ModelAttribute("meetingTimingMap")
    public Map<String, String> getMeetingTimingList() {
        return getMeetingTimings();
    }

    @ModelAttribute("departments")
    public List<Department> getDepartmentList() {
        return departmentService.getAllDepartments();
    }

    @ModelAttribute("meetingType")
    public List<CouncilMeetingType> getmeetingTypeList() {
        return councilMeetingTypeService.findAllActiveMeetingType();
    }
    
    @RequestMapping(value = "/new/{id}", method = RequestMethod.GET)
    public String newForm(@ModelAttribute final CouncilMeeting councilMeeting, @PathVariable("id") final Long id,
                          final Model model) {

        CouncilAgenda councilAgenda = councilAgendaService.findOne(id);
        model.addAttribute("autoMeetingNoGenEnabled", isAutoMeetingNoGenEnabled()); 
        model.addAttribute(COUNCIL_MEETING, councilMeeting);
        if (councilAgenda != null && AGENDAUSEDINMEETING.equals(councilAgenda.getStatus().getCode())) {
            model.addAttribute(MESSAGE, "msg.agenda.exist");
            return COMMONERRORPAGE;
        } else if (councilAgenda != null && councilAgenda.getCommitteeType() != null
                && councilAgenda.getCommitteeType().getCommiteemembers().isEmpty()) {
            model.addAttribute("errormessage", messageSource.getMessage("msg.committee.members.notadded",
                    new String[] { councilAgenda.getCommitteeType().getName() }, null));
            return COMMONERRORPAGE;
        } else if (councilAgenda != null) {
            councilMeeting.setCommitteeType(councilAgenda.getCommitteeType());
            buildMeetingMomByUsingAgendaDetails(councilMeeting, councilAgenda);
            return COUNCILMEETING_NEW;

        } else {
            model.addAttribute(MESSAGE, "msg.invalid.agenda.details");
            return COMMONERRORPAGE;
        }

    }

    private void buildMeetingMomByUsingAgendaDetails(final CouncilMeeting councilMeeting, CouncilAgenda councilAgenda) {
        Long itemNumber = Long.valueOf(1);
        for (CouncilAgendaDetails councilAgendaDetail : councilAgenda.getAgendaDetails()) {
            MeetingMOM meetingMom = new MeetingMOM();
            meetingMom.setMeeting(councilMeeting);
            meetingMom.setAgenda(councilAgendaDetail.getAgenda());
            meetingMom.setPreamble(councilAgendaDetail.getPreamble());
            meetingMom.setItemNumber(itemNumber.toString());
            itemNumber++;
            councilMeeting.addMeetingMoms(meetingMom);
        }
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(@Valid @ModelAttribute final CouncilMeeting councilMeeting, final BindingResult errors,
                         final Model model, final RedirectAttributes redirectAttrs, final HttpServletRequest request) {

        validateCouncilMeeting(errors);
        if (errors.hasErrors()) {
            model .addAttribute("autoMeetingNoGenEnabled", isAutoMeetingNoGenEnabled()); 
            model.addAttribute(COUNCIL_MEETING, councilMeeting);
            return COUNCILMEETING_NEW;
        }
        if (councilMeeting.getStatus() == null)
            councilMeeting.setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(COUNCILMEETING, APPROVED));
        if (isAutoMeetingNoGenEnabled()) {
            CouncilMeetingNumberGenerator meetingNumberGenerator = autonumberServiceBeanResolver
                    .getAutoNumberServiceFor(CouncilMeetingNumberGenerator.class);
            councilMeeting.setMeetingNumber(meetingNumberGenerator.getNextNumber(councilMeeting));
        }
        for (MeetingMOM meetingMom : councilMeeting.getMeetingMOMs()) {
            meetingMom.setMeeting(councilMeeting);
            meetingMom.getAgenda()
                    .setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(AGENDA_MODULENAME, AGENDAUSEDINMEETING));
        }

        councilMeetingService.create(councilMeeting);
        councilSmsAndEmailService.sendSms(councilMeeting, null);
        councilSmsAndEmailService.sendEmail(councilMeeting, null,
                councilReportService.generatePDFForAgendaDetails(councilMeeting));
        redirectAttrs.addFlashAttribute(MESSAGE, messageSource.getMessage("msg.councilMeeting.success", null, null));
        return REDIRECT_COUNCILMEETING_RESULT + councilMeeting.getId();
    }

    private void validateCouncilMeeting(BindingResult errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "meetingDate", "notempty.meeting.meetingDate");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "meetingTime", "notempty.meeting.meetingTime");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "meetingLocation", "notempty.meeting.committeeType");
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable("id") final Long id, final Model model) {
        CouncilMeeting councilMeeting = councilMeetingService.findOne(id);
        councilMeetingService.sortMeetingMomByItemNumber(councilMeeting);
        model.addAttribute("autoMeetingNoGenEnabled", true);
        model.addAttribute(COUNCIL_MEETING, councilMeeting);

        return COUNCILMEETING_EDIT;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(@Valid @ModelAttribute final CouncilMeeting councilMeeting, final BindingResult errors,
                         final Model model, final RedirectAttributes redirectAttrs) {
        validateCouncilMeeting(errors);
        if (errors.hasErrors()) {
            councilMeetingService.sortMeetingMomByItemNumber(councilMeeting);
            model.addAttribute("autoMeetingNoGenEnabled", true);
            return COUNCILMEETING_EDIT;
        }
        councilMeetingService.update(councilMeeting);
        redirectAttrs.addFlashAttribute(MESSAGE, messageSource.getMessage("msg.councilMeeting.success", null, null));
        return REDIRECT_COUNCILMEETING_RESULT + councilMeeting.getId();
    }
    
    @RequestMapping(value = "/update", params = "cancel", method = RequestMethod.POST)
    public String cancelMeeting(@Valid @ModelAttribute final CouncilMeeting councilMeeting, final BindingResult errors,
            final Model model, final RedirectAttributes redirectAttrs) {
        if (errors.hasErrors()) {
            return COUNCILMEETING_EDIT;
        }
        councilMeeting.setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(MEETING_MODULENAME, MEETINGCANCELLED));
        if (!councilMeeting.getMeetingMOMs().isEmpty()) {
            councilMeeting.getMeetingMOMs().get(0).getAgenda().setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(
                    AGENDA_MODULENAME, AGENDA_STATUS_APPROVED));
        }
        councilMeetingService.update(councilMeeting);
        councilMeetingService.deleteMeetingMoms(councilMeeting.getMeetingMOMs());
        model.addAttribute(MESSAGE, "msg.councilMeeting.cancel");
        return COMMONERRORPAGE;
    }

    @RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
    public String view(@PathVariable("id") final Long id, Model model) {
        CouncilMeeting councilMeeting = councilMeetingService.findOne(id);
        councilMeetingService.sortMeetingMomByItemNumber(councilMeeting);
        model.addAttribute(COUNCIL_MEETING, councilMeeting);
        return COUNCILMEETING_VIEW;
    }

    @RequestMapping(value = "/result/{id}", method = RequestMethod.GET)
    public String result(@PathVariable("id") final Long id, Model model) {
        CouncilMeeting councilMeeting = councilMeetingService.findOne(id);
        model.addAttribute(COUNCIL_MEETING, councilMeeting);
        model.addAttribute("commiteemembelist", councilMeeting.getCommitteeType().getCommiteemembers());
        return COUNCILMEETING_RESULT;
    }

    @RequestMapping(value = "/search/{mode}", method = RequestMethod.GET)
    public String search(@PathVariable("mode") final String mode, Model model) {
        CouncilMeeting councilMeeting = new CouncilMeeting();
        model.addAttribute(COUNCIL_MEETING, councilMeeting);
        return COUNCILMEETING_SEARCH;

    }

    @RequestMapping(value = "/agendasearch/{mode}", method = RequestMethod.GET)
    public String searchagenda(@PathVariable("mode") final String mode, Model model) {
        model.addAttribute("councilAgenda", new CouncilAgenda());
        return COUNCIL_MEETING_AGENDA_SEARCH;

    }

    @RequestMapping(value = "/ajaxsearch/{mode}", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String ajaxsearch(@PathVariable("mode") final String mode, Model model,
                             @ModelAttribute final CouncilMeeting councilMeeting) {
        if (null != mode && !"".equals(mode)) {
            List<CouncilMeeting> searchResultList;

            if ("edit".equalsIgnoreCase(mode)) {
                searchResultList = councilMeetingService.searchMeetingForEdit(councilMeeting);
            } else {
                searchResultList = councilMeetingService.searchMeeting(councilMeeting);
            }
            return new StringBuilder(DATA)
                    .append(toJSON(searchResultList, CouncilMeeting.class, CouncilMeetingJsonAdaptor.class))
                    .append("}").toString();
        }
        return null;
    }

    @RequestMapping(value = "/searchmeeting-tocreatemom", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String searchMeetingAndToCreateMOM(Model model,
                                              @ModelAttribute final CouncilMeeting councilMeeting) {
        List<CouncilMeeting> searchResultList = councilMeetingService.searchMeetingToCreateMOM(councilMeeting);
        return new StringBuilder(DATA).append(toJSON(searchResultList, CouncilMeeting.class, CouncilMeetingJsonAdaptor.class))
                .append("}")
                .toString();
    }

    @RequestMapping(value = "/viewsmsemail", method = RequestMethod.GET)
    public String retrieveSmsAndEmailDetailsForCouncilMeeting(final Model model) {
        CouncilMeeting councilMeeting = new CouncilMeeting();
        model.addAttribute(COUNCIL_MEETING, councilMeeting);
        model.addAttribute("mode", "view");
        return COUNCILMEETING_SEND_SMS_EMAIL;
    }

    @RequestMapping(value = "/sendsmsemail", method = RequestMethod.GET)
    @ResponseBody
    public String sendSmsAndEmailDetailsForCouncilMeeting(@RequestParam("id") Long id,
                                                          @RequestParam("msg") String msg, final Model model) {
        CouncilMeeting councilMeeting = councilMeetingService.findOne(id);
        councilSmsAndEmailService.sendSms(councilMeeting, msg);
        councilSmsAndEmailService.sendEmail(councilMeeting, msg,
                councilReportService.generatePDFForAgendaDetails(councilMeeting));
        return new StringBuilder("{ \"success\":true }").toString();
    }

    @RequestMapping(value = "/generateresolution/{id}", method = RequestMethod.POST)
    public String viewDemandNoticeReport(@PathVariable final Long id, final Model model) {

        CouncilMeeting councilMeeting = councilMeetingService.findOne(id);
        councilReportService.generatePDFForMom(councilMeeting);
        model.addAttribute(MESSAGE, MSG_MOM_RESOLUTION_CREATED);
        model.addAttribute("id", id);
        return "mom-resolution-response";
    }

    @RequestMapping(value = "/attendance/search", method = RequestMethod.GET)
    public String getSearchAttendance(final Model model) {
        CouncilMeeting councilMeeting = new CouncilMeeting();
        model.addAttribute(COUNCIL_MEETING, councilMeeting);
        model.addAttribute("mode", "view");
        return COUNCILMEETING_ATTENDANCE_SEARCH;
    }

    @RequestMapping(value = "/attendance/report/search", method = RequestMethod.GET)
    public String getSearchReportForAttendance(final Model model) {
        CouncilMeeting councilMeeting = new CouncilMeeting();
        model.addAttribute(COUNCIL_MEETING, councilMeeting);
        model.addAttribute("mode", "view");
        return COUNCILMEETING_ATTENDANCE_SEARCH;
    }

    @RequestMapping(value = "/attendance/search/view/{id}", method = RequestMethod.GET)
    public String viewAttendanceDetails(@PathVariable("id") final CouncilMeeting councilMeeting, Model model) {
        model.addAttribute("id", councilMeeting.getId());
        model.addAttribute("currDate", new Date());
        return COUNCILMEETING_ATTENDANCE_VIEW;
    }

    @RequestMapping(value = "/attendance/result/{id}", method = RequestMethod.GET)
    public String showAttendanceResult(@PathVariable("id") final CouncilMeeting councilMeeting, Model model) {
        model.addAttribute("id", councilMeeting.getId());
        model.addAttribute("currDate", new Date());
        return COUNCILMEETING_ATTENDANCE_RESULT;
    }

    @RequestMapping(value = "/attend/search/edit/{id}", method = RequestMethod.GET)
    public String editAttendance(@PathVariable("id") final CouncilMeeting councilMeeting1, Model model) {
        CouncilMeeting councilMeeting = councilMeetingService.findOne(councilMeeting1.getId());

        if (councilMeeting != null && councilMeeting.getCommitteeType().getCommiteemembers().isEmpty()) {
            model.addAttribute(MESSAGE, "msg.committee.members.not.added");
            return COMMONERRORPAGE;
        }
        if (councilMeeting != null && councilMeeting.getStatus() != null
                && ATTENDANCEFINALIZED.equals(councilMeeting.getStatus().getCode())) {
            model.addAttribute(MESSAGE, MSG_ATTENDANCE_ALREADY_FINALIZD);
            return COMMONERRORPAGE;
        }

        buildAttendanceDetails(councilMeeting);
        councilMeetingService.sortMeetingMomByItemNumber(councilMeeting);
        model.addAttribute(COUNCIL_MEETING, councilMeeting);
        return COUNCILMEETING_EDIT_ATTENDANCE;
    }

    private void buildAttendanceDetails(CouncilMeeting councilMeeting) {
        if (councilMeeting != null && councilMeeting.getCommitteeType() != null) {
            List<MeetingAttendence> attendencesList = new ArrayList<>();
            List<Long> attendenceIdList = new ArrayList<>();

            for (MeetingAttendence meetingAttendance : councilMeeting.getMeetingAttendence()) {
                if (meetingAttendance.getAttendedMeeting())
                    attendenceIdList.add(meetingAttendance.getCouncilMember().getId());
            }
            for (CommitteeMembers committeeMembers : committeeMemberService
                    .findAllByCommitteTypeMemberIsActive(councilMeeting.getCommitteeType())) {
                MeetingAttendence attendence = new MeetingAttendence();
                attendence.setCouncilMember(committeeMembers.getCouncilMember());
                if (attendenceIdList.indexOf(committeeMembers.getCouncilMember().getId()) > -1) {
                    attendence.setAttendedMeeting(true);
                }
                attendencesList.add(attendence);
            }
            councilMeeting.setUpdateMeetingAttendance(attendencesList);
        }
    }

    @RequestMapping(value = "/attendance/update", method = RequestMethod.POST)
    public String updateAttendance(@Valid @ModelAttribute final CouncilMeeting councilMeeting, final BindingResult errors,
                                   final Model model, final RedirectAttributes redirectAttrs) {
        if (councilMeeting.getStatus() != null && ATTENDANCEFINALIZED.equals(councilMeeting.getStatus().getCode())) {
            model.addAttribute(MESSAGE, MSG_ATTENDANCE_ALREADY_FINALIZD);
            return COMMONERRORPAGE;
        }
        deleteAtteandance(councilMeeting);
        setAttendanceDetails(councilMeeting);
        if (errors.hasErrors()) {
            return "redirect:councilmeeting/attend/search/edit/" + councilMeeting.getId();
        }
        buildAttendanceDetailsForMeeting(councilMeeting);
        councilMeetingService.update(councilMeeting);
        redirectAttrs.addFlashAttribute(MESSAGE, messageSource.getMessage("msg.councilMeeting.attendance.success", null, null));
        return "redirect:result/" + councilMeeting.getId();
    }

    private void setAttendanceDetails(final CouncilMeeting councilMeeting) {
        councilMeeting.setMeetingAttendence(councilMeeting.getUpdateMeetingAttendance());
    }

    private void deleteAtteandance(final CouncilMeeting councilMeeting) {
        councilMeetingService.deleteAttendance(councilMeeting.getMeetingAttendence());
    }

    @RequestMapping(value = "/attendance/finalizeattendance", method = RequestMethod.POST)
    public String updateFinalizedAttendance(@Valid @ModelAttribute final CouncilMeeting councilMeeting,
                                            final BindingResult errors,
                                            final Model model, final RedirectAttributes redirectAttrs) {

        if (councilMeeting.getStatus() != null && ATTENDANCEFINALIZED.equals(councilMeeting.getStatus().getCode())) {
            model.addAttribute(MESSAGE, MSG_ATTENDANCE_ALREADY_FINALIZD);
            return COMMONERRORPAGE;
        }
        deleteAtteandance(councilMeeting);
        if (councilMeeting.getUpdateMeetingAttendance() != null) {
            setAttendanceDetails(councilMeeting);
        }
        if (errors.hasErrors()) {
            return "redirect:councilmeeting/attend/search/edit/" + councilMeeting.getId();
        }
        buildAttendanceDetailsForMeeting(councilMeeting);
        councilMeeting.setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(MEETING_MODULENAME, ATTENDANCEFINALIZED));
        councilMeetingService.update(councilMeeting);
        redirectAttrs.addFlashAttribute(MESSAGE, messageSource.getMessage("msg.councilMeeting.attendance.success", null, null));
        return "redirect:result/" + councilMeeting.getId();
    }

    private void buildAttendanceDetailsForMeeting(final CouncilMeeting councilMeeting) {
        for (MeetingAttendence attendence : councilMeeting
                .getMeetingAttendence()) {
            if (attendence.getChecked() != null && attendence.getChecked()) {
                attendence.setAttendedMeeting(true);
            } else {
                attendence.setAttendedMeeting(false);
            }
        }
    }

    @RequestMapping(value = "/downloadfile/{id}")
    @ResponseBody
    public ResponseEntity<InputStreamResource> download(@PathVariable("id") final Long id) {
        CouncilMeeting councilMeeting = councilMeetingService.findOne(id);
        if (councilMeeting != null) {
            if (councilMeeting.getFilestore() != null) {
                return fetchMeetingResolutionByFileStoreId(councilMeeting);
            } else {
                if (MOM_FINALISED.equals(councilMeeting.getStatus().getCode())) {
                    byte[] reportOutput = councilReportService.generatePDFForMom(councilMeeting);

                    if (reportOutput != null) {
                        councilMeeting.setFilestore(fileStoreService.store(FileUtils.byteArrayToFile(reportOutput, MEETINGRESOLUTIONFILENAME,"rtf" ).toFile(),
                                MEETINGRESOLUTIONFILENAME, APPLICATION_RTF, MODULE_NAME));
                        councilMeetingService.update(councilMeeting);
                    }

                    if (councilMeeting.getFilestore() != null) {
                        return fetchMeetingResolutionByFileStoreId(councilMeeting);
                    }
                }
            }
        }
        return ResponseEntity.notFound().build();
    }

    private ResponseEntity<InputStreamResource> fetchMeetingResolutionByFileStoreId(CouncilMeeting councilMeeting) {
        return fileStoreUtils.fileAsResponseEntity(councilMeeting.getFilestore().getFileStoreId(),
                MODULE_NAME, true);
    }

    @RequestMapping(value = "/attendance/ajaxsearch/{id}", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String ajaxsearc(@PathVariable("id") final CouncilMeeting id, Model model) {
        List<MeetingAttendence> searchResultList = councilMeetingService.findListOfAttendance(id);
        return new StringBuilder(DATA)
                .append(toJSON(searchResultList, MeetingAttendence.class, MeetingAttendanceJsonAdaptor.class)).append("}")
                .toString();
    }

    @RequestMapping(value = "/generateagenda/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> printAgendaDetails(@PathVariable("id") final Long id) {
        byte[] reportOutput;
        CouncilMeeting councilMeeting = councilMeetingService.findOne(id);
        reportOutput = councilReportService.generatePDFForAgendaDetails(councilMeeting);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(APPLICATION_RTF));
        headers.add("content-disposition", "inline;filename=meetingdetails.rtf");
        return new ResponseEntity<>(reportOutput, headers, HttpStatus.CREATED);

    }
    
    public Boolean isAutoMeetingNoGenEnabled() {
        return councilPreambleService.autoGenerationModeEnabled(
                CouncilConstants.MODULE_FULLNAME, MEETING_NUMBER_AUTO);
    }
}