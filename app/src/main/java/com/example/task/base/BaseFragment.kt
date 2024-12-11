//package com.example.task.base
//
//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import android.os.SystemClock
//import android.provider.Settings
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.EditText
//import androidx.annotation.LayoutRes
//import androidx.collection.arrayMapOf
//import androidx.databinding.DataBindingUtil
//import androidx.databinding.ViewDataBinding
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.LifecycleOwner
//import com.google.firebase.analytics.FirebaseAnalytics
//import com.narayana.analytics.AnalyticsEventGroup
//import com.narayana.analytics.AppAnalyticsTracker
//import com.narayana.analytics.amplitude.*
//import com.narayana.analytics.mixpanel.MixPanelEventAttributes
//import com.narayana.analytics.mixpanel.MixPanelEventTypes
//import com.narayana.analytics.navigation.Navigation
//import com.narayana.analytics.utils.log
//import com.narayana.base.extension_functions.ifNullOrEmptyAddDefaultString
//import com.narayana.base.utils.ToastTypeV2
//import com.narayana.base.utils.isAutomaticDateTimeEnabled
//import com.narayana.base.utils.safeLaunchWhenResumed
//import com.narayana.base.views.StandardDialog
//import dagger.android.AndroidInjector
//import dagger.android.DispatchingAndroidInjector
//import dagger.android.HasAndroidInjector
//import dagger.android.support.AndroidSupportInjection
//import kotlinx.coroutines.flow.collectLatest
//import timber.log.Timber
//import javax.inject.Inject
//
//
///**
// * Base Fragment of all the fragments
// *
// * @param <VM>
// * @param <Binding>
//</Binding></VM> */
//abstract class BaseFragment<VM : BaseViewModel, Binding : ViewDataBinding> : Fragment(),
//    HasAndroidInjector {
//
//    var baseActivity: BaseActivity<*, *>? = null
//
//    @Suppress("PropertyName")
//    protected abstract val TAG: String
//
//
//    @Inject
//    lateinit var viewModel: VM
//
//    @Inject
//    lateinit var androidInjector: DispatchingAndroidInjector<Any>
//
//    @Inject
//    lateinit var analyticsTracker: AppAnalyticsTracker
//
//    private var screenStartTime: Long = -1
//
//    private val firebaseAnalytics by lazy {
//        FirebaseAnalytics.getInstance(requireContext())
//    }
//
//    fun isViewModelInitialized() = ::viewModel.isInitialized
//
//    protected val isTablet by lazy {
//        resources.getBoolean(R.bool.isTablet)
//    }
//
//    protected val isLandScape by lazy {
//        resources.getBoolean(R.bool.isLandscape)
//    }
//
//    protected val isSevenInchTablet by lazy {
//        resources.getInteger(R.integer.tablet_size) == 1
//    }
//
//    protected val isTenInchTablet by lazy {
//        resources.getInteger(R.integer.tablet_size) == 2
//    }
//
//    /**
//     * Analytics screen name
//     * override this variable for recording screen time of this screen
//     */
//    protected abstract val currentScreenName: Navigation
//
//    /**
//     * Analytics EventGroup
//     */
//    abstract val eventGroup: AnalyticsEventGroup
//
//    lateinit var dataBinding: Binding
//
//    @LayoutRes
//    protected abstract fun getLayoutResource(): Int
//
//    open val restrictCapturingScreenShot: Boolean? = null
//    open val lightStatusBar: Boolean? = null
//    open val recordTimeSpent = true
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        Timber.i("Base:onCreate - $TAG")
//        try {
//            /**
//             * Dependency Injection
//             */
//            performDependencyInjection()
//            super.onCreate(savedInstanceState)
//
//            //Progress dialog
//            setUpProgressDialog()
//
//            //finish current activity
//            finishCurrentActivity()
//
//            //Refreshes API Data
//            setupRefreshAPIData()
//
//            recordMixpanelClickEvent()
//            recordRevampMixpanelClickEvent()
//
//
//        } catch (e: Exception) {
//            throw Exception(TAG, e)
//        }
//
//    }
//
//    private fun setupRefreshAPIData() {
//        safeLaunchWhenResumed {
//            viewModel.dataManager.refreshAPIDataFlow.collectLatest {
//                if (it) viewModel.refreshAPIData()
//            }
//        }
//    }
//
//    private fun finishCurrentActivity() {
//        safeLaunchWhenResumed {
//            viewModel.finishCurrentActivityChannel.receive()
//            baseActivity?.finish()
//        }
//    }
//
//    private fun recordMixpanelClickEvent() {
//        safeLaunchWhenResumed {
//            viewModel.analyticsEventSharedFlow.collectLatest {
//                it[MixPanelEventAttributes.FEATURE_NAME] = eventGroup.value
//                it[MixPanelEventAttributes.PAGE_NAME] = currentScreenName.value
//
//                recordMixpanelEvent(MixPanelEventTypes.CLICK, it)
//            }
//        }
//    }
//
//    private fun recordRevampMixpanelClickEvent() {
//        safeLaunchWhenResumed {
//            viewModel.analyticsEventSharedFlow.collectLatest {
//            }
//        }
//    }
//
//    private fun performDependencyInjection() {
//        try {
//            AndroidSupportInjection.inject(this)
//        } catch (e: Exception) {
//            log(e)
//            showToast("DependencyInjection Failed")
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        try {
//            dataBinding = DataBindingUtil.inflate(inflater, getLayoutResource(), container, false)
//            dataBinding.lifecycleOwner = viewLifecycleOwner
//            initSharedElementTransition()
//        } catch (e: Exception) {
//            throw RuntimeException(TAG, e)
//        }
//        return dataBinding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        Timber.i("Base:onViewCreated - $TAG")
//        restrictCapturingScreenShot?.let {
//            baseActivity?.restrictCapturingScreenShot(it)
//        }
//        setUp()
//
//        initObservers(viewLifecycleOwner)
//        initPostPoneEnterSharedElementTransition()
//    }
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        AndroidSupportInjection.inject(this)
//        Timber.i("Base:onAttach - $TAG")
//        if (context is BaseActivity<*, *>) {
//            this.baseActivity = context
//            context.onFragmentAttached()
//        }
//    }
//
//    override fun onDetach() {
//        baseActivity = null
//        super.onDetach()
//        Timber.i("Base:onDetach - $TAG")
//    }
//
//    override fun onResume() {
//        super.onResume()
//        screenStartTime = SystemClock.elapsedRealtime()
//        firebaseAnalytics.setCurrentScreen(requireActivity(), TAG, null)
//        Timber.i("Base:onResume - $TAG")
//        lightStatusBar?.let {
//            baseActivity?.setStatusBarIconsColor(it)
//        }
//        checkIsAutomaticDateTimeEnabled()
//        recordPageVisitEvent()
//        recordRevampPageVisitEvent()
//
//
//    }
//
//    override fun onPause() {
//        super.onPause()
//        recordTimeSpent()
//        Timber.i("Base:onPause - $TAG")
//    }
//
//    protected open fun recordPageVisitEvent() {
//        val newFeatureName = eventGroup
//        val newScreenName = currentScreenName
//        if (BaseActivity.CURRENT_SCREEN_NAME != newScreenName) {
//            BaseActivity.CURRENT_FEATURE_NAME = newFeatureName
//            BaseActivity.CURRENT_SCREEN_NAME = newScreenName
//            recordAmplitudeEvent(
//                eventName = EventNames.PAGE_VISITED,
//                attributes = arrayMapOf(
//                    EventAttributeKeys.FEATURE_NAME to newFeatureName.value,
//                    EventAttributeKeys.SCREEN_NAME to newScreenName.value
//                )
//            )
//            recordMixpanelEvent(
//                eventName = MixPanelEventTypes.PAGE_VISIT,
//                attributes = arrayMapOf(
//                    MixPanelEventAttributes.FEATURE_NAME to newFeatureName.value,
//                    MixPanelEventAttributes.PAGE_NAME to newScreenName.value
//                )
//            )
//        }
//    }
//
//    protected open fun recordRevampPageVisitEvent() {
//        val newFeatureName = eventGroup
//        val newScreenName = currentScreenName
//        if (BaseActivity.CURRENT_SCREEN_NAME != newScreenName) {
//            BaseActivity.CURRENT_FEATURE_NAME = newFeatureName
//            BaseActivity.CURRENT_SCREEN_NAME = newScreenName
//            recordAmplitudeEvent(
//                eventName = EventNames.PAGE_VISITED,
//                attributes = arrayMapOf(
//                    EventAttributeKeys.FEATURE_NAME to newFeatureName.value,
//                    EventAttributeKeys.SCREEN_NAME to newScreenName.value
//                )
//            )
//        }
//    }
//
//    protected open fun recordTimeSpent() {
//        val newFeatureName = eventGroup
//        val newScreenName = currentScreenName
//        if (recordTimeSpent) {
//            val timeSpent = SystemClock.elapsedRealtime() - screenStartTime
//            screenStartTime = SystemClock.elapsedRealtime()
//
//            recordAmplitudeEvent(
//                eventName = EventNames.TIME_SPENT,
//                attributes = arrayMapOf(
//                    EventAttributeKeys.FEATURE_NAME to newFeatureName.value,
//                    EventAttributeKeys.SCREEN_NAME to newScreenName.value,
//                    EventAttributeKeys.TIME_SPENT to timeSpent
//                )
//            )
//
//            recordMixpanelEvent(
//                eventName = MixPanelEventTypes.TIME_SPENT,
//                attributes = arrayMapOf(
//                    MixPanelEventAttributes.FEATURE_NAME to newFeatureName.value,
//                    MixPanelEventAttributes.PAGE_NAME to newScreenName.value,
//                    MixPanelEventAttributes.SPENT_TIME to timeSpent
//                )
//            )
//        }
//    }
//
//    open fun recordAmplitudeEvent(
//        eventName: EventName,
//        attributes: Map<EventAttributeKey, Any?>
//    ) {
//        baseActivity?.recordAmplitudeEvent(eventName, attributes)
//    }
//
//    open fun recordMixpanelEvent(
//        eventName: Any,
//        attributes: Map<Any, Any?>
//    ) {
//        baseActivity?.recordMixpanelEvent(eventName, attributes)
//    }
//
//    private var automaticDateSettingsDialog: StandardDialog? = null
//
//    private fun checkIsAutomaticDateTimeEnabled() {
//        if (!isTablet && context?.isAutomaticDateTimeEnabled() == false) {
//            showAutomaticDateSettingsDialog()
//        } else {
//            automaticDateSettingsDialog?.dismiss()
//        }
//    }
//
//
//    private fun showAutomaticDateSettingsDialog() {
//        if (BuildConfig.DEBUG) return
//
//        if (automaticDateSettingsDialog?.isShowing == true) return
//        automaticDateSettingsDialog = StandardDialog.Builder(requireContext())
//            .setTitle("Automatic date is disabled")
//            .setMessage("need to enable automatic date in your device")
//            .setCancellable(false)
//            .setPositiveButton("Settings") {
//                val intent = Intent(Settings.ACTION_DATE_SETTINGS)
//                startActivity(intent)
//                it.dismiss()
//            }
//            .build().apply {
//                show()
//                baseActivity?.finishActivityWhenAutomaticTimeSettingsDisabled()
//            }
//    }
//
//    private fun setUpProgressDialog() {
//        safeLaunchWhenResumed {
//            viewModel.progressDialogStateFlow.collect {
//                if (it)
//                    showLoading()
//                else
//                    hideLoading()
//            }
//        }
//    }
//
//    fun hideKeyPadIfVisible() {
//        baseActivity?.hideKeyPadIfVisible()
//    }
//
//    fun showKeyPad(editText: EditText) {
//        baseActivity?.showKeyPad(editText)
//    }
//
//    fun showLoading() {
//        baseActivity?.showLoading()
//    }
//
//    fun hideLoading() {
//        baseActivity?.hideLoading()
//    }
//
//    abstract fun initObservers(viewLifecycleOwner: LifecycleOwner)
//
//    abstract fun setUp()
//
//    fun showToast(toastMessage: String, toastType: ToastTypeV2 = ToastTypeV2.DEFAULT) {
//        viewModel.showToastMessage(toastMessage, toastType)
//    }
//
//    fun isPackageInstalled(
//        packageName: String
//    ): Boolean {
//        return baseActivity!!.isPackageInstalled(packageName)
//    }
//
//    override fun androidInjector(): AndroidInjector<Any> {
//        return androidInjector
//    }
//
//    open fun initSharedElementTransition() {}
//    open fun initPostPoneEnterSharedElementTransition() {}
//
//
//    fun showVideoPolicyDialog(message: String, onClicked: () -> Unit) {
//        StandardDialog.Builder(requireContext())
//            .setTitle("")
//            .setMessage(message.ifNullOrEmptyAddDefaultString(resources.getString(R.string.video_policy_msg)))
//            .setCancellable(false)
//            .setPositiveButton(resources.getString(R.string.okay)) {
//                onClicked.invoke()
//                it.dismiss()
//            }
//            .setLifeCycle(lifecycle)
//            .build().show()
//    }
//
//    open fun onRecordTimeSpentComplete(duration: Long) = Unit
//
//
//}