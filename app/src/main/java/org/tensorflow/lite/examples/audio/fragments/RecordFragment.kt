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
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_record.recordButton
import kotlinx.android.synthetic.main.fragment_record.playButton
import org.tensorflow.lite.examples.audio.AudioDenoiseHelper
import org.tensorflow.lite.examples.audio.databinding.FragmentRecordBinding
//import org.tensorflow.lite.examples.audio.R

interface AudioDenoiseListener {
    fun onError(error: String)
    fun onResult(inferenceTime: Long)
}

class RecordFragment : Fragment() {
    private var _fragmentBinding: FragmentRecordBinding? = null
    private var startRecording: Boolean = true
    private val fragmentRecordBinding get() = _fragmentBinding!!
    private var recorder: MediaRecorder? = null
    private var fileName: String = ""

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

        fileName = "${Environment.getExternalStorageDirectory().path}/audiorecordtest.3gp"
        Log.i(LOG_TAG, fileName)

        return fragmentRecordBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        denoiseHelper = AudioDenoiseHelper(
            requireContext(),
            audioDenoiseListener
        )

        recordButton.setOnClickListener {
            if (startRecording) {
                recorder = MediaRecorder(it.context).apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                    setOutputFile(fileName)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

                    try {
                        prepare()
                    } catch (e: Exception) {
                        Log.e(LOG_TAG, "prepare() failed")
                    }
                    start()
                }
            } else {
                recorder?.apply {
                    stop()
                    release()
                }
                recorder = null
            }
            startRecording = !startRecording
        }

        playButton.setOnClickListener {
            it.id
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
