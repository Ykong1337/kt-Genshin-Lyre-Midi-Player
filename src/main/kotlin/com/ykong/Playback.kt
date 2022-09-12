package com.ykong

import com.alibaba.fastjson.JSON
import java.awt.Robot
import java.io.File
import java.lang.Thread.sleep
import javax.sound.midi.MetaMessage
import javax.sound.midi.MidiEvent
import javax.sound.midi.MidiSystem
import javax.sound.midi.ShortMessage

class Playback {
    private val robot = Robot()
    private var key = c()

    fun init(file: File): ArrayList<HashMap<String, Any>> {
        val sequence = MidiSystem.getSequence(file)
        val resolution = sequence.resolution
        val tracks = sequence.tracks
        val eventMessage = ArrayList<MidiEvent>()
        val message = ArrayList<HashMap<String, Any>>()
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
            } else if ((midiEvent.message is ShortMessage) && ((midiEvent.message as ShortMessage).command == 144) && (key.containsKey(
                    (midiEvent.message as ShortMessage).data1
                ))
            ) {
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

            if (inputTime - playbackTime > 0) {
                sleep((inputTime - playbackTime).toLong())
            }

            robot.keyPress(key[msg["data1"] as Int] as Int)
            robot.keyRelease(key[msg["data1"] as Int] as Int)
        }
    }
}