package com.jailton.apptemplateproject.ui.usuario

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jailton.apptemplateproject.MainActivity
import com.jailton.apptemplateproject.R
import com.jailton.apptemplateproject.baseclasses.Usuario
import com.jailton.apptemplateproject.databinding.FragmentHomeBinding
import com.jailton.apptemplateproject.databinding.FragmentPerfilUsuarioBinding


class PerfilUsuarioFragment : Fragment() {

    private var _binding: FragmentPerfilUsuarioBinding? = null

    private lateinit var registerNameEditText: EditText
    private lateinit var registerEmailEditText: EditText
    private lateinit var registerPasswordEditText: EditText
    private lateinit var registerConfirmPasswordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var sairButton: Button
    private lateinit var database: DatabaseReference

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_perfil_usuario, container, false)

        // Inicializar Realtime Database
        database =
            FirebaseDatabase.getInstance().reference

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
            Toast.makeText(
                context,
                "Logout realizado com sucesso!",
                Toast.LENGTH_SHORT
            ).show()
            requireActivity().finish()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Exibe os dados do usuário logado, se disponível

        // Acessar currentUser
        val user = MainActivity.currentUser

        if (user != null) {
            registerNameEditText.setText(user.nome)
            registerEmailEditText.setText(user.email)
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
            Toast.makeText(context, "Não foi possível encontrar o usuário logado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}