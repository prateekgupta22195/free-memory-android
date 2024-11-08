package com.pg.cloudcleaner.domain.interactors

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


}