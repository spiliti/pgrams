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

package org.egov.pgr.config;

import org.egov.infra.config.scheduling.QuartzSchedulerConfiguration;
import org.egov.infra.config.scheduling.SchedulerConfigCondition;
import org.egov.pgr.config.conditions.GrievanceSchedulerConfigCondition;
import org.egov.pgr.config.properties.GrievanceApplicationSettings;
import org.egov.pgr.scheduler.jobs.ComplaintEscalationJob;
import org.egov.pgr.scheduler.jobs.ComplaintIndexingJob;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Conditional(SchedulerConfigCondition.class)
public class GrievanceSchedulerConfiguration extends QuartzSchedulerConfiguration {

    @Autowired
    private GrievanceApplicationSettings grievanceApplicationSettings;

    @Bean(destroyMethod = "destroy")
    @Conditional(GrievanceSchedulerConfigCondition.class)
    public SchedulerFactoryBean pgrScheduler(DataSource dataSource) {
        SchedulerFactoryBean pgrScheduler = createScheduler(dataSource);
        pgrScheduler.setSchedulerName("pgr-scheduler");
        pgrScheduler.setAutoStartup(true);
        pgrScheduler.setOverwriteExistingJobs(true);
        List<Trigger> triggers = new ArrayList<>();
        if (grievanceApplicationSettings.escalationSchedulerEnabled())
            triggers.add(complaintEscalationCronTrigger().getObject());
        if (grievanceApplicationSettings.indexingSchedulerEnabled())
            triggers.add(complaintIndexingCronTrigger().getObject());
        pgrScheduler.setTriggers(triggers.toArray(new Trigger[triggers.size()]));
        return pgrScheduler;
    }

    @Bean("complaintEscalationJob")
    public ComplaintEscalationJob complaintEscalationJob() {
        return new ComplaintEscalationJob();
    }

    @Bean
    public JobDetailFactoryBean complaintEscalationJobDetail() {
        JobDetailFactoryBean escalationJobDetail = new JobDetailFactoryBean();
        escalationJobDetail.setGroup("PGR_JOB_GROUP");
        escalationJobDetail.setName("PGR_ESCALATION_JOB");
        escalationJobDetail.setDurability(true);
        escalationJobDetail.setJobClass(ComplaintEscalationJob.class);
        escalationJobDetail.setRequestsRecovery(true);
        Map<String, String> jobDetailMap = new HashMap<>();
        jobDetailMap.put("jobBeanName", "complaintEscalationJob");
        jobDetailMap.put("userName", "system");
        jobDetailMap.put("cityDataRequired", "true");
        jobDetailMap.put("moduleName", "pgr");
        escalationJobDetail.setJobDataAsMap(jobDetailMap);
        return escalationJobDetail;
    }

    @Bean
    public CronTriggerFactoryBean complaintEscalationCronTrigger() {
        CronTriggerFactoryBean escalationCron = new CronTriggerFactoryBean();
        escalationCron.setJobDetail(complaintEscalationJobDetail().getObject());
        escalationCron.setGroup("PGR_TRIGGER_GROUP");
        escalationCron.setName("PGR_ESCALATION_TRIGGER");
        escalationCron.setCronExpression(grievanceApplicationSettings.getValue("pgr.escalation.job.cron"));
        return escalationCron;
    }

    @Bean("complaintIndexingJob")
    public ComplaintIndexingJob complaintIndexingJob() {
        return new ComplaintIndexingJob();
    }

    @Bean
    public JobDetailFactoryBean complaintIndexingJobDetail() {
        JobDetailFactoryBean complaintIndexingJobDetail = new JobDetailFactoryBean();
        complaintIndexingJobDetail.setGroup("PGR_JOB_GROUP");
        complaintIndexingJobDetail.setName("PGR_INDEX_JOB");
        complaintIndexingJobDetail.setDurability(true);
        complaintIndexingJobDetail.setJobClass(ComplaintIndexingJob.class);
        complaintIndexingJobDetail.setRequestsRecovery(true);
        Map<String, String> jobDetailMap = new HashMap<>();
        jobDetailMap.put("jobBeanName", "complaintIndexingJob");
        jobDetailMap.put("userName", "system");
        jobDetailMap.put("cityDataRequired", "true");
        jobDetailMap.put("moduleName", "pgr");
        complaintIndexingJobDetail.setJobDataAsMap(jobDetailMap);
        return complaintIndexingJobDetail;
    }

    @Bean
    public CronTriggerFactoryBean complaintIndexingCronTrigger() {
        CronTriggerFactoryBean escalationCron = new CronTriggerFactoryBean();
        escalationCron.setJobDetail(complaintIndexingJobDetail().getObject());
        escalationCron.setGroup("PGR_TRIGGER_GROUP");
        escalationCron.setName("PGR_INDEX_TRIGGER");
        escalationCron.setCronExpression(grievanceApplicationSettings.getValue("pgr.indexing.job.cron"));
        return escalationCron;
    }
}
