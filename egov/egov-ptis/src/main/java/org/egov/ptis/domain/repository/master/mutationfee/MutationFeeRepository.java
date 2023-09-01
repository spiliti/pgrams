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

package org.egov.ptis.domain.repository.master.mutationfee;

import org.egov.ptis.domain.model.MutationFeeDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public interface MutationFeeRepository extends JpaRepository<MutationFeeDetails, Long> {

    @Query("select max(highLimit) from MutationFeeDetails where toDate>now()")
    BigDecimal maxByHighLimit();

    @Query("select lowLimit from MutationFeeDetails where highLimit is null and toDate > now()")
    BigDecimal findLowLimitForHighLimitNullValue();

    @Query("select max(mfd.toDate) from MutationFeeDetails mfd where mfd.slabName=:slabName ")
    Date findToDateBySlabName(@Param("slabName") String slabName);

    @Query("select mfd1.slabName from MutationFeeDetails mfd1 where mfd1.slabName=:slabName")
    List<String> findIfSlabNameExists(@Param("slabName") String slabName);
    
    @Query("select max(mfd1.toDate) from MutationFeeDetails mfd1 where mfd1.slabName=:slabName")
    Date findLatestToDateForSlabName(@Param("slabName") String slabName);

    @Query(value = "select distinct on(slab_name) * from egpt_mutation_fee_details order by slab_name ", nativeQuery = true)
    List<MutationFeeDetails> getDistinctSlabNamesList();

    @Query("select md from MutationFeeDetails md where md.slabName=:slabName")
    List<MutationFeeDetails> findBySlabNames(@Param("slabName") String slabName);

    @Query("select mfd2 from MutationFeeDetails mfd2 where mfd2.id=:id")
    MutationFeeDetails getAllDetailsById(@Param("id") Long id);

    @Query("select mfd3 from MutationFeeDetails mfd3 order by mfd3.lowLimit asc")
    List<MutationFeeDetails> selectAllOrderBySlabName();
    
    @Query("select mfd4 from MutationFeeDetails mfd4 where mfd4.lowLimit <= :documentValue and (mfd4.highLimit is null OR mfd4.highLimit >= :documentValue) and current_date between mfd4.fromDate and mfd4.toDate")
    List<MutationFeeDetails> getMutationFee(@Param("documentValue") BigDecimal documentValue);
}
