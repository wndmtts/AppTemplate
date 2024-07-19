package com.jailton.apptemplateproject.ui.usuario

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jailton.apptemplateproject.MainActivity
import com.jailton.apptemplateproject.R
import com.jailton.apptemplateproject.baseclasses.Usuario

class CadastroUsuarioActivity : AppCompatActivity() {

    private lateinit var textCadastroUsuarioTitle: TextView
    private lateinit var registerNameEditText: EditText
    private lateinit var registerEmailEditText: EditText
    private lateinit var registerPasswordEditText: EditText
    private lateinit var registerConfirmPasswordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var sairButton: Button
    private lateinit var database: DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activityt_cadastro_usuario)


        // Inicializar Realtime Database
        database =
            FirebaseDatabase.getInstance().reference

        textCadastroUsuarioTitle = findViewById(R.id.textCadastroUsuarioTitle)
        registerNameEditText = findViewById(R.id.registerNameEditText)
        registerEmailEditText = findViewById(R.id.registerEmailEditText)
        registerPasswordEditText = findViewById(R.id.registerPasswordEditText)
        registerConfirmPasswordEditText = findViewById(R.id.registerConfirmPasswordEditText)
        registerButton = findViewById(R.id.registerButton)
        sairButton = findViewById(R.id.sairButton)

        // Acessar currentUser
        val user = MainActivity.currentUser

        if (user != null) {
            sairButton.visibility = View.VISIBLE
        }

        registerButton.setOnClickListener {
            registerUser()
        }

        sairButton.setOnClickListener {
            MainActivity.currentUser = null
            finish()
        }

    }

    private fun registerUser() {
        val name = registerNameEditText.text.toString().trim()
        val email = registerEmailEditText.text.toString().trim()
        val password = registerPasswordEditText.text.toString().trim()
        val confirmPassword = registerConfirmPasswordEditText.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtém uma referência para o nó "usuarios" no Realtime Database
        val usuariosRef = database.child("usuarios")

        // Acessar currentUser
        val user = MainActivity.currentUser

        // Verifica se o usuário atual já está definido
        if (user != null) {
            // Se o usuário já existe, atualiza os dados
            val userId =
                user.key  // Assumindo que userId é a chave do usuário no banco de dados
            val updatedUser = Usuario(userId, name, email, password)

            if (userId != null) {
                usuariosRef.child(userId).setValue(updatedUser)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Dados do usuário atualizados com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Falha ao atualizar dados do usuário",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        } else {
            // Se o usuário não existe, insere um novo usuário
            val userId = usuariosRef.push().key
            if (userId != null) {
                val newUser = Usuario(userId, name, email, password)

                usuariosRef.child(userId).setValue(newUser)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Novo usuário cadastrado com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Falha ao cadastrar novo usuário",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                Toast.makeText(this, "Erro ao gerar o ID do usuário", Toast.LENGTH_SHORT).show()
            }
        }
    }
}