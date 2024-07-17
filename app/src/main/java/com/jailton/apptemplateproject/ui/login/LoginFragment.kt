package com.jailton.apptemplateproject.ui.login

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jailton.apptemplateproject.MainActivity
import com.jailton.apptemplateproject.R
import com.jailton.apptemplateproject.baseclasses.Usuario
import com.jailton.apptemplateproject.ui.cadastros.CadastroUsuarioFragment

class LoginFragment : Fragment() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView
    private lateinit var navController: NavController

    private lateinit var textCadastroUsuarioTitle: TextView
    private lateinit var registerNameEditText: EditText
    private lateinit var registerEmailEditText: EditText
    private lateinit var registerPasswordEditText: EditText
    private lateinit var registerConfirmPasswordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var sairButton: Button
    private lateinit var database: DatabaseReference

    companion object {
        var MODE = ""
        const val LOGIN = "login"
        const val CADASTRO = "cadastro"

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        emailEditText = view.findViewById(R.id.edit_text_email)
        passwordEditText = view.findViewById(R.id.edit_text_password)
        loginButton = view.findViewById(R.id.button_login)
        registerLink = view.findViewById(R.id.registerLink)

        // Initialize NavController
        navController = findNavController()

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    context,
                    "Por favor, preencha todos os campos",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Implementar a lógica de login aqui
                val database =
                    FirebaseDatabase.getInstance("https://apptemplate-35820-default-rtdb.firebaseio.com/")
                val usersReference = database.getReference("usuarios")

                // Listener para verificar se o usuário existe
                usersReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var userExists = false
                        for (userSnapshot in snapshot.children) {
                            // Tente pegar os dados como um mapa e criar uma instância de Usuario manualmente
                            val userMap = userSnapshot.value as? Map<*, *>
                            if (userMap != null) {
                                val emailFromDb = userMap["email"] as? String
                                val senhaFromDb = userMap["password"] as? String
                                if (emailFromDb == email && senhaFromDb == password) {
                                    val nomeFromDb = userMap["nome"] as? String
                                    MainActivity.currentUser = Usuario(
                                        userSnapshot.key,
                                        nomeFromDb,
                                        emailFromDb,
                                        senhaFromDb
                                    )
                                    userExists = true
                                    break
                                }
                            }
                        }

                        if (userExists) {
                            Toast.makeText(context, "Login bem-sucedido!", Toast.LENGTH_SHORT)
                                .show()
                            (activity as MainActivity).updateNavigationMenu()
                        } else {
                            Toast.makeText(
                                context,
                                "Email ou senha incorretos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            context,
                            "Erro ao acessar o banco de dados",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }


        val registerLink: TextView = view.findViewById(R.id.registerLink)
        registerLink.setOnClickListener {
            navController.navigate(R.id.navigation_cadastro_usuario)
        }

        return view

    }
}