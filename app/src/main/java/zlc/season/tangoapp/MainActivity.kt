package zlc.season.tangoapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import zlc.season.tango.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getScreenWidth().logd("Width")
        getScreenHeight().logd("Height")

        getRealScreenWidth().logd("RealWidth")
        getRealScreenHeight().logd("RealHeight")

        isShowNavigationBar().logd("Show navigation")
        getNavigationBarHeight().logd("Navigation  bar height")
    }
}
