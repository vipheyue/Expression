package com.lightworld.expression


import android.graphics.Color
import android.os.Bundle
import android.os.Handler
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
    private var dataCenter = ArrayList<String>()
    private var showedDataCenter = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_expression, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189))
        initAdapter()
        mSwipeRefreshLayout.setOnRefreshListener { refresh() }
        refresh()

    }

    private fun initAdapter() {
        mAdapter = PullToRefreshAdapter( R.layout.item_expression, showedDataCenter)
        mAdapter!!.setOnLoadMoreListener { loadMore() }
//        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT)
        //        mAdapter.setPreLoadNumber(3);
        mRecyclerView.layoutManager = GridLayoutManager(activity, 3)
//        mRecyclerView.addItemDecoration()
        mRecyclerView.adapter = mAdapter
    }


    private fun loadMore() {
        //添加20个数据到 showedDataCenter 中
        dataCenter.shuffle()
        var index = 0
        for (item: String in dataCenter) {
            showedDataCenter.add(dataCenter[index])
            index++
            if (index == 20) {
                break
            }
        }
        val handler = Handler()
        val r = Runnable {
            mAdapter?.notifyDataSetChanged()
            mAdapter?.loadMoreComplete()
        }
        handler.post(r)

    }

    private fun refresh() {
        dataCenter.clear()
        showedDataCenter.clear()
        mSwipeRefreshLayout.isRefreshing = true
        mAdapter?.setEnableLoadMore(false)//这里的作用是防止下拉刷新的时候还可以上拉加载

        //访问网络
        val jsObjRequest = JsonObjectRequest(Request.Method.GET, mParam2, null, Response.Listener { response ->

            val netDataBean = Gson().fromJson<ExpressionBean>(response.toString(), ExpressionBean::class.java)
            for (item in netDataBean.listData) {
                dataCenter.add(netDataBean.host + item)
            }
            //添加20个数据到 showedDataCenter 中
            dataCenter.shuffle()
            var index = 0
            for (item: String in dataCenter) {
                showedDataCenter.add(dataCenter[index])
                index++
                if (index == 20) {
                    break
                }
            }

            mAdapter?.setEnableLoadMore(true)
            mSwipeRefreshLayout?.isRefreshing = false
            mAdapter?.notifyDataSetChanged()

        }, Response.ErrorListener {
            Toast.makeText(activity, "" + it.toString(), Toast.LENGTH_SHORT).show()
            mAdapter?.setEnableLoadMore(true)
            mSwipeRefreshLayout.isRefreshing = false
        })
        Volley.newRequestQueue(activity).add(jsObjRequest)

    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
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
