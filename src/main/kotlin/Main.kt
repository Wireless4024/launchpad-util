import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import launchpadutil.api.Human
import launchpadutil.api.Human.Companion.parse
import launchpadutil.config.HumanConfiguration
import launchpadutil.config.YamlFile
import launchpadutil.event.HoldEvent
import launchpadutil.event.KeyHoldEvent
import launchpadutil.event.OnceEvent
import launchpadutil.keyboard.KeyPress
import launchpadutil.keyboard.Keyboard
import launchpadutil.launchpad.mini.LaunchpadMini
import launchpadutil.launchpad.mini.MiniPad
import launchpadutil.mouse.LMB
import launchpadutil.mouse.MouseMove
import launchpadutil.robot.BetterRobot
import launchpadutil.robot.LowLatencyRobot
import launchpadutil.util.ActionList
import launchpadutil.util.LaunchpadDevice
import java.awt.Robot
import java.io.File
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.FloatControl
import javax.usb.UsbDevice
import javax.usb.UsbHostManager
import javax.usb.UsbHub
import kotlin.coroutines.coroutineContext
import kotlin.system.exitProcess
import kotlin.system.measureNanoTime


suspend fun move_sink(PID: Long, SINK: String) {
	val id = Util.exec(
		"pactl list sink-inputs | grep -e 'application.process.id = \"$PID\"' -e 'Sink Input'"
	)?.lines()?.takeLast(2)?.get(0)?.takeLastWhile { it.isDigit() }
	println("pacmd move-sink-input $id $SINK")
	Util.exec("pacmd move-sink-input $id $SINK")
}

fun findDevice(hub: UsbHub = (UsbHostManager.getUsbServices() as org.usb4java.javax.Services).rootUsbHub) {
	for (device in hub.attachedUsbDevices as List<UsbDevice>) {
		if (device.isUsbHub) {
			findDevice(device as UsbHub)
			//if (device != null) return device
		}
		for (i in 0..127)
			try {
				println(device.getString(i.toByte()))
			} catch (e: Throwable) {
				// e.printStackTrace()
			}
	}
	//return null
}

fun <T> test(value: T, t: Class<out T>) {

}

suspend fun main() {
	/* YamlFile("config/CC1.yml").let {
		 println(it["obj.key.value"])
		 // it["obj.key.value"] = Int.MAX_VALUE.toLong()
		 it.reload()
		 println(it.get("obj.key.value")?.javaClass)
 //        val list = it.getList<Any>("obj.key.value")!!
		 //list.add(10)
		 println(it)
		 it.save()274%678()-=}:">>?
	 }*/
	val dev = LaunchpadDevice.scanDevices()
	if (dev.isEmpty()) {
		throw RuntimeException("launchpad not found")
	}

	val lp = LaunchpadMini(dev.first())
	// println(Human.parse("{ctrl}{press {alt}{expr {f11}}}"))
	lp.loadDir("config")
	/*lp.register(MiniPad.CC4,HumanConfiguration.load(YamlFile("config/CC1.yml"))?.createEvent())
	lp.register(MiniPad.CC5, OnceEvent {
		parse("{clipboard}").accept(BetterRobot(false))
	})
	lp.register(MiniPad.A1, OnceEvent() {//world!hello hello
		parse("{ctrl}{alt}{left}").accept()
	})
	lp.register(MiniPad.A2, OnceEvent() {
		Human.parse("{ctrl}{alt}{right}").accept()
	})
	lp.register(MiniPad.A3, OnceEvent() {
		val robot = Robot()
		ActionList().apply {
			add(Keyboard.CTRL_DOWN)
			add(Keyboard.ALT_DOWN)
			add(KeyPress('t'))
			add(Keyboard.ALT_UP)
			add(Keyboard.CTRL_UP)
		}.accept()
		delay(500)//Hello Hello Hello Hello World!
		Human.parse("sudo systemctl status nginx\n").accept()
	})
	val hm = Human.parse("89")
	hm.delayRange = 2L..4L
	val br = BetterRobot()
	lp.register(MiniPad.A4, HoldEvent(loop = true) {
		hm.accept(br)
	})
	lp.register(MiniPad.E8, OnceEvent {
		val hm = ActionList(parse("{lmb}{delay 180}{esc}{delay 50}"), MouseMove(1920, 600), LMB)
		println(hm)
		hm.accept(br)
	})

	lp.register(MiniPad.H, OnceEvent {
		lp.close()
		// parse("{shift}{f10}").accept(Robot())
		exitProcess(0)
	})
	lp.register(MiniPad.B1, OnceEvent {
		parse("{rmb}{lmb}").accept()
	})
	lp.register(MiniPad.G, OnceEvent {
		parse("{ctrl}{alt}{shift}m").accept(BetterRobot())
	})
	lp.register(MiniPad.G1, KeyHoldEvent(true, LowLatencyRobot) {
		parse("{ctrl}{left}").accept()
		delay(150)
	})
	lp.register(MiniPad.G2, KeyHoldEvent(true, LowLatencyRobot) {
		parse("{ctrl}{right}").accept()
		delay(150)
	})
	lp.register(MiniPad.G3, OnceEvent {
		parse("{ctrl}{left}:{ctrl}{right}=").accept()
	})
	lp.register(MiniPad.G4, OnceEvent {
		val mod = parse("{shift}{expr ${"hello world".repeat(10)}}\n")
		val raw = parse("HELLO WORLD".repeat(10) + "\n")
		println(measureNanoTime {
			raw.setDelay().accept(BetterRobot(false))
		} / 1000000.0)
		println(measureNanoTime {
			mod.accept()
		} / 1000000.0)
	})
	lp.register(MiniPad.G5, OnceEvent {
		parse("{ctrl}{shift}/").accept()
	})
	lp.register(MiniPad.G7, OnceEvent {
		parse("{alt}{enter}{enter}").accept()
	})
	lp.register(MiniPad.F8, OnceEvent {
		parse("{rmb}{hd}{repeat 7}{down}{right}{repeat 5}{down}{right}{down}\n{delay 300}\n").accept(false)
	})
	lp.register(MiniPad.G8, OnceEvent {
		parse("{alt}{enter}{right}{up}{up}{enter}").accept(false)
	})
	lp.register(MiniPad.H1, OnceEvent {
		parse("const ").setDelay().accept()
	})
	lp.register(MiniPad.H2, OnceEvent {
		parse("let ").setDelay().accept()
	})
	lp.register(MiniPad.H3, OnceEvent {
		parse("div{tab}").setDelay().accept()
	})
	lp.register(MiniPad.H4, OnceEvent {
		parse("function ()\\{}{left}{left}{left}{left}").setDelay().accept()
	})
	lp.register(MiniPad.H5, OnceEvent {
		parse("async function ()\\{}{left}{left}{left}{left}").setDelay().accept()
	})
	lp.register(MiniPad.H8, HoldEvent(true) {
		parse(";{delay 500}{down}").accept()
	})
	lp.register(MiniPad.H7, OnceEvent {
		parse("{clipboard}").accept()
	})*/
	/*
	val aa = AudioSystem.getAudioInputStream(File("sound/vitas.wav"))
	var clip: Clip? = null
	suspend fun load_music(path: String, v: Float = 0f) {
		println(path)
		var c = clip
		if (c?.isRunning == true || c?.isActive == true || c?.isOpen == true) {
			c.stop()
			// c.close()
			c.framePosition = 0
		}
		c = c ?: AudioSystem.getClip()!!
		clip = c
		c.open(aa)
		val vol = c.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
		vol.value = v
		c.start()
		/* move_sink(PID, SINK)
		 println(
			 Util.exec(
				 "pactl list sink-inputs | grep -e 'application.process.id = \"${
					 PID
				 }\"' -e 'Sink Input'"
			 )?.lines()?.takeLast(2)
		 ) */
		while ((c.isRunning || c.isActive || c.isOpen)) {
			delay(10)
			coroutineContext.ensureActive()
		}
		c.stop()
		//  c.close()
		c.framePosition = 0
	}

	lp.register(MiniPad.C1, OnceEvent {
		Util.exec("micvol 100")
	})
	lp.register(MiniPad.C2, HoldEvent(false) {
		load_music("sound/vitas.wav")
	}.apply {
		released = {
			val c = clip
			c?.stop()
			//c?.close()
			c?.framePosition = 0
		}
	})
	*/
	/*lp.fillColor()
	delay(2000)
	*//*val fp = FontPainting()
    fp.drawString("hello")
    fp.paint(lp)
    for (i in 0..1)
        for (i in 0 until fp.width) {
            delay(100)
            fp.paint(lp, i)
        }*//**/
	/*val g = GifPainting()
	while (true)
		g.drawGif("images/frog.gif", lp)*/

	/*for (i in 0..128) {
		for (j in 0..79) {
			lp.setColor(MiniPad[j], object : PadColor {
				override val code: Int = (i + j) % 128
			})
			delay(1)
		}
	}*/
	delay(Long.MAX_VALUE)
	exitProcess(0)
	// pactl load-module module-null-sink sink_name=soundpad
	// pactl load-module module-loopback sink=soundpad source=alsa_input.usb-0c76_USB_PnP_Audio_Device-00.mono-fallback

	// pactl load-module module-null-sink sink_name=soundpad
	// pactl load-module module-loopback sink=alsa_output.pci-0000_00_1b.0.analog-stereo source=soundpad.monitor
	/*val SINK = Util.exec("pacmd list-sinks | grep -e 'name: <soundpad' -B1")?.lines()?.firstOrNull()
		?.takeLastWhile { it.isDigit() }
		?: run { System.err.println("sink unavailable");return }

	val PID = ProcessHandle.current().pid()
	println(
		Util.exec(
			"pactl list sink-inputs | grep -e 'application.process.id = \"${
				ProcessHandle.current().pid()
			}\"' -e 'Sink Input'"
		)?.lines()?.takeLast(2)
	)*/

/*

    val launchpad: Launchpad?
    launchpad = Launchpad(Listeners)
    Listeners.launchpad = launchpad

    val t = Thread.currentThread()
*/


	/* Thread {
		 val format = AudioFormat(16000.0f, 16, 1, true, true)
		 var microphone = AudioSystem.getTargetDataLine(format)
		 var speaker = AudioSystem.getSourceDataLine(format)
		// val info = DataLine.Info(TargetDataLine::class.java, format)
		// microphone = AudioSystem.getLine(info) as TargetDataLine
		 microphone.open(format)

		// val sinfo = DataLine.Info(SourceDataLine::class.java, format)
		 //speaker = AudioSystem.getLine(sinfo) as SourceDataLine
		 speaker.open(format)
		 var numBytesRead: Int
		 val CHUNK_SIZE = 1024
		 val data = ByteArray(microphone.bufferSize / 5)
		 microphone.start()


		 var off = 0
		 while (true) {
			 val len = microphone.read(data, 0, data.size)
			 speaker.write(data, 0, len)
		 }
	 }.start()*/

	/*Listeners.add {
		println(it)
	}

	val rb = Robot()
	var clip: Clip? = null

	rb.autoDelay = 0
	rb.delay(0)
	Listeners.add(Pad.H1, Listener {
		launchpad.set(it, Color.AMBER)
		*//* rb.press(KeyEvent.VK_Q)
         rb.press(KeyEvent.VK_Q)
         rb.press(KeyEvent.VK_W)
         rb.press(KeyEvent.VK_R)
         rb.press(KeyEvent.VK_D)*//*
        while (true) {
            rb.mouseMove(500, 1000)
            rb.mousePress(MouseEvent.BUTTON1_DOWN_MASK)
            rb.mouseMove(520, 1000)
            delay(10)
            rb.mouseRelease(MouseEvent.BUTTON1_DOWN_MASK)
        }
    })

    Listeners.add(Pad.H2, Listener {
        launchpad.set(it, Color.AMBER)
        rb.press(KeyEvent.VK_W)
        rb.press(KeyEvent.VK_W)
        rb.press(KeyEvent.VK_W)
        rb.press(KeyEvent.VK_R)
        rb.press(KeyEvent.VK_D)
        rb.mouse(MouseEvent.BUTTON1_DOWN_MASK)
    })

    Listeners.add(Pad.H3, Listener {
        launchpad.set(it, Color.AMBER)
        rb.press(KeyEvent.VK_E)
        rb.press(KeyEvent.VK_E)
        rb.press(KeyEvent.VK_W)
        rb.press(KeyEvent.VK_R)
        rb.press(KeyEvent.VK_D)
        rb.mouse(MouseEvent.BUTTON1_DOWN_MASK)
    })

    Listeners.add(Pad.H4, Listener {
        launchpad.set(it, Color.AMBER)
        rb.press(KeyEvent.VK_Q)
        rb.press(KeyEvent.VK_W)
        rb.press(KeyEvent.VK_E)
        rb.press(KeyEvent.VK_R)
        rb.press(KeyEvent.VK_D)
        rb.mouse(MouseEvent.BUTTON1_DOWN_MASK)
    })

    suspend fun load_music(path: String, v: Float = -25f) {
        var c = clip
        if (c?.isRunning == true || c?.isActive == true || c?.isOpen == true) {
            c.stop()
            c.close()
            c.framePosition = 0
        }
        c = c ?: AudioSystem.getClip()!!
        clip = c
        c.open(AudioSystem.getAudioInputStream(File(path)))
        val vol = c.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
        vol.value = v
        c.start()
        move_sink(PID, SINK)
        println(
            Util.exec(
                "pactl list sink-inputs | grep -e 'application.process.id = \"${
                    PID
                }\"' -e 'Sink Input'"
            )?.lines()?.takeLast(2)
        )
        c?.drain()
        while (c?.isRunning == true) delay(10)
    }

    Listeners.add(Pad.E1, Listener {
        load_music("sound/vitas.wav")
    })
    Listeners.add(Pad.E2, Listener {
        load_music("sound/rip.wav", -35f)
    })

    Listeners.add(Pad.E, Listener {
        val c = clip
        c?.stop()
    })
    Listeners.add(Pad.F1, Listener {
        rb.press("eeerc")
        rb.leftMouse()
        delay(750)
        rb.press("d")
        rb.leftMouse()
        rb.press("eewr")
        delay(200)
        rb.press("d")
        rb.leftMouse()
        delay(500)
        rb.press("qwerd")
        rb.leftMouse()
    })
    Listeners.add(Pad.F2, Listener {
        rb.press("eewrd")
        rb.leftMouse()
    })
    Listeners.add(Pad.F3, Listener {
        rb.press("wwwrd")
        rb.leftMouse()
    })
    Listeners.add(Pad.F4, Listener {
        rb.press("wwerdd")
    })
    Listeners.add(Pad.F5, Listener {
        rb.press("wwqrd")
        rb.leftMouse()
    })
    Listeners.add(Pad.F7, Listener {
        rb.leftMouse()
        rb.press("eeerd")
    })
    Listeners.add(Pad.G1, Listener {
        rb.press("1t")
        rb.leftMouse()
        delay(3000)
        rb.press("c1") // blink key
        rb.leftMouse()
        for (i in 0..3) {
            rb.press("e")
            rb.leftMouse()
            delay(1000) // march of machine time
            rb.press("12r")
            delay(1300) // rearm time
        }
        rb.press("e")
        rb.leftMouse()
        rb.press("tt")
        delay(3200)
        rb.press("12r")
        for (i in 0..1) {
            delay(2500)
            rb.press("1")
        }
    })
    Listeners.add(Pad.G2, Listener {
        rb.press("wq")
        rb.leftMouse()
        delay(1200)
        rb.press("12r")
    })

    Listeners.add(Pad.H7, Listener {
        rb.press(KeyEvent.VK_W)
        rb.press(KeyEvent.VK_W)
        rb.press(KeyEvent.VK_Q)
        rb.press(KeyEvent.VK_R)
        rb.press(KeyEvent.VK_D)
        rb.leftMouse()

        rb.press(KeyEvent.VK_W)
        rb.press(KeyEvent.VK_W)
        rb.press(KeyEvent.VK_W)
        delay(600)
        rb.press(KeyEvent.VK_R)
        rb.press(KeyEvent.VK_D)
        rb.leftMouse()

        rb.press(KeyEvent.VK_E)
        rb.press(KeyEvent.VK_E)
        rb.press(KeyEvent.VK_W)
        delay(400)
        rb.press(KeyEvent.VK_R)
        rb.press(KeyEvent.VK_D)
        rb.leftMouse()

        rb.press(KeyEvent.VK_Q)
        rb.press(KeyEvent.VK_W)
        rb.press(KeyEvent.VK_E)
        delay(400)
        rb.press(KeyEvent.VK_R)
        rb.press(KeyEvent.VK_D)
        rb.leftMouse()
    })
    Listeners.add(Pad.A8, Listener {
        (clip?.getControl(FloatControl.Type.MASTER_GAIN) as? FloatControl)?.value = 6f
    }, launchpad)
    Listeners.add(Pad.B8, Listener {
        (clip?.getControl(FloatControl.Type.MASTER_GAIN) as? FloatControl)?.value = 3f
    }, launchpad)
    Listeners.add(Pad.C8, Listener {
        (clip?.getControl(FloatControl.Type.MASTER_GAIN) as? FloatControl)?.value = 0f
    }, launchpad)
    Listeners.add(Pad.D8, Listener {
        (clip?.getControl(FloatControl.Type.MASTER_GAIN) as? FloatControl)?.value = -10f
    }, launchpad)
    Listeners.add(Pad.E8, Listener {
        (clip?.getControl(FloatControl.Type.MASTER_GAIN) as? FloatControl)?.value = -20f
    }, launchpad)
    Listeners.add(Pad.F8, Listener {
        (clip?.getControl(FloatControl.Type.MASTER_GAIN) as? FloatControl)?.value = -30f
    }, launchpad)
    Listeners.add(Pad.G8, Listener {
        (clip?.getControl(FloatControl.Type.MASTER_GAIN) as? FloatControl)?.value = -40f
    }, launchpad)

    Listeners.add(Pad.H8, Listener {
        (clip?.getControl(FloatControl.Type.MASTER_GAIN) as? FloatControl)?.value = -50f
    }, launchpad)


    Listeners.add(Pad.H, Listener { LockSupport.unpark(t) }, launchpad)
    *//*Listeners.add(Pad.G, Listener {
        *//**//* rb.keyPress(KeyEvent.SHIFT_DOWN_MASK)
         lkd()*//**//*
        rb.keyPress(KeyEvent.VK_F10 or KeyEvent.SHIFT_DOWN_MASK)
        lkd()
        rb.keyRelease(KeyEvent.VK_F10 or KeyEvent.SHIFT_DOWN_MASK)
        *//**//*lkd()
        rb.keyRelease(KeyEvent.SHIFT_DOWN_MASK)*//**//*
    }, launchpad)*//*

    LockSupport.park()
    launchpad.fill(Color.BLANK)
    delay(200)
    exitProcess(0)*/
}