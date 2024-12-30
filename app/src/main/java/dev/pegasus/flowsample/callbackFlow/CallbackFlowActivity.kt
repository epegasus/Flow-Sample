package dev.pegasus.flowsample.callbackFlow

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

const val TAG = "MyTag"

class CallbackFlowActivity : AppCompatActivity() {

    private val viewModel by viewModels<ViewModelCallbackFlow>()
    private val tvTitle by lazy { findViewById<TextView>(R.id.tvTitle) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_callback_flow)

        initObservers()
    }

    private fun initObservers() {
        viewModel.liveData.observe(this) {
            tvTitle.text = it
        }
    }
}

class ViewModelCallbackFlow : ViewModel() {

    private val useCaseCallbackFlow by lazy { UseCaseCallbackFlow() }

    private val _liveData = MutableLiveData<String>()
    val liveData: LiveData<String> get() = _liveData

    init {
        fetchData()
    }

    private fun fetchData() = viewModelScope.launch {
        useCaseCallbackFlow
            .fetchData(10)
            .map { it.toString() }
            .onEach { Log.d(TAG, "fetchData: Value: $it") }
            .flowOn(Dispatchers.Default)
            .collect {
                _liveData.value = it
            }
    }
}

class UseCaseCallbackFlow {

    fun fetchData(value: Int) = callbackFlow {
        send(0)
        try {
            delay(100)
            send(value)
        } catch (e: Exception) {
            close(e)
        }

        awaitClose {

        }
    }
}