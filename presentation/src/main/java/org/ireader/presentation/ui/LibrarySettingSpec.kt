package org.ireader.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.ireader.common_resources.R
import org.ireader.components.components.TitleToolbar


object LibrarySettingSpec : ScreenSpec {

    override val navHostRoute: String = "library_settings_screen_route"

    @Composable
    override fun TopBar(
        controller: ScreenSpec.Controller
    ) {
        TitleToolbar(
            title = stringResource(R.string.library),
            navController = controller.navController
        )
    }


    @Composable
    override fun Content(
        controller: ScreenSpec.Controller
    ) {

    }

}
