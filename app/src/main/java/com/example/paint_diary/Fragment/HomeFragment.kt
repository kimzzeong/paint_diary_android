package com.example.paint_diary.Fragment

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
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
import kotlinx.android.synthetic.main.activity_diary.*
import kotlinx.android.synthetic.main.dialog_loading.*
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {
    lateinit var diaryRecyclerview: DiaryRecyclerviewAdapter
    var listing_num : Int = 0

    var datePicker : DatePickerDialog? = null
    var formatDate = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)

    var gson: Gson = GsonBuilder()
            .setLenient()
            .create()

    var retrofit = Retrofit.Builder()
            .baseUrl("http://ec2-3-36-52-195.ap-northeast-2.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    var date : String? = null
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
        val activity = activity as AppCompatActivity?

        activity?.setSupportActionBar(home_toolbar)
        activity?.supportActionBar?.title = "홈"

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

        //캘린더 아이콘 클릭 시 날짜별로 일기 보기 가능
        home_calendar.setOnClickListener {
            Log.e("datePicker","1")
            val getDate = Calendar.getInstance()
            Log.e("datePicker","2")

            var date_split : Array<String>? = null
            datePicker = DatePickerDialog(activity!!, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->


                Log.e("datePicker","3")
                val selectDate: Calendar = Calendar.getInstance()
                selectDate.set(Calendar.YEAR, year)
                selectDate.set(Calendar.MONTH, month)
                selectDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                date = formatDate.format(selectDate.time)
                Log.e("date",date!!)
               // retrofit_date = formatDateSave.format(selectDate.time)

                //Toast.makeText(activity,date,Toast.LENGTH_SHORT).show()
                requestDiaryDate(date!!)
                home_listing(listing_num)
                Log.e("datePicker","4")
                content_view.visibility = View.VISIBLE
              //  if(!date.equals("")){
              //  }


//                Log.e("size",date_split.size.toString())
//                Log.e("date_split",date_split[0])

            }, getDate.get(Calendar.YEAR), getDate.get(Calendar.MONTH), getDate.get(Calendar.DAY_OF_MONTH))
                .apply {

                    Log.e("datePicker","5")
                    datePicker.maxDate = System.currentTimeMillis()
                }

            content_view.setOnClickListener {
                content_view.visibility = View.INVISIBLE
                requestDiary()
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
                date = null
            }


            datePicker!!.show()

            Log.e("datePicker","6")
            if(date != null){

                date_split = date?.split("-")?.toTypedArray()
                Log.e("datePicker","7")
                Log.e("date_split",date_split!![0])
                Log.e("date_split",date_split!![1])
                Log.e("date_split",date_split!![2])
                datePicker?.updateDate(Integer.parseInt(date_split!![0]),Integer.parseInt(date_split!![1])- 1,Integer.parseInt(date_split!![2]))
            }else{

            }
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
        }

        listing_spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                listing_num = position
                home_listing(listing_num)

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
            override fun onClick(view: View, position: Int, diary_idx: Int, diary_writer: Int) {
                val intent = Intent(context, DiaryInfoActivity::class.java)
                intent.putExtra("diary_idx", diary_idx)
                intent.putExtra("diary_writer", diary_writer)
                Log.e("click", diary_idx.toString())
                startActivity(intent)
            }
        })

        diary_refresh.setOnRefreshListener {
            requestDiary()
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
            diary_refresh.isRefreshing = false

        }

    }

    private fun requestDiaryDate(date : String) {
        loaderLayout.visibility = View.VISIBLE
        var diary_request = retrofit.create(IRetrofit::class.java)

        diary_request.requestDiaryDate(date).enqueue(object : Callback<ArrayList<DiaryRequest>> {
            override fun onResponse(
                call: Call<ArrayList<DiaryRequest>>, response: Response<ArrayList<DiaryRequest>>
            ) {

                var diary = response.body()!!

                diary_list.adapter = diaryRecyclerview
                diaryRecyclerview.diaryList = diary
                diaryRecyclerview.notifyDataSetChanged()
                if(diaryRecyclerview.diaryList?.size == 0){
                    home_info_text.visibility = View.VISIBLE
                    diary_list.visibility = View.INVISIBLE
                }else{
                    home_info_text.visibility = View.INVISIBLE
                    diary_list.visibility = View.VISIBLE
                }

            }

            override fun onFailure(call: Call<ArrayList<DiaryRequest>>, t: Throwable) {
                Log.e("실패", t.localizedMessage)
            }

        })
        loaderLayout.visibility = View.INVISIBLE
    }


    //다이어리 전체 리스트 불러오기
    private fun requestDiary() {
//        val sharedPreferences = activity?.getSharedPreferences("user", Context.MODE_PRIVATE)
//        var user_idx : String? = sharedPreferences?.getString("user_idx", "")

        loaderLayout.visibility = View.VISIBLE
        var diary_request = retrofit.create(IRetrofit::class.java)

        diary_request.requestDiary().enqueue(object : Callback<ArrayList<DiaryRequest>> {
            override fun onResponse(
                call: Call<ArrayList<DiaryRequest>>, response: Response<ArrayList<DiaryRequest>>
            ) {

                var diary = response.body()!!

                diary_list.adapter = diaryRecyclerview
                diaryRecyclerview.diaryList = diary
                diaryRecyclerview.notifyDataSetChanged()
                home_listing(listing_num)

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


    fun home_listing(listing_num : Int){
        if(date != null){
            if (listing_num == 0) {
                diaryRecyclerview.diaryList?.sortByDescending { it.diary_idx }
                // diaryRecyclerview.diaryList?.sortByDescending { it.diary_idx }
            } else if (listing_num == 1) {
                diaryRecyclerview.diaryList?.sortBy { it.diary_idx  }
                // diaryRecyclerview.diaryList?.sortBy { it.diary_idx  }
            } else if (listing_num == 2) {
                diaryRecyclerview.diaryList?.sortByDescending { it.diary_like_count }
            } else if (listing_num == 3){
                diaryRecyclerview.diaryList?.sortByDescending { it.diary_comment_count }
            }
        }else{
            if (listing_num == 0) {
                diaryRecyclerview.diaryList?.sortByDescending { it.diary_date }
                // diaryRecyclerview.diaryList?.sortByDescending { it.diary_idx }
            } else if (listing_num == 1) {
                diaryRecyclerview.diaryList?.sortBy { it.diary_date  }
                // diaryRecyclerview.diaryList?.sortBy { it.diary_idx  }
            } else if (listing_num == 2) {
                diaryRecyclerview.diaryList?.sortByDescending { it.diary_like_count }
            } else if (listing_num == 3){
                diaryRecyclerview.diaryList?.sortByDescending { it.diary_comment_count }
            }
        }

        diaryRecyclerview.notifyDataSetChanged()
    }

}

