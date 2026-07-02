package com.bio.idcardrecognition

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ResultPortraitFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ResultPortraitFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var profileImg: ImageView
    private lateinit var nameTxt: TextView
    private lateinit var typeTxt: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_result_portrait, container, false)
        profileImg = view.findViewById(R.id.profileImg)
        profileImg.setImageBitmap(ResultActivity.profileBmp)

        nameTxt = view.findViewById(R.id.nameTxt)
        nameTxt.text = ResultActivity.nameTxt

        typeTxt = view.findViewById(R.id.typeTxt)
        typeTxt.text = ResultActivity.typeTxt

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ResultOverviewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(): ResultPortraitFragment {
            return ResultPortraitFragment()
        }
    }
}