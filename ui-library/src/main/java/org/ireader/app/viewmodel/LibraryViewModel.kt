package org.ireader.app.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.ireader.common_models.DisplayMode
import org.ireader.common_models.FilterType
import org.ireader.common_models.SortType
import org.ireader.core_api.log.Log
import org.ireader.core_ui.viewmodel.BaseViewModel
import org.ireader.domain.use_cases.local.DeleteUseCase
import org.ireader.domain.use_cases.local.LocalGetBookUseCases
import org.ireader.domain.use_cases.local.LocalGetChapterUseCase
import org.ireader.domain.use_cases.local.LocalInsertUseCases
import org.ireader.domain.use_cases.preferences.reader_preferences.screens.LibraryScreenPrefUseCases
import org.ireader.domain.use_cases.services.ServiceUseCases
import javax.inject.Inject


@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val localGetBookUseCases: LocalGetBookUseCases,
    private val insertUseCases: LocalInsertUseCases,
    private val deleteUseCase: DeleteUseCase,
    private val localGetChapterUseCase: LocalGetChapterUseCase,
    private val libraryScreenPrefUseCases: LibraryScreenPrefUseCases,
    private val libraryState: LibraryStateImpl,
    private val serviceUseCases: ServiceUseCases,
) : BaseViewModel(), LibraryState by libraryState {

    init {
        readLayoutTypeAndFilterTypeAndSortType()
        getLibraryBooks()
    }

    private var getBooksJob: Job? = null
    fun getLibraryBooks() {
        getBooksJob?.cancel()
        getBooksJob = localGetBookUseCases.SubscribeInLibraryBooks(
                sortType,
                desc = desc,
                filters
            ).onEach { list ->
                books = list.filter { it.title.contains(searchQuery, true) }
            }.launchIn(scope)
    }

    fun onLayoutTypeChange(layoutType: DisplayMode) {
        libraryScreenPrefUseCases.libraryLayoutTypeUseCase.save(layoutType.layoutIndex)
        this.layout = layoutType.layout
    }
    fun downloadChapters() {
        serviceUseCases.startDownloadServicesUseCase(bookIds = selection.toLongArray())
        selection.clear()
    }

    fun markAsRead() {
        viewModelScope.launch(Dispatchers.IO) {
            selection.forEach { bookId ->
                val chapters = localGetChapterUseCase.findChaptersByBookId(bookId)
                insertUseCases.insertChapters(chapters.map { it.copy(read = true) })
            }
            selection.clear()
        }
    }

    fun markAsNotRead() {
        viewModelScope.launch(Dispatchers.IO) {
            selection.forEach { bookId ->
                val chapters = localGetChapterUseCase.findChaptersByBookId(bookId)
                insertUseCases.insertChapters(chapters.map { it.copy(read = false) })
            }
            selection.clear()
        }
    }

    fun deleteBooks() {
        viewModelScope.launch(Dispatchers.IO) {
            deleteUseCase.deleteBookAndChapterByBookIds(selection)
            selection.clear()
        }
    }

    fun readLayoutTypeAndFilterTypeAndSortType() {
        viewModelScope.launch {
            val sortType = libraryScreenPrefUseCases.sortersUseCase.read()
            val layoutType = libraryScreenPrefUseCases.libraryLayoutTypeUseCase.read().layout
            val sortBy = libraryScreenPrefUseCases.sortersDescUseCase.read()
            this@LibraryViewModel.layout = layoutType
            this@LibraryViewModel.sortType = sortType
            this@LibraryViewModel.desc = sortBy
        }
    }

    fun changeSortIndex(sortType: SortType) {
        this.sortType = sortType
        if (sortType == sortType) {
            this.desc = !desc
            libraryScreenPrefUseCases.sortersDescUseCase.save(this.desc)
        }
        saveSortType(sortType)
        getLibraryBooks()
    }

    private fun saveSortType(sortType: SortType) {
        libraryScreenPrefUseCases.sortersUseCase.save(sortType.index)
    }

    fun addFilters(filterType: FilterType) {
        this.filters.add(filterType)
        getLibraryBooks()
    }

    fun removeFilters(filterType: FilterType) {
        this.filters.remove(filterType)
        getLibraryBooks()
    }

    fun refreshUpdate() {
        serviceUseCases.startLibraryUpdateServicesUseCase()
    }

    override fun onDestroy() {
        Log.error { "Destroyed" }
        super.onDestroy()
    }
}
