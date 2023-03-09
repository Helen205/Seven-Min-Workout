package com.example.sevenminworkout

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sevenminworkout.databinding.ActivityBmiactivityBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BMIActivity : AppCompatActivity() {

    companion object {
        private const val METRIC_UNITS_VIEW = "METRIC_UNIT_VIEW"
        private const val US_UNITS_VIEW = "US_UNIT_VIEW"
    }

    private var currentVisibleView: String = METRIC_UNITS_VIEW
    private var binding: ActivityBmiactivityBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiactivityBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarBmiActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "BMI HESAPLAMA"
        binding?.toolbarBmiActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
        makeVisibleMetricUnitsView()
        binding?.rgUnits?.setOnCheckedChangeListener { _, checkedId: Int ->

            if (checkedId == R.id.rbMetricUnits) {
                makeVisibleMetricUnitsView()
            } else {
                makeVisibleUsUnitsView()
            }
        }
        binding?.btnCalculateUnits?.setOnClickListener {
            calculateUnits()
        }

    }

    private fun calculateUnits(){
        if (currentVisibleView == METRIC_UNITS_VIEW) {
            if (validateMetricUnits()) {
                val heightValue: Float = binding?.etMetricUnitHeight?.text.toString().toFloat() / 100
                val weightValue: Float = binding?.etMetricUnitWeight?.text.toString().toFloat()
                val bmi = weightValue / (heightValue * heightValue)

                displayBMIResult(bmi)
            } else {
                Toast.makeText(
                    this@BMIActivity,
                    "Lütfen geçerli değerler girin.",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        } else {

            if (validateUsUnits()) {

                val usUnitHeightValueFeet: String = binding?.etUsMetricUnitHeightFeet?.text.toString()
                val usUnitHeightValueInch: String = binding?.etUsMetricUnitHeightInch?.text.toString()
                val usUnitWeightValue: Float = binding?.etUsMetricUnitWeight?.text.toString()
                    .toFloat()


                val heightValue = usUnitHeightValueInch.toFloat() + usUnitHeightValueFeet.toFloat() * 12


                val bmi = 703 * (usUnitWeightValue / (heightValue * heightValue))

                displayBMIResult(bmi)
            } else {
                Toast.makeText(
                    this@BMIActivity,
                    "Lütfen geçerli değerler girin.",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }



    private fun makeVisibleMetricUnitsView() {
        currentVisibleView = METRIC_UNITS_VIEW
        binding?.tilMetricUnitWeight?.visibility = View.VISIBLE
        binding?.tilMetricUnitHeight?.visibility = View.VISIBLE
        binding?.tilUsMetricUnitWeight?.visibility = View.GONE
        binding?.tilMetricUsUnitHeightFeet?.visibility = View.GONE
        binding?.tilMetricUsUnitHeightInch?.visibility = View.GONE

        binding?.etMetricUnitHeight?.text!!.clear()
        binding?.etMetricUnitWeight?.text!!.clear()

        binding?.llDiplayBMIResult?.visibility = View.INVISIBLE
    }
    // END

    private fun makeVisibleUsUnitsView() {
        currentVisibleView = US_UNITS_VIEW
        binding?.tilMetricUnitHeight?.visibility = View.INVISIBLE
        binding?.tilMetricUnitWeight?.visibility = View.INVISIBLE
        binding?.tilUsMetricUnitWeight?.visibility = View.VISIBLE
        binding?.tilMetricUsUnitHeightFeet?.visibility = View.VISIBLE
        binding?.tilMetricUsUnitHeightInch?.visibility = View.VISIBLE

        binding?.etUsMetricUnitWeight?.text!!.clear()
        binding?.etUsMetricUnitHeightFeet?.text!!.clear()
        binding?.etUsMetricUnitHeightInch?.text!!.clear()

        binding?.llDiplayBMIResult?.visibility = View.INVISIBLE
    }


    private fun validateMetricUnits(): Boolean {
        var isValid = true

        if (binding?.etMetricUnitWeight?.text.toString().isEmpty()) {
            isValid = false
        } else if (binding?.etMetricUnitHeight?.text.toString().isEmpty()) {
            isValid = false
        }

        return isValid
    }

    private fun validateUsUnits(): Boolean {
        var isValid = true

        when {
            binding?.etUsMetricUnitWeight?.text.toString().isEmpty() -> {
                isValid = false
            }
            binding?.etUsMetricUnitHeightFeet?.text.toString().isEmpty() -> {
                isValid = false
            }
            binding?.etUsMetricUnitHeightInch?.text.toString().isEmpty() -> {
                isValid = false
            }
        }

        return isValid
    }

    private fun displayBMIResult(bmi: Float) {

        val bmiLabel: String
        val bmiDescription: String

        if (bmi.compareTo(15f) <= 0) {
            bmiLabel = "Çok ciddi şekilde zayıf"
            bmiDescription = "Kendinize gerçekten daha iyi bakmanız gerekiyor!"
        } else if (bmi.compareTo(15f) > 0 && bmi.compareTo(16f) <= 0
        ) {
            bmiLabel = "Şiddetli Zayıf"
            bmiDescription = "Kendinize gerçekten daha iyi bakmanız gerekiyor!"
        } else if (bmi.compareTo(16f) > 0 && bmi.compareTo(18.5f) <= 0
        ) {
            bmiLabel = "Zayıf"
            bmiDescription = "Kendinize gerçekten daha iyi bakmanız gerekiyor!"
        } else if (bmi.compareTo(18.5f) > 0 && bmi.compareTo(25f) <= 0
        ) {
            bmiLabel = "Normal"
            bmiDescription = "Tebrikler! İyi durumdasın!"
        } else if (java.lang.Float.compare(bmi, 25f) > 0 && java.lang.Float.compare(
                bmi,
                30f
            ) <= 0
        ) {
            bmiLabel = "Kilolu"
            bmiDescription = "Oops! Kendine gerçekten iyi bakmalısın! Egzersiz zamanı!"
        } else if (bmi.compareTo(30f) > 0 && bmi.compareTo(35f) <= 0
        ) {
            bmiLabel = "Obez Sınıf ||(Orta derecede obez)"
            bmiDescription = "Oops! Kendine gerçekten iyi bakmalısın! Egzersiz zamanı!"
        } else if (bmi.compareTo(35f) > 0 && bmi.compareTo(40f) <= 0
        ) {
            bmiLabel = "Obez Sınıf || (Aşırı Obez)"
            bmiDescription = "Çok tehlikeli bir durumdasın! Şimdi harekete geçmelisin!"
        } else {
            bmiLabel = "Obez Sınıf ||| (Çok Ağır Obez)"
            bmiDescription = "Çok tehlikeli bir durumdasın! Şimdi harekete geçmelisin!"
        }

        binding?.llDiplayBMIResult?.visibility = View.VISIBLE

        val bmiValue = BigDecimal(bmi.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toString()

        binding?.tvBMIValue?.text = bmiValue
        binding?.tvBMIType?.text = bmiLabel
        binding?.tvBMIDescription?.text = bmiDescription
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}