package com.jigar.me.ui.view.dashboard.fragments.number_puzzles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.jigar.me.R
import com.jigar.me.databinding.FragmentPuzzleNumberHomeBinding
import com.jigar.me.ui.view.base.BaseFragment
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.extensions.onClick
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PuzzleNumberHomeFragment : BaseFragment(){
    private lateinit var mBinding: FragmentPuzzleNumberHomeBinding
    private lateinit var mNavController: NavController
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = FragmentPuzzleNumberHomeBinding.inflate(inflater, container, false)
        setNavigationGraph()
        initListener()
        return mBinding.root
    }
    private fun setNavigationGraph() {
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
    }
    fun initListener() {
        mBinding.img9.onClick { onPuzzle8Click() }
        mBinding.img16.onClick { onPuzzle15Click() }
        mBinding.img25.onClick { onPuzzle24Click() }
        mBinding.txt9.onClick { mBinding.img9.performClick() }
        mBinding.txt16.onClick { mBinding.img16.performClick() }
        mBinding.txt25.onClick { mBinding.img25.performClick() }
        mBinding.cardBack.onClick { mNavController.navigateUp() }
    }

    private fun onPuzzle8Click() {
        gotoNext(3)
    }

    private fun onPuzzle15Click() {
        gotoNext(4)
    }

    private fun onPuzzle24Click() {
        gotoNext(5)
    }

    private fun gotoNext(type: Int) {
        val action = PuzzleNumberHomeFragmentDirections.actionPuzzleNumberHomeFragmentToPuzzleNumberFragment(type)
        mNavController.navigate(action)
    }


}