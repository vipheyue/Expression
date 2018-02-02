package com.lightworld.expression


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_main.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class MainFragment : Fragment() {

    private val mFragments = ArrayList<Fragment>()
    private val mTitles = ArrayList<String>()
    //    private val mTitles = arrayOf("热门", "欺负你", "搞怪", "装逼", "开心", "震惊", "难过", "生气", "节日", "表白", "游戏")
    private var mAdapter: MyPagerAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestMainJson()


    }


    private fun requestMainJson() {
        val url = "http://heyue.oss-cn-hangzhou.aliyuncs.com/AppData/expression/main.json"
        val jsObjRequest = JsonObjectRequest(Request.Method.GET, url, null, Response.Listener { response ->
            dealData(response.toString())
        }, Response.ErrorListener {
            Toast.makeText(activity, "" + it.toString(), Toast.LENGTH_SHORT).show()
            val open = activity!!.assets.open("main.json")
            val readString = ReadAssetsJsonUtils.read(open)
            dealData(readString)
        })
        Volley.newRequestQueue(activity).add(jsObjRequest)
    }

    private fun dealData(json: String) {
        val mainDataBean = Gson().fromJson<MainBean>(json, MainBean::class.java)
        val data = mainDataBean.data

        for (item in data) {
            mTitles.add(item.title)
            mFragments.add(ExpressionFragment.newInstance(item.title, item.url))
        }
        mAdapter = activity?.supportFragmentManager?.let { MyPagerAdapter(it) }
        vp?.adapter = mAdapter
        slide_tab_layout.setViewPager(vp)
        vp.currentItem = 1
    }

    private inner class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getCount(): Int {
            return mFragments.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mTitles[position]
        }

        override fun getItem(position: Int): Fragment {
            return mFragments[position]
        }
    }

}// Required empty public constructor
