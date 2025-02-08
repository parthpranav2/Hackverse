package com.example.hackverse

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Home : AppCompatActivity() {

    private lateinit var viewPager : ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frmdmhome)

        viewPager=findViewById(R.id.viewpager)
        viewPager.adapter = HomePagerAdapter(this)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<TextView>(R.id.lblname).text = GlobalClass.NameIco



        findViewById<LinearLayout>(R.id.tabBookmark).setOnClickListener{
            findViewById<ImageView>(R.id.imgBookmark).setImageResource(R.drawable.bookmark_a)
            findViewById<TextView>(R.id.txtBookmark).setTextColor(Color.parseColor("#5E7BF0"))
            findViewById<TextView>(R.id.txtBookmarkCount).setTextColor(Color.parseColor("#5E7BF0"))
            findViewById<View>(R.id.lvwBookmark).visibility=View.VISIBLE

            findViewById<ImageView>(R.id.imgActiveTeams).setImageResource(R.drawable.activeteam_na)
            findViewById<TextView>(R.id.txtActiveTeams).setTextColor(Color.parseColor("#969DA4"))
            findViewById<View>(R.id.lvwActiveTeams).visibility=View.INVISIBLE

            viewPager.currentItem=0

        }

        findViewById<LinearLayout>(R.id.tabActiveTeams).setOnClickListener{
            findViewById<ImageView>(R.id.imgActiveTeams).setImageResource(R.drawable.activeteam_a)
            findViewById<TextView>(R.id.txtActiveTeams).setTextColor(Color.parseColor("#5E7BF0"))
            findViewById<View>(R.id.lvwActiveTeams).visibility=View.VISIBLE

            findViewById<ImageView>(R.id.imgBookmark).setImageResource(R.drawable.bookmark_na)
            findViewById<TextView>(R.id.txtBookmark).setTextColor(Color.parseColor("#969DA4"))
            findViewById<TextView>(R.id.txtBookmarkCount).setTextColor(Color.parseColor("#969DA4"))
            findViewById<View>(R.id.lvwBookmark).visibility=View.INVISIBLE

            viewPager.currentItem=1
        }

        val viewpager = findViewById<ViewPager2>(R.id.viewpager)
        viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when(position){
                    0->{findViewById<ImageView>(R.id.imgBookmark).setImageResource(R.drawable.bookmark_a)
                        findViewById<TextView>(R.id.txtBookmark).setTextColor(Color.parseColor("#5E7BF0"))
                        findViewById<TextView>(R.id.txtBookmarkCount).setTextColor(Color.parseColor("#5E7BF0"))
                        findViewById<View>(R.id.lvwBookmark).visibility=View.VISIBLE

                        findViewById<ImageView>(R.id.imgActiveTeams).setImageResource(R.drawable.activeteam_na)
                        findViewById<TextView>(R.id.txtActiveTeams).setTextColor(Color.parseColor("#969DA4"))
                        findViewById<View>(R.id.lvwActiveTeams).visibility=View.INVISIBLE
                    }
                    1->{
                        findViewById<ImageView>(R.id.imgActiveTeams).setImageResource(R.drawable.activeteam_a)
                        findViewById<TextView>(R.id.txtActiveTeams).setTextColor(Color.parseColor("#5E7BF0"))
                        findViewById<View>(R.id.lvwActiveTeams).visibility=View.VISIBLE

                        findViewById<ImageView>(R.id.imgBookmark).setImageResource(R.drawable.bookmark_na)
                        findViewById<TextView>(R.id.txtBookmark).setTextColor(Color.parseColor("#969DA4"))
                        findViewById<TextView>(R.id.txtBookmarkCount).setTextColor(Color.parseColor("#969DA4"))
                        findViewById<View>(R.id.lvwBookmark).visibility=View.INVISIBLE
                    }
                }
            }
        })

        val sanitizedEmail = GlobalClass.Email?.replace(".", ",") ?: ""
        val userRef = FirebaseDatabase.getInstance()
            .getReference("User")
            .child(sanitizedEmail)
            .child("BookmarkedEvents")

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val childCount = snapshot.childrenCount
                findViewById<TextView>(R.id.txtBookmarkCount).text= childCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


        findViewById<LinearLayout>(R.id.btnactiveevents).setOnClickListener {
            // Navigate to activity_frmdpin
            val intent = Intent(this, ActiveEvents::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        findViewById<LinearLayout>(R.id.btnportfolio).setOnClickListener {
            // Navigate to activity_frmdpin
            val intent = Intent(this, Portfolio::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        findViewById<LinearLayout>(R.id.btnchatroom).setOnClickListener {
            // Navigate to activity_frmdpin
            val intent = Intent(this, ChatRoomEventList::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        findViewById<LinearLayout>(R.id.btnaddhackathon).setOnClickListener {
            // Navigate to activity_frmdpin
            val intent = Intent(this, AddHackathon::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        findViewById<RelativeLayout>(R.id.btnusernico).setOnClickListener {
            // Navigate to activity_frmdpin
            val intent = Intent(this, Account::class.java)
            startActivity(intent)
        }


    }

}