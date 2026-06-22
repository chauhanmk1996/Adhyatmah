package com.app.adhyatmah.presentation.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.data.preferences.CURRENT_PINCODE
import com.app.adhyatmah.data.preferences.IS_LOGIN
import com.app.adhyatmah.data.preferences.MENU_TITLE
import com.app.adhyatmah.data.preferences.PRODUCT_ID
import com.app.adhyatmah.data.preferences.REVIEW_PD_ID
import com.app.adhyatmah.data.preferences.TYPE
import com.app.adhyatmah.data.preferences.UserPreference
import com.app.adhyatmah.data.preferences.UserPreference.CART_COUNT
import com.app.adhyatmah.databinding.FragmentHomeBinding
import com.app.adhyatmah.domain.model.PopularPooja
import com.app.adhyatmah.domain.model.Testimonials
import com.app.adhyatmah.domain.model.TrendingSection
import com.app.adhyatmah.domain.model.WhyChooseUs
import com.app.adhyatmah.domain.model.create_booking.PanditJiDetails
import com.app.adhyatmah.domain.model.home_banner_response.HomeBanner
import com.app.adhyatmah.domain.model.home_collection_Response.HomeCollection
import com.app.adhyatmah.domain.model.pandit_list.get_pandit_list.Vendor
import com.app.adhyatmah.domain.model.profile.manage_address.ManageAddressRequest
import com.app.adhyatmah.domain.model.wish_list.wish_list_request.AddWishListRequest
import com.app.adhyatmah.presentation.ui.activity.LoginActivity
import com.app.adhyatmah.presentation.ui.activity.MainActivity
import com.app.adhyatmah.presentation.ui.adapter.AdapterBanner
import com.app.adhyatmah.presentation.ui.adapter.HomeCollectionAdapter
import com.app.adhyatmah.presentation.ui.adapter.PopularPujasAdapter
import com.app.adhyatmah.presentation.ui.adapter.RatingReviewAdapter
import com.app.adhyatmah.presentation.ui.adapter.TrendingSectionAdapter
import com.app.adhyatmah.presentation.ui.adapter.ViewPagerAdapter
import com.app.adhyatmah.presentation.ui.adapter.WhyChooseUsAdapter
import com.app.adhyatmah.presentation.ui.pandit_ji.adapter.HomePanditJiAdapter
import com.app.adhyatmah.presentation.ui.viewmodel.HomeViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.CommonUtils
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.app.adhyatmah.utils.hide
import com.app.adhyatmah.utils.show
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    var viewPagerList: MutableList<HomeBanner> = mutableListOf()
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var bannerAdapter: AdapterBanner
    private val homeViewModel by activityViewModels<HomeViewModel>()
    private var isCollectionLoaded = false
    private var isBannerLoaded = false
    var token = ""

    private val panditJiList: ArrayList<Vendor> = ArrayList()
    private lateinit var panditJiAdapter: HomePanditJiAdapter
    private val trendingSectionList: ArrayList<TrendingSection> = ArrayList()
    private lateinit var trendingSectionAdapter: TrendingSectionAdapter

    private val popularPoojaList: ArrayList<PopularPooja> = ArrayList()
    private lateinit var popularPujasAdapter: PopularPujasAdapter

    private val whyChooseUsList: ArrayList<WhyChooseUs> = ArrayList()
    private lateinit var whyChooseUsAdapter: WhyChooseUsAdapter

    private val rateReviewList: ArrayList<Testimonials> = ArrayList()
    private lateinit var ratingReviewAdapter: RatingReviewAdapter

    private lateinit var productsAdapter: HomeCollectionAdapter

    private val settingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            handlePermissionResult()
        }

    override fun setLayout(): Int {
        return R.layout.fragment_home
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().supportFragmentManager.setFragmentResultListener(
            "selectedAddress",
            viewLifecycleOwner
        ) { _, bundle ->
            if (bundle.isEmpty) return@setFragmentResultListener
            val address = bundle.getString("address")
            val zip = bundle.getString("zip")
            binding.tvLocation.text = address
            Preferences.setStringPreference(requireContext(), CURRENT_PINCODE, zip)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        token = Preferences.getStringPreference(requireContext(), ACCESS_TOKEN).toString()
        setObserver()
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        homeViewModel.homeCollectionApi(token)
        homeViewModel.trendingSectionApi()
        homeViewModel.hitPanditListApi()
        homeViewModel.homeDataApi()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fetchAddress()

        binding.clSearch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchListFragment)
        }

        binding.clLocation.setOnClickListener {
            val bundle = Bundle().apply {
                putString("from", "home")
            }
            findNavController().navigate(R.id.mangeAddressFragment, bundle)
        }

        binding.tvViewAllPanditJi.setOnClickListener {
            (requireActivity() as? MainActivity)?.switchToPanditJiTab()
        }

        binding.tvViewAllPopularPujas.setOnClickListener {
            findNavController().navigate(R.id.popularPujaFragment)
        }

        binding.clLongBanner.setOnClickListener {
            (requireActivity() as? MainActivity)?.switchToPanditJiTab()
        }

        binding.ivLanguage.setOnClickListener {
            findNavController().navigate(R.id.chooseLanguageFragment)
        }

        binding.ivCart.setOnClickListener {
            (requireActivity() as? MainActivity)?.switchToCartTab()
        }
    }

    fun fetchAddress() {
        val request = ManageAddressRequest()
        request.accessToken = token
        homeViewModel.getAddressData(request)
    }

    private fun setViewPager(homeBanner: List<HomeBanner>) {
        viewPagerAdapter = ViewPagerAdapter(homeBanner) {
            (requireActivity() as? MainActivity)?.switchToPanditJiTab()
        }
        binding.viewPager.adapter = viewPagerAdapter
        binding.indicatorRecyclerview.setIndicators(homeBanner.size)
        binding.viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
                binding.indicatorRecyclerview.setCurrentPosition(position)
            }

            override fun onPageSelected(p0: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }

    override fun onResume() {
        super.onResume()
        setObserver()
        homeViewModel.homeCollectionApi(token)
    }

    private fun setObserver() {
        homeViewModel.getCustomerAddressRes().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            var data = it.data.payload.addresses

                            val allAddresses = it.data?.payload?.addresses

                            val validAddresses = allAddresses?.filter { address ->
                                !address.name.isNullOrBlank() && !address.address1.isNullOrBlank()
                            }


                            if (data.isEmpty()) {
                                checkLocationPermission()

                            } else {
                                val location =
                                    validAddresses?.get(0)?.city + ", " + validAddresses?.get(0)?.province + ", " + validAddresses?.get(
                                        0
                                    )?.country + " - " + validAddresses?.get(0)?.zip
                                binding.tvLocation.text = location
                                Preferences.setStringPreference(
                                    requireContext(),
                                    CURRENT_PINCODE,
                                    validAddresses?.get(0)?.zip
                                )

                            }
                        }

                        401 -> {
                            Log.e("TAG", "Unauthorized access")
                        }
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireActivity(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "Error: ${it.message}")
                    ProcessDialog.dismissDialog(true)
//                    Snackbar.make(requireView(), "${it.message}", Snackbar.LENGTH_SHORT).show()
                }
            }

        }

        homeViewModel.getTrendingSectionLiveData().observe(viewLifecycleOwner) { res ->
            when (res.status) {
                Status.LOADING -> ProcessDialog.showDialog(requireActivity(), true)

                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog(true)
                    val code = res.data?.code
                    if (code == 200) {
                        val list = res.data.payload?.collections ?: emptyList()
                        trendingSectionList.clear()
                        trendingSectionList.addAll(list)
                        trendingSectionAdapter =
                            TrendingSectionAdapter(trendingSectionList) { pos ->
                                val trendingSection = trendingSectionList[pos]
                                val handle = trendingSection.handle
                                val title = trendingSection.title
                                val bundle = Bundle()
                                bundle.putString(TYPE, "2")
                                bundle.putString(MENU_TITLE, handle)
                                bundle.putString("TITLE", title)

                                if (handle.isNullOrEmpty()) {
                                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                                } else {
                                    findNavController().navigate(
                                        R.id.action_homeFragment_to_productListFragment,
                                        bundle
                                    )
                                }
                            }
                        binding.rvTrendingSections.adapter = trendingSectionAdapter
                    } else if (code == 401) {
                        ProcessDialog.dismissDialog(true)
                        Toast.makeText(
                            requireActivity(),
                            res.data.message ?: "Unauthorized",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("TAG", "Unauthorized access $res")
                    } else {
                        // handle other codes gracefully
                        ProcessDialog.dismissDialog(true)
                    }
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog(true)
                    Snackbar.make(
                        requireView(),
                        res.message ?: "Something went wrong",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

        homeViewModel.getPanditListLiveData().observe(viewLifecycleOwner) { res ->
            when (res.status) {
                Status.LOADING -> ProcessDialog.showDialog(requireActivity(), true)

                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog(true)
                    val code = res.data?.code
                    if (code == 200) {
                        val vendors = res.data.payload.vendors
                        panditJiList.clear()
                        panditJiList.addAll(vendors)
                        panditJiAdapter = HomePanditJiAdapter(panditJiList) { pos ->
                            if (Preferences.getStringPreference(
                                    requireContext(),
                                    IS_LOGIN
                                ) == "1"
                            ) {
                                val panditJi = panditJiList[pos]
                                UserPreference.panditJiDetails = PanditJiDetails(
                                    id = panditJi.id,
                                    image = panditJi.image?.url ?: "",
                                    firstName = panditJi.firstName ?: "",
                                    lastName = panditJi.lastName ?: "",
                                    city = panditJi.city ?: "",
                                    experience = panditJi.experience ?: "",
                                    about = panditJi.about ?: "",
                                    seoContent = panditJi.seoContent,
                                    gotra = panditJi.gotra ?: "",
                                    verified = panditJi.verified ?: false,
                                    trusted = panditJi.trusted ?: false,
                                    address = panditJi.address ?: "",
                                    panditLanguage = panditJi.language
                                )

                                if ((panditJi.services?.size ?: 0) > 0) {
                                    findNavController().navigate(R.id.bookingDetailsFragment)
                                } else {
                                    Toast.makeText(
                                        requireActivity(),
                                        "No Service Available!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                showLoginPrompt()
                            }
                        }
                        binding.rvPanditJi.adapter = panditJiAdapter
                    } else if (code == 401) {
                        ProcessDialog.dismissDialog(true)
                        Toast.makeText(
                            requireActivity(),
                            res.data.message ?: "Unauthorized",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("TAG", "Unauthorized access $res")
                    } else {
                        // handle other codes gracefully
                        ProcessDialog.dismissDialog(true)
                    }
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog(true)
                    Log.e("PanditJiFragment", "Error: ${res.message}")
                    Snackbar.make(
                        requireView(),
                        res.message ?: "Something went wrong",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

        homeViewModel.getHomeDataApi().observe(viewLifecycleOwner) { res ->
            when (res.status) {
                Status.LOADING -> ProcessDialog.showDialog(requireActivity(), true)

                Status.SUCCESS -> {
                    ProcessDialog.dismissDialog(true)
                    val code = res.data?.code
                    if (code == 200) {
                        val payload = res.data.payload
                        payload?.services?.let { list ->
                            popularPoojaList.clear()
                            popularPoojaList.addAll(list)
                            popularPujasAdapter =
                                PopularPujasAdapter(popularPoojaList) { selectedPuja ->
                                    (requireActivity() as? MainActivity)?.panditJiFromPopularPuja = false
                                    (requireActivity() as? MainActivity)?.switchToPanditJiTab(
                                        "Service",
                                        selectedPuja.name ?: ""
                                    )
                                }
                            binding.rvPopularPujas.adapter = popularPujasAdapter
                        }

                        payload?.longBanner?.let { longBanner ->
                            longBanner.url?.let { url ->
                                Glide.with(requireContext())
                                    .load(url)
                                    .placeholder(R.drawable.pamdit_ji)
                                    .error(R.drawable.pamdit_ji)
                                    .into(binding.ivLongBanner)
                            }
                            binding.tvTitle.text = longBanner.title ?: ""
                            binding.tvSubTitle.text = longBanner.subtitle ?: ""
                        }

                        payload?.whyChooseUs?.let { list ->
                            whyChooseUsList.clear()
                            whyChooseUsList.addAll(list)
                            whyChooseUsAdapter = WhyChooseUsAdapter(whyChooseUsList) {
                            }
                            binding.rvWhyChooseUs.adapter = whyChooseUsAdapter
                        }

                        payload?.testimonialsData?.let { testimonialsData ->
                            val totalRateReview =
                                "${testimonialsData.rating} (${testimonialsData.totalReviews} reviews)"
                            binding.tvRatingReview.text = totalRateReview

                            testimonialsData.testimonials?.let { list ->
                                rateReviewList.clear()
                                rateReviewList.addAll(list)

                                ratingReviewAdapter = RatingReviewAdapter(rateReviewList)
                                binding.viewPagerRatingReview.adapter = ratingReviewAdapter
                                binding.indicatorRatingReview.setIndicators(rateReviewList.size)

                                binding.viewPagerRatingReview.addOnPageChangeListener(object :
                                    OnPageChangeListener {
                                    override fun onPageScrolled(
                                        position: Int,
                                        positionOffset: Float,
                                        positionOffsetPixels: Int,
                                    ) {
                                        binding.indicatorRatingReview.setCurrentPosition(position)
                                    }

                                    override fun onPageSelected(p0: Int) {
                                    }

                                    override fun onPageScrollStateChanged(state: Int) {
                                    }
                                })
                            }
                        }
                    } else if (code == 401) {
                        ProcessDialog.dismissDialog(true)
                        Toast.makeText(
                            requireActivity(),
                            res.data.message ?: "Unauthorized",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("TAG", "Unauthorized access $res")
                    } else {
                        // handle other codes gracefully
                        ProcessDialog.dismissDialog(true)
                    }
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog(true)
                    Snackbar.make(
                        requireView(),
                        res.message ?: "Something went wrong",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

        homeViewModel.getBannerLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    isBannerLoaded = true
                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            val data = it.data.payload.banners.subBanners
                            val homeBanner = it.data.payload.banners.homeBanners
                            viewPagerList = homeBanner
                            setViewPager(homeBanner)
                            bannerAdapter = AdapterBanner(data) { pos ->
                                when (pos) {
                                    0 -> {
                                        (requireActivity() as? MainActivity)?.switchToCategoryTab()
                                    }

                                    1 -> {
                                        (requireActivity() as? MainActivity)?.switchToPanditJiTab()
                                    }
                                }
                            }
                            binding.bannerRecycler.adapter = bannerAdapter
                        }

                        401 -> {
                            Log.e("TAG", "Unauthorized access")
                        }
                    }
                    stopShimmer()
                    //   ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    startShimmerLayout()
                    //    ProcessDialog.showDialog(requireActivity(), true)
                }

                Status.ERROR -> {
                    stopShimmer()
                    Log.e("TAG", "Error: ${it.message}")
                    //  ProcessDialog.dismissDialog(true)
                    Snackbar.make(requireView(), "${it.message}", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        homeViewModel.getAddWishList().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.payload
                    Toast.makeText(
                        requireActivity(),
                        "Successfully Added to Wishlist",
                        Toast.LENGTH_SHORT
                    ).show()
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireActivity(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "Error: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                    Snackbar.make(
                        requireView(),
                        it.message ?: "Unknown error",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

        homeViewModel.removeWishList().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.payload

                    val message = it.data?.message ?: "Something went wrong"
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    startShimmerLayout()
                }

                Status.ERROR -> {
                    Log.e("TAG", "Error: ${it.message}")
                    stopShimmer()
                    Snackbar.make(
                        requireView(),
                        it.message ?: "Unknown error",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

        homeViewModel.getPostCurrencyLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code
                    when (statusCode) {
                        200 -> {
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT)
                                .show()
                            homeViewModel.homeCollectionApi(token)
                            //stopShimmer()
                            //ProcessDialog.dismissDialog(true)
                        }

                        401 -> {
                            Log.e("TAG", "Unauthorized access")
                        }
                    }
                    stopShimmer()
                    //ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    startShimmerLayout()
                    //  ProcessDialog.showDialog(requireActivity(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "Error: ${it.message}")
                    stopShimmer()
                    //ProcessDialog.dismissDialog(true)
                    Snackbar.make(requireView(), "${it.message}", Snackbar.LENGTH_SHORT).show()
                }
            }

        }

        homeViewModel.getCurrencyLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code
                    when (statusCode) {
                        200 -> {
                            var data = it.data.payload.currency
                            Log.d("TAG", "setObserver: $data")
                            // setSpinnerOptions(data)
                            stopShimmer()
                            //   ProcessDialog.dismissDialog(true)
                        }

                        401 -> {
                            Log.e("TAG", "Unauthorized access")
                        }
                    }
                    stopShimmer()
                    // ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    startShimmerLayout()
                    //  ProcessDialog.showDialog(requireActivity(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "Error: ${it.message}")
                    stopShimmer()
                    // ProcessDialog.dismissDialog(true)
                    Snackbar.make(requireView(), "${it.message}", Snackbar.LENGTH_SHORT).show()
                }
            }

        }

        homeViewModel.getCollectionLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    isCollectionLoaded = true
                    val statusCode = it.data?.code
                    when (statusCode) {
                        200 -> {
                            it.data.payload?.collections?.let { list ->
                                setAdapter(list)
                                CART_COUNT = it.data.payload.cart ?: 0
                                (requireActivity() as? MainActivity)?.updateBagBadge(CART_COUNT)
                                if (CART_COUNT > 0) {
                                    binding.clCartCount.show()
                                    binding.tvCartCount.text = CART_COUNT.toString()
                                } else {
                                    binding.clCartCount.hide()
                                }
                            }
                        }

                        401 -> {
                            Log.e("TAG", "Unauthorized access")
                        }
                    }
                    stopShimmer()
                }

                Status.LOADING -> {
                    startShimmerLayout()
                }

                Status.ERROR -> {
                    stopShimmer()
                    Snackbar.make(requireView(), "${it.message}", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun setAdapter(list: ArrayList<HomeCollection>) {
        val filteredList = ArrayList(
            list.filter { !it.products.isNullOrEmpty() }
        )
        productsAdapter = HomeCollectionAdapter(
            filteredList,
            false,
            onViewAllClick = { position ->
                val handle = filteredList[position].handle ?: ""
                val title = filteredList[position].title ?: ""
                (activity as MainActivity).switchToCategoryTab()
                val bundle = Bundle()
                bundle.putString("category_handle", handle)
                findNavController().navigate(R.id.categoryProductFragment, bundle)
            },
            onWishlistClick = { collectionIndex, productIndex, isLiked ->
                val product = filteredList[collectionIndex].products!![productIndex]
                val productId = product.id
                val isLogin = Preferences.getStringPreference(requireContext(), IS_LOGIN)
                if (isLogin == "1") {
                    if (isLiked) {
                        val req = AddWishListRequest(token, productId)
                        homeViewModel.addWishLisData(req)
                    } else {
                        val requ = AddWishListRequest(token, productId)
                        homeViewModel.removeWishLisData(requ)
                    }
                } else {
                    showLoginPrompt()
                }
            },
            onSubAdapterClick = { collectionIndex, productIndex, isLiked ->
                val product = filteredList[collectionIndex].products!![productIndex]
                val productId = product.id
                Preferences.setStringPreference(requireContext(), REVIEW_PD_ID, productId)
                val bundle = Bundle()
                bundle.putString(PRODUCT_ID, productId)
                findNavController().navigate(
                    R.id.action_homeFragment_to_productDetailsFragment,
                    bundle
                )
            }
        )
        binding.rvFeaturedProducts.adapter = productsAdapter
    }

    private fun showLoginPrompt() {
        var dialog: AlertDialog? = null
        dialog = CommonUtils.showCustomAlertDialog(
            requireActivity(),
            "Sign Up Required",
            "Please sign up to add items to your wishlist.",
            positiveButtonText = "Sign up",
            negativeButtonText = "Cancel",
            positiveButtonAction = {
                dialog?.dismiss()
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                val bundle = Bundle()
                bundle.putString("previousScreen", "logout")
                bundle.putString("selectedImage", "0")
                intent.putExtras(bundle)
                requireActivity().startActivity(intent)
            },
            negativeButtonAction = {
                dialog?.dismiss()
            }
        )
    }

    private var shimmerLoadingCount = 0

    private fun startShimmerLayout() {
        if (shimmerLoadingCount == 0) {
            binding.myPropertyShimmer.myPropertyMainShimmer.startShimmer()
            binding.myPropertyShimmer.myPropertyMainShimmer.visibility = View.VISIBLE
            binding.scrollViewId.visibility = View.GONE
        }
        shimmerLoadingCount++

    }

    private fun stopShimmer() {
        binding.myPropertyShimmer.myPropertyMainShimmer.stopShimmer()
        binding.myPropertyShimmer.myPropertyMainShimmer.visibility = View.GONE
        binding.scrollViewId.visibility = View.VISIBLE
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getCurrentLocation()
        } else {
            Toast.makeText(requireContext(), "Location permission denied!", Toast.LENGTH_SHORT)
                .show()
            if (!isLocationPermissionGranted()) {
                openAppSettings()
            }
        }
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        } else {
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val lat = location.latitude
                val lng = location.longitude
                getAddressFromLatLng(lat, lng)
            } else {
                requestNewLocationData()
            }
        }
    }

    private fun getAddressFromLatLng(latitude: Double, longitude: Double) {
        try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0].getAddressLine(0)
                binding.tvLocation.text =
                    addresses[0].subAdminArea + ", " + addresses[0].adminArea + ", " + addresses[0].countryName + " - " + addresses[0].postalCode
                Preferences.setStringPreference(
                    requireContext(),
                    CURRENT_PINCODE,
                    addresses[0].postalCode
                )

            } else {
                binding.tvLocation.text = "Unable to fetch address"
            }

        } catch (e: Exception) {
            e.printStackTrace()
            binding.tvLocation.text = "Address fetch failed!"
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
            interval = 2000
            fastestInterval = 1000
            numUpdates = 1
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val location = result.lastLocation
                    val lat = location?.latitude
                    val lng = location?.longitude

                    Log.d("Location", "Lat: $lat , Lng: $lng")
                    Toast.makeText(requireContext(), "Lat: $lat , Lng: $lng", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            Looper.getMainLooper()
        )
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun openAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", requireActivity().packageName, null)
        )
        settingsLauncher.launch(intent)
    }

    private fun handlePermissionResult() {
        if (isLocationPermissionGranted()) {
            onLocationPermissionGranted()
        } else {
            openAppSettings()
        }
    }

    private fun onLocationPermissionGranted() {
        getCurrentLocation()
    }
}