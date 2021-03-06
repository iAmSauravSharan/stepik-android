package org.stepik.android.domain.comments.interactor

import io.reactivex.Completable
import io.reactivex.Single
import org.stepik.android.domain.comments.repository.CommentsBannerRepository
import javax.inject.Inject

class CommentsInteractor
@Inject
constructor(
    private val commentsBannerRepository: CommentsBannerRepository
) {
    fun shouldShowCommentsBannerForCourse(courseId: Long): Single<Boolean> =
        commentsBannerRepository.hasCourseId(courseId)

    fun onBannerShown(courseId: Long): Completable =
        commentsBannerRepository.addCourseId(courseId)
}