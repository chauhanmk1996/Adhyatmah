package com.app.adhyatmah.presentation.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.app.adhyatmah.data.preferences.PRODUCT_TITLE
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.R
import com.app.adhyatmah.data.local.DataString
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.data.preferences.CATEGORY_TITLE
import com.app.adhyatmah.data.preferences.HANDLER
import com.app.adhyatmah.data.preferences.IS_LOGIN
import com.app.adhyatmah.data.preferences.MENU_TITLE
import com.app.adhyatmah.data.preferences.PRODUCT_ID
import com.app.adhyatmah.data.preferences.TYPE
import com.app.adhyatmah.databinding.FragmentProductListBinding
import com.app.adhyatmah.domain.model.view_all_product.request.ViewAllProductRequest
import com.app.adhyatmah.domain.model.view_all_product.response.Product
import com.app.adhyatmah.domain.model.wish_list.wish_list_request.AddWishListRequest
import com.app.adhyatmah.presentation.ui.activity.LoginActivity
import com.app.adhyatmah.presentation.ui.adapter.AdapterViewAllProduct
import com.app.adhyatmah.presentation.ui.bottom_sheet.ShortByBottomSheet
import com.app.adhyatmah.presentation.ui.viewmodel.FilterViewModel
import com.app.adhyatmah.presentation.ui.viewmodel.HomeViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.CommonUtils
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.google.android.material.snackbar.Snackbar

class ProductListFragment : BaseFragment<FragmentProductListBinding>() {
    lateinit var adapter: AdapterViewAllProduct
    private val homeViewModel by activityViewModels<HomeViewModel>()
    private val filterViewModel by activityViewModels<FilterViewModel>()

    var categoryHandle = ""
    var collectionHandle = ""
    var title = ""
    var manuTitle = ""
    var productId = ""
    var token = ""
    var discardType = ""
    var fromFilter = ""
    var type = ""


    override fun setLayout(): Int {
        return R.layout.fragment_product_list
    }


    @SuppressLint("SuspiciousIndentation")
    override fun initView(savedInstanceState: Bundle?) {

        type = arguments?.getString(TYPE).toString()
        discardType = arguments?.getString("discardType").toString()
        categoryHandle = arguments?.getString(CATEGORY_TITLE).toString()
        collectionHandle = arguments?.getString(MENU_TITLE).toString()
        manuTitle = arguments?.getString("TITLE").toString()
        title = arguments?.getString(PRODUCT_TITLE).toString()

        Log.d("Tafdfd", "fdkjhj: $collectionHandle")
        token = Preferences.getStringPreference(requireContext(), ACCESS_TOKEN).toString()


        setObserver()
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        filterAction()
//        listenToSortResult()
        fromWhere(type, fromFilter)


        try {

            if (type == "1") {
                /*  binding.filterLayoutId.setOnClickListener {
                      val bundle = Bundle()
                      categoryHandle = arguments?.getString(CATEGORY_TITLE).toString()
                      Log.d("TAG", "filterAction:fdhj  $categoryHandle")
                      bundle.putString("TYPE", "5")
                      bundle.putString("menu_handle",collectionHandle)
                      bundle.putString(CATEGORY_HANDLE_FILTER, categoryHandle)
                      findNavController().navigate(R.id.action_productListFragment_to_filterFragment, bundle)

                  }*/

                binding.shortLayout.setOnClickListener {

                    Log.d("TAG", "filterAction: $productId $categoryHandle")
                    showSortBottomSheet(categoryHandle)
                    //  findNavController().navigate(R.id.action_productListFragment_to_filterFragment)
                }

            } else if (type == "2") {
                /* binding.filterLayoutId.setOnClickListener {
                     val bundle = Bundle()
                     collectionHandle = arguments?.getString(MENU_TITLE).toString()
                     Log.d("TAG", "filterAction:fdhj  $collectionHandle")
                     bundle.putString("TYPE", "6")
                     bundle.putString("menu_handle",collectionHandle)
                     findNavController().navigate(R.id.action_productListFragment_to_filterFragment, bundle)

                 }*/

                binding.shortLayout.setOnClickListener {

                    Log.d("TAG", "filterAction: $productId $collectionHandle")
                    showSortBottomSheet(collectionHandle)
                    //  findNavController().navigate(R.id.action_productListFragment_to_filterFragment)
                }

            }
        } catch (e: Exception) {
            Log.e("TAG", "InitView: ${e.message}")
        }

    }

    fun fromWhere(type: String, fromFilter: String) {

        if (type == "1") {
            binding.header.text = title.toString() ?: "Men's T-shirt"
            productId = categoryHandle
            productId = categoryHandle
            Log.d("tag", "initdd: $categoryHandle")
            homeViewModel.getViewAllData(token, ViewAllProductRequest(categoryHandle))

            when (fromFilter) {
                "fromFilter" -> {
                    // Do nothing; filtered data already observed
                }

                "fromDisCart" -> {
                    homeViewModel.getViewAllData(token, ViewAllProductRequest(categoryHandle))
                }

                else -> {
                    homeViewModel.getViewAllData(token, ViewAllProductRequest(categoryHandle))
                }
            }
            /* if(fromFilter =="fromFilter"){

            }
            else{
                homeViewModel.getViewAllData(ViewAllProductRequest(categoryHandle))
            }*/
        } else if (type == "2") {

            binding.header.text = manuTitle.toString() ?: "Men's T-shirt"
            homeViewModel.getViewAllData(token, ViewAllProductRequest(collectionHandle))
            productId = categoryHandle

            /*else if (type == "2") {
                binding.header.text =manuTitle.toString() ?: "Men's T-shirt"

    //            binding.header.text = title.toString() ?: "Men's T-shirt"
    */
            when (fromFilter) {
                "fromFilter" -> {

                }

                "fromDisCart" -> {
                    homeViewModel.getViewAllData(token, ViewAllProductRequest(collectionHandle))
                }

                else -> {
                    homeViewModel.getViewAllData(token, ViewAllProductRequest(collectionHandle))
                }
            }
//            productId = categoryHandle
            productId = collectionHandle

        } else {
            val title = arguments?.getString(PRODUCT_TITLE).toString()
            val productId = arguments?.getString(HANDLER).toString()
            Log.d("TAG", "initView1:11 $title prdvv $productId")
            this.productId = productId

            binding.header.text = title.toString() ?: "Men's T-shirt"
            homeViewModel.getViewAllData(token, ViewAllProductRequest(productId))

            /*if(fromFilter =="fromFilter"){

            }else{
                homeViewModel.getViewAllData(ViewAllProductRequest(productId))
            }
*/
            when (fromFilter) {
                "fromFilter" -> {
//                    homeViewModel.getViewAllData(ViewAllProductRequest(productId, token))
                    homeViewModel.getViewAllData(token, ViewAllProductRequest(productId, ""))
                }

                "fromDisCart" -> {
//                    homeViewModel.getViewAllData(ViewAllProductRequest(productId, token))
                    homeViewModel.getViewAllData(token, ViewAllProductRequest(productId))
                }

                else -> {
                    homeViewModel.getViewAllData(token, ViewAllProductRequest(productId))
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        /*  fromFilter = arguments?.getString(FROM_FILTER).toString()
        val type = arguments?.getString(TYPE).toString()
        fromWhere(type, fromFilter)
      */

        fromWhere(type, fromFilter)
        setObserver()
    }

    fun setAdapter(data: List<Product>) {

        adapter = AdapterViewAllProduct(
            data, true, ::isLikeClick,
            onSubAdapterClick = ::onSubAdapterImgClick
        )
        binding.productListRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.productListRecyclerView.adapter = adapter

    }

    fun isLikeClick(position: Int, isLike: Boolean) {

        val product = adapter.getItemAt(position) // Make sure adapter exposes this function
        val request = AddWishListRequest(token, product.id)
        val isLogin = Preferences.getStringPreference(requireContext(), IS_LOGIN)

        if (isLogin == "1") {
            if (isLike) {
                homeViewModel.addWishLisData(request)
            } else {
                homeViewModel.removeWishLisData(request)
            }

        } else {
            showLoginPrompt()
        }
    }

    fun onSubAdapterImgClick(position: Int, isTrue: Boolean, data: Product) {
        Log.d("TAG", "onSubAdapterImgClick: ${data.id}")

        val productId = data.id
        val bundle = Bundle()
        bundle.putString(TYPE, "3")
        bundle.putString(PRODUCT_ID, productId)
        findNavController().navigate(
            R.id.action_productListFragment_to_productDetailsFragment,
            bundle
        )
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


    private fun setObserver() {
        homeViewModel.getViewAllLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.status // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            val data = it.data.payload.collection.products
                            setAdapter(data)
                            adapter.updateData(data)
                            // Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()

                        }

                        401 -> {
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT)
                                .show()
                        }

                        404 -> {
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT)
                                .show()
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

        /*filterViewModel.getShortCollectionList().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code
                    if (statusCode == 200) {
                        val sortedProducts = it.data.payload.products
                        setAdapter(sortedProducts) // ✅ Update UI
                    } else {
                        Log.e("TAG", "Sorting API returned non-200")
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
        */
        filterViewModel.getShortCollectionList().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code
                    if (statusCode == 200) {
                        val sortedProducts = it.data.payload.collection.products
                        setAdapter(sortedProducts) // ✅ Update UI
                    } else {
                        Log.e("TAG", "Sorting API returned non-200")
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
        filterViewModel.getApplyFilter().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code
                    if (statusCode == 200) {
                        val sortedProducts = it.data.payload.collection.products
                        Log.d("TAG", "setObccserver: $sortedProducts")
                        setAdapter(sortedProducts) // ✅ Update UI
                    } else {
                        Log.e("TAG", "Sorting API returned non-200")
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
        homeViewModel.removeWishList().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val data = it.data?.payload

                    val message = it.message ?: "Something went wrong"
                    //   Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

//                    Toast.makeText(requireActivity(), "Remove from wishlist", Toast.LENGTH_SHORT).show()

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


    }


    fun filterAction() {
        /* binding.filterLayoutId.setOnClickListener {
             val bundle = Bundle()
             val handle = arguments?.getString(HANDLER).toString()

             Log.d("TAG", "filterAction:cdcd  $categoryHandle $handle")
             bundle.putString(PRODUCT_ID, handle)
             findNavController().navigate(R.id.action_productListFragment_to_filterFragment, bundle)

         }*/
        binding.shortLayout.setOnClickListener {
            val handle = arguments?.getString(HANDLER).toString()
            val productId = arguments?.getString(PRODUCT_TITLE).toString()
            // val productId = arguments?.getString(PRODUCT_TITLE).toString()
            Log.d("TAG", "filterAction: $productId $handle")
            showSortBottomSheet(handle)
            //  findNavController().navigate(R.id.action_productListFragment_to_filterFragment)
        }

    }

    private fun showSortBottomSheet(productId: String) {
        val bottomSheet = ShortByBottomSheet.newInstance(productId)

        bottomSheet.onSortSelected = { sortedList ->
            Log.i("TAG", "showSortBottomSheet: ")
            adapter.updateData(sortedList)
        }
        bottomSheet.onSortRequestSent = { request ->
            Log.i("TAG", "showSortBottomSheet: request ")
            homeViewModel.getViewAllData(token, request)
        }
        bottomSheet.show(parentFragmentManager, "ShortByBottomSheet")
    }

    private fun listenToSortResult() {
        parentFragmentManager.setFragmentResultListener("sort_result_key", this) { _, bundle ->
            val title = bundle.getString("selected_sort_title") ?: return@setFragmentResultListener
            Toast.makeText(requireContext(), "Selected: $title", Toast.LENGTH_SHORT).show()
        }
    }


}