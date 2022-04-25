package com.feadca.protectora.utils

// Valor que contiene la IP del servidor
const val IP = "http://192.168.1.130:8069"

// URL usadas para las autenticaciones ( login, registro, recordarContraseña, loginToken )
const val LOGIN_URL = IP + "/apirest/login"
const val LOGIN_TOKEN_URL = IP + "/apirest/loginToken"
const val RECOVER_PASSWORD_URL = IP + "/apirest/recuperarContrasenya"
const val REGISTER_URL = IP + "/apirest/registro"