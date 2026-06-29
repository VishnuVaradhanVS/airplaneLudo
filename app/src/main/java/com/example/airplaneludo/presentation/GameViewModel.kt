package com.example.airplaneludo.presentation

import android.app.Application
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.airplaneludo.audio.GameAudioManager
import com.example.airplaneludo.service.LudoNetworkDiscovery
import com.example.backend.startLudoServer
import com.example.backend.stopLudoServer
import com.example.shared.data.Game
import com.example.shared.data.GameAction
import com.example.shared.data.GamePackets
import com.example.shared.data.LobbyAction
import com.example.shared.data.Player
import com.example.shared.data.PlayerGameState
import com.example.shared.data.Room
import com.example.shared.data.Team
import com.example.shared.data.Token
import com.example.shared.data.bluePath
import com.example.shared.data.redPath
import com.example.shared.data.safe
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.serialization.kotlinx.json.json
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.random.Random

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val networkDiscovery = LudoNetworkDiscovery(application)
    val roomId: MutableState<Int?> = mutableStateOf(null)
    val hostIp = mutableStateOf("")
    private val serverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    fun hostLocalNetworkRoom() {
        val generatedRoomId = Random.nextInt(9000) + 1000
        this.roomId.value = generatedRoomId
        serverScope.launch {
            try {
                startLudoServer(port = 8080)
                Log.i("server", "Started server engine successfully")
                delay(500)
                launch(Dispatchers.Main) {
                    connectToNetworkRoom("127.0.0.1", generatedRoomId)
                    Log.i("connection", "Host Client successfully attached via loopback")
                }
                delay(100)
                networkDiscovery.advertiseRoom(generatedRoomId, userName.value ?: "Player")
                Log.i("service", "NSD Room Advertised to Wi-Fi network!")
            } catch (e: Exception) {
                Log.e("server_error", "Error starting module engine: ${e.localizedMessage}")
            }
        }
    }

    fun shutdownHostedRoom() {
        viewModelScope.launch(Dispatchers.IO) {
            exitRoom()
            networkDiscovery.stopAll()
            delay(100)
            stopLudoServer()
        }
    }

    private val client = HttpClient(CIO) {
        install(WebSockets)
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }
    private var webSocketSession: DefaultClientWebSocketSession? = null
    val isConnected = mutableStateOf(false)
    fun connectToNetworkRoom(hostIp: String, targetRoomId: Int) {
        serverScope.launch {
            try {
                client.webSocket(
                    urlString = "ws://$hostIp:8080/ludo/$targetRoomId",
                    request = {
                        userName.value?.let { url.parameters.append("name", it) }
                    }
                ) {
                    webSocketSession = this
                    isConnected.value = true
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val jsonState = frame.readText()
                            val gamePacket = Json.decodeFromString<GamePackets>(jsonState)
                            when (gamePacket.lobbyAction) {
                                LobbyAction.RoomUpdate -> {
                                    updateRoomState(gamePacket.room)
                                }

                                LobbyAction.PlayerAck -> {
                                    updatePlayerState(gamePacket.player)
                                }

                                LobbyAction.RoomInGame -> {
                                    roomInGame.value = true
                                }

                                else -> {

                                }
                            }
                            when (gamePacket.gameAction) {
                                GameAction.GameUpdate -> {
                                    updateGameState(gamePacket.game)
                                }

                                GameAction.StartGame -> {
                                    startGame.value = true
                                    gameStartIsPlaying.value = true
                                    showExitRoom.value = false
                                    exitRoomMessage.value = null
                                    gameInfo.value = false
                                    gameInfoMessage.value = ""
                                }

                                GameAction.AnimateTokenMovement -> {
                                    animateTokenMovement(
                                        gamePacket.moveTokenId,
                                        currentPlayer.value.team,
                                        gamePacket.selectedValue
                                    )
                                }

                                GameAction.SpawnToken -> {
                                    gameAudioManager.playSpawn()
                                }

                                GameAction.EndGame -> {
                                    gameOver.value = true
                                    winningTeam.value = currentPlayer.value.team
                                }

                                GameAction.LeaveGame -> {
                                    gameInfo.value = true
                                    gameInfoMessage.value =
                                        "${gamePacket.player?.name} left the game"
                                }

                                else -> {

                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                println("Connection failed or lost: ${e.localizedMessage}")
            } finally {
                val reason = webSocketSession?.closeReason?.await()
                if (reason?.code == CloseReason.Codes.VIOLATED_POLICY.code) {
                    showLobbyInfo.value = true
                    if (roomInGame.value) {
                        lobbyMessage.value = "Room already in game"
                    } else {
                        lobbyMessage.value = "You were kicked from the room by the host"
                    }
                } else if (reason?.code == CloseReason.Codes.GOING_AWAY.code) {
                    gameStartIsPlaying.value = false
                    if (!gameStartIsPlaying.value) {
                        showExitRoom.value = true
                        exitRoomMessage.value = "Host left the room"
                    }
                    showLobbyInfo.value = true
                    lobbyMessage.value = "Room has been closed by the host"
                } else {
                    showLobbyInfo.value = true
                    if (player.value?.id == host.value?.id) {
                        lobbyMessage.value = "Room has been closed"
                    } else {
                        lobbyMessage.value = "Check the network connection"
                    }
                }
                isConnected.value = false
                webSocketSession = null
            }
        }
    }

    fun updateRoomState(updatedRoom: Room?) {
        roomState.value = updatedRoom
        baseTokenCount.value = updatedRoom?.baseTokenCount!!
        host.value = updatedRoom.players.first { player -> player.id == updatedRoom.hostId }
        println(roomState.value.toString())
        updatePlayerState(updatedRoom.players?.first { p -> p.id == player.value?.id })
        if (updatedRoom.game != null) {
            updateGameState(updatedRoom.game)
        }
        redTeamPlayers.clear()
        redTeamPlayers.addAll(updatedRoom.redTeam)
        blueTeamPlayers.clear()
        blueTeamPlayers.addAll(updatedRoom.blueTeam)
    }

    fun updatePlayerState(updatedPlayer: Player?) {
        player.value = updatedPlayer ?: player.value
        if (updatedPlayer?.playerGameState == PlayerGameState.Rolling) {
            canRollDice.value = true
        } else {
            canRollDice.value = false
        }
        if (updatedPlayer?.playerGameState == PlayerGameState.Moving) {
            canMoveToken.value = true
        } else {
            canMoveToken.value = false
        }
        println(player.value.toString())
    }

    suspend fun swapTeam(player: Player) {
        val gamePacket = GamePackets(LobbyAction.SwapTeam, player = player, room = roomState.value)
        val jsonState = Json.encodeToString(gamePacket)
        webSocketSession?.send(Frame.Text(jsonState))
    }

    suspend fun kickPlayer(player: Player) {
        val gamePacket = GamePackets(LobbyAction.Kick, player = player, room = roomState.value)
        val jsonState = Json.encodeToString(gamePacket)
        webSocketSession?.send(Frame.Text(jsonState))
    }

    suspend fun changeBaseTokenCount() {
        val gamePacket = GamePackets(
            LobbyAction.ChangeTokenCount,
            room = roomState.value?.copy(baseTokenCount = baseTokenCount.value)
        )
        val jsonState = Json.encodeToString(gamePacket)
        webSocketSession?.send(Frame.Text(jsonState))
    }

    suspend fun startGame() {
        val gamePacket = GamePackets(
            lobbyAction = null,
            gameAction = GameAction.StartGame,
            room = null,
            player = null
        )
        val jsonState = Json.encodeToString(gamePacket)
        webSocketSession?.send(Frame.Text(jsonState))
    }

    suspend fun rollDice() {
        val gamePacket = GamePackets(
            lobbyAction = null,
            gameAction = GameAction.RollDice,
            room = null,
            player = null
        )
        val jsonState = Json.encodeToString(gamePacket)
        webSocketSession?.send(Frame.Text(jsonState))
        delay(400)
    }

    suspend fun exitRoom() {
        viewModelScope.launch {
            try {
                webSocketSession?.close(
                    CloseReason(
                        CloseReason.Codes.GOING_AWAY,
                        "Player left room"
                    )
                )
            } catch (e: Exception) {
                println("Error closing client socket: ${e.message}")
            } finally {
                if (player.value?.id == host.value?.id) {
                    showLobbyInfo.value = true
                    lobbyMessage.value = "Room has been closed"
                }
                webSocketSession = null
                isConnected.value = false
            }
        }
    }

    val availableNetworkRooms = networkDiscovery.discoveredRooms

    fun hostNetworkGame(roomId: Int) {
        networkDiscovery.advertiseRoom(roomId, userName.value ?: "Player")
    }

    fun startLookingForRooms() {
        networkDiscovery.startScanning()
    }

    fun stopNetworkDiscovery() {
        networkDiscovery.stopAll()
    }

    override fun onCleared() {
        super.onCleared()
        stopNetworkDiscovery()
    }

    val gameAudioManager = GameAudioManager(application)
    var player = mutableStateOf<Player?>(null)
    var host = mutableStateOf<Player?>(null)
    var roomState = MutableStateFlow<Room?>(null)

    var startGame = mutableStateOf(false)
    var showExitRoom = mutableStateOf(false)
    var exitRoomMessage: MutableState<String?> = mutableStateOf(null)
    var gameStartIsPlaying = mutableStateOf(false)
    val currentPlayer =
        mutableStateOf(Player(1, "player", Team.RED, false, 0, PlayerGameState.Waiting))
    var redHomeEnabled = mutableStateOf(false)
    val blueHomeEnabled = mutableStateOf(false)
    val showJoinRoomOnline = mutableStateOf(false)
    val showJoinRoomLocal = mutableStateOf(false)
    val showHelp = mutableStateOf(false)
    val musicEnabled = mutableStateOf(true)
    val showLobbyInfo = mutableStateOf(false)
    val lobbyMessage = mutableStateOf("")
    val userName: MutableState<String?> = mutableStateOf(null)
    val baseTokenCount = mutableStateOf(6)
    val hostRoomClose = mutableStateOf(false)
    val tokenInMotion = mutableStateOf(false)
    val killedTokenRed = mutableStateOf(false)
    val killedTokenBlue = mutableStateOf(false)
    val redHomeCount = mutableStateOf(0)
    val redBaseCount = mutableStateOf(0)
    val blueHomeCount = mutableStateOf(0)
    val blueBaseCount = mutableStateOf(0)
    val redHome = mutableStateOf(false)
    val blueHome = mutableStateOf(false)
    val dice1 = mutableStateOf(0)
    val dice2 = mutableStateOf(0)
    var triggerDiceRoll = mutableStateOf(0L)
    val canRollDice = mutableStateOf(false)
    val canMoveToken = mutableStateOf(false)
    val selectedStep = mutableStateOf(0)
    val gameOver = mutableStateOf(false)
    val winningTeam = mutableStateOf<Team?>(null)
    var redTeamPlayers = mutableStateListOf<Player>()
    var blueTeamPlayers = mutableStateListOf<Player>()
    var redTokens = mutableStateListOf<Token>()
    var blueTokens = mutableStateListOf<Token>()
    var diceDenomination = mutableStateMapOf<Int, Int>()
    var roomInGame = mutableStateOf(false)
    var gameInfo = mutableStateOf(false)
    var gameInfoMessage = mutableStateOf("")

    fun updateGameState(currentGame: Game?) {
        if (currentGame == null) return
        if (player.value?.id == currentGame.currentPlayer?.id) {
            updatePlayerState(currentGame.currentPlayer)
        }
        currentPlayer.value = roomState.value?.game?.currentPlayer
            ?: Player(1, "player", Team.RED, false, 0, playerGameState = PlayerGameState.Waiting)
        redTokens.clear()
        redTokens.addAll(currentGame.redTokens)
        blueTokens.clear()
        blueTokens.addAll(currentGame.blueTokens)
        redHomeEnabled.value = currentGame.redHomeEnabled
        blueHomeEnabled.value = currentGame.blueHomeEnabled
        redBaseCount.value = currentGame.redBaseCount
        blueBaseCount.value = currentGame.blueBaseCount
        redHomeCount.value = currentGame.redHomeCount
        blueHomeCount.value = currentGame.blueHomeCount
        triggerDiceRoll.value++
        dice1.value = currentGame.diceValue.d1
        dice2.value = currentGame.diceValue.d2
        viewModelScope.launch {
            delay(800)
            diceDenomination.clear()
            diceDenomination.putAll(currentGame.diceDenomination)
        }
    }

    suspend fun spawnToken() {
        val gamePacket = GamePackets(
            lobbyAction = null,
            gameAction = GameAction.SpawnToken,
            room = null,
            player = null,
            selectedValue = selectedStep.value
        )
        val jsonState = Json.encodeToString(gamePacket)
        webSocketSession?.send(Frame.Text(jsonState))
        gameAudioManager.playSpawn()
    }

    suspend fun animateTokenMovement(tokenId: Int?, team: Team, step: Int?) {
        for ((k, v) in diceDenomination) {
            if (k == step) {
                diceDenomination[k] = diceDenomination.getOrDefault(k, 0) - 1
                if (diceDenomination[k] == 0) {
                    diceDenomination.remove(k)
                }
                break
            }
        }
        if (team == Team.RED) {
            val index = redTokens.indexOfFirst { it.id == tokenId }
            if (index != -1) {
                tokenInMotion.value = true
                for (i in 1..step!!) {
                    val currentStepToken = redTokens[index].copy()
                    gameAudioManager.playToken()
                    currentStepToken.incrementPosition()
                    if (currentStepToken.position == 60 && !redHomeEnabled.value) {
                        currentStepToken.position = 12
                    }
                    redTokens[index] = currentStepToken
                    delay(400)
                }
                tokenInMotion.value = false
                val finalToken = redTokens[index]
                if (finalToken.position >= redPath.size - 1) {
                    redHomeCount.value += 1
                    gameAudioManager.playHome()
                    redHome()
                    val scoredToken = finalToken.copy()
                    scoredToken.killToken()
                    redTokens[index] = scoredToken
                }
                val postHomeToken = redTokens[index]
                if (postHomeToken.position != -1 && !safe.contains(postHomeToken.cords)) {
                    for (i in blueTokens.indices) {
                        val opponentToken = blueTokens[i]
                        if (opponentToken.position != -1 && opponentToken.cords == postHomeToken.cords) {
                            killedTokenBlue.value = true
                            redHomeEnabled.value = true
                            gameAudioManager.playKill()
                            while (blueTokens[i].position > -1) {
                                val killedToken = blueTokens[i].copy()
                                killedToken.decrementPosition()
                                blueTokens[i] = killedToken
                                delay(50)
                            }
                            killedTokenBlue.value = false
                            val finalKilledToken = blueTokens[i].copy()
                            finalKilledToken.killToken()
                            blueTokens[i] = finalKilledToken
                            blueBaseCount.value += 1
                        }
                    }
                }
            }
        } else {
            val index = blueTokens.indexOfFirst { it.id == tokenId }
            if (index != -1) {
                tokenInMotion.value = true
                for (i in 1..step!!) {
                    val currentStepToken = blueTokens[index].copy()
                    gameAudioManager.playToken()
                    currentStepToken.incrementPosition()
                    if (currentStepToken.position == 60 && !blueHomeEnabled.value) {
                        currentStepToken.position = 12
                    }
                    blueTokens[index] = currentStepToken
                    delay(400)
                }
                tokenInMotion.value = false
                val finalToken = blueTokens[index]
                if (finalToken.position >= bluePath.size - 1) {
                    blueHomeCount.value += 1
                    gameAudioManager.playHome()
                    blueHome()
                    val scoredToken = finalToken.copy()
                    scoredToken.killToken()
                    blueTokens[index] = scoredToken
                }
                val postHomeToken = blueTokens[index]
                if (postHomeToken.position != -1 && !safe.contains(postHomeToken.cords)) {
                    for (i in redTokens.indices) {
                        val opponentToken = redTokens[i]
                        if (opponentToken.position != -1 && opponentToken.cords == postHomeToken.cords) {
                            killedTokenRed.value = true
                            blueHomeEnabled.value = true
                            gameAudioManager.playKill()
                            while (redTokens[i].position > -1) {
                                val killedToken = redTokens[i].copy()
                                killedToken.decrementPosition()
                                redTokens[i] = killedToken
                                delay(50)
                            }
                            killedTokenRed.value = false
                            val finalKilledToken = redTokens[i].copy()
                            finalKilledToken.killToken()
                            redTokens[i] = finalKilledToken
                            redBaseCount.value += 1
                        }
                    }
                }
            }
        }
        if (!diceDenomination.containsKey(selectedStep.value)) {
            selectedStep.value = 0
        }
        selectedStep.value = 0
    }

    suspend fun moveToken(token: Token, team: Team) {
        viewModelScope.launch {
            var gamePacket = GamePackets(
                lobbyAction = null,
                gameAction = GameAction.AnimateTokenMovement,
                room = null,
                player = null,
                selectedValue = selectedStep.value,
                moveTokenId = token.id
            )
            var jsonState = Json.encodeToString(gamePacket)
            webSocketSession?.send(Frame.Text(jsonState))
        }
            .join()
        val gamePacket = GamePackets(
            lobbyAction = null,
            gameAction = GameAction.MoveToken,
            room = null,
            player = null,
            selectedValue = selectedStep.value,
            moveTokenId = token.id
        )
        val jsonState = Json.encodeToString(gamePacket)
        webSocketSession?.send(Frame.Text(jsonState))
    }

    suspend fun redHome() {
        redHome.value = true
        delay(300)
        redHome.value = false
        delay(300)
        redHome.value = true
        delay(300)
        redHome.value = false
        delay(300)
        redHome.value = true
        delay(300)
        redHome.value = false
    }

    suspend fun blueHome() {
        blueHome.value = true
        delay(300)
        blueHome.value = false
        delay(300)
        blueHome.value = true
        delay(300)
        blueHome.value = false
        delay(300)
        blueHome.value = true
        delay(300)
        blueHome.value = false
    }
}
