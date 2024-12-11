import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.task.R
import com.example.task.application.AppDetails

class ApplicationViewModel : ViewModel() {

    private val originalAppList = listOf(
        AppDetails(icon = R.drawable.ic_amazon, name = "Amazon", isEnabled = true),
        AppDetails(icon = R.drawable.ic_google_assistant, name = "Assistant", isEnabled = true),
        AppDetails(icon = R.drawable.ic_calculator, name = "Calculator", isEnabled = false),
        AppDetails(icon = R.drawable.ic_calculator_2, name = "Calculator", isEnabled = false),
        AppDetails(icon = R.drawable.ic_caller, name = "Contacts", isEnabled = true),
        AppDetails(icon = R.drawable.ic_calender, name = "Calendar", isEnabled = true),
        AppDetails(icon = R.drawable.ic_chrome, name = "Chrome", isEnabled = true),
        AppDetails(icon = R.drawable.ic_drive, name = "Drive", isEnabled = false),
        AppDetails(icon = R.drawable.ic_facebook, name = "Facebook", isEnabled = false),
        AppDetails(icon = R.drawable.ic_google, name = "Google", isEnabled = true),
        AppDetails(icon = R.drawable.ic_instagram, name = "Instagram", isEnabled = true),
        AppDetails(icon = R.drawable.ic_messenger, name = "Messenger", isEnabled = true),
        AppDetails(icon = R.drawable.ic_playstore, name = "PlayStore", isEnabled = false),
        AppDetails(icon = R.drawable.ic_whatsapp, name = "Whatsapp", isEnabled = false),
    )

    private val _filteredAppList = MutableLiveData<List<AppDetails>>()
    val filteredAppList: LiveData<List<AppDetails>> get() = _filteredAppList

    init {
        _filteredAppList.value = originalAppList
    }

    fun filterAppList(appName: String) {
        _filteredAppList.value = if (appName.isEmpty()) {
            originalAppList
        } else {
            val lowercaseQuery = appName.lowercase()
            originalAppList.filter { it.name.lowercase().startsWith(lowercaseQuery) }
        }
    }
}