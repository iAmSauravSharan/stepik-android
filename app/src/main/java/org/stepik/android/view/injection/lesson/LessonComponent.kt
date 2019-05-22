package org.stepik.android.view.injection.lesson

import dagger.Subcomponent
import org.stepik.android.view.injection.assignment.AssignmentDataModule
import org.stepik.android.view.injection.progress.ProgressDataModule
import org.stepik.android.view.injection.section.SectionDataModule
import org.stepik.android.view.injection.step.StepDataModule
import org.stepik.android.view.injection.unit.UnitDataModule
import org.stepik.android.view.injection.view_assignment.ViewAssignmentDataModule
import org.stepik.android.view.lesson.ui.activity.LessonActivity

@Subcomponent(modules = [
    LessonModule::class,
    LessonDataModule::class,

    UnitDataModule::class,
    SectionDataModule::class,

    AssignmentDataModule::class,
    StepDataModule::class,
    ProgressDataModule::class,
    ViewAssignmentDataModule::class
])
interface LessonComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): LessonComponent
    }

    fun inject(lessonActivity: LessonActivity)
}