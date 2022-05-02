package com.feadca.protectora.ui.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.feadca.protectora.R
import com.feadca.protectora.databinding.FragmentDonationsBinding
import com.feadca.protectora.ui.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar


class DonationsFragment : Fragment() {
    private var fragmentDonationsBinding: FragmentDonationsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_donations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentDonationsBinding.bind(view)
        fragmentDonationsBinding = binding

        binding.btnBizum.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.donation_bizum))
                .setMessage(
                    "Puede donar enviando un Bizum al teléfono 666 999 333 con su donación.\n\n" +
                            "Recuerde que cada cuenco de comida cuesta 1€. Muchas gracias."
                )
                .setNeutralButton(getString(R.string.cerrar)) { _, _ -> }
                .show()
        }

        binding.btnTransfer.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.donation_bank))
                .setMessage(
                    "Puede donar enviando una transferencia al siguiente IBAN con su donación.\n\n" +
                            "IBAN: ES6500493732237968060296\n\n" +
                            "Recuerde que cada cuenco de comida cuesta 1€. Muchas gracias."
                )
                .setNeutralButton(getString(R.string.cerrar)) { _, _ -> }
                .show()
        }

        binding.btnPayPal.setOnClickListener {
            prepareDonationCall(binding)
        }

        binding.btnCreditCard.setOnClickListener {
            prepareDonationCall(binding)
        }
    }

    private fun prepareDonationCall(binding: FragmentDonationsBinding) {
        var donationAmountText = binding.etDonation.text.toString()
        if (donationAmountText != "") {
            val donationAmount = donationAmountText.toDouble()
            if (donationAmount > 0) {
                (activity as MainActivity?)!!.getPayment(donationAmount)
            } else {
                showSnackbar("Por favor, introduzca una cantidad superior a 0")
            }
        }else{
            showSnackbar("Por favor, introduzca la cantidad a donar")
        }
    }

    // Función encargada de mostrar avisos
    private fun showSnackbar(message: String) {
        Snackbar.make(fragmentDonationsBinding!!.layout, message, Snackbar.LENGTH_SHORT)
            .show()
    }
}