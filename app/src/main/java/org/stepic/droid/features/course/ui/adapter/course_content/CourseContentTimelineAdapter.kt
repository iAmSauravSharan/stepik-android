package org.stepic.droid.features.course.ui.adapter.course_content

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.view_course_content_section_date.view.*
import org.stepic.droid.R
import org.stepic.droid.features.course.ui.model.course_content.CourseContentSectionDate
import org.stepic.droid.ui.custom.adapter_delegates.DelegateViewHolder
import org.stepic.droid.ui.util.changeVisibility
import org.stepic.droid.util.DateTimeHelper
import java.util.*

class CourseContentTimelineAdapter : RecyclerView.Adapter<DelegateViewHolder<CourseContentSectionDate>>() {
    var dates: List<CourseContentSectionDate> = emptyList()
        set(value) {
            field = value.sortedBy { it.date }
            notifyDataSetChanged()
        }

    var now: Date = Date()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_course_content_section_date, parent, false))

    override fun getItemCount(): Int =
            dates.size

    override fun onBindViewHolder(holder: DelegateViewHolder<CourseContentSectionDate>, position: Int) =
            holder.bind(dates[position])

    inner class ViewHolder(root: View) : DelegateViewHolder<CourseContentSectionDate>(root) {
        private val dateDot = root.dateDot
        private val dateProgress = root.dateProgress
        private val dateTitle = root.dateTitle
        private val dateValue = root.dateValue

        override fun onBind(data: CourseContentSectionDate) {
            dateTitle.setText(data.titleRes)
            dateValue.text = DateTimeHelper.getPrintableDate(data.date, DateTimeHelper.DISPLAY_DATETIME_PATTERN, TimeZone.getDefault())

            val isNotLastItem = adapterPosition < itemCount - 1
            dateProgress.changeVisibility(isNotLastItem)
            if (isNotLastItem) {
                dateProgress.max = (dates[adapterPosition + 1].date.time - data.date.time).toInt()
                dateProgress.progress = (now.time - data.date.time).toInt()
            }

            dateDot.isEnabled = now >= data.date
        }
    }
}