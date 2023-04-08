package com.pg.cloudcleaner.domain.interactors

import com.pg.cloudcleaner.data.model.LocalFile
import kotlinx.coroutines.flow.Flow

interface HomeInteractor {

   fun getAnyTwoDuplicates() : Flow<Pair<LocalFile, LocalFile>?>
   fun getVideoFile() : Flow<LocalFile?>
   fun getVideoFiles() : Flow<List<LocalFile>>
   fun getImageFiles() : Flow<List<LocalFile>>


}