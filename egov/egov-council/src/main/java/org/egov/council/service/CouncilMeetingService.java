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

package org.egov.council.service;

import static org.egov.council.utils.constants.CouncilConstants.ADJOURNED;
import static org.egov.council.utils.constants.CouncilConstants.AGENDAUSEDINMEETING;
import static org.egov.council.utils.constants.CouncilConstants.AGENDA_MODULENAME;
import static org.egov.council.utils.constants.CouncilConstants.ATTENDANCEFINALIZED;
import static org.egov.council.utils.constants.CouncilConstants.MEETINGCANCELLED;
import static org.egov.council.utils.constants.CouncilConstants.MEETINGSTATUSAPPROVED;
import static org.egov.council.utils.constants.CouncilConstants.MEETINGUSEDINRMOM;
import static org.egov.council.utils.constants.CouncilConstants.MEETING_MODULENAME;
import static org.egov.council.utils.constants.CouncilConstants.MOM_FINALISED;
import static org.egov.council.utils.constants.CouncilConstants.PREAMBLE_MODULENAME;
import static org.egov.council.utils.constants.CouncilConstants.RESOLUTION_APPROVED_PREAMBLE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.ArrayUtils;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.council.autonumber.PreambleNumberGenerator;
import org.egov.council.entity.CouncilAgendaDetails;
import org.egov.council.entity.CouncilMeeting;
import org.egov.council.entity.MeetingAttendence;
import org.egov.council.entity.MeetingMOM;
import org.egov.council.entity.enums.PreambleType;
import org.egov.council.repository.CouncilMeetingRepository;
import org.egov.council.repository.CouncilMoMRepository;
import org.egov.council.repository.MeetingAttendanceRepository;
import org.egov.council.utils.constants.CouncilConstants;
import org.egov.eis.service.EisCommonService;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.UserService;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.filestore.entity.FileStoreMapper;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.utils.DateUtils;
import org.egov.infra.utils.autonumber.AutonumberServiceBeanResolver;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
public class CouncilMeetingService {

    private static final String STATUS_DOT_CODE = "status.code";
    private final CouncilMeetingRepository councilMeetingRepository;
    private final MeetingAttendanceRepository meetingAttendanceRepository;
    
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private EgwStatusHibernateDAO egwStatusHibernateDAO;
    @Autowired
    private CouncilMoMRepository councilMoMRepository;
    @Autowired
    private EisCommonService eisCommonService;
    @Autowired
    private UserService userService;
    @Autowired
    private FileStoreService fileStoreService;
    @Autowired
    private AutonumberServiceBeanResolver autonumberServiceBeanResolver;

    @Autowired
    public CouncilMeetingService(CouncilMeetingRepository councilMeetingRepository,
            MeetingAttendanceRepository meetingAttendance) {
        this.councilMeetingRepository = councilMeetingRepository;
        this.meetingAttendanceRepository = meetingAttendance;
    }

    public Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }

    @Transactional
    public CouncilMeeting create(final CouncilMeeting councilMeeting) {
        return councilMeetingRepository.save(councilMeeting);
    }

    @Transactional
    public CouncilMeeting update(final CouncilMeeting councilMeeting) {
        return councilMeetingRepository.save(councilMeeting);
    }
    
    @Transactional
    public List<MeetingMOM> createDataEntry(final List<MeetingMOM> meetingMOM) {
        return councilMoMRepository.save(meetingMOM);
    }
    
    @Transactional
    public void deleteMeetingMoms(final List<MeetingMOM> meetingMOM) {
        councilMoMRepository.deleteInBatch(meetingMOM);
    }

    public List<CouncilMeeting> findAll() {
        return councilMeetingRepository.findAll(new Sort(Sort.Direction.DESC, "meetingDate"));
    }

    public CouncilMeeting findOne(Long id) {
        return councilMeetingRepository.findById(id);
    }

    public List<MeetingAttendence> findListOfAttendance(CouncilMeeting id) {
        return meetingAttendanceRepository.findByMeeting(id);
    }
    
    public CouncilMeeting findByMeetingNumber(String meetingNumber) {
        return councilMeetingRepository.findByMeetingNumber(meetingNumber);
    }
    
    public MeetingMOM findByResolutionNumber(String resolutionNumber) {
        return councilMoMRepository.findByResolutionNumber(resolutionNumber);
    }
    
    public MeetingMOM findByResolutionNumberAndPreamble(String resolutionNumber, Long preambleId) {
        return councilMoMRepository.findByResolutionNumberAndPreamble(resolutionNumber, preambleId);
    }

    public CouncilMeeting updateMoMStatus(CouncilMeeting councilMeeting) {
        for (MeetingMOM meetingMOM : councilMeeting.getMeetingMOMs()) {

            if (meetingMOM.getResolutionStatus().getCode().equals(ADJOURNED))
                meetingMOM.getPreamble()
                        .setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(PREAMBLE_MODULENAME, ADJOURNED));
        }
        return councilMeeting;
    }

    @SuppressWarnings("unchecked")
    public List<CouncilMeeting> searchMeetingToCreateMOM(CouncilMeeting councilMeeting) {
        return buildSearchCriteria(councilMeeting)
                .add(Restrictions.in(STATUS_DOT_CODE, MEETINGSTATUSAPPROVED,ATTENDANCEFINALIZED)).list();
    }

    @SuppressWarnings("unchecked")
    public List<CouncilMeeting> searchMeeting(CouncilMeeting councilMeeting) {
        return buildSearchCriteria(councilMeeting).add(Restrictions.ne(STATUS_DOT_CODE, MEETINGCANCELLED)).list();
    }
    
    @SuppressWarnings("unchecked")
    public List<CouncilMeeting> searchMeetingForEdit(CouncilMeeting councilMeeting) {
        return buildSearchCriteria(councilMeeting).add(Restrictions.in(STATUS_DOT_CODE, MEETINGSTATUSAPPROVED,ATTENDANCEFINALIZED)).list();
    }
    
    @SuppressWarnings("unchecked")
    public List<CouncilMeeting> searchMeetingWithMomCreatedStatus(CouncilMeeting councilMeeting) {
        return buildSearchCriteria(councilMeeting)
                .add(Restrictions.in(STATUS_DOT_CODE, MEETINGUSEDINRMOM)).list();
    }

    public void sortMeetingMomByItemNumber(CouncilMeeting councilMeeting) {
        councilMeeting.getMeetingMOMs().sort((MeetingMOM f1, MeetingMOM f2) -> Long.valueOf(f1.getItemNumber()).compareTo(Long.valueOf(f2.getItemNumber())));
    }
    
    public Criteria buildSearchCriteria(CouncilMeeting councilMeeting) {
        final Criteria criteria = getCurrentSession().createCriteria(CouncilMeeting.class, "councilMeeting")
                .createAlias("councilMeeting.status", "status");
        if (councilMeeting.getCommitteeType() != null)
            criteria.add(Restrictions.eq("committeeType", councilMeeting.getCommitteeType()));
        
        if (councilMeeting.getMeetingNumber() != null)
            criteria.add(Restrictions.eq("meetingNumber", councilMeeting.getMeetingNumber()));

        if (councilMeeting.getFromDate() != null && councilMeeting.getToDate() != null) {
            criteria.add(Restrictions.between("meetingDate", councilMeeting.getFromDate(),
                    DateUtils.addDays(councilMeeting.getToDate(), 1)));
        }
        if(councilMeeting.getMeetingType()!=null)
            criteria.add(Restrictions.eq("meetingType", councilMeeting.getMeetingType()));
        return criteria;
    }
    @Transactional
    public void deleteAttendance(final List<MeetingAttendence> meetingAttendences) {
        meetingAttendanceRepository.deleteInBatch(meetingAttendences);
    }
    
    /***
     * Get the preambles creator and approver and meeting creator details
     * to send ams and email.
     * @param councilMeeting
     * @return
     */
    public List<User> getUserListForMeeting(CouncilMeeting councilMeeting) {
        Set<User> usersListResult = new HashSet<>();
        List<String> agendaNumber = new ArrayList<>();
        for (MeetingMOM mom : councilMeeting.getMeetingMOMs()) {
            if (mom != null && mom.getAgenda() != null && !agendaNumber.contains(mom.getAgenda().getAgendaNumber())
                    && mom.getAgenda().getAgendaDetails() != null) {
                for (CouncilAgendaDetails agendaDetails : mom.getAgenda().getAgendaDetails()) {
                    if (agendaDetails != null && agendaDetails.getPreamble() != null
                            && agendaDetails.getPreamble().getState() != null
                            && agendaDetails.getPreamble().getState().getOwnerPosition() != null) {
                        usersListResult.add(agendaDetails.getPreamble().getState().getCreatedBy());
                        usersListResult.add(eisCommonService
                                .getUserForPosition(agendaDetails.getPreamble().getState().getOwnerPosition().getId(),
                                        new Date()));
                    }
                }
                agendaNumber.add(mom.getAgenda().getAgendaNumber());
            }
        }
        usersListResult.add(userService.getUserById(councilMeeting.getCreatedBy().getId()));
        return new ArrayList<>(usersListResult);
    }
    
    public Set<FileStoreMapper> addToFileStore(final MultipartFile[] files) {
        if (ArrayUtils.isNotEmpty(files))
            return Arrays.asList(files).stream().filter(file -> !file.isEmpty()).map(file -> {
                try {
                    return fileStoreService.store(file.getInputStream(), file.getOriginalFilename(),
                            file.getContentType(), CouncilConstants.MODULE_NAME);
                } catch (final Exception e) {
                    throw new ApplicationRuntimeException("Error occurred while getting inputstream", e);
                }
            }).collect(Collectors.toSet());
        else
            return Collections.emptySet();
}
    public void prepareMeetingMomData(final MeetingMOM meetingMOM, List<MeetingMOM> meetingMOMList,
            List<CouncilAgendaDetails> preambleList,boolean autoPreambleNoGenEnabled) {
        for (MeetingMOM meetingMoMs : meetingMOM.getMeeting().getMeetingMOMs()) {
            meetingMoMs.getPreamble().setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(
                    PREAMBLE_MODULENAME, RESOLUTION_APPROVED_PREAMBLE));
            meetingMoMs.getPreamble().setType(PreambleType.GENERAL);
            meetingMoMs.setMeeting(meetingMOM.getMeeting());
            if (autoPreambleNoGenEnabled){
                PreambleNumberGenerator preamblenumbergenerator = autonumberServiceBeanResolver
                        .getAutoNumberServiceFor(PreambleNumberGenerator.class);
                meetingMoMs.getPreamble().setPreambleNumber(preamblenumbergenerator
                        .getNextNumber(meetingMoMs.getPreamble()));
                }
            meetingMoMs.getMeeting().setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(MEETING_MODULENAME, MOM_FINALISED));
            meetingMoMs.getMeeting().setCommitteeType(meetingMOM.getAgenda().getCommitteeType());
            meetingMoMs.setAgenda(meetingMOM.getAgenda());
            meetingMoMs.getAgenda()
                    .setStatus(egwStatusHibernateDAO.getStatusByModuleAndCode(AGENDA_MODULENAME, AGENDAUSEDINMEETING));
            meetingMoMs.setLegacy(true);
            CouncilAgendaDetails councilAgendaDetails = new CouncilAgendaDetails();
            councilAgendaDetails.setAgenda(meetingMOM.getAgenda());
            councilAgendaDetails.setPreamble(meetingMoMs.getPreamble());
            councilAgendaDetails.setItemNumber(meetingMoMs.getItemNumber());
            preambleList.add(councilAgendaDetails);
            meetingMOMList.add(meetingMoMs);
        }
    }
}