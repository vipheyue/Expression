package com.lightworld.expression

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var fragmentArrayList: ArrayList<Fragment>? =  ArrayList()
    private var currentFragment: Fragment? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                switchContent(fragmentArrayList!!.get(0))
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                switchContent(fragmentArrayList!!.get(1))
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        initView()
    }

    private fun initView() {
        /*第一步: 准备 数据 */
        val homeFragment = MainFragment()
        val meFragment = MeFragment()
        fragmentArrayList!!.add(homeFragment);
        fragmentArrayList!!.add(meFragment);
        switchContent(fragmentArrayList!!.get(0))
    }

    fun switchContent(to: Fragment) {
        val fm = supportFragmentManager
        val transaction = fm.beginTransaction()
        if (currentFragment != null) {
            if (currentFragment !== to) {
                if (!to.isAdded) {    // 先判断是否被add过
                    transaction.hide(currentFragment).add(R.id.fl_container, to).commit() // 隐藏当前的fragment，add下一个到Activity中
                } else {
                    transaction.hide(currentFragment).show(to).commit() // 隐藏当前的fragment，显示下一个
                }
            }
        } else {
            transaction.replace(R.id.fl_container, to)
            transaction.commit()
        }
        currentFragment = to
    }
}
