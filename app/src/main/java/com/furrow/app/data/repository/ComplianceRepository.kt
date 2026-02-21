package com.furrow.app.data.repository

import com.furrow.app.data.local.dao.ComplianceDocumentDao
import com.furrow.app.data.local.dao.ComplianceInspectionDao
import com.furrow.app.data.local.dao.LabelTemplateDao
import com.furrow.app.data.local.dao.LicensePermitDao
import com.furrow.app.data.local.dao.SalesTrackerDao
import com.furrow.app.data.local.entity.ComplianceDocument
import com.furrow.app.data.local.entity.ComplianceInspection
import com.furrow.app.data.local.entity.LabelTemplate
import com.furrow.app.data.local.entity.LicensePermit
import com.furrow.app.data.local.entity.SalesTracker
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ComplianceRepository @Inject constructor(
    private val licensePermitDao: LicensePermitDao,
    private val complianceInspectionDao: ComplianceInspectionDao,
    private val salesTrackerDao: SalesTrackerDao,
    private val labelTemplateDao: LabelTemplateDao,
    private val complianceDocumentDao: ComplianceDocumentDao,
) {

    // --- License Permits ---

    suspend fun insertLicensePermit(licensePermit: LicensePermit): Long =
        licensePermitDao.insert(licensePermit)

    suspend fun updateLicensePermit(licensePermit: LicensePermit) =
        licensePermitDao.update(licensePermit)

    suspend fun deleteLicensePermit(licensePermit: LicensePermit) =
        licensePermitDao.delete(licensePermit)

    fun getAllLicensePermits(): Flow<List<LicensePermit>> = licensePermitDao.getAll()

    fun getLicensePermitById(id: Long): Flow<LicensePermit?> = licensePermitDao.getById(id)

    fun getLicensePermitsExpiringSoon(beforeDate: Long): Flow<List<LicensePermit>> =
        licensePermitDao.getExpiringSoon(beforeDate)

    // --- Compliance Inspections ---

    suspend fun insertComplianceInspection(complianceInspection: ComplianceInspection): Long =
        complianceInspectionDao.insert(complianceInspection)

    suspend fun updateComplianceInspection(complianceInspection: ComplianceInspection) =
        complianceInspectionDao.update(complianceInspection)

    suspend fun deleteComplianceInspection(complianceInspection: ComplianceInspection) =
        complianceInspectionDao.delete(complianceInspection)

    fun getAllComplianceInspections(): Flow<List<ComplianceInspection>> =
        complianceInspectionDao.getAll()

    fun getComplianceInspectionById(id: Long): Flow<ComplianceInspection?> =
        complianceInspectionDao.getById(id)

    // --- Sales Trackers ---

    suspend fun insertSalesTracker(salesTracker: SalesTracker): Long =
        salesTrackerDao.insert(salesTracker)

    suspend fun updateSalesTracker(salesTracker: SalesTracker) =
        salesTrackerDao.update(salesTracker)

    suspend fun deleteSalesTracker(salesTracker: SalesTracker) =
        salesTrackerDao.delete(salesTracker)

    fun getAllSalesTrackers(): Flow<List<SalesTracker>> = salesTrackerDao.getAll()

    fun getSalesTrackerById(id: Long): Flow<SalesTracker?> = salesTrackerDao.getById(id)

    fun getSalesTrackersByYear(year: Int): Flow<List<SalesTracker>> =
        salesTrackerDao.getByYear(year)

    // --- Label Templates ---

    suspend fun insertLabelTemplate(labelTemplate: LabelTemplate): Long =
        labelTemplateDao.insert(labelTemplate)

    suspend fun updateLabelTemplate(labelTemplate: LabelTemplate) =
        labelTemplateDao.update(labelTemplate)

    suspend fun deleteLabelTemplate(labelTemplate: LabelTemplate) =
        labelTemplateDao.delete(labelTemplate)

    fun getAllLabelTemplates(): Flow<List<LabelTemplate>> = labelTemplateDao.getAll()

    fun getLabelTemplateById(id: Long): Flow<LabelTemplate?> = labelTemplateDao.getById(id)

    // --- Compliance Documents ---

    suspend fun insertComplianceDocument(complianceDocument: ComplianceDocument): Long =
        complianceDocumentDao.insert(complianceDocument)

    suspend fun updateComplianceDocument(complianceDocument: ComplianceDocument) =
        complianceDocumentDao.update(complianceDocument)

    suspend fun deleteComplianceDocument(complianceDocument: ComplianceDocument) =
        complianceDocumentDao.delete(complianceDocument)

    fun getAllComplianceDocuments(): Flow<List<ComplianceDocument>> =
        complianceDocumentDao.getAll()

    fun getComplianceDocumentById(id: Long): Flow<ComplianceDocument?> =
        complianceDocumentDao.getById(id)
}
