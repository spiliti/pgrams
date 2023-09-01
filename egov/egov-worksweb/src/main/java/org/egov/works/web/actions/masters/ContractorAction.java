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
package org.egov.works.web.actions.masters;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.egov.commons.Bank;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.egf.commons.bank.service.CreateBankService;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.admin.master.service.UserService;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.infra.web.struts.actions.SearchFormAction;
import org.egov.infstr.search.SearchQuery;
import org.egov.infstr.services.PersistenceService;
import org.egov.works.master.service.ContractorGradeService;
import org.egov.works.master.service.ContractorService;
import org.egov.works.models.masters.Contractor;
import org.egov.works.models.masters.ContractorDetail;
import org.egov.works.services.WorksService;
import org.egov.works.utils.WorksConstants;
import org.egov.works.utils.WorksUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@ParentPackage("egov")
@Results({ @Result(name = ContractorAction.NEW, location = "contractor-new.jsp"),
        @Result(name = ContractorAction.SEARCH_CONTRACTOR, location = "contractor-searchContractor.jsp"),
        @Result(name = ContractorAction.SEARCH, location = "contractor-search.jsp"),
        @Result(name = ContractorAction.SUCCESS, location = "contractor-success.jsp"),
        @Result(name = ContractorAction.EDIT, location = "contractor-edit.jsp"),
        @Result(name = ContractorAction.VIEW, location = "contractor-view.jsp") })
public class ContractorAction extends SearchFormAction {

    private static final long serialVersionUID = 3167651186547987956L;

    private static final Logger logger = Logger.getLogger(ContractorAction.class);

    public static final String SEARCH_CONTRACTOR = "searchContractor";
    public static final String SEARCH = "search";
    public static final String SUCCESS = "success";
    @Autowired
    private ContractorService contractorService;
    private Contractor contractor = new Contractor();

    private Map<String, String> exmptionMap = ContractorService.exemptionForm;

    private List<Contractor> contractorList = null;
    private List<ContractorDetail> actionContractorDetails = new LinkedList<ContractorDetail>();
    private Long id;
    private String mode;

    @Autowired
    private UserService userService;
    private WorksService worksService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private EgwStatusHibernateDAO egwStatusHibDAO;
    @Autowired
    private ContractorGradeService contractorGradeService;

    @Autowired
    private WorksUtils worksUtils;

    private String contractorName;
    private String contractorCode;
    private Long departmentId;
    private Long gradeId;
    private Date searchDate;
    private boolean sDisabled;

    private Integer statusId;
    private List<ContractorDetail> contractorDetailList = null;
    private PersistenceService<ContractorDetail, Long> contractorDetailService;
    private Integer rowId;
    private Long defaultDepartmentId;
    private boolean contractorCodeAutoGeneration;

    private Map<String, Object> criteriaMap = null;

    @Autowired
    private CreateBankService createBankService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public String execute() {
        return list();
    }

    @Action(value = "/masters/contractor-newform")
    public String newform() {
        return NEW;
    }

    public String list() {
        contractorList = contractorService.getAllContractors();
        return SUCCESS;
    }

    @Action(value = "/masters/contractor-edit")
    public String edit() {
        contractor = contractorService.findById(contractor.getId(), false);
        if (mode.equals(WorksConstants.EDIT))
            return EDIT;
        else
            return VIEW;
    }

    @Override
    @Action(value = "/masters/contractor-search")
    public String search() {
        return SEARCH_CONTRACTOR;
    }

    @Action(value = "/masters/contractor-searchContractor")
    public String searchContractor() {
        return SEARCH_CONTRACTOR;
    }

    @Action(value = "/masters/contractor-viewResult")
    public String viewResult() {
        setPageSize(WorksConstants.PAGE_SIZE);
        contractorList = contractorService.getContractorListForCriterias(createCriteriaMap());
        super.search();
        return SEARCH_CONTRACTOR;
    }

    @Action(value = "/masters/contractor-save")
    public String save() {
        if (actionContractorDetails.isEmpty()) {
            addActionMessage(getText("contractor.details.altleastone_details_needed"));
            return NEW;
        }
        if (contractor.getBank().getId() != -1 && contractor.getBank().getId() != 0 && contractor.getBank() != null
                && contractor.getBank().getId() != null) {
            final Integer bankId = contractor.getBank().getId();
            entityManager.detach(contractor.getBank());
            final Bank bank = entityManager.find(Bank.class, bankId);
            contractor.setBank(bank);
        } else
            contractor.setBank(null);
        populateContractorDetails(mode);
        if (isContractorCodeAutoGeneration() && StringUtils.isBlank(mode))
            contractor.setCode(contractorService.generateContractorCode(contractor));
        contractor = contractorService.persist(contractor);
        if (mode == null || org.apache.commons.lang.StringUtils.isEmpty(mode))
            contractorService.createAccountDetailKey(contractor);

        // TODO:Fixme - Added temporarily since AccountDetailKey was not persisting. Need to find the fix for this and remove
        // below line of code
        contractorService.persist(contractor);
        if (StringUtils.isBlank(mode))
            addActionMessage(getText("contractor.save.success", new String[] { contractor.getCode() }));
        else
            addActionMessage(getText("contractor.modified.success"));
        return SUCCESS;

    }

    @Action(value = "/masters/contractor-searchPage")
    public String searchPage() {
        final String negDate = (String) request.get(WorksConstants.NEGOTIATION_DATE);
        if (negDate != null) {
            final SimpleDateFormat dftDateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            try {
                searchDate = dftDateFormatter.parse(negDate);
            } catch (final ParseException e) {
                logger.error(WorksConstants.NEGOTIATION_DATE_FORMAT_INVALID);
            }
        }
        return SEARCH;
    }

    @Action(value = "/masters/contractor-searchResult")
    public String searchResult() {
        contractorService.searchContractor(createCriteriaMap());
        return SEARCH;

    }

    protected void populateContractorDetails(final String mode) {
        contractor.getContractorDetails().clear();

        for (final ContractorDetail contractorDetail : actionContractorDetails)
            if (validContractorDetail(contractorDetail)) {
                contractorDetail
                        .setDepartment(departmentService.getDepartmentById(contractorDetail.getDepartment().getId()));
                contractorDetail.setStatus(egwStatusHibDAO.findById(contractorDetail.getStatus().getId(), false));
                if (contractorDetail.getGrade().getId() == null)
                    contractorDetail.setGrade(null);
                else
                    contractorDetail.setGrade(
                            contractorGradeService.getContractorGradeById(contractorDetail.getGrade().getId()));
                if (contractorDetail.getCategory() == null || "0".equals(contractorDetail.getCategory()))
                    contractorDetail.setCategory(null);
                contractorDetail.setContractor(contractor);
                if (mode.equals(WorksConstants.EDIT))
                    setPrimaryDetails(contractorDetail);
                contractor.addContractorDetail(contractorDetail);
            } else if (contractorDetail != null) {
                if (contractorDetail.getDepartment() == null || contractorDetail.getDepartment().getId() == null)
                    contractorDetail.setDepartment(null);
                else
                    contractorDetail.setDepartment(
                            departmentService.getDepartmentById(contractorDetail.getDepartment().getId()));
                if (contractorDetail.getStatus() == null || contractorDetail.getStatus().getId() == null)
                    contractorDetail.setStatus(null);
                else
                    contractorDetail.setStatus(egwStatusHibDAO.findById(contractorDetail.getStatus().getId(), false));
                if (contractorDetail.getGrade() == null || contractorDetail.getGrade().getId() == null)
                    contractorDetail.setGrade(null);
                else
                    contractorDetail.setGrade(
                            contractorGradeService.getContractorGradeById(contractorDetail.getGrade().getId()));
                if (contractorDetail.getCategory() == null || "0".equals(contractorDetail.getCategory()))
                    contractorDetail.setCategory(null);
                contractorDetail.setContractor(contractor);
                if (mode.equals(WorksConstants.EDIT))
                    setPrimaryDetails(contractorDetail);
                contractor.addContractorDetail(contractorDetail);
            }
    }

    protected boolean validContractorDetail(final ContractorDetail contractorDetail) {

        if (contractorDetail != null && contractorDetail.getDepartment() != null && contractorDetail.getStatus() != null
                && contractorDetail.getDepartment().getId() != null && contractorDetail.getStatus().getId() != null) {
            validateContractorMasterMandatoryFields(contractorDetail);
            return true;
        }
        return false;
    }

    @Override
    public Object getModel() {
        return contractor;
    }

    public List<Contractor> getContractorList() {
        return contractorList;
    }

    @Override
    public void prepare() {
        if (id != null)
            contractor = contractorService.findById(id, false);
        super.prepare();
        setupDropdownDataExcluding(WorksConstants.BANK);
        addDropdownData("departmentList", departmentService.getAllDepartments());
        addDropdownData("gradeList", contractorGradeService.getAllContractorGrades());
        addDropdownData("bankList", createBankService.getByIsActiveTrueOrderByName());
        addDropdownData("statusList", egwStatusHibDAO.getStatusByModule(WorksConstants.STATUS_MODULE_NAME));
        defaultDepartmentId = worksUtils.getDefaultDepartmentId();
        addDropdownData("contractorDetailsCategoryValues",
                Arrays.asList(contractorService.getContractorMasterCategoryValues()));
        addDropdownData("contractorMasterMandatoryFields",
                Arrays.asList(contractorService.getcontractorMasterSetMandatoryFields()));
        addDropdownData("contractorMasterHiddenFields",
                Arrays.asList(contractorService.getcontractorMasterSetHiddenFields()));
        setContractorMasterCodeAutoGenerationConfigValue();
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Contractor getContractor() {
        return contractor;
    }

    public void setContractor(final Contractor contractor) {
        this.contractor = contractor;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(final String mode) {
        this.mode = mode;
    }

    public List<ContractorDetail> getActionContractorDetails() {
        return actionContractorDetails;
    }

    public void setActionContractorDetails(final List<ContractorDetail> actionContractorDetails) {
        this.actionContractorDetails = actionContractorDetails;
    }

    public String getContractorName() {
        return contractorName;
    }

    public void setContractorName(final String contractorName) {
        this.contractorName = contractorName;
    }

    public String getContractorCode() {
        return contractorCode;
    }

    public void setContractorCode(final String contractorCode) {
        this.contractorCode = contractorCode;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(final Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getGradeId() {
        return gradeId;
    }

    public void setGradeId(final Long gradeId) {
        this.gradeId = gradeId;
    }

    public void setWorksService(final WorksService worksService) {
        this.worksService = worksService;
    }

    public Date getSearchDate() {
        return searchDate;
    }

    public void setSearchDate(final Date searchDate) {
        this.searchDate = searchDate;
    }

    public Map<String, String> getExmptionMap() {
        return exmptionMap;
    }

    public void setExmptionMap(final Map<String, String> exmptionMap) {
        this.exmptionMap = exmptionMap;
    }

    /**
     * @return the statusId
     */
    public Integer getStatusId() {
        return statusId;
    }

    /**
     * @param statusId the statusId to set
     */
    public void setStatusId(final Integer statusId) {
        this.statusId = statusId;
    }

    /**
     * @return the contractorDetailList
     */
    public List<ContractorDetail> getContractorDetailList() {
        return contractorDetailList;
    }

    /**
     * @param contractorDetailList the contractorDetailList to set
     */
    public void setContractorDetailList(final List<ContractorDetail> contractorDetailList) {
        this.contractorDetailList = contractorDetailList;
    }

    /**
     * @return the contractorDetailService
     */
    public PersistenceService<ContractorDetail, Long> getContractorDetailService() {
        return contractorDetailService;
    }

    /**
     * @param contractorDetailService the contractorDetailService to set
     */
    public void setContractorDetailService(final PersistenceService<ContractorDetail, Long> contractorDetailService) {
        this.contractorDetailService = contractorDetailService;
    }

    public boolean isSDisabled() {
        return sDisabled;
    }

    @Override
    public SearchQuery prepareQuery(final String sortField, final String sortOrder) {
        return contractorService.prepareQuery(createCriteriaMap());
    }

    public Integer getRowId() {
        return rowId;
    }

    public void setRowId(final Integer rowId) {
        this.rowId = rowId;
    }

    // TODO: Need to remove this method after getting better alternate option
    private ContractorDetail setPrimaryDetails(final ContractorDetail contractorDetail) {
        final User user = userService.getUserById(worksService.getCurrentLoggedInUserId());
        contractorDetail.setCreatedBy(user);
        contractorDetail.setCreatedDate(new Date());
        return contractorDetail;
    }

    private Map<String, Object> createCriteriaMap() {
        criteriaMap = new HashMap<String, Object>();
        criteriaMap.put(WorksConstants.CONTRACTOR_NAME, contractorName);
        criteriaMap.put(WorksConstants.CONTRACTOR_CODE, contractorCode);
        criteriaMap.put(WorksConstants.DEPARTMENT_ID, departmentId);
        criteriaMap.put(WorksConstants.STATUS_ID, statusId);
        criteriaMap.put(WorksConstants.GRADE_ID, gradeId);
        criteriaMap.put(WorksConstants.SEARCH_DATE, searchDate);
        return criteriaMap;
    }

    public List<ValidationError> getContractorMasterMandatoryFieldsErrors(final Contractor contractor,
            final String[] mandatoryFields) {
        final List<ValidationError> validationErrors = new ArrayList<ValidationError>();
        final String[] contractorMasterMandatoryFields = mandatoryFields;

        for (final String val : contractorMasterMandatoryFields) {
            if ("mobileNumber".equals(val) && StringUtils.isBlank(contractor.getMobileNumber()))
                addValidationError(validationErrors, "contractor.mobileNumber", "depositworks.roadcut.enter.mobileno");
            if ("bankAccount".equals(val) && StringUtils.isBlank(contractor.getBankAccount()))
                addValidationError(validationErrors, "contractor.bankAccount", "contractor.bankaccount.null");
            if ("bank".equals(val) && (contractor.getBank() == null || contractor.getBank().getId() == -1))
                addValidationError(validationErrors, "contractor.bank.name", "bank.name.null");
            if ("ifscCode".equals(val) && StringUtils.isBlank(contractor.getIfscCode()))
                addValidationError(validationErrors, "contractor.ifscCode", "contractor.ifsccode.null");
            if ("tinNumber".equals(val) && StringUtils.isBlank(contractor.getTinNumber()))
                addValidationError(validationErrors, "contractor.tinNumber", "contractor.tinnumber.null");
            if ("panNumber".equals(val) && StringUtils.isBlank(contractor.getPanNumber()))
                addValidationError(validationErrors, "contractor.panNumber", "contractor.pannumber.null");
            if ("email".equals(val) && StringUtils.isBlank(contractor.getEmail()))
                addValidationError(validationErrors, "contractor.email", "depositworks.roadcut.enter.email");
            if ("contactPerson".equals(val) && StringUtils.isBlank(contractor.getContactPerson()))
                addValidationError(validationErrors, "contractor.contactPerson", "contractor.contactperson.null");
            if ("narration".equals(val) && StringUtils.isBlank(contractor.getNarration()))
                addValidationError(validationErrors, "contractor.narration", "contractor.narration.null");
            if ("correspondenceAddress".equals(val) && StringUtils.isBlank(contractor.getCorrespondenceAddress()))
                addValidationError(validationErrors, "contractor.correspondenceAddress",
                        "contractor.correspondenceAddress.null");
            if ("paymentAddress".equals(val) && StringUtils.isBlank(contractor.getPaymentAddress()))
                addValidationError(validationErrors, "contractor.paymentAddress", "contractor.paymentAddress.null");
            if ("pwdApprovalCode".equals(val) && StringUtils.isBlank(contractor.getPwdApprovalCode()))
                addValidationError(validationErrors, "contractor.pwdApprovalCode", "contractor.pwdApprovalCode.null");
            if ("exemptionForm".equals(val) && StringUtils.isBlank(contractor.getExemptionForm().toString()))
                addValidationError(validationErrors, "contractor.exemptionForm", "contractor.exemptionForm.null");

        }
        if (!isContractorCodeAutoGeneration() && StringUtils.isBlank(contractor.getCode()))
            addValidationError(validationErrors, "contractor.code", "contractor.code.null");

        if (isContractorCodeAutoGeneration() && contractor.getName().length() < 4)
            addValidationError(validationErrors, "contractor.name", "contractor.name.length");
        return validationErrors;
    }

    private void validateContractorMasterMandatoryFields(final ContractorDetail contractorDetail) {
        final List<ValidationError> validationErrors = new ArrayList<ValidationError>();
        final String[] contractorMasterMandatoryFields = contractorService.getContractorMasterMandatoryFields();

        validationErrors.addAll(getContractorMasterMandatoryFieldsErrors(contractor, contractorMasterMandatoryFields));

        for (final String val : contractorMasterMandatoryFields) {
            if ("registrationNumber".equals(val) && StringUtils.isBlank(contractorDetail.getRegistrationNumber()))
                addValidationError(validationErrors, "contractorDetail.registrationNumber",
                        "contractordetail.registrationnumber.required");
            if ("grade".equals(val)
                    && (contractorDetail.getGrade() == null || contractorDetail.getGrade().getId() == null))
                addValidationError(validationErrors, "contractorDetail.grade", "contractordetail.grade.required");
            if ("category".equals(val) && StringUtils.isBlank(contractorDetail.getCategory()))
                addValidationError(validationErrors, "contractorDetail.category", "contractordetail.category.required");
        }
        contractorDetail.setErrorList(validationErrors);

    }

    public void addValidationError(final List<ValidationError> errorList, final String errorCode,
            final String errorValue) {
        errorList.add(new ValidationError(errorCode, errorValue));
    }

    public Long getDefaultDepartmentId() {
        return defaultDepartmentId;
    }

    public void setDefaultDepartmentId(final Long defaultDepartmentId) {
        this.defaultDepartmentId = defaultDepartmentId;
    }

    public boolean isContractorCodeAutoGeneration() {
        return contractorCodeAutoGeneration;
    }

    public void setContractorCodeAutoGeneration(final boolean contractorCodeAutoGeneration) {
        this.contractorCodeAutoGeneration = contractorCodeAutoGeneration;
    }

    public void setContractorMasterCodeAutoGenerationConfigValue() {
        final String autoGenerateContractorCode = contractorService.getContractorMasterAutoCodeGenerateValue();
        if (autoGenerateContractorCode != null && "Yes".equals(autoGenerateContractorCode))
            setContractorCodeAutoGeneration(true);
        else
            setContractorCodeAutoGeneration(false);
    }

}