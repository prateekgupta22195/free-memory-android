package com.pg.cloudcleaner.domain.interactors

import com.pg.cloudcleaner.data.model.LocalFile
import kotlinx.coroutines.flow.Flow

interface HomeInteractor {

   fun getAnyTwoDuplicateImages() : Flow<Pair<LocalFile, LocalFile>?>


}