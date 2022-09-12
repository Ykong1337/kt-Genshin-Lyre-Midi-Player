package com.ykong

import com.sun.javafx.application.PlatformImpl
import javafx.stage.FileChooser
import java.io.File
import java.lang.Thread.sleep
import java.util.*
import kotlin.system.exitProcess

fun main() {

    val playback = Playback()
    val sc = Scanner(System.`in`)
    val speed: Double
    val sleep: Long
    lateinit var file: File

    PlatformImpl.startup {
        val fileChooser = FileChooser()
        fileChooser.extensionFilters.addAll(FileChooser.ExtensionFilter("Midi File", "*.mid"))
        file = fileChooser.showOpenDialog(null)
    }

    while (true) {
        println("Input play speed x (1.0):")
        if (sc.hasNextDouble()) {
            speed = sc.nextDouble()
            break
        } else {
            println("You are not entering numbers!")
            sc.nextLine()
        }
    }

    while (true) {
        println("Input sleep time s (s):")
        if (sc.hasNextLong()) {
            sleep = sc.nextLong()
            break
        } else {
            println("You are not entering numbers!")
            sc.nextLine()
        }
    }

    for (i in sleep downTo 1) {
        println("Play will be start in $i seconds")
        sleep(1000)
    }

    try {
        playback.play(playback.init(file), speed)
    } finally {
        exitProcess(0)
    }
}