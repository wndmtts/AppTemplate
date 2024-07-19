package com.jailton.apptemplateproject.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jailton.apptemplateproject.MainActivity
import com.jailton.apptemplateproject.R
import com.jailton.apptemplateproject.baseclasses.Usuario
import com.jailton.apptemplateproject.ui.usuario.CadastroUsuarioActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView
    private lateinit var navController: NavController
    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        FirebaseApp.initializeApp(this)

        emailEditText = findViewById(R.id.edit_text_email)
        passwordEditText = findViewById(R.id.edit_text_password)
        loginButton = findViewById(R.id.button_login)
        registerLink = findViewById(R.id.registerLink)



        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this,
                    "Por favor, preencha todos os campos",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Implementar a lógica de login aqui
                database =
                    FirebaseDatabase.getInstance().reference
                val usersReference = database.child("usuarios")

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
                            Toast.makeText(applicationContext, "Login bem-sucedido!", Toast.LENGTH_SHORT)
                                .show()
                            val intent: Intent = Intent(
                                applicationContext,
                                MainActivity::class.java
                            )
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Email ou senha incorretos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            applicationContext,
                            "Erro ao acessar o banco de dados",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }


        val registerLink: TextView = findViewById(R.id.registerLink)
        registerLink.setOnClickListener {
            val intent: Intent = Intent(
                applicationContext,
                CadastroUsuarioActivity::class.java
            )
            startActivity(intent)
        }
    }
}