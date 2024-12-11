import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.task.R
import com.example.task.databinding.ItemApplicationBinding
import com.example.task.application.AppDetails

class ApplicationsAdapter(private var appList: List<AppDetails>) :
    RecyclerView.Adapter<ApplicationsAdapter.ApplicationViewHolder>() {

    class ApplicationViewHolder(val binding: ItemApplicationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicationViewHolder {
        val binding = ItemApplicationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ApplicationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ApplicationViewHolder, position: Int) {
        val app = appList[position]

        with(holder.binding) {
            icon.setImageResource(app.icon)
            appName.text = app.name
            appToggle.isChecked = app.isEnabled

            appToggle.setOnCheckedChangeListener { _, isChecked ->
                app.isEnabled = isChecked
            }
        }
        holder.binding.appToggle.thumbTintList =
            ContextCompat.getColorStateList(holder.binding.root.context, R.color.switch_thumb_color)
        holder.binding.appToggle.trackTintList =
            ContextCompat.getColorStateList(holder.binding.root.context, R.color.switch_track_color)

    }

    override fun getItemCount(): Int = appList.size

    fun updateList(newList: List<AppDetails>) {
        appList = newList
        notifyDataSetChanged()
    }

    fun appList(position: Int, updatedApp: AppDetails) {
        appList = appList.toMutableList().apply {
            this[position] = updatedApp
        }
        notifyItemChanged(position)
    }

}
