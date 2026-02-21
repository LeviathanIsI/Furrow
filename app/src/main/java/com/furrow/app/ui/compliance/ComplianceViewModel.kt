package com.furrow.app.ui.compliance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furrow.app.data.local.entity.ComplianceDocument
import com.furrow.app.data.local.entity.ComplianceInspection
import com.furrow.app.data.local.entity.LabelTemplate
import com.furrow.app.data.local.entity.LicensePermit
import com.furrow.app.data.local.entity.SalesTracker
import com.furrow.app.data.repository.ComplianceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class ComplianceViewModel @Inject constructor(
    private val repository: ComplianceRepository,
) : ViewModel() {

    val licensePermits: StateFlow<List<LicensePermit>> = repository.getAllLicensePermits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val ninetyDaysFromNow = Instant.now().atZone(ZoneId.systemDefault())
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
    fun addLicensePermit(p: LicensePermit) { viewModelScope.launch { repository.insertLicensePermit(p) } }
    fun updateLicensePermit(p: LicensePermit) { viewModelScope.launch { repository.updateLicensePermit(p) } }
    fun deleteLicensePermit(p: LicensePermit) { viewModelScope.launch { repository.deleteLicensePermit(p) } }

    fun addInspection(i: ComplianceInspection) { viewModelScope.launch { repository.insertComplianceInspection(i) } }
    fun updateInspection(i: ComplianceInspection) { viewModelScope.launch { repository.updateComplianceInspection(i) } }
    fun deleteInspection(i: ComplianceInspection) { viewModelScope.launch { repository.deleteComplianceInspection(i) } }

    fun addSalesTracker(s: SalesTracker) { viewModelScope.launch { repository.insertSalesTracker(s) } }
    fun updateSalesTracker(s: SalesTracker) { viewModelScope.launch { repository.updateSalesTracker(s) } }
    fun deleteSalesTracker(s: SalesTracker) { viewModelScope.launch { repository.deleteSalesTracker(s) } }

    fun addLabelTemplate(l: LabelTemplate) { viewModelScope.launch { repository.insertLabelTemplate(l) } }
    fun updateLabelTemplate(l: LabelTemplate) { viewModelScope.launch { repository.updateLabelTemplate(l) } }
    fun deleteLabelTemplate(l: LabelTemplate) { viewModelScope.launch { repository.deleteLabelTemplate(l) } }

    fun addDocument(d: ComplianceDocument) { viewModelScope.launch { repository.insertComplianceDocument(d) } }
    fun updateDocument(d: ComplianceDocument) { viewModelScope.launch { repository.updateComplianceDocument(d) } }
    fun deleteDocument(d: ComplianceDocument) { viewModelScope.launch { repository.deleteComplianceDocument(d) } }
}
