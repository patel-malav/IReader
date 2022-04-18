package org.ireader.presentation.ui

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import org.ireader.domain.ui.NavigationArgs
import org.ireader.presentation.feature_detail.presentation.chapter_detail.ChapterDetailScreen
import org.ireader.presentation.feature_detail.presentation.chapter_detail.viewmodel.ChapterDetailViewModel

object ChapterScreenSpec : ScreenSpec {

    override val navHostRoute: String = "chapter_detail_route/{bookId}/{sourceId}"

    override val arguments: List<NamedNavArgument> = listOf(
        NavigationArgs.sourceId,
        NavigationArgs.bookId,
    )

    fun buildRoute(
        bookId: Long,
        sourceId: Long,
    ): String {
        return "chapter_detail_route/$bookId/$sourceId"
    }


    @OptIn(ExperimentalPagerApi::class, androidx.compose.animation.ExperimentalAnimationApi::class,
        androidx.compose.material.ExperimentalMaterialApi::class)
    @Composable
    override fun Content(
        navController: NavController,
        navBackStackEntry: NavBackStackEntry,
        scaffoldState: ScaffoldState,
    ) {
        val  vm: ChapterDetailViewModel = hiltViewModel()
        val book = vm.book
        val context = LocalContext.current
        val scrollState = rememberLazyListState()
        val focusManager = LocalFocusManager.current
        ChapterDetailScreen(
            navController = navController,
            onItemClick = { index ->
                if (vm.selection.isEmpty()) {
                    if (book != null) {
                        navController.navigate(ReaderScreenSpec.buildRoute(
                            bookId = book.id,
                            sourceId = book.sourceId,
                            chapterId = vm.chapters[index].id,
                        ))
                    }
                } else {
                    when (vm.chapters[index].id) {
                        in vm.selection -> {
                            vm.selection.remove(vm.chapters[index].id)
                        }
                        else -> {
                            vm.selection.add(vm.chapters[index].id)
                        }
                    }

                }
            },
            onLongItemClick = { index ->
                when (vm.chapters[index].id) {
                    in vm.selection -> {
                        vm.selection.remove(vm.chapters[index].id)
                    }
                    else -> {
                        vm.selection.add(vm.chapters[index].id)
                    }
                }
            }
        )
    }

}