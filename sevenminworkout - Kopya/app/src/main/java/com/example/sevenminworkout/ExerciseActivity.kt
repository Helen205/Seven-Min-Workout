package com.example.sevenminworkout

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sevenminworkout.databinding.ActivityExerciseBinding
import com.example.sevenminworkout.databinding.DialogCustomBackConfirmationBinding

import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var restTimer: CountDownTimer? = null
    private var restProgress = 0
    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress = 0
    private var exerciseTimerDuration:Long = 30
    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1
    private var binding: ActivityExerciseBinding? = null
    private var tts: TextToSpeech? = null
    private var player: MediaPlayer? = null
    private var exerciseAdapter: ExerciseStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarExercise)

        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarExercise?.setNavigationOnClickListener {
            customDialogForBackButton()
        }
        tts = TextToSpeech(this, this)
        exerciseList = Constants.defaultExerciseList()
        setupRestView()

        setupExerciseStatusRecyclerView()
    }

    /**
     * Fonksiyon,zamanlayıcıyı ayarlamak için kullanılır.
     */
    private fun setupRestView() {
        /**
         * Burada ses dosyası kaynaklarda "raw" klasörüne eklenir.
         * Ve MediaPlayer kullanılarak oynanır. MediaPlayer sınıfı, oynatmayı kontrol etmek için kullanılabilir
         * ses/video dosyaları ve akışları.
         */
        try {
            val soundURI =
                Uri.parse("android.resource://eu.tutorials.a7_minutesworkoutapp/" + R.raw.press_start)
            player = MediaPlayer.create(applicationContext, soundURI)
            player?.isLooping = false
            player?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding?.flRestView?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.upcomingLabel?.visibility = View.VISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.flExerciseView?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE
        /**
         * Burada öncelikle zamanlayıcının çalışıp çalışmadığını ve boş olmadığını kontrol edeceğiz, ardından çalışan zamanlayıcıyı iptal edip yenisini başlatacağız.
         * Ve ilerlemeyi 0 olan ilk değere ayarlayacağız.
         */
        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }
        // UI öğesinde yaklaşan egzersiz adını ayarlama
        // Burada yaklaşan alıştırmanın adını metin görünümü olarak ayarlanıyor
        // Burada mevcut konum default olarak -1 olduğu için listeden seçilebilmesi için 0 olması gerektiği için +1 arttırır.
        binding?.tvUpcomingExerciseName?.text = exerciseList!![currentExercisePosition + 1].getName()
        setRestProgressBar()
    }

    // Dinlenme görünümü için 10 saniyelik zamanlayıcıyı ayarlayıp sürekli güncelleniyor.

    private fun setRestProgressBar() {
        binding?.progressBar?.progress = restProgress // Geçerli ilerlemeyi belirtilen değere ayarlar.

        //Burada 10 saniyelik bir zamanlayıcı başlattım, yani 10000 milisaniye 10 saniye ve geri sayım aralığı 1 saniye yani 1000.
        restTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                binding?.progressBar?.progress = 10 - restProgress // İlerleme çubuğunun ilerlemesini gösterir
                binding?.tvTimer?.text = (10 - restProgress).toString()  // Geçerli ilerleme, saniye cinsinden metin görünümüne ayarlanır.
            }

            override fun onFinish() {
                currentExercisePosition++
                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter?.notifyDataSetChanged() // Geçerli öğeyi, kullanıcı arabirimine yansıtması için bağdaştırıcı sınıfına bildirdi.
                setupExerciseView()
            }
        }.start()
    }


// Egzersiz Görünümünü 30 saniyelik bir zamanlayıcı ile ayarlama

    private fun setupExerciseView() {
// yaklaşan egzersiz etiketini ve ad görünürlüğünü değiştiriyor.
        binding?.flRestView?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.INVISIBLE
        binding?.upcomingLabel?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.flExerciseView?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE

        /**
         * Burada öncelikle zamanlayıcının çalışıp çalışmadığını ve boş olmadığını kontrol edeceğiz, ardından çalışan zamanlayıcıyı iptal edip yenisini başlatacak.
         * Ve ilerlemeyi 0 olan başlangıç değerine ayarlayacak.
         */
        if (exerciseTimer != null) {
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }
        speakOut(exerciseList!![currentExercisePosition].getName())
        /**
         * Burada geçerli egzersiz adı ve görüntüsü egzersiz görünümüne ayarlanmıştır.
         */
        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExerciseName?.text = exerciseList!![currentExercisePosition].getName()
        setExerciseProgressBar()

    }
    /**
     * Fonksiyon, 30 Saniye Egzersiz Görünümü için ilerlemeyi kullanarak zamanlayıcının ilerlemesini ayarlamak için kullanılır
     */
    private fun setExerciseProgressBar() {
        binding?.progressBarExercise?.progress = exerciseProgress
        exerciseTimer = object : CountDownTimer(exerciseTimerDuration * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = exerciseTimerDuration.toInt() - exerciseProgress
                binding?.tvTimerExercise?.text = (exerciseTimerDuration.toInt() - exerciseProgress).toString()
            }

            override fun onFinish() {
                if (currentExercisePosition < exerciseList?.size!! - 1) {
                    exerciseList!![currentExercisePosition].setIsSelected(false) // alıştırma tamamlandı, bu yüzden seçim false olarak ayarlandı
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    exerciseAdapter?.notifyDataSetChanged()
                    setupRestView()
                } else {
                    finish()
                    val intent = Intent(this@ExerciseActivity,FinishActivity::class.java)
                    startActivity(intent)
                }

            }
        }.start()
    }

    /**
     * Burada Destroy fonksiyonunda eğer çalışıyorsa dinlenme zamanlayıcısını sıfırlayacağız.
     */
    public override fun onDestroy() {
        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        if(player != null){
            player!!.stop()
        }
        super.onDestroy()
        binding = null
    }


    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Belirtilen dil desteklenmiyor!")
            }

        } else {
            Log.e("TTS", "Başlatma Başarısız!")
        }
    }

    /**
     * Fonksiyon kendisine ilettiğimiz metni seslendirmek için kullanılır.
     */
    private fun speakOut(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }
    private fun setupExerciseStatusRecyclerView() {
        binding?.rvExerciseStatus?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExerciseStatus?.adapter = exerciseAdapter
    }


    private fun customDialogForBackButton() {
        val customDialog = Dialog(this)
        val dialogBinding = DialogCustomBackConfirmationBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)
        dialogBinding.tvYes.setOnClickListener {
            this@ExerciseActivity.finish()
            customDialog.dismiss()
        }
        dialogBinding.tvNo.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }
}