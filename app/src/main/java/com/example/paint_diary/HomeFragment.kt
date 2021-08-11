package com.example.paint_diary

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.diary_item.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_mypage.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class HomeFragment : Fragment() {
    lateinit var diaryRecyclerview: DiaryRecyclerview
    var diary = ArrayList<DiaryRequest>()
    companion object {
        const val TAG : String = "로그"
        fun newInstance() : HomeFragment {
            return HomeFragment()
        }
    }

    //메모리에 올라갔을 때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        Log.d(TAG, "HomeFragment = onCreate() called")
    }

    //프래그먼트를 안고 있는 엑티비티에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "HomeFragment = onAttach() called")

    }

    //뷰가 생성 되었을 때
    //프래그먼트와 뷰를 연결시킴
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        home_toolbar.inflateMenu(R.menu.home_menu)
        home_toolbar.setTitleTextColor(Color.WHITE)
        home_toolbar.setTitle("홈")



        write_btn.setOnClickListener{
            //프래그먼트 -> 액티비티 화면 이동
            val intent = Intent(context, PaintActivity::class.java)
            startActivity(intent)

        }

        diaryRecyclerview = DiaryRecyclerview()

        val layoutManager = GridLayoutManager(activity, 2)
        val spaceDecoration = RecyclerDecoration(20)
        diary_list.addItemDecoration(spaceDecoration)
        diary_list.layoutManager = layoutManager
        requestDiary()

        diaryRecyclerview.setItemClickListener(object : DiaryRecyclerview.ItemClickListener {
            override fun onClick(view: View, position: Int, title: String) {
                Toast.makeText(activity, title, Toast.LENGTH_SHORT).show()
            }
        })

        diary_refresh.setOnRefreshListener {
            requestDiary()
            diary_refresh.isRefreshing = false
        }

    }

    private fun requestDiary() {
        var gson: Gson = GsonBuilder()
            .setLenient()
            .create()

        var retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        var diary_request = retrofit.create(IRetrofit::class.java)

        diary_request.requestDiary().enqueue(object : Callback<ArrayList<DiaryRequest>> {
            override fun onResponse(
                call: Call<ArrayList<DiaryRequest>>,
                response: Response<ArrayList<DiaryRequest>>
            ) {
                diary = response.body()!!
                diary_list.adapter = diaryRecyclerview

                diaryRecyclerview.diaryList = diary
                diaryRecyclerview.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<ArrayList<DiaryRequest>>, t: Throwable) {
                Log.e("실패", t.localizedMessage)
            }

        })
    }

    override fun onResume() {
        super.onResume()
        activity?.invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.date_pick -> {
                Toast.makeText(activity, "클릭", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.home -> {

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}

