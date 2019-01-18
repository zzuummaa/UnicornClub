package ru.zuma.unicornclub

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.GridLayoutManager


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CollectionFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CollectionFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class CollectionFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var listener: OnFragmentInteractionListener? = null
    private var unicornImages = ArrayList<UnicornImage>()
    private lateinit var unicornsAdapter: ImageGalleryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_collection, container, false)

        val layoutManager = GridLayoutManager(activity, 2)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvImages)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager

//        launchPrintThrowable {
//            loadCollection()
//        }

        unicornsAdapter = ImageGalleryAdapter(activity!!, unicornImages)
        recyclerView.adapter = unicornsAdapter

        return view
    }

    private suspend fun loadCollection() {
        val unicorns = Backend.auth.waitAuthBefore {
            Backend.api.getUnicornCollection().unwrapCall()
        } ?: return

        unicornImages.clear()
        unicorns.forEach {
            val date = it.date ?: return

            unicornImages.add(UnicornImage(
                date.substring(0, 2).toInt(),
                date.substring(2, 4).toInt(),
                true
            ))
        }

        runOnUiThread {
            unicornsAdapter.notifyDataSetChanged()
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onCollectionFragmentInteraction(uri)
    }

    fun doLoadCollection() {
        launchPrintThrowable {
            loadCollection()
        }
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
        fun onCollectionFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CollectionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                CollectionFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
