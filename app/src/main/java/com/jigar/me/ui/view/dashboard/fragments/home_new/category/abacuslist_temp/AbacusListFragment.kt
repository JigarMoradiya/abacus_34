package com.jigar.me.ui.view.dashboard.fragments.home_new.category.abacuslist_temp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.jigar.me.R
import com.jigar.me.databinding.FragmentAbacusListBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.ui.viewmodel.AppViewModel
import com.jigar.me.utils.extensions.onClick
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AbacusListFragment : BaseFragment() {
    private lateinit var binding: FragmentAbacusListBinding
    private var root: View? = null
    private var mNavController: NavController? = null

    private val appViewModel by viewModels<AppViewModel>()
    private lateinit var abacusListAdapter: AbacusListAdapter
    private var setId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setId = AbacusListFragmentArgs.fromBundle(requireArguments()).setId
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (root == null) {
            binding = FragmentAbacusListBinding.inflate(inflater, container, false)
            root = binding.root
            setNavigationGraph()
            initViews()
        }
        return root
    }

    private fun setNavigationGraph() {
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }

    private fun initViews() = with(binding) {
        cardBack.onClick { onBack() }
        CoroutineScope(Dispatchers.Main).launch {
            val abacusList = appViewModel.getAbacus(setId)
            abacusListAdapter = AbacusListAdapter(abacusList)
            recyclerview.adapter = abacusListAdapter
        }
    }

    private fun onBack() {
        mNavController?.navigateUp()
    }
}