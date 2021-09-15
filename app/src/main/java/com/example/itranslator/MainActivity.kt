package com.example.itranslator

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var translatedTV : TextView
    private lateinit var fromSpinner: Spinner
    private lateinit var toSpinner: Spinner
    private lateinit var sourceEdt : TextInputEditText
    private lateinit var micIV : ImageView
    private lateinit var translateBtn : MaterialButton


    var fromLanguage = arrayOf("From", "English", "Afrikaans", "Arabic", "Belarusian", "Bulgarian", "Bengali", "Catalan", "Czech", "Welsh", "Hindi", "Urdu")
    var toLanguage = arrayOf("To", "English", "Afrikaans", "Arabic", "Belarusian", "Bulgarian", "Bengali", "Catalan", "Czech", "Welsh", "Hindi", "Urdu")
    private var REQUEST_PERMISSION_CODE = 1
    var languageCode= 0
    var fromLanguageCode = 0
    var toLanguageCode = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        translatedTV = findViewById(R.id.idTVTranslatedTV)
        fromSpinner = findViewById(R.id.idFromSpinner)
        toSpinner = findViewById(R.id.idToSpinner)
        sourceEdt = findViewById(R.id.idEdtSource)
        micIV = findViewById(R.id.idIVMic)
        translateBtn = findViewById(R.id.idBtnTranslate)

        fromSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    fromLanguageCode = getLanguageCode(fromLanguage[position])
                }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        val fromAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(this, R.layout.spinner_item, fromLanguage)
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fromSpinner!!.adapter = fromAdapter

        toSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                toLanguageCode = getLanguageCode(toLanguage[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        val toAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(this, R.layout.spinner_item, fromLanguage)
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        toSpinner.adapter = toAdapter

        translateBtn.setOnClickListener(View.OnClickListener {
            translatedTV.text = ""
            if(sourceEdt.text.toString().isEmpty()){
                Toast.makeText(this,"Please enter the text to translate",Toast.LENGTH_SHORT)
            }else if (fromLanguageCode==0){
                Toast.makeText(this,"Please select source language",Toast.LENGTH_SHORT)
            }else if (toLanguageCode==0){
                Toast.makeText(this,"Please select the language to make translation",Toast.LENGTH_SHORT)
            }else{
                translateText(fromLanguageCode, toLanguageCode, sourceEdt!!.text.toString())
            }
        })

        micIV.setOnClickListener(View.OnClickListener {
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak to convert into text")
            try {
                startActivityForResult(i,REQUEST_PERMISSION_CODE)
            }catch (e : Exception){
                e.printStackTrace()
                Toast.makeText(this,"error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==REQUEST_PERMISSION_CODE){
            if(resultCode== RESULT_OK && data!= null){
                val result : ArrayList<String>? = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                sourceEdt.setText(result!![0])
            }
        }
    }

    private fun translateText(fromLanguageCode : Int, toLanguageCode : Int, source : String){
        translatedTV.text = "Downloading Model..."
        val options = FirebaseTranslatorOptions.Builder().setSourceLanguage(fromLanguageCode)
            .setTargetLanguage(toLanguageCode) // after that we are building our options.
                .build()
        val translator = FirebaseNaturalLanguage.getInstance().getTranslator(options)
        val conditions = FirebaseModelDownloadConditions.Builder().build()

        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(OnSuccessListener {
            translatedTV.text = "Translating...."
            translator.translate(source).addOnSuccessListener(OnSuccessListener {s->
                translatedTV.text = s
            }).addOnFailureListener(OnFailureListener {
                Toast.makeText(this,"Fail to translate",Toast.LENGTH_SHORT).show()
            })

            }).addOnFailureListener(OnFailureListener {
            Toast.makeText(this,"Fail to download language model",Toast.LENGTH_SHORT).show()
        })
        }

    private fun getLanguageCode(language : String): Int {
        var languageCode = 0
        when (language){
            "English"->{
                languageCode = FirebaseTranslateLanguage.EN
            }
            "Afrikaans"->{
                languageCode = FirebaseTranslateLanguage.AF
            }
            "Arabic"->{
                languageCode = FirebaseTranslateLanguage.AR
            }
            "Belarusian"->{
                languageCode = FirebaseTranslateLanguage.BE
            }
            "Bengali"->{
                languageCode = FirebaseTranslateLanguage.BN
            }
            "Catalan"->{
                languageCode = FirebaseTranslateLanguage.CA
            }
            "Czech"->{
                languageCode = FirebaseTranslateLanguage.CS
            }
            "Welsh"->{
                languageCode = FirebaseTranslateLanguage.CY
            }
            "Hindi"->{
                languageCode = FirebaseTranslateLanguage.HI
            }
            "Urdu"->{
                languageCode = FirebaseTranslateLanguage.UR
            }
        }
        return languageCode
    }

    }

