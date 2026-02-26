package com.furrow.app.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.furrow.app.data.FurrowModule
import com.furrow.app.ui.components.AppScaffold
import com.furrow.app.ui.components.AppTopBar
import com.furrow.app.ui.components.ListRow
import com.furrow.app.ui.components.Panel
import com.furrow.app.ui.theme.AppSpacing
import com.furrow.app.ui.theme.TextSecondary

@Composable
fun MoreScreen(
    enabledModules: Set<String>,
    onModuleClick: (String) -> Unit,
) {
    AppScaffold(
        topBar = { AppTopBar(title = "More") },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(start = AppSpacing.md, top = AppSpacing.md, end = AppSpacing.md, bottom = AppSpacing.bottomListPadding),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
        ) {
            Panel(contentPadding = PaddingValues(0.dp)) {
                Text(
                    "Modules",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(AppSpacing.md),
                )
                val enabledList = FurrowModule.entries
                    .filter { it.key in enabledModules }
                    .sortedBy { it.displayOrder }
                enabledList.forEachIndexed { index, module ->
                    ListRow(
                        title = module.displayName,
                        leadingIcon = {
                            Icon(
                                imageVector = module.unselectedIcon,
                                contentDescription = module.displayName,
                                modifier = Modifier.size(18.dp),
                                tint = TextSecondary,
                            )
                        },
                        onClick = { onModuleClick(module.key) },
                        showDivider = index < enabledList.lastIndex,
                    )
                }
            }
        }
    }
}
