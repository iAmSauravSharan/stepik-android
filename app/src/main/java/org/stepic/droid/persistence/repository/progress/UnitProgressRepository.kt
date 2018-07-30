package org.stepic.droid.persistence.repository.progress

import io.reactivex.Observable
import org.stepic.droid.di.AppSingleton
import org.stepic.droid.persistence.model.PersistentItem
import org.stepic.droid.persistence.storage.dao.PersistentItemDao
import org.stepic.droid.persistence.storage.dao.SystemDownloadsDao
import org.stepic.droid.persistence.storage.structure.DBStructurePersistentItem
import javax.inject.Inject

@AppSingleton
class UnitProgressRepository
@Inject
constructor(
        updatesObservable: Observable<PersistentItem>,
        intervalUpdatesObservable: Observable<Unit>,

        systemDownloadsDao: SystemDownloadsDao,
        persistentItemDao: PersistentItemDao
): ProgressRepositoryBase(updatesObservable, intervalUpdatesObservable, systemDownloadsDao, persistentItemDao), ProgressRepository {
    override val PersistentItem.keyFieldValue: Long
        get() = unit

    override val persistentItemKeyFieldColumn =
            DBStructurePersistentItem.Columns.UNIT
}