package com.lightworld.expression


import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_expression.*


/**
 * A simple [Fragment] subclass.
 * Use the [ExpressionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExpressionFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var mAdapter: PullToRefreshAdapter? = null
    var dataCenter = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_expression, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189))
        initAdapter()
    }

    private fun initAdapter() {


//        for (i in 1..10) {
//            dataCenter.add(ExpressionBean("http://vipheyue.oss-cn-hangzhou.aliyuncs.com/temporaryAPK/0a7fbf1780401add4f2a166a073642d6_713_50.jpg"))
//            dataCenter.add(ExpressionBean("http://vipheyue.oss-cn-hangzhou.aliyuncs.com/temporaryAPK/5d25c3b28864d7c35eed2884ba7a9975_782_98.jpg"))
//        }

        mAdapter = PullToRefreshAdapter(R.layout.item_expression, dataCenter)
//        mAdapter.setOnLoadMoreListener(object : BaseQuickAdapter.RequestLoadMoreListener() {
//            fun onLoadMoreRequested() {
//                loadMore()
//            }
//        })
//        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT)
        //        mAdapter.setPreLoadNumber(3);
        mRecyclerView.setLayoutManager(GridLayoutManager(activity, 3))
//        mRecyclerView.addItemDecoration()
        mRecyclerView.adapter = mAdapter

        //访问网络
        val jsObjRequest = JsonObjectRequest(Request.Method.GET, mParam2, null, Response.Listener { response ->

            var netDataBean = Gson().fromJson<ExpressionBean>(response.toString(), ExpressionBean::class.java)
            for (item in netDataBean.listData) {
                dataCenter.add(netDataBean.host + item)
            }
            mAdapter!!.notifyDataSetChanged()
        }, Response.ErrorListener {
            Toast.makeText(activity, "" + it.toString(), Toast.LENGTH_SHORT).show()

        })
        Volley.newRequestQueue(activity).add(jsObjRequest)

    }

    companion object {
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"
        fun newInstance(param1: String, param2: String): ExpressionFragment {
            val fragment = ExpressionFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
