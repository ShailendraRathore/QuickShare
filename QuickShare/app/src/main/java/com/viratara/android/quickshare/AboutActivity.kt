package com.viratara.android.quickshare

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import com.vansuita.materialabout.builder.AboutBuilder
import com.vansuita.materialabout.views.AboutView
import android.content.Intent
import android.widget.FrameLayout


class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = AboutBuilder.with(this)
            .setAppIcon(R.mipmap.ic_launcher)
            .setAppName(R.string.app_name)
            .setPhoto(R.mipmap.profile_picture)
            .setCover(R.mipmap.profile_cover)
            .setLinksAnimated(true)
            .setDividerDashGap(13)
            .setName("Foji Singh")
            .setSubTitle("Mobile Developer")
            .setLinksColumnsCount(3)
            .setBrief("I'm warmed of mobile technologies. Ideas maker, curious and nature lover.")
            .addGooglePlayStoreLink("8002078663318221363")
            .addGitHubLink("jrvansuita")
            .addEmailLink("ssr29rathore@gmail.com")
            .addFiveStarsAction()
            .addMoreFromMeAction("Viratara Technologies")
            .setVersionNameAsAppSubTitle()
            .addShareAction(R.string.app_name)
            .addUpdateAction()
            .setActionsColumnsCount(2)
            .addFeedbackAction("ssr29rathore@gmail.com")
            .addIntroduceAction(null as Intent?)
            .setWrapScrollView(true)
            .setShowAsCard(true)
            .build()
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        addContentView(view, layoutParams)


    }
}
