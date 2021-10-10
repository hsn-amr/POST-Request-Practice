package com.example.postrequestpractice

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    lateinit var addNewUserButton: Button
    lateinit var rvMain: RecyclerView
    var userList = arrayListOf<User>()

    lateinit var progress: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        addNewUserButton = findViewById(R.id.btnAddNewUser)
        rvMain = findViewById(R.id.rvMain)

        val adaptor = RVUser(userList)
        rvMain.adapter = adaptor
        rvMain.layoutManager = LinearLayoutManager(this)

        val apiInterface = APIClient().getClient()?.create(APIInterface::class.java)
        val call: Call<List<UserInfo>?>? = apiInterface!!.getUsers()

        showProgressDialog()
        call?.enqueue(object : Callback<List<UserInfo>?>{

            override fun onResponse(
                call: Call<List<UserInfo>?>,
                response: Response<List<UserInfo>?>
            ) {
                val response: List<UserInfo>? = response.body()
                for(user in response!!){
                    userList.add(User(user.name.toString(), user.location.toString()))
                }
                rvMain.adapter!!.notifyDataSetChanged()
                removeProgressDialog()
            }

            override fun onFailure(call: Call<List<UserInfo>?>, t: Throwable) {
                Log.d("TAG", "${t}")
                Toast.makeText(this@MainActivity, "Something went wrong $t", Toast.LENGTH_LONG).show()
                removeProgressDialog()
            }
        })


        addNewUserButton.setOnClickListener {
            val intent = Intent(this, AddNewUser::class.java)
            startActivity(intent)
        }
    }

    private fun showProgressDialog(){
        progress = ProgressDialog(this@MainActivity)
        progress.setTitle("Loading")
        progress.setMessage("Wait while loading...")
        progress.show()
    }
    private fun removeProgressDialog(){
        progress.dismiss()
    }
}