package com.furrow.app.ui.compliance

import androidx.lifecycle.viewModelScope
import com.furrow.app.data.local.entity.ComplianceDocument
import com.furrow.app.data.local.entity.ComplianceInspection
import com.furrow.app.data.local.entity.LabelTemplate
import com.furrow.app.data.local.entity.LicensePermit
import com.furrow.app.data.local.entity.SalesTracker
import com.furrow.app.data.repository.ComplianceRepository
import com.furrow.app.data.repository.UserProfileRepository
import com.furrow.app.ui.FurrowViewModel
import com.furrow.app.util.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class ComplianceViewModel @Inject constructor(
    private val repository: ComplianceRepository,
    private val userProfileRepository: UserProfileRepository,
) : FurrowViewModel() {

    internal val zone: ZoneId = runBlocking {
        DateUtil.profileZone(userProfileRepository.getProfile().firstOrNull())
    }

    val licensePermits: StateFlow<List<LicensePermit>> = repository.getAllLicensePermits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val ninetyDaysFromNow = Instant.now().atZone(zone)
        .plusDays(90).toInstant().toEpochMilli()

    val expiringSoonPermits: StateFlow<List<LicensePermit>> = repository.getLicensePermitsExpiringSoon(ninetyDaysFromNow)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inspections: StateFlow<List<ComplianceInspection>> = repository.getAllComplianceInspections()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val salesTrackers: StateFlow<List<SalesTracker>> = repository.getAllSalesTrackers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val labelTemplates: StateFlow<List<LabelTemplate>> = repository.getAllLabelTemplates()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val documents: StateFlow<List<ComplianceDocument>> = repository.getAllComplianceDocuments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // -- Lookups for edit mode --
    fun getLicensePermitById(id: Long) = repository.getLicensePermitById(id)
    fun getInspectionById(id: Long) = repository.getComplianceInspectionById(id)
    fun getSalesTrackerById(id: Long) = repository.getSalesTrackerById(id)
    fun getLabelTemplateById(id: Long) = repository.getLabelTemplateById(id)
    fun getDocumentById(id: Long) = repository.getComplianceDocumentById(id)

    // -- Actions --
    fun addLicensePermit(p: LicensePermit) { safeLaunch { repository.insertLicensePermit(p) } }
    fun updateLicensePermit(p: LicensePermit) { safeLaunch { repository.updateLicensePermit(p) } }
    fun deleteLicensePermit(p: LicensePermit) { safeLaunch { repository.deleteLicensePermit(p) } }

    fun addInspection(i: ComplianceInspection) { safeLaunch { repository.insertComplianceInspection(i) } }
    fun updateInspection(i: ComplianceInspection) { safeLaunch { repository.updateComplianceInspection(i) } }
    fun deleteInspection(i: ComplianceInspection) { safeLaunch { repository.deleteComplianceInspection(i) } }

    fun addSalesTracker(s: SalesTracker) { safeLaunch { repository.insertSalesTracker(s) } }
    fun updateSalesTracker(s: SalesTracker) { safeLaunch { repository.updateSalesTracker(s) } }
    fun deleteSalesTracker(s: SalesTracker) { safeLaunch { repository.deleteSalesTracker(s) } }

    fun addLabelTemplate(l: LabelTemplate) { safeLaunch { repository.insertLabelTemplate(l) } }
    fun updateLabelTemplate(l: LabelTemplate) { safeLaunch { repository.updateLabelTemplate(l) } }
    fun deleteLabelTemplate(l: LabelTemplate) { safeLaunch { repository.deleteLabelTemplate(l) } }

    fun addDocument(d: ComplianceDocument) { safeLaunch { repository.insertComplianceDocument(d) } }
    fun updateDocument(d: ComplianceDocument) { safeLaunch { repository.updateComplianceDocument(d) } }
    fun deleteDocument(d: ComplianceDocument) { safeLaunch { repository.deleteComplianceDocument(d) } }
}
