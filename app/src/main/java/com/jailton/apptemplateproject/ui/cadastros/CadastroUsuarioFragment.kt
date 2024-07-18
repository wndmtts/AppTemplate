package com.jailton.apptemplateproject.ui.cadastros

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jailton.apptemplateproject.MainActivity
import com.jailton.apptemplateproject.R
import com.jailton.apptemplateproject.baseclasses.Usuario

class CadastroUsuarioFragment : Fragment() {

    private lateinit var textCadastroUsuarioTitle: TextView
    private lateinit var registerNameEditText: EditText
    private lateinit var registerEmailEditText: EditText
    private lateinit var registerPasswordEditText: EditText
    private lateinit var registerConfirmPasswordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var sairButton: Button
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cadastro_usuario, container, false)

        // Inicializar Realtime Database
        database =
            FirebaseDatabase.getInstance().reference

        textCadastroUsuarioTitle = view.findViewById(R.id.textCadastroUsuarioTitle)
        registerNameEditText = view.findViewById(R.id.registerNameEditText)
        registerEmailEditText = view.findViewById(R.id.registerEmailEditText)
        registerPasswordEditText = view.findViewById(R.id.registerPasswordEditText)
        registerConfirmPasswordEditText = view.findViewById(R.id.registerConfirmPasswordEditText)
        registerButton = view.findViewById(R.id.registerButton)
        sairButton = view.findViewById(R.id.sairButton)

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
            requireActivity().supportFragmentManager.popBackStack()
        }

        // Infla o layout para este fragmento
        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Exibe os dados do usuário logado, se disponível

        // Acessar currentUser
        val user = MainActivity.currentUser

        if(user != null){
            registerNameEditText.setText(user.nome)
            registerEmailEditText.setText(user.email)

            // Altera o texto do botão para "Atualizar"
            registerButton.text = "Atualizar"
            textCadastroUsuarioTitle.text = "Atualizar Dados"
        }
    }


    private fun registerUser() {
        val name = registerNameEditText.text.toString().trim()
        val email = registerEmailEditText.text.toString().trim()
        val password = registerPasswordEditText.text.toString().trim()
        val confirmPassword = registerConfirmPasswordEditText.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(context, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(context, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
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
                            context,
                            "Dados do usuário atualizados com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            context,
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
                            context,
                            "Novo usuário cadastrado com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            context,
                            "Falha ao cadastrar novo usuário",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                Toast.makeText(context, "Erro ao gerar o ID do usuário", Toast.LENGTH_SHORT).show()
            }
        }
    }
}