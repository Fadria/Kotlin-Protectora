package com.feadca.protectora.utils

// Valor que contiene la IP del servidor
const val IP = "http://192.168.1.135:8069"

// URL usadas para las autenticaciones ( login, logout, registro, recordarContraseña, loginToken )
const val LOGIN_URL = IP + "/apirest/login"
const val LOGIN_TOKEN_URL = IP + "/apirest/loginToken"
const val RECOVER_PASSWORD_URL = IP + "/apirest/recuperarContrasenya"
const val REGISTER_URL = IP + "/apirest/registro"
const val LOGOUT_URL = IP + "/apirest/logout"

// URL usadas para el mailing ( contacto )
const val CONTACT_URL = IP + "/apirest/contacto"

// URL usadas para el blog
const val BLOG_ENTRIES = IP + "/apirest/publicaciones"

// URL usadas para los gráficos
const val GRAPHICS = IP + "/apirest/graficos"

// URL usadas para las revisiones
const val REVISION_LIST = IP + "/apirest/revisiones/"