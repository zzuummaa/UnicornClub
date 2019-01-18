package ru.zuma.unicornclub

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.graphics.Palette
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target;
import kotlinx.android.synthetic.main.activity_space_photo.*
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking


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

    private var isLoadWorking = false
    private var isUnicornInCollection = false

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
                updateDailyImageView(bitmap)
            }
        }
        loadImage()

        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onShow(uri: Uri) {
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

    fun loadImage() {
        if (isLoadWorking) return

        launchPrintThrowable({ isLoadWorking = false }) {

            val dailyUnicorn = Backend.auth.waitAuthBefore {
                Backend.api.getDailyUnicorn().unwrapCall()
            }

            if (dailyUnicorn?.url == null) {
                Log.e(javaClass.simpleName, "Response field or body is null")
                toastUI("Ошибка загрузки изображения")
                isLoadWorking = false
                return@launchPrintThrowable
            }

            if (!isUnicornInCollection) {
                async {
                    Backend.api.saveDailyUnicornToCollection().unwrapCall()?.let {
                        isUnicornInCollection = true
                    }
                }
            }

            runOnUiThread {
                Glide.with(activity!!)
                    .load(dailyUnicorn.url)
                    .asBitmap()
                    .animate(R.anim.daily_image_appear)
                    .listener(object : RequestListener<String, Bitmap> {
                        override fun onException(e: Exception, model: String, target: Target<Bitmap>, isFirstResource: Boolean): Boolean {
                            isLoadWorking = false

                            Log.e(this@DailyUnicornFragment.javaClass.simpleName, "Load failed", e)
                            return false
                        }

                        override fun onResourceReady(resource: Bitmap, model: String, target: Target<Bitmap>, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
                            updateDailyImageView(resource)
                            image = resource
                            isLoadWorking = false
                            Log.d(this@DailyUnicornFragment.javaClass.simpleName,
                                    "Image " + dailyUnicorn.url + " loaded")
                            return true
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(ivDailyImage)
            }
        }
    }

    private fun updateDailyImageView(img: Bitmap) {
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