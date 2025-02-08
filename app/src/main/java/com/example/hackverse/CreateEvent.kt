package com.example.hackverse

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreateEvent : AppCompatActivity() {
    lateinit var btnssd : LinearLayout
    lateinit var btnsst : LinearLayout
    private val calendar=Calendar.getInstance()
    lateinit var btnsed : LinearLayout
    lateinit var btnset : LinearLayout
    var a = 1
    var teamsize = 1
    lateinit var btnadd:ImageView
    lateinit var btnsubs:ImageView
    lateinit var btnpublish : Button



    private lateinit var iname: EditText
    var itheme: String = ""
    var itargetaudiance: String = ""
    private lateinit var iteamsize: TextView
    private lateinit var issd:TextView
    private lateinit var isst: TextView
    private lateinit var ised:TextView
    private lateinit var iset: TextView
    private lateinit var idescription: EditText
    var imode: String = ""
    private lateinit var ivenue: EditText
    private lateinit var iPosterURL: EditText


    private lateinit var dbRef1: DatabaseReference
    private lateinit var dbRef2: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_frmdcreateevent)

        //Theme cmb box
        val ThemeItems = arrayOf("Select Option","Social Impact","Ai and Machine Learning","Sustainability & Climate Change","HealthTech","FinTech","Smart Cities & IoT","Cybersecurity","Gaming & Entertainment","Education & EdTech","E-commerce & Retail","Blockchain & Decentralized Systems","Diversity, Equity & Inclusion","Productivity & Workflow Automation","Data Science & Analytics","Mechathon","Other")
        val arrayThemeAdapter =ArrayAdapter(this,android.R.layout.select_dialog_item,ThemeItems)
        val spntheme = findViewById<Spinner>(R.id.spntheme)
        spntheme.adapter = arrayThemeAdapter

        spntheme.onItemSelectedListener=object :
        AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                // Reference to the ImageView
                val imageView: ImageView = findViewById(R.id.imgtheme)
                val imageResource = when (p2) {
                    1 -> R.drawable.socioimpact
                    2 -> R.drawable.aiml
                    3 -> R.drawable.sustainability
                    4 -> R.drawable.healthtech
                    5 -> R.drawable.fintech
                    6 -> R.drawable.smartcity
                    7 -> R.drawable.cybersec
                    8 -> R.drawable.gaming
                    9 -> R.drawable.edtech
                    10 -> R.drawable.retail
                    11 -> R.drawable.blockchain
                    12 -> R.drawable.diversity
                    13 -> R.drawable.workflowautomation
                    14 -> R.drawable.datasci
                    15 -> R.drawable.mechathon
                    16 -> R.drawable.others
                    else -> R.drawable.resource_void
                }
                imageView.setImageResource(imageResource)

                if(p2==16){
                    itheme=""
                    findViewById<EditText>(R.id.txttheme).visibility=View.VISIBLE
                }else{
                    findViewById<EditText>(R.id.txttheme).visibility=View.GONE
                    itheme=ThemeItems[p2]
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                findViewById<ImageView>(R.id.imgtheme).setImageResource(R.drawable.resource_void)
            }
        }

        //TargetAudiance cmb box
        val TargetAudianceItems = arrayOf("Select Option","Developers & Engineers","Designer (UX/UI)","Entrepreneurs & Startups","Students","Researchers & Academics","Business & Product Managers","Nonprofit & Social Impact Organisations","Investors & Mentors","Corporations & Industry Professionals","Gamers & Creators","General Tech Enthusiasts","Educators & Trainers","Legal & Compliance Experts","Other")
        val arrayTargetAudianceAdapter =ArrayAdapter(this,android.R.layout.select_dialog_item,TargetAudianceItems)
        val spnTargetAudiance = findViewById<Spinner>(R.id.spntargetaudiance)
        spnTargetAudiance.adapter = arrayTargetAudianceAdapter
        spnTargetAudiance.onItemSelectedListener=object :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(p2==14){
                    findViewById<EditText>(R.id.txttargetaudiance).visibility=View.VISIBLE
                }else{
                    findViewById<EditText>(R.id.txttargetaudiance).visibility=View.GONE
                    itargetaudiance=TargetAudianceItems[p2]
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
        //Mode cmb box
        val ModeItems = arrayOf("Online","Offline","Synergy")
        val arrayMode =ArrayAdapter(this,android.R.layout.select_dialog_item,ModeItems)
        val spnMode = findViewById<Spinner>(R.id.spnmode)
        spnMode.adapter = arrayMode
        spnMode.onItemSelectedListener=object :
        AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
               imode=ModeItems[p2]
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnssd=findViewById<LinearLayout>(R.id.butssdate)
        issd=findViewById<TextView>(R.id.txtssdate)
        btnsed=findViewById<LinearLayout>(R.id.butsedate)
        ised=findViewById<TextView>(R.id.txtsedate)
        btnsst = findViewById<LinearLayout>(R.id.butsstime)
        isst = findViewById<TextView>(R.id.txtsstime)
        btnset = findViewById<LinearLayout>(R.id.butsetime)
        iset = findViewById<TextView>(R.id.txtsetime)
        iteamsize=findViewById<TextView>(R.id.txtteamsize)
        iteamsize.text=teamsize.toString()
        btnadd=findViewById<ImageView>(R.id.btnadd)
        btnsubs=findViewById<ImageView>(R.id.btnsubs)
        btnpublish=findViewById<Button>(R.id.btnpublish)
        idescription=findViewById<EditText>(R.id.txtdescription)
        iname=findViewById<EditText>(R.id.txtname1)
        ivenue=findViewById<EditText>(R.id.txtvenue)

        iPosterURL=findViewById<EditText>(R.id.txtposterurl)


        dbRef1= FirebaseDatabase.getInstance().getReference("Events")
        val sanitizedEmail = GlobalClass.Email?.replace(".", ",") ?: ""
        dbRef2= FirebaseDatabase.getInstance().getReference("User").child(sanitizedEmail).child("EventsCreated")

        btnadd.setOnClickListener{
            teamsize++
            iteamsize.text=teamsize.toString()
        }
        btnsubs.setOnClickListener{
            if(teamsize>1){
                teamsize--
            }else{
                Toast.makeText(this@CreateEvent, "Team size cannot be less than 1", Toast.LENGTH_LONG).show()
            }
            iteamsize.text=teamsize.toString()
        }

        btnssd.setOnClickListener{
            a=1
            showDatePicker()
        }
        btnsed.setOnClickListener{
            a=2
            showDatePicker()
        }

        btnsst.setOnClickListener{
            if(issd.text=="startdate"){
                Toast.makeText(this, "Please select the start date first", Toast.LENGTH_SHORT).show()
                issd.setTextColor(Color.parseColor("#FF5E51"))
            }else{
                a=1
                showTimePicker()
            }
        }

        btnset.setOnClickListener{
            if(ised.text=="enddate"){
                Toast.makeText(this, "Please select the end date first", Toast.LENGTH_SHORT).show()
                ised.setTextColor(Color.parseColor("#FF5E51"))
            }else{
                a=2
                showTimePicker()
            }
        }

        findViewById<ImageView>(R.id.btnback).setOnClickListener {
            finish()
        }


        findViewById<Button>(R.id.btnpreview).setOnClickListener {
            val posterUrlEditText = findViewById<EditText>(R.id.txtposterurl)
            val posterImageView = findViewById<ImageView>(R.id.imgposter)
            val posterUrl = posterUrlEditText.text.toString().trim() // Trim spaces

            if (posterUrl.isBlank()) {
                posterImageView.visibility = View.GONE
                Toast.makeText(this, "No source image URL provided", Toast.LENGTH_LONG).show()
            } else {
                posterImageView.visibility = View.VISIBLE

                Glide.with(this)
                    .load(posterUrl)
                    .fitCenter()
                    .placeholder(R.drawable.poster) // Placeholder while loading
                    .error(R.drawable.unlink) // Error image if URL fails
                    .into(posterImageView)
            }
        }





        btnpublish.setOnClickListener{
                if(itheme.isNullOrEmpty()){
                    itheme=findViewById<EditText>(R.id.txttheme).text.toString()
                }
                if(itargetaudiance.isNullOrEmpty()){
                    itargetaudiance=findViewById<EditText>(R.id.txttargetaudiance).text.toString()
                }
            if(allEntriesCheck(this)){
                saveEventData()

                finish()
            }
        }

    }

    private fun saveEventData(){
        val evName = iname.text.toString()
        val evTheme = itheme
        val evTargetAudiance = itargetaudiance
        val evTeamSize = iteamsize.text.toString()
        val evssd = issd.text.toString()
        val evsst = isst.text.toString()
        val evsed = ised.text.toString()
        val evset = iset.text.toString()
        val evDescription = idescription.text.toString()
        val evMode = imode
        val evVenue = ivenue.text.toString()
        val evPosterURL = iPosterURL.text.toString()

        val evId = dbRef1.push().key!!
        val event = EventModel(evName,evTargetAudiance,evMode,evssd,evsed,evTheme,evId,evTeamSize,evsst,evset,evDescription,evVenue,evPosterURL)


        dbRef1.child(evId).setValue(event)
            .addOnCompleteListener{

            }.addOnFailureListener{

            }
        dbRef2.push().setValue(evId)


    }

    fun allEntriesCheck(context: Context): Boolean {
        if(iname.text.isNullOrEmpty()){
            iname.background= ContextCompat.getDrawable(this, R.drawable.pinrejection)
            Toast.makeText(this@CreateEvent, "EventModel name cannot be empty", Toast.LENGTH_LONG).show()
            return false
        }
        if(itheme=="Select Option"){
            findViewById<Spinner>(R.id.spntheme).background= ContextCompat.getDrawable(this, R.drawable.btnred)
            Toast.makeText(this@CreateEvent, "EventModel theme should have some valid selection", Toast.LENGTH_LONG).show()
            return false
        }
        if(itheme.isNullOrEmpty()){
            findViewById<EditText>(R.id.txttheme).background= ContextCompat.getDrawable(this, R.drawable.pinrejection)
            Toast.makeText(this@CreateEvent, "EventModel theme field cannot be empty", Toast.LENGTH_LONG).show()
            return false
        }
        if(itargetaudiance=="Select Option"){
            findViewById<Spinner>(R.id.spntargetaudiance).background= ContextCompat.getDrawable(this, R.drawable.btnred)
            Toast.makeText(this@CreateEvent, "Target audiance should have some valid selection", Toast.LENGTH_LONG).show()
            return false
        }
        if(itargetaudiance.isNullOrEmpty()) {
            findViewById<EditText>(R.id.txttargetaudiance).background= ContextCompat.getDrawable(this, R.drawable.pinrejection)
            Toast.makeText(this@CreateEvent, "Target audiance field cannot be empty", Toast.LENGTH_LONG).show()
            return false
        }
        if(issd.text.toString()=="startdate"){
            issd.setTextColor(Color.parseColor("#FF5E51"))
            Toast.makeText(this@CreateEvent, "Start date cannot be empty", Toast.LENGTH_LONG).show()
            return false
        }
        if(isst.text.toString()=="starttime"){
            isst.setTextColor(Color.parseColor("#FF5E51"))
            Toast.makeText(this@CreateEvent, "Start time cannot be empty", Toast.LENGTH_LONG).show()
            return false
        }
        if(ised.text.toString()=="enddate"){
            ised.setTextColor(Color.parseColor("#FF5E51"))
            Toast.makeText(this@CreateEvent, "End date cannot be empty", Toast.LENGTH_LONG).show()
            return false
        }
        if(iset.text.toString()=="endtime"){
            iset.setTextColor(Color.parseColor("#FF5E51"))
            Toast.makeText(this@CreateEvent, "End time cannot be empty", Toast.LENGTH_LONG).show()
            return false
        }
        if(ivenue.text.isNullOrEmpty()){
            ivenue.background= ContextCompat.getDrawable(this, R.drawable.pinrejection)
            Toast.makeText(this@CreateEvent, "EventModel venue cannot be empty", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun showTimePicker(){
        val cal = Calendar.getInstance()
        val timeSetListner = TimePickerDialog.OnTimeSetListener{timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            // Define the date format
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            if(a==1){
                if(issd.text==ised.text){
                    if(iset.text=="endtime"){
                        isst.setTextColor(Color.parseColor("#FFFFFF"))
                        isst.text=timeFormat.format(cal.time)
                    }else{
                        val selectedTime = timeFormat.parse(timeFormat.format(cal.time))
                        val existingTime = timeFormat.parse(iset.text.toString())
                        // Compare the times
                        if (selectedTime != null && existingTime != null && selectedTime.before(existingTime)) {
                            isst.setTextColor(Color.parseColor("#FFFFFF"))
                            isst.text = timeFormat.format(cal.time)
                        }else{
                            Toast.makeText(this@CreateEvent, "Invalid Time: start time cannot be after end time on the same day", Toast.LENGTH_SHORT).show()
                            isst.text="starttime"
                            isst.setTextColor(Color.parseColor("#FF5E51"))
                        }
                    }
                }else{
                    isst.setTextColor(Color.parseColor("#FFFFFF"))
                    isst.text=timeFormat.format(cal.time)
                }
            }else{
                if(issd.text==ised.text){
                    if(isst.text=="starttime"){
                        iset.setTextColor(Color.parseColor("#FFFFFF"))
                        iset.text = timeFormat.format(cal.time)
                    }else{
                        val selectedTime = timeFormat.parse(timeFormat.format(cal.time))
                        val existingTime = timeFormat.parse(isst.text.toString())
                        // Compare the times
                        if (selectedTime != null && existingTime != null && existingTime.before(selectedTime)) {
                            iset.setTextColor(Color.parseColor("#FFFFFF"))
                            iset.text = timeFormat.format(cal.time)
                        }else{
                            Toast.makeText(this@CreateEvent, "Invalid Time: end time cannot be before start time on the same day", Toast.LENGTH_SHORT).show()
                            iset.text="endtime"
                            iset.setTextColor(Color.parseColor("#FF5E51"))
                        }
                    }
                }else {
                    iset.setTextColor(Color.parseColor("#FFFFFF"))
                    iset.text = timeFormat.format(cal.time)
                }
            }
        }
        TimePickerDialog(this, timeSetListner,cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),true).show()
    }

    private fun showDatePicker(){
         val datePickerDialog = DatePickerDialog(this,{DatePicker, year: Int,monthOfYear: Int,dayOfMonth: Int ->
             val selectedDate = Calendar.getInstance()
             selectedDate.set(year,monthOfYear,dayOfMonth)
             val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
             val formattedDate = dateFormat.format(selectedDate.time)
             if(a==1){
                 // Check if 'ised' text is empty or if 'ised' date >= selected formatted date
                 if (ised.text=="enddate" || compareDates(ised.text.toString(), formattedDate)) {
                     issd.setTextColor(Color.parseColor("#FFFFFF"))
                     issd.text=formattedDate
                 }else{
                     issd.setTextColor(Color.parseColor("#FF5E51"))
                     issd.text="startdate"
                     Toast.makeText(this@CreateEvent, "Invalid date : Start date cannot be after end date", Toast.LENGTH_LONG).show()
                 }
             }else{
                 if(issd.text=="startdate" || compareDates(formattedDate, issd.text.toString())) {
                     ised.setTextColor(Color.parseColor("#FFFFFF"))
                     ised.text = formattedDate
                 }else{
                     ised.setTextColor(Color.parseColor("#FF5E51"))
                     ised.text="enddate"
                     Toast.makeText(this@CreateEvent, "Invalid date : End date cannot be before start date", Toast.LENGTH_LONG).show()
                 }
             }
         },
             calendar. get (Calendar. YEAR),
             calendar. get (Calendar .MONTH),
             calendar.get(Calendar.DAY_OF_MONTH)
             )
        datePickerDialog.show()
    }

    fun compareDates(date1: String, date2: String): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        try {
            val parsedDate1 = dateFormat.parse(date1)
            val parsedDate2 = dateFormat.parse(date2)
            return parsedDate1?.compareTo(parsedDate2) ?: 0 >= 0
        } catch (e: ParseException) {
            e.printStackTrace()
            return false
        }
    }

}