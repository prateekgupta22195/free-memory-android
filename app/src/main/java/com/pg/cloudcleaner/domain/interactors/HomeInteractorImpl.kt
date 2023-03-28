package com.pg.cloudcleaner.domain.interactors

import com.pg.cloudcleaner.data.model.LocalFile
import com.pg.cloudcleaner.domain.repository.LocalFilesRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class HomeInteractorImpl(private val repo: LocalFilesRepo) : HomeInteractor {

    override fun getAnyTwoDuplicateImages(): Flow<Pair<LocalFile, LocalFile>?> {

        return repo.getFilesViaQuery(
            "SELECT t1.*\n" +
                    "FROM localfile t1\n" +
                    "JOIN (\n" +
                    "  SELECT md5\n" +
                    "  FROM localfile\n" +
                    "  GROUP BY md5\n" +
                    "  HAVING COUNT(*) >= 2\n" +
                    "  ORDER BY id\n" +
                    "  LIMIT 1\n" +
                    ") t2 ON t1.md5 = t2.md5\n" +
                    "WHERE t1.mimeType LIKE '%image%'\n" +
                    "ORDER BY t1.id\n" +
                    "LIMIT 2"
        )
            .flowOn(Dispatchers.IO).map {
                if (it.size == 2)
                    Pair(it.first(), it.last())
                else
                    null
            }
    }


}