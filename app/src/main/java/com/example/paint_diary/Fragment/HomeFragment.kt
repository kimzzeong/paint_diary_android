package com.example.paint_diary.Fragment

import LoadingDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.paint_diary.Activity.DiaryInfoActivity
import com.example.paint_diary.Activity.PaintActivity
import com.example.paint_diary.Adapter.DiaryRecyclerviewAdapter
import com.example.paint_diary.Data.DiaryRequest
import com.example.paint_diary.IRetrofit
import com.example.paint_diary.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.dialog_loading.*
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {
    lateinit var diaryRecyclerview: DiaryRecyclerviewAdapter
    var listing_num : Int = 0

    var gson: Gson = GsonBuilder()
            .setLenient()
            .create()

    var retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

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

        listing_spinner.adapter = activity?.let {
            ArrayAdapter.createFromResource(
                    it,
                R.array.listSpinner,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            listing_spinner.adapter = adapter
        }
        }

        listing_spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                listing_num = position
                requestDiary()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {} // 스피너 쓰기위한 어뎁터 메소드 선언부
        })



        write_btn.setOnClickListener{
            //프래그먼트 -> 액티비티 화면 이동
            val intent = Intent(context, PaintActivity::class.java)
            startActivity(intent)

        }

        diaryRecyclerview = DiaryRecyclerviewAdapter()

        val layoutManager = GridLayoutManager(activity, 2)

        diary_list.layoutManager = layoutManager

        diaryRecyclerview.setItemClickListener(object : DiaryRecyclerviewAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, diary_idx :Int, diary_writer : Int) {
                val intent = Intent(context, DiaryInfoActivity::class.java)
                intent.putExtra("diary_idx",diary_idx)
                intent.putExtra("diary_writer",diary_writer)
                Log.e("click",diary_idx.toString())
                startActivity(intent)
            }
        })

        diary_refresh.setOnRefreshListener {
            requestDiary()
            diary_refresh.isRefreshing = false
        }

    }


    //다이어리 전체 리스트 불러오기
    private fun requestDiary() {
//        val sharedPreferences = activity?.getSharedPreferences("user", Context.MODE_PRIVATE)
//        var user_idx : String? = sharedPreferences?.getString("user_idx", "")

        loaderLayout.visibility = View.VISIBLE
        var diary_request = retrofit.create(IRetrofit::class.java)

        diary_request.requestDiary().enqueue(object : Callback<ArrayList<DiaryRequest>> {
            override fun onResponse(call: Call<ArrayList<DiaryRequest>>, response: Response<ArrayList<DiaryRequest>>
            ) {

                var diary = response.body()!!

                diary_list.adapter = diaryRecyclerview
                if(listing_num == 1){
                    diary.sortByDescending { it.diary_date }
                }else if(listing_num == 2){
                    diary.sortByDescending { it.diary_like_count }
                }else if(listing_num == 3){
                    diary.sortByDescending { it.diary_comment_count }
                }
                diaryRecyclerview.diaryList = diary
                diaryRecyclerview.notifyDataSetChanged()

            }

            override fun onFailure(call: Call<ArrayList<DiaryRequest>>, t: Throwable) {
                Log.e("실패", t.localizedMessage)
            }

        })
        loaderLayout.visibility = View.INVISIBLE
    }

    override fun onResume() {
        super.onResume()
        activity?.invalidateOptionsMenu()
        requestDiary()
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

//    private fun likeProcess() {
//
//        var request_like = retrofit.create(IRetrofit::class.java)
//
//        request_like.requestLikeInfo(diary_idx!!,user_idx!!).enqueue(object : Callback<like_data>{
//            override fun onResponse(call: Call<like_data>, response: Response<like_data>) {
//                val like = response.body()
//                if(like != null){
//                    diary_like = like.like_status
//                    diary_like_count = like.like_count
//                    diary_favorite_text.text = diary_like_count.toString()
//                    diary_comment_text.text = like.comments_count.toString()
//
//                    diary_like_setting()
//
//                }
//            }
//
//            override fun onFailure(call: Call<like_data>, t: Throwable) {
//                Toast.makeText(activity,"좋아요 불러오기 실패",Toast.LENGTH_SHORT).show()
//            }
//
//        })
//    }


}

