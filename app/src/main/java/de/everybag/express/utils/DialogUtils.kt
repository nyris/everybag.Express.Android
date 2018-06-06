package de.everybag.express.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.support.design.widget.Snackbar
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import de.everybag.express.R

/**
 * DialogUtils.kt - A helper class to create different UI dialogs
 *
 * @author Sidali Mellouk
 * Created by nyris GmbH
 * Copyright © 2018 nyris GmbH. All rights reserved.
 */
class DialogUtils(private val context: Activity) {
    private var messageBox: AlertDialog.Builder = AlertDialog.Builder(context, R.style.EverybagStyle_Dialog)
    private var ok: String? = null
    private var yes: String? = null
    private var no: String? = null

    private val red = "#ffe25d63"
    private val green = "#ff4caf50"
    private val blue = "#3ea0cd"
    private val orange = "#ffffc107"
    private val defaultColor = "#031F2B"
    private var exitDialog: AlertDialog? = null

    /**
     * KindMessageBox An enumeration that help to create an OK, YesNo, Finish, ... Dialog
     */
    enum class KindMessageBox {
        OK, YesNo, Finish, Exit, ForceExit, UpdateExit, Back
    }

    /**
     * KindSnack An enumeration that help to create an Info, Warning, Alert, Confirm snackview
     */
    enum class KindSnack {
        Info, Warning, Alert, Confirm, DefaultSnack
    }

    init {
        ok = context.getString(R.string.dialog_message_ok)
        yes = context.getString(R.string.dialog_message_yes)
        no = context.getString(R.string.dialog_message_no)
    }

    /**
     * Create Message Box Dialog
     * @param title A variable of type String
     * @param message A variable of type String
     * @param kind A variable of type KindMessageBox
     * @see KindMessageBox
     */
    fun messageBoxDialog(title: String, message: String, kind: KindMessageBox) {
        if (exitDialog != null && exitDialog!!.isShowing)
            exitDialog!!.dismiss()
        messageBox.setMessage(message)
        val drawableResourceId = context.resources.getIdentifier("ic_launcher", "mipmap", context.getPackageName())
        messageBox.setIcon(drawableResourceId)
        messageBox.setTitle(title)
        messageBox.setCancelable(false)
        when (kind) {
            DialogUtils.KindMessageBox.OK -> {
                messageBox.setPositiveButton(ok, null)
                messageBox.setNegativeButton(null, null)
            }
            DialogUtils.KindMessageBox.YesNo -> {
                messageBox.setNegativeButton(yes) { _, _ ->
                    val toast = Toast.makeText(context.applicationContext, yes, Toast.LENGTH_SHORT)
                    toast.show()
                }
                messageBox.setPositiveButton(no) { _, _ ->
                    val toast = Toast.makeText(context.applicationContext, no, Toast.LENGTH_LONG)
                    toast.show()
                }
            }
            DialogUtils.KindMessageBox.Finish -> messageBox.setPositiveButton(ok) { _, _ -> context.finish() }
            DialogUtils.KindMessageBox.Exit -> messageBox.setNegativeButton(ok) { _, _ ->
                context.finish()
                System.exit(0)
            }
            DialogUtils.KindMessageBox.Back -> messageBox.setNegativeButton(ok) { _, _ -> context.onBackPressed() }
            DialogUtils.KindMessageBox.ForceExit -> messageBox.setNegativeButton(ok) { _, _ ->
                context.finish()
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            }
            DialogUtils.KindMessageBox.UpdateExit -> messageBox.setNegativeButton(ok) { _, _ ->
                val uri = Uri.parse("market://details?id=" + context.packageName)
                val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                try {
                    context.startActivity(goToMarket)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }

                context.finish()
            }
        }
        messageBox.setOnCancelListener(null)
        exitDialog = messageBox.create()
        exitDialog!!.show()
    }

    /**
     * Create Message Box Dialog with OK mListener
     * @param title A variable of type String
     * @param message A variable of type String
     * @param listener A variable of type DialogInterface.OnClickListener
     */
    fun messageBoxDialog(title: String, message: String, listener: DialogInterface.OnClickListener) {
        if (exitDialog != null && exitDialog!!.isShowing)
            exitDialog!!.dismiss()
        messageBox.setMessage(message)
        val drawableResourceId = R.mipmap.ic_launcher
        messageBox.setIcon(drawableResourceId)
        messageBox.setTitle(title)
        messageBox.setCancelable(false)
        messageBox.setNegativeButton(ok, listener)
        messageBox.setNegativeButton(null, null)
        exitDialog = messageBox.create()
        exitDialog!!.show()
    }

    /**
     * Create A default Message Box Dialog
     * @param title A variable of type String
     * @param message A variable of type String
     */
    fun messageBoxDialog(title: String, message: String) {
        if (exitDialog != null && exitDialog!!.isShowing)
            exitDialog!!.dismiss()

        messageBox.setMessage(message)
        val drawableResourceId = R.mipmap.ic_launcher
        messageBox.setIcon(drawableResourceId)
        messageBox.setTitle(title)
        messageBox.setPositiveButton(ok, null)
        messageBox.setNegativeButton(null, null)
        messageBox.setOnCancelListener { }
        exitDialog = messageBox.create()
        exitDialog!!.show()
    }

    /**
     * Create toast message
     * @param message A variable of type String
     */
    fun toastMessage(message: String) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast.show()
    }

    /**
     * Create A default Message Box ExitYesNo Dialog
     */
    fun messageBoxDialogExitYesNo() {
        if (exitDialog != null && exitDialog!!.isShowing)
            return
        messageBox.setTitle(context.getString(R.string.dialog_message))
        messageBox.setMessage(context.getString(R.string.dialog_message_confirmation))
        val drawableResourceId = R.mipmap.ic_launcher
        messageBox.setIcon(drawableResourceId)
        messageBox.setNegativeButton(yes) { _, _ -> context.finish() }
        messageBox.setPositiveButton(no, null)
        exitDialog = messageBox.create()
        exitDialog!!.show()
    }

    /**
     * Show Snack view
     * @param parent A variable of type View
     * @param message A variable of type String
     * @param actionOneName A variable of type String
     * @param actionTwoName A variable of type String
     * @param actionOneListener A variable of type View.OnClickListener
     * @param actionTwoListener A variable of type View.OnClickListener
     * @param kindSnack A variable of type KindSnack
     * @param isShowOneAction A variable of type Boolean
     * @see KindSnack
     */
    @JvmOverloads
    fun showSnackBar(parent: View,
                     message: String,
                     actionOneName: String,
                     actionTwoName: String,
                     actionOneListener: ISnackListener?,
                     actionTwoListener: ISnackListener?,
                     kindSnack: KindSnack = KindSnack.DefaultSnack,
                     isShowOneAction: Boolean = false) {
        val objLayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val snackbar = Snackbar.make(parent, "", Snackbar.LENGTH_LONG)

        val layout = snackbar.view as Snackbar.SnackbarLayout
        layout.setPadding(0, 0, 0, 0)

        val textView = layout.findViewById<TextView>(android.support.design.R.id.snackbar_text)
        textView.visibility = View.INVISIBLE

        val snackView = context.layoutInflater.inflate(R.layout.my_snackbar, null)

        val btnActionOne = snackView.findViewById<Button>(R.id.btnActionOne)
        btnActionOne.text = actionOneName
        btnActionOne.setOnClickListener({
            actionOneListener?.onClick()
            snackbar.dismiss()
        })

        val btnActionTwo = snackView.findViewById<Button>(R.id.btnActionTwo)
        if (isShowOneAction)
            btnActionTwo.visibility = View.GONE
        else {
            btnActionTwo.text = actionTwoName
            btnActionTwo.setOnClickListener({
                actionTwoListener?.onClick()
                snackbar.dismiss()
            })
        }

        val tvSnackMessage = snackView.findViewById<TextView>(R.id.tvSnackMessage)
        tvSnackMessage.text = message

        layout.addView(snackView, objLayoutParams)
        when (kindSnack) {
            DialogUtils.KindSnack.Info -> info(snackbar).show()
            DialogUtils.KindSnack.Warning -> warning(snackbar).show()
            DialogUtils.KindSnack.Alert -> alert(snackbar).show()
            DialogUtils.KindSnack.Confirm -> confirm(snackbar).show()
            DialogUtils.KindSnack.DefaultSnack -> defaultSnack(snackbar).show()
        }
        snackbar.show()
    }

    fun showSnackBar(parent: View, message: String, actionName: String, actionListener: ISnackListener, kindSnack: KindSnack) {
        showSnackBar(parent, message, actionName, "", actionListener, null, kindSnack, true)
    }

    private fun getSnackBarLayout(snackbar: Snackbar): View {
        return snackbar.view.findViewById(R.id.rootSnack)
    }

    private fun colorSnackBar(snackbar: Snackbar, colorId: String): Snackbar {
        val snackBarView = getSnackBarLayout(snackbar)
        snackBarView.setBackgroundColor(Color.parseColor(colorId))
        return snackbar
    }

    private fun info(snackbar: Snackbar): Snackbar {
        return colorSnackBar(snackbar, blue)
    }

    private fun warning(snackbar: Snackbar): Snackbar {
        return colorSnackBar(snackbar, orange)
    }

    private fun alert(snackbar: Snackbar): Snackbar {
        return colorSnackBar(snackbar, red)
    }

    private fun confirm(snackbar: Snackbar): Snackbar {
        return colorSnackBar(snackbar, green)
    }

    private fun defaultSnack(snackbar: Snackbar): Snackbar {
        return colorSnackBar(snackbar, defaultColor)
    }

    interface ISnackListener {
        fun onClick()
    }
}
