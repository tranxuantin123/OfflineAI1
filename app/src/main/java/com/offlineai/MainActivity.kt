package com.offlineai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.File
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OfflineAIApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineAIApp() {
    var input by remember { mutableStateOf("") }
    var chat by remember { mutableStateOf(listOf<String>()) }
    val context = LocalContext.current
    val memoryFile = File(context.filesDir, "memory.json")

    fun loadMemory(): JSONObject {
        return if (memoryFile.exists()) {
            JSONObject(memoryFile.readText())
        } else JSONObject()
    }

    fun saveMemory(memory: JSONObject) {
        memoryFile.writeText(memory.toString())
    }

    fun respond(msg: String): String {
        val memory = loadMemory()
        val reply = memory.optString(msg, "Tôi chưa biết điều đó. Hãy dạy tôi nhé!")
        return reply
    }

    fun learn(msg: String, reply: String) {
        val memory = loadMemory()
        memory.put(msg, reply)
        saveMemory(memory)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Offline AI 🤖") }) },
        content = { padding ->
            Column(Modifier.padding(padding).fillMaxSize().padding(8.dp)) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(chat.size) { i -> Text(chat[i]) }
                }
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nhập tin nhắn...") }
                )
                Button(
                    onClick = {
                        val r = respond(input)
                        chat = chat + "Bạn: $input" + "AI: $r"
                        input = ""
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                ) {
                    Text("Gửi")
                }
            }
        }
    )
}
