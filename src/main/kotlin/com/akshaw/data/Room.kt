package com.akshaw.data

import io.ktor.websocket.*
import kotlinx.coroutines.isActive

class Room(
    val name: String,
    val maxPlayer: Int,
    var players: List<Player> = emptyList()
) {


    private var phaseChangeListener: ((Phase) -> Unit)? = null
    var phase = Phase.WAITING_FOR_PLAYERS
        set(value) {
            synchronized(field) {
                field = value
                phaseChangeListener?.let { change ->
                    change(value)
                }
            }
        }

    private fun setPhaseChangedListener(listener: (Phase) -> Unit){
        phaseChangeListener = listener
    }

    init {
        setPhaseChangedListener { newPhase ->
            when(newPhase){
                Phase.WAITING_FOR_PLAYERS -> waitingForPlayers()
                Phase.WAITING_FOR_START -> waitingForStart()
                Phase.NEW_ROUND -> newRound()
                Phase.GAME_RUNNING -> gameRunning()
                Phase.SHOW_WORD -> showWord()
            }
        }
    }

    suspend fun broadcast(message: String) {
        players.forEach { player ->
            if (player.socket.isActive) {
                player.socket.send(Frame.Text(message))
            }
        }
    }

    suspend fun broadcastToAllExcept(message: String, exceptionClientId: String) {
        players.forEach { player ->
            if (player.clientId != exceptionClientId && player.socket.isActive) {
                player.socket.send(Frame.Text(message))
            }
        }
    }

    fun containPlayer(username: String): Boolean {
        return players.find { it.userName == username } != null
    }

    private fun waitingForPlayers() {

    }

    private fun waitingForStart() {

    }

    private fun newRound() {

    }

    private fun gameRunning() {

    }

    private fun showWord() {

    }

    enum class Phase {
        WAITING_FOR_PLAYERS,
        WAITING_FOR_START,
        NEW_ROUND,
        GAME_RUNNING,
        SHOW_WORD
    }


}