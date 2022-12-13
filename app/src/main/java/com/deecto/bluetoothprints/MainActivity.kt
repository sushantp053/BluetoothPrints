package com.deecto.bluetoothprints


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    val PERMISSION_BLUETOOTH = 1

    private val locale = Locale("id", "ID")
    private val df: DateFormat = SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", locale)
    private val nf: NumberFormat = NumberFormat.getCurrencyInstance(locale)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btn: Button = findViewById(R.id.button)
        btn.setOnClickListener {
            doPrint()
        }
    }

    fun doPrint() {
        try {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH),
                    PERMISSION_BLUETOOTH
                )
            } else {
                val connection = BluetoothPrintersConnections.selectFirstPaired()
                if (connection != null) {
                    val printer = EscPosPrinter(connection, 203, 48f, 32)
                    val text = "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(
                        printer,
                        this.applicationContext.resources.getDrawableForDensity(
                            R.drawable.ic_launcher_foreground,
                            DisplayMetrics.DENSITY_LOW, theme
                        )
                    ) + "</img>\n" +
                            "[L]\n" +
                            "[L]" + df.format(Date()) + "\n" +
                            "[C]================================\n" +
                            "[L]<b>मराठी प्रिंटर डेमो </b>\n" +
                            "[L]    1 pcs[R]" + nf.format(25000) + "\n" +
                            "[L]<b>सुशांत पाटील </b>\n" +
                            "[L]    1 pcs[R]" + nf.format(45000) + "\n" +
                            "[L]<b>7875648090</b>\n" +
                            "[L]    1 pcs[R]" + nf.format(20000) + "\n" +
                            "[C]--------------------------------\n"
                    printer.printFormattedText(text)
                } else {
                    Toast.makeText(this, "No printer was connected!", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e("APP", "Can't print", e)
        }
    }
}
