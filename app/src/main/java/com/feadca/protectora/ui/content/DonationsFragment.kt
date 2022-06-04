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

/**
 * @Author: Federico Adrià Carrasco
 * @Date: 04/06/2022
 * @Email: fadriacarrasco@gmail.com
 *
 */

// Fragmento que contendrá los elementos relacionados con las donaciones
class DonationsFragment : Fragment() {
    // Enlace con las vistas
    private var fragmentDonationsBinding: FragmentDonationsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_donations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Actualizamos el valor del binding para poder acceder al fragmento
        val binding = FragmentDonationsBinding.bind(view)
        fragmentDonationsBinding = binding

        // Al pulsar el botón de bizum informaremos de cómo realizar una donación con este método
        binding.btnBizum.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.donation_bizum))
                .setMessage(
                    "Puede donar enviando un Bizum al teléfono 666 999 333 con su donación.\n\n" +
                            "Cada 1€, un animal puede disfrutar de una comida. Muchas gracias."
                )
                .setNeutralButton(getString(R.string.cerrar)) { _, _ -> }
                .show()
        }

        // Al pulsar el botón de transferencia bancaria informaremos de cómo realizar una donación con este método
        binding.btnTransfer.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.donation_bank))
                .setMessage(
                    "Puede donar enviando una transferencia al siguiente IBAN con su donación.\n\n" +
                            "IBAN: ES6500493732237968060296\n\n" +
                            "Cada 1€, un animal puede disfrutar de una comida. Muchas gracias."
                )
                .setNeutralButton(getString(R.string.cerrar)) { _, _ -> }
                .show()
        }

        // Al pulsar el botón de PayPal bancaria llamaremos a la función encargada de realizar esta acción
        binding.btnPayPal.setOnClickListener {
            prepareDonationCall(binding)
        }

        // Al pulsar el botón de PayPal bancaria llamaremos a la función encargada de realizar esta acción
        binding.btnCreditCard.setOnClickListener {
            prepareDonationCall(binding)
        }
    }

    // Función encargada de preparar los datos necesarios para realizar una donación
    private fun prepareDonationCall(binding: FragmentDonationsBinding) {
        // Obtenemos la cifra que se desea donar
        var donationAmountText = binding.etDonation.text.toString()

        // Si el usuario aporta una cantidad procedemos, si no, informaremos de ello
        if (donationAmountText != "") {
            // Convertimos el string en un número doble
            val donationAmount = donationAmountText.toDouble()
            if (donationAmount > 0) {
                // Llamamos a la función ubicada en el main activity para continuar el proceso de pago
                (activity as MainActivity?)!!.makePayment(donationAmount)
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