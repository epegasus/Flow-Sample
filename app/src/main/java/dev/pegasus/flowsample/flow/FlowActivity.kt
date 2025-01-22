package dev.pegasus.flowsample.flow

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.pegasus.flowsample.R
import dev.pegasus.flowsample.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FlowActivity : AppCompatActivity() {

    private val viewModel by viewModels<ViewModelFlow>()

    private val tvTitle by lazy { findViewById<TextView>(R.id.tvTitle) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flow)

        initObservers()
        inflateLayout()
    }

    private fun inflateLayout() {
        supportFragmentManager.beginTransaction().replace(R.id.fcvContainer, FragmentFlow()).commit()
    }

    private fun initObservers() {
        viewModel.liveData.observe(this) {
            val text = it.value.toString()
            tvTitle.text = text
        }
    }
}

class ViewModelFlow : ViewModel() {

    private val useCaseFlow by lazy { UseCaseFlow() }

    private val _liveData = MutableLiveData<CounterFlow>()
    val liveData: LiveData<CounterFlow> get() = _liveData

    init {
        startLoopInfinite()
    }

    private fun startLoopInfinite() {
        viewModelScope.launch {
            useCaseFlow
                .startLoopInfinite()
                .map {
                    if (it.value == 10) {
                        it.copy(value = 1 / 0)// Simulate exception
                    } else {
                        it
                    }
                }
                .stateIn(
                    scope = CoroutineScope(Dispatchers.Default), // Scope for sharing
                    started = SharingStarted.WhileSubscribed(5000), // When to start/stop sharing
                    initialValue = 1 // Initial value (current state)
                )
                .shareIn(
                    scope = CoroutineScope(Dispatchers.Default), // Scope for sharing
                    started = SharingStarted.WhileSubscribed(5000), // When to start/stop sharing
                    replay = 1 // Number of values to replay to new collectors
                )
                .onEach {
                    Log.d(TAG, "startLoopInfinite: Intermediate Counter: $it")
                }
                .buffer()
                .onEach {
                    Log.i(TAG, "startLoopInfinite: Intermediate Counter: $it")
                }
                .buffer()
                .flowOn(Dispatchers.IO)
                .catch {
                    Log.e(TAG, "startLoopInfinite: Number Exception Found:", it)
                    emit(CounterFlow(-1))
                }
                .collect {
                    _liveData.value = it
                }
        }
    }
}

class UseCaseFlow {
    fun startLoopInfinite() = flow {
        var i = -3
        while (true) {
            emit(CounterFlow(i))
            i++
            delay(1000)
        }
    }
}

data class CounterFlow(
    val value: Int,
)