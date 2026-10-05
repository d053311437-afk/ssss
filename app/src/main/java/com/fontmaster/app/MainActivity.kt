package com.fontmaster.app

import android.app.WallpaperManager
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.fontmaster.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding
    private val prefs by lazy { getSharedPreferences("fontmaster", MODE_PRIVATE) }
    private val fonts by lazy { listOf(
        "מערכת" to Typeface.DEFAULT,
        "מודרני" to Typeface.SANS_SERIF,
        "קלאסי" to Typeface.SERIF,
        "מכונת כתיבה" to Typeface.MONOSPACE,
        "מודרני עבה" to Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD),
        "מודרני נטוי" to Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC),
        "קלאסי עבה" to Typeface.create(Typeface.SERIF, Typeface.BOLD),
        "קלאסי נטוי" to Typeface.create(Typeface.SERIF, Typeface.ITALIC),
        "מונוספייס עבה" to Typeface.create(Typeface.MONOSPACE, Typeface.BOLD),
        "כותרת חזקה" to Typeface.create("sans-serif-black", Typeface.NORMAL),
        "דק ונקי" to Typeface.create("sans-serif-light", Typeface.NORMAL),
        "בינוני" to Typeface.create("sans-serif-medium", Typeface.NORMAL),
        "מעוגל" to Typeface.create("sans-serif-rounded", Typeface.NORMAL)
    )}

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(if (getSharedPreferences("fontmaster", MODE_PRIVATE).getBoolean("dark",false)) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        b=ActivityMainBinding.inflate(layoutInflater); setContentView(b.root)
        b.navFonts.setOnClickListener{show("fonts")}; b.navWallpapers.setOnClickListener{show("walls")}; b.navSettings.setOnClickListener{show("settings")}
        buildFonts(); buildWalls(); buildSettings(); show("fonts")
    }

    private fun show(s:String){
        b.fontsSection.visibility=if(s=="fonts")View.VISIBLE else View.GONE
        b.wallpapersSection.visibility=if(s=="walls")View.VISIBLE else View.GONE
        b.settingsSection.visibility=if(s=="settings")View.VISIBLE else View.GONE
        b.pageTitle.text=when(s){"fonts"->"ספריית גופנים";"walls"->"טפטים";else->"הגדרות"}
    }

    private fun buildFonts(){
        b.preview.textSize=prefs.getInt("size",26).toFloat()
        b.previewInput.setText("שלום! כך נראה הכתב שלי")
        b.previewInput.setOnKeyListener{_,_,_->b.preview.text=b.previewInput.text;false}
        fonts.forEachIndexed{index,item->
            val v=TextView(this).apply{
                text=item.first+"\nאבגדהוז ABC 123"; textSize=20f; typeface=item.second
                setPadding(22,18,22,18); background=ContextCompat.getDrawable(this@MainActivity,R.drawable.panel_background)
                setOnClickListener{selectFont(index)}
            }
            val lp=LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,0,0,12);v.layoutParams=lp;b.fontList.addView(v)
        }
        selectFont(prefs.getInt("font",0).coerceIn(fonts.indices))
        b.sizeSeek.max=42;b.sizeSeek.progress=prefs.getInt("size",26)-14
        b.sizeSeek.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(s:SeekBar?,p:Int,f:Boolean){val z=p+14;b.preview.textSize=z.toFloat();b.sizeValue.text="$z sp";prefs.edit().putInt("size",z).apply()}
            override fun onStartTrackingTouch(s:SeekBar?){};override fun onStopTrackingTouch(s:SeekBar?){}
        })
        b.openDisplaySettings.setOnClickListener{startActivity(Intent(Settings.ACTION_DISPLAY_SETTINGS))}
    }
    private fun selectFont(i:Int){b.preview.typeface=fonts[i].second;b.selectedFont.text="נבחר: "+fonts[i].first;prefs.edit().putInt("font",i).apply()}

    private fun buildWalls(){
        val names=listOf("לילה כחול","זריחה","ים רגוע","ניאון","יער","סגול עמוק","מנטה","שקיעה","אפור מודרני","שמיים","חול","טורקיז")
        names.forEachIndexed{i,n->
            val v=TextView(this).apply{
                text=n;gravity=Gravity.CENTER;textSize=18f;setTextColor(Color.WHITE);setPadding(12,52,12,52)
                background=android.graphics.drawable.GradientDrawable(android.graphics.drawable.GradientDrawable.Orientation.TL_BR,intArrayOf(color(i),color(i+4))).apply{cornerRadius=30f}
                setOnClickListener{applyWall(i,n)}
            }
            val lp=LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,0,0,14);v.layoutParams=lp;b.wallpaperGrid.addView(v)
        }
    }
    private fun color(i:Int):Int{val a=intArrayOf(0xff152238.toInt(),0xffd66d75.toInt(),0xff1976a3.toInt(),0xff5b2c83.toInt(),0xff235347.toInt(),0xff37205c.toInt(),0xff2a8c82.toInt(),0xffe07a5f.toInt(),0xff4b5563.toInt(),0xff4776a8.toInt(),0xffa77b4d.toInt(),0xff168aad.toInt());return a[Math.floorMod(i,a.size)]}
    private fun applyWall(i:Int,n:String){
        try{val bm=Bitmap.createBitmap(1080,1920,Bitmap.Config.ARGB_8888);val c=Canvas(bm);val p=Paint();p.shader=LinearGradient(0f,0f,1080f,1920f,color(i),color(i+4),Shader.TileMode.CLAMP);c.drawRect(0f,0f,1080f,1920f,p);WallpaperManager.getInstance(this).setBitmap(bm);Toast.makeText(this,"הטפט $n הוגדר",Toast.LENGTH_SHORT).show()}catch(e:Exception){Toast.makeText(this,"המכשיר לא אפשר להגדיר את הטפט",Toast.LENGTH_LONG).show()}
    }
    private fun buildSettings(){
        b.deviceInfo.text="Android "+android.os.Build.VERSION.RELEASE+" • "+android.os.Build.MANUFACTURER+" "+android.os.Build.MODEL
        b.darkSwitch.isChecked=prefs.getBoolean("dark",false)
        b.darkSwitch.setOnCheckedChangeListener{_,v->prefs.edit().putBoolean("dark",v).apply();AppCompatDelegate.setDefaultNightMode(if(v)AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)}
        b.appSettings.setOnClickListener{startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,android.net.Uri.parse("package:$packageName")))}
        b.reset.setOnClickListener{prefs.edit().clear().apply();recreate()}
    }
}