/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tensorflow.lite.examples.audio.fragments

//import android.widget.RadioGroup
//import androidx.navigation.Navigation
import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_record.recordButton
import kotlinx.android.synthetic.main.fragment_record.playButton
import org.tensorflow.lite.InterpreterApi
import org.tensorflow.lite.examples.audio.AudioDenoiseHelper
import org.tensorflow.lite.examples.audio.databinding.FragmentRecordBinding

import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.Tensor
import org.tensorflow.lite.examples.audio.ml.TfModel10s
import org.tensorflow.lite.support.audio.TensorAudio
import org.tensorflow.lite.support.audio.TensorAudio.TensorAudioFormat
import java.io.File


interface AudioDenoiseListener {
    fun onError(error: String)
    fun onResult(inferenceTime: Long)
}

class RecordFragment : Fragment() {
    private var _fragmentBinding: FragmentRecordBinding? = null
    private var startRecording: Boolean = true
    private val fragmentRecordBinding get() = _fragmentBinding!!
    private var recorder: AudioRecord? = null
    private var player: MediaPlayer? = null
    private var startPlaying: Boolean = true
//    private var fileName: String = ""
    private var model: TfModel10s? = null

    private val maxAudioSamples: Int = 16000 * 60
    private val frameSamples: Int = 16 * 40
    private val modelSamples: Int = 160085

    private lateinit var denoiseHelper: AudioDenoiseHelper
    private val LOG_TAG : String = "record fragment"

    private val audioDenoiseListener = object : AudioDenoiseListener {
        override fun onResult(inferenceTime: Long) {
            requireActivity().runOnUiThread {
                fragmentRecordBinding.bottomSheetLayout.inferenceTimeVal.text =
                    String.format("%d ms", inferenceTime)
            }
        }

        override fun onError(error: String) {
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstsanceState: Bundle?
    ): View {
        _fragmentBinding = FragmentRecordBinding.inflate(inflater, container, false)

        return fragmentRecordBinding.root
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        denoiseHelper = AudioDenoiseHelper(
            requireContext(),
            audioDenoiseListener
        )

        model = TfModel10s.newInstance(requireContext())

//        fileName = "${requireContext().externalCacheDir?.path}/audiorecordtest.3gp"
//        Log.i(LOG_TAG, fileName)

        recordButton.setOnClickListener {
            if (startRecording) {
                recorder = AudioRecord(
                    MediaRecorder.AudioSource.DEFAULT,
                    16000,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_FLOAT,
                    4 * maxAudioSamples,
                )
                recorder?.startRecording()
//                recorder = MediaRecorder(it.context).apply {
//                    setAudioSource(MediaRecorder.AudioSource.MIC)
//                    setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
//                    setOutputFile(fileName)
//                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
//
//                    prepare()
//                    start()
//                }
            } else {
                Log.i(LOG_TAG, "finish recording")
                recorder?.stop()
            }
            startRecording = !startRecording
        }

        playButton.setOnClickListener {
            if (startPlaying) {
                // process the audio
//                val audioData = FloatArray(maxAudioSamples)
//                val readState: Int? = recorder?.read(audioData, 0, maxAudioSamples, AudioRecord.READ_NON_BLOCKING)
//                readState?.toString()?.let { it1 -> Log.i(LOG_TAG, it1) }
//                for(idx in 0 until (maxAudioSamples/frameSamples)){
//                }
                val tfAudioFormat = TensorAudioFormat.builder().apply {
                    setChannels(1)
                    setSampleRate(16000)
                }
                val tensor = TensorAudio.create(tfAudioFormat.build(), modelSamples)
                tensor.load(recorder)

                val outputTensor = model!!.process(tensor.tensorBuffer).outputFeature0AsTensorBuffer
//                val outputTensor = TensorAudio.create(tfAudioFormat.build(), modelSamples)
//                interpreter!!.run(tensor, outputTensor)
                Log.i(LOG_TAG, "finish processing audio")
                // play the audio

                Log.i(LOG_TAG, outputTensor.floatArray.sum().toString())

                // release and remove recorder
                recorder?.release()
                recorder = null
            } else {

            }
            startPlaying = !startPlaying
        }

        // Allow the user to select between multiple supported audio models.
        // DELETED

        // Allow the user to change the amount of overlap used in classification. More overlap
        // can lead to more accurate resolves in classification.
        // DELETED

        // Allow the user to change the max number of results returned by the audio classifier.
        // Currently allows between 1 and 5 results, but can be edited here.
        // DELETED

        // Allow the user to change the confidence threshold required for the classifier to return
        // a result. Increments in steps of 10%.
        // DELETED

        // Allow the user to change the number of threads used for classification
        // DELETED

        // When clicked, change the underlying hardware used for inference. Current options are CPU
        // and NNAPI. GPU is another available option, but when using this option you will need
        // to initialize the classifier on the thread that does the classifying. This requires a
        // different app structure than is used in this sample.
        // DELETED
    }

//    override fun onResume() {
//        super.onResume()
//        // Make sure that all permissions are still present, since the
//        // user could have removed them while the app was in paused state.
//        if (!PermissionsFragment.hasPermissions(requireContext())) {
//            Navigation.findNavController(requireActivity(), R.id.fragment_container)
//                .navigate(AudioFragmentDirections.actionAudioToPermissions())
//        }
//
//        if (::audioHelper.isInitialized ) {
//            audioHelper.startAudioClassification()
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//        if (::audioHelper.isInitialized ) {
//            audioHelper.stopAudioClassification()
//        }
//    }
//
//    override fun onDestroyView() {
//        _fragmentBinding = null
//        super.onDestroyView()
//    }
}
