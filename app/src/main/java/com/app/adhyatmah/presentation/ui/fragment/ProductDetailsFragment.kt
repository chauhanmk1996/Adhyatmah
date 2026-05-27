package com.app.adhyatmah.presentation.ui.fragment

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.data.preferences.PRODUCT_ID
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.data.preferences.CART_ID
import com.app.adhyatmah.data.preferences.CURRENT_PINCODE
import com.app.adhyatmah.data.preferences.SEARCH_PRODUCT_ID
import com.app.adhyatmah.data.preferences.TYPE
import com.app.adhyatmah.data.preferences.UserPreference.CART_COUNT
import com.app.adhyatmah.databinding.FragmentProductDetailsBinding
import com.app.adhyatmah.domain.model.ProductReviewListResponse
import com.app.adhyatmah.domain.model.add_to_bag.add_to_bag_request.AddToBagRequest
import com.app.adhyatmah.domain.model.product_detail_response.Image
import com.app.adhyatmah.domain.model.product_detail_response.Product
import com.app.adhyatmah.domain.model.product_detail_response.Variant
import com.app.adhyatmah.domain.model.wish_list.wish_list_request.AddWishListRequest
import com.app.adhyatmah.presentation.ui.activity.LoginActivity
import com.app.adhyatmah.presentation.ui.activity.MainActivity
import com.app.adhyatmah.presentation.ui.adapter.ProductDetailColorAdapter
import com.app.adhyatmah.presentation.ui.adapter.ProductDetailReviewAdapter
import com.app.adhyatmah.presentation.ui.adapter.ProductDetailSizeAdapter
import com.app.adhyatmah.presentation.ui.adapter.ProductDetailSleeveAdapter
import com.app.adhyatmah.presentation.ui.adapter.ViewPagerProductDetailAdapter
import com.app.adhyatmah.presentation.ui.viewmodel.HomeViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.CommonUtils
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.app.adhyatmah.utils.getString
import com.google.android.material.snackbar.Snackbar

class ProductDetailsFragment : BaseFragment<FragmentProductDetailsBinding>() {
    private var availablePincode: List<String> = listOf()
    private var stockQuantity = 0
    private lateinit var productDetailColorAdapter: ProductDetailColorAdapter
    private lateinit var productDetailSizeAdapter: ProductDetailSizeAdapter
    private lateinit var productDetailSleeveAdapter: ProductDetailSleeveAdapter

    private lateinit var productDetailReviewAdapter: ProductDetailReviewAdapter
    private var viewPagerlist: MutableList<Image> = mutableListOf()
    private var productReviewList: MutableList<ProductReviewListResponse.Review> = mutableListOf()
    private lateinit var viewPagerAdapter: ViewPagerProductDetailAdapter

    private var productVariants = listOf<Variant>()
    private val selectedOptions = mutableMapOf<String, String>()

    private val validCombinations = mutableSetOf<Map<String, String>>()


    private val homeViewModel by activityViewModels<HomeViewModel>()

    private var isLoading = false
    private var currentPage = 1
    private val limitPerPage = 10
    private var productId = ""
    private var variantId = ""

    var token = ""
    var type = ""
    var categoryId = ""
    var searchProductId = ""
    var wishList = false

    override fun setLayout(): Int {
        return R.layout.fragment_product_details
    }


    override fun initView(savedInstanceState: Bundle?) {
        setupArrowClickListeners()
        productDetailReviewAdapter = ProductDetailReviewAdapter(
            mutableListOf(),
            object : ProductDetailReviewAdapter.OnViewMoreClickListener {
                override fun onViewMoreClicked() {
                    findNavController().navigate(R.id.action_productDetailsFragment_to_reviewsFragment)
                }

        })
        binding.reviewRecyclerId.adapter = productDetailReviewAdapter
        updateCartBadge(CART_COUNT)
        setObserve()
        type = arguments?.getString(TYPE).toString()
        categoryId = arguments?.getString(PRODUCT_ID).toString()
        productId = arguments?.getString(PRODUCT_ID).toString()
        searchProductId = arguments?.getString(SEARCH_PRODUCT_ID).toString()
        token = Preferences.getStringPreference(requireContext(), ACCESS_TOKEN).toString()

        Log.d("TAG", "initViewf: $categoryId $productId")


        if (type.equals("3")) {
            homeViewModel.getProductDtData(categoryId, token)
            //homeViewModel.getAllProductReview(productId, currentPage, limitPerPage)
        }
        if (type.equals("4")) {
            homeViewModel.getProductDtData(searchProductId, token)
            //homeViewModel.getAllProductReview(searchProductId, currentPage, limitPerPage)
        } else {
            homeViewModel.getProductDtData(productId, token)
          //  homeViewModel.getAllProductReview(productId, currentPage, limitPerPage)
        }

        binding.backBtn.setOnClickListener {
           findNavController().popBackStack()

        }
        binding.cartButton.setOnClickListener {
            (requireActivity() as? MainActivity)?.switchToCartTab()
        }
        binding.sizeGuideText.setOnClickListener {
            findNavController().navigate(R.id.action_productDetailsFragment_to_sizeImageFragment)
        }

        binding.reviewRecyclerId.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                if (!isLoading && lastVisibleItemPosition >= totalItemCount - 1) {
                    // ✅ Load next page
                    loadNextPage()
                }
            }
        })

        binding.shareBtn.setOnClickListener {
         shareAppProduct()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 👉 This will be called when system back is pressed in this fragment
                // ✅ You can put any logic here before going back

                findNavController().popBackStack() // same as clicking backBtn

                 // Toast.makeText(requireContext(), "Back pressed", Toast.LENGTH_SHORT).show()
            }
        })

    }
    fun updateCartBadge(count: Int) {
        val badgeTextView = binding.cartBadge as TextView

        if (count > 0) {
            badgeTextView.visibility = View.VISIBLE
            badgeTextView.text = count.toString()
        } else {
            badgeTextView.visibility = View.GONE
        }
    }


    private fun shareAppProduct() {
        val title = binding.greyPullover.getString()
        val description = binding.productDetailsDis.getString()
        val price = binding.price.getString()

        val shareText = """
        Check out this product!
        
         $title
         $description
        💵 Price: $price
        
        Download the app: https://play.google.com/store/apps/details?id=${requireContext().packageName}
    """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Awesome Product!")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        startActivity(Intent.createChooser(intent, "Share product via"))
    }

    private fun loadNextPage() {
        isLoading = true
        currentPage++
      //  homeViewModel.getAllProductReview(productId, currentPage, limitPerPage)
    }


    fun setAdapters(product: Product) {
        val colorOption = product.options.find { it.name.equals("Color", ignoreCase = true) }
        val sizeOption = product.options.find { it.name.equals("Size", ignoreCase = true) }
        val sleeveOption = product.options.find { it.name.equals("Sleeve length type", ignoreCase = true) }
      /*  binding.sizeText.text = sizeOption?.name ?: ""
        binding.colorText.text = colorOption?.name ?: ""
      */
        // For size
        if (!sizeOption?.name.isNullOrEmpty()) {
            binding.sizeText.text = sizeOption?.name
            binding.sizeText.visibility = View.VISIBLE
            binding.sizeRecyclerview.visibility = View.VISIBLE
        } else {
            binding.sizeRecyclerview.visibility = View.GONE
            binding.sizeText.visibility = View.GONE
        }

// For color
        if (!colorOption?.name.isNullOrEmpty()) {
            binding.colorText.text = colorOption?.name
            binding.colorText.visibility = View.VISIBLE
            binding.colorRecyclerview.visibility= View.VISIBLE

        } else {
            binding.colorRecyclerview.visibility= View.GONE
            binding.colorText.visibility = View.GONE
        }



        // binding.sleeve.text = sleeveOption?.name ?: ""
        if (!::productDetailColorAdapter.isInitialized) {
            productDetailColorAdapter = ProductDetailColorAdapter(mutableListOf())
            binding.colorRecyclerview.adapter = productDetailColorAdapter
        }

        if (!::productDetailSizeAdapter.isInitialized) {
            productDetailSizeAdapter = ProductDetailSizeAdapter(mutableListOf())
            binding.sizeRecyclerview.adapter = productDetailSizeAdapter
        }

        if (!::productDetailSleeveAdapter.isInitialized) {
            productDetailSleeveAdapter = ProductDetailSleeveAdapter(mutableListOf())
           // binding.sleeveRecyclerview.adapter = productDetailSleeveAdapter
        }

        val allSizes = sizeOption?.values ?: emptyList()     // all available sizes

        var enabledSize = productVariants.mapNotNull { variant ->
            variant.selectedOptions.find { it.name == "Size" }?.value
        }.distinct()

        val availableVariants = productVariants.filter { it.availableForSale }

        val availableSizes = availableVariants
            .filter { it.partialMatch(selectedOptions, exclude = "Size") }
            .mapNotNull { it.selectedOptions.find { it.name == "Size" }?.value }

        val availableColors = availableVariants
            .filter { it.partialMatch(selectedOptions, exclude = "Color") }
            .mapNotNull { it.selectedOptions.find { it.name == "Color" }?.value }

        val availableSleeves = availableVariants
            .filter { it.partialMatch(selectedOptions, exclude = "Sleeve length type") }
            .mapNotNull { it.selectedOptions.find { it.name == "Sleeve length type" }?.value }

        productDetailSizeAdapter.updateSizeList(getAllSizes(), availableSizes.distinct())
        productDetailColorAdapter.updateColorList(getAllColors(), availableColors.distinct())
        productDetailSleeveAdapter.updateList(getAllSleeves(), availableSleeves.distinct())





        productDetailSleeveAdapter.updateList(
            sleeveOption?.values ?: emptyList(),
            availableSleeves.distinct()
        )
        productDetailColorAdapter.updateColorList(
            colorOption?.values ?: emptyList(),
            availableColors.distinct()
        )
        productDetailSizeAdapter.updateSizeList(
            sizeOption?.values ?: emptyList(),
            availableSizes.distinct()
        )


        productVariants = product.variants?: emptyList()

        productDetailColorAdapter.onItemClick = { selectedColor ->
            selectedOptions["Color"] = selectedColor
            Log.d("ClickDebug", "Color selected: $selectedColor")

            updateSelectedVariant()
        }

        productDetailSizeAdapter.onItemClick = { selectedSize ->
            selectedOptions["Size"] = selectedSize
            Log.d("ClickDebug", "Size selected: $selectedSize")

            updateSelectedVariant()
        }

        productDetailSleeveAdapter.onItemClick = { selectedSleeve ->
            selectedOptions["Sleeve length type"] = selectedSleeve
            updateSelectedVariant()
        }


        productVariants.forEach { variant ->
            val combo = variant.selectedOptions.associate { it.name to it.value }
            validCombinations.add(combo)
        }


        val sizeOptions = sizeOption?.values ?: emptyList()
        val enabledSizes = sizeOptions.filter { isOptionValid("Size", it) }
        productDetailSizeAdapter.updateSizeList(sizeOptions, enabledSizes)




        Log.d("dsd","Size :$sizeOption")
        // Reviews (no dynamic list, so just set)
//        productDetailReviewAdapter = ProductDetailReviewAdapter(productReviewList)
//        binding.reviewRecyclerId.adapter = productDetailReviewAdapter





    }


    private fun setViewPager(img: List<Image>) {
        viewPagerlist.clear()
        viewPagerAdapter= ViewPagerProductDetailAdapter(img)
        binding.viewpager.adapter = viewPagerAdapter
        binding.viewpager.offscreenPageLimit = 1
        binding.viewpager.clipToPadding = false
        binding.viewpager.clipChildren = false

        binding.viewpager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        binding.indicatorRecyclerview.setIndicators(img.size)



        // Connect ViewPager to indicators
        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.indicatorRecyclerview.setCurrentPosition(position)
            }
        })
    }

    private fun setupArrowClickListeners() {
        var token = Preferences.getStringPreference(requireContext(), ACCESS_TOKEN)
        binding.arrowLeft.setOnClickListener {
            val currentItem = binding.viewpager.currentItem
            if (currentItem > 0) {
                binding.viewpager.currentItem = currentItem - 1
            }
        }

        binding.arrowRight.setOnClickListener {
            val currentItem = binding.viewpager.currentItem
            val adapter = binding.viewpager.adapter
            if (adapter != null && currentItem < adapter.itemCount - 1) {
                binding.viewpager.currentItem = currentItem + 1
            }
        }
        binding.BagBtn.setOnClickListener {
            Log.i("TAG", "setupArrowClickListeners: "+availablePincode)
            Log.i("TAG", "setupArrowClickListeners: "+Preferences.getStringPreference(requireContext(), CURRENT_PINCODE))
                if(token.isNullOrEmpty()){
                    showLoginPrompt()
                }else if(stockQuantity==0){
                    Toast.makeText(requireContext(), "Product not available!", Toast.LENGTH_SHORT).show()
                }
                else if ( (availablePincode.isNullOrEmpty()) || (availablePincode.isNotEmpty() && availablePincode.any { it.equals(Preferences.getStringPreference(requireContext(), CURRENT_PINCODE), ignoreCase = true) } )) {
                    var request = AddToBagRequest()
                    request.accessToken= token
                    request.quantity = 1
                    request.variantId = variantId
                    homeViewModel.addToBagApi(request)
                }
                else{
                    showPincodePrompt()
                }

        }


    }
    private fun showPincodePrompt() {
        var dialog: AlertDialog? = null
        dialog=  CommonUtils.showPincodeDialog(
            requireActivity(),
            getString(R.string.alert),
            getString(R.string.product_is_not_available_for_your_current_location),
            positiveButtonText = getString(R.string.ok),
            negativeButtonText = "",
            positiveButtonAction = {
                dialog?.dismiss()
            },
            negativeButtonAction = {
                dialog?.dismiss()
            }
        )
    }
    private fun showLoginPrompt() {
        var dialog: AlertDialog? = null
        dialog=  CommonUtils.showCustomAlertDialog(
            requireActivity(),
            "Sign Up Required",
            "Please sign up to add items to your bag.",
            positiveButtonText = "Sign up",
            negativeButtonText = "Cancel",
            positiveButtonAction = {
                dialog?.dismiss()
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                val bundle = Bundle()
                bundle.putString("previousScreen", "logout")
                bundle.putString("selectedImage", "0")
                intent.putExtras(bundle)
                startActivity(intent)
            },
            negativeButtonAction = {
                dialog?.dismiss()
            }
        )
    }
    private fun setObserve(){
        homeViewModel.getProductLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            var data = it.data.payload.product
                            var img = it.data.payload.product.images
                            if (data.pincode?.isNotEmpty() == true) availablePincode = data.pincode
                            stockQuantity = data.stockQuantity
                            setViewPager(img)
                            setAdapters(data)
                            binding.greyPullover.text = data.title?:""
                            binding.productDetailsDis.text = data.description?:""

                            Log.d("TAG", "setObservedfgh: $data")

                          //  binding.price.text = data.variants?.get(0)?.price?.amount
                            if (!data.variants.isNullOrEmpty()) {
                                var price = data.variants[0].price
                                binding.price.text =price?.currencyCode+" "+price?.amount

//                                variantId = data.variants?.get(0)?.id?:""
                                variantId = data.id

                            } else {
                              //  variantId = data.variants?.get(0)?.id?:""
                                binding.price.text = "INR 0.0"
                            }

                            var isWishListed = it.data.payload.product.wishlist == true

                            binding.fabIcon.setImageResource(
                                if (isWishListed) {
                               //     R.drawable.like
                                    R.drawable.wish_fev_like_icon
                                }else {
                                    R.drawable.wish_fev_unlike_icon
                                }
                            )

                            binding.fabIcon.setOnClickListener {
                                isWishListed = !isWishListed

                                binding.fabIcon.setImageResource(
                                    if (isWishListed) R.drawable.wish_fev_like_icon else R.drawable.wish_fev_unlike_icon
                                )

                                val req = AddWishListRequest(token, productId)

                                if (isWishListed) {

                                    homeViewModel.addWishLisData(req)

                                } else {

                                    homeViewModel.removeWishLisData(req)
                                }

                            }

                            Log.d("tt","sdsfdsfds, $id")
                          //  variantId = data.variants?.get(0)?.id?:""



                            Log.d("Tdkjd",data.toString())
//                            setAdapter(data)
                            // setup adapter here
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
                    Snackbar.make(requireView(), "${it.message}", Snackbar.LENGTH_SHORT).show()
                }
            }

        }
        homeViewModel.getAllPdReviewLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            var data = it.data.payload.product.reviews

                            Log.d("Tag","Ihjhfds: ${data.size}")
                            binding.numOfRating.text = data.size.toString()+" "+"Ratings"

                            val totalRating = data.sumOf { it.rating.toDouble() }
                            val averageRating = if (data.isNotEmpty()) totalRating / data.size else 0.0

                            binding.numOfRating.text = "${data.size} Ratings"
                            binding.numRate.text = String.format("%.1f", averageRating)

                            binding.rating.rating = averageRating.toFloat() ?: 0f

                            binding.rating.progressTintList = ColorStateList.valueOf(Color.BLACK)
                            binding.rating.secondaryProgressTintList = ColorStateList.valueOf(Color.BLACK)
                            binding.rating.progressBackgroundTintList = ColorStateList.valueOf(Color.GRAY)


                            /*isLoading = false
                            val newItems = it.data?.payload?.product?.reviews ?: emptyList()
//                            productDetailReviewAdapter.updateItems(newItems)

                            if (!::productDetailReviewAdapter.isInitialized) {
                                productDetailReviewAdapter = ProductDetailReviewAdapter(
                                    newItems.toMutableList())
                                binding.reviewRecyclerId.adapter = productDetailReviewAdapter
                            } else {
                                productDetailReviewAdapter.updateItems(newItems)
                            }
*/
                          /*  val reviewContainer = it.data.payload.product.reviews
                            productDetailReviewAdapter.updateItems(reviewContainer)
*/
                            val reviewContainer = it.data.payload.product.reviews
                            productDetailReviewAdapter.updateItems(reviewContainer)


                           /* if (!::productDetailReviewAdapter.isInitialized) {
                                productDetailReviewAdapter = ProductDetailReviewAdapter(reviewContainer.toMutableList())
                                binding.reviewRecyclerId.adapter = productDetailReviewAdapter
                            } else {
                                productDetailReviewAdapter.updateItems(reviewContainer)
                            }*/



                            Log.d("tt","sdsfdsfds, $id")
                            Log.d("Tdkjd",data.toString())
//                            setAdapter(data)
                            // setup adapter here
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
                    Snackbar.make(requireView(), "${it.message}", Snackbar.LENGTH_SHORT).show()
                }

            }

        }
        homeViewModel.getAddToBag().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            var message = it.data.message.toString()
                            Toast.makeText(requireActivity(),message,Toast.LENGTH_SHORT).show()
                            Preferences.setStringPreference(requireContext(), CART_ID, it.data.payload.cart.id)
                            CART_COUNT = it.data.payload.cart.lines.edges.size
                            (requireActivity() as MainActivity).updateBagBadge(CART_COUNT)
                            updateCartBadge(CART_COUNT)
                          /*  findNavController().navigate(R.id.action_productDetailsFragment_to_bagFragment)
                            (requireActivity() as MainActivity).setBottomNavSelected(R.id.navigation_bag)
*/
//                            setAdapter(data)
                            // setup adapter here
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
                    Snackbar.make(requireView(), "${it.message}", Snackbar.LENGTH_SHORT).show()
                }

            }

        }
        homeViewModel.getAddWishList().observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.SUCCESS -> {
                        val data = it.data?.payload
                        Toast.makeText(requireActivity(), "Successfully Added to Wishlist", Toast.LENGTH_SHORT).show()
                        /* if (data?.product?.wishlist==true) {
     //                        setAdapter(data)
                         } else {
                             Log.e("TAG", "Empty data or null payload")
                             Snackbar.make(requireView(), "No wishlist items found", Snackbar.LENGTH_SHORT).show()
                         }*/
                        ProcessDialog.dismissDialog(true)
                    }

                    Status.LOADING -> {
                        ProcessDialog.showDialog(requireActivity(), true)
                    }

                    Status.ERROR -> {
                        Log.e("TAG", "Error: ${it.message}")
                        ProcessDialog.dismissDialog(true)
                        Snackbar.make(requireView(), it.message ?: "Unknown error", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        homeViewModel.removeWishList().observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.SUCCESS -> {
                        val data = it.data?.payload

                        val message = it.data?.message ?: "Something went wrong"
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

//                    Toast.makeText(requireActivity(), "Remove from wishlist", Toast.LENGTH_SHORT).show()

                        ProcessDialog.dismissDialog(true)
                    }

                    Status.LOADING -> {
                        ProcessDialog.showDialog(requireActivity(), true)
                    }

                    Status.ERROR -> {
                        Log.e("TAG", "Error: ${it.message}")
                        ProcessDialog.dismissDialog(true)
                        Snackbar.make(requireView(), it.message ?: "Unknown error", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }


    }


    //new code
    private fun updateSelectedVariant() {
        val match = productVariants.firstOrNull { it.matches(selectedOptions) }

        if (match != null && match.availableForSale) {
            binding.price.text = "${match.price?.currencyCode} ${match?.price?.amount}"
            binding.BagBtn.isEnabled = true
            binding.BagBtn.setBackgroundResource(R.drawable.rectangle_000000_radius_12)
            binding.BagBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            variantId = match.id?:""
        } else {
            binding.BagBtn.isEnabled = false
            binding.BagBtn.setBackgroundResource(R.drawable.rectangle_9baoa3_12dp_radius)
            binding.BagBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            variantId = ""

//            Toast.makeText(requireContext(), "Not available", Toast.LENGTH_SHORT).show()
        }

        val availableVariants = productVariants.filter { it.availableForSale }

        val availableSizes = availableVariants
            .filter { it.partialMatch(selectedOptions, exclude = "Size") }
            .mapNotNull { it.selectedOptions.find { it.name == "Size" }?.value }

        val availableColors = availableVariants
            .filter { it.partialMatch(selectedOptions, exclude = "Color") }
            .mapNotNull { it.selectedOptions.find { it.name == "Color" }?.value }

        val availableSleeves = availableVariants
            .filter { it.partialMatch(selectedOptions, exclude = "Sleeve length type") }
            .mapNotNull { it.selectedOptions.find { it.name == "Sleeve length type" }?.value }

        productDetailSizeAdapter.updateSizeList(getAllSizes(), availableSizes.distinct())
        productDetailColorAdapter.updateColorList(getAllColors(), availableColors.distinct())
        productDetailSleeveAdapter.updateList(getAllSleeves(), availableSleeves.distinct())
    }
    fun Variant.matches(options: Map<String, String>): Boolean {
        return selectedOptions.all { option ->
            options[option.name] == option.value
        }
    }
    fun isOptionValid(optionName: String, optionValue: String): Boolean {
        val tempSelection = selectedOptions.toMutableMap()
        tempSelection[optionName] = optionValue

        return validCombinations.any { combo ->
            tempSelection.all { (key, value) -> combo[key] == value }
        }
    }
    fun Variant.partialMatch(options: Map<String, String>, exclude: String): Boolean {
        return selectedOptions.all {
            it.name == exclude || options[it.name] == it.value
        }
    }
    private fun getAllSizes(): List<String> {
        return productVariants.flatMap { variant ->
            variant.selectedOptions.filter { it.name == "Size" }.map { it.value }
        }.distinct()
    }
    private fun getAllColors(): List<String> {
        return productVariants.flatMap { variant ->
            variant.selectedOptions.filter { it.name == "Color" }.map { it.value }
        }.distinct()
    }
    private fun getAllSleeves(): List<String> {
        return productVariants.flatMap { variant ->
            variant.selectedOptions.filter { it.name == "Sleeve length type" }.map { it.value }
        }.distinct()
    }



}