package com.example.features.player.presentation.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.features.player.presentation.databinding.FragmentEqualizerBinding
import com.example.features.player.presentation.databinding.LayoutEqualizerBandItemBinding
import com.example.features.player.presentation.databinding.LayoutEqualizerPresetItemBinding
import com.example.features.player.presentation.model.AudioEffectPlayerPresentationModel
import com.example.features.player.presentation.model.BassBoostVirtualizerEffectPlayerUIModel
import com.example.features.player.presentation.values.effectType
import com.example.features.player.presentation.viewmodel.ViewModelPlayer
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.collections.chunked

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentEqualizer.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentEqualizer : DialogFragment() {
    // TODO: Rename and change types of parameters
    /*private var param1: String? = null
    private var param2: String? = null*/

    interface OnEqualizerStateChanged {

        fun onChanged(isEnabled: Boolean)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentEqualizer.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =
            FragmentEqualizer().apply {
                arguments = Bundle().apply {
                    /*putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)*/
                }
            }
    }
    val bandXAxisLabels = listOf("60Hz", "230Hz", "910Hz", "3kHz", "14kHz")

    private val bandSliders: ArrayList<LinearLayout> = ArrayList()

    private val presets: ArrayList<MaterialButton> = ArrayList()

    private var selectedEffect: AudioEffectPlayerPresentationModel? = null

    private var bassBoostEffect: BassBoostVirtualizerEffectPlayerUIModel? = null

    private var equalizerEnabled: Boolean = false

    private val viewModelPlayer: ViewModelPlayer by viewModel<ViewModelPlayer>(ownerProducer = { requireActivity() })

    private var _binding: FragmentEqualizerBinding?= null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            /*param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)*/
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEqualizerBinding.inflate(
            inflater,
            container,
            false
        )
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelPlayer.enableEqualizer.collect {

                    equalizerEnabled = it

                    handleEqualizerToggleChange(it)

                    Log.d("equalizer", equalizerEnabled.toString())
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelPlayer.audioEffects.collect {

                    selectedEffect = it

                    handlePresetChange(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelPlayer.bassBoostEffects.collect {

                    bassBoostEffect = it

                    handleBassBoostVirtualizerChange(it)
                }
            }
        }

        binding.equalizerSwitch.isChecked = equalizerEnabled

        binding.equalizerSwitch.setOnClickListener { l ->

            viewModelPlayer.toggleEqualizer()
        }

        binding.back.setOnClickListener { l ->

            this.dismiss()
        }

        binding.bassBoost.addOnChangeListener { slider, value, fromUser ->

            viewModelPlayer.onBassBoostLevelChanged(value.toInt())
        }

        binding.virtualizer.addOnChangeListener { slider, value, fromUser ->

            viewModelPlayer.onVirtualizerLevelChanged(value.toInt())
        }

        bandXAxisLabels.forEachIndexed { index, label ->

            val bandItem = createBandItem(label, index)

            bandSliders.add(bandItem)

            binding.bands.addView(bandItem)
        }

        val groupedList = effectType.chunked(4)

        for (list in groupedList) {

            binding.presets.addView(
                createLayoutForPresetsRow(list)
            )
        }
    }

    private fun handleEqualizerToggleChange(equalizerToggled: Boolean) {

        updateEqualizerSwitch(equalizerToggled)

        updateBandItems(equalizerToggled)

        updatePresetItems(equalizerToggled)

        updateBassBoostVirtualizerSliders(equalizerToggled)
    }

    private fun updateBassBoostVirtualizerSliders(equalizerEnabled: Boolean) {

        binding.bassBoost.isEnabled = equalizerEnabled

        binding.virtualizer.isEnabled = equalizerEnabled
    }

    private fun updateEqualizerSwitch(equalizerEnabled: Boolean) {

        binding.equalizerSwitch.isChecked = equalizerEnabled
    }

    private fun updateBandItems(equalizerEnabled: Boolean) {

        bandSliders.forEachIndexed { index, layout ->

            val slider = layout.findViewWithTag<Slider>("slider_$index")

            slider.isEnabled = equalizerEnabled
        }
    }

    private fun updatePresetItems(equalizerEnabled: Boolean) {

        presets.forEachIndexed { index, button ->

            val button = binding.presets.findViewWithTag<MaterialButton>("preset_$index")

            button.isEnabled = equalizerEnabled
        }
    }

    private fun handleBassBoostVirtualizerChange(preset: BassBoostVirtualizerEffectPlayerUIModel?) {

        updateBassBoostSlider(preset?.bassBoostStrength)

        updateVirtualizerSlider(preset?.virtualizerStrength)
    }

    private fun updateBassBoostSlider(value: Int?) {

        value?.let {
            binding.bassBoost.value = value.toFloat()
        }
    }

    private fun updateVirtualizerSlider(value: Int?) {

        value?.let {
            binding.virtualizer.value = value.toFloat()
        }
    }

    private fun handlePresetChange(preset: AudioEffectPlayerPresentationModel?) {

        updateSlidersOnPresetChange(preset)

        updatePresetButtonsOnPresetChange(preset)
    }

    private fun updateSlidersOnPresetChange(preset: AudioEffectPlayerPresentationModel?) {

        bandSliders.forEachIndexed { index, layout ->

            val slider = layout.findViewWithTag<Slider>("slider_$index")

            preset?.gainValues[index]?.let {

                slider.value = it.times(1000f).toFloat().coerceIn(-3000f, 3000f)
            }
        }
    }

    private fun updatePresetButtonsOnPresetChange(preset: AudioEffectPlayerPresentationModel?) {

        presets.forEachIndexed { index, button ->

            val button = binding.presets.findViewWithTag<MaterialButton>("preset_$index")

            preset?.selectedEffectType?.let {

                button.isChecked = (index == it)
            }
        }
    }

    private fun createBandItem(label: String, index: Int): LinearLayout {

        val bandBinding = LayoutEqualizerBandItemBinding.inflate(
            layoutInflater,
            null,
            false
        )

        bandBinding.sliderText.text = label

        bandBinding.slider.isEnabled = equalizerEnabled

        bandBinding.slider.tag = "slider_$index"

        selectedEffect?.gainValues[index]?.let {
            bandBinding.slider.value = it.times(1000f).toFloat()
                .coerceIn(-3000f, 3000f)
        }

        bandBinding.slider.addOnChangeListener { slider, value, fromUser ->

            if (fromUser) {

                viewModelPlayer.onBandLevelChanged(index, value.toInt())
            }
        }

        return bandBinding.root
    }

    private fun createLayoutForPresetsRow(presetRow: List<String>): LinearLayout {

        val layout = LinearLayout(context)

        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        layout.layoutParams = layoutParams

        layout.orientation = LinearLayout.HORIZONTAL

        layout.gravity = Gravity.CENTER

        presetRow.forEach {

            val index = effectType.indexOf(it)

            val presetButton = createPresetItem(it, index)

            presets.add(presetButton)

            layout.addView(presetButton)
        }

        return layout
    }

    private fun createPresetItem(presetName: String, index: Int): MaterialButton {

        val button = LayoutEqualizerPresetItemBinding.inflate(
            layoutInflater,
            null,
            false
        )

        button.presetItem.text = presetName

        button.presetItem.tag = "preset_$index"

        button.presetItem.isEnabled = equalizerEnabled

        button.presetItem.isCheckable = true

        button.presetItem.isChecked = (index == selectedEffect?.selectedEffectType)

        button.presetItem.setOnClickListener { l ->

            viewModelPlayer.onSelectPreset(index)
        }

        return button.presetItem
    }
}