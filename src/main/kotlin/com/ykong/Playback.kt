package com.ykong

import com.alibaba.fastjson.JSON
import java.awt.Robot
import java.awt.event.KeyEvent
import java.io.File
import java.lang.Thread.sleep
import javax.sound.midi.MetaMessage
import javax.sound.midi.MidiEvent
import javax.sound.midi.MidiSystem
import javax.sound.midi.ShortMessage

class Playback {
    private val robot = Robot()
    private var key: HashMap<Int, Int> = HashMap()
    fun keyInit() {
        key[24] = KeyEvent.VK_Z
        key[26] = KeyEvent.VK_X
        key[28] = KeyEvent.VK_C
        key[29] = KeyEvent.VK_V
        key[31] = KeyEvent.VK_B
        key[33] = KeyEvent.VK_N
        key[35] = KeyEvent.VK_M
        key[36] = KeyEvent.VK_Z
        key[38] = KeyEvent.VK_X
        key[40] = KeyEvent.VK_C
        key[41] = KeyEvent.VK_V
        key[43] = KeyEvent.VK_B
        key[45] = KeyEvent.VK_N
        key[47] = KeyEvent.VK_M
        key[48] = KeyEvent.VK_Z
        key[50] = KeyEvent.VK_X
        key[52] = KeyEvent.VK_C
        key[53] = KeyEvent.VK_V
        key[55] = KeyEvent.VK_B
        key[57] = KeyEvent.VK_N
        key[59] = KeyEvent.VK_M
        key[60] = KeyEvent.VK_A
        key[62] = KeyEvent.VK_S
        key[64] = KeyEvent.VK_D
        key[65] = KeyEvent.VK_F
        key[67] = KeyEvent.VK_G
        key[69] = KeyEvent.VK_H
        key[71] = KeyEvent.VK_J
        key[72] = KeyEvent.VK_Q
        key[74] = KeyEvent.VK_W
        key[76] = KeyEvent.VK_E
        key[77] = KeyEvent.VK_R
        key[79] = KeyEvent.VK_T
        key[81] = KeyEvent.VK_Y
        key[83] = KeyEvent.VK_U
        key[84] = KeyEvent.VK_Q
        key[86] = KeyEvent.VK_W
        key[88] = KeyEvent.VK_E
        key[89] = KeyEvent.VK_R
        key[91] = KeyEvent.VK_T
        key[93] = KeyEvent.VK_Y
        key[95] = KeyEvent.VK_U
    }

    fun init(file: File): ArrayList<HashMap<String, Any>> {
        val sequence = MidiSystem.getSequence(file)
        val resolution = sequence.resolution
        val tracks = sequence.tracks
        val eventMessage: ArrayList<MidiEvent> = ArrayList()
        val message: ArrayList<HashMap<String, Any>> = ArrayList()
        var map: HashMap<String, Any>
        var tempo = 500000L
        var tick = 0L

        println("Start processing sequence!")
        for (t in tracks) {
            for (i in 0 until t.size()) {
                eventMessage.add(t.get(i))
            }
        }
        eventMessage.sortWith(Comparator.comparing(MidiEvent::getTick))

        for (midiEvent in eventMessage) {

            var time: Double
            map = JSON.parseObject(JSON.toJSONString(midiEvent.message)).toMap() as HashMap<String, Any>

            if ((midiEvent.message is MetaMessage) && (midiEvent.message as MetaMessage).type == 81) {
                val data = midiEvent.message.message
                tempo = (data[3].toInt().and(255).toLong()).shl(16)
                    .or((data[4].toInt().and(255).toLong()).shl(8).or(data[5].toInt().and(255).toLong()))
            } else if (midiEvent.message is ShortMessage) {
                time = (midiEvent.tick - tick) * (tempo / 1000.0 / resolution)
                tick = midiEvent.tick
                map["time"] = time
                message.add(map)
            }
        }
        return message
    }

    fun play(message: ArrayList<HashMap<String, Any>>, speed: Double) {
        println("Start playing!")
        val startTime = System.currentTimeMillis()
        var inputTime = 0.0

        for (msg in message) {

            inputTime += (msg["time"] as Double / speed)
            val playbackTime = System.currentTimeMillis() - startTime

            val currentTime = (inputTime - playbackTime).toLong()

            if (currentTime > 0) {
                sleep(currentTime)
            }

            if (msg["command"] as Int == 144 && key.containsKey(msg["data1"] as Int)) {
                robot.keyPress(key[msg["data1"] as Int] as Int)
                robot.keyRelease(key[msg["data1"] as Int] as Int)
            }
        }
    }
}