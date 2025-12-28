package com.pg.cloudcleaner.domain.interactors

import coil.size.Size
import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.domain.repository.LocalFilesRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class HomeUseCases(private val repo: LocalFilesRepo) {

    fun getAnyTwoDuplicates(): Flow<Pair<LocalFile, LocalFile>?> {

        return repo.getFilesViaQuery(

            "SELECT * \n" + "FROM localfile \n" + "WHERE md5 IN \n" + "    (SELECT md5 \n" + "     FROM localfile \n" + "     GROUP BY md5 \n" + "     HAVING COUNT(*) >= 2) \n" + "AND mimeType Like '%image%'" + "LIMIT 2;"

        ).flowOn(Dispatchers.IO).map {
            if (it.size == 2) Pair(it.first(), it.last())
            else null
        }
    }

    fun getVideoFile(): Flow<LocalFile?> {
        return repo.getFilesViaQuery("SELECT * FROM localfile WHERE mimeType LIKE '%video%' limit 2")
            .flowOn(Dispatchers.IO).map {
                if (it.isEmpty()) null
                else it[0]
            }
    }

    fun getVideoFiles(): Flow<List<LocalFile>> {
        return repo.getFilesViaQuery("SELECT * FROM localfile WHERE mimeType LIKE '%video%'")
    }

    fun getImageFiles(): Flow<List<LocalFile>> {
        return repo.getFilesViaQuery("SELECT * FROM localfile WHERE mimeType LIKE '%image%' LIMIT 5")
    }

    fun getLargeFiles(): Flow<List<LocalFile>> {
        return repo.getFilesViaQuery("SELECT * FROM localfile WHERE size > 5000")
    }

    fun getTotalSizeOfMimeType(mimeTypePattern: String): Flow<Long> {
        val query = "SELECT COALESCE(SUM(size), 0) FROM localfile WHERE mimeType LIKE '$mimeTypePattern'"
        return repo.getFilesSizeSumViaQuery(query).flowOn(Dispatchers.IO)
    }

    // 2. Get sum for Large Files (Size > 5000MB)
    fun getTotalSizeOfLargeFiles(): Flow<Long> {
        val query = "SELECT COALESCE(SUM(size), 0) FROM localfile WHERE size > 5000"
        return repo.getFilesSizeSumViaQuery(query).flowOn(Dispatchers.IO)
    }

    // 3. Get sum for specific MimeType AND minimum size (Dynamic)
    fun getCustomTotalSize(mimeTypePattern: String, minSize: Long): Flow<Long> {
        val query = "SELECT COALESCE(SUM(size), 0) FROM localfile WHERE mimeType LIKE '$mimeTypePattern' AND size > $minSize"
        return repo.getFilesSizeSumViaQuery(query).flowOn(Dispatchers.IO)
    }


}