package org.stepik.android.presentation.course_content

import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import org.stepic.droid.di.qualifiers.BackgroundScheduler
import org.stepic.droid.di.qualifiers.MainScheduler
import org.stepic.droid.persistence.downloads.interactor.DownloadInteractor
import org.stepic.droid.persistence.downloads.progress.DownloadProgressProvider
import org.stepic.droid.persistence.model.DownloadConfiguration
import org.stepic.droid.persistence.model.DownloadProgress
import org.stepik.android.domain.course_content.interactor.CourseContentInteractor
import org.stepik.android.model.Section
import org.stepik.android.model.Unit
import org.stepik.android.presentation.base.PresenterBase
import org.stepik.android.view.course_content.model.CourseContentItem
import java.util.*
import javax.inject.Inject

class CourseContentPresenter
@Inject
constructor(
    private val courseContentInteractor: CourseContentInteractor,

    private val sectionDownloadProgressProvider: DownloadProgressProvider<Section>,
    private val sectionDownloadInteractor: DownloadInteractor<Section>,

    private val unitDownloadProgressProvider: DownloadProgressProvider<Unit>,
    private val unitDownloadInteractor: DownloadInteractor<Unit>,

    @BackgroundScheduler
    private val backgroundScheduler: Scheduler,
    @MainScheduler
    private val mainScheduler: Scheduler
) : PresenterBase<CourseContentView>() {
    private var state: CourseContentView.State = CourseContentView.State.Idle
        set(value) {
            field = value
            view?.setState(value)
        }

    private val downloadsDisposable = CompositeDisposable()

    private val pendingUnits = mutableSetOf<Long>()
    private val pendingSections = mutableSetOf<Long>()

    init {
        compositeDisposable += downloadsDisposable
        fetchCourseContent()
    }

    override fun attachView(view: CourseContentView) {
        super.attachView(view)
        view.setState(state)
        resolveDownloadProgressSubscription()
    }

    /**
     * Content
     */
    private fun fetchCourseContent(forceUpdate: Boolean = false) {
        if (state != CourseContentView.State.Idle
            && !(state == CourseContentView.State.NetworkError && forceUpdate)) return

        state = CourseContentView.State.Loading
        compositeDisposable += courseContentInteractor
            .getCourseContent()
            .subscribeOn(backgroundScheduler)
            .observeOn(mainScheduler)
            .subscribeBy(
                onNext  = { state = CourseContentView.State.CourseContentLoaded(it); resolveDownloadProgressSubscription() },
                onError = { state = CourseContentView.State.NetworkError }
            )
    }

    /**
     * Download progresses
     */
    private fun resolveDownloadProgressSubscription() {
        val items =
            (state as? CourseContentView.State.CourseContentLoaded)
            ?.courseContent
            ?.takeIf { view != null }
            ?: return

        downloadsDisposable.clear()
        val sectionIds = items
            .mapNotNull { item ->
                (item as? CourseContentItem.SectionItem)
                    ?.takeIf(CourseContentItem.SectionItem::isEnabled)
                    ?.section
                    ?.id
            }
            .toLongArray()

        subscribeForSectionsProgress(sectionIds)

        val unitIds = items
            .mapNotNull {
                (it as? CourseContentItem.UnitItem)?.takeIf(CourseContentItem.UnitItem::isEnabled)?.unit?.id
                    ?: (it as? CourseContentItem.UnitItemPlaceholder)?.unitId
            }
            .toLongArray()

        subscribeForUnitsProgress(unitIds)
    }

    private fun subscribeForSectionsProgress(sectionIds: LongArray) {
        downloadsDisposable += sectionDownloadProgressProvider
            .getProgress(*sectionIds)
            .subscribeOn(backgroundScheduler)
            .observeOn(mainScheduler)
            .subscribeBy(
                onError = { it.printStackTrace() },
                onNext  = { view?.updateSectionDownloadProgress(it) }
            )
    }

    private fun subscribeForUnitsProgress(unitIds: LongArray) {
        downloadsDisposable += unitDownloadProgressProvider
            .getProgress(*unitIds)
            .subscribeOn(backgroundScheduler)
            .observeOn(mainScheduler)
            .subscribeBy(
                onError = { it.printStackTrace() },
                onNext  = { view?.updateUnitDownloadProgress(it) }
            )
    }

    /**
     * Download tasks
     */
    fun addUnitDownloadTask(unit: Unit) {
        if (unit.id in pendingUnits) return
        pendingUnits.add(unit.id)
        view?.updateUnitDownloadProgress(DownloadProgress(unit.id, DownloadProgress.Status.Pending))

        compositeDisposable += unitDownloadInteractor
            .addTask(unit, configuration = DownloadConfiguration(EnumSet.allOf(DownloadConfiguration.NetworkType::class.java), "720"))
            .subscribeOn(backgroundScheduler)
            .observeOn(mainScheduler)
            .subscribeBy(
                onComplete = { pendingUnits.remove(unit.id) },
                onError    = { pendingUnits.remove(unit.id) }
            )
    }

    fun removeUnitDownloadTask(unit: Unit) {
        if (unit.id in pendingUnits) return
        pendingUnits.add(unit.id)
        view?.updateUnitDownloadProgress(DownloadProgress(unit.id, DownloadProgress.Status.Pending))

        compositeDisposable += unitDownloadInteractor
            .removeTask(unit)
            .subscribeOn(backgroundScheduler)
            .observeOn(mainScheduler)
            .subscribeBy(
                onComplete = { pendingUnits.remove(unit.id) },
                onError    = { pendingUnits.remove(unit.id) }
            )
    }

    fun addSectionDownloadTask(section: Section) {
        if (section.id in pendingSections) return
        pendingSections.add(section.id)
        view?.updateSectionDownloadProgress(DownloadProgress(section.id, DownloadProgress.Status.Pending))

        compositeDisposable += sectionDownloadInteractor
            .addTask(section, configuration = DownloadConfiguration(EnumSet.allOf(DownloadConfiguration.NetworkType::class.java), "720"))
            .subscribeOn(backgroundScheduler)
            .observeOn(mainScheduler)
            .subscribeBy(
                onComplete = { pendingSections.remove(section.id) },
                onError    = { pendingSections.remove(section.id) }
            )
    }

    fun removeSectionDownloadTask(section: Section) {
        if (section.id in pendingSections) return
        pendingSections.add(section.id)
        view?.updateSectionDownloadProgress(DownloadProgress(section.id, DownloadProgress.Status.Pending))

        compositeDisposable += sectionDownloadInteractor
            .removeTask(section)
            .subscribeOn(backgroundScheduler)
            .observeOn(mainScheduler)
            .subscribeBy(
                onComplete = { pendingSections.remove(section.id) },
                onError    = { pendingSections.remove(section.id) }
            )
    }

    override fun detachView(view: CourseContentView) {
        downloadsDisposable.clear()
        super.detachView(view)
    }
}