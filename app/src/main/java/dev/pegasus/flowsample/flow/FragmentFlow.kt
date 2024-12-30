package dev.pegasus.flowsample.flow

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dev.pegasus.flowsample.R

/**
 *   Developer: Sohaib Ahmed
 *   Date: 12/30/2024
 *   Profile:
 *     -> github.com/epegasus
 *     -> linkedin.com/in/epegasus
 */

class FragmentFlow : Fragment() {

    private val viewModel by activityViewModels<ViewModelFlow>()
    private var tvSharedTitle: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_flow, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        Handler(Looper.getMainLooper()).postDelayed(this::initObservers, 5000)
    }

    private fun initViews(view: View) {
        tvSharedTitle = view.findViewById(R.id.tvSharedTitle)
    }

    private fun initObservers() {
        viewModel.liveData.observe(viewLifecycleOwner) {
            val text = it.value.toString()
            tvSharedTitle?.text = text
        }
    }
}