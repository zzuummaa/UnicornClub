package ru.zuma.unicornclub

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.ChannelSftp.LsEntry
import com.jcraft.jsch.JSch
import com.jcraft.jsch.SftpException
import java.io.IOException


typealias OnImage = (Bitmap, Int) -> Unit
typealias OnNothingUpdate = () -> Unit

class SFTPManager(
    val user: String = "zuma", /* username for remote host */
    val password: String = "12345", /* password of the remote host */
    val host: String = "zzuummaa.sytes.net" /* remote host address */) {

    fun loadDailyImageIfUpdated(prevTimeOfCreation: Int, onImage: OnImage? = null, onNothingUpdate: OnNothingUpdate? = null) {
        launchPrintThrowable {
            try {

                val jsch = JSch()
                val session = jsch.getSession(user, host)
                session.setPassword(password)
                val config = java.util.Properties()
                config["StrictHostKeyChecking"] = "no"
                session.setConfig(config)
                session.connect()

                val sftpChannel = session.openChannel("sftp") as ChannelSftp
                sftpChannel.connect()

                val timeOfCreation = getCreationTime(sftpChannel, "/home/zuma/UnicornClub/daily.jpg")
                if (timeOfCreation > prevTimeOfCreation) {
                    val imageStream = sftpChannel.get("/home/zuma/UnicornClub/daily.jpg")
                    val bmp: Bitmap? = BitmapFactory.decodeStream(imageStream)
                    bmp?.let { onImage?.invoke(it, timeOfCreation) }
                } else {
                    onNothingUpdate?.invoke()
                }

                sftpChannel.disconnect()
                session.disconnect()

            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
    }
}

private fun getCreationTime(channel: ChannelSftp, fileName: String): Int {
    try {
        val vec = channel.ls(fileName)

        // Assumption only no duplicate file names on server
        if (vec != null && vec!!.size === 1) {
            val details = vec!!.get(0) as LsEntry
            val attrs = details.attrs

            return attrs.mTime
        } else {
            Log.w("SFTP", "can't get time of file creation")
        }
    } catch (e: SftpException) {
        e.printStackTrace()
    }

    return 0
}

