package com.example.setapakhouse

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_forget_password.*
import java.util.concurrent.TimeUnit


class ForgetPasswordActivity : AppCompatActivity() {

    lateinit var callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var ref : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setTitle("Forgot Password")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val timer = object: CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                lowerCaseText.text = "OTP expired in " + (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60).toString() + "seconds..."
            }

            override fun onFinish() {
                lowerCaseText.text = "OTP is already expired!!"

                val builder = AlertDialog.Builder(this@ForgetPasswordActivity)
                builder.setTitle("OTP is already expired!!")
                builder.setMessage("Click Okay to try again.")

                builder.setNeutralButton("Okay", { dialog: DialogInterface?, which: Int ->
                    val intent = Intent(this@ForgetPasswordActivity, VerifyPhoneNumberActivity::class.java)
                    startActivity(intent)
                })
                builder.setCancelable(false)
                builder.show()

            }
        }
        timer.start()

        testButton.setOnClickListener {
           updateUsername()
        }
    }


    private fun updateUsername(){
        //get userID
        val builder = AlertDialog.Builder(this)
        with(builder) {
            setTitle("Icon and Button Color")
            setMessage("We have a message")
            setPositiveButton("OK", null)
            setIcon(resources.getDrawable(android.R.drawable.ic_dialog_alert, theme))
        }
        val alertDialog = builder.create()
        alertDialog.show()



        /*ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {
                    for (h in snapshot.children) {
                        //Log.d("Errorhello", "zzzz : " + h.key.toString())
                        percentage.text = h.child("humid").getValue().toString() + " %"
                        ref1.child("lcdtext").setValue("Window Is Closed");
                    }
                }
            }
        })*/

    }


    private fun testing(timer: CountDownTimer) {

        timer.cancel()
        val intent = Intent(this, SignUpActivity::class.java)
        intent.putExtra("PhoneNumber2", intent.getStringExtra("PhoneNumber"))
        startActivity(intent)
        finish()

    }

    private fun createAccount(email : String, password:String) {

            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("SignUp")
            progressDialog.setMessage("Please wait, this may take a while...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()
            val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

            mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener{task ->
                    if(task.isSuccessful){
                        saveUserInfo("cshong199",email, password,progressDialog)
                    }
                    else
                    {
                        val message = task.exception!!.toString()
                        Toast.makeText(this,"Error: $message", Toast.LENGTH_LONG).show()
                        mAuth.signOut()
                        progressDialog.dismiss()

                    }
                }

    }


    private fun saveUserInfo(username: String, email: String, password: String, progressDialog: ProgressDialog) {
        val currentUserID= FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap=HashMap<String,Any>()
        userMap["userID"]=currentUserID
        userMap["username"]=username
        userMap["fullName"]="Chong Soon Hong"
        userMap["email"]=email
        userMap["phoneNumber"]="+601133400142"
        userMap["rewardPoint"]=0
        userMap["balance"]=0
        userMap["image"]="https://firebasestorage.googleapis.com/v0/b/setapak-house.appspot.com/o/Default_Profile_Picture%2FdefaultProfilePicture.jpg?alt=media&token=e405bcb3-6a4a-4e89-aec8-7773f379674d"

        usersRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener{task ->
                if(task.isSuccessful){
                    progressDialog.dismiss()
                    Toast.makeText(this,"Account has been created successfully.", Toast.LENGTH_LONG)
                    val user= FirebaseAuth.getInstance().currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener{task ->
                            if(task.isSuccessful){
                                FirebaseAuth.getInstance().signOut()

                                val intent = Intent(this,LoginActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                Toast.makeText(this,"Account created",Toast.LENGTH_SHORT).show()
                                finish()

                            }
                        }
                }
                else
                {
                    val message = task.exception!!.toString()
                    Toast.makeText(this,"Error: $message", Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }
            }
    }





}