package org.stepic.droid.di.network

import dagger.Binds
import dagger.Module
import dagger.Provides
import org.stepic.droid.di.AppSingleton
import org.stepic.droid.features.achievements.repository.AchievementsRepository
import org.stepic.droid.features.achievements.repository.AchievementsRepositoryImpl
import org.stepic.droid.features.stories.repository.StoryTemplatesRepository
import org.stepic.droid.features.stories.repository.StoryTemplatesRepositoryImpl
import org.stepic.droid.web.ApiImpl
import org.stepic.droid.web.StepicRestLoggedService
import org.stepic.droid.web.achievements.AchievementsService
import org.stepic.droid.web.storage.RemoteStorageService
import org.stepik.android.view.injection.base.Authorized
import retrofit2.Retrofit

@Module(includes = [NetworkUtilModule::class])
abstract class NetworkModule {
    @Binds
    @AppSingleton
    abstract fun bindAchievementsRepository(achievementsRepositoryImpl: AchievementsRepositoryImpl): AchievementsRepository

    @Binds
    abstract fun bindStoryTemplatesRepository(storyTemplatesRepositoryImpl: StoryTemplatesRepositoryImpl): StoryTemplatesRepository

    @Module
    companion object {
        @Provides
        @AppSingleton
        @JvmStatic
        fun provideLoggedService(apiImpl: ApiImpl): StepicRestLoggedService =
            apiImpl.loggedService

        @Provides
        @AppSingleton
        @JvmStatic
        fun provideRemoteStorageService(apiImpl: ApiImpl): RemoteStorageService =
            apiImpl.remoteStorageService

        @Provides
        @AppSingleton
        @JvmStatic
        fun provideAchievementsService(apiImpl: ApiImpl): AchievementsService =
            apiImpl.achievementsService

        @Provides
        @AppSingleton
        @JvmStatic
        @Authorized
        fun provideAuhtorizedRetrofit(apiImpl: ApiImpl): Retrofit =
            apiImpl.authorizedRetrofit
    }
}