package id.niteroomcreation.mncvideorecordfilter.presentation.camera

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import id.niteroomcreation.mncvideorecordfilter.databinding.IFilterBinding
import id.niteroomcreation.mncvideorecordfilter.presentation.custom.CameraFilter

/**
 * Created by Septian Adi Wijaya on 09/04/2023.
 * please be sure to add credential if you use people's code
 */
class FilterAdapter(var filters: List<CameraFilter>, private val listener: FilterListener) :
    RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding = IFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, listener)
    }

    private fun getItem(pos: Int): CameraFilter {
        return filters[pos]
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binds(getItem(position))
    }

    override fun getItemCount(): Int {
        return filters.size
    }

}

class ViewHolder(
    private val binding: IFilterBinding,
    private val listener: FilterListener
) : RecyclerView.ViewHolder(binding.root) {

    fun binds(data: CameraFilter) {
//        binding.filterName.text = data.name

        when(data.name.lowercase()){
            "red"->{
                binding.filterName.backgroundTintList =ContextCompat.getColorStateList(binding.filterName.context, android.R.color.holo_red_dark)
            }
            "green"->{
                binding.filterName.backgroundTintList =ContextCompat.getColorStateList(binding.filterName.context, android.R.color.holo_green_dark)
            }
            "blue"->{
                binding.filterName.backgroundTintList =ContextCompat.getColorStateList(binding.filterName.context, android.R.color.holo_blue_dark)
            }
            "normal"->{
                binding.filterName.backgroundTintList =ContextCompat.getColorStateList(binding.filterName.context, android.R.color.darker_gray)
            }
        }

        binding.filterName.setOnClickListener { listener.onFilterClicked(data.name) }

    }
}

interface FilterListener {
    fun onFilterClicked(filter: String)
}