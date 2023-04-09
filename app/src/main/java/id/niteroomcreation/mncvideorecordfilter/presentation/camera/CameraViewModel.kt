package id.niteroomcreation.mncvideorecordfilter.presentation.camera

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.niteroomcreation.mncvideorecordfilter.presentation.custom.CameraFilter
import javax.inject.Inject

/**
 * Created by Septian Adi Wijaya on 09/04/2023.
 * please be sure to add credential if you use people's code
 */
@HiltViewModel
class CameraViewModel @Inject constructor() : ViewModel() {

    private val state_ = MutableLiveData<List<CameraFilter>>()
    val state = state_

    init {
        loadFilters()
    }

    private fun loadFilters() {
        state_.value = CameraFilter.createFilterList()
    }

}