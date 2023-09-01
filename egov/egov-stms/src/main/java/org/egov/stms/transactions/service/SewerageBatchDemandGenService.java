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
package org.egov.stms.transactions.service;

import org.apache.log4j.Logger;
import org.egov.commons.Installment;
import org.egov.demand.dao.EgDemandDao;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.stms.entity.SewerageTaxBatchDemandGenerate;
import org.egov.stms.transactions.entity.SewerageApplicationDetails;
import org.egov.stms.transactions.repository.SewerageTaxBatchDemandGenRepository;
import org.egov.stms.utils.constants.SewerageTaxConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SewerageBatchDemandGenService {

    private static final Logger LOGGER = Logger.getLogger(SewerageBatchDemandGenService.class);

    @Autowired
    private EgDemandDao egDemandDao;
    @Autowired
    private SewerageApplicationDetailsService sewerageApplicationDetailsService;

    @Autowired
    private SewerageDemandService sewerageDemandService;

    @Autowired
    private AppConfigValueService appConfigValuesService;

    @Autowired
    private SewerageTaxBatchDemandGenRepository sewerageTaxBatchDemandGenRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    public SewerageTaxBatchDemandGenerate getBatchDemandGenById(final Long id) {
        return sewerageTaxBatchDemandGenRepository.findOne(id);
    }

    public List<SewerageTaxBatchDemandGenerate> findActiveBatchDemands() {
        return sewerageTaxBatchDemandGenRepository.findByActiveTrueOrderByCreatedDate();
    }

    @Transactional
    public SewerageTaxBatchDemandGenerate createSewerageTaxBatchDemandGenerate(final SewerageTaxBatchDemandGenerate advBatchDmd) {
        return sewerageTaxBatchDemandGenRepository.save(advBatchDmd);
    }

    @Transactional
    public SewerageTaxBatchDemandGenerate updateSewerageTaxBatchDemandGenerate(final SewerageTaxBatchDemandGenerate advBatchDmd) {
        return sewerageTaxBatchDemandGenRepository.save(advBatchDmd);
    }

    public int generateSewerageDemandForNextFinYear() {
        Integer[] recordsResult = null;

        List<SewerageTaxBatchDemandGenerate> sewerageBatchDmdGenResult = findActiveBatchDemands();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("SewerageBatchDmdGenResult " + sewerageBatchDmdGenResult.size());
        }

        if (!sewerageBatchDmdGenResult.isEmpty()) {

            final AppConfigValues totalRecordToFeatch = appConfigValuesService.getConfigValuesByModuleAndKey(
                    SewerageTaxConstants.MODULE_NAME, SewerageTaxConstants.TOTALRESULTTOBEFETCH).get(0);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(
                        "*************************************** totalRecordToFeatch records " + totalRecordToFeatch.getValue());
            }
            SewerageTaxBatchDemandGenerate sewerageDmdGen = sewerageBatchDmdGenResult.get(0);

            /*
             * GET LIST OF DEMANDS WHICH ARE PERMANENT AND BASED ON FINANCIAL YEAR, GET Sewerage Applications. Check count, if
             * count greater than 300 then
             */
            if (sewerageDmdGen != null && sewerageDmdGen.getInstallment() != null) {

                List<Installment> previousInstallment = sewerageDemandService.getPreviousInstallment(sewerageDmdGen
                        .getInstallment().getToDate());

                Installment sewerageDmdGenerationInstallment = sewerageDemandService
                        .getInsatllmentByModuleForGivenDate(sewerageDmdGen.getInstallment().getToDate());

                /*
                 * Assumption : selected installment data not present in sewerage demand.
                 */
                if (sewerageDmdGenerationInstallment != null && previousInstallment != null && !previousInstallment.isEmpty()) {

                    List<SewerageApplicationDetails> sewerageApplnsDetails = sewerageApplicationDetailsService
                            .findActiveSewerageApplnsByCurrentInstallmentAndNumberOfResultToFetch(
                                    previousInstallment.get(0), Integer.valueOf(totalRecordToFeatch.getValue()));
                    for (SewerageApplicationDetails applicationDetails : sewerageApplnsDetails) {
                        applicationDetails.getDemandConnections().get(0).setDemand(egDemandDao
                                .findById(applicationDetails.getDemandConnections().get(0).getDemand().getId(), false));
                    }

                    recordsResult = sewerageDemandService.generateDemandForNextInstallment(
                            sewerageApplnsDetails, previousInstallment, sewerageDmdGenerationInstallment);

                }

                sewerageDmdGen.setActive(false);
                sewerageDmdGen.setTotalRecords(
                        (recordsResult != null && recordsResult.length > 0 && recordsResult[0] != null) ? recordsResult[0] : 0);
                sewerageDmdGen.setSuccessfullRecords(
                        (recordsResult != null && recordsResult.length >= 2 && recordsResult[1] != null) ? recordsResult[1] : 0);
                sewerageDmdGen.setFailureRecords(
                        (recordsResult != null && recordsResult.length >= 3 && recordsResult[2] != null) ? recordsResult[2] : 0);

                final TransactionTemplate txTemplate = new TransactionTemplate(transactionTemplate.getTransactionManager());
                txTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

                txTemplate.execute(result -> {
                    updateSewerageTaxBatchDemandGenerate(sewerageDmdGen);
                    return Boolean.TRUE;
                });
            }

        }

        return (recordsResult != null && recordsResult.length >= 2 && recordsResult[1] != null) ? recordsResult[1] : 0;
    }

}
