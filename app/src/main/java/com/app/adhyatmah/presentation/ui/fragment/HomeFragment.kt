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
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.viewpager2.widget.ViewPager2
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.data.preferences.PRODUCT_ID
import com.app.adhyatmah.data.preferences.PRODUCT_TITLE
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.data.preferences.CART_ID
import com.app.adhyatmah.data.preferences.CURRENT_PINCODE
import com.app.adhyatmah.data.preferences.IS_LOGIN
import com.app.adhyatmah.data.preferences.HANDLER
import com.app.adhyatmah.data.preferences.MENU_TITLE
import com.app.adhyatmah.data.preferences.REVIEW_PD_ID
import com.app.adhyatmah.data.preferences.TYPE
import com.app.adhyatmah.data.preferences.UserPreference
import com.app.adhyatmah.data.preferences.UserPreference.CART_COUNT
import com.app.adhyatmah.databinding.FragmentHomeBinding
import com.app.adhyatmah.domain.model.TrendingSection
import com.app.adhyatmah.domain.model.wish_list.wish_list_request.AddWishListRequest
import com.app.adhyatmah.domain.model.home_banner_response.HomeBanner
import com.app.adhyatmah.domain.model.home_collection_Response.Payload
import com.app.adhyatmah.domain.model.home_menu_response.Item
import com.app.adhyatmah.domain.model.pandit_list.get_pandit_list.Vendor
import com.app.adhyatmah.domain.model.profile.manage_address.Addresse
import com.app.adhyatmah.domain.model.profile.manage_address.ManageAddressRequest
import com.app.adhyatmah.presentation.ui.activity.LoginActivity
import com.app.adhyatmah.presentation.ui.activity.MainActivity
import com.app.adhyatmah.presentation.ui.adapter.AdapterBanner
import com.app.adhyatmah.presentation.ui.adapter.BlogAdapter
import com.app.adhyatmah.presentation.ui.adapter.DrawerMenuAdapter
import com.app.adhyatmah.presentation.ui.adapter.FeaturedProductsAdapter
import com.app.adhyatmah.presentation.ui.adapter.TrendingSectionAdapter
import com.app.adhyatmah.presentation.ui.adapter.ViewPagerAdapter
import com.app.adhyatmah.presentation.ui.pandit_ji.adapter.HomePanditJiAdapter
import com.app.adhyatmah.presentation.ui.viewmodel.HomeViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.CommonUtils
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import java.util.Locale

class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    var viewPagerlist: MutableList<HomeBanner> = mutableListOf()
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var drawerMenuAdapter: DrawerMenuAdapter
    private lateinit var productsAdapter: FeaturedProductsAdapter
    private lateinit var bannerAdapter: AdapterBanner
    private val homeViewModel by activityViewModels<HomeViewModel>()
    private var selectedType: String = "GHS" // Default

    private var isCollectionLoaded = false
    private var isBannerLoaded = false
    private var isBlogLoaded = false
    private var isMenuLoaded = false
    private var isYoutubeLoaded = false

    private val panditJiList: ArrayList<Vendor> = ArrayList()
    private val trendingSectionList: ArrayList<TrendingSection> = ArrayList()


    var videoUrl = ""

    var token = ""
    private var exoPlayer: ExoPlayer? = null

    private lateinit var youTubePlayerView: YouTubePlayerView
    private var youtubeVideoId = "" // Replace with actual video ID

    private lateinit var panditJiAdapter: HomePanditJiAdapter
    private lateinit var trendingSectionAdapter: TrendingSectionAdapter
    private val settingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            // Callback will trigger when user comes back from settings
            handlePermissionResult()
        }


    override fun setLayout(): Int {
        return R.layout.fragment_home
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResultListener("requestKey") { _, bundle ->
            Log.i("TAG", "onViewCreated: fragment result listener called")
            val user = bundle.getParcelable<Addresse>("resultKey")
            user?.let {
                binding.tvLocation.text =
                    it.address1 + ", " + it.city + ", " + it.province + ", " + it.country + " - " + it.zip
                Preferences.setStringPreference(requireContext(), CURRENT_PINCODE, it.zip)
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        token = Preferences.getStringPreference(requireContext(), ACCESS_TOKEN).toString()
        setObserver()
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        setupArrowClickListeners()
        //initializePlayer()
        setupYouTubePlayer()
        redirectOnYoutube()
        navigateFromDrawer()
        homeViewModel.getYouTubeUrlData()
        youTubePlayerView = binding.playerView
        Log.d("TAG", "inczfdsfitView: Token is $token")
        homeViewModel.homeCollectionApi(token)
        homeViewModel.trendingSectionApi()
        homeViewModel.hitPanditListApi()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fetchAddress()

        binding.searchIcon.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchListFragment)
        }
        binding.tvLocation.setOnClickListener {
            val bundle = Bundle().apply {
                putString("from", "home")
            }
            findNavController().navigate(R.id.mangeAddressFragment, bundle)
        }

        //TODO
        /*binding.header.setOnClickListener {
            ProcessDialog.showDialog(requireActivity(),true)
        }*/
        binding.tvViewAllPanditJi.setOnClickListener {
            (requireActivity() as? MainActivity)?.switchToPanditJiTab()
        }


    }

    fun fetchAddress() {
        val request = ManageAddressRequest()
        request.accessToken = token
        homeViewModel.getAddressData(request)
    }


    fun redirectOnYoutube() {

        binding.youTubeClick.setOnClickListener {
//            val videoUrl = "https://www.youtube.com/watch?v=fioP1oZDUwo"
            val intent = Intent(Intent.ACTION_VIEW, videoUrl.toUri())
            intent.setPackage("com.google.android.youtube") // Try to open in YouTube app

            // Fallback to browser if YouTube app isn't available
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            } else {
                val webIntent = Intent(Intent.ACTION_VIEW, videoUrl.toUri())
                startActivity(webIntent)

            }

        }
    }


    fun drawable(menuItem: List<Item>) {
        //TODO
        /*binding.threeDot.setOnClickListener {
                binding.drawerLayout.openDrawer(GravityCompat.START)
                drawerMenuAdapter = DrawerMenuAdapter(menuItem, {
                    it->
                    val handle = it.collectionHandle
                    val title = it.title
                    val bundle = Bundle()
                    bundle.putString(TYPE,"2")
                    bundle.putString(MENU_TITLE,handle)
                    bundle.putString("TITLE",title)
                    Log.d("TAG", "setAdapter:jj $handle")

                    if (handle.isNullOrEmpty()){

                        binding.drawerLayout.closeDrawer(GravityCompat.START)
                    }
                    else{
                        findNavController().navigate(R.id.action_homeFragment_to_productListFragment,bundle)
                    }

                } )
                binding.drawerId.recyclerDrawer.adapter = drawerMenuAdapter

        }*/
    }

    fun setAdapter(data: Payload) {

        productsAdapter = FeaturedProductsAdapter(
            requireContext(),
            data,
            false,
            onViewAllClick = { position ->
                val handle = data.collections[position].handle
                val title = data.collections[position].title
                Log.d("TAG", "setAdapter1: $handle  $title")
                (activity as MainActivity).switchToCategoryTab()
                val bundle = Bundle()
                bundle.putString("category_handle", handle)
                findNavController().navigate(R.id.categoryProductFragment, bundle)


            },
            onWishlistClick = { collectionIndex, productIndex, isLiked ->

                val product = data.collections[collectionIndex].products[productIndex]
                val productId = product.id
                val isLogin = Preferences.getStringPreference(requireContext(), IS_LOGIN)

                if (isLogin == "1") {
                    if (isLiked) {
                        Log.d("TAG", "setAxcvcxvcxdapter: $productId")
                        var req = AddWishListRequest(token, productId)
                        homeViewModel.addWishLisData(req)
                    } else {
                        Log.d("TAG", "setAxcvcxvcxdapter: $productId")
                        var requ = AddWishListRequest(token, productId)
                        homeViewModel.removeWishLisData(requ)
//                        Toast.makeText(requireActivity(), "Remove from wishlist", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    showLoginPrompt()
                }
                // Log.d("Fragment", "SubAdapter clicked position: $subPosition, liked: $isLiked")
            },
            onSubAdapterClick = { collectionIndex, productIndex, isLiked ->
                val product = data.collections[collectionIndex].products[productIndex]
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

        binding.featuredProducts.adapter = productsAdapter
        // setBlog adapter

    }


    private fun setViewPager(homeBanner: List<HomeBanner>) {
        viewPagerAdapter = ViewPagerAdapter(homeBanner)
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

    private fun setupArrowClickListeners() {
        //TODO
        /*binding.leftArrow.setOnClickListener {
            val currentItem = binding.viewpager.currentItem
            if (currentItem > 0) {
                binding.viewpager.currentItem = currentItem - 1
            }
        }

        binding.rightArrow.setOnClickListener {
            val currentItem = binding.viewpager.currentItem
            if (currentItem < viewPagerlist.size - 1) {
                binding.viewpager.currentItem = currentItem + 1
            }
        }*/
    }


    override fun onStop() {
        super.onStop()
        exoPlayer?.release()
        exoPlayer = null
    }

    override fun onPause() {
        super.onPause()
        exoPlayer?.pause()  // Pause the video when the fragment goes into the background
    }

    override fun onResume() {
        super.onResume()
        exoPlayer?.playWhenReady = true
        setObserver()
        homeViewModel.homeCollectionApi(token)
    }

    // Declare this at class level
    private fun setupYouTubePlayer() {
        youTubePlayerView = binding.playerView // If using ViewBinding

        // Add lifecycle observer for proper cleanup
        lifecycle.addObserver(youTubePlayerView)

        binding.progressBar.visibility = View.VISIBLE

        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                binding.progressBar.visibility = View.GONE
                youTubePlayer.cueVideo(youtubeVideoId, 0f)
            }
        })
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

    /* override fun onDestroyView() {
         if (this::youTubePlayerView.isInitialized) {
             youTubePlayerView.release()
         }
         super.onDestroyView()

     }
 */

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
                                binding.tvLocation.text =
                                    validAddresses?.get(0)?.city + ", " + validAddresses?.get(0)?.province + ", " + validAddresses?.get(
                                        0
                                    )?.country + " - " + validAddresses?.get(0)?.zip
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
                        trendingSectionAdapter = TrendingSectionAdapter(trendingSectionList) {

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
                        // Store latest full vendors list (useful for showing full list on blank search)
                        panditJiList.clear()
                        panditJiList.addAll(vendors)
                        panditJiAdapter = HomePanditJiAdapter(panditJiList) {pos->
                            val data = panditJiList[pos]
                            UserPreference.panditjiBookingRequest.apply {
                                address = data.address
                                vendorId = data.id
                                firstName = data.firstName
                                lastName = data.lastName
                                image = data.image?.url
                                bookingId = ""
                                about = data.about
                            }

                            if ((data.services?.size ?: 0) > 0) {
                                findNavController().navigate(R.id.bookingDetailsFragment)
                            } else {
                                Toast.makeText(
                                    requireActivity(),
                                    "No Service Available!",
                                    Toast.LENGTH_SHORT
                                ).show()
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

        homeViewModel.getCollectionLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    isCollectionLoaded = true

                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            val data = it.data.payload
                            setAdapter(data)
                            CART_COUNT = it.data.payload.cart ?: 0
                            (requireActivity() as? MainActivity)?.updateBagBadge(CART_COUNT)
                            // setup adapter here
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
                    //   ProcessDialog.showDialog(requireActivity(), true)
                }

                Status.ERROR -> {
                    stopShimmer()
                    //  ProcessDialog.dismissDialog(true)
                    Snackbar.make(requireView(), "${it.message}", Snackbar.LENGTH_SHORT).show()
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
                            viewPagerlist = homeBanner
                            setViewPager(homeBanner)
                            bannerAdapter = AdapterBanner(data)
                            binding.bannerRecycler.adapter = bannerAdapter
                            // setup adapter here
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

        homeViewModel.getBlogLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    isBlogLoaded = true
                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {

                            val blogs = it.data.payload.blogs ?: emptyList()

                            // Flatten all articles from all blogs
                            val articles = blogs.flatMap { blog ->
                                blog.articles.edges.map { edge -> edge.node }
                            }

                            val blogAdapter = BlogAdapter(
                                articles,
                                object : BlogAdapter.OnBlogReadMoreClickListener {
                                    override fun onReadMoreClicked(
                                        title: String,
                                        content: String,
                                        imageUrl: String,
                                    ) {
                                        val bundle = Bundle().apply {
                                            putString("title", title)
                                            putString("content", content)
                                            putString("imageUrl", imageUrl)
                                        }
                                        findNavController().navigate(
                                            R.id.action_homeFragment_to_blogDetailsFragment,
                                            bundle
                                        )
                                    }
                                })
                            binding.blogsRecyclerview.adapter = blogAdapter


                            /* val blogAdapter = BlogAdapter(articles)
                             binding.blogsRecyclerview.adapter = blogAdapter
                            */ // setup adapter here
                        }

                        401 -> {
                            Log.e("TAG", "Unauthorized access")
                        }
                    }
                    stopShimmer()
                    //  ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    startShimmerLayout()
                    // ProcessDialog.showDialog(requireActivity(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "Error: ${it.message}")
                    stopShimmer()
                    // ProcessDialog.dismissDialog(true)
                    Snackbar.make(requireView(), "${it.message}", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        homeViewModel.getHomeMenuLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    isMenuLoaded = true

                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            val menuItem = it.data.payload.menu.items
                            Log.d("TAG", "setObserver:  $menuItem")

                            // Flatten all articles from all blogs

                            drawable(menuItem)

                            // setup adapter here
                        }

                        401 -> {
                            Log.e("TAG", "Unauthorized access")
                        }
                    }
                    stopShimmer()
                    //  ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    startShimmerLayout()
                    //   ProcessDialog.showDialog(requireActivity(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "Error: ${it.message}")
                    stopShimmer()
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
                    // startShimmerLayout()
                    ProcessDialog.showDialog(requireActivity(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "Error: ${it.message}")
                    //stopShimmer()
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

//                    Toast.makeText(requireActivity(), "Remove from wishlist", Toast.LENGTH_SHORT).show()
                    //  stopShimmer()
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    startShimmerLayout()
                    //  ProcessDialog.showDialog(requireActivity(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "Error: ${it.message}")
                    stopShimmer()
                    // ProcessDialog.dismissDialog(true)
                    Snackbar.make(
                        requireView(),
                        it.message ?: "Unknown error",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }

        homeViewModel.getYouTubeUrlResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code
                    when (statusCode) {
                        200 -> {
                            isYoutubeLoaded = true
                            val videoUrl = it.data?.payload?.url
                            youtubeVideoId = videoUrl?.let { extractVideoIdFromUrl(it) }.toString()

                            it.data?.message ?: "Something went wrong"
//                           Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            if (!youtubeVideoId.isNullOrEmpty()) {
                                setupYouTubePlayer()
                            }
                            //  stopShimmer()
                            ProcessDialog.dismissDialog(true)
                        }

                        401 -> {
                            Log.e("TAG", "Unauthorized access")
                        }
                    }
                    stopShimmer()
                    //   ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    //startShimmerLayout()
                    ProcessDialog.showDialog(requireActivity(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "Error: ${it.message}")
                    //stopShimmer()
                    ProcessDialog.dismissDialog(true)
                    Snackbar.make(requireView(), "${it.message}", Snackbar.LENGTH_SHORT).show()
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
    }


    private fun extractVideoIdFromUrl(url: String): String? {
        val uri = url.toUri()
        return uri.getQueryParameter("v")
    }

    private fun navigateFromDrawer() {
        binding.drawerId.apply {

        }
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
//        if (isCollectionLoaded && isBannerLoaded && isBlogLoaded && isMenuLoaded && isYoutubeLoaded ) {
        binding.myPropertyShimmer.myPropertyMainShimmer.stopShimmer()
        binding.myPropertyShimmer.myPropertyMainShimmer.visibility = View.GONE
        binding.scrollViewId.visibility = View.VISIBLE
//        }
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

                // ✅ Convert to address and set on TextView
                getAddressFromLatLng(lat, lng)
                Log.i("TAG", "getCurrentLocation: $location")

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

                val address = addresses[0].getAddressLine(0) // Full Address

                // ✅ Set fetched address to TextView
                Log.i("TAG", "getAddressFromLatLng: " + address)
                Log.i("TAG", "getAddressFromLatLng: " + addresses)
                Log.i("TAG", "getAddressFromLatLng: " + addresses[0].postalCode)
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
            // ✅ Permission granted
            onLocationPermissionGranted()
        } else {
            // ❌ Still not granted — send back to settings again
            openAppSettings()
        }
    }

    private fun onLocationPermissionGranted() {
        getCurrentLocation()
    }


}