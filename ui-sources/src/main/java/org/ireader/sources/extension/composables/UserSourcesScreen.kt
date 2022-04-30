package org.ireader.sources.extension.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.ireader.common_models.entities.Catalog
import org.ireader.common_models.entities.CatalogInstalled
import org.ireader.common_models.entities.CatalogLocal
import org.ireader.core_api.log.Log
import org.ireader.core_ui.ui_components.TextSection
import org.ireader.sources.extension.CatalogItem
import org.ireader.sources.extension.CatalogsState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UserSourcesScreen(
    modifier: Modifier = Modifier,
    state: CatalogsState,
    onClickCatalog: (Catalog) -> Unit,
    onClickTogglePinned: (CatalogLocal) -> Unit,
) {
    val scrollState = rememberLazyListState()
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        state = scrollState,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        kotlin.runCatching {
            if (state.pinnedCatalogs.isNotEmpty()) {
                item {
                    TextSection(
                        text = "Pinned",
                    )
                }
                items(state.pinnedCatalogs.size) { index ->
                    val catalog = state.pinnedCatalogs[index]
                    CatalogItem(
                        catalog = catalog,
                        installStep = if (catalog is CatalogInstalled) state.installSteps[catalog.pkgName] else null,
                        onClick = { onClickCatalog(catalog) },
                        onPinToggle = { onClickTogglePinned(catalog) }
                    )
                }
            }

            if (state.unpinnedCatalogs.isNotEmpty()) {
                state.mappedCatalogs.value.forEach { (lang, catalogs) ->
                    item {
                        TextSection(
                            text = lang,
                        )
                    }
                    items(catalogs.size) { index ->
                        val catalog = catalogs[index]
                        kotlin.runCatching {
                            CatalogItem(
                                catalog = catalog,
                                installStep = if (catalog is CatalogInstalled) state.installSteps[catalog.pkgName] else null,
                                onClick = { onClickCatalog(catalog) },
                                onPinToggle = { onClickTogglePinned(catalog) }
                            )
                        }.getOrElse {
                            Log.error { catalog.name + "Throws an error" + it.message }
                        }
                    }
                }
            }
        }.getOrElse {
            Log.error { "Throws an error" + it.message }
        }
    }
}
