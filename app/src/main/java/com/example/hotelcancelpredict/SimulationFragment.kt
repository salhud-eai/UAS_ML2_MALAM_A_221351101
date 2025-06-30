package com.example.hotelcancelpredict

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.hotelcancelpredict.databinding.FragmentSimulationBinding

class SimulationFragment : Fragment() {

    private lateinit var binding: FragmentSimulationBinding
    private lateinit var tfliteHelper: TFLiteHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSimulationBinding.inflate(inflater, container, false)
        tfliteHelper = TFLiteHelper(requireContext())

        val view = binding.root

        // Spinner setup
        val spinnerData = mapOf(
            binding.spinnerHotel to listOf("Resort Hotel", "City Hotel"),
            binding.spinnerMeal to listOf("BB", "FB", "HB", "SC", "Undefined"),
            binding.spinnerMarketSegment to listOf("Direct", "Corporate", "Online TA", "Offline TA/TO", "Complementary", "Groups", "Undefined", "Aviation"),
            binding.spinnerDistribution to listOf("Direct", "Corporate", "TA/TO", "Undefined", "GDS"),
            binding.spinnerDeposit to listOf("No Deposit", "Refundable", "Non Refund"),
            binding.spinnerCustomer to listOf("Transient", "Contract", "Transient-Party", "Group"),
            binding.spinnerMonth to listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        )

        for ((spinner, items) in spinnerData) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        binding.btnPredict.setOnClickListener {
            try {
                val input = floatArrayOf(
                    tfliteHelper.encode("hotel", binding.spinnerHotel.selectedItem.toString()),
                    tfliteHelper.encode("meal", binding.spinnerMeal.selectedItem.toString()),
                    tfliteHelper.encode("market_segment", binding.spinnerMarketSegment.selectedItem.toString()),
                    tfliteHelper.encode("distribution_channel", binding.spinnerDistribution.selectedItem.toString()),
                    tfliteHelper.encode("deposit_type", binding.spinnerDeposit.selectedItem.toString()),
                    tfliteHelper.encode("customer_type", binding.spinnerCustomer.selectedItem.toString()),
                    tfliteHelper.encode("arrival_date_month", binding.spinnerMonth.selectedItem.toString()),
                    binding.inputLeadTime.text.toString().toFloat(),
                    binding.inputWeekendNights.text.toString().toFloat(),
                    binding.inputWeekNights.text.toString().toFloat(),
                    binding.inputAdults.text.toString().toFloat(),
                    binding.inputChildren.text.toString().toFloat(),
                    binding.inputBabies.text.toString().toFloat(),
                    binding.inputPrevCancel.text.toString().toFloat(),
                    binding.inputBookingChange.text.toString().toFloat(),
                    binding.inputWaitingList.text.toString().toFloat(),
                    binding.inputADR.text.toString().toFloat(),
                    binding.inputCarParking.text.toString().toFloat()
                )

                val result = tfliteHelper.predict(input)
                Toast.makeText(requireContext(), if (result >= 0.5f) "Booking Dibatalkan" else "Booking Tidak Dibatalkan", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Masukkan semua nilai dengan benar", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
