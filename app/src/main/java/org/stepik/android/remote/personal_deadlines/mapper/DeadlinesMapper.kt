package org.stepik.android.remote.personal_deadlines.mapper

import com.google.gson.GsonBuilder
import org.stepic.droid.jsonHelpers.adapters.UTCDateAdapter
import org.stepic.droid.web.storage.model.StorageRecord
import org.stepic.droid.web.storage.model.StorageRecordWrapped
import org.stepic.droid.web.storage.model.StorageRequest
import org.stepic.droid.web.storage.model.StorageResponse
import org.stepik.android.data.personal_deadlines.getKindOfRecord
import org.stepik.android.domain.personal_deadlines.model.DeadlinesWrapper
import java.util.Date
import javax.inject.Inject

class DeadlinesMapper
@Inject
constructor() {
    private val gson = GsonBuilder()
        .registerTypeAdapter(Date::class.java, UTCDateAdapter())
        .create()

    fun mapToStorageRequest(deadlines: DeadlinesWrapper, recordId: Long? = null): StorageRequest =
        StorageRequest(
            StorageRecordWrapped(
                id = recordId,
                kind = getKindOfRecord(deadlines.course),
                data = gson.toJsonTree(deadlines)
            )
        )

    fun mapToStorageRequest(record: StorageRecord<DeadlinesWrapper>): StorageRequest =
        StorageRequest(record.wrap(gson))

    fun mapToStorageRecord(response: StorageResponse): StorageRecord<DeadlinesWrapper>? =
        response
            .records
            .firstOrNull()
            ?.unwrap<DeadlinesWrapper>(gson)
            ?.takeIf { it.data.deadlines != null }
}