package ru.zuma.unicornclub

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DailyUnicornFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DailyUnicornFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class DailyUnicornFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null

    private var image: Bitmap? = null
    private lateinit var ivDailyImage: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_new_unicorn, container, false)

        ivDailyImage = view.findViewById(R.id.ivDailyImage)
        ivDailyImage.setOnClickListener {
            val bitmap = image
            if (bitmap == null) {
                loadImage()
            } else {
                updateDailyImage(bitmap)
            }
        }
        loadImage()

        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onNewUnicornFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun loadImage() {
        launchPrintThrowable {
            val responseBody = Backend.api.getDailyUnicornImage().unwrapCall()

            if (responseBody == null) {
                Log.e(javaClass.simpleName, "Response body is null")
                toast("Ошибка загрузки изображения")
                return@launchPrintThrowable
            }

            if (responseBody.contentType()?.type() == "image") {
                val bytes = responseBody.bytes()
                val img = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                image = img
                runOnUiThread {
                    updateDailyImage(img)
                }
            } else {
                Log.e(javaClass.simpleName, "Invalid content type. Required 'image'")
                toast("Сервер вернул не изображение")
            }
        }

    }

    private fun updateDailyImage(img: Bitmap) {
        ivDailyImage.setImageBitmap(img)
        val animation = AnimationUtils.loadAnimation(activity, R.anim.daily_image_appear)
        ivDailyImage.startAnimation(animation)
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onNewUnicornFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DailyUnicornFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                DailyUnicornFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}