package com.belltree.readtrack.domain.usecase

import com.belltree.readtrack.domain.model.ReadLog
import com.belltree.readtrack.domain.repository.ReadLogRepository
import javax.inject.Inject

class InsertReadLogUseCase @Inject constructor(
    private val readLogRepository: ReadLogRepository
) {
    suspend operator fun invoke(readLog: ReadLog) {
        readLogRepository.insertLog(readLog)
    }
}
