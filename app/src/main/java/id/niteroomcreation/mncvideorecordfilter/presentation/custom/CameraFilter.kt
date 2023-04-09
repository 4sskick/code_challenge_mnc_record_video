package id.niteroomcreation.mncvideorecordfilter.presentation.custom

/**
 * Created by Septian Adi Wijaya on 09/04/2023.
 * please be sure to add credential if you use people's code
 */
enum class CameraFilter {
    RED,
    GREEN,
    BLUE,
    NORMAL;

    companion object {
        fun createFilterList(): List<CameraFilter> {
            return values().toList()
        }
    }
}