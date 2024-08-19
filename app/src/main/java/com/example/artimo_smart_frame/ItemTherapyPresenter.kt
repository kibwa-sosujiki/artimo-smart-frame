package com.example.artimo_smart_frame

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.VideoView
import androidx.core.content.ContextCompat
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import java.io.File

class ItemTherapyPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val view = LayoutInflater.from(requireNotNull(parent).context).inflate(R.layout.item_view_therapy, parent, false)

        val params = view.layoutParams
        params.width = getWidthInPercent(parent.context, 10)
        params.height = getHeightInPercent(parent.context, 10)
        return ViewHolder(view)
    }

    fun getWidthInPercent(context: Context, percent: Int): Int {
        val width = context.resources.displayMetrics.widthPixels
        return (width * percent / 22)
    }

    fun getHeightInPercent(context: Context, percent: Int): Int {
        val height = context.resources.displayMetrics.heightPixels
        return (height* percent / 17)
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val content = item as? DataTherapyModel.Result ?: return

        val art = viewHolder.view.findViewById<VideoView>(R.id.art)
        val thumbnail = viewHolder.view.findViewById<ImageView>(R.id.thumbnail)
        val framebtn = viewHolder.view.findViewById<Button>(R.id.framebtn)

        content?.let {

            // 썸네일 이미지를 로드
            val thumbnailUrl = content.thumb
            Glide.with(viewHolder.view.context)
                .load(thumbnailUrl)
                .into(thumbnail)

            thumbnail.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    art.visibility = View.VISIBLE
                    framebtn.visibility = View.VISIBLE
                } else {
                    art.visibility = View.GONE
                    framebtn.visibility = View.GONE
                }
            }

            // 비디오 URL을 가져옴
            val context = viewHolder.view.context
            val file = File(context.filesDir, "${content.id}.mp4") // 내부 저장소에서 비디오 파일 찾기
            Log.d("ItemTherapyPresenter", "비디오 파일 : $file")
            if (file.exists()) {
                val videoUri = Uri.fromFile(file) // File 객체를 Uri로 변환
                art.setVideoURI(videoUri) // VideoView에 URI 설정

                // 비디오 준비 완료 시 자동 재생을 시작합니다.
                art.setOnPreparedListener { mediaPlayer ->
                    mediaPlayer.start()
                }
            } else {
                Log.w("ItemTherapyPresenter", "비디오 파일이 존재하지 않습니다: $file")
            }


            thumbnail.setOnClickListener {
            }

            framebtn.setOnClickListener {
                val activity = viewHolder.view.context as? Activity
                Log.d("ItemTherapyPresenter", "videoUrl: $file") // URL 로그 확인

                if (activity != null && file.exists()) {
                    val intent = Intent(activity, LegacyTherapyActivity::class.java).apply {
                        putExtra("file", file.toString())
                    }
                    activity.startActivity(intent)
                    activity.finish()
                } else {
                    if (activity == null) {
                        Log.e("ItemTherapyPresenter", "Activity is null")
                    }
                    if (!file.exists()) {
                        Log.e("ItemTherapyPresenter", "Video URL is null or empty")
                    }
                }
            }

            framebtn.setOnFocusChangeListener { v, hasFocus ->
                val button = v as Button
                if (hasFocus) {
                    // 포커스가 있을 때 framebtn의 배경 변경
                    button.setTextColor(Color.parseColor("#FFFF00"))
                } else {
                    // 포커스가 없을 때 기본 배경으로 변경
                    button.setTextColor(ContextCompat.getColor(button.context, R.color.brand_white)) // 원래 배경 색상으로 변경
                }
            }
        }

    }
    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
    }

}