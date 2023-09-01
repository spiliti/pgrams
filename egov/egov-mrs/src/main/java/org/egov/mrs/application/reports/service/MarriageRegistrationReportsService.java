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

package org.egov.mrs.application.reports.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.egov.infra.admin.master.service.CityService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.config.persistence.datasource.routing.annotation.ReadOnly;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.reporting.engine.ReportFormat;
import org.egov.infra.reporting.engine.ReportOutput;
import org.egov.infra.reporting.engine.ReportRequest;
import org.egov.infra.reporting.engine.ReportService;
import org.egov.mrs.domain.entity.MarriageCertificate;
import org.egov.mrs.domain.entity.MarriageRegistration;
import org.egov.mrs.domain.entity.ReIssue;
import org.egov.mrs.domain.entity.SearchModel;
import org.egov.mrs.domain.entity.SearchResult;
import org.egov.mrs.domain.enums.MaritalStatus;
import org.egov.mrs.domain.enums.MarriageCertificateType;
import org.egov.mrs.entity.es.MarriageRegistrationIndex;
import org.egov.mrs.masters.entity.MarriageRegistrationUnit;
import org.egov.mrs.masters.entity.MarriageReligion;
import org.egov.mrs.masters.service.ReligionService;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MarriageRegistrationReportsService {

    private static final String WIFE = "wife";
    private static final String ALL = "ALL";
    private static final String APPLICATIONDATE_BETWEEN_CONDITION = " and applicationdate between to_timestamp(:fromdate,'yyyy-MM-dd HH24:mi:ss') and to_timestamp(:todate,'YYYY-MM-DD HH24:MI:SS') ";
    private static final String MARRIAGE_REGISTRATION_DOT_HUSBAND = "marriageRegistration.husband";
    private static final String DOT_MARRIAGE_REGISTRATION_UNIT = ".marriageRegistrationUnit";
    private static final String MARRIAGE_REGISTRATION_DOT_WIFE = "marriageRegistration.wife";
    private static final String REGISTRATIONUNIT_WHERE_QUERY = " and registrationunit=to_number(:regunit,'999999')";
    private static final String STATUS_DOT_CODE = "status.code";
    private static final String MARRIAGE_REGISTRATION_STATUS = "marriageRegistration.status";
    private static final String STATUS = "status";
    private static final String MARRIAGE_REGISTRATION_UNIT_DOT_ID = "marriageRegistrationUnit.id";
    private static final String YYYY_MM_DD = "yyyy/MM/dd";
    private static final String MARRIAGE_REGISTRATION_APPLICATION_DATE = "marriageRegistration.applicationDate";
    private static final String TODATE = "todate";
    private static final String FROMDATE = "fromdate";
    private static final String REG_ZONE_WHERE_CONDITION = " and reg.zone=to_number(:zone,'999999')";
    private static final String MARRIAGE_REGISTRATION_UNIT = "marriageRegistrationUnit";
    private static final String INPUTYEAR = "year";
    private static final String REGUNIT = "regunit";
    private static final String ZONE_ID = "zone.id";
    private static final String TO_DATE = "toDate";
    private static final String ZONE = "zone";
    private static final String FROM_DATE = "fromDate";
    private static final String HUSBAND = "husband";
    private static final String MARRIAGE_REGISTRATION = "marriageRegistration";
    private static final String UNION = " union ";

    private final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ReportService reportService;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    
    @Autowired
    private ReligionService religionService;
    @Autowired
    private CityService cityService;

    private Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }

    public Date resetFromDateTimeStamp(final Date date) {
        final Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        return cal1.getTime();
    }

    public Date resetToDateTimeStamp(final Date date) {
        final Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        cal1.set(Calendar.HOUR_OF_DAY, 23);
        cal1.set(Calendar.MINUTE, 59);
        cal1.set(Calendar.SECOND, 59);
        cal1.set(Calendar.MILLISECOND, 999);
        return cal1.getTime();
    }

    public Date getMonthStartday(final String monthyear) {
        Date monthStartDate = new Date();
        if (monthyear != null) {
            final String[] monthYear = monthyear.split("/");
            final Calendar calnew = Calendar.getInstance();
            calnew.set(Calendar.MONTH, Integer.parseInt(monthYear[0]) - 1);
            calnew.set(Calendar.YEAR, Integer.parseInt(monthYear[1]));
            calnew.set(Calendar.HOUR_OF_DAY, 0);
            calnew.set(Calendar.MINUTE, 0);
            calnew.set(Calendar.SECOND, 0);
            calnew.set(Calendar.DAY_OF_MONTH,
                    calnew.getActualMinimum(Calendar.DAY_OF_MONTH));
            monthStartDate = calnew.getTime();

        }
        return monthStartDate;
    }

    public Date getMonthEndday(final String monthyear) {
        Date monthEndDate = new Date();
        if (monthyear != null) {
            final String[] monthYear = monthyear.split("/");
            final Calendar calnew = Calendar.getInstance();
            calnew.set(Calendar.MONTH, Integer.parseInt(monthYear[0]) - 1);
            calnew.set(Calendar.YEAR, Integer.parseInt(monthYear[1]));
            calnew.set(Calendar.HOUR_OF_DAY, 23);
            calnew.set(Calendar.MINUTE, 59);
            calnew.set(Calendar.SECOND, 59);
            calnew.set(Calendar.MILLISECOND, 999);
            calnew.set(Calendar.DAY_OF_MONTH,
                    calnew.getActualMaximum(Calendar.DAY_OF_MONTH));
            monthEndDate = calnew.getTime();

        }
        return monthEndDate;
    }

    @ReadOnly
    @SuppressWarnings("unchecked")
    public List<Object[]> searchMarriageRegistrationsForCertificateReport(final MarriageCertificate certificate) {

        final Map<String, String> params = new HashMap<>();

        final StringBuilder queryStrForRegistration = new StringBuilder(1000);
        queryStrForRegistration
                .append(
                        "Select reg.registrationno,reg.dateofmarriage,reg.applicationdate,reg.rejectionreason,cert.certificateno,cert.certificatetype,cert.certificatedate, brndy.name,(Select concat(concat(concat(app.firstname, ' '), app.middlename, ' '), app.lastname) as hus_name from egmrs_applicant app where app.id = reg.husband),(Select concat(concat(concat(app.firstname, ' '), app.middlename, ' '), app.lastname) as wife_name from egmrs_applicant app where app.id = reg.wife),reg.id "
                                + " from egmrs_registration reg, egmrs_certificate cert, eg_boundary brndy,egw_status st"
                                + " where reg.zone = brndy.id and reg.status = st.id and st.code in('REGISTERED')  and reg.id = cert.registration and cert.reissue is null ");
        if (certificate.getRegistration().getZone() != null) {
            queryStrForRegistration.append(REG_ZONE_WHERE_CONDITION);
            params.put(ZONE, String.valueOf(certificate.getRegistration().getZone().getId()));
        }

        if (certificate.getCertificateType() != null && Arrays.stream(MarriageCertificateType.values())
                .anyMatch(mrgCertiType -> mrgCertiType.name().equals(certificate.getCertificateType().name()))) {
            queryStrForRegistration.append(" and cert.certificatetype=:certificatetype");
            params.put("certificatetype", certificate.getCertificateType().name());
        } else
            queryStrForRegistration.append(" and cert.certificatetype in('REGISTRATION','REISSUE','REJECTION')");

        if (certificate.getFromDate() != null) {
            queryStrForRegistration
                    .append(
                            " and cert.certificatedate >= to_timestamp(:fromDate,'yyyy-MM-dd HH24:mi:ss')");
            params.put(FROM_DATE, sf.format(resetFromDateTimeStamp(certificate.getFromDate())));
        }

        if (certificate.getToDate() != null) {
            queryStrForRegistration
                    .append(
                            " and cert.certificatedate <= to_timestamp(:toDate,'YYYY-MM-DD HH24:MI:SS')");
            params.put(TO_DATE, sf.format(resetToDateTimeStamp(certificate.getToDate())));
        }

        if (certificate.getRegistration().getRegistrationNo() != null) {
            queryStrForRegistration.append(" and reg.registrationno=:registrationNo");
            params.put("registrationNo", certificate.getRegistration().getRegistrationNo());
        }

        final StringBuilder queryStrForReissue = new StringBuilder(1100);
        queryStrForReissue
                .append(
                        "Select reg.registrationno,reg.dateofmarriage,reg.applicationdate,reg.rejectionreason,cert.certificateno,cert.certificatetype,cert.certificatedate, brndy.name,(Select concat(concat(concat(app.firstname, ' '), app.middlename, ' '), app.lastname) as hus_name from egmrs_applicant app where app.id = reg.husband),(Select concat(concat(concat(app.firstname, ' '), app.middlename, ' '), app.lastname) as wife_name from egmrs_applicant app where app.id = reg.wife),reg.id"
                                + " from egmrs_registration reg,egmrs_reissue reis, egmrs_certificate cert, eg_boundary brndy,egw_status st "
                                + " where reg.zone = brndy.id and reg.id=reis.registration and reis.status = st.id and st.code in('CERTIFICATEREISSUED','REJECTION')  and reis.id = cert.reissue and cert.registration is null");
        if (certificate.getRegistration().getZone() != null) {
            queryStrForReissue.append(REG_ZONE_WHERE_CONDITION);
            params.put(ZONE, String.valueOf(certificate.getRegistration().getZone().getId()));
        }

        if (certificate.getCertificateType() != null && Arrays.stream(MarriageCertificateType.values())
                .anyMatch(mrgCertiType -> mrgCertiType.name().equals(certificate.getCertificateType().name()))) {
            queryStrForReissue.append(" and cert.certificatetype=:certificatetype");
            params.put("certificatetype", certificate.getCertificateType().name());
        } else
            queryStrForReissue.append(" and cert.certificatetype in('REGISTRATION','REISSUE','REJECTION')");

        if (certificate.getFromDate() != null) {
            queryStrForReissue
                    .append(
                            " and cert.certificatedate >= to_timestamp(:fromDate,'yyyy-MM-dd HH24:mi:ss')");
            params.put(FROM_DATE, sf.format(resetFromDateTimeStamp(certificate.getFromDate())));
        }

        if (certificate.getToDate() != null) {
            queryStrForReissue
                    .append(
                            " and cert.certificatedate <= to_timestamp(:toDate,'YYYY-MM-DD HH24:MI:SS')");
            params.put(TO_DATE, sf.format(resetToDateTimeStamp(certificate.getToDate())));
        }

        if (certificate.getRegistration().getRegistrationNo() != null) {
            queryStrForReissue.append(" and reg.registrationno=:registrationNo");
            params.put("registrationNo", certificate.getRegistration().getRegistrationNo());
        }

        final StringBuilder aggregateQueryStr = new StringBuilder();
        aggregateQueryStr.append(queryStrForRegistration.toString());
        aggregateQueryStr.append(UNION);
        aggregateQueryStr.append(queryStrForReissue.toString());

        final org.hibernate.Query query = getCurrentSession().createSQLQuery(aggregateQueryStr.toString());
        for (final Map.Entry<String, String> param : params.entrySet())
            query.setParameter(param.getKey(), param.getValue());
        return query.list();
    }

    @ReadOnly
    @SuppressWarnings("unchecked")
    public List<String[]> searchRegistrationOfHusbandAgeWise(final int year, final MarriageRegistration registration) {

        final Map<String, Integer> params = new HashMap<>();
        final StringBuilder queryForHusband = new StringBuilder(1000);
        queryForHusband
                .append(
                        "(Select ap.ageinyears , count(*) from egmrs_registration rg,egmrs_applicant ap,egmrs_registrationunit  ru,egw_status st"
                                + " where rg.husband=ap.id and  rg.registrationunit=ru.id and rg.status = st.id and  st.code='REGISTERED'"
                                + " and extract( year from rg.applicationdate)=:year ");
        params.put(INPUTYEAR, year);

        if (registration.getMarriageRegistrationUnit().getId() != null) {
            queryForHusband
                    .append(" and  rg.registrationunit=:regunit ");
            params.put(REGUNIT, registration.getMarriageRegistrationUnit().getId().intValue());
        }
        if (registration.getZone().getId() != null) {
            queryForHusband
                    .append(" and  rg.zone=:zone ");
            params.put(ZONE, registration.getZone().getId().intValue());
        }
        queryForHusband.append("group by ap.ageinyears order by ap.ageinyears )");

        final org.hibernate.Query query = getCurrentSession().createSQLQuery(queryForHusband.toString());
        for (final Map.Entry<String, Integer> param : params.entrySet())
            query.setParameter(param.getKey(), param.getValue());
        return query.list();

    }

    @ReadOnly
    @SuppressWarnings("unchecked")
    public List<String[]> searchRegistrationOfWifeAgeWise(final int year, final MarriageRegistration registration) {

        final Map<String, Integer> params = new HashMap<>();
        final StringBuilder queryForWife = new StringBuilder(1000);
        queryForWife
                .append(
                        "(Select ap.ageinyears , count(*) from egmrs_registration rg,egmrs_applicant ap,egmrs_registrationunit  ru,egw_status st"
                                + " where rg.wife=ap.id and  rg.registrationunit=ru.id and rg.status = st.id and  st.code='REGISTERED'"
                                + " and  extract( year from rg.applicationdate)=:year ");
        params.put(INPUTYEAR, year);

        if (registration.getMarriageRegistrationUnit().getId() != null) {
            queryForWife
                    .append(" and  rg.registrationunit=:regunit ");
            params.put(REGUNIT, registration.getMarriageRegistrationUnit().getId().intValue());

        }
        if (registration.getZone().getId() != null) {
            queryForWife
                    .append(" and  rg.zone=:zone ");
            params.put(ZONE, registration.getZone().getId().intValue());

        }
        queryForWife.append("group by ap.ageinyears order by ap.ageinyears )");

        final org.hibernate.Query query = getCurrentSession().createSQLQuery(queryForWife.toString());
        for (final Map.Entry<String, Integer> param : params.entrySet())
            query.setParameter(param.getKey(), param.getValue());
        return query.list();
    }

    @ReadOnly
    @SuppressWarnings("unchecked")
    public List<String[]> searchRegistrationActWise(final MarriageRegistration registration, final int year){

        final Map<String, Integer> params = new HashMap<>();
        final StringBuilder queryForAct = new StringBuilder(700);
        queryForAct
                .append("(select extract( month from reg.applicationdate) as Month,count(*)  from egmrs_registration  reg, egmrs_act  act, egw_status  status ,egmrs_registrationunit ru,eg_boundary b "
                        + "where reg.marriageact=act.id and  reg.registrationunit=ru.id and reg.status = status.id and status.code in('REGISTERED')  and reg.zone=b.id and "
                        + "reg.marriageact=:act and extract( year from reg.applicationdate)=:year ");
        params.put("act", registration.getMarriageAct().getId().intValue());
        params.put(INPUTYEAR, year);
        if (registration.getMarriageRegistrationUnit().getId() != null) {
            queryForAct
                    .append(" and  reg.registrationunit=:regunit ");
            params.put(REGUNIT, registration.getMarriageRegistrationUnit().getId().intValue());

        }
        if (registration.getZone().getId() != null) {
            queryForAct
                    .append(" and reg.zone=:zone ");
            params.put(ZONE, registration.getZone().getId().intValue());

        }
        queryForAct.append("group by Month)");

        final org.hibernate.Query query = getCurrentSession().createSQLQuery(queryForAct.toString());
        for (final Map.Entry<String, Integer> param : params.entrySet())
            query.setParameter(param.getKey(), param.getValue());
        return query.list();
    }

    @ReadOnly
    @SuppressWarnings("unchecked")
    public List<MarriageRegistration> getAgewiseDetails(final MarriageRegistration registration, final String regunit,
            final String age, final int year, final String applicant)
            throws ParseException {
        final Criteria criteria = getCurrentSession().createCriteria(MarriageRegistration.class,
                MARRIAGE_REGISTRATION);
        final String[] values = age.split("-");

        final SimpleDateFormat formatter = new SimpleDateFormat(YYYY_MM_DD);
        final Date fromDate = formatter.parse(year + "/" + 1 + "/" + 1);
        final Date toDate = formatter.parse(year + "/" + 12 + "/" + 31);

        if (HUSBAND.equals(applicant)) {
            criteria.createAlias(MARRIAGE_REGISTRATION_DOT_HUSBAND, HUSBAND).add(Restrictions
                    .between("husband.ageInYearsAsOnMarriage", Integer.valueOf(values[0]), Integer.valueOf(values[1])));
            buildAgeWiseSearchCriteria(registration, regunit, criteria, fromDate, toDate);
        } else {
            criteria.createAlias(MARRIAGE_REGISTRATION_DOT_WIFE, WIFE).add(Restrictions
                    .between("wife.ageInYearsAsOnMarriage", Integer.valueOf(values[0]), Integer.valueOf(values[1])));
            buildAgeWiseSearchCriteria(registration, regunit, criteria, fromDate, toDate);
        }

        criteria.createAlias(MARRIAGE_REGISTRATION_STATUS, STATUS)
                .add(Restrictions.in(STATUS_DOT_CODE, MarriageRegistration.RegistrationStatus.REGISTERED.name()));
        return criteria.list();
    }

    private void buildAgeWiseSearchCriteria(final MarriageRegistration registration, final String regunit,
            final Criteria criteria, final Date fromDate, final Date toDate) {
        if (fromDate != null)
            criteria.add(Restrictions.ge(MARRIAGE_REGISTRATION_APPLICATION_DATE, fromDate));
        if (toDate != null)
            criteria.add(Restrictions.le(MARRIAGE_REGISTRATION_APPLICATION_DATE, toDate));
        if (regunit != null)
            criteria.createAlias(MARRIAGE_REGISTRATION + DOT_MARRIAGE_REGISTRATION_UNIT, MARRIAGE_REGISTRATION_UNIT)
                    .add(Restrictions.eq(MARRIAGE_REGISTRATION_UNIT_DOT_ID,
                            Long.parseLong(regunit)));
        if (null != registration.getZone()
                && registration.getZone().getId() != null)
            criteria.add(Restrictions.eq(ZONE_ID, registration.getZone()
                    .getId()));
    }

    @ReadOnly
    @SuppressWarnings("unchecked")
    public List<MarriageRegistration> searchStatusAtTimeOfMarriage(final MarriageRegistration registration) {
        final Criteria criteria = getCurrentSession().createCriteria(MarriageRegistration.class, MARRIAGE_REGISTRATION)
                .createAlias(MARRIAGE_REGISTRATION_STATUS, STATUS);
        if (registration.getHusband().getMaritalStatus() != null)
            criteria.createAlias(MARRIAGE_REGISTRATION_DOT_HUSBAND, HUSBAND)
                    .add(Restrictions.eq("husband.maritalStatus", registration.getHusband().getMaritalStatus()));
        criteria.createAlias(MARRIAGE_REGISTRATION_DOT_WIFE, WIFE)
                .add(Restrictions.eq("wife.maritalStatus", registration.getHusband().getMaritalStatus()));

        criteria.add(Restrictions.in(STATUS_DOT_CODE, MarriageRegistration.RegistrationStatus.REGISTERED.name()));
        return criteria.list();
    }

    @ReadOnly
    @SuppressWarnings("unchecked")
    public List<String[]> getHusbandCountByMaritalStatus(final Date fromDate, final Date toDate, final String maritalStatus,
            final MarriageRegistration registration) {
        final Map<String, String> params = new HashMap<>();
        final StringBuilder queryStrForHusbandCount = new StringBuilder(600);
        queryStrForHusbandCount
                .append(
                        "select app.relationstatus,to_char(app.createddate,'Mon'),count(*) from egmrs_applicant as app ,egmrs_registration as reg where reg.husband = app.id  ");
        if (maritalStatus != null) {
            queryStrForHusbandCount.append(" and app.relationstatus=:maritalStatus");
            params.put("maritalStatus", maritalStatus);
        }
        if (fromDate != null) {
            queryStrForHusbandCount
                    .append(
                            " and app.createddate >= to_timestamp(:fromDate,'yyyy-MM-dd HH24:mi:ss') ");
            params.put(FROM_DATE, sf.format(resetFromDateTimeStamp(fromDate)));
        }

        if (toDate != null) {
            queryStrForHusbandCount
                    .append(
                            " and app.createddate <= to_timestamp(:toDate,'YYYY-MM-DD HH24:MI:SS')");
            params.put(TO_DATE, sf.format(resetToDateTimeStamp(toDate)));
        }
        if (registration.getMarriageRegistrationUnit().getId() != null) {
            queryStrForHusbandCount
                    .append(" and reg.registrationunit=to_number(:regunit,'999999')");
            params.put(REGUNIT, registration.getMarriageRegistrationUnit().getId().toString());
        }
        if (registration.getZone().getId() != null) {
            queryStrForHusbandCount
                    .append(REG_ZONE_WHERE_CONDITION);
            params.put(ZONE, registration.getZone().getId().toString());
        }

        queryStrForHusbandCount.append(
                " group by app.relationstatus, to_char(app.createddate,'Mon') order by to_char(app.createddate,'Mon') desc");
        final org.hibernate.Query query = getCurrentSession().createSQLQuery(queryStrForHusbandCount.toString());
        for (final Map.Entry<String, String> param : params.entrySet())
            query.setParameter(param.getKey(), param.getValue());
        return query.list();
    }

    @SuppressWarnings("unchecked")
    public List<String[]> getWifeCountByMaritalStatus(final Date fromDate, final Date toDate, final String maritalStatus,
            final MarriageRegistration registration) {

        final Map<String, String> params = new HashMap<>();
        final StringBuilder queryStrForWifeCount = new StringBuilder(600);
        queryStrForWifeCount
                .append(
                        "select app.relationstatus,to_char(app.createddate,'Mon'),count(*) from egmrs_applicant as app ,egmrs_registration as reg where reg.wife = app.id  ");
        if (maritalStatus != null) {
            queryStrForWifeCount.append(" and app.relationstatus=:maritalStatus");
            params.put("maritalStatus", maritalStatus);
        }
        if (fromDate != null) {
            queryStrForWifeCount
                    .append(
                            " and app.createddate >= to_timestamp(:fromDate,'yyyy-MM-dd HH24:mi:ss')");
            params.put(FROM_DATE, sf.format(resetFromDateTimeStamp(fromDate)));
        }

        if (toDate != null) {
            queryStrForWifeCount
                    .append(
                            " and app.createddate <= to_timestamp(:toDate,'YYYY-MM-DD HH24:MI:SS')");
            params.put(TO_DATE, sf.format(resetToDateTimeStamp(toDate)));
        }
        if (registration.getMarriageRegistrationUnit().getId() != null) {
            queryStrForWifeCount
                    .append(REGISTRATIONUNIT_WHERE_QUERY);
            params.put(REGUNIT, registration.getMarriageRegistrationUnit().getId().toString());
        }
        if (registration.getZone().getId() != null) {
            queryStrForWifeCount
                    .append(" and zone=to_number(:zone,'999999')");
            params.put(ZONE, registration.getZone().getId().toString());
        }
        queryStrForWifeCount.append(
                " group by app.relationstatus, to_char(app.createddate,'Mon') order by to_char(app.createddate,'Mon') desc");
        final org.hibernate.Query query = getCurrentSession().createSQLQuery(queryStrForWifeCount.toString());
        for (final Map.Entry<String, String> param : params.entrySet())
            query.setParameter(param.getKey(), param.getValue());
        return query.list();

    }

    @ReadOnly
    @SuppressWarnings("unchecked")
    public List<MarriageRegistration> getByMaritalStatusDetails(final MarriageRegistration registration, final String regunit,
            final String applicant,
            final String maritalStatus, final Date fromDate, final Date toDate) {
        Criteria criteria = getCurrentSession().createCriteria(MarriageRegistration.class,
                MARRIAGE_REGISTRATION);

        if (maritalStatus != null && HUSBAND.equals(applicant)) {
            criteria = criteria.createAlias(MARRIAGE_REGISTRATION + ".husband", HUSBAND);
            if (fromDate != null)
                criteria.add(Restrictions.ge("husband.createdDate", resetFromDateTimeStamp(fromDate)));

            if (toDate != null)
                criteria.add(Restrictions.le("husband.createdDate", resetToDateTimeStamp(toDate)));
            if (regunit != null)
                criteria.createAlias(MARRIAGE_REGISTRATION + DOT_MARRIAGE_REGISTRATION_UNIT, MARRIAGE_REGISTRATION_UNIT)
                        .add(Restrictions.eq(MARRIAGE_REGISTRATION_UNIT_DOT_ID,
                                Long.parseLong(regunit)));
            if (null != registration.getZone()
                    && registration.getZone().getId() != null)
                criteria.add(Restrictions.eq(ZONE_ID, registration.getZone()
                        .getId()));

            criteria.add(Restrictions.eq("husband.maritalStatus", MaritalStatus.valueOf(maritalStatus)));
        } else {
            criteria = criteria.createAlias(MARRIAGE_REGISTRATION + ".wife", WIFE);
            if (fromDate != null)
                criteria.add(Restrictions.ge("wife.createdDate", resetFromDateTimeStamp(fromDate)));
            if (toDate != null)
                criteria.add(Restrictions.le("wife.createdDate", resetToDateTimeStamp(toDate)));
            if (maritalStatus != null)
                criteria.add(Restrictions.eq("wife.maritalStatus", MaritalStatus.valueOf(maritalStatus)));
            if (regunit != null)
                criteria.createAlias(MARRIAGE_REGISTRATION + DOT_MARRIAGE_REGISTRATION_UNIT, MARRIAGE_REGISTRATION_UNIT)
                        .add(Restrictions.eq(MARRIAGE_REGISTRATION_UNIT_DOT_ID,
                                Long.parseLong(regunit)));
            if (null != registration.getZone()
                    && registration.getZone().getId() != null)
                criteria.add(Restrictions.eq(ZONE_ID, registration.getZone()
                        .getId()));
        }

        return criteria.list();
    }

    @ReadOnly
    @SuppressWarnings("unchecked")
    public List<MarriageRegistration> searchRegistrationBydate(
            final MarriageRegistration registration) {
        final Criteria criteria = getCurrentSession().createCriteria(
                MarriageRegistration.class, MARRIAGE_REGISTRATION);

        if (null != registration.getMarriageRegistrationUnit()
                && registration.getMarriageRegistrationUnit().getId() != null)
            criteria.add(Restrictions.eq(MARRIAGE_REGISTRATION_UNIT_DOT_ID,
                    registration.getMarriageRegistrationUnit().getId()));
        if (null != registration.getZone()
                && registration.getZone().getId() != null)
            criteria.add(Restrictions.eq(ZONE_ID, registration.getZone()
                    .getId()));
        if (null != registration.getStatus()
                && registration.getStatus().getCode() != null)
            criteria.createAlias(MARRIAGE_REGISTRATION_STATUS, STATUS).add(
                    Restrictions.eq(STATUS_DOT_CODE, registration.getStatus()
                            .getCode()));
        if (registration.getFromDate() != null)
            criteria.add(Restrictions.ge(
                    MARRIAGE_REGISTRATION_APPLICATION_DATE,
                    resetFromDateTimeStamp(registration.getFromDate())));
        if (registration.getToDate() != null)
            criteria.add(Restrictions.le(
                    MARRIAGE_REGISTRATION_APPLICATION_DATE,
                    resetToDateTimeStamp(registration.getToDate())));
        criteria.addOrder(Order.desc(MARRIAGE_REGISTRATION_APPLICATION_DATE));
        if (registration.getFromDate() != null) {

            final Calendar cal = Calendar.getInstance();
            final Date todate = cal.getTime();
            criteria.add(Restrictions.between(
                    MARRIAGE_REGISTRATION_APPLICATION_DATE,
                    registration.getFromDate(), todate != null ? todate : new Date()));
        }
        if (registration.getToDate() == null) {
            final Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, 2009);
            final Date fromdate = cal.getTime();

            criteria.add(Restrictions.between(
                    MARRIAGE_REGISTRATION_APPLICATION_DATE,
                    fromdate, registration.getToDate() != null
                            ? org.apache.commons.lang3.time.DateUtils.addDays(registration.getToDate(), 1) : new Date()));
        }

        return criteria.list();
    }

    @ReadOnly
    @SuppressWarnings("unchecked")
    public List<String[]> getMonthwiseFundCollected(final MarriageRegistration registration, final String year) {
        final Map<String, String> params = new HashMap<>();
        final Map<String, Integer> intparams = new HashMap<>();
        final StringBuilder queryStrForRegFee = new StringBuilder(1000);
        final StringBuilder queryStrForReissueFee = new StringBuilder(1000);
        final StringBuilder queryStr = new StringBuilder(700);
        queryStr.append("select Month, SUM(Amount),Monthname from (");
        queryStrForRegFee
                .append("(select extract(month from applicationdate) as Month,SUM(reg.feepaid) as Amount,to_char(applicationdate,'Month') as Monthname from egmrs_registration reg,egmrs_registrationunit regunit,eg_demand demand "
                        + "where reg.registrationunit=regunit.id and reg.demand=demand.id and demand.amt_collected!=0 ");
        buildSearchCriteriaMonthWiseFundCollection(registration, year, params, intparams, queryStrForRegFee);

        queryStrForReissueFee
                .append("(select extract(month from applicationdate) as Month,SUM(reissue.feepaid) as Amount,to_char(applicationdate,'Month') as Monthname from egmrs_reissue reissue,egmrs_registrationunit regunit,eg_demand demand "
                        + "where reissue.registrationunit=regunit.id and reissue.demand=demand.id and demand.amt_collected!=0 ");
        buildSearchCriteriaMonthWiseFundCollection(registration, year, params, intparams, queryStrForReissueFee);

        final StringBuilder aggregateQueryStr = new StringBuilder();
        aggregateQueryStr.append(queryStrForRegFee.toString());
        aggregateQueryStr.append(UNION);
        aggregateQueryStr.append(queryStrForReissueFee.toString());
        queryStr.append(aggregateQueryStr.toString());
        queryStr.append(") as x GROUP BY MONTH,Monthname");

        final org.hibernate.Query query = getCurrentSession().createSQLQuery(queryStr.toString());
        for (final Map.Entry<String, String> param : params.entrySet())
            query.setParameter(param.getKey(), param.getValue());
        for (final Map.Entry<String, Integer> intparam : intparams.entrySet())
            query.setParameter(intparam.getKey(), intparam.getValue());
        return query.list();
    }

    private void buildSearchCriteriaMonthWiseFundCollection(final MarriageRegistration registration, final String year,
            final Map<String, String> params, final Map<String, Integer> intparams, final StringBuilder queryStrFOorApplns) {
        if (year != null) {
            queryStrFOorApplns.append(" and extract(year from applicationdate)=:years ");
            final int years = Integer.parseInt(year);
            intparams.put("years", years);
        }

        if (registration.getMonthYear() != null) {
            queryStrFOorApplns.append(APPLICATIONDATE_BETWEEN_CONDITION);
            params.put(FROMDATE, sf.format(getMonthStartday(registration.getMonthYear())));
            params.put(TODATE, sf.format(getMonthEndday(registration.getMonthYear())));
        }
        if (registration.getMarriageRegistrationUnit().getId() != null) {
            queryStrFOorApplns.append(REGISTRATIONUNIT_WHERE_QUERY);
            params.put(REGUNIT, registration.getMarriageRegistrationUnit().getId().toString());
        }

        queryStrFOorApplns.append(
                "group by regunit.name,extract(month from applicationdate),to_char(applicationdate,'Month') order by regunit.name)");
    }

    @ReadOnly
    @SuppressWarnings("unchecked")
    public List<String[]> getCountOfApplications(final MarriageRegistration registration) {

        final Map<String, String> params = new HashMap<>();
        final StringBuilder queryStrForRegCount = new StringBuilder(1000);
        queryStrForRegCount
                .append("(select regunit.name,count(*),to_char(applicationdate,'Mon'),'registration' from egmrs_registration reg,egmrs_registrationunit regunit,egw_status st "
                        + "where reg.registrationunit=regunit.id and reg.status = st.id and st.code='REGISTERED' ");

        if (registration.getZone().getId() != null) {
            queryStrForRegCount
                    .append(" and reg.zone=to_number(:zone,'999999')");
            params.put(ZONE, registration.getZone().getId().toString());
        }
        buildCriteriaForMrgApplicationsCount(registration, params, queryStrForRegCount);
        final StringBuilder queryStrForReissueCount = new StringBuilder(1000);
        queryStrForReissueCount
                .append("(select regunit.name,count(*),to_char(applicationdate,'Mon'),'reissue' from egmrs_reissue rei,egmrs_registrationunit regunit,egw_status st"
                        + " where rei.registrationunit=regunit.id and rei.status = st.id and st.code='CERTIFICATEREISSUED' ");

        if (registration.getZone().getId() != null) {
            queryStrForReissueCount
                    .append(" and rei.zone=to_number(:zone,'999999')");
            params.put(ZONE, registration.getZone().getId().toString());
        }
        buildCriteriaForMrgApplicationsCount(registration, params, queryStrForReissueCount);
        final StringBuilder aggregateQueryStr = new StringBuilder();
        aggregateQueryStr.append(queryStrForRegCount.toString());

        aggregateQueryStr.append(UNION);
        aggregateQueryStr.append(queryStrForReissueCount.toString());

        final org.hibernate.Query query = getCurrentSession().createSQLQuery(aggregateQueryStr.toString());
        for (final Map.Entry<String, String> param : params.entrySet())
            query.setParameter(param.getKey(), param.getValue());
        return query.list();
    }

    private void buildCriteriaForMrgApplicationsCount(final MarriageRegistration registration, final Map<String, String> params,
            final StringBuilder queryForApplns) {
        if (registration.getMonthYear() != null) {
            queryForApplns.append(
                    APPLICATIONDATE_BETWEEN_CONDITION);
            params.put(FROMDATE, sf.format(getMonthStartday(registration.getMonthYear())));
            params.put(TODATE, sf.format(getMonthEndday(registration.getMonthYear())));
        }
        if (registration.getMarriageRegistrationUnit().getId() != null) {
            queryForApplns
                    .append(REGISTRATIONUNIT_WHERE_QUERY);
            params.put(REGUNIT, registration.getMarriageRegistrationUnit().getId().toString());
        }
        queryForApplns.append("group by regunit.name,to_char(applicationdate,'Mon') order by regunit.name)");
    }

    @ReadOnly
    @SuppressWarnings("unchecked")
    public List<MarriageRegistration> searchRegistrationBymonth(
            final MarriageRegistration registration, final String month, final String registrationUnit) {
        final Criteria criteria = getCurrentSession().createCriteria(
                MarriageRegistration.class, MARRIAGE_REGISTRATION).createAlias(MARRIAGE_REGISTRATION_STATUS, STATUS);
        if (month != null)
            criteria.add(Restrictions.between(
                    MARRIAGE_REGISTRATION_APPLICATION_DATE,
                    getMonthStartday(month),
                    getMonthEndday(month)));
        if (registration.getZone() != null)
            criteria.createAlias(MARRIAGE_REGISTRATION + ".zone", "zone").add(Restrictions.eq(ZONE_ID,
                    registration.getZone().getId().toString()));

        if (registrationUnit != null)
            criteria.createAlias(MARRIAGE_REGISTRATION + DOT_MARRIAGE_REGISTRATION_UNIT, REGUNIT)
                    .add(Restrictions.eq("regunit.name",
                            registrationUnit));
        criteria.add(Restrictions.in(STATUS_DOT_CODE, MarriageRegistration.RegistrationStatus.REGISTERED.name()));
        return criteria.list();
    }

    @ReadOnly
    @SuppressWarnings("unchecked")
    public List<ReIssue> searchReissueBymonth(
            final MarriageRegistration registration, final String month, final String registrationUnit) {
        final Criteria criteria = getCurrentSession().createCriteria(
                ReIssue.class, "reissue").createAlias("reissue.status", STATUS);

        if (month != null)
            criteria.add(Restrictions.between(
                    "reissue.applicationDate",
                    getMonthStartday(month),
                    getMonthEndday(month)));
        if (registration.getZone() != null)
            criteria.createAlias(MARRIAGE_REGISTRATION + ".zone", "zone").add(Restrictions.eq(ZONE_ID,
                    registration.getZone().getId().toString()));
        if (registrationUnit != null)
            criteria.createAlias("reissue.marriageRegistrationUnit", REGUNIT).add(Restrictions.eq("regunit.name",
                    registrationUnit));
        criteria.add(Restrictions.in(STATUS_DOT_CODE, "CERTIFICATEREISSUED"));
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<MarriageRegistration> searchRegistrationByreligion(
            final MarriageRegistration registration, final int year) throws ParseException {
        final Criteria criteria = getCurrentSession().createCriteria(
                MarriageRegistration.class, MARRIAGE_REGISTRATION)
                .createAlias(MARRIAGE_REGISTRATION_STATUS, STATUS);
        final SimpleDateFormat formatter = new SimpleDateFormat(YYYY_MM_DD);
        final Date fromDate = formatter.parse(year + "/" + 1 + "/" + 1);
        final Date toDate = formatter.parse(year + "/" + 12 + "/" + 31);
        // when we select ALL as religion(wife and husband) in drop down passing value as 0 as assumption to that,
        // and checking condition and get result.
        if (null != registration.getHusband().getReligion()
                && registration.getHusband().getReligion().getId() != null
                && registration.getHusband().getReligion().getId() != 0) {
            criteria.createAlias(MARRIAGE_REGISTRATION_DOT_HUSBAND, HUSBAND)
                    .add(Restrictions.eq("husband.religion.id", registration
                            .getHusband().getReligion().getId()));
        } else {
            List<String> mrgReligionNames = getReligionNames();
            criteria.createAlias(MARRIAGE_REGISTRATION_DOT_HUSBAND, HUSBAND).createAlias("husband.religion", "husreligion")
                    .add(Restrictions.in("husreligion.name", mrgReligionNames));
        }
        if (null != registration.getWife().getReligion()
                && registration.getWife().getReligion().getId() != null && registration.getWife().getReligion().getId() != 0) {
            criteria.createAlias(MARRIAGE_REGISTRATION_DOT_WIFE, WIFE).add(
                    Restrictions.eq("wife.religion.id", registration.getWife()
                            .getReligion().getId()));
        } else {
            List<String> mrgReligionNames = getReligionNames();
            criteria.createAlias(MARRIAGE_REGISTRATION_DOT_WIFE, WIFE).createAlias("wife.religion", "wifereligion")
                    .add(Restrictions.in("wifereligion.name", mrgReligionNames));
        }
        if (null != fromDate)
            criteria.add(Restrictions.ge(
                    MARRIAGE_REGISTRATION_APPLICATION_DATE, resetFromDateTimeStamp(fromDate)));
        if (null != toDate)
            criteria.add(Restrictions.le(
                    MARRIAGE_REGISTRATION_APPLICATION_DATE, resetToDateTimeStamp(toDate)));
        if (null != registration.getMarriageRegistrationUnit()
                && registration.getMarriageRegistrationUnit().getId() != null)
            criteria.add(Restrictions.eq(MARRIAGE_REGISTRATION_UNIT_DOT_ID,
                    registration.getMarriageRegistrationUnit().getId()));
        if (null != registration.getZone()
                && registration.getZone().getId() != null)
            criteria.add(Restrictions.eq(ZONE_ID, registration.getZone()
                    .getId()));
        if (null != registration.getMarriageRegistrationUnit()
                && registration.getMarriageRegistrationUnit().getId() != null)
            criteria.add(Restrictions.eq(MARRIAGE_REGISTRATION_UNIT_DOT_ID,
                    registration.getMarriageRegistrationUnit().getId()));
        criteria.add(Restrictions.in(STATUS_DOT_CODE, MarriageRegistration.RegistrationStatus.APPROVED.name(),
                MarriageRegistration.RegistrationStatus.REGISTERED.name()));
        return criteria.list();
    }

    private List<String> getReligionNames() {
        List<String> religionNameList = new ArrayList<>();
        List<MarriageReligion> marriageReligions = religionService.findAll();
        for (MarriageReligion marriageReligion : marriageReligions) {
            religionNameList.add(marriageReligion.getName());
        }
        return religionNameList;
    }

    @ReadOnly
    @SuppressWarnings("unchecked")
    public List<String[]> searchRegistrationMrActWise(final int year, final MarriageRegistration registration) {
        final Map<String, Integer> params = new HashMap<>();
        final StringBuilder queryForAct = new StringBuilder(1000);

        queryForAct
                .append("(select act.name,count(*) from egmrs_registration  reg, egmrs_act  act, egw_status  status ,egmrs_registrationunit ru,eg_boundary b "
                        + "where reg.marriageact=act.id and status.code in('APPROVED')  and reg.registrationunit=ru.id and reg.status = status.id and reg.zone=b.id "
                        + " and extract( year from reg.applicationdate)=:year ");
        params.put(INPUTYEAR, year);
        if (registration.getMarriageRegistrationUnit().getId() != null) {
            queryForAct
                    .append(" and  reg.registrationunit=:regunit ");
            params.put(REGUNIT, registration.getMarriageRegistrationUnit().getId().intValue());

        }
        if (registration.getZone().getId() != null) {
            queryForAct
                    .append(" and  reg.zone=:zone ");
            params.put(ZONE, registration.getZone().getId().intValue());

        }
        queryForAct.append("group by act.name)");
        final org.hibernate.Query query = getCurrentSession().createSQLQuery(queryForAct.toString());
        for (final Map.Entry<String, Integer> param : params.entrySet())
            query.setParameter(param.getKey(), param.getValue());
        return query.list();
    }

    @ReadOnly
    @SuppressWarnings("unchecked")
    public List<MarriageRegistration> getActwiseDetails(final int year, final String act)
            throws ParseException {
        final Criteria criteria = getCurrentSession().createCriteria(
                MarriageRegistration.class, MARRIAGE_REGISTRATION).createAlias(MARRIAGE_REGISTRATION_STATUS, STATUS);

        final SimpleDateFormat formatter = new SimpleDateFormat(YYYY_MM_DD);
        final Date fromDate = formatter.parse(year + "/" + 1 + "/" + 1);
        final Date toDate = formatter.parse(year + "/" + 12 + "/" + 31);
        if (act != null)
            criteria.createAlias("marriageRegistration.marriageAct",
                    "marriageAct")
                    .add(Restrictions.eq("marriageAct.name", act));
        if (fromDate != null)
            criteria.add(Restrictions.ge(
                    MARRIAGE_REGISTRATION_APPLICATION_DATE, resetFromDateTimeStamp(fromDate)));

        if (toDate != null)
            criteria.add(Restrictions.le(
                    MARRIAGE_REGISTRATION_APPLICATION_DATE, resetToDateTimeStamp(toDate)));

        criteria.add(Restrictions.in(STATUS_DOT_CODE, MarriageRegistration.RegistrationStatus.APPROVED.toString()));
        return criteria.list();

    }

    @ReadOnly
    @SuppressWarnings("unchecked")
    public List<MarriageRegistration> getmonthWiseActDetails(final int year,
            final int month, final Long actid) {
        final Criteria criteria = getCurrentSession().createCriteria(
                MarriageRegistration.class, MARRIAGE_REGISTRATION).createAlias(MARRIAGE_REGISTRATION_STATUS, STATUS);
        final String monthYear = month + "/" + year;
        if (actid != null)
            criteria.createAlias("marriageRegistration.marriageAct",
                    "marriageAct")
                    .add(Restrictions.eq("marriageAct.id", actid));
        if (StringUtils.isNotBlank(monthYear))
            criteria.add(Restrictions.between(MARRIAGE_REGISTRATION_APPLICATION_DATE, getMonthStartday(monthYear),
                    getMonthEndday(monthYear)));

        criteria.add(Restrictions.in(STATUS_DOT_CODE, MarriageRegistration.RegistrationStatus.APPROVED.name()));
        return criteria.list();
    }

    @ReadOnly
    @SuppressWarnings("unchecked")
    public List<Object[]> getAgeingRegDetails(final String day, final int year) {
        final String[] values = day.split("-");
        final StringBuilder queryStrForRegistration = new StringBuilder(1000);
        final Map<String, Double> params = new HashMap<>();
        queryStrForRegistration
                .append(
                        "Select reg.applicationno,reg.registrationno,(Select concat(concat(concat(app.firstname, ' '), app.middlename, ' '), app.lastname) as hus_name from egmrs_applicant app where app.id = reg.husband),(Select concat(concat(concat(app.firstname, ' '), app.middlename, ' '), app.lastname) as wife_name from egmrs_applicant app where app.id = reg.wife),reg.dateofmarriage,reg.applicationdate,reg.placeofmarriage, brndy.name,st.code,'Marriage Registration',state.owner_pos,state.nextaction"
                                + " from egmrs_registration reg,egmrs_applicant app, eg_boundary brndy,egw_status st,eg_wf_states state"
                                + " where reg.state_id = state.id and reg.zone = brndy.id and reg.status = st.id and st.code not in ('REGISTERED','CANCELLED') and EXTRACT(EPOCH FROM date_trunc('day',(now()-reg.applicationdate)))/60/60/24 between :fromdays and :todays ");
        params.put("fromdays", Double.valueOf(values[0]));
        params.put("todays", Double.valueOf(values[1]));

        final StringBuilder queryStrForReissue = new StringBuilder(1000);
        queryStrForReissue
                .append(
                        "Select rei.applicationno,reg.registrationno,(Select concat(concat(concat(app.firstname, ' '), app.middlename, ' '), app.lastname) as hus_name from egmrs_applicant app where app.id = reg.husband),(Select concat(concat(concat(app.firstname, ' '), app.middlename, ' '), app.lastname) as wife_name from egmrs_applicant app where app.id = reg.wife),reg.dateofmarriage,rei.applicationdate,reg.placeofmarriage,brndy.name,st.code,'Reissue',state1.owner_pos,state1.nextaction as action1"
                                + " from egmrs_reissue rei,egmrs_registration reg, egmrs_applicant app, eg_boundary brndy,egw_status st,eg_wf_states state1"
                                + " where rei.state_id = state1.id and rei.registration=reg.id and rei.zone = brndy.id and rei.status = st.id and st.code not in ('CERTIFICATEREISSUED','CANCELLED') and EXTRACT(EPOCH FROM date_trunc('day',(now()-rei.applicationdate)))/60/60/24 between :fromdays and :todays ");
        params.put("fromdays", Double.valueOf(values[0]));
        params.put("todays", Double.valueOf(values[1]));

        final StringBuilder aggregateQueryStr = new StringBuilder();
        aggregateQueryStr.append(queryStrForRegistration.toString());

        aggregateQueryStr.append(UNION);
        aggregateQueryStr.append(queryStrForReissue.toString());

        final org.hibernate.Query query = getCurrentSession().createSQLQuery(aggregateQueryStr.toString());
        for (final Map.Entry<String, Double> param : params.entrySet())
            query.setParameter(param.getKey(), param.getValue());
        return query.list();
    }

    @ReadOnly
    @SuppressWarnings("unchecked")
    public List<String[]> searchRegistrationbyDays(final int year, final MarriageRegistration registration) {

        final Map<String, Integer> params = new HashMap<>();
        final StringBuilder queryStrForRegAgeingDetails = new StringBuilder(1000);
        queryStrForRegAgeingDetails
                .append(
                        "(Select EXTRACT(EPOCH FROM date_trunc('day',(now()-applicationdate)))/60/60/24, count(*),st.code"
                                + " from egmrs_registration reg,egw_status st,egmrs_registrationunit ru,eg_boundary brndy"
                                + " where reg.zone=brndy.id and reg.registrationunit=ru.id and status = st.id and st.code not in ('REGISTERED','CANCELLED') and extract(year from applicationdate)=:year ");

        params.put(INPUTYEAR, year);
        if (registration.getMarriageRegistrationUnit().getId() != null) {
            queryStrForRegAgeingDetails
                    .append("  and reg.registrationunit=:regunit ");
            params.put(REGUNIT, registration.getMarriageRegistrationUnit().getId().intValue());
        }

        if (registration.getZone() != null && registration.getZone().getId() != null) {

            queryStrForRegAgeingDetails
                    .append(" and  reg.zone=:zone ");
            params.put(ZONE, registration.getZone().getId().intValue());
        }
        queryStrForRegAgeingDetails
                .append(" group by st.code,EXTRACT(EPOCH FROM date_trunc('day',(now()-applicationdate)))/60/60/24 order by EXTRACT(EPOCH FROM date_trunc('day',(now()-applicationdate)))/60/60/24 ) ");

        final StringBuilder queryStrForReIssueAgeingDetails = new StringBuilder(1000);
        queryStrForReIssueAgeingDetails
                .append(
                        "(Select EXTRACT(EPOCH FROM date_trunc('day',(now()-applicationdate)))/60/60/24, count(*),st.code"
                                + " from egmrs_reissue rei,egw_status st,egmrs_registrationunit ru,eg_boundary brndy"
                                + " where  rei.registrationunit=ru.id and  rei.zone=brndy.id and status = st.id and st.code not in ('CERTIFICATEREISSUED','CANCELLED') and extract(year from applicationdate)=:year  ");
        params.put(INPUTYEAR, year);

        if (registration.getMarriageRegistrationUnit().getId() != null) {
            queryStrForReIssueAgeingDetails
                    .append("  and rei.registrationunit=:regunit ");
            params.put(REGUNIT, registration.getMarriageRegistrationUnit().getId().intValue());
        }

        if (registration.getZone() != null && registration.getZone().getId() != null) {

            queryStrForReIssueAgeingDetails
                    .append(" and  rei.zone=:zone ");
            params.put(ZONE, registration.getZone().getId().intValue());
        }
        queryStrForReIssueAgeingDetails
                .append(" group by st.code,EXTRACT(EPOCH FROM date_trunc('day',(now()-applicationdate)))/60/60/24 order by EXTRACT(EPOCH FROM date_trunc('day',(now()-applicationdate)))/60/60/24) ");

        final StringBuilder aggregateQueryStr = new StringBuilder();
        aggregateQueryStr.append(queryStrForRegAgeingDetails.toString());

        aggregateQueryStr.append(UNION);
        aggregateQueryStr.append(queryStrForReIssueAgeingDetails.toString());

        final org.hibernate.Query query = getCurrentSession().createSQLQuery(aggregateQueryStr.toString());
        for (final Map.Entry<String, Integer> param : params.entrySet())
            query.setParameter(param.getKey(), param.getValue());
        return query.list();

    }

    @ReadOnly
    @SuppressWarnings("unchecked")
    public List<String[]> getCountOfApplnsStatusWise(final String status, final Date fromDate, final Date toDate,
            final MarriageRegistrationUnit registrationUnit, final MarriageRegistration registration) {

        final Map<String, String> params = new HashMap<>();
        final StringBuilder queryStrForRegCount = new StringBuilder(1000);
        queryStrForRegCount
                .append("select regunit.name,st.code,count(*) from egmrs_registration reg,egmrs_registrationunit regunit,egw_status st"
                        + " where reg.registrationunit=regunit.id and reg.status = st.id  ");
        if (fromDate != null) {
            queryStrForRegCount.append(" and applicationdate >= to_timestamp(:fromdate,'yyyy-MM-dd HH24:mi:ss')");
            params.put(FROMDATE, sf.format(resetFromDateTimeStamp(fromDate)));
        }
        if (toDate != null) {
            queryStrForRegCount.append(" and applicationdate <=to_timestamp(:todate,'YYYY-MM-DD HH24:MI:SS') ");
            params.put(TODATE, sf.format(resetToDateTimeStamp(toDate)));
        }
        if (status != null && ALL.equalsIgnoreCase(status))
            queryStrForRegCount
                    .append(" and st.code in ('CREATED','APPROVED','REGISTERED','REJECTED','CANCELLED')");
        else if (status != null) {
            queryStrForRegCount
                    .append(" and st.code=:status ");
            params.put(STATUS, status);
        }

        if (registrationUnit.getId() != null) {
            queryStrForRegCount
                    .append(" and  reg.registrationunit=to_number(:regunitid,'999999') ");
            params.put("regunitid", registrationUnit.getId().toString());
        }
        if (registration.getZone().getId() != null) {
            queryStrForRegCount
                    .append(REG_ZONE_WHERE_CONDITION);
            params.put(ZONE, registration.getZone().getId().toString());
        }

        queryStrForRegCount.append(" group by regunit.name,st.code order by regunit.name desc");

        final org.hibernate.Query query = getCurrentSession().createSQLQuery(queryStrForRegCount.toString());
        for (final Map.Entry<String, String> param : params.entrySet())
            query.setParameter(param.getKey(), param.getValue());
        return query.list();
    }

    @ReadOnly
    @SuppressWarnings("unchecked")
    public List<MarriageRegistration> searchRegistrationByStatusForReport(final String registrationUnit, final String status,
            final Date fromDate, final Date toDate) {

        final Criteria criteria = getCurrentSession().createCriteria(MarriageRegistration.class, "marriageRegistration")
                .createAlias(MARRIAGE_REGISTRATION_STATUS, STATUS);
        if (fromDate != null)
            criteria.add(Restrictions.ge(MARRIAGE_REGISTRATION_APPLICATION_DATE, resetFromDateTimeStamp(fromDate)));
        if (fromDate != null)
            criteria.add(Restrictions.le(MARRIAGE_REGISTRATION_APPLICATION_DATE, resetToDateTimeStamp(toDate)));
        if (registrationUnit != null)
            criteria.createAlias("marriageRegistration.marriageRegistrationUnit", MARRIAGE_REGISTRATION_UNIT)
                    .add(Restrictions.eq("marriageRegistrationUnit.name", registrationUnit.replaceAll("[^a-zA-Z0-9]", " ")));

        if (status != null && !ALL.equalsIgnoreCase(status))
            criteria.add(Restrictions.in(STATUS_DOT_CODE, status));
        else
            criteria.add(Restrictions.in(STATUS_DOT_CODE, "CREATED", "APPROVED", "REGISTERED", "REJECTED", "CANCELLED"));
        return criteria.list();

    }

    public List<SearchResult> getUlbWiseReligionDetails(final SearchModel searchRequest) {

        SearchResponse ulbWiseResponse;
        try {
            ulbWiseResponse = findAllReligionByUlbName(searchRequest,
                    getQueryFilter(searchRequest));
        } catch (final ParseException e) {
            throw new ApplicationRuntimeException("Error Occured while fetching records from elastic search", e);
        }

        final List<SearchResult> responseDetailsList = new ArrayList<>();
        final Terms ulbs = ulbWiseResponse.getAggregations().get("groupByUlbName");
        SearchResult searchResult;
        for (final Terms.Bucket ulb : ulbs.getBuckets()) {
            long countOthers = 0;
            long total = 0;
            searchResult = new SearchResult();
            searchResult.setUlbName(ulb.getKeyAsString());
            final Terms religions = ulb.getAggregations().get("groupByReligion");
            for (final Terms.Bucket religion : religions.getBuckets())
                if ("Christianity".equals(religion.getKeyAsString())) {
                    total = total + religion.getDocCount();
                    searchResult.setChristian(religion.getDocCount());
                } else if ("Hinduism".equals(religion.getKeyAsString())) {
                    total = total + religion.getDocCount();
                    searchResult.setHindu(religion.getDocCount());
                } else if ("Islam".equals(religion.getKeyAsString())) {
                    total = total + religion.getDocCount();
                    searchResult.setMuslim(religion.getDocCount());
                } else {
                    total = total + religion.getDocCount();
                    countOthers = countOthers + religion.getDocCount();
                }
            searchResult.setOthers(countOthers);
            searchResult.setTotal(total);
            responseDetailsList.add(searchResult);
        }
        return responseDetailsList;
    }

    public BoolQueryBuilder getQueryFilter(final SearchModel searchRequest) throws ParseException {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .filter(QueryBuilders.matchQuery("ulbName", ApplicationThreadLocals.getCityName()));
        final SimpleDateFormat formatter = new SimpleDateFormat(YYYY_MM_DD);
        final Date fromDate = formatter.parse(searchRequest.getYear() + "/" + 1 + "/" + 1);
        final Date toDate = formatter.parse(searchRequest.getYear() + "/" + 12 + "/" + 31);
        if (fromDate != null && toDate != null)
            boolQuery = boolQuery.filter(QueryBuilders.rangeQuery("registrationDate")
                    .from(fromDate)
                    .to(toDate));
        boolQuery = boolQuery.filter(QueryBuilders.matchQuery("applicationStatus", "Registered"));
        return boolQuery;
    }

    public List<MarriageRegistrationIndex> getSearchResultByBoolQuery(final BoolQueryBuilder boolQuery,
            final FieldSortBuilder sort) {
        final SearchQuery searchQuery = new NativeSearchQueryBuilder().withIndices("marriageregistration").withQuery(boolQuery)
                .withSort(sort).build();
        return elasticsearchTemplate.queryForList(searchQuery, MarriageRegistrationIndex.class);
    }

    public SearchResponse findAllReligionByUlbName(final SearchModel searchRequest,
            final BoolQueryBuilder query) {

        return elasticsearchTemplate.getClient().prepareSearch("marriageregistration")
                .setQuery(query).setSize(0)
                .addAggregation(getCountWithGrouping("groupByUlbName", "ulbName", 120)
                        .subAggregation(getCountWithGrouping("groupByReligion", "husbandReligion", 30)))
                .execute().actionGet();
    }

    public static AggregationBuilder<?> getCountWithGrouping(final String aggregationName, final String fieldName,
            final int size) {
        return AggregationBuilders.terms(aggregationName).field(fieldName).size(size);
    }

    public ReportOutput generateReportOutputForReligionWiseReport(final int year, final List<SearchResult> searchResponse) {
        final Map<String, Object> reportParams = new HashMap<>();
        reportParams.put("cityName", cityService.getMunicipalityName());
        reportParams.put("logoPath", cityService.getCityLogoURL());
        reportParams.put(INPUTYEAR, year);
        reportParams.put("remarks", "");
        reportParams.put("searchResponse", searchResponse);
        final ReportRequest reportInput = new ReportRequest("printreligionwisereport", searchResponse, reportParams);
        reportInput.setReportFormat(ReportFormat.PDF);
        return reportService.createReport(reportInput);
    }

}
