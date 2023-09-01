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

package org.egov.ptis.service.es;

import static org.egov.ptis.constants.PropertyTaxConstants.PROPERTY_TAX_INDEX_PREFIX;
import static org.egov.ptis.constants.PropertyTaxConstants.PTMODULENAME;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.egov.commons.CFinancialYear;
import org.egov.commons.dao.FinancialYearDAO;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.ptis.constants.PropertyTaxConstants;
import org.egov.ptis.domain.dao.property.PropertyTypeMasterDAO;
import org.egov.ptis.domain.entity.property.PTYearWiseDCBRequest;
import org.egov.ptis.domain.entity.property.PropertyTypeMaster;
import org.egov.ptis.domain.entity.property.YearWiseDCBReponse;
import org.egov.ptis.domain.entity.property.YearWiseDCBReportResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

@Service
public class PTYearWiseDCBIndexService {

    private static final String CATEGORY = "consumerType";
    private static final String ARREARDEMAND = "arrearDemand";
    private static final String CURRENT_DMD = "currentDemand";
    private static final String CURR_INTEREST_DMD = "currentPenDemand";
    private static final String ARREAR_INTEREST_DMD = "arrearPenDemand";
    private static final String ARREAR_COLLECTION = "arrearCollection";
    private static final String ARREAR_INTEREST_COLLECTION = "arrearPenCollection";
    private static final String CURRENT_COLLECTION = "currentCollection";
    private static final String CURRENT_INTEREST_COLLECTION = "currentPenCollection";
    private static final String CITY_CODE = "ulbCode";
    private static final String REVENUE_WARD = "wardName";
    private static final String BLOCK = "blockName";
    private static final String GROUP_TYPE_WARD = "ward";
    private static final String GROUP_TYPE_BLOCK = "block";
    private static final String GROUP_TYPE_PROPERTY = "property";
    private static final String OWNERS_NAME = "ownersName";
    private static final String DOOR_NO = "doorNo";
    private static final String IS_UNDER_COURT = "isUnderCourtcase";
    private static final String VLT_CODE = "Vacant Land";
    private static final String EWHS_CODE = "EWSHS";

    @Autowired
    private AppConfigValueService appConfigValuesService;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private PropertyTypeMasterDAO propertyTypeMasterDAO;

    @Autowired
    private FinancialYearDAO financialYearDAO;

    public List<PropertyTypeMaster> getPropertyTypes() {
        return propertyTypeMasterDAO.findBuiltUpOwnerShipTypes();
    }

    public List<CFinancialYear> getFinancialYears() {
        return financialYearDAO
                .getFinancialYearsAfterFromDate(
                        DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(getDCBStartDateFromAppConfig()).toDate());
    }

    public String getDCBStartDateFromAppConfig() {
        final List<AppConfigValues> appConfigValues = appConfigValuesService.getConfigValuesByModuleAndKey(PTMODULENAME,
                PropertyTaxConstants.PTYEARWISEDCBSTARTYEAR);
        return appConfigValues.get(0).getValue();
    }

    public YearWiseDCBReportResponse getDetails(final PTYearWiseDCBRequest serviceRequest) {
        YearWiseDCBReportResponse reportResponse = new YearWiseDCBReportResponse();
        List<YearWiseDCBReponse> responseList;
        if (GROUP_TYPE_PROPERTY.equalsIgnoreCase(serviceRequest.getType()))
            responseList = getResponseForPropertyDCB(serviceRequest);
        else
            responseList = getResponseForDCB(serviceRequest);
        reportResponse.setYearWiseDCBResponse(responseList);
        return reportResponse;
    }

    private List<YearWiseDCBReponse> getResponseForPropertyDCB(PTYearWiseDCBRequest serviceRequest) {
        SearchResponse getPropertyCount = elasticsearchTemplate.getClient()
                .prepareSearch(PROPERTY_TAX_INDEX_PREFIX + serviceRequest.getYearIndex())
                .setQuery(getBoolQuery(serviceRequest)).execute().actionGet();
        SearchResponse response = elasticsearchTemplate.getClient()
                .prepareSearch(PROPERTY_TAX_INDEX_PREFIX + serviceRequest.getYearIndex())
                .setSize((int) getPropertyCount.getHits().getTotalHits())
                .setQuery(getBoolQuery(serviceRequest)).execute().actionGet();
        List<YearWiseDCBReponse> dcbData = new ArrayList<>();
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            YearWiseDCBReponse serviceWiseResponse = new YearWiseDCBReponse();
            Map<String, Object> responseFields = hit.getSource();
            serviceWiseResponse.setOwnersName(responseFields.get(OWNERS_NAME).toString());
            serviceWiseResponse.setDoorNo(responseFields.get(DOOR_NO).toString());
            serviceWiseResponse.setArrearDemand(new BigDecimal(responseFields.get(ARREARDEMAND).toString()));
            serviceWiseResponse.setArrearPenDemand(new BigDecimal(responseFields.get(ARREAR_INTEREST_DMD).toString()));
            serviceWiseResponse.setArrearTotalDemand(new BigDecimal(responseFields.get(ARREARDEMAND).toString())
                    .add(new BigDecimal(responseFields.get(ARREAR_INTEREST_DMD).toString())));
            serviceWiseResponse.setCurrentDemand(new BigDecimal(responseFields.get(CURRENT_DMD).toString()));
            serviceWiseResponse.setCurrentPenDemand(new BigDecimal(responseFields.get(CURR_INTEREST_DMD).toString()));
            serviceWiseResponse.setCurrentTotalDemand(new BigDecimal(responseFields.get(CURRENT_DMD).toString())
                    .add(new BigDecimal(responseFields.get(CURR_INTEREST_DMD).toString())));
            serviceWiseResponse.setTotalDemand(new BigDecimal(responseFields.get(ARREAR_INTEREST_DMD).toString())
                    .add(new BigDecimal(responseFields.get(ARREARDEMAND).toString())
                            .add(new BigDecimal(responseFields.get(CURRENT_DMD).toString())
                                    .add(new BigDecimal(responseFields.get(CURR_INTEREST_DMD).toString())))));
            serviceWiseResponse.setArrearCollection(new BigDecimal(responseFields.get(ARREAR_COLLECTION).toString()));
            serviceWiseResponse.setArrearPenCollection(new BigDecimal(responseFields.get(ARREAR_INTEREST_COLLECTION).toString()));
            serviceWiseResponse.setArrearTotalCollection(new BigDecimal(responseFields.get(ARREAR_COLLECTION).toString())
                    .add(new BigDecimal(responseFields.get(ARREAR_INTEREST_COLLECTION).toString())));
            serviceWiseResponse.setCurrentCollection(new BigDecimal(responseFields.get(CURRENT_COLLECTION).toString()));
            serviceWiseResponse
                    .setCurrentPenCollection(new BigDecimal(responseFields.get(CURRENT_INTEREST_COLLECTION).toString()));
            serviceWiseResponse.setCurrentTotalCollection(new BigDecimal(responseFields.get(CURRENT_COLLECTION).toString())
                    .add(new BigDecimal(responseFields.get(CURRENT_INTEREST_COLLECTION).toString())));
            serviceWiseResponse.setTotalCollection(new BigDecimal(responseFields.get(ARREAR_COLLECTION).toString())
                    .add(new BigDecimal(responseFields.get(ARREAR_INTEREST_COLLECTION).toString())
                            .add(new BigDecimal(responseFields.get(CURRENT_COLLECTION).toString())
                                    .add(new BigDecimal(responseFields.get(CURRENT_INTEREST_COLLECTION).toString())))));
            serviceWiseResponse.setArrearBalance(new BigDecimal(responseFields.get(ARREARDEMAND).toString())
                    .subtract(new BigDecimal(responseFields.get(ARREAR_COLLECTION).toString())));
            serviceWiseResponse.setArrearPenBalance(new BigDecimal(responseFields.get(ARREAR_INTEREST_DMD).toString())
                    .subtract(new BigDecimal(responseFields.get(ARREAR_INTEREST_COLLECTION).toString())));
            serviceWiseResponse.setCurrentBalance(new BigDecimal(responseFields.get(CURRENT_DMD).toString())
                    .subtract(new BigDecimal(responseFields.get(CURRENT_COLLECTION).toString())));
            serviceWiseResponse.setCurrentPenBalance(new BigDecimal(responseFields.get(CURR_INTEREST_DMD).toString())
                    .subtract(new BigDecimal(responseFields.get(CURRENT_INTEREST_COLLECTION).toString())));
            serviceWiseResponse.setTotalBalance(new BigDecimal(responseFields.get(ARREARDEMAND).toString())
                    .add(new BigDecimal(responseFields.get(ARREAR_INTEREST_DMD).toString())
                            .add(new BigDecimal(responseFields.get(CURRENT_DMD).toString())
                                    .add(new BigDecimal(responseFields.get(CURR_INTEREST_DMD).toString()))))
                    .subtract(new BigDecimal(responseFields.get(ARREAR_COLLECTION).toString())
                            .add(new BigDecimal(responseFields.get(ARREAR_INTEREST_COLLECTION).toString())
                                    .add(new BigDecimal(responseFields.get(CURRENT_COLLECTION).toString()))
                                    .add(new BigDecimal(responseFields.get(CURRENT_INTEREST_COLLECTION).toString())))));
            serviceWiseResponse.setDrillDownType(responseFields.get("assessmentNo").toString());
            dcbData.add(serviceWiseResponse);
        }
        return dcbData;
    }

    private List<YearWiseDCBReponse> getResponseForDCB(PTYearWiseDCBRequest serviceRequest) {
        @SuppressWarnings("rawtypes")
        AggregationBuilder aggregationBuilder = getAggregationBuilderForDCB(serviceRequest.getType());
        SearchResponse response;
        response = elasticsearchTemplate.getClient().prepareSearch(PROPERTY_TAX_INDEX_PREFIX + serviceRequest.getYearIndex())
                .setQuery(getBoolQuery(serviceRequest)).addAggregation(aggregationBuilder).execute().actionGet();

        String aggregationTerms = GROUP_TYPE_WARD.equalsIgnoreCase(serviceRequest.getType()) ? GROUP_TYPE_WARD
                : GROUP_TYPE_BLOCK;
        Sum arrearDemand;
        Sum arrearPenDemand;
        Sum currentDemand;
        Sum currentPenDemand;
        Sum arrearCollected;
        Sum arrearPenCollected;
        Sum currentCollected;
        Sum currentPenCollected;
        Terms aggTerms = response.getAggregations().get(aggregationTerms);
        List<YearWiseDCBReponse> serviceWiseResponses = new ArrayList<>();
        for (Terms.Bucket entry : aggTerms.getBuckets()) {
            YearWiseDCBReponse serviceWiseResponse = new YearWiseDCBReponse();
            arrearDemand = entry.getAggregations().get(ARREARDEMAND);
            arrearPenDemand = entry.getAggregations().get(ARREAR_INTEREST_DMD);
            currentDemand = entry.getAggregations().get(CURRENT_DMD);
            currentPenDemand = entry.getAggregations().get(CURR_INTEREST_DMD);
            arrearCollected = entry.getAggregations().get(ARREAR_COLLECTION);
            arrearPenCollected = entry.getAggregations().get(ARREAR_INTEREST_COLLECTION);
            currentCollected = entry.getAggregations().get(CURRENT_COLLECTION);
            currentPenCollected = entry.getAggregations().get(CURRENT_INTEREST_COLLECTION);
            serviceWiseResponse.setCount(entry.getDocCount());
            serviceWiseResponse
                    .setArrearDemand(BigDecimal.valueOf(arrearDemand.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP));
            serviceWiseResponse
                    .setArrearPenDemand(BigDecimal.valueOf(arrearPenDemand.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP));
            serviceWiseResponse
                    .setArrearTotalDemand(BigDecimal.valueOf(arrearDemand.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP)
                            .add(BigDecimal.valueOf(arrearPenDemand.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP)));
            serviceWiseResponse.setCurrentDemand(
                    BigDecimal.valueOf(currentDemand.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP));
            serviceWiseResponse.setCurrentPenDemand(
                    BigDecimal.valueOf(currentPenDemand.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP));
            serviceWiseResponse
                    .setCurrentTotalDemand(BigDecimal.valueOf(currentDemand.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP)
                            .add(BigDecimal.valueOf(currentPenDemand.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP)));
            serviceWiseResponse.setTotalDemand(BigDecimal.valueOf(arrearDemand.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP)
                    .add(BigDecimal.valueOf(arrearPenDemand.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP))
                    .add(BigDecimal.valueOf(currentDemand.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP))
                    .add(BigDecimal.valueOf(currentPenDemand.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP)));
            serviceWiseResponse.setArrearCollection(
                    BigDecimal.valueOf(arrearCollected.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP));
            serviceWiseResponse.setArrearPenCollection(
                    BigDecimal.valueOf(arrearPenCollected.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP));
            serviceWiseResponse
                    .setArrearTotalCollection(
                            BigDecimal.valueOf(arrearCollected.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP)
                                    .add(BigDecimal.valueOf(arrearPenCollected.getValue()).setScale(0,
                                            BigDecimal.ROUND_HALF_UP)));
            serviceWiseResponse.setCurrentCollection(
                    BigDecimal.valueOf(currentCollected.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP));
            serviceWiseResponse.setCurrentPenCollection(
                    BigDecimal.valueOf(currentPenCollected.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP));
            serviceWiseResponse
                    .setCurrentTotalCollection(
                            BigDecimal.valueOf(currentCollected.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP)
                                    .add(BigDecimal.valueOf(currentPenCollected.getValue()).setScale(0,
                                            BigDecimal.ROUND_HALF_UP)));
            serviceWiseResponse
                    .setTotalCollection(BigDecimal.valueOf(arrearCollected.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP)
                            .add(BigDecimal.valueOf(arrearPenCollected.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP))
                            .add(BigDecimal.valueOf(currentCollected.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP))
                            .add(BigDecimal.valueOf(currentPenCollected.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP)));

            serviceWiseResponse
                    .setArrearBalance(BigDecimal.valueOf(arrearDemand.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP)
                            .subtract(BigDecimal.valueOf(arrearCollected.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP)));

            serviceWiseResponse
                    .setArrearPenBalance(BigDecimal.valueOf(arrearPenDemand.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP)
                            .subtract(BigDecimal.valueOf(arrearPenCollected.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP)));

            serviceWiseResponse
                    .setCurrentBalance(BigDecimal.valueOf(currentDemand.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP)
                            .subtract(BigDecimal.valueOf(currentCollected.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP)));

            serviceWiseResponse
                    .setCurrentPenBalance(BigDecimal.valueOf(currentPenDemand.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP)
                            .subtract(
                                    BigDecimal.valueOf(currentPenCollected.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP)));

            serviceWiseResponse.setTotalBalance(BigDecimal.valueOf(arrearDemand.getValue()).setScale(0,
                    BigDecimal.ROUND_HALF_UP).add(BigDecimal.valueOf(arrearPenDemand.getValue()).setScale(0,
                            BigDecimal.ROUND_HALF_UP)
                            .add(BigDecimal.valueOf(currentDemand.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP))
                            .add(BigDecimal.valueOf(currentPenDemand.getValue()).setScale(0, BigDecimal.ROUND_HALF_UP)
                                    .subtract(BigDecimal.valueOf(arrearCollected.getValue()).setScale(0,
                                            BigDecimal.ROUND_HALF_UP)
                                            .add(BigDecimal.valueOf(arrearPenCollected.getValue()).setScale(0,
                                                    BigDecimal.ROUND_HALF_UP))
                                            .add(BigDecimal.valueOf(currentCollected.getValue()).setScale(0,
                                                    BigDecimal.ROUND_HALF_UP))
                                            .add(BigDecimal.valueOf(currentPenCollected.getValue()).setScale(0,
                                                    BigDecimal.ROUND_HALF_UP))))));

            serviceWiseResponse.setDrillDownType(entry.getKeyAsString());
            serviceWiseResponses.add(serviceWiseResponse);
        }

        return serviceWiseResponses;
    }

    @SuppressWarnings("rawtypes")
    private AggregationBuilder getAggregationBuilderForDCB(final String groupingField) {
        String field = GROUP_TYPE_WARD.equalsIgnoreCase(groupingField) ? REVENUE_WARD : BLOCK;
        return AggregationBuilders.terms(groupingField).field(field).size(120)
                .subAggregation(AggregationBuilders.sum(ARREARDEMAND).field(ARREARDEMAND))
                .subAggregation(AggregationBuilders.sum(ARREAR_INTEREST_DMD).field(ARREAR_INTEREST_DMD))
                .subAggregation(AggregationBuilders.sum(CURRENT_DMD).field(CURRENT_DMD))
                .subAggregation(AggregationBuilders.sum(CURR_INTEREST_DMD).field(CURR_INTEREST_DMD))
                .subAggregation(AggregationBuilders.sum(ARREAR_COLLECTION).field(ARREAR_COLLECTION))
                .subAggregation(AggregationBuilders.sum(ARREAR_INTEREST_COLLECTION).field(ARREAR_INTEREST_COLLECTION))
                .subAggregation(AggregationBuilders.sum(CURRENT_COLLECTION).field(CURRENT_COLLECTION)).subAggregation(
                        AggregationBuilders.sum(CURRENT_INTEREST_COLLECTION).field(CURRENT_INTEREST_COLLECTION));

    }

    private BoolQueryBuilder getBoolQuery(PTYearWiseDCBRequest serviceRequest) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .filter(QueryBuilders.matchQuery(CITY_CODE, ApplicationThreadLocals.getCityCode()));
        boolQuery = boolQuery.filter(QueryBuilders.matchQuery(IS_UNDER_COURT, serviceRequest.getIsCourtCase()));
        boolQuery = boolQuery.mustNot(QueryBuilders.termsQuery(CATEGORY, Arrays.asList(EWHS_CODE, VLT_CODE)));
        if (StringUtils.isNotBlank(serviceRequest.getPropertyUsage()))
            boolQuery = boolQuery.filter(QueryBuilders.matchQuery(CATEGORY, serviceRequest.getPropertyUsage()));
        if (GROUP_TYPE_PROPERTY.equalsIgnoreCase(serviceRequest.getType()) && StringUtils.isNotBlank(serviceRequest.getBlock()))
            boolQuery = boolQuery.filter(QueryBuilders.matchQuery(BLOCK, serviceRequest.getBlock()));
        if (StringUtils.isNotBlank(serviceRequest.getRevenueWard()))
            boolQuery = boolQuery.filter(QueryBuilders.matchQuery(REVENUE_WARD, serviceRequest.getRevenueWard()));
        return boolQuery;
    }

}